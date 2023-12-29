package org.lukabrx.qrcodeservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
public class ApiController {
    private static final Set<String> SUPPORTED_FORMATS = Set.of("png", "jpeg", "gif");


    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity<?> getQRCode(@RequestParam String contents, @RequestParam int size, @RequestParam String type) throws IOException {
        if(contents == null || contents.trim().isEmpty()) {
            return new ResponseEntity<>(Map.of(
                    "error",
                    "Contents cannot be null or blank"),
                    HttpStatus.BAD_REQUEST);
        }

        if(size < 150 || size > 350) {
            return new ResponseEntity<>(Map.of(
                    "error",
                    "Image size must be between 150 and 350 pixels"),
                    HttpStatus.BAD_REQUEST);
        }
        if (!SUPPORTED_FORMATS.contains(type.toLowerCase())) {
            return new ResponseEntity<>(Map.of(
                    "error",
                    "Only png, jpeg and gif image types are supported"),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            BufferedImage qrImage = generateQRCodeImage(contents, size, type);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, type.toLowerCase(), baos);
            byte[] imageBytes = baos.toByteArray();
            MediaType mediaType = MediaType.valueOf("image/" + type.toLowerCase());
            return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private BufferedImage generateQRCodeImage(String contents, int size, String type) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE,size,size);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
