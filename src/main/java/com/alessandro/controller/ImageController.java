package com.alessandro.controller;


import com.alessandro.service.AwsS3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {


    private final AwsS3Service awsS3Service;

    public ImageController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // Validación del tamaño del archivo
        if (file.getSize() > 5 * 1024 * 1024) {  // 5MB
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El archivo es demasiado grande. El tamaño máximo permitido es 5MB.");
        }

        try {
            String imageUrl = awsS3Service.uploadFile(file);
            return ResponseEntity.ok("Archivo subido correctamente: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el archivo: " + e.getMessage());
        }
    }


}
