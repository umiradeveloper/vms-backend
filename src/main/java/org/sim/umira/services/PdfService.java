package org.sim.umira.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sim.umira.dtos.CostControl.ReportProyekDto;
import org.sim.umira.entities.ChecklistTransaksi.JenisTransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiProyekDetailPersetujuanEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiProyekEntity;

@ApplicationScoped
public class PdfService {

        public byte[] generatePdf(ReportProyekDto project) {

                try {

                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        Document document = new Document(PageSize.A4, 40, 40, 50, 50);

                        PdfWriter.getInstance(document, out);

                        document.open();

                        /*
                         * FONT
                         */
                        Font titleFont = new Font(
                                        Font.HELVETICA,
                                        18,
                                        Font.BOLD);

                        Font headerFont = new Font(
                                        Font.HELVETICA,
                                        12,
                                        Font.BOLD);

                        Font normalFont = new Font(
                                        Font.HELVETICA,
                                        11,
                                        Font.NORMAL);

                        /*
                         * TITLE
                         */
                        Paragraph title = new Paragraph(
                                        "LAPORAN PROYEK",
                                        titleFont);

                        title.setAlignment(Element.ALIGN_CENTER);

                        document.add(title);

                        document.add(new Paragraph(" "));

                        /*
                         * PROJECT INFO
                         */
                        PdfPTable infoTable = new PdfPTable(2);
                        infoTable.setWidthPercentage(100);
                        infoTable.setWidths(new int[] { 30, 70 });

                        infoTable.addCell(createCell("Nama Proyek", headerFont));
                        infoTable.addCell(createCell(project.namaProject, normalFont));

                        infoTable.addCell(createCell("Client", headerFont));
                        infoTable.addCell(createCell(project.client, normalFont));

                        infoTable.addCell(createCell("Tanggal Mulai", headerFont));
                        infoTable.addCell(createCell(
                                        project.tanggalMulai.toString(),
                                        normalFont));

                        infoTable.addCell(createCell("Tanggal Selesai", headerFont));
                        infoTable.addCell(createCell(
                                        project.tanggalSelesai.toString(),
                                        normalFont));

                        infoTable.addCell(createCell("Progress", headerFont));
                        infoTable.addCell(createCell(
                                        project.progress + "%",
                                        normalFont));

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
                                        new int[] { 40, 20, 20, 20 });

                        taskTable.addCell(createHeader("Pekerjaan"));
                        taskTable.addCell(createHeader("PIC"));
                        taskTable.addCell(createHeader("Status"));
                        taskTable.addCell(createHeader("Progress"));

                        // for (TaskDto task : project.tasks) {

                        // taskTable.addCell(task.pekerjaan);
                        // taskTable.addCell(task.pic);
                        // taskTable.addCell(task.status);
                        // taskTable.addCell(task.progress + "%");
                        // }

                        document.add(taskTable);

                        document.add(new Paragraph(" "));

                        /*
                         * FOOTER SUMMARY
                         */
                        Paragraph summary = new Paragraph(
                                        "Total Progress Proyek : "
                                                        + project.progress + "%",
                                        headerFont);

                        summary.setAlignment(Element.ALIGN_RIGHT);

                        document.add(summary);

                        document.newPage();

                        // document.add(summary);

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
                        Font font) {

                PdfPCell cell = new PdfPCell(new Phrase(text, font));

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
                                Font.BOLD);

                PdfPCell cell = new PdfPCell(new Phrase(text, font));

                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPadding(8);

