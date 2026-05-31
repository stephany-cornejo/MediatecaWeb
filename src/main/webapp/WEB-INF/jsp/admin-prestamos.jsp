<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.PrestamoService" %>
<%@ page import="com.mediateca.web.UsuarioService" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Prestamos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Administracion de prestamos</h2>
        <div class="d-flex flex-wrap gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/config">Configuracion</a>
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
    List<UsuarioService.Usuario> usuarios = (List<UsuarioService.Usuario>) request.getAttribute("usuarios");
    List<DocumentoService.Documento> documentos = (List<DocumentoService.Documento>) request.getAttribute("documentos");
%>

<h3>Registrar prestamo</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/prestamos" class="row g-2 align-items-end mb-3">
    <input type="hidden" name="action" value="registrar" />
    <div class="col-md-5">
    <label class="form-label">Usuario</label>
    <select class="form-select" name="usuarioId" required>
        <% if (usuarios != null) {
            for (UsuarioService.Usuario u : usuarios) { %>
        <option value="<%= u.id() %>"><%= u.id() %> - <%= u.nombre() %> (<%= u.rol() %>)</option>
        <%  }
          } %>
    </select>
    </div>
    <div class="col-md-5">
    <label class="form-label">Documento</label>
    <select class="form-select" name="documentoId" required>
        <% if (documentos != null) {
            for (DocumentoService.Documento d : documentos) { %>
        <option value="<%= d.id() %>"><%= d.id() %> - <%= d.titulo() %> [disp: <%= d.stockDisponible() %>]</option>
        <%  }
          } %>
    </select>
    </div>
    <div class="col-md-2 d-grid">
        <button class="btn btn-app-primary" type="submit">Registrar prestamo</button>
    </div>
</form>

<%
    List<PrestamoService.Prestamo> prestamos = (List<PrestamoService.Prestamo>) request.getAttribute("prestamos");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead>
    <tr>
        <th>ID</th><th>Usuario</th><th>Documento</th><th>Salida</th><th>Devolucion</th><th>Mora</th><th>Accion</th>
    </tr>
    </thead>
    <tbody>
    <% if (prestamos != null) {
        for (PrestamoService.Prestamo p : prestamos) { %>
    <tr>
        <td><%= p.id() %></td>
        <td><%= p.usuarioNombre() %> (id: <%= p.usuarioId() %>)</td>
        <td><%= p.documentoTitulo() %> (id: <%= p.documentoId() %>)</td>
        <td><%= p.fechaSalida() %></td>
        <td><%= p.fechaDevolucion() %></td>
        <td><%= p.mora() %></td>
        <td>
            <% if ("Pendiente".equalsIgnoreCase(p.fechaDevolucion())) { %>
            <form method="post" action="${pageContext.request.contextPath}/admin/prestamos">
                <input type="hidden" name="action" value="devolver" />
                <input type="hidden" name="prestamoId" value="<%= p.id() %>" />
                <button class="btn btn-sm btn-outline-primary" type="submit">Registrar devolucion</button>
            </form>
            <% } else { %>
            <span class="badge text-bg-success">Devuelto</span>
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

