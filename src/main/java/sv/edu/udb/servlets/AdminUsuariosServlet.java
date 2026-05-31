package sv.edu.udb.servlets;

import com.mediateca.web.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/usuarios")
public class AdminUsuariosServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

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

        req.setAttribute("usuarios", usuarioService.listarUsuarios());
        req.getRequestDispatcher("/WEB-INF/jsp/admin-usuarios.jsp").forward(req, resp);
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
            if ("crear".equals(action)) {
                String nombre = value(req, "nombre");
                String password = value(req, "password");
                String rol = value(req, "rol").toUpperCase();
                ok = !nombre.isBlank() && !password.isBlank() && !rol.isBlank()
                    && usuarioService.crearUsuario(nombre, password, rol);
                flash(session, ok, "Usuario creado correctamente.", "No se pudo crear el usuario.");
            } else if ("actualizarRol".equals(action)) {
                int idUsuario = Integer.parseInt(req.getParameter("id"));
                String rol = value(req, "rol").toUpperCase();
                ok = usuarioService.actualizarRol(idUsuario, rol);
                flash(session, ok, "Rol actualizado.", "No se pudo actualizar el rol.");
            } else if ("resetPassword".equals(action)) {
                int idUsuario = Integer.parseInt(req.getParameter("id"));
                String nuevaPassword = value(req, "password");
                ok = !nuevaPassword.isBlank() && usuarioService.restablecerPassword(idUsuario, nuevaPassword);
                flash(session, ok, "Contrasena restablecida.", "No se pudo restablecer la contrasena.");
            } else if ("eliminar".equals(action)) {
                int idUsuario = Integer.parseInt(req.getParameter("id"));
                ok = usuarioService.eliminarUsuario(idUsuario);
                flash(session, ok, "Usuario eliminado.", "No se pudo eliminar (puede tener prestamos asociados). ");
            } else {
                session.setAttribute("flashError", "Accion no soportada.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", "Error procesando usuarios: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
    }

    private void flash(HttpSession session, boolean ok, String success, String error) {
        session.setAttribute(ok ? "flashSuccess" : "flashError", ok ? success : error);
    }

    private String value(HttpServletRequest req, String key) {
        String value = req.getParameter(key);
        return value == null ? "" : value.trim();
    }
}

