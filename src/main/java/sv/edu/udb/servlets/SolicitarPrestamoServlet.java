package sv.edu.udb.servlets;

import com.mediateca.web.PrestamoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/prestamos/solicitar")
public class SolicitarPrestamoServlet extends HttpServlet {

    private final PrestamoService prestamoService = new PrestamoService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int documentoId = Integer.parseInt(req.getParameter("documentoId"));
            int usuarioId = Integer.parseInt(session.getAttribute("usuarioId").toString());
            String role = session.getAttribute("role") == null ? "ALUMNO" : session.getAttribute("role").toString();

            boolean ok = prestamoService.solicitarPrestamo(usuarioId, role, documentoId);
            if (ok) {
                session.setAttribute("flashSuccess", "Prestamo solicitado correctamente.");
            } else {
                session.setAttribute("flashError", "No se pudo solicitar el prestamo.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Documento invalido.");
        }

        resp.sendRedirect(req.getContextPath() + "/catalogo");
    }
}

