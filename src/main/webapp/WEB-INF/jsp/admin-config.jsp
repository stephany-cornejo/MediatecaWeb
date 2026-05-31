<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Configuracion</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Configuracion del sistema</h2>
        <div class="d-flex flex-wrap gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/prestamos">Prestamos</a>
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

<h3>Guardar clave de configuracion</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/config" class="row g-2 align-items-end mb-3">
    <div class="col-md-5"><input class="form-control" type="text" name="clave" placeholder="clave (ej. max_prestamos_alumno)" required /></div>
    <div class="col-md-5"><input class="form-control" type="text" name="valor" placeholder="valor" required /></div>
    <div class="col-md-2 d-grid"><button class="btn btn-app-primary" type="submit">Guardar</button></div>
</form>

<%
    Map<String, String> configuracion = (Map<String, String>) request.getAttribute("configuracion");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead><tr><th>Clave</th><th>Valor actual</th><th>Actualizar</th></tr></thead>
    <tbody>
    <% if (configuracion != null) {
        for (Map.Entry<String, String> item : configuracion.entrySet()) { %>
    <tr>
        <td><%= item.getKey() %></td>
        <td><%= item.getValue() %></td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/config">
                <input type="hidden" name="clave" value="<%= item.getKey() %>" />
                <input class="form-control form-control-sm" type="text" name="valor" value="<%= item.getValue() %>" required />
                <button class="btn btn-sm btn-outline-primary mt-1" type="submit">Guardar</button>
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

