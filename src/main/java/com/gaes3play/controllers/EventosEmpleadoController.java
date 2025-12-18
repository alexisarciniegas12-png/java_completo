package com.gaes3play.controllers;

import com.gaes3play.models.Evento;
import com.gaes3play.repository.EventoRepository;
import com.gaes3play.services.ReporteEventoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/empleado/eventos")
public class EventosEmpleadoController {

    private final EventoRepository repo;
    private final ReporteEventoService reporteService;

    public EventosEmpleadoController(
            EventoRepository repo,
            ReporteEventoService reporteService
    ) {
        this.repo = repo;
        this.reporteService = reporteService;
    }

    // ===============================
    //        CRUD EMPLEADO
    // ===============================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("eventos", repo.findAll());
        return "eventos_empleado/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("evento", new Evento());
        return "eventos_empleado/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Evento evento) {
        repo.save(evento);
        return "redirect:/empleado/eventos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        repo.findById(id)
                .ifPresent(e -> model.addAttribute("evento", e));
        return "eventos_empleado/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/empleado/eventos";
    }

    // ===============================
    //           BUSCAR
    // ===============================
    @GetMapping("/buscar")
    public String buscar(@RequestParam("titulo") String titulo, Model model) {
        List<Evento> resultados =
                repo.findByTituloContainingIgnoreCase(titulo);
        model.addAttribute("eventos", resultados);
        return "eventos_empleado/list";
    }

    // ===============================
    //             PDF
    // ===============================
    @GetMapping("/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=reporte_eventos_empleado.pdf"
        );

        List<Evento> eventos = repo.findAll();

        var bis = reporteService.generarReporteEventos(eventos);

        org.apache.commons.io.IOUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
    }
}
