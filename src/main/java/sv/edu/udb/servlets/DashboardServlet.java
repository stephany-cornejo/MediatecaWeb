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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    private final DocumentoService documentoService = new DocumentoService();
    private final PrestamoService prestamoService = new PrestamoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int usuarios = usuarioService.contarUsuarios();
        int stockDisponible = documentoService.sumarStockDisponible();
        int stockTotal = documentoService.sumarStockTotal();
        int ejemplaresPrestados = prestamoService.contarPrestamosActivos();

        req.setAttribute("kpiUsuarios", usuarios);
        req.setAttribute("kpiEjemplaresStock", stockDisponible);
        req.setAttribute("kpiEjemplaresPrestados", ejemplaresPrestados);
        req.setAttribute("kpiDocumentosTotal", stockTotal);
        req.setAttribute("kpiLibros", documentoService.contarPorTipo("LIBRO"));
        req.setAttribute("kpiRevistas", documentoService.contarPorTipo("REVISTA"));
        req.setAttribute("kpiCDs", documentoService.contarPorTipo("CD"));
        req.setAttribute("kpiTesis", documentoService.contarPorTipo("TESIS"));

        req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
    }
}

