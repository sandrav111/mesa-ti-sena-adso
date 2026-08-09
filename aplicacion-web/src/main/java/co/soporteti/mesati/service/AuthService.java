package co.soporteti.mesati.service;

import co.soporteti.mesati.dto.AuthResponse;
import co.soporteti.mesati.model.UserAccount;
import co.soporteti.mesati.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordService passwords;

    public AuthService(UserRepository users, PasswordService passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public AuthResponse register(String username, String password) {
        // Solo se persiste el hash; la contraseña original nunca llega al repositorio.
        if (users.existsByUsername(username)) {
            throw new IllegalArgumentException("El usuario ya está registrado");
        }
        UserAccount account = users.save(username, passwords.hash(password));
        return new AuthResponse(true, account.username(), "Usuario registrado correctamente");
    }

    public AuthResponse login(String username, String password) {
        // La coincidencia se comprueba contra el hash almacenado y no contra texto plano.
        return users.findByUsername(username)
                .filter(account -> passwords.matches(password, account.passwordHash()))
                .map(account -> new AuthResponse(true, account.username(), "Autenticación correcta"))
                .orElseGet(() -> new AuthResponse(false, username, "Usuario o contraseña incorrectos"));
    }
}
