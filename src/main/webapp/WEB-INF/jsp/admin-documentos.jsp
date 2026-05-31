<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Documentos</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-shell">
<div class="app-card">
    <div class="app-card-header">
        <h2 class="app-title">Administracion de documentos</h2>
        <div class="d-flex flex-wrap gap-2 mt-2 app-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
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

<h3>Crear documento</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/documentos">
    <input type="hidden" name="action" value="crear" />
    <div class="row g-2 align-items-end mb-3">
        <div class="col-md-2"><select class="form-select" name="tipo" id="tipoDocumento" required>
            <option value="LIBRO">LIBRO</option>
            <option value="REVISTA">REVISTA</option>
            <option value="CD">CD</option>
            <option value="TESIS">TESIS</option>
        </select></div>
        <div class="col-md-3"><input class="form-control" type="text" name="titulo" placeholder="Titulo" required /></div>
        <div class="col-md-2"><input class="form-control" type="text" name="ubicacion" placeholder="Ubicacion" required /></div>
        <div class="col-md-2"><input class="form-control" type="number" min="0" name="stockTotal" placeholder="Stock total" required /></div>
        <div class="col-md-1"><input class="form-control" type="text" id="campo1" name="campo1" placeholder="Autor" /></div>
        <div class="col-md-1"><input class="form-control" type="text" id="campo2" name="campo2" placeholder="ISBN" /></div>
        <div class="col-md-1"><input class="form-control" type="text" id="campo3" name="campo3" placeholder="Editorial" /></div>
        <div class="col-md-9"><input class="form-control" type="text" name="camposEspecificosJson" placeholder='JSON extra opcional (sobrescribe campos)' /></div>
        <div class="col-md-3 d-grid"><button class="btn btn-app-primary" type="submit">Crear</button></div>
    </div>
</form>

<%
    List<DocumentoService.Documento> documentos = (List<DocumentoService.Documento>) request.getAttribute("documentos");
%>
<div class="app-table-wrap">
<table class="table table-bordered table-hover app-table">
    <thead>
    <tr>
        <th>ID</th><th>Tipo</th><th>Titulo</th><th>Ubicacion</th><th>Disp.</th><th>Total</th><th>Campos JSON</th><th>Guardar</th><th>Eliminar</th>
    </tr>
    </thead>
    <tbody>
    <% if (documentos != null) {
        for (DocumentoService.Documento d : documentos) {
            String formId = "upd-" + d.id(); %>
    <tr>
        <td><%= d.id() %></td>
        <td>
            <select class="form-select form-select-sm" name="tipo" form="<%= formId %>">
                <option value="LIBRO" <%= "LIBRO".equals(d.tipo()) ? "selected" : "" %>>LIBRO</option>
                <option value="REVISTA" <%= "REVISTA".equals(d.tipo()) ? "selected" : "" %>>REVISTA</option>
                <option value="CD" <%= "CD".equals(d.tipo()) ? "selected" : "" %>>CD</option>
                <option value="TESIS" <%= "TESIS".equals(d.tipo()) ? "selected" : "" %>>TESIS</option>
            </select>
        </td>
        <td><input class="form-control form-control-sm" type="text" name="titulo" value="<%= d.titulo() %>" form="<%= formId %>" required /></td>
        <td><input class="form-control form-control-sm" type="text" name="ubicacion" value="<%= d.ubicacion() %>" form="<%= formId %>" required /></td>
        <td><input class="form-control form-control-sm" type="number" min="0" name="stockDisponible" value="<%= d.stockDisponible() %>" form="<%= formId %>" required /></td>
        <td><input class="form-control form-control-sm" type="number" min="0" name="stockTotal" value="<%= d.stockTotal() %>" form="<%= formId %>" required /></td>
        <td><textarea class="form-control form-control-sm" name="camposEspecificosJson" form="<%= formId %>"><%= d.camposEspecificosJson() %></textarea></td>
        <td>
            <form id="<%= formId %>" method="post" action="${pageContext.request.contextPath}/admin/documentos">
                <input type="hidden" name="action" value="actualizar" />
                <input type="hidden" name="id" value="<%= d.id() %>" />
                <button class="btn btn-sm btn-outline-primary" type="submit">Guardar</button>
            </form>
        </td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/documentos" onsubmit="return confirm('Eliminar documento?');">
                <input type="hidden" name="action" value="eliminar" />
                <input type="hidden" name="id" value="<%= d.id() %>" />
                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
            </form>
        </td>
    </tr>
    <%  }
      } %>
    </tbody>
</table>
</div>

<script>
    (function () {
        var tipo = document.getElementById('tipoDocumento');
        var campo1 = document.getElementById('campo1');
        var campo2 = document.getElementById('campo2');
        var campo3 = document.getElementById('campo3');

        function actualizarEtiquetas() {
            var valor = tipo.value;
            if (valor === 'LIBRO') {
                campo1.placeholder = 'Autor';
                campo2.placeholder = 'ISBN';
                campo3.placeholder = 'Editorial';
            } else if (valor === 'REVISTA') {
                campo1.placeholder = 'Periodicidad';
                campo2.placeholder = 'Fecha publicacion';
                campo3.placeholder = 'Detalle';
            } else if (valor === 'CD') {
                campo1.placeholder = 'Genero';
                campo2.placeholder = 'Duracion';
                campo3.placeholder = 'Detalle';
            } else if (valor === 'TESIS') {
                campo1.placeholder = 'Autor';
                campo2.placeholder = 'Carrera';
                campo3.placeholder = 'Detalle';
            }
        }

        tipo.addEventListener('change', actualizarEtiquetas);
        actualizarEtiquetas();
    })();
</script>
    </div>
</div>
</div>
</body>
</html>



