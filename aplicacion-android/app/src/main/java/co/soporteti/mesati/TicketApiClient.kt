package co.soporteti.mesati

import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

const val DEFAULT_API_BASE_URL = "http://10.0.2.2:8081"

data class MobileTicket(
    val id: Long = 0,
    val title: String,
    val description: String,
    val requester: String,
    val category: String,
    val priority: String,
    val status: String
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("title", title)
        put("description", description)
        put("requester", requester)
        put("category", category)
        put("priority", priority)
        put("status", status)
    }

    companion object {
        fun fromJson(json: JSONObject): MobileTicket = MobileTicket(
            id = json.optLong("id"),
            title = json.optString("title"),
            description = json.optString("description"),
            requester = json.optString("requester"),
            category = json.optString("category"),
            priority = json.optString("priority"),
            status = json.optString("status")
        )
    }
}

class TicketApiClient(
    private val baseUrl: String = DEFAULT_API_BASE_URL
) {
    fun listTickets(): List<MobileTicket> {
        val response = request("GET", "/api/tickets")
        val json = JSONArray(response)
        return List(json.length()) { index -> MobileTicket.fromJson(json.getJSONObject(index)) }
    }

    fun createTicket(ticket: MobileTicket): MobileTicket =
        MobileTicket.fromJson(JSONObject(request("POST", "/api/tickets", ticket.toJson())))

    fun updateTicket(ticket: MobileTicket): MobileTicket =
        MobileTicket.fromJson(JSONObject(request("PUT", "/api/tickets/${ticket.id}", ticket.toJson())))

    fun deleteTicket(id: Long) {
        request("DELETE", "/api/tickets/$id")
    }

    private fun request(method: String, path: String, body: JSONObject? = null): String {
        val connection = (URL("$baseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Accept", "application/json")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
        }

        try {
            if (body != null) connection.outputStream.use { it.write(body.toString().toByteArray()) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (status !in 200..299) throw ApiException(status, response)
            return response
        } finally {
            connection.disconnect()
        }
    }
}

class ApiException(val statusCode: Int, body: String) :
    IllegalStateException("API request failed with HTTP $statusCode: $body")
