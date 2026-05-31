<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.PrestamoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mis Prestamos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Mis prestamos</h2>
        <div class="d-flex flex-wrap gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
            <% String role = (String) session.getAttribute("role"); if ("ADMIN".equalsIgnoreCase(role)) { %>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/prestamos">Administrar prestamos</a>
            <% } %>
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </div>
    <div class="app-card-body">
<%
    List<PrestamoService.Prestamo> prestamos = (List<PrestamoService.Prestamo>) request.getAttribute("prestamos");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead>
    <tr>
        <th>ID</th><th>Documento</th><th>Salida</th><th>Devolucion</th><th>Mora</th>
    </tr>
    </thead>
    <tbody>
    <% if (prestamos != null) {
        for (PrestamoService.Prestamo p : prestamos) { %>
    <tr>
        <td><%= p.id() %></td>
        <td><%= p.documentoTitulo() %></td>
        <td><%= p.fechaSalida() %></td>
        <td><%= p.fechaDevolucion() %></td>
        <td><%= p.mora() %></td>
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

