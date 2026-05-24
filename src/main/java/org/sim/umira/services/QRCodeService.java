package org.sim.umira.services;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class QRCodeService {
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
                hints
        );

        ByteArrayOutputStream pngOutputStream =
                new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                pngOutputStream
        );

        return pngOutputStream.toByteArray();
    }

    public String generateBase64QRCode(String text)
            throws Exception {

        byte[] qr = generateQRCodeImage(text, 300, 300);

        return Base64.getEncoder().encodeToString(qr);
    }
}
