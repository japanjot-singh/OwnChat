public class clientSession {
    private static String username;
    private static boolean loggedIn;

    private clientSession() {
    }

    public static void login(String currentUsername) {
        username = currentUsername;
        loggedIn = true;
    }

    public static void logout() {
        username = null;
        loggedIn = false;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }
}
