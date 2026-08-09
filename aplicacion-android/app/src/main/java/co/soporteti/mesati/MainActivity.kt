package co.soporteti.mesati

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.soporteti.mesati.ui.theme.MesaTITheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MesaTITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TicketHomeScreen()
                }
            }
        }
    }
}

data class TicketUiState(
    val tickets: List<MobileTicket> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

class TicketController(
    private val apiClient: TicketApiClient = TicketApiClient()
) {
    var state by mutableStateOf(TicketUiState(isLoading = true))
        private set

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        load()
    }

    fun load() {
        update { copy(isLoading = true, error = null) }
        executor.execute {
            try {
                val tickets = apiClient.listTickets()
                update { TicketUiState(tickets = tickets) }
            } catch (error: Exception) {
                update { copy(isLoading = false, error = error.message ?: "No fue posible cargar los tickets.") }
            }
        }
    }

    fun save(ticket: MobileTicket) {
        update { copy(isSaving = true, error = null) }
        executor.execute {
            try {
                if (ticket.id == 0L) apiClient.createTicket(ticket) else apiClient.updateTicket(ticket)
                load()
            } catch (error: Exception) {
                update { copy(isSaving = false, error = error.message ?: "No fue posible guardar el ticket.") }
            }
        }
    }

    fun delete(id: Long) {
        update { copy(isSaving = true, error = null) }
        executor.execute {
            try {
                apiClient.deleteTicket(id)
                load()
            } catch (error: Exception) {
                update { copy(isSaving = false, error = error.message ?: "No fue posible eliminar el ticket.") }
            }
        }
    }

    fun close() {
        executor.shutdownNow()
    }

    private fun update(change: TicketUiState.() -> TicketUiState) {
        mainHandler.post { state = state.change() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketHomeScreen() {
    val controller = remember { TicketController() }
    var search by remember { mutableStateOf(TextFieldValue()) }
    var editingTicket by remember { mutableStateOf<MobileTicket?>(null) }
    var showNewTicket by remember { mutableStateOf(false) }
    var deletingTicket by remember { mutableStateOf<MobileTicket?>(null) }
    val uiState = controller.state

    DisposableEffect(controller) {
        onDispose { controller.close() }
    }

    val visibleTickets = uiState.tickets.filter {
        it.title.contains(search.text, ignoreCase = true) ||
            it.category.contains(search.text, ignoreCase = true) ||
            it.requester.contains(search.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mesa TI", fontWeight = FontWeight.Bold)
                        Text("Tickets sincronizados", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { controller.load() }, enabled = !uiState.isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar tickets")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (!uiState.isSaving) showNewTicket = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear ticket")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7FB))
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Resumen", fontSize = 13.sp, color = Color(0xFF6757DF), fontWeight = FontWeight.Bold)
                Text("Atiende primero lo importante.", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                SummaryCards(uiState.tickets)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Buscar tickets") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                Spacer(Modifier.height(8.dp))
                Text("Tickets recientes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            if (uiState.isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Cargando tickets desde la API...")
                    }
                }
            }
            uiState.error?.let { message ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8E5))) {
                        Column(Modifier.padding(16.dp)) {
                            Text("No fue posible completar la operación.", fontWeight = FontWeight.Bold)
                            Text(message, modifier = Modifier.padding(top = 4.dp))
                            TextButton(onClick = { controller.load() }) { Text("Reintentar") }
                        }
                    }
                }
            }
            if (!uiState.isLoading && uiState.error == null && visibleTickets.isEmpty()) {
                item { Text("No hay tickets para mostrar.", modifier = Modifier.padding(vertical = 24.dp)) }
            }
            items(visibleTickets, key = { it.id }) { ticket ->
                TicketCard(
                    ticket = ticket,
                    onEdit = { editingTicket = ticket },
                    onDelete = { deletingTicket = ticket }
                )
            }
        }
    }

    if (showNewTicket || editingTicket != null) {
        NewTicketDialog(
            ticket = editingTicket,
            onDismiss = {
                showNewTicket = false
                editingTicket = null
            },
            onSave = { ticket ->
                controller.save(ticket)
                showNewTicket = false
                editingTicket = null
            }
        )
    }

    deletingTicket?.let { ticket ->
        AlertDialog(
            onDismissRequest = { deletingTicket = null },
            title = { Text("Eliminar ticket") },
            text = { Text("¿Deseas eliminar \"${ticket.title}\"? Esta acción se enviará a la API.") },
            confirmButton = {
                TextButton(onClick = {
                    controller.delete(ticket.id)
                    deletingTicket = null
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { deletingTicket = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun SummaryCards(tickets: List<MobileTicket>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        SummaryCard("Total", tickets.size.toString(), Modifier.weight(1f))
        SummaryCard("Nuevos", tickets.count { it.status == "Nuevo" }.toString(), Modifier.weight(1f))
        SummaryCard("Resueltos", tickets.count { it.status == "Resuelto" }.toString(), Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = Color(0xFF858999), fontSize = 11.sp)
            Text(value, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TicketCard(ticket: MobileTicket, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("#${ticket.id}", color = Color(0xFF6757DF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(Modifier.width(8.dp))
                Text(ticket.title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            }
            Text(ticket.description, color = Color(0xFF656978), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(top = 8.dp)) {
                AssistChip(onClick = {}, label = { Text(ticket.category, fontSize = 10.sp) })
                AssistChip(onClick = {}, label = { Text(ticket.status, fontSize = 10.sp) })
                Text(ticket.priority, color = if (ticket.priority == "Alta") Color(0xFFD96F54) else Color(0xFF858999), fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}

@Composable
fun NewTicketDialog(ticket: MobileTicket?, onDismiss: () -> Unit, onSave: (MobileTicket) -> Unit) {
    var title by remember { mutableStateOf(ticket?.title.orEmpty()) }
    var description by remember { mutableStateOf(ticket?.description.orEmpty()) }
    var requester by remember { mutableStateOf(ticket?.requester ?: "Sandra Milena Vargas") }
    var category by remember { mutableStateOf(ticket?.category ?: "Aplicaciones") }
    var priority by remember { mutableStateOf(ticket?.priority ?: "Media") }
    var status by remember { mutableStateOf(ticket?.status ?: "Nuevo") }
    val canSave = title.isNotBlank() && description.isNotBlank() && requester.isNotBlank() && category.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (ticket == null) "Nuevo ticket" else "Editar ticket") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TicketInput("Título", title) { title = it }
                TicketInput("Descripción", description) { description = it }
                TicketInput("Solicitante", requester) { requester = it }
                TicketInput("Categoría", category) { category = it }
                TicketInput("Prioridad (Baja, Media o Alta)", priority) { priority = it }
                TicketInput("Estado (Nuevo, En curso o Resuelto)", status) { status = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(MobileTicket(ticket?.id ?: 0, title, description, requester, category, priority, status))
                },
                enabled = canSave
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun TicketInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = label != "Descripción"
    )
}
