<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.PrestamoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mis Prestamos</title>
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
        body { font-family: Arial, sans-serif; max-width: 1000px; margin: 30px auto; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; }
    </style>
</head>
<body>
<h2>Mis prestamos</h2>
<p>
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
    <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
</p>
<%
    List<PrestamoService.Prestamo> prestamos = (List<PrestamoService.Prestamo>) request.getAttribute("prestamos");
%>
<table>
    <tr>
        <th>ID</th><th>Documento</th><th>Salida</th><th>Devolucion</th><th>Mora</th>
    </tr>
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
</table>
</body>
</html>

