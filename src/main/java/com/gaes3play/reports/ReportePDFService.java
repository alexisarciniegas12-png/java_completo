package com.gaes3play.reports;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportePDFService {

    public ByteArrayInputStream generarPdf(String titulo, List<Map<String, String>> filas, List<String> columnas) {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph tituloParrafo = new Paragraph(titulo, fontTitulo);
            tituloParrafo.setAlignment(Element.ALIGN_CENTER);
            tituloParrafo.setSpacingAfter(20);
            document.add(tituloParrafo);

            // Tabla
            PdfPTable table = new PdfPTable(columnas.size());
            table.setWidthPercentage(100);

            // Encabezados
            for (String col : columnas) {
                table.addCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            }

            // Filas
            for (Map<String, String> fila : filas) {
                for (String col : columnas) {
                    table.addCell(fila.getOrDefault(col, ""));
                }
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
