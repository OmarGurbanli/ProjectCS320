package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    /** Создаёт новый BCrypt-хеш пароля */
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    /** Проверяет пароль против хеша. */
    public static boolean verify(String plain, String hash) {
        if (hash == null) {
            return false;
        }
        // Попробуем BCrypt - если это валидный salt-хеш
        if (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plain, hash);
            } catch (IllegalArgumentException e) {
                // Если вдруг salt неверный версии, падает здесь
                // Логируем, но не обрываем приложение
                System.err.println("BCrypt error: " + e.getMessage());
            }
        }
        // Фоллбек: простое сравнение (на случай прежних plaintext-паролей)
        return plain.equals(hash);
    }
}
