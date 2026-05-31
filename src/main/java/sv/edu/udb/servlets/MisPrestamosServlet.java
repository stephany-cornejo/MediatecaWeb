package sv.edu.udb.servlets;

import com.mediateca.web.PrestamoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/prestamos/mios")
public class MisPrestamosServlet extends HttpServlet {

    private final PrestamoService prestamoService = new PrestamoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int usuarioId = Integer.parseInt(session.getAttribute("usuarioId").toString());
        req.setAttribute("prestamos", prestamoService.listarPrestamosUsuario(usuarioId));
        req.getRequestDispatcher("/WEB-INF/jsp/mis-prestamos.jsp").forward(req, resp);
    }
}

