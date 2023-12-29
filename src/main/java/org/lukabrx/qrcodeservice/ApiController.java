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
    public ResponseEntity<?> getQRCode(
            @RequestParam String contents,
            @RequestParam(defaultValue = "250") int size,
            @RequestParam(defaultValue = "png") String type,
            @RequestParam(defaultValue = "L") String correction
    )  {

        ResponseEntity<Map<String, String>> validationResult = ApiUtils.validateParameters(contents, size, type, correction);
        if (validationResult != null) {
            return validationResult;
        }


        try {
            BufferedImage qrImage = ApiUtils.generateQRCodeImage(contents, size, type, correction);
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

