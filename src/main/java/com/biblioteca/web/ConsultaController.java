package com.biblioteca.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ConsultaController {

    private final DocumentoService documentoService;

    public ConsultaController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping("/consultas")
    public String consultas(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) String tipo,
            Model model) {
        List<DocumentoService.Documento> resultados = documentoService.buscarDocumentos(palabra, tipo);
        model.addAttribute("resultados", resultados);
        model.addAttribute("palabra", palabra == null ? "" : palabra);
        model.addAttribute("tipo", tipo == null ? "" : tipo);
        return "consultas";
    }
}
