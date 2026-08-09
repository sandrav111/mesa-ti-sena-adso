package co.soporteti.mesati;

import co.soporteti.mesati.dto.AuthResponse;
import co.soporteti.mesati.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Test
    void shouldRegisterAndAuthenticateUser() {
        String username = "qa_" + UUID.randomUUID();

        AuthResponse registered = authService.register(username, "ClaveSegura123");
        AuthResponse login = authService.login(username, "ClaveSegura123");
        AuthResponse rejected = authService.login(username, "ClaveIncorrecta");

        assertTrue(registered.authenticated());
        assertTrue(login.authenticated());
        assertEquals(false, rejected.authenticated());
        assertThrows(IllegalArgumentException.class,
                () -> authService.register(username, "OtraClave123"));
    }
}
