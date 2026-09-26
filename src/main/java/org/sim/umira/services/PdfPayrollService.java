package org.sim.umira.services;



import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.enterprise.context.ApplicationScoped;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@ApplicationScoped
public class PdfPayrollService {

    private static final Color PURPLE =
            new Color(99, 88, 200);

    private static final Color GREEN =
            new Color(25, 190, 90);

    private static final Color RED =
            new Color(240, 60, 60);

    private static final Color TEXT =
            new Color(35, 38, 50);

    private String rupiah(BigDecimal value) {

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        NumberFormat format =
                NumberFormat.getNumberInstance(
                        new Locale("id", "ID")
                );

        format.setMaximumFractionDigits(0);

        return "Rp " + format.format(value);
    }

    public byte[] generate(PayrollData data) {

        try {

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            35,
                            35,
                            35,
                            35
                    );

            PdfWriter.getInstance(
                    document,
                    output
            );

            document.open();

            Font titleFont =
                    new Font(
                            Font.HELVETICA,
                            20,
                            Font.BOLD,
                            TEXT
                    );

            Font normalFont =
                    new Font(
                            Font.HELVETICA,
                            10,
                            Font.NORMAL,
                            TEXT
                    );

            Font boldFont =
                    new Font(
                            Font.HELVETICA,
                            10,
                            Font.BOLD,
                            TEXT
                    );

            Font smallFont =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.NORMAL,
                            new Color(140, 140, 170)
                    );

            /*
             * ============================
             * HEADER
             * ============================
             */

            PdfPTable employeeTable =
                    new PdfPTable(3);

            employeeTable.setWidthPercentage(100);

            employeeTable.setWidths(
                    new float[]{1, 1, 1}
            );

            employeeTable.setSpacingAfter(15);

            addEmployeeColumn(
                    employeeTable,
                    "Nama",
                    data.nama,
                    boldFont
            );

            addEmployeeColumn(
                    employeeTable,
                    "Jabatan",
                    data.jabatan,
                    boldFont
            );

            addEmployeeColumn(
                    employeeTable,
                    "Departemen",
                    data.departemen,
                    boldFont
            );

            document.add(employeeTable);

            /*
             * ============================
             * STATISTIC
             * ============================
             */

            PdfPTable stats =
                    new PdfPTable(4);

            stats.setWidthPercentage(100);

            stats.setWidths(
                    new float[]{1, 1, 1, 1}
            );

        //     stats.setSpacingAfter(18);

        //     addStat(
        //             stats,
        //             String.valueOf(data.hariKerja),
        //             "Hari Kerja",
        //             GREEN,
        //             titleFont,
        //             smallFont
        //     );

        //     addStat(
        //             stats,
        //             String.valueOf(data.izin),
        //             "Izin",
        //             PURPLE,
        //             titleFont,
        //             smallFont
        //     );

        //     addStat(
        //             stats,
        //             String.valueOf(data.sakit),
        //             "Sakit",
        //             new Color(245, 150, 0),
        //             titleFont,
        //             smallFont
        //     );

        //     addStat(
        //             stats,
        //             String.valueOf(data.alpha),
        //             "Alpha",
        //             RED,
        //             titleFont,
        //             smallFont
        //     );

        //     document.add(stats);

            /*
             * ============================
             * PENDAPATAN / POTONGAN
             * ============================
             */

            PdfPTable main =
                    new PdfPTable(2);

            main.setWidthPercentage(100);

            main.setWidths(
                    new float[]{1, 1}
            );

            PdfPCell incomeCell =
                    new PdfPCell();

            incomeCell.setBorder(
                    Rectangle.NO_BORDER
            );

            PdfPCell deductionCell =
                    new PdfPCell();

            deductionCell.setBorder(
                    Rectangle.NO_BORDER
            );

            /*
             * PENDAPATAN
             */

            Paragraph incomeTitle =
                    new Paragraph(
                            "+  Pendapatan",
                            new Font(
                                    Font.HELVETICA,
                                    12,
                                    Font.BOLD,
                                    GREEN
                            )
                    );

            incomeCell.addElement(incomeTitle);

            addMoneyRow(
                    incomeCell,
                    "Gaji Pokok",
                    data.gajiPokok,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Jabatan",
                    data.tjJabatan,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Operasional",
                    data.tjOperasional,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Transport",
                    data.tjTransport,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Pulsa",
                    data.tjPulsa,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Makan",
                    data.tjMakan,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Lembur",
                    data.tjLembur,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "Tj. Lainnya",
                    data.tjLainnya,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "BPJS Kesehatan",
                    data.bpjsKesehatanPendapatan,
                    normalFont
            );

            addMoneyRow(
                    incomeCell,
                    "BPJS Ketenagakerjaan",
                    data.bpjsKetenagakerjaanPendapatan,
                    normalFont
            );

            addTotal(
                    incomeCell,
                    "Total Pendapatan",
                    data.totalPendapatan,
                    boldFont
            );

            /*
             * POTONGAN
             */

            Paragraph deductionTitle =
                    new Paragraph(
                            "−  Potongan",
                            new Font(
                                    Font.HELVETICA,
                                    12,
                                    Font.BOLD,
                                    RED
                            )
                    );

            deductionCell.addElement(
                    deductionTitle
            );

            addMoneyRow(
                    deductionCell,
                    "Potongan Kehadiran",
                    data.potonganKehadiran,
                    normalFont
            );

            addMoneyRow(
                    deductionCell,
                    "Pinjaman",
                    data.pinjaman,
                    normalFont
            );

