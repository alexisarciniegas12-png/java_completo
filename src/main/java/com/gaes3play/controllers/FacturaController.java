package com.gaes3play.controllers;

import com.gaes3play.repository.FacturaRepository;
import com.gaes3play.repository.PedidoRepository;
import com.gaes3play.services.ReporteVentaService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaRepository facturaRepo;
    private final PedidoRepository pedidoRepo;
    private final ReporteVentaService reporteVentaService;

    public FacturaController(
            FacturaRepository facturaRepo,
            PedidoRepository pedidoRepo,
            ReporteVentaService reporteVentaService
    ) {
        this.facturaRepo = facturaRepo;
        this.pedidoRepo = pedidoRepo;
        this.reporteVentaService = reporteVentaService;
    }

    @GetMapping
    public String list(Model model) {

        var pedidos = pedidoRepo.findAll();

        BigDecimal totalVentas = pedidos.stream()
                .map(p -> BigDecimal.valueOf(p.getPrecio()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalVentas", totalVentas);

        return "facturas/list";
    }

    // ======================
    //   DESCARGAR PDF
    // ======================
    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> generarPdf() {

        var pedidos = pedidoRepo.findAll();
        var pdf = reporteVentaService.generarReporteVentas(pedidos);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_ventas.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}
