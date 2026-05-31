package sv.edu.udb.servlets;

import com.mediateca.web.DocumentoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private final DocumentoService documentoService = new DocumentoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
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
        req.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(req, resp);
    }
}