            addMoneyRow(
                    deductionCell,
                    "BPJS Kesehatan",
                    data.bpjsKesehatan,
                    normalFont
            );

            addMoneyRow(
                    deductionCell,
                    "BPJS Ketenagakerjaan",
                    data.bpjsKetenagakerjaan,
                    normalFont
            );

            addMoneyRow(
                    deductionCell,
                    "Potongan Lainnya",
                    data.potonganLainnya,
                    normalFont
            );

            addMoneyRow(
                    deductionCell,
                    "PPh21",
                    data.pph21,
                    normalFont
            );

            addTotal(
                    deductionCell,
                    "Total Potongan",
                    data.totalPotongan,
                    boldFont
            );

            main.addCell(incomeCell);
            main.addCell(deductionCell);

            document.add(main);

            /*
             * ============================
             * TAKE HOME PAY
             * ============================
             */

            PdfPTable takeHome =
                    new PdfPTable(1);

            takeHome.setWidthPercentage(100);

            takeHome.setSpacingBefore(20);

            PdfPCell takeHomeCell =
                    new PdfPCell();

            takeHomeCell.setBackgroundColor(
                    PURPLE
            );

            takeHomeCell.setPadding(15);

            takeHomeCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            Paragraph takeHomeTitle =
                    new Paragraph(
                            "TAKE HOME PAY",
                            new Font(
                                    Font.HELVETICA,
                                    10,
                                    Font.NORMAL,
                                    Color.WHITE
                            )
                    );

            takeHomeTitle.setAlignment(
                    Element.ALIGN_CENTER
            );

            takeHomeCell.addElement(
                    takeHomeTitle
            );

            Paragraph amount =
                    new Paragraph(
                            rupiah(data.takeHomePay),
                            new Font(
                                    Font.HELVETICA,
                                    20,
                                    Font.BOLD,
                                    Color.WHITE
                            )
                    );

            amount.setAlignment(
                    Element.ALIGN_CENTER
            );

            takeHomeCell.addElement(amount);

            takeHome.addCell(
                    takeHomeCell
            );

            document.add(takeHome);

            /*
             * FOOTER
             */

            Paragraph footer =
                    new Paragraph(
                            "Slip gaji ini digenerate secara otomatis oleh sistem.",
                            smallFont
                    );

            footer.setAlignment(
                    Element.ALIGN_CENTER
            );

            footer.setSpacingBefore(15);

            document.add(footer);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Gagal generate PDF payroll",
                    e
            );
        }
    }

    private void addEmployeeColumn(
            PdfPTable table,
            String label,
            String value,
            Font valueFont
    ) {

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(10);
        cell.setBorder(
                Rectangle.BOX
        );

        cell.setBorderColor(
                new Color(225, 227, 240)
        );

        Paragraph labelParagraph =
                new Paragraph(
                        label,
                        new Font(
                                Font.HELVETICA,
                                9,
                                Font.NORMAL,
                                new Color(140, 140, 170)
                        )
                );

        Paragraph valueParagraph =
                new Paragraph(
                        value,
                        valueFont
                );

        cell.addElement(
                labelParagraph
        );

        cell.addElement(
                valueParagraph
        );

        table.addCell(cell);
    }

    private void addStat(
            PdfPTable table,
            String value,
            String label,
            Color color,
            Font valueFont,
            Font labelFont
    ) {

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(10);

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setBorder(
                Rectangle.BOX
        );

        cell.setBorderColor(
                new Color(225, 227, 240)
        );

        Paragraph valueParagraph =
                new Paragraph(
                        value,
                        new Font(
                                Font.HELVETICA,
                                18,
                                Font.BOLD,
                                color
                        )
                );

        valueParagraph.setAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph labelParagraph =
                new Paragraph(
                        label,
                        labelFont
                );

        labelParagraph.setAlignment(
                Element.ALIGN_CENTER
        );

        cell.addElement(
                valueParagraph
        );

        cell.addElement(
                labelParagraph
        );

        table.addCell(cell);
    }

    private void addMoneyRow(
            PdfPCell parent,
            String label,
            BigDecimal value,
            Font font
    ) {

        PdfPTable row =
                new PdfPTable(2);

        row.setWidthPercentage(100);

        row.setWidths(
                new float[]{65, 35}
        );

        PdfPCell labelCell =
                new PdfPCell(
                        new Phrase(label, font)
                );

        labelCell.setBorder(
                Rectangle.NO_BORDER
        );

        PdfPCell valueCell =
                new PdfPCell(
                        new Phrase(
                                rupiah(value),
                                font
                        )
                );

        valueCell.setBorder(
                Rectangle.NO_BORDER
        );

        valueCell.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        row.addCell(labelCell);
        row.addCell(valueCell);

        parent.addElement(row);
    }

    private void addTotal(
            PdfPCell parent,
            String label,
            BigDecimal value,
            Font font
    ) {

        PdfPTable row =
                new PdfPTable(2);

        row.setWidthPercentage(100);

        row.setWidths(
                new float[]{65, 35}
        );

        PdfPCell labelCell =
                new PdfPCell(
                        new Phrase(label, font)
                );

        labelCell.setBorder(
                Rectangle.TOP
        );

        labelCell.setBorderColor(
                new Color(220, 220, 230)
        );

        PdfPCell valueCell =
                new PdfPCell(
                        new Phrase(
                                rupiah(value),
                                font
                        )
                );

        valueCell.setBorder(
                Rectangle.TOP
        );

        valueCell.setBorderColor(
                new Color(220, 220, 230)
        );

        valueCell.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        row.addCell(labelCell);
        row.addCell(valueCell);

        parent.addElement(row);
    }
}