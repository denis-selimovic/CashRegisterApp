package ba.unsa.etf.si.utility.db;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class HashUtils {

    private HashUtils() {
    }

    public static String generateSHA256(String password) {
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_16).toString();
    }

    public static boolean comparePasswords(String hash, String password) {
        return hash.equals(generateSHA256(password));
    }
}
