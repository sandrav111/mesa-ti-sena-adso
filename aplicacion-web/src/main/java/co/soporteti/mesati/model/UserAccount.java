package co.soporteti.mesati.model;

import java.time.LocalDateTime;

public record UserAccount(Long id, String username, String passwordHash, LocalDateTime createdAt) {
}
