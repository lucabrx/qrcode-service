package org.lukabrx.qrcodeservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.http.ResponseEntity;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApiUtils {
    public static final Set<String> SUPPORTED_CORRECTIONS = Set.of("L", "M", "Q", "H");

    public static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpeg", "gif");

    public static boolean isValidSize(int size) {
        return size >= 150 && size <= 350;
    }

    public static boolean isValidFormat(String format) {
        return SUPPORTED_FORMATS.contains(format.toLowerCase());
    }

    public static BufferedImage generateQRCodeImage(String contents, int size, String format, String correction) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        switch (correction.toUpperCase()) {
            case "L":
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                break;
            case "M":
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                break;
            case "Q":
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
                break;
            case "H":
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                break;
        }
        BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static ResponseEntity<Map<String, String>> validateParameters(String contents, int size, String type, String correction) {
        if (contents == null || contents.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contents cannot be null or blank"));
        }
        if (!ApiUtils.isValidSize(size)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image size must be between 150 and 350 pixels"));
        }
        if (!ApiUtils.SUPPORTED_CORRECTIONS.contains(correction.toUpperCase())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Permitted error correction levels are L, M, Q, H"));
        }
        if (!ApiUtils.isValidFormat(type)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only png, jpeg and gif image types are supported"));
        }
        return null;
    }
}
