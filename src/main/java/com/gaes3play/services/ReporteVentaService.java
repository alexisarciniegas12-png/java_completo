package com.gaes3play.services;

import com.gaes3play.models.Pedido;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color; // SOLO Color de AWT (esto no genera conflicto)
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ReporteVentaService {

    // ===============================
    //     ENCABEZADO Y PIE DE PÁGINA
    // ===============================
    private static class EncabezadoPiePagina extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Fondo gris suave
            cb.setColorFill(new Color(230, 230, 230));
            cb.rectangle(0, document.getPageSize().getHeight() - 40, document.getPageSize().getWidth(), 40);
            cb.fill();

            // Texto Parrilla Express
            Font marcaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, Color.BLACK);
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_LEFT,
                    new Paragraph("Parrilla Express", marcaFont),
                    document.leftMargin(),
                    document.getPageSize().getHeight() - 25,
                    0
            );

            // Línea decorativa amarilla
            cb.setLineWidth(1f);
            cb.setColorStroke(new Color(255, 193, 7));
            cb.moveTo(document.leftMargin(), document.getPageSize().getHeight() - 42);
            cb.lineTo(document.getPageSize().getWidth() - document.rightMargin(), document.getPageSize().getHeight() - 42);
            cb.stroke();

            // Pie de página (número de página)
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
            ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_CENTER,
                    new Paragraph("Parrilla Express – Página " + writer.getPageNumber(), smallFont),
                    document.getPageSize().getWidth() / 2,
                    document.bottomMargin() - 10,
                    0
            );
        }
    }

    // ============================================
    // GENERAR REPORTE PDF
    // ============================================
    public ByteArrayInputStream generarReporteVentas(List<Pedido> pedidos) {

        Document document = new Document(PageSize.A4, 36, 36, 80, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new EncabezadoPiePagina());

            document.open();

            // ===== TÍTULO PRINCIPAL =====
            com.lowagie.text.Font titleFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);

            Paragraph titulo = new Paragraph("Reporte de Ventas", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingBefore(55);
            titulo.setSpacingAfter(25);
            document.add(titulo);

            // ===== TABLA =====
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 5f, 3f});
            table.setSpacingBefore(12);

            Color amarillo = new Color(251, 192, 45);
            com.lowagie.text.Font headerFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            PdfPCell h1 = new PdfPCell(new Paragraph("NÚMERO DE MESA", headerFont));
            PdfPCell h2 = new PdfPCell(new Paragraph("DESCRIPCIÓN", headerFont));
            PdfPCell h3 = new PdfPCell(new Paragraph("PRECIO", headerFont));

            PdfPCell[] headers = {h1, h2, h3};

            for (PdfPCell c : headers) {
                c.setBackgroundColor(amarillo);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                c.setPadding(8);
                table.addCell(c);
            }

            // Filas alternadas
            com.lowagie.text.Font bodyFont =
                    FontFactory.getFont(FontFactory.HELVETICA, 11);

            Color altRow = new Color(255, 243, 205);
            boolean alternate = false;

            BigDecimal totalGeneral = BigDecimal.ZERO;

            for (Pedido p : pedidos) {
                Color bg = alternate ? altRow : Color.WHITE;

                table.addCell(celda(p.getCliente(), bodyFont, bg));
                table.addCell(celda(p.getDescripcion(), bodyFont, bg));
                table.addCell(celda(String.valueOf(p.getPrecio()), bodyFont, bg));

                totalGeneral = totalGeneral.add(BigDecimal.valueOf(p.getPrecio()));
                alternate = !alternate;
            }

            document.add(table);

            // ===== TOTAL GENERAL =====
            com.lowagie.text.Font totalFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            Paragraph total = new Paragraph(
                    "\nTotal General de Ventas: $" + totalGeneral.toPlainString(),
                    totalFont
            );
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(15);
            document.add(total);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // CELDA CORREGIDA (USO DE com.lowagie.text.Font)
    private PdfPCell celda(String texto, com.lowagie.text.Font font, Color bg) {
        PdfPCell c = new PdfPCell(new Paragraph(texto, font));
        c.setBackgroundColor(bg);
        c.setPadding(6);
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        return c;
    }
}
