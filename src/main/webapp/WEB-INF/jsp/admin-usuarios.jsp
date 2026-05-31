<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.UsuarioService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Usuarios</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Administracion de usuarios</h2>
        <div class="d-flex flex-wrap gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/prestamos">Prestamos</a>
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

<h3>Crear usuario</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
    <input type="hidden" name="action" value="crear" />
    <div class="row g-2 align-items-end mb-3">
        <div class="col-md-4"><input class="form-control" type="text" name="nombre" placeholder="Nombre" required /></div>
        <div class="col-md-3"><input class="form-control" type="password" name="password" placeholder="Contrasena" required /></div>
        <div class="col-md-3"><select class="form-select" name="rol" required>
            <option value="ALUMNO">ALUMNO</option>
            <option value="PROFESOR">PROFESOR</option>
            <option value="ADMIN">ADMIN</option>
        </select></div>
        <div class="col-md-2 d-grid"><button class="btn btn-app-primary" type="submit">Crear</button></div>
    </div>
</form>

<%
    List<UsuarioService.Usuario> usuarios = (List<UsuarioService.Usuario>) request.getAttribute("usuarios");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead>
    <tr>
        <th>ID</th><th>Nombre</th><th>Rol</th><th>Mora</th><th>Actualizar rol</th><th>Reset password</th><th>Eliminar</th>
    </tr>
    </thead>
    <tbody>
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
                <select class="form-select form-select-sm" name="rol">
                    <option value="ALUMNO" <%= "ALUMNO".equals(u.rol()) ? "selected" : "" %>>ALUMNO</option>
                    <option value="PROFESOR" <%= "PROFESOR".equals(u.rol()) ? "selected" : "" %>>PROFESOR</option>
                    <option value="ADMIN" <%= "ADMIN".equals(u.rol()) ? "selected" : "" %>>ADMIN</option>
                </select>
                <button class="btn btn-sm btn-outline-primary mt-1" type="submit">Guardar</button>
            </form>
        </td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
                <input type="hidden" name="action" value="resetPassword" />
                <input type="hidden" name="id" value="<%= u.id() %>" />
                <input class="form-control form-control-sm" type="password" name="password" placeholder="Nueva contrasena" required />
                <button class="btn btn-sm btn-outline-secondary mt-1" type="submit">Reset</button>
            </form>
        </td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios" onsubmit="return confirm('Eliminar usuario?');">
                <input type="hidden" name="action" value="eliminar" />
                <input type="hidden" name="id" value="<%= u.id() %>" />
                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
            </form>
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

