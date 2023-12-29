package org.lukabrx.qrcodeservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.Set;

public class ApiUtils {

    private static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpeg", "gif");

    public static boolean isValidSize(int size) {
        return size >= 150 && size <= 350;
    }

    public static boolean isValidFormat(String format) {
        return SUPPORTED_FORMATS.contains(format.toLowerCase());
    }

    public static BufferedImage generateQRCodeImage(String contents, int size, String format) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
