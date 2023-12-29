package org.lukabrx.qrcodeservice;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class ApiController {
    private final ImageGenerator imageGenerator = new ImageGenerator();


    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity<byte[]> getQRCode() {
        BufferedImage image = imageGenerator.createWhiteImage(250, 250);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
