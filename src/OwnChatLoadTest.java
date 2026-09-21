import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;
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

        if (config.users < 2) {
            System.err.println("The number of users must be at least 2.");
            return;
        }
        if (config.users % 2 != 0) {
            System.err.println("The number of users must be even (e.g., 10, 20, 50). Received: " + config.users);
            return;
        }

        printConfig(config);

        String runId = "load_" + Instant.now().toEpochMilli() + "_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String[] usernames = new String[config.users];
        for (int i = 0; i < config.users; i++) {
            usernames[i] = runId + "_u" + i;
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

        ChatClient[] clients = new ChatClient[config.users];
        AtomicBoolean running = new AtomicBoolean(true);

        ExecutorService receiverPool = Executors.newFixedThreadPool(config.users);
        ScheduledExecutorService senderPool = Executors.newScheduledThreadPool(Math.max(2, Math.min(config.users, Runtime.getRuntime().availableProcessors() * 2)));
        ScheduledExecutorService progressPool = Executors.newSingleThreadScheduledExecutor();

        long startedAt = System.currentTimeMillis();
        long endAt = startedAt + (config.durationSeconds * 1000L);

        try {
            System.out.println("\n[Chat] Opening persistent chat sockets...");
            for (int i = 0; i < config.users; i++) {
                String username = usernames[i];
                String targetUser = usernames[(i % 2 == 0) ? i + 1 : i - 1];
                try {
                    ChatClient client = new ChatClient(config, username, targetUser);
                    client.connect();
                    clients[i] = client;
                } catch (Exception e) {
                    metrics.chatConnectionFailures.incrementAndGet();
                    System.err.println("[Chat] Connection failed for " + username + " -> " + targetUser + ": " + e.getMessage());
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
            progressPool.shutdownNow();
            receiverPool.shutdownNow();

            awaitTermination(senderPool, "senderPool");
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

        for (int i = 0; i < usernames.length; i += 2) {
            String userA = usernames[i];
            String userB = usernames[i + 1];
            if (!addContact(config, userA, userB, metrics)) {
                return false;
            }
            if (!addContact(config, userB, userA, metrics)) {
                return false;
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
        System.out.println("Users: " + config.users + " (pairs=" + (config.users / 2) + ")");
        System.out.println("Duration: " + config.durationSeconds + " seconds");
        System.out.println("Message interval: " + config.messageIntervalMs + " ms");
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
        System.out.println("  java OwnChatLoadTest [host] [users] [durationSeconds] [messageIntervalMs] [password] [setupTimeoutMs] [chatReadTimeoutMs]");
        System.out.println("Defaults:");
        System.out.println("  host=127.0.0.1 users=10 durationSeconds=60 messageIntervalMs=1000 ****** setupTimeoutMs=10000 chatReadTimeoutMs=2000");
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

    private static class Config {
        final String host;
        final int users;
        final int durationSeconds;
        final int messageIntervalMs;
        final String password;
        final int setupTimeoutMs;
        final int chatSocketReadTimeoutMs;

        Config(String host,
               int users,
               int durationSeconds,
               int messageIntervalMs,
               String password,
               int setupTimeoutMs,
               int chatSocketReadTimeoutMs) {
            this.host = host;
            this.users = users;
            this.durationSeconds = durationSeconds;
            this.messageIntervalMs = messageIntervalMs;
            this.password = password;
            this.setupTimeoutMs = setupTimeoutMs;
            this.chatSocketReadTimeoutMs = chatSocketReadTimeoutMs;
        }

        static Config fromArgs(String[] args) {
            String host = getArg(args, 0, DEFAULT_HOST);
            int users = parsePositiveInt(getArg(args, 1, String.valueOf(DEFAULT_USERS)), "users");
            int durationSeconds = parsePositiveInt(getArg(args, 2, String.valueOf(DEFAULT_DURATION_SECONDS)), "durationSeconds");
            int messageIntervalMs = parsePositiveInt(getArg(args, 3, String.valueOf(DEFAULT_MESSAGE_INTERVAL_MS)), "messageIntervalMs");
            String password = getArg(args, 4, DEFAULT_PASSWORD);
            int setupTimeoutMs = parsePositiveInt(getArg(args, 5, String.valueOf(DEFAULT_SETUP_TIMEOUT_MS)), "setupTimeoutMs");
            int chatReadTimeoutMs = parsePositiveInt(getArg(args, 6, String.valueOf(DEFAULT_CHAT_SOCKET_TIMEOUT_MS)), "chatReadTimeoutMs");
            return new Config(host, users, durationSeconds, messageIntervalMs, password, setupTimeoutMs, chatReadTimeoutMs);
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
        final String targetUser;
        private final Config config;

        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        ChatClient(Config config, String username, String targetUser) {
            this.config = config;
            this.username = username;
            this.targetUser = targetUser;
        }

        void connect() throws IOException {
            socket = new Socket();
            socket.connect(new InetSocketAddress(config.host, PORT), config.setupTimeoutMs);
            socket.setSoTimeout(config.chatSocketReadTimeoutMs);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            writer.println("chat_connect");
            writer.println(username);
            writer.println(targetUser);
        }

        synchronized void sendMessage(String message) {
            writer.println(message);
            if (writer.checkError()) {
                throw new IllegalStateException("Socket write failed");
            }
        }

        void receiveLoop(AtomicBoolean running, Metrics metrics) {
            try {
                while (running.get()) {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        metrics.messagesReceived.incrementAndGet();
                    } catch (SocketTimeoutException timeout) {
                        // Keep looping while running to allow responsive shutdown.
                    }
                }
            } catch (IOException e) {
                if (running.get()) {
                    metrics.receiveErrors.incrementAndGet();
                    System.err.println("[ReceiveError] " + username + " <- " + targetUser + ": " + e.getMessage());
                }
            }
        }

        void close() {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
