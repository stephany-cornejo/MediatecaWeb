<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.UsuarioService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Usuarios</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1100px; margin: 30px auto; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 8px; vertical-align: top; }
        input, select { padding: 6px; }
        .ok { color: green; }
        .err { color: #b30000; }
        .menu a { margin-right: 12px; }
        .grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin: 10px 0; }
    </style>
</head>
<body>
<h2>Administracion de usuarios</h2>
<p class="menu">
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
    <a href="${pageContext.request.contextPath}/admin/prestamos">Prestamos</a>
    <a href="${pageContext.request.contextPath}/admin/config">Configuracion</a>
    <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
</p>

<% String success = (String) request.getAttribute("success"); if (success != null) { %>
<div class="ok"><%= success %></div>
<% } %>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
<div class="err"><%= error %></div>
<% } %>

<h3>Crear usuario</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
    <input type="hidden" name="action" value="crear" />
    <div class="grid">
        <input type="text" name="nombre" placeholder="Nombre" required />
        <input type="password" name="password" placeholder="Contrasena" required />
        <select name="rol" required>
            <option value="ALUMNO">ALUMNO</option>
            <option value="PROFESOR">PROFESOR</option>
            <option value="ADMIN">ADMIN</option>
        </select>
        <button type="submit">Crear</button>
    </div>
</form>

<%
    List<UsuarioService.Usuario> usuarios = (List<UsuarioService.Usuario>) request.getAttribute("usuarios");
%>
<table>
    <tr>
        <th>ID</th><th>Nombre</th><th>Rol</th><th>Mora</th><th>Actualizar rol</th><th>Reset password</th><th>Eliminar</th>
    </tr>
    <% if (usuarios != null) {
        for (UsuarioService.Usuario u : usuarios) { %>
    <tr>
        <td><%= u.id() %></td>
        <td><%= u.nombre() %></td>
        <td><%= u.rol() %></td>
        <td><%= u.mora() %></td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
                <input type="hidden" name="action" value="actualizarRol" />
                <input type="hidden" name="id" value="<%= u.id() %>" />
                <select name="rol">
                    <option value="ALUMNO" <%= "ALUMNO".equals(u.rol()) ? "selected" : "" %>>ALUMNO</option>
                    <option value="PROFESOR" <%= "PROFESOR".equals(u.rol()) ? "selected" : "" %>>PROFESOR</option>
                    <option value="ADMIN" <%= "ADMIN".equals(u.rol()) ? "selected" : "" %>>ADMIN</option>
                </select>
                <button type="submit">Guardar</button>
            </form>
        </td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
                <input type="hidden" name="action" value="resetPassword" />
                <input type="hidden" name="id" value="<%= u.id() %>" />
                <input type="password" name="password" placeholder="Nueva contrasena" required />
                <button type="submit">Reset</button>
            </form>
        </td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios" onsubmit="return confirm('Eliminar usuario?');">
                <input type="hidden" name="action" value="eliminar" />
                <input type="hidden" name="id" value="<%= u.id() %>" />
                <button type="submit">Eliminar</button>
            </form>
        </td>
    </tr>
    <%  }
      } %>
</table>
</body>
</html>

