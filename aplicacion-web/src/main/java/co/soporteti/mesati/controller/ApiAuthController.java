package co.soporteti.mesati.controller;

import co.soporteti.mesati.dto.AuthRequest;
import co.soporteti.mesati.dto.AuthResponse;
import co.soporteti.mesati.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService service;

    public ApiAuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        // El registro devuelve 409 sin revelar datos internos si el usuario ya existe.
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.register(request.username(), request.password()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(false, request.username(), exception.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // La respuesta uniforme evita indicar si falló el usuario o la contraseña.
        AuthResponse response = service.login(request.username(), request.password());
        return response.authenticated()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
