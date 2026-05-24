package org.sim.umira.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessStreamCache;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFMerge {

    public byte[] mergeUploadedPdf(List<String> files, byte[] cover) throws Exception {

        PDDocument merged = new PDDocument();

        PDFMergerUtility merger = new PDFMergerUtility();

        if (cover != null && cover.length > 0) {

            PDDocument coverDoc = Loader.loadPDF(cover);

            ByteArrayOutputStream coverOutput = new ByteArrayOutputStream();

            coverDoc.setAllSecurityToBeRemoved(true);

            coverDoc.save(coverOutput);

            coverDoc.close();

            PDDocument normalizedCover = Loader.loadPDF(
                    coverOutput.toByteArray());

            merger.appendDocument(
                    merged,
                    normalizedCover);

            normalizedCover.close();
        }

        for (String file : files) {

            File pdfFile = new File(file);

            if (!pdfFile.exists()) {
                continue;
            }

            byte[] pdfBytes = Files.readAllBytes(
                    pdfFile.toPath());

            // LOAD SOURCE
            PDDocument source = Loader.loadPDF(pdfBytes);

            // NORMALIZE SOURCE
            ByteArrayOutputStream normalizedOutput = new ByteArrayOutputStream();

            source.setAllSecurityToBeRemoved(true);

            source.save(normalizedOutput);

            source.close();

            // RELOAD NORMALIZED PDF
            PDDocument normalizedDoc = Loader.loadPDF(
                    normalizedOutput.toByteArray());

            // APPEND
            merger.appendDocument(
                    merged,
                    normalizedDoc);

            normalizedDoc.close();
        }

        ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();

        // FINAL NORMALIZE
        merged.setAllSecurityToBeRemoved(true);

        merged.save(finalOutput);

        merged.close();

        return finalOutput.toByteArray();
    }

}
