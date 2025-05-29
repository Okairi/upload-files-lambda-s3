package com.alessandro;

import com.alessandro.dto.FileUploadRequest;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class LambdaHandler extends SpringBootRequestHandler<FileUploadRequest, String> {
}
