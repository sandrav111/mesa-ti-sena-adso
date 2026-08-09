package co.soporteti.mesati.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 80, message = "El usuario no puede superar 80 caracteres")
        String username,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 120, message = "La contraseña debe tener entre 8 y 120 caracteres")
        String password) {
}
