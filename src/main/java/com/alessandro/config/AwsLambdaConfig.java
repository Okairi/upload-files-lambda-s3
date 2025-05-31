package com.alessandro.config;

import com.alessandro.entity.PresignRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.asm.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class AwsLambdaConfig {

    private static final String BUCKET_NAME = "imagenes-portfolio-alessandro";



    @Bean
    public Function<Map<String, Object>, Map<String, String>> generatePresignedUrl() {
        return (event) -> {
            System.out.println("DEBUG QUERY: " + event);

            String rawBody = (String) event.get("body");

            ObjectMapper mapper = new ObjectMapper();
            PresignRequest query;
            try {
                query = mapper.readValue(rawBody, PresignRequest.class);
                System.out.println("Query real : " + query);
            } catch (Exception e) {

                System.out.println("Error parsing body: " + e.getMessage());
                return Map.of(
                        "status", "error",
                        "message", "Invalid JSON body"
                );

            }


            String key = query.key;
            String contentType = query.contentType;





            if (key == null || contentType == null || !contentType.startsWith("image/")) {
                return Map.of(
                        "status", "error",
                        "message", "Invalid or missing key or contentType"
                );
            }

            try (S3Presigner presigner = S3Presigner.builder()
                    .region(Region.of("us-east-1"))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build()) {

                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .contentType(contentType)
                        .build();

                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(objectRequest)
                        .build();

                URL url = presigner.presignPutObject(presignRequest).url();

                return Map.of(
                        "status", "success",
                        "uploadUrl", url.toString(),
                        "key", key
                );

            } catch (Exception e) {
                return Map.of(
                        "status", "error",
                        "message", "Error generating presigned URL: " + e.getMessage()
                );
            }
        };
    }
}