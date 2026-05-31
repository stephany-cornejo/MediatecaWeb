<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Catalogo</title>
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
        .ok { color: green; }
        .err { color: #b30000; }
    </style>
</head>
<body>
<h2>Catalogo de documentos</h2>
<p>
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/prestamos/mios">Mis prestamos</a>
    <% String role = (String) session.getAttribute("role"); if ("ADMIN".equalsIgnoreCase(role)) { %>
    <a href="${pageContext.request.contextPath}/admin/documentos">Administrar documentos</a>
    <% } %>
    <a href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
</p>
<% String success = (String) request.getAttribute("success"); if (success != null) { %>
    <div class="ok"><%= success %></div>
<% } %>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
    <div class="err"><%= error %></div>
<% } %>
<%
    List<DocumentoService.Documento> documentos = (List<DocumentoService.Documento>) request.getAttribute("documentos");
%>
<table>
    <tr>
        <th>ID</th><th>Titulo</th><th>Tipo</th><th>Disponibles</th><th>Total</th><th>Accion</th>
    </tr>
    <% if (documentos != null) {
        for (DocumentoService.Documento d : documentos) { %>
    <tr>
        <td><%= d.id() %></td>
        <td><%= d.titulo() %></td>
        <td><%= d.tipo() %></td>
        <td><%= d.stockDisponible() %></td>
        <td><%= d.stockTotal() %></td>
        <td>
            <% if (d.stockDisponible() > 0) { %>
            <form method="post" action="${pageContext.request.contextPath}/prestamos/solicitar">
                <input type="hidden" name="documentoId" value="<%= d.id() %>" />
                <button type="submit">Solicitar</button>
            </form>
            <% } else { %>
            Sin stock
            <% } %>
        </td>
    </tr>
    <%  }
      } %>
</table>
</body>
</html>

