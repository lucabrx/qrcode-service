package org.lukabrx.qrcodeservice;


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
    private final ImageGenerator imageGenerator = new ImageGenerator();


    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity<?> getQRCode(@RequestParam int size, @RequestParam String type) throws IOException {
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

        BufferedImage image = imageGenerator.createWhiteImage(size, size);
        MediaType mediaType = MediaType.valueOf("image/" + type.toLowerCase());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, type.toLowerCase(), baos);
            byte[] imageBytes = baos.toByteArray();

            return ResponseEntity.ok().contentType(mediaType).body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

