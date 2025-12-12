package com.gaes3play.services;

import com.gaes3play.models.Usuario;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReporteService {

    // ENCABEZADO Y PIE DE PÁGINA
    private static class EncabezadoPiePagina extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Fondo del encabezado (gris suave)
            cb.setColorFill(new Color(230, 230, 230));
            cb.rectangle(0, document.getPageSize().getHeight() - 40, document.getPageSize().getWidth(), 40);
            cb.fill();

            // Texto Parrilla Express (marca) - arriba izquierda
            com.lowagie.text.Font marcaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, com.lowagie.text.Font.NORMAL, Color.BLACK);
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
            cb.setColorStroke(new Color(255, 193, 7)); // Amarillo estilo restaurante
            cb.moveTo(document.leftMargin(), document.getPageSize().getHeight() - 42);
            cb.lineTo(document.getPageSize().getWidth() - document.rightMargin(), document.getPageSize().getHeight() - 42);
            cb.stroke();

            // PIE DE PÁGINA (abajo centro) con número de página
            com.lowagie.text.Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, com.lowagie.text.Font.NORMAL, Color.DARK_GRAY);
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

    public ByteArrayInputStream generarReporteUsuarios(List<Usuario> usuarios) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Activamos encabezado y pie de página
            writer.setPageEvent(new EncabezadoPiePagina());

            document.open();

            // TÍTULO PRINCIPAL (centrado)
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, com.lowagie.text.Font.NORMAL, Color.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Usuarios", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingBefore(50);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // TABLA (3 columnas: Nombre, Correo, Rol)
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{3.5f, 4.5f, 2f});

            // Encabezados estilo amarillo
            Color amarilloHeader = new Color(251, 192, 45); // amarillo fuerte
            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, com.lowagie.text.Font.NORMAL, Color.BLACK);

            PdfPCell h1 = new PdfPCell(new Paragraph("USUARIO", headerFont));
            PdfPCell h2 = new PdfPCell(new Paragraph("CORREO", headerFont));
            PdfPCell h3 = new PdfPCell(new Paragraph("ROL", headerFont));

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

            // Filas alternadas (amarillo suave / blanco)
            Color rowAlt = new Color(255, 243, 205); // amarillo suave
            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, com.lowagie.text.Font.NORMAL, Color.BLACK);

            boolean alternate = false;
            for (Usuario u : usuarios) {
                Color bg = alternate ? rowAlt : Color.WHITE;

                PdfPCell c1 = new PdfPCell(new Paragraph(u.getNombre(), bodyFont));
                c1.setBackgroundColor(bg);
                c1.setPadding(6);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Paragraph(u.getCorreo(), bodyFont));
                c2.setBackgroundColor(bg);
                c2.setPadding(6);
                c2.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c2);

                PdfPCell c3 = new PdfPCell(new Paragraph(String.valueOf(u.getRol()), bodyFont));
                c3.setBackgroundColor(bg);
                c3.setPadding(6);
                c3.setHorizontalAlignment(Element.ALIGN_CENTER);
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
