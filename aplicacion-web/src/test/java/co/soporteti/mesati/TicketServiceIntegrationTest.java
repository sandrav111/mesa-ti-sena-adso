package co.soporteti.mesati;

import co.soporteti.mesati.model.Ticket;
import co.soporteti.mesati.exception.TicketNotFoundException;
import co.soporteti.mesati.service.TicketService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TicketServiceIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private Validator validator;

    @Test
    void shouldCreateUpdateSearchAndDeleteTicketThroughJdbc() {
        Ticket ticket = new Ticket(
                "VPN no conecta",
                "La conexión falla al iniciar sesión.",
                "Sandra Milena Vargas",
                "Red y conectividad",
                "Alta",
                "Nuevo");

        Ticket created = ticketService.create(ticket);

        assertNotNull(created.getId());
        assertEquals("Nuevo", created.getStatus());
        assertTrue(ticketService.search("VPN").stream().anyMatch(item -> item.getId().equals(created.getId())));

        created.setTitle("VPN conectado");
        created.setStatus("Resuelto");
        assertTrue(ticketService.update(created.getId(), created));
        assertEquals("Resuelto", ticketService.findById(created.getId()).getStatus());

        assertTrue(ticketService.delete(created.getId()));
    }

    @Test
    void shouldRejectInvalidTicketCatalogValues() {
        Ticket ticket = new Ticket(
                "Título válido",
                "Descripción válida para la prueba.",
                "Sandra Milena Vargas",
                "Categoría que no existe",
                "Urgente",
                "Cerrado");

        var violations = validator.validate(ticket);

        assertTrue(violations.stream().map(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("category")));
        assertTrue(violations.stream().map(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("priority")));
        assertTrue(violations.stream().map(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("status")));
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldReportMissingTicketOperations() {
        Long missingId = Long.MAX_VALUE;
        Ticket ticket = new Ticket(
                "Título válido",
                "Descripción válida para la prueba.",
                "Sandra Milena Vargas",
                "Aplicaciones",
                "Media",
                "Nuevo");

        assertThrows(TicketNotFoundException.class, () -> ticketService.findById(missingId));
        assertFalse(ticketService.update(missingId, ticket));
        assertFalse(ticketService.delete(missingId));
    }
}
