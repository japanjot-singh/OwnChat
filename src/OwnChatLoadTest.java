import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class OwnChatLoadTest {
    private static final int PORT = 4567;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_USERS = 10;
    private static final int DEFAULT_DURATION_SECONDS = 60;
    private static final int DEFAULT_MESSAGE_INTERVAL_MS = 1000;
    private static final String DEFAULT_PASSWORD = "LoadTestPassword123";
    private static final int DEFAULT_SETUP_TIMEOUT_MS = 10000;
    private static final int DEFAULT_CHAT_SOCKET_TIMEOUT_MS = 2000;
    private static final String DEFAULT_CHAT_MODE = "pair";
    private static final int DEFAULT_SWITCH_INTERVAL_MS = 10000;
    private static final int PROGRESS_LOG_INTERVAL_SECONDS = 5;

    public static void main(String[] args) {
        Config config;
        try {
            config = Config.fromArgs(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments: " + e.getMessage());
            printUsage();
            return;
        }

        if (!validateUserCount(config)) {
            return;
        }

        printConfig(config);

        String runId = "lt" + Long.toString(Instant.now().toEpochMilli(), 36)
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4);
        String[] usernames = new String[config.users];
        for (int i = 0; i < config.users; i++) {
            usernames[i] = buildUsername(runId, i);
        }

        Metrics metrics = new Metrics();

        System.out.println("\n[Setup] Starting sequential setup (create account -> login -> add contacts).");
        System.out.println("[Setup] Sequential setup is intentional to avoid overloading per-request DB connection creation on ServerL.");

        boolean setupOk = setupUsers(config, usernames, metrics);
        if (!setupOk) {
            System.err.println("\n[Setup] Setup failed. Not starting chat load phase.");
            printSummary(metrics, 0L);
            return;
        }

        String[][] targetsByUser = buildTargetsByUser(usernames, config.chatMode);
        ChatClient[] clients = new ChatClient[config.users];
        AtomicBoolean running = new AtomicBoolean(true);

        ExecutorService receiverPool = Executors.newFixedThreadPool(config.users);
        ScheduledExecutorService senderPool = Executors.newScheduledThreadPool(Math.max(2, Math.min(config.users, Runtime.getRuntime().availableProcessors() * 2)));
        ScheduledExecutorService switchPool = Executors.newScheduledThreadPool(Math.max(1, Math.min(config.users, 4)));
        ScheduledExecutorService progressPool = Executors.newSingleThreadScheduledExecutor();

        long startedAt = System.currentTimeMillis();
        long endAt = startedAt + (config.durationSeconds * 1000L);

        try {
            System.out.println("\n[Chat] Opening persistent chat sockets...");
            for (int i = 0; i < config.users; i++) {
                String username = usernames[i];
                try {
                    ChatClient client = new ChatClient(config, username, targetsByUser[i]);
                    client.connect();
                    clients[i] = client;
                } catch (Exception e) {
                    metrics.chatConnectionFailures.incrementAndGet();
                    System.err.println("[Chat] Connection failed for " + username + ": " + e.getMessage());
                }
            }

            int connectedClients = 0;
            for (ChatClient client : clients) {
                if (client != null) {
                    connectedClients++;
                    ChatClient current = client;
                    receiverPool.submit(() -> current.receiveLoop(running, metrics));
                }
            }

            if (connectedClients == 0) {
                System.err.println("[Chat] No clients connected successfully. Ending test.");
                return;
            }

            System.out.println("[Chat] Connected clients: " + connectedClients + "/" + config.users);
            System.out.println("[Chat] Starting sender tasks...");

            for (ChatClient client : clients) {
                if (client != null) {
                    senderPool.scheduleAtFixedRate(() -> {
                        if (!running.get()) {
                            return;
                        }
                        String payload = "msg from " + client.username + " at " + System.currentTimeMillis();
                        try {
                            client.sendMessage(payload);
                            metrics.messagesSent.incrementAndGet();
                        } catch (Exception e) {
                            metrics.sendErrors.incrementAndGet();
                            System.err.println("[SendError] " + client.username + " -> " + client.targetUser + ": " + e.getMessage());
                        }
                    }, 0, config.messageIntervalMs, TimeUnit.MILLISECONDS);
                }
            }

            if ("triad".equals(config.chatMode)) {
                System.out.println("[Chat] Starting target switch tasks (interval=" + config.switchIntervalMs + " ms)...");
                for (ChatClient client : clients) {
                    if (client != null && client.hasMultipleTargets()) {
                        switchPool.scheduleAtFixedRate(() -> {
                            if (!running.get()) {
                                return;
                            }
                            try {
                                client.switchToNextTarget();
                            } catch (Exception e) {
                                metrics.chatConnectionFailures.incrementAndGet();
                                System.err.println("[SwitchError] " + client.username + ": " + e.getMessage());
                            }
                        }, config.switchIntervalMs, config.switchIntervalMs, TimeUnit.MILLISECONDS);
                    }
                }
            }

            progressPool.scheduleAtFixedRate(() -> {
                long elapsedSec = (System.currentTimeMillis() - startedAt) / 1000;
                System.out.println("[Progress] elapsed=" + elapsedSec + "s"
                        + ", setupFailures=" + metrics.setupFailures.get()
                        + ", chatConnectionFailures=" + metrics.chatConnectionFailures.get()
                        + ", sent=" + metrics.messagesSent.get()
                        + ", received=" + metrics.messagesReceived.get()
                        + ", sendErrors=" + metrics.sendErrors.get()
                        + ", receiveErrors=" + metrics.receiveErrors.get());
            }, PROGRESS_LOG_INTERVAL_SECONDS, PROGRESS_LOG_INTERVAL_SECONDS, TimeUnit.SECONDS);

            while (System.currentTimeMillis() < endAt) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted. Stopping test early.");
        } finally {
            running.set(false);

            for (ChatClient client : clients) {
                if (client != null) {
                    client.close();
                }
            }

            senderPool.shutdownNow();
            switchPool.shutdownNow();
            progressPool.shutdownNow();
            receiverPool.shutdownNow();

            awaitTermination(senderPool, "senderPool");
            awaitTermination(switchPool, "switchPool");
            awaitTermination(progressPool, "progressPool");
            awaitTermination(receiverPool, "receiverPool");
        }

        long elapsedMs = System.currentTimeMillis() - startedAt;
        printSummary(metrics, elapsedMs);
    }

    private static boolean setupUsers(Config config, String[] usernames, Metrics metrics) {
        for (String username : usernames) {
            if (!createAccount(config, username, metrics)) {
                return false;
            }
        }

        for (String username : usernames) {
            if (!login(config, username, metrics)) {
                return false;
            }
        }

        for (String[] group : buildSetupGroups(usernames, config.chatMode)) {
            for (int i = 0; i < group.length; i++) {
                for (int j = 0; j < group.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    if (!addContact(config, group[i], group[j], metrics)) {
                        return false;
                    }
                }
            }
        }

        System.out.println("[Setup] Completed successfully for " + usernames.length + " users.");
        return true;
    }

    private static boolean createAccount(Config config, String username, Metrics metrics) {
        try {
            String response = sendRequestWithSingleResponse(config, "Create Account", username, config.password);
            if ("Saved".equals(response) || "Exists".equals(response)) {
                return true;
            }
            metrics.setupFailures.incrementAndGet();
            System.err.println("[Setup] Create Account failed for " + username + ". Expected Saved/Exists, got: " + response);
            return false;
        } catch (IOException e) {
            metrics.setupFailures.incrementAndGet();
            System.err.println("[Setup] Create Account IO failure for " + username + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean login(Config config, String username, Metrics metrics) {
        try {
            String response = sendRequestWithSingleResponse(config, "Log In", username, config.password);
            if ("found".equals(response)) {
                return true;
            }
            metrics.setupFailures.incrementAndGet();
            System.err.println("[Setup] Log In failed for " + username + ". Expected found, got: " + response);
            return false;
        } catch (IOException e) {
            metrics.setupFailures.incrementAndGet();
            System.err.println("[Setup] Log In IO failure for " + username + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean addContact(Config config, String owner, String contact, Metrics metrics) {
        try {
            String response = sendRequestWithSingleResponse(config, "Add Contacts", owner, contact);
            if ("Added".equals(response)) {
                return true;
            }
            metrics.setupFailures.incrementAndGet();
            System.err.println("[Setup] Add Contacts failed for owner=" + owner + ", contact=" + contact
                    + ". Expected Added, got: " + response);
            return false;
        } catch (IOException e) {
            metrics.setupFailures.incrementAndGet();
            System.err.println("[Setup] Add Contacts IO failure for owner=" + owner + ", contact=" + contact
                    + ": " + e.getMessage());
            return false;
        }
    }

    private static String sendRequestWithSingleResponse(Config config, String... lines) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(config.host, PORT), config.setupTimeoutMs);
            socket.setSoTimeout(config.setupTimeoutMs);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                for (String line : lines) {
                    writer.println(line);
                }
                return reader.readLine();
            }
        }
    }

    private static void printConfig(Config config) {
        System.out.println("Starting OwnChat load test");
        System.out.println("Host: " + config.host + ":" + PORT);
        if ("pair".equals(config.chatMode)) {
            System.out.println("Users: " + config.users + " (pairs=" + (config.users / 2) + ")");
        } else {
            System.out.println("Users: " + config.users + " (triads=" + (config.users / 3) + ")");
        }
        System.out.println("Chat mode: " + config.chatMode);
        System.out.println("Duration: " + config.durationSeconds + " seconds");
        System.out.println("Message interval: " + config.messageIntervalMs + " ms");
        if ("triad".equals(config.chatMode)) {
            System.out.println("Target switch interval: " + config.switchIntervalMs + " ms");
        }
        System.out.println("Setup timeout: " + config.setupTimeoutMs + " ms");
        System.out.println("Chat read timeout: " + config.chatSocketReadTimeoutMs + " ms");
    }

    private static void printSummary(Metrics metrics, long elapsedMs) {
        long elapsedSec = elapsedMs / 1000;
        System.out.println("\n=== OwnChat Load Test Summary ===");
        System.out.println("Elapsed: " + elapsedSec + " seconds");
        System.out.println("Setup failures: " + metrics.setupFailures.get());
        System.out.println("Chat connection failures: " + metrics.chatConnectionFailures.get());
        System.out.println("Messages sent: " + metrics.messagesSent.get());
        System.out.println("Messages received: " + metrics.messagesReceived.get());
        System.out.println("Send errors: " + metrics.sendErrors.get());
        System.out.println("Receive errors: " + metrics.receiveErrors.get());
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java OwnChatLoadTest [host] [users] [durationSeconds] [messageIntervalMs] [password] [setupTimeoutMs] [chatReadTimeoutMs] [chatMode] [switchIntervalMs]");
        System.out.println("Defaults:");
        System.out.println("  host=127.0.0.1 users=10 durationSeconds=60 messageIntervalMs=1000 ****** setupTimeoutMs=10000 chatReadTimeoutMs=2000 chatMode=pair switchIntervalMs=10000");
    }

    private static String buildUsername(String runId, int index) {
        String prefix = "u" + index + "_";
        int maxRunPart = 20 - prefix.length();
        if (maxRunPart < 1) {
            throw new IllegalArgumentException("User index is too large to fit username constraints");
        }
        String runPart = runId.length() <= maxRunPart ? runId : runId.substring(0, maxRunPart);
        return prefix + runPart;
    }

    private static void awaitTermination(ExecutorService executor, String name) {
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println(name + " did not terminate within timeout.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for " + name + " termination.");
        }
    }

    private static boolean validateUserCount(Config config) {
        if ("pair".equals(config.chatMode)) {
            if (config.users < 2) {
                System.err.println("For pair mode, users must be at least 2.");
                return false;
            }
            if (config.users % 2 != 0) {
                System.err.println("For pair mode, users must be even (e.g., 10, 20, 50). Received: " + config.users);
                return false;
            }
            return true;
        }

        if ("triad".equals(config.chatMode)) {
            if (config.users < 3) {
                System.err.println("For triad mode, users must be at least 3.");
                return false;
            }
            if (config.users % 3 != 0) {
                System.err.println("For triad mode, users must be a multiple of 3 (e.g., 3, 6, 9). Received: " + config.users);
                return false;
            }
            return true;
        }

        System.err.println("Unsupported chat mode: " + config.chatMode + ". Use 'pair' or 'triad'.");
        return false;
    }

    private static List<String[]> buildSetupGroups(String[] usernames, String chatMode) {
        List<String[]> groups = new ArrayList<>();
        if ("pair".equals(chatMode)) {
            for (int i = 0; i < usernames.length; i += 2) {
                groups.add(new String[]{usernames[i], usernames[i + 1]});
            }
            return groups;
        }

        for (int i = 0; i < usernames.length; i += 3) {
            groups.add(new String[]{usernames[i], usernames[i + 1], usernames[i + 2]});
        }
        return groups;
    }

    private static String[][] buildTargetsByUser(String[] usernames, String chatMode) {
        String[][] targets = new String[usernames.length][];
        if ("pair".equals(chatMode)) {
            for (int i = 0; i < usernames.length; i++) {
                targets[i] = new String[]{usernames[(i % 2 == 0) ? i + 1 : i - 1]};
            }
            return targets;
        }

        for (int i = 0; i < usernames.length; i += 3) {
            targets[i] = new String[]{usernames[i + 1], usernames[i + 2]};
            targets[i + 1] = new String[]{usernames[i + 2], usernames[i]};
            targets[i + 2] = new String[]{usernames[i], usernames[i + 1]};
        }
        return targets;
    }

    private static class Config {
        final String host;
        final int users;
        final int durationSeconds;
        final int messageIntervalMs;
        final String password;
        final int setupTimeoutMs;
        final int chatSocketReadTimeoutMs;
        final String chatMode;
        final int switchIntervalMs;

        Config(String host,
               int users,
               int durationSeconds,
               int messageIntervalMs,
               String password,
               int setupTimeoutMs,
               int chatSocketReadTimeoutMs,
               String chatMode,
               int switchIntervalMs) {
            this.host = host;
            this.users = users;
            this.durationSeconds = durationSeconds;
            this.messageIntervalMs = messageIntervalMs;
            this.password = password;
            this.setupTimeoutMs = setupTimeoutMs;
            this.chatSocketReadTimeoutMs = chatSocketReadTimeoutMs;
            this.chatMode = chatMode;
            this.switchIntervalMs = switchIntervalMs;
        }

        static Config fromArgs(String[] args) {
            String host = getArg(args, 0, DEFAULT_HOST);
            int users = parsePositiveInt(getArg(args, 1, String.valueOf(DEFAULT_USERS)), "users");
            int durationSeconds = parsePositiveInt(getArg(args, 2, String.valueOf(DEFAULT_DURATION_SECONDS)), "durationSeconds");
            int messageIntervalMs = parsePositiveInt(getArg(args, 3, String.valueOf(DEFAULT_MESSAGE_INTERVAL_MS)), "messageIntervalMs");
            String password = getArg(args, 4, DEFAULT_PASSWORD);
            int setupTimeoutMs = parsePositiveInt(getArg(args, 5, String.valueOf(DEFAULT_SETUP_TIMEOUT_MS)), "setupTimeoutMs");
            int chatReadTimeoutMs = parsePositiveInt(getArg(args, 6, String.valueOf(DEFAULT_CHAT_SOCKET_TIMEOUT_MS)), "chatReadTimeoutMs");
            String chatMode = normalizeChatMode(getArg(args, 7, DEFAULT_CHAT_MODE));
            int switchIntervalMs = parsePositiveInt(getArg(args, 8, String.valueOf(DEFAULT_SWITCH_INTERVAL_MS)), "switchIntervalMs");
            return new Config(host, users, durationSeconds, messageIntervalMs, password, setupTimeoutMs, chatReadTimeoutMs, chatMode, switchIntervalMs);
        }

        private static String getArg(String[] args, int index, String defaultValue) {
            if (index < args.length && args[index] != null && !args[index].trim().isEmpty()) {
                return args[index].trim();
            }
            return defaultValue;
        }

        private static int parsePositiveInt(String value, String name) {
            try {
                int parsed = Integer.parseInt(value);
                if (parsed <= 0) {
                    throw new IllegalArgumentException(name + " must be > 0");
                }
                return parsed;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(name + " must be a valid integer: " + value);
            }
        }

        private static String normalizeChatMode(String value) {
            return value.trim().toLowerCase();
        }
    }

    private static class Metrics {
        final AtomicLong setupFailures = new AtomicLong();
        final AtomicLong chatConnectionFailures = new AtomicLong();
        final AtomicLong messagesSent = new AtomicLong();
        final AtomicLong messagesReceived = new AtomicLong();
        final AtomicLong sendErrors = new AtomicLong();
        final AtomicLong receiveErrors = new AtomicLong();
    }

    private static class ChatClient {
        final String username;
        private volatile String targetUser;
        private final String[] targetUsers;
        private int currentTargetIndex;
        private final Config config;
        private volatile boolean switching;
        private final Object ioLock = new Object();

        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        ChatClient(Config config, String username, String[] targetUsers) {
            this.config = config;
            this.username = username;
            this.targetUsers = targetUsers;
            this.targetUser = targetUsers[0];
        }

        void connect() throws IOException {
            synchronized (ioLock) {
                connectToTarget(targetUser);
            }
        }

        boolean hasMultipleTargets() {
            return targetUsers.length > 1;
        }

        void switchToNextTarget() throws IOException {
            synchronized (ioLock) {
                switching = true;
                try {
                    currentTargetIndex = (currentTargetIndex + 1) % targetUsers.length;
                    String nextTarget = targetUsers[currentTargetIndex];
                    closeSocketInternal();
                    connectToTarget(nextTarget);
                    targetUser = nextTarget;
                } finally {
                    switching = false;
                }
            }
        }

        void sendMessage(String message) {
            synchronized (ioLock) {
                if (writer == null) {
                    throw new IllegalStateException("No active chat socket");
                }
                writer.println(message);
                if (writer.checkError()) {
                    throw new IllegalStateException("Socket write failed");
                }
            }
        }

        void receiveLoop(AtomicBoolean running, Metrics metrics) {
            try {
                while (running.get()) {
                    try {
                        BufferedReader currentReader = reader;
                        if (currentReader == null) {
                            Thread.sleep(50);
                            continue;
                        }
                        String line = currentReader.readLine();
                        if (line == null) {
                            if (running.get()) {
                                Thread.sleep(50);
                                continue;
                            }
                            break;
                        }
                        metrics.messagesReceived.incrementAndGet();
                    } catch (SocketTimeoutException timeout) {
                        // Keep looping while running to allow responsive shutdown.
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (IOException e) {
                if (running.get() && !switching) {
                    metrics.receiveErrors.incrementAndGet();
                    System.err.println("[ReceiveError] " + username + " <- " + targetUser + ": " + e.getMessage());
                }
            }
        }

        void close() {
            synchronized (ioLock) {
                closeSocketInternal();
            }
        }

        private void connectToTarget(String target) throws IOException {
            socket = new Socket();
            socket.connect(new InetSocketAddress(config.host, PORT), config.setupTimeoutMs);
            socket.setSoTimeout(config.chatSocketReadTimeoutMs);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            writer.println("chat_connect");
            writer.println(username);
            writer.println(target);
        }

        private void closeSocketInternal() {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
            socket = null;
            reader = null;
            writer = null;
        }
    }
}
