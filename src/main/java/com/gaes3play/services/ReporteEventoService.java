package com.gaes3play.services;

import com.gaes3play.models.Evento;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReporteEventoService {

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
    public ByteArrayInputStream generarReporteEventos(List<Evento> eventos) {

        Document document = new Document(PageSize.A4, 36, 36, 80, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new EncabezadoPiePagina());

            document.open();

            // ===== TÍTULO PRINCIPAL =====
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.NORMAL, Color.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Eventos", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingBefore(55);
            titulo.setSpacingAfter(25);
            document.add(titulo);

            // ===== TABLA =====
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{3.5f, 2.5f, 3.5f});

            // Encabezado amarillo (igual a Usuarios)
            Color amarilloHeader = new Color(251, 192, 45);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, Color.BLACK);

            PdfPCell h1 = new PdfPCell(new Paragraph("TÍTULO", headerFont));
            PdfPCell h2 = new PdfPCell(new Paragraph("FECHA", headerFont));
            PdfPCell h3 = new PdfPCell(new Paragraph("LOCAL", headerFont));

            h1.setBackgroundColor(amarilloHeader);
            h2.setBackgroundColor(amarilloHeader);
            h3.setBackgroundColor(amarilloHeader);

            h1.setHorizontalAlignment(Element.ALIGN_CENTER);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            h3.setHorizontalAlignment(Element.ALIGN_CENTER);

            h1.setPadding(8);
            h2.setPadding(8);
            h3.setPadding(8);

            table.addCell(h1);
            table.addCell(h2);
            table.addCell(h3);

            // Filas alternadas
            Color rowAlt = new Color(255, 243, 205);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, Color.BLACK);

            boolean alternate = false;

            for (Evento e : eventos) {

                Color bg = alternate ? rowAlt : Color.WHITE;

                PdfPCell c1 = new PdfPCell(new Paragraph(e.getTitulo(), bodyFont));
                PdfPCell c2 = new PdfPCell(new Paragraph(String.valueOf(e.getFecha()), bodyFont));
                PdfPCell c3 = new PdfPCell(new Paragraph(e.getLocal(), bodyFont));

                c1.setBackgroundColor(bg);
                c2.setBackgroundColor(bg);
                c3.setBackgroundColor(bg);

                c1.setPadding(6);
                c2.setPadding(6);
                c3.setPadding(6);

                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                c3.setHorizontalAlignment(Element.ALIGN_LEFT);

                table.addCell(c1);
                table.addCell(c2);
                table.addCell(c3);

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
