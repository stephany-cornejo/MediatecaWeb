<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
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
        body { font-family: Arial, sans-serif; max-width: 420px; margin: 40px auto; }
        input { width: 100%; margin: 8px 0; padding: 8px; }
        button { padding: 8px 14px; }
        .error { color: #b30000; margin-top: 10px; }
    </style>
</head>
<body>
<h2>Mediateca - Login</h2>
<form method="post" action="${pageContext.request.contextPath}/login">
    <label>Usuario</label>
    <input type="text" name="username" required />

    <label>Contrasena</label>
    <input type="password" name="password" required />

    <button type="submit">Ingresar</button>
</form>
<p><a href="${pageContext.request.contextPath}/consultas">Consulta publica</a></p>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
    <div class="error"><%= error %></div>
<% } %>
</body>
</html>

