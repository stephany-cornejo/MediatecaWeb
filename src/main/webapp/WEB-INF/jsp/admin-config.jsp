<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Configuracion</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1000px; margin: 30px auto; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 8px; }
        input { padding: 6px; }
        .ok { color: green; }
        .err { color: #b30000; }
        .menu a { margin-right: 12px; }
    </style>
</head>
<body>
<h2>Configuracion del sistema</h2>
<p class="menu">
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
    <a href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
    <a href="${pageContext.request.contextPath}/admin/prestamos">Prestamos</a>
    <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
</p>

<% String success = (String) request.getAttribute("success"); if (success != null) { %>
<div class="ok"><%= success %></div>
<% } %>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
<div class="err"><%= error %></div>
<% } %>

<h3>Guardar clave de configuracion</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/config">
    <input type="text" name="clave" placeholder="clave (ej. max_prestamos_alumno)" required />
    <input type="text" name="valor" placeholder="valor" required />
    <button type="submit">Guardar</button>
</form>

<%
    Map<String, String> configuracion = (Map<String, String>) request.getAttribute("configuracion");
%>
<table>
    <tr><th>Clave</th><th>Valor actual</th><th>Actualizar</th></tr>
    <% if (configuracion != null) {
        for (Map.Entry<String, String> item : configuracion.entrySet()) { %>
    <tr>
        <td><%= item.getKey() %></td>
        <td><%= item.getValue() %></td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/config">
                <input type="hidden" name="clave" value="<%= item.getKey() %>" />
                <input type="text" name="valor" value="<%= item.getValue() %>" required />
                <button type="submit">Guardar</button>
            </form>
        </td>
    </tr>
    <%  }
      } %>
</table>
</body>
</html>

