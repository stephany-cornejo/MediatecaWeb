<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.PrestamoService" %>
<%@ page import="com.mediateca.web.UsuarioService" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Prestamos</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 30px auto; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 8px; vertical-align: top; }
        select, input { padding: 6px; }
        .ok { color: green; }
        .err { color: #b30000; }
        .menu a { margin-right: 12px; }
        .line { margin: 8px 0; }
    </style>
</head>
<body>
<h2>Administracion de prestamos</h2>
<p class="menu">
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
    <a href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
    <a href="${pageContext.request.contextPath}/admin/config">Configuracion</a>
    <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
</p>

<% String success = (String) request.getAttribute("success"); if (success != null) { %>
<div class="ok"><%= success %></div>
<% } %>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
<div class="err"><%= error %></div>
<% } %>

<%
    List<UsuarioService.Usuario> usuarios = (List<UsuarioService.Usuario>) request.getAttribute("usuarios");
    List<DocumentoService.Documento> documentos = (List<DocumentoService.Documento>) request.getAttribute("documentos");
%>

<h3>Registrar prestamo</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/prestamos" class="line">
    <input type="hidden" name="action" value="registrar" />
    <select name="usuarioId" required>
        <% if (usuarios != null) {
            for (UsuarioService.Usuario u : usuarios) { %>
        <option value="<%= u.id() %>"><%= u.id() %> - <%= u.nombre() %> (<%= u.rol() %>)</option>
        <%  }
          } %>
    </select>
    <select name="documentoId" required>
        <% if (documentos != null) {
            for (DocumentoService.Documento d : documentos) { %>
        <option value="<%= d.id() %>"><%= d.id() %> - <%= d.titulo() %> [disp: <%= d.stockDisponible() %>]</option>
        <%  }
          } %>
    </select>
    <button type="submit">Registrar prestamo</button>
</form>

<%
    List<PrestamoService.Prestamo> prestamos = (List<PrestamoService.Prestamo>) request.getAttribute("prestamos");
%>
<table>
    <tr>
        <th>ID</th><th>Usuario</th><th>Documento</th><th>Salida</th><th>Devolucion</th><th>Mora</th><th>Accion</th>
    </tr>
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
                <button type="submit">Registrar devolucion</button>
            </form>
            <% } else { %>
            Devuelto
            <% } %>
        </td>
    </tr>
    <%  }
      } %>
</table>
</body>
</html>

