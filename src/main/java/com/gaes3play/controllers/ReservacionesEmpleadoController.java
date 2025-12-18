package com.gaes3play.controllers;

import com.gaes3play.models.Reservacion;
import com.gaes3play.repository.ReservacionRepository;
import com.gaes3play.services.ReporteReservacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/empleado/reservaciones")
public class ReservacionesEmpleadoController {

    private final ReservacionRepository repo;
    private final ReporteReservacionService reporteService;

    public ReservacionesEmpleadoController(
            ReservacionRepository repo,
            ReporteReservacionService reporteService
    ) {
        this.repo = repo;
        this.reporteService = reporteService;
    }

    // ================================
    //        CRUD EMPLEADO
    // ================================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("reservaciones", repo.findAll());
        return "reservaciones_empleado/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("reservacion", new Reservacion());
        model.addAttribute("estados", Reservacion.Estado.values());
        return "reservaciones_empleado/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Reservacion r) {
        repo.save(r);
        return "redirect:/empleado/reservaciones";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        repo.findById(id)
                .ifPresent(r -> model.addAttribute("reservacion", r));
        model.addAttribute("estados", Reservacion.Estado.values());
        return "reservaciones_empleado/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/empleado/reservaciones";
    }

    // ================================
    //            BUSCAR
    // ================================
    @GetMapping("/buscar")
    public String buscar(@RequestParam("cliente") String cliente, Model model) {
        List<Reservacion> resultados =
                repo.findByClienteNombreContainingIgnoreCase(cliente);
        model.addAttribute("reservaciones", resultados);
        return "reservaciones_empleado/list";
    }

    // ================================
    //              PDF
    // ================================
    @GetMapping("/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=reporte_reservaciones_empleado.pdf"
        );

        List<Reservacion> reservaciones = repo.findAll();

        var bis = reporteService.generarReporteReservaciones(reservaciones);

        org.apache.commons.io.IOUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
    }
}
