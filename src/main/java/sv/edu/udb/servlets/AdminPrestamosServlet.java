package sv.edu.udb.servlets;

import com.mediateca.web.DocumentoService;
import com.mediateca.web.PrestamoService;
import com.mediateca.web.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/prestamos")
public class AdminPrestamosServlet extends HttpServlet {

    private final PrestamoService prestamoService = new PrestamoService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final DocumentoService documentoService = new DocumentoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = AuthSupport.requireLogin(req, resp);
        if (session == null || !AuthSupport.requireAdmin(req, resp, session)) {
            return;
        }

        Object success = session.getAttribute("flashSuccess");
        Object error = session.getAttribute("flashError");
        if (success != null) {
            req.setAttribute("success", success);
            session.removeAttribute("flashSuccess");
        }
        if (error != null) {
            req.setAttribute("error", error);
            session.removeAttribute("flashError");
        }

        req.setAttribute("prestamos", prestamoService.listarPrestamosTodos());
        req.setAttribute("usuarios", usuarioService.listarUsuarios());
        req.setAttribute("documentos", documentoService.listarDocumentos());
        req.getRequestDispatcher("/WEB-INF/jsp/admin-prestamos.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = AuthSupport.requireLogin(req, resp);
        if (session == null || !AuthSupport.requireAdmin(req, resp, session)) {
            return;
        }

        String action = req.getParameter("action");
        boolean ok = false;

        try {
            if ("registrar".equals(action)) {
                int usuarioId = Integer.parseInt(req.getParameter("usuarioId"));
                int documentoId = Integer.parseInt(req.getParameter("documentoId"));
                String rol = usuarioService.obtenerRolUsuario(usuarioId);
                ok = prestamoService.solicitarPrestamo(usuarioId, rol, documentoId);
                flash(session, ok, "Prestamo registrado correctamente.", "No se pudo registrar (mora, limite o stock insuficiente).");
            } else if ("devolver".equals(action)) {
                int prestamoId = Integer.parseInt(req.getParameter("prestamoId"));
                ok = prestamoService.devolverPrestamo(prestamoId);
                flash(session, ok, "Devolucion registrada.", "No se pudo registrar la devolucion.");
            } else {
                session.setAttribute("flashError", "Accion no soportada.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", "Error procesando prestamos: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/prestamos");
    }

    private void flash(HttpSession session, boolean ok, String success, String error) {
        session.setAttribute(ok ? "flashSuccess" : "flashError", ok ? success : error);
    }
}

