package org.sim.umira.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayOutputStream;

import org.sim.umira.dtos.CostControl.ReportProyekDto;

@ApplicationScoped
public class PdfService {

    public byte[] generatePdf(ReportProyekDto project) {

        try {

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(PageSize.A4, 40, 40, 50, 50);

            PdfWriter.getInstance(document, out);

            document.open();

            /*
             * FONT
             */
            Font titleFont = new Font(
                    Font.HELVETICA,
                    18,
                    Font.BOLD
            );

            Font headerFont = new Font(
                    Font.HELVETICA,
                    12,
                    Font.BOLD
            );

            Font normalFont = new Font(
                    Font.HELVETICA,
                    11,
                    Font.NORMAL
            );

            /*
             * TITLE
             */
            Paragraph title = new Paragraph(
                    "LAPORAN PROYEK",
                    titleFont
            );

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            /*
             * PROJECT INFO
             */
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new int[]{30, 70});

            infoTable.addCell(createCell("Nama Proyek", headerFont));
            infoTable.addCell(createCell(project.namaProject, normalFont));

            infoTable.addCell(createCell("Client", headerFont));
            infoTable.addCell(createCell(project.client, normalFont));

            infoTable.addCell(createCell("Tanggal Mulai", headerFont));
            infoTable.addCell(createCell(
                    project.tanggalMulai.toString(),
                    normalFont
            ));

            infoTable.addCell(createCell("Tanggal Selesai", headerFont));
            infoTable.addCell(createCell(
                    project.tanggalSelesai.toString(),
                    normalFont
            ));

            infoTable.addCell(createCell("Progress", headerFont));
            infoTable.addCell(createCell(
                    project.progress + "%",
                    normalFont
            ));

            document.add(infoTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Daftar Pekerjaan", headerFont));
            document.add(new Paragraph(" "));

            /*
             * TASK TABLE
             */
            PdfPTable taskTable = new PdfPTable(4);

            taskTable.setWidthPercentage(100);

            taskTable.setWidths(
                    new int[]{40, 20, 20, 20}
            );

            taskTable.addCell(createHeader("Pekerjaan"));
            taskTable.addCell(createHeader("PIC"));
            taskTable.addCell(createHeader("Status"));
            taskTable.addCell(createHeader("Progress"));

            // for (TaskDto task : project.tasks) {

            //     taskTable.addCell(task.pekerjaan);
            //     taskTable.addCell(task.pic);
            //     taskTable.addCell(task.status);
            //     taskTable.addCell(task.progress + "%");
            // }

            document.add(taskTable);

            document.add(new Paragraph(" "));

            /*
             * FOOTER SUMMARY
             */
            Paragraph summary = new Paragraph(
                    "Total Progress Proyek : "
                            + project.progress + "%",
                    headerFont
            );

            summary.setAlignment(Element.ALIGN_RIGHT);

            document.add(summary);


            document.newPage();

            //  document.add(summary);


            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * HELPER CELL
     */
    private PdfPCell createCell(
            String text,
            Font font
    ) {

        PdfPCell cell =
                new PdfPCell(new Phrase(text, font));

        cell.setPadding(8);

        return cell;
    }

    /*
     * HEADER TABLE
     */
    private PdfPCell createHeader(String text) {

        Font font = new Font(
                Font.HELVETICA,
                11,
                Font.BOLD
        );

        PdfPCell cell =
                new PdfPCell(new Phrase(text, font));

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell.setPadding(8);

        return cell;
    }
}