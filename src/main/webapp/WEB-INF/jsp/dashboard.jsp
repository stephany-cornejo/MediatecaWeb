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
    Integer kpiEjemplaresStock = (Integer) request.getAttribute("kpiEjemplaresStock");
    Integer kpiUsuarios = (Integer) request.getAttribute("kpiUsuarios");
    Integer kpiEjemplaresPrestados = (Integer) request.getAttribute("kpiEjemplaresPrestados");
    Integer kpiLibros = (Integer) request.getAttribute("kpiLibros");
    Integer kpiRevistas = (Integer) request.getAttribute("kpiRevistas");
    Integer kpiCDs = (Integer) request.getAttribute("kpiCDs");
    Integer kpiTesis = (Integer) request.getAttribute("kpiTesis");
%>
    <div class="dashboard-layout">
        <aside class="dashboard-sidebar app-card">
            <div>
                <div class="dashboard-brand">
                    <div class="dashboard-brand-mark">M</div>
                    <div>
                        <h2 class="app-title mb-1">Mediateca</h2>
                        <p class="app-subtitle mb-0"><%= username %> · <%= role %></p>
                    </div>
                </div>

                <nav class="dashboard-menu mt-4">
                    <a class="dashboard-link active" href="${pageContext.request.contextPath}/dashboard">Resumen</a>
                    <a class="dashboard-link" href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
                    <% if ("ADMIN".equalsIgnoreCase(role)) { %>
                    <a class="dashboard-link" href="${pageContext.request.contextPath}/admin/usuarios">Usuarios</a>
                    <a class="dashboard-link" href="${pageContext.request.contextPath}/admin/documentos">Documentos</a>
                    <a class="dashboard-link" href="${pageContext.request.contextPath}/admin/prestamos">Préstamos</a>
                    <% } %>
                    <a class="dashboard-link" href="${pageContext.request.contextPath}/prestamos/mios">Mis préstamos</a>
                    <% if ("ADMIN".equalsIgnoreCase(role)) { %>
                    <a class="dashboard-link" href="${pageContext.request.contextPath}/admin/config">Configuración</a>
                    <% } %>
                </nav>
            </div>

            <div class="dashboard-sidebar-footer">
                <a class="dashboard-link dashboard-link-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesión</a>
            </div>
        </aside>

        <section class="dashboard-main">
            <div class="app-card mb-4">
                <div class="app-card-body dashboard-hero">
                    <div>
                        <span class="badge badge-soft mb-3">Overview general</span>
                        <h1 class="dashboard-heading">Inventario y actividad de la mediateca</h1>
                        <p class="dashboard-copy mb-0">
                            Consulta rapidamente el estado del sistema, el volumen de usuarios y la distribución actual del inventario por tipo de documento.
                        </p>
                    </div>
                    <div class="dashboard-highlight app-card">
                        <div class="app-card-body p-3">
                            <div class="text-uppercase small text-muted mb-1">Ejemplares prestados</div>
                            <div class="dashboard-highlight-number"><%= kpiEjemplaresPrestados == null ? 0 : kpiEjemplaresPrestados %></div>
                            <div class="text-muted small">Préstamos activos registrados actualmente.</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="dashboard-kpi-grid">
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">Ejemplares en stock</div>
                        <div class="dashboard-kpi-value"><%= kpiEjemplaresStock == null ? 0 : kpiEjemplaresStock %></div>
                    </div>
                </article>
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">Usuarios</div>
                        <div class="dashboard-kpi-value"><%= kpiUsuarios == null ? 0 : kpiUsuarios %></div>
                    </div>
                </article>
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">Ejemplares prestados</div>
                        <div class="dashboard-kpi-value"><%= kpiEjemplaresPrestados == null ? 0 : kpiEjemplaresPrestados %></div>
                    </div>
                </article>
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">Libros</div>
                        <div class="dashboard-kpi-value"><%= kpiLibros == null ? 0 : kpiLibros %></div>
                    </div>
                </article>
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">Revistas</div>
                        <div class="dashboard-kpi-value"><%= kpiRevistas == null ? 0 : kpiRevistas %></div>
                    </div>
                </article>
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">CDs</div>
                        <div class="dashboard-kpi-value"><%= kpiCDs == null ? 0 : kpiCDs %></div>
                    </div>
                </article>
                <article class="dashboard-kpi-card app-card">
                    <div class="app-card-body">
                        <div class="dashboard-kpi-label">Tesis</div>
                        <div class="dashboard-kpi-value"><%= kpiTesis == null ? 0 : kpiTesis %></div>
                    </div>
                </article>
            </div>
        </section>
    </div>
</div>
</body>
</html>

