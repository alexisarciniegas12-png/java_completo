package com.gaes3play.controllers;

import com.gaes3play.models.Pedido;
import com.gaes3play.repository.PedidoRepository;
import com.gaes3play.services.ReportePedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository repo;
    private final ReportePedidoService reporteService;

    public PedidoController(PedidoRepository repo, ReportePedidoService reporteService) {
        this.repo = repo;
        this.reporteService = reporteService;
    }

    // ===============================
    //            LISTAR
    // ===============================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("pedidos", repo.findAll());
        return "pedidos/list";
    }

    // ===============================
    //             NEW
    // ===============================
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("pedido", new Pedido());
        return "pedidos/form";
    }

    // ===============================
    //             SAVE
    // ===============================
    @PostMapping("/save")
    public String save(@ModelAttribute Pedido p) {
        repo.save(p);
        return "redirect:/pedidos";
    }

    // ===============================
    //             EDIT
    // ===============================
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        repo.findById(id).ifPresent(p -> model.addAttribute("pedido", p));
        return "pedidos/form";
    }

    // ===============================
    //             DELETE
    // ===============================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/pedidos";
    }

    // ===============================
    //            BUSCAR
    // ===============================
    @GetMapping("/buscar")
    public String buscar(@RequestParam("cliente") String cliente, Model model) {
        List<Pedido> resultados = repo.findByClienteContainingIgnoreCase(cliente);
        model.addAttribute("pedidos", resultados);
        return "pedidos/list";
    }

    // ===============================
    //              PDF
    // ===============================
    @GetMapping("/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_pedidos.pdf");

        List<Pedido> pedidos = repo.findAll();

        var bis = reporteService.generarReportePedidos(pedidos);

        org.apache.commons.io.IOUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
    }
}
