package sv.edu.udb.servlets;

import com.mediateca.web.DocumentoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/documentos")
public class AdminDocumentosServlet extends HttpServlet {

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

        req.setAttribute("documentos", documentoService.listarDocumentos());
        req.getRequestDispatcher("/WEB-INF/jsp/admin-documentos.jsp").forward(req, resp);
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
                String tipo = value(req, "tipo").toUpperCase();
                String titulo = value(req, "titulo");
                String ubicacion = value(req, "ubicacion");
                int stockTotal = parseInt(req, "stockTotal");
                String extras = value(req, "camposEspecificosJson");
                if (extras.isBlank()) {
                    extras = construirJsonExtras(tipo, value(req, "campo1"), value(req, "campo2"), value(req, "campo3"));
                }

                ok = !titulo.isBlank() && !ubicacion.isBlank() && stockTotal >= 0
                    && documentoService.crearDocumento(tipo, titulo, ubicacion, stockTotal, extras);
                flash(session, ok, "Documento creado correctamente.", "No se pudo crear el documento.");
            } else if ("actualizar".equals(action)) {
                int id = parseInt(req, "id");
                String tipo = value(req, "tipo").toUpperCase();
                String titulo = value(req, "titulo");
                String ubicacion = value(req, "ubicacion");
                int stockTotal = parseInt(req, "stockTotal");
                int stockDisponible = parseInt(req, "stockDisponible");
                String extras = value(req, "camposEspecificosJson");

                if (stockDisponible > stockTotal) {
                    stockDisponible = stockTotal;
                }

                ok = !titulo.isBlank() && !ubicacion.isBlank() && stockTotal >= 0 && stockDisponible >= 0
                    && documentoService.actualizarDocumento(id, tipo, titulo, ubicacion, stockDisponible, stockTotal, extras);
                flash(session, ok, "Documento actualizado.", "No se pudo actualizar el documento.");
            } else if ("eliminar".equals(action)) {
                int id = parseInt(req, "id");
                ok = documentoService.eliminarDocumento(id);
                flash(session, ok, "Documento eliminado.", "No se pudo eliminar (puede tener prestamos asociados). ");
            } else {
                session.setAttribute("flashError", "Accion no soportada.");
            }
        } catch (Exception e) {
            session.setAttribute("flashError", "Error procesando documentos: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/documentos");
    }

    private void flash(HttpSession session, boolean ok, String success, String error) {
        session.setAttribute(ok ? "flashSuccess" : "flashError", ok ? success : error);
    }

    private int parseInt(HttpServletRequest req, String key) {
        return Integer.parseInt(req.getParameter(key));
    }

    private String value(HttpServletRequest req, String key) {
        String value = req.getParameter(key);
        return value == null ? "" : value.trim();
    }

    private String construirJsonExtras(String tipo, String campo1, String campo2, String campo3) {
        String v1 = escaparJson(campo1);
        String v2 = escaparJson(campo2);
        String v3 = escaparJson(campo3);
        if ("LIBRO".equals(tipo)) {
            return "{\"autor\":\"" + v1 + "\",\"isbn\":\"" + v2 + "\",\"editorial\":\"" + v3 + "\"}";
        }
        if ("REVISTA".equals(tipo)) {
            return "{\"periodicidad\":\"" + v1 + "\",\"fechaPublicacion\":\"" + v2 + "\",\"detalle\":\"" + v3 + "\"}";
        }
        if ("CD".equals(tipo)) {
            return "{\"genero\":\"" + v1 + "\",\"duracion\":\"" + v2 + "\",\"detalle\":\"" + v3 + "\"}";
        }
        if ("TESIS".equals(tipo)) {
            return "{\"autor\":\"" + v1 + "\",\"carrera\":\"" + v2 + "\",\"detalle\":\"" + v3 + "\"}";
        }
        return "{}";
    }

    private String escaparJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


