package sv.edu.udb.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public final class AuthSupport {

    private AuthSupport() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static HttpSession requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return session;
    }

    public static boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equalsIgnoreCase(role.toString())) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return false;
        }
        return true;
    }
}

