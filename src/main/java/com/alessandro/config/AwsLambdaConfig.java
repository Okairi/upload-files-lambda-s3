package com.alessandro.config;

import com.alessandro.dto.FileUploadRequest;
import com.alessandro.service.AwsS3Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class AwsLambdaConfig {

    private final AwsS3Service awsS3Service;

    public AwsLambdaConfig(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @Bean
    public Function<FileUploadRequest, String> reciveParams() {
        return (request) -> {
            try {
                byte[] fileBytes = Base64.getDecoder().decode(request.getBase64());
                String result = awsS3Service.uploadFile(fileBytes, request.getFilename(), request.getContentType());
                return "Archivo subido correctamente: " + result;
            } catch (Exception e) {
                return "Error al subir archivo: " + e.getMessage();
            }
        };
    }

    @Bean
    public Supplier<String> greeting(){
        return () -> "Hello World";
    }
}
