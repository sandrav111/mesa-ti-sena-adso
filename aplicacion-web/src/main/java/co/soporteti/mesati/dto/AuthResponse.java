package co.soporteti.mesati.dto;

public record AuthResponse(boolean authenticated, String username, String message) {
}