                return cell;
        }

        public byte[] generateFormDisposisi(TransaksiEntity trx, java.util.List<JenisTransaksiEntity> jenisTrx,
                        TransaksiProyekEntity trxProyek) {
                try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        Document document = new Document(
                                        PageSize.A4,
                                        30,
                                        30,
                                        30,
                                        30);

                        PdfWriter.getInstance(document, out);

                        document.open();

                        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);

                        Font normalFont = new Font(Font.HELVETICA, 10);

                        Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);

                        Paragraph company = new Paragraph(
                                        "PT UMIRA SINERGI GLOBAL",
                                        titleFont);

                        company.setAlignment(Element.ALIGN_CENTER);

                        document.add(company);

                        Paragraph project = new Paragraph(
                                        trx.proyek,
                                        boldFont);

                        project.setAlignment(Element.ALIGN_CENTER);

                        document.add(project);
                        Paragraph address = new Paragraph(
                                        "Bali Office : SOHO Dharmawangsa Hills 5A, Jl Dharmawangsa, Benoa, Kec. Kuta Selatan, Badung, Bali - 80362",
                                        normalFont);

                        address.setAlignment(Element.ALIGN_CENTER);

                        document.add(address);

                        document.add(new Paragraph(" "));

                        PdfPTable infoTable = new PdfPTable(4);

                        infoTable.setWidthPercentage(100);

                        infoTable.setWidths(new float[] { 25, 25, 25, 25 });

                        // String iso = date.toString();
                        String tanggal_inv = "-";
                        if(trx.tanggal_invoice != null){
                                tanggal_inv = trx.tanggal_invoice.format(
                                        DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"))
                                );
                        }

                        String tanggal_terima = "-";
                        if(trx.approved_at != null){
                                tanggal_terima = trx.approved_at.format(
                                        DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"))
                                );
                        }

                        String tanggal_jatuh_tempo = "-";
                        if(trx.tanggal_jatuh_tempo_after_verified != null){
                                tanggal_jatuh_tempo = trx.tanggal_jatuh_tempo_after_verified.format(
                                        DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"))
                                );
                        }
                       

                        addCell(infoTable, "Nama Supplier", boldFont);
                        addCell(infoTable, ": " + trx.nama_vendor, normalFont);
                        addCell(infoTable, "No Invoice", boldFont);
                        addCell(infoTable, ": " + trx.nomor_invoice, normalFont);

                        addCell(infoTable, "No Order / Kontrak", boldFont);
                        addCell(infoTable, ": " + trx.no_po_kontrak, normalFont);
                        addCell(infoTable, "Tanggal Invoice", boldFont);
                        addCell(infoTable, ": " + tanggal_inv, normalFont);

                        addCell(infoTable, "Tanggal Terima", boldFont);
                        addCell(infoTable, ": " + tanggal_terima, normalFont);
                        addCell(infoTable, "Tanggal Jatuh Tempo", boldFont);
                        addCell(infoTable, ": " + tanggal_jatuh_tempo, normalFont);

                        addCell(infoTable, "Nilai Invoice", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.nilai_invoice), normalFont);
                        addCell(infoTable, "PPN", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.ppn), normalFont);

                        addCell(infoTable, "PPH", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.pph), normalFont);
                        addCell(infoTable, "Retensi", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.retensi), normalFont);

                        addCell(infoTable, "Kasbon", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.kasbon), normalFont);
                        addCell(infoTable, "Potongan Biaya Lainnya", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.biaya_potongan_lainnya), normalFont);

                        addCell(infoTable, "Nilai Invoice Netto", boldFont);
                        addCell(infoTable, ": " + toRupiah(trx.nilai_invoice_bersih), normalFont);
                        addCell(infoTable, "", boldFont);
                        addCell(infoTable, "", normalFont);

                        document.add(infoTable);

                        document.add(new Paragraph(" "));

                        Paragraph descTitle = new Paragraph(
                                        "Kelengkapan Dokumen Invoice",
                                        boldFont);

                        document.add(descTitle);

                        PdfPTable checklistTable = new PdfPTable(2);

                        checklistTable.setWidthPercentage(100);

                        checklistTable.setWidths(new float[] { 10, 90 });

                        document.add(new Paragraph(" "));

                        if (trx.detailTransaksi.size() > 0 && jenisTrx.size() > 0) {
                                String[] checklists = jenisTrx.stream().map(x -> x.nama_transaksi)
                                                .toArray(String[]::new);
                                Set<String> existNames = trx.detailTransaksi.stream().map(x -> x.pertanyaan)
                                                .collect(Collectors.toSet());
                                for (String item : checklists) {

                                        boolean exists = existNames.contains(item);
                                        addCell(checklistTable, exists ? "[x]" : "[ ]", normalFont);
                                        addCell(checklistTable, item, normalFont);
                                }
                        }

                        document.add(checklistTable);

                        document.add(new Paragraph(" "));

                        if (trxProyek != null) {
                                if (trxProyek.pengajuanTransaksi.size() > 0) {
                                        Paragraph descTitleApprovalProyek = new Paragraph(
                                                        "Tanda Terima Berkas Proyek",
                                                        boldFont);
                                        descTitleApprovalProyek.setAlignment(Element.ALIGN_CENTER);
                                        document.add(descTitleApprovalProyek);
                                        document.add(new Paragraph(" "));
                                        int maxColumn = 3;

                                        PdfPTable approvalTable = null;

                                        for (int i = 0; i < trxProyek.pengajuanTransaksi.size(); i++) {

                                                // CREATE NEW ROW TABLE
                                                if (i % maxColumn == 0) {

                                                        if (approvalTable != null) {

                                                                document.add(approvalTable);

                                                                document.add(
                                                                                new Paragraph(" "));
                                                        }

                                                        int remain = trxProyek.pengajuanTransaksi.size() - i;

                                                        int column = Math.min(remain, maxColumn);

                                                        approvalTable = new PdfPTable(column);

                                                        approvalTable.setWidthPercentage(100);

                                                        float[] widths = new float[column];

                                                        Arrays.fill(
                                                                        widths,
                                                                        100f / column);

                                                        approvalTable.setWidths(widths);
                                                }

                                                TransaksiProyekDetailPersetujuanEntity approval = trxProyek.pengajuanTransaksi
                                                                .get(i);

                                                PdfPCell cell = new PdfPCell();

                                                cell.setFixedHeight(170);

                                                cell.setVerticalAlignment(
                                                                Element.ALIGN_MIDDLE);

                                                Paragraph title = new Paragraph(
                                                                (approval.status_approver.equals("Pengajuan"))
                                                                                ? "Di Ajukan Oleh"
                                                                                : (approval.status_approver.equals(
                                                                                                "Approve")) ? "Mengetahui"
                                                                                                                : "-" + "\n\n",
                                                                normalFont);

                                                title.setAlignment(
                                                                Element.ALIGN_CENTER);
                                                title.setSpacingAfter(10f);

                                                cell.addElement(title);

                                                byte[] qrBytes = generateQRCodeImage(
                                                                trx.id_transaksi + "|" + approval.tanggal_persetujuan
                                                                                + "|" + approval.nama_persetujuan,
                                                                300,
                                                                150);

                                                Image qrImage = Image.getInstance(qrBytes);

                                                qrImage.scaleToFit(80, 80);

                                                qrImage.setAlignment(
                                                                Image.ALIGN_CENTER);

                                                cell.addElement(qrImage);

                                                Paragraph name = new Paragraph(
                                                                "\n" + approval.nama_persetujuan,
                                                                normalFont);

                                                name.setAlignment(
                                                                Element.ALIGN_CENTER);

                                                cell.addElement(name);

                                                Paragraph jabatan = new Paragraph(
                                                                approval.jabatan_persetujuan, // <-- FIELD BARU
                                                                normalFont);

                                                jabatan.setAlignment(Element.ALIGN_CENTER);

                                                cell.addElement(jabatan);

                                                Paragraph tanggal = new Paragraph(
                                                                approval.tanggal_persetujuan != null
                                                                                ? approval.tanggal_persetujuan
                                                                                                .toString()
                                                                                : "-",
                                                                normalFont);

                                                tanggal.setAlignment(Element.ALIGN_CENTER);

                                                cell.addElement(tanggal);

                                                approvalTable.addCell(cell);
                                        }

                                        // LAST TABLE
                                        if (approvalTable != null) {

                                                document.add(approvalTable);
                                        }
                                }
                        }

                        Paragraph descTitleApproval = new Paragraph(
                                        "Tanda Terima Berkas Finance HO",
                                        boldFont);
                        descTitleApproval.setAlignment(Element.ALIGN_CENTER);
                        document.add(descTitleApproval);
                        document.add(new Paragraph(" "));
                        PdfPTable approvalTableHO = new PdfPTable(1);
                        ;
                        PdfPCell cellApporvalHO = new PdfPCell();

                        cellApporvalHO.setFixedHeight(170);

                        cellApporvalHO.setVerticalAlignment(
                                        Element.ALIGN_MIDDLE);

                        String status_pengajuan = "-";
                        if (trx.status_pengajuan != null) {
                                status_pengajuan = trx.status_pengajuan;
                        }

                        Paragraph title = new Paragraph(
                                        (status_pengajuan.equals("Pengajuan")) ? "Di Ajukan Oleh"
                                                        : (status_pengajuan.equals("Verified")) ? "Di Verifikasi Oleh"
                                                                        : "-" + "\n\n",
                                        normalFont);

                        title.setAlignment(
                                        Element.ALIGN_CENTER);
                        title.setSpacingAfter(10f);

                        cellApporvalHO.addElement(title);

                        byte[] qrBytes = generateQRCodeImage(
                                        trx.id_transaksi + "|" + trx.approved_at,
                                        300,
                                        150);

                        Image qrImage = Image.getInstance(qrBytes);

                        qrImage.scaleToFit(80, 80);

                        qrImage.setAlignment(
                                        Image.ALIGN_CENTER);

                        cellApporvalHO.addElement(qrImage);

                        // System.out.println(trx.approvedBy.username);
                        String username = "-";

                        if (trx.approvedBy != null) {
                                if (trx.approvedBy.username != null) {
                                        username = trx.approvedBy.username;
                                }
                        }

                        Paragraph name = new Paragraph(
                                        "\n" + username,
                                        normalFont);

                        name.setAlignment(
                                        Element.ALIGN_CENTER);

                        cellApporvalHO.addElement(name);

                        String role = "-";
                        if (trx.approvedBy != null) {
                                if (trx.approvedBy.username != null) {
                                        role = trx.approvedBy.role.nama_role;
                                }
                        }

                        Paragraph jabatan = new Paragraph(
                                        role, // <-- FIELD BARU
                                        normalFont);

                        jabatan.setAlignment(Element.ALIGN_CENTER);

                        cellApporvalHO.addElement(jabatan);

                        Paragraph tanggal = new Paragraph(
                                        trx.approved_at != null
                                                        ? trx.approved_at.toString()
                                                        : "-",
                                        normalFont);

                        tanggal.setAlignment(Element.ALIGN_CENTER);

                        cellApporvalHO.addElement(tanggal);

                        approvalTableHO.addCell(cellApporvalHO);

                        document.add(approvalTableHO);

                        // approvalTable.addCell(cell1);
                        // approvalTable.addCell(cell2);
                        // approvalTable.addCell(cell3);

                        // document.add(approvalTable);

                        document.close();

                        return out.toByteArray();

                } catch (Exception e) {
                        e.printStackTrace();
                        return new byte[0];
                        // TODO: handle exception
                }
        }

        private void addCell(
                        PdfPTable table,
                        String text,
                        Font font) {

                PdfPCell cell = new PdfPCell(
                                new Phrase(text, font));

                cell.setPadding(5);

                table.addCell(cell);
        }

        public byte[] generateQRCodeImage(String text, int width, int height)
                        throws Exception {

                QRCodeWriter qrCodeWriter = new QRCodeWriter();

                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.MARGIN, 1);

                BitMatrix bitMatrix = qrCodeWriter.encode(
                                text,
                                BarcodeFormat.QR_CODE,
                                width,
                                height,
                                hints);

                ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

                MatrixToImageWriter.writeToStream(
                                bitMatrix,
                                "PNG",
                                pngOutputStream);

                return pngOutputStream.toByteArray();
        }

        public String generateBase64QRCode(String text)
                        throws Exception {

                byte[] qr = generateQRCodeImage(text, 300, 300);

                return Base64.getEncoder().encodeToString(qr);
        }

        public static String toRupiah(BigInteger value) {
                if (value == null)
                        return "Rp0";

                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                return format.format(value);
        }

        public class Approval {

                public String title;
                public String name;
                public String qr;

                public Approval(
                                String title,
                                String name,
                                String qr) {

                        this.title = title;
                        this.name = name;
                        this.qr = qr;
                }
        }
}