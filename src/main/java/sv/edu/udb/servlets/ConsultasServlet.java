package sv.edu.udb.servlets;

import com.mediateca.web.DocumentoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/consultas")
public class ConsultasServlet extends HttpServlet {

    private final DocumentoService documentoService = new DocumentoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String palabra = req.getParameter("palabra");
        String tipo = req.getParameter("tipo");

        req.setAttribute("palabra", palabra == null ? "" : palabra);
        req.setAttribute("tipo", tipo == null ? "" : tipo);
        req.setAttribute("resultados", documentoService.buscarDocumentos(palabra, tipo));

        req.getRequestDispatcher("/WEB-INF/jsp/consultas.jsp").forward(req, resp);
    }
}

