package com.gaes3play.controllers;

import com.gaes3play.models.Usuario;
import com.gaes3play.repository.UsuarioRepository;
import com.gaes3play.services.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;
    private final ReporteService reporteService;

    public UsuarioController(UsuarioRepository repo, ReporteService reporteService) {
        this.repo = repo;
        this.reporteService = reporteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("usuarios", repo.findAll());
        return "usuarios/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Usuario.Rol.values());
        return "usuarios/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Usuario usuario) {
        repo.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        repo.findById(id).ifPresent(u -> model.addAttribute("usuario", u));
        model.addAttribute("roles", Usuario.Rol.values());
        return "usuarios/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/usuarios";
    }

    // ----------------------------------------------------
    // 🔎 BUSQUEDA
    // ----------------------------------------------------
    @GetMapping("/buscar")
    public String buscar(@RequestParam("nombre") String nombre, Model model) {

        List<Usuario> resultados;

        if (nombre == null || nombre.trim().isEmpty()) {
            resultados = repo.findAll();
        } else {
            resultados = repo.findByNombreContainingIgnoreCase(nombre.trim());
        }

        model.addAttribute("usuarios", resultados);
        model.addAttribute("busqueda", nombre);

        return "usuarios/list";
    }

    // ----------------------------------------------------
    // 📄 GENERAR PDF
    // ----------------------------------------------------
    @GetMapping("/reporte")
    public ResponseEntity<byte[]> generarPDF() {

        List<Usuario> usuarios = repo.findAll();
        ByteArrayInputStream pdf = reporteService.generarReporteUsuarios(usuarios);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.pdf")
                .body(pdf.readAllBytes());
    }
}
