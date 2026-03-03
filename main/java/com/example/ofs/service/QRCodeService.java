package com.example.ofs.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Slf4j
@Service
public class QRCodeService {

    public String generateQRCode(String username) {

        if (username == null || username.trim().isEmpty()) {
            log.error("Cannot generate QR code: Username is null or empty");
            return null;
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();


            BitMatrix bitMatrix = qrCodeWriter.encode(username, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            log.info("Successfully generated QR code for user: {}", username);


            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            log.error("CRITICAL: Failed to generate QR code for {}. Error: {}", username, e.getMessage());
            return null;
        }
    }
}