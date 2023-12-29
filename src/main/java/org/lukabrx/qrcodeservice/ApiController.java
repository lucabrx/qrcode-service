package org.lukabrx.qrcodeservice;

import com.google.zxing.WriterException;

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

@RestController
public class ApiController {
    @GetMapping("/api/health")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity<?> getQRCode(@RequestParam String contents, @RequestParam int size, @RequestParam String type)  {
        if (contents == null || contents.trim().isEmpty()) {
            return new ResponseEntity<>(Map.of(
                    "error",
                    "Contents cannot be null or blank"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!ApiUtils.isValidSize(size)) {
            return new ResponseEntity<>(Map.of(
                    "error",
                    "Image size must be between 150 and 350 pixels"),
                    HttpStatus.BAD_REQUEST);
        }
        if (!ApiUtils.isValidFormat(type)) {
            return new ResponseEntity<>(Map.of(
                    "error",
                    "Only png, jpeg and gif image types are supported"),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            BufferedImage qrImage = ApiUtils.generateQRCodeImage(contents, size, type);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, type.toLowerCase(), byteStream);
            byte[] imageBytes = byteStream.toByteArray();
            MediaType mediaType = MediaType.valueOf("image/" + type.toLowerCase());
            return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

