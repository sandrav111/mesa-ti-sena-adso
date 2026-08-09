package co.soporteti.mesati.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordService {

    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private final SecureRandom random = new SecureRandom();

    public String hash(String password) {
        // Una sal aleatoria evita que dos contraseñas iguales produzcan el mismo hash.
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS);
        return ITERATIONS + "$" + encode(salt) + "$" + encode(derived);
    }

    public boolean matches(String password, String storedHash) {
        String[] parts = storedHash.split("\\$", -1);
        if (parts.length != 3) {
            return false;
        }
        int iterations;
        try {
            iterations = Integer.parseInt(parts[0]);
        } catch (NumberFormatException exception) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expected = Base64.getDecoder().decode(parts[2]);
        return MessageDigest.isEqual(expected, derive(password, salt, iterations));
    }

    private byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec specification = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(specification)
                    .getEncoded();
        } catch (Exception exception) {
            throw new IllegalStateException("No fue posible proteger la contraseña", exception);
        } finally {
            specification.clearPassword();
        }
    }

    private String encode(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }
}
