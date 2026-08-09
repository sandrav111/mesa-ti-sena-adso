package co.soporteti.mesati.repository;

import co.soporteti.mesati.model.UserAccount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        return count != null && count > 0;
    }

    public UserAccount save(String username, String passwordHash) {
        jdbc.update("INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                username, passwordHash);
        return findByUsername(username).orElseThrow();
    }

    public Optional<UserAccount> findByUsername(String username) {
        List<UserAccount> users = jdbc.query(
                "SELECT id, username, password_hash, created_at FROM users WHERE username = ?",
                (rs, rowNum) -> new UserAccount(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getTimestamp("created_at").toLocalDateTime()),
                username);
        return users.stream().findFirst();
    }
}
