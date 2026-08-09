package co.soporteti.mesati.service;

import co.soporteti.mesati.model.Ticket;
import co.soporteti.mesati.exception.TicketNotFoundException;
import co.soporteti.mesati.repository.TicketJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketJdbcRepository repository;

    public TicketService(TicketJdbcRepository repository) {
        this.repository = repository;
    }

    public List<Ticket> findAll() {
        return repository.findAll();
    }

    public List<Ticket> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String normalized = query.trim().toLowerCase();
        return findAll().stream()
                .filter(ticket -> contains(ticket.getTitle(), normalized)
                        || contains(ticket.getRequester(), normalized)
                        || contains(ticket.getCategory(), normalized)
                        || String.valueOf(ticket.getId()).contains(normalized))
                .toList();
    }

    public Ticket findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    public Ticket create(Ticket ticket) {
        ticket.setPriority(defaultValue(ticket.getPriority(), "Media"));
        ticket.setStatus(defaultValue(ticket.getStatus(), "Nuevo"));
        return repository.insert(ticket);
    }

    public boolean update(Long id, Ticket ticket) {
        ticket.setPriority(defaultValue(ticket.getPriority(), "Media"));
        ticket.setStatus(defaultValue(ticket.getStatus(), "En curso"));
        return repository.update(id, ticket);
    }

    public boolean delete(Long id) {
        return repository.delete(id);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
