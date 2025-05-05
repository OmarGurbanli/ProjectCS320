package utils;

import model.User;

/**
 * Хранит залогиненного пользователя приложения.
 */
public class AppContext {
    private static User currentUser;

    /** Установить текущего (залогиненного) пользователя. */
    public static void setCurrentUser(User u) {
        currentUser = u;
    }

    /** Получить текущего пользователя. */
    public static User getCurrentUser() {
        return currentUser;
    }

    /** Удобный метод: возвращает роль (ADMIN, TRAINER или MEMBER). */
    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}
