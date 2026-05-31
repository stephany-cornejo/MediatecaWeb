<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        html, body {
            background: #fff !important;
            filter: none !important;
            opacity: 1 !important;
            pointer-events: auto !important;
        }
        body, body * {
            filter: none !important;
            opacity: 1 !important;
            pointer-events: auto !important;
        }
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background: #f4f6f8;
        }
        .panel {
            max-width: 800px;
            margin: 30px auto;
            background: #fff;
            border: 1px solid #dcdcdc;
            border-radius: 8px;
            padding: 20px;
            position: relative;
        }
        .menu a {
            margin-right: 12px;
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="panel">
<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
%>
<h2>Bienvenido, <%= username %> (<%= role %>)</h2>
<p class="menu">
    <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
    <a href="${pageContext.request.contextPath}/prestamos/mios">Mis prestamos</a>
    <a href="${pageContext.request.contextPath}/consultas">Consulta publica</a>
    <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
</p>
</div>
</body>
</html>

