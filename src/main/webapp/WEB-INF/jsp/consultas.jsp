<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Consultas</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Consulta publica de documentos</h2>
        <div class="d-flex gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
        </div>
    </div>
    <div class="app-card-body">
<%
    String palabra = (String) request.getAttribute("palabra");
    String tipo = (String) request.getAttribute("tipo");
%>
<form method="get" action="${pageContext.request.contextPath}/consultas" class="row g-2 align-items-end mb-3">
    <div class="col-md-5">
        <label class="form-label">Palabra</label>
        <input class="form-control" type="text" name="palabra" value="<%= palabra == null ? "" : palabra %>" placeholder="Palabra" />
    </div>
    <div class="col-md-4">
        <label class="form-label">Tipo</label>
        <select class="form-select" name="tipo">
        <option value="">Todos</option>
        <option value="LIBRO" <%= "LIBRO".equals(tipo) ? "selected" : "" %>>LIBRO</option>
        <option value="REVISTA" <%= "REVISTA".equals(tipo) ? "selected" : "" %>>REVISTA</option>
        <option value="CD" <%= "CD".equals(tipo) ? "selected" : "" %>>CD</option>
        <option value="TESIS" <%= "TESIS".equals(tipo) ? "selected" : "" %>>TESIS</option>
        </select>
    </div>
    <div class="col-md-3 d-grid">
        <button class="btn btn-app-primary" type="submit">Buscar</button>
    </div>
</form>
<%
    List<DocumentoService.Documento> resultados = (List<DocumentoService.Documento>) request.getAttribute("resultados");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead>
    <tr>
        <th>ID</th><th>Titulo</th><th>Tipo</th><th>Ubicacion</th><th>Disponibles</th><th>Total</th>
    </tr>
    </thead>
    <tbody>
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
    </tbody>
</table>
</div>
    </div>
</div>
</div>
</body>
</html>

