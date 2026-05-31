<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Consultas</title>
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
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 8px; }
        form input, form select { margin-right: 8px; }
    </style>
</head>
<body>
<h2>Consulta publica de documentos</h2>
<p>
    <a href="${pageContext.request.contextPath}/login">Login</a>
</p>
<%
    String palabra = (String) request.getAttribute("palabra");
    String tipo = (String) request.getAttribute("tipo");
%>
<form method="get" action="${pageContext.request.contextPath}/consultas">
    <input type="text" name="palabra" value="<%= palabra == null ? "" : palabra %>" placeholder="Palabra" />
    <select name="tipo">
        <option value="">Todos</option>
        <option value="LIBRO" <%= "LIBRO".equals(tipo) ? "selected" : "" %>>LIBRO</option>
        <option value="REVISTA" <%= "REVISTA".equals(tipo) ? "selected" : "" %>>REVISTA</option>
        <option value="CD" <%= "CD".equals(tipo) ? "selected" : "" %>>CD</option>
        <option value="TESIS" <%= "TESIS".equals(tipo) ? "selected" : "" %>>TESIS</option>
    </select>
    <button type="submit">Buscar</button>
</form>
<%
    List<DocumentoService.Documento> resultados = (List<DocumentoService.Documento>) request.getAttribute("resultados");
%>
<table>
    <tr>
        <th>ID</th><th>Titulo</th><th>Tipo</th><th>Ubicacion</th><th>Disponibles</th><th>Total</th>
    </tr>
    <% if (resultados != null) {
        for (DocumentoService.Documento d : resultados) { %>
    <tr>
        <td><%= d.id() %></td>
        <td><%= d.titulo() %></td>
        <td><%= d.tipo() %></td>
        <td><%= d.ubicacion() %></td>
        <td><%= d.stockDisponible() %></td>
        <td><%= d.stockTotal() %></td>
    </tr>
    <%  }
      } %>
</table>
</body>
</html>

