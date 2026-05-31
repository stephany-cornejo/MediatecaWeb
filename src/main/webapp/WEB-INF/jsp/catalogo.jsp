<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Catalogo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<% String role = (String) session.getAttribute("role"); %>
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Catalogo de documentos</h2>
        <div class="d-flex flex-wrap gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/prestamos/mios">Mis prestamos</a>
            <% if ("ADMIN".equalsIgnoreCase(role)) { %>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/documentos">Administrar documentos</a>
            <% } %>
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </div>
    <div class="app-card-body">
<% String success = (String) request.getAttribute("success"); if (success != null) { %>
    <div class="alert alert-success" role="alert"><%= success %></div>
<% } %>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
    <div class="alert alert-danger" role="alert"><%= error %></div>
<% } %>
<%
    List<DocumentoService.Documento> documentos = (List<DocumentoService.Documento>) request.getAttribute("documentos");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead>
    <tr>
        <th>ID</th><th>Titulo</th><th>Tipo</th><th>Disponibles</th><th>Total</th><th>Accion</th>
    </tr>
    </thead>
    <tbody>
    <% if (documentos != null) {
        for (DocumentoService.Documento d : documentos) { %>
    <tr>
        <td><%= d.id() %></td>
        <td><%= d.titulo() %></td>
        <td><%= d.tipo() %></td>
        <td><%= d.stockDisponible() %></td>
        <td><%= d.stockTotal() %></td>
        <td>
            <% if (d.stockDisponible() > 0) { %>
            <form method="post" action="${pageContext.request.contextPath}/prestamos/solicitar">
                <input type="hidden" name="documentoId" value="<%= d.id() %>" />
                <button class="btn btn-sm btn-app-primary" type="submit">Solicitar</button>
            </form>
            <% } else { %>
            <span class="badge text-bg-secondary">Sin stock</span>
            <% } %>
        </td>
    </tr>
    <%  }
      } %>
    </tbody>
</table>
</div>
    </div>
</div>
</div>
</body>
</html>

