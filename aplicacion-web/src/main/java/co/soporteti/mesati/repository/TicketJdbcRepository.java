package co.soporteti.mesati.repository;

import co.soporteti.mesati.model.Ticket;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TicketJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ticket> findAll() {
        return jdbcTemplate.query("""
                SELECT id, title, description, requester, category, priority, status, created_at, updated_at
                FROM tickets ORDER BY created_at DESC
                """, this::mapRow);
    }

    public Optional<Ticket> findById(Long id) {
        List<Ticket> tickets = jdbcTemplate.query("""
                SELECT id, title, description, requester, category, priority, status, created_at, updated_at
                FROM tickets WHERE id = ?
                """, this::mapRow, id);
        return tickets.stream().findFirst();
    }

    public Ticket insert(Ticket ticket) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO tickets (title, description, requester, category, priority, status, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, ticket.getTitle());
            statement.setString(2, ticket.getDescription());
            statement.setString(3, ticket.getRequester());
            statement.setString(4, ticket.getCategory());
            statement.setString(5, ticket.getPriority());
            statement.setString(6, ticket.getStatus());
            statement.setTimestamp(7, Timestamp.valueOf(now));
            statement.setTimestamp(8, Timestamp.valueOf(now));
            return statement;
        }, keyHolder);
        ticket.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);
        return ticket;
    }

    public boolean update(Long id, Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();
        int updated = jdbcTemplate.update("""
                UPDATE tickets SET title = ?, description = ?, requester = ?, category = ?, priority = ?, status = ?, updated_at = ?
                WHERE id = ?
                """, ticket.getTitle(), ticket.getDescription(), ticket.getRequester(), ticket.getCategory(),
                ticket.getPriority(), ticket.getStatus(), Timestamp.valueOf(now), id);
        ticket.setId(id);
        ticket.setUpdatedAt(now);
        return updated == 1;
    }

    public boolean delete(Long id) {
        return jdbcTemplate.update("DELETE FROM tickets WHERE id = ?", id) == 1;
    }

    private Ticket mapRow(java.sql.ResultSet resultSet, int rowNumber) throws java.sql.SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(resultSet.getLong("id"));
        ticket.setTitle(resultSet.getString("title"));
        ticket.setDescription(resultSet.getString("description"));
        ticket.setRequester(resultSet.getString("requester"));
        ticket.setCategory(resultSet.getString("category"));
        ticket.setPriority(resultSet.getString("priority"));
        ticket.setStatus(resultSet.getString("status"));
        ticket.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        ticket.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        return ticket;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
