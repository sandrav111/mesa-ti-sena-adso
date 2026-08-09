<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Mesa TI | ${pageTitle}</title><link rel="icon" href="/assets/favicon.svg" type="image/svg+xml"><link rel="stylesheet" href="/css/app.css">
</head>
<body>
<header class="topbar"><a class="brand brand-link" href="/tickets"><span class="brand-mark">MT</span><span><strong>Mesa TI</strong><small>Soporte claro, siempre</small></span></a><nav><a href="/tickets">Tickets</a><a href="/api/tickets">API</a></nav><button class="theme-button" id="theme-toggle" type="button">◐ <span>Modo oscuro</span></button></header>
<main class="page-shell narrow"><a class="back-link" href="/tickets">← Volver a tickets</a><section class="form-card"><p class="eyebrow">GESTIÓN DE SOLICITUD</p><h1>${pageTitle}</h1><p class="intro">Completa los datos para que el equipo pueda atender la solicitud.</p>
    <form:form method="post" action="${action}" modelAttribute="ticket" cssClass="ticket-form">
        <div class="form-grid"><label>Título<form:input path="title" placeholder="Ej. No puedo ingresar al correo"/><form:errors path="title" cssClass="field-error"/></label><label>Solicitante<form:input path="requester" placeholder="Nombre de la persona"/><form:errors path="requester" cssClass="field-error"/></label><label>Categoría<form:select path="category"><form:option value="" label="Selecciona una categoría"/><form:option value="Acceso y cuentas"/><form:option value="Equipos"/><form:option value="Red y conectividad"/><form:option value="Aplicaciones"/></form:select><form:errors path="category" cssClass="field-error"/></label><label>Prioridad<form:select path="priority"><form:option value="Baja"/><form:option value="Media"/><form:option value="Alta"/></form:select></label><label>Estado<form:select path="status"><form:option value="Nuevo"/><form:option value="En curso"/><form:option value="Resuelto"/></form:select></label></div>
        <label>Descripción<form:textarea path="description" rows="6" placeholder="Describe el problema, el contexto y el impacto."/><form:errors path="description" cssClass="field-error"/></label>
        <div class="form-actions"><a class="button secondary" href="/tickets">Cancelar</a><button class="button primary" type="submit">Guardar ticket</button></div>
    </form:form>
</section></main><script src="/js/app.js"></script></body></html>
