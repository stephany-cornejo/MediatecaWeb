<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mediateca.web.DocumentoService" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin - Documentos</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 30px auto; }
        table { border-collapse: collapse; width: 100%; margin-top: 12px; }
        th, td { border: 1px solid #ccc; padding: 8px; vertical-align: top; }
        input, select, textarea { padding: 6px; width: 100%; box-sizing: border-box; }
        textarea { min-height: 45px; }
        .ok { color: green; }
        .err { color: #b30000; }
        .menu a { margin-right: 12px; }
        .grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin: 10px 0; }
    </style>
</head>
<body>
<h2>Administracion de documentos</h2>
<p class="menu">
    <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
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

<h3>Crear documento</h3>
<form method="post" action="${pageContext.request.contextPath}/admin/documentos">
    <input type="hidden" name="action" value="crear" />
    <div class="grid">
        <select name="tipo" id="tipoDocumento" required>
            <option value="LIBRO">LIBRO</option>
            <option value="REVISTA">REVISTA</option>
            <option value="CD">CD</option>
            <option value="TESIS">TESIS</option>
        </select>
        <input type="text" name="titulo" placeholder="Titulo" required />
        <input type="text" name="ubicacion" placeholder="Ubicacion" required />
        <input type="number" min="0" name="stockTotal" placeholder="Stock total" required />
        <input type="text" id="campo1" name="campo1" placeholder="Autor" />
        <input type="text" id="campo2" name="campo2" placeholder="ISBN" />
        <input type="text" id="campo3" name="campo3" placeholder="Editorial" />
        <input type="text" name="camposEspecificosJson" placeholder='JSON extra opcional (sobrescribe campos)' />
        <button type="submit">Crear</button>
    </div>
</form>

<%
    List<DocumentoService.Documento> documentos = (List<DocumentoService.Documento>) request.getAttribute("documentos");
%>
<table>
    <tr>
        <th>ID</th><th>Tipo</th><th>Titulo</th><th>Ubicacion</th><th>Disp.</th><th>Total</th><th>Campos JSON</th><th>Guardar</th><th>Eliminar</th>
    </tr>
    <% if (documentos != null) {
        for (DocumentoService.Documento d : documentos) {
            String formId = "upd-" + d.id(); %>
    <tr>
        <td><%= d.id() %></td>
        <td>
            <select name="tipo" form="<%= formId %>">
                <option value="LIBRO" <%= "LIBRO".equals(d.tipo()) ? "selected" : "" %>>LIBRO</option>
                <option value="REVISTA" <%= "REVISTA".equals(d.tipo()) ? "selected" : "" %>>REVISTA</option>
                <option value="CD" <%= "CD".equals(d.tipo()) ? "selected" : "" %>>CD</option>
                <option value="TESIS" <%= "TESIS".equals(d.tipo()) ? "selected" : "" %>>TESIS</option>
            </select>
        </td>
        <td><input type="text" name="titulo" value="<%= d.titulo() %>" form="<%= formId %>" required /></td>
        <td><input type="text" name="ubicacion" value="<%= d.ubicacion() %>" form="<%= formId %>" required /></td>
        <td><input type="number" min="0" name="stockDisponible" value="<%= d.stockDisponible() %>" form="<%= formId %>" required /></td>
        <td><input type="number" min="0" name="stockTotal" value="<%= d.stockTotal() %>" form="<%= formId %>" required /></td>
        <td><textarea name="camposEspecificosJson" form="<%= formId %>"><%= d.camposEspecificosJson() %></textarea></td>
        <td>
            <form id="<%= formId %>" method="post" action="${pageContext.request.contextPath}/admin/documentos">
                <input type="hidden" name="action" value="actualizar" />
                <input type="hidden" name="id" value="<%= d.id() %>" />
                <button type="submit">Guardar</button>
            </form>
        </td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/documentos" onsubmit="return confirm('Eliminar documento?');">
                <input type="hidden" name="action" value="eliminar" />
                <input type="hidden" name="id" value="<%= d.id() %>" />
                <button type="submit">Eliminar</button>
            </form>
        </td>
    </tr>
    <%  }
      } %>
</table>

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
</body>
</html>



