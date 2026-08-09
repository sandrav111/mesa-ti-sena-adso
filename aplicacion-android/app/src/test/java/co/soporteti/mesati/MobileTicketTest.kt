package co.soporteti.mesati

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MobileTicketTest {

    @Test
    fun ticketStoresTheRequiredFields() {
        val ticket = MobileTicket(
            id = 1050,
            title = "VPN no conecta",
            description = "El equipo no obtiene acceso a la red.",
            requester = "Sandra Milena Vargas",
            category = "Red y conectividad",
            priority = "Alta",
            status = "Nuevo"
        )

        assertEquals(1050L, ticket.id)
        assertEquals("VPN no conecta", ticket.title)
        assertEquals("Sandra Milena Vargas", ticket.requester)
        assertTrue(ticket.status == "Nuevo")
    }
}
