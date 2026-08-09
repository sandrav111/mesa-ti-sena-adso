<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mesa TI | Tickets</title>
    <link rel="icon" href="/assets/favicon.svg" type="image/svg+xml">
    <link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar">
    <div class="brand"><span class="brand-mark">MT</span><span><strong>Mesa TI</strong><small>Soporte claro, siempre</small></span></div>
    <nav><a class="active" href="/tickets">Tickets</a><a href="/api/tickets">API</a><a href="/h2-console">Base de datos</a></nav>
    <button class="theme-button" id="theme-toggle" type="button">◐ <span>Modo oscuro</span></button>
</header>
<main class="page-shell">
    <section class="page-heading">
        <div><p class="eyebrow">MESA DE AYUDA · CRUD</p><h1>Tickets de soporte</h1><p>Registra y administra las solicitudes del equipo.</p></div>
        <a class="button primary" href="/tickets/nuevo">+ Nuevo ticket</a>
    </section>
    <c:if test="${not empty success}"><div class="alert success"><c:out value="${success}"/></div></c:if>
    <section class="panel">
        <div class="toolbar"><form class="search-form" action="/tickets" method="get"><label for="search">Buscar</label><input id="search" name="search" value="<c:out value="${search}"/>" placeholder="Título, persona o categoría"><button class="button secondary" type="submit">Filtrar</button></form><span class="ticket-count">${tickets.size()} resultado(s)</span></div>
        <div class="table-wrap"><table><thead><tr><th>ID</th><th>Solicitud</th><th>Solicitante</th><th>Categoría</th><th>Prioridad</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>
        <c:choose><c:when test="${empty tickets}"><tr><td class="empty" colspan="7">No hay tickets para mostrar.</td></tr></c:when><c:otherwise><c:forEach items="${tickets}" var="ticket"><tr><td>#<c:out value="${ticket.id}"/></td><td><strong><c:out value="${ticket.title}"/></strong><small><c:out value="${ticket.description}"/></small></td><td><c:out value="${ticket.requester}"/></td><td><c:out value="${ticket.category}"/></td><td><span class="priority priority-${ticket.priority.toLowerCase()}"><c:out value="${ticket.priority}"/></span></td><td><span class="status status-${ticket.status.toLowerCase().replace(' ', '-')}"><c:out value="${ticket.status}"/></span></td><td class="actions"><a href="/tickets/editar/${ticket.id}">Editar</a><form action="/tickets/eliminar/${ticket.id}" method="post" onsubmit="return confirm('¿Eliminar este ticket?')"><button type="submit">Eliminar</button></form></td></tr></c:forEach></c:otherwise></c:choose>
        </tbody></table></div>
    </section>
</main>
<script src="/js/app.js"></script>
</body>
</html>
