package com.gaes3play.services;

import com.gaes3play.models.Pedido;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportePedidoService {

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

    // ===============================
    //        GENERAR REPORTE PDF
    // ===============================
    public ByteArrayInputStream generarReportePedidos(List<Pedido> pedidos) {

        Document document = new Document(PageSize.A4, 36, 36, 80, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new EncabezadoPiePagina());

            document.open();

            // ===== TÍTULO =====
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph titulo = new Paragraph("Reporte de Pedidos", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingBefore(55);
            titulo.setSpacingAfter(25);
            document.add(titulo);

            // ===== TABLA =====
            PdfPTable table = new PdfPTable(3);   // ← Cambiado a 3 columnas
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.5f, 3.5f, 2.0f});
            table.setSpacingBefore(10);

            // Encabezado
            Color amarilloHeader = new Color(251, 192, 45);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            PdfPCell h1 = new PdfPCell(new Paragraph("MESA", headerFont));
            PdfPCell h2 = new PdfPCell(new Paragraph("DESCRIPCIÓN", headerFont));
            PdfPCell h3 = new PdfPCell(new Paragraph("PRECIO", headerFont));

            PdfPCell[] headers = {h1, h2, h3};

            for (PdfPCell c : headers) {
                c.setBackgroundColor(amarilloHeader);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                c.setPadding(8);
                table.addCell(c);
            }

            // Filas alternadas
            Color rowAlt = new Color(255, 243, 205);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            boolean alternate = false;

            for (Pedido p : pedidos) {

                Color bg = alternate ? rowAlt : Color.WHITE;

                PdfPCell c1 = new PdfPCell(new Paragraph(p.getCliente(), bodyFont));
                PdfPCell c2 = new PdfPCell(new Paragraph(p.getDescripcion(), bodyFont));
                PdfPCell c3 = new PdfPCell(new Paragraph(String.valueOf(p.getPrecio()), bodyFont));

                PdfPCell[] cells = {c1, c2, c3};

                for (PdfPCell c : cells) {
                    c.setBackgroundColor(bg);
                    c.setPadding(6);
                    c.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(c);
                }

                alternate = !alternate;
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
