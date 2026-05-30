package com.biblioteca.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping({"/", "/login"})
    public String showLoginPage(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LoginService.Usuario usuario = loginService.autenticar(username.trim(), password.trim());
        if (usuario == null) {
            redirectAttributes.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "redirect:/login";
        }

        session.setAttribute("usuarioId", usuario.id());
        session.setAttribute("username", usuario.nombre());
        session.setAttribute("role", usuario.rol());
        return "redirect:/dashboard";
    }
}
