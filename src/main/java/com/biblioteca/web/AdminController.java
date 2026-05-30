package com.biblioteca.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;
    private final PrestamoService prestamoService;
    private final ConfigService configService;

    public AdminController(UsuarioService usuarioService, DocumentoService documentoService, PrestamoService prestamoService, ConfigService configService) {
        this.usuarioService = usuarioService;
        this.documentoService = documentoService;
        this.prestamoService = prestamoService;
        this.configService = configService;
    }

    @GetMapping("/usuarios")
    public String usuarios(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("username", session.getAttribute("username"));
        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("username", session.getAttribute("username"));
        return "usuario-form";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(HttpSession session,
                               @RequestParam String nombre,
                               @RequestParam String password,
                               @RequestParam String rol,
                               RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        if (nombre.isBlank() || password.isBlank() || rol.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Completa todos los campos.");
            return "redirect:/admin/usuarios/nuevo";
        }
        boolean creado = usuarioService.crearUsuario(nombre.trim(), password.trim(), rol.trim());
        if (!creado) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear el usuario. Verifica que el nombre no exista.");
            return "redirect:/admin/usuarios/nuevo";
        }
        redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/documentos")
    public String documentos(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("documentos", documentoService.listarDocumentos());
        model.addAttribute("username", session.getAttribute("username"));
        return "documentos";
    }

    @GetMapping("/documentos/nuevo")
    public String nuevoDocumento(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("username", session.getAttribute("username"));
        return "documento-form";
    }

    @PostMapping("/documentos")
    public String crearDocumento(HttpSession session,
                                 @RequestParam String tipo,
                                 @RequestParam String titulo,
                                 @RequestParam String ubicacion,
                                 @RequestParam int stockTotal,
                                 @RequestParam(required = false) String camposEspecificosJson,
                                 RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        if (tipo.isBlank() || titulo.isBlank() || ubicacion.isBlank() || stockTotal < 1) {
            redirectAttributes.addFlashAttribute("error", "Completa todos los campos y usa un stock válido.");
            return "redirect:/admin/documentos/nuevo";
        }
        boolean creado = documentoService.crearDocumento(tipo, titulo.trim(), ubicacion.trim(), stockTotal, camposEspecificosJson);
        if (!creado) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear el documento. Revise los datos.");
            return "redirect:/admin/documentos/nuevo";
        }
        redirectAttributes.addFlashAttribute("success", "Documento creado correctamente.");
        return "redirect:/admin/documentos";
    }

    @GetMapping("/prestamos")
    public String prestamos(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("prestamos", prestamoService.listarPrestamosTodos());
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));
        return "prestamos";
    }

    @GetMapping("/configuracion")
    public String configuracion(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("maxPrestamosAlumno", configService.getInt("max_prestamos_alumno", 3));
        model.addAttribute("maxPrestamosProfesor", configService.getInt("max_prestamos_profesor", 6));
        model.addAttribute("diasPrestamoAlumno", configService.getInt("dias_prestamo_alumno", 7));
        model.addAttribute("diasPrestamoProfesor", configService.getInt("dias_prestamo_profesor", 14));
        model.addAttribute("moraDiaria", configService.getDouble("mora_diaria", 5.0));
        return "configuracion";
    }

    @PostMapping("/configuracion")
    public String guardarConfiguracion(HttpSession session,
                                       @RequestParam int maxPrestamosAlumno,
                                       @RequestParam int maxPrestamosProfesor,
                                       @RequestParam int diasPrestamoAlumno,
                                       @RequestParam int diasPrestamoProfesor,
                                       @RequestParam double moraDiaria,
                                       RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        configService.setConfig("max_prestamos_alumno", String.valueOf(maxPrestamosAlumno));
        configService.setConfig("max_prestamos_profesor", String.valueOf(maxPrestamosProfesor));
        configService.setConfig("dias_prestamo_alumno", String.valueOf(diasPrestamoAlumno));
        configService.setConfig("dias_prestamo_profesor", String.valueOf(diasPrestamoProfesor));
        configService.setConfig("mora_diaria", String.valueOf(moraDiaria));
        redirectAttributes.addFlashAttribute("success", "Configuración guardada correctamente.");
        return "redirect:/admin/configuracion";
    }

    @PostMapping("/prestamos/devolver")
    public String devolverPrestamo(HttpSession session,
                                   @RequestParam int prestamoId,
                                   RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }
        boolean ok = prestamoService.devolverPrestamo(prestamoId);
        if (ok) {
            redirectAttributes.addFlashAttribute("success", "Devolución registrada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar la devolución.");
        }
        return "redirect:/admin/prestamos";
    }

    private boolean esAdmin(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && "ADMIN".equals(role.toString());
    }
}
