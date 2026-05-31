<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
%>
    <div class="app-card">
        <div class="app-card-header">
            <h2 class="app-title">Bienvenido, <%= username %></h2>
            <p class="app-subtitle">Perfil activo: <span class="badge badge-soft"><%= role %></span></p>
        </div>
        <div class="app-card-body">
            <div class="d-flex flex-wrap gap-2 app-nav">
                <a class="nav-link" href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/prestamos/mios">Mis prestamos</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/consultas">Consulta publica</a>
                <% if ("ADMIN".equalsIgnoreCase(role)) { %>
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/usuarios">Admin usuarios</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/documentos">Admin documentos</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/prestamos">Admin prestamos</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/config">Configuracion</a>
                <% } %>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>

