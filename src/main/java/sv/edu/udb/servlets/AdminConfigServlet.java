package sv.edu.udb.servlets;

import com.mediateca.web.ConfigService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/config")
public class AdminConfigServlet extends HttpServlet {

    private final ConfigService configService = new ConfigService();

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

        req.setAttribute("configuracion", configService.listarConfiguracion());
        req.getRequestDispatcher("/WEB-INF/jsp/admin-config.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = AuthSupport.requireLogin(req, resp);
        if (session == null || !AuthSupport.requireAdmin(req, resp, session)) {
            return;
        }

        String clave = req.getParameter("clave");
        String valor = req.getParameter("valor");

        if (clave == null || clave.trim().isEmpty() || valor == null || valor.trim().isEmpty()) {
            session.setAttribute("flashError", "Clave y valor son requeridos.");
            resp.sendRedirect(req.getContextPath() + "/admin/config");
            return;
        }

        try {
            configService.setConfig(clave.trim(), valor.trim());
            session.setAttribute("flashSuccess", "Configuracion guardada.");
        } catch (Exception e) {
            session.setAttribute("flashError", "No se pudo guardar la configuracion: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/config");
    }
}

