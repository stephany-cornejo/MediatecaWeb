package com.biblioteca.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    private final DocumentoService documentoService;
    private final PrestamoService prestamoService;

    public UsuarioController(DocumentoService documentoService, PrestamoService prestamoService) {
        this.documentoService = documentoService;
        this.prestamoService = prestamoService;
    }

    @GetMapping("/catalogo")
    public String catalogo(HttpSession session, Model model) {
        if (!estaLogueado(session)) {
            return "redirect:/login";
        }
        model.addAttribute("documentos", documentoService.listarDocumentos());
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "catalogo";
    }

    @PostMapping("/prestamos/solicitar")
    public String solicitarPrestamo(HttpSession session,
                                    @RequestParam int documentoId,
                                    RedirectAttributes redirectAttributes) {
        if (!estaLogueado(session)) {
            return "redirect:/login";
        }
        Object userId = session.getAttribute("usuarioId");
        Object role = session.getAttribute("role");
        if (userId == null) {
            return "redirect:/login";
        }
        boolean exito = prestamoService.solicitarPrestamo(Integer.parseInt(userId.toString()), role == null ? "ALUMNO" : role.toString(), documentoId);
        if (exito) {
            redirectAttributes.addFlashAttribute("success", "Préstamo solicitado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo solicitar el préstamo. Revisa mora, límite o disponibilidad.");
        }
        return "redirect:/catalogo";
    }

    @GetMapping("/prestamos/mios")
    public String misPrestamos(HttpSession session, Model model) {
        if (!estaLogueado(session)) {
            return "redirect:/login";
        }
        Object userId = session.getAttribute("usuarioId");
        if (userId == null) {
            return "redirect:/login";
        }
        model.addAttribute("prestamos", prestamoService.listarPrestamosUsuario(Integer.parseInt(userId.toString())));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "mis-prestamos";
    }

    private boolean estaLogueado(HttpSession session) {
        return session.getAttribute("username") != null;
    }
}
