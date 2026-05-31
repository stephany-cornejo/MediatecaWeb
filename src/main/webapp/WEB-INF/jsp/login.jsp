<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet" />
</head>
<body class="app-body">
<div class="app-login-shell">
    <div class="app-card">
        <div class="app-card-header">
            <h2 class="app-title">Mediateca - Login</h2>
            <p class="app-subtitle">Ingresa tus credenciales para continuar.</p>
        </div>
        <div class="app-card-body">
            <% String error = (String) request.getAttribute("error"); if (error != null) { %>
            <div class="alert alert-danger" role="alert"><%= error %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/login" class="vstack gap-3">
                <div>
                    <label class="form-label">Usuario</label>
                    <input class="form-control" type="text" name="username" required />
                </div>
                <div>
                    <label class="form-label">Contrasena</label>
                    <input class="form-control" type="password" name="password" required />
                </div>
                <button class="btn btn-app-primary w-100" type="submit">Ingresar</button>
            </form>
            <div class="mt-3 text-center">
                <a href="${pageContext.request.contextPath}/consultas">Consulta publica</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>

