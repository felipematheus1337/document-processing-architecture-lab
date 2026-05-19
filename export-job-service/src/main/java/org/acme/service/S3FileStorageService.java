package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@ApplicationScoped
public class S3FileStorageService {

    private static final Logger LOG = Logger.getLogger(S3FileStorageService.class);

    private final S3Client s3Client;

    @ConfigProperty(name = "aws.s3.bucket-name")
    String bucketName;

    @ConfigProperty(name = "aws.s3.export-prefix", defaultValue = "exports")
    String exportPrefix;

    public S3FileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String save(String fileName, String content) {
        String objectKey = buildObjectKey(fileName);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("application/json")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromString(content, StandardCharsets.UTF_8)
        );

        LOG.infof(
                "event=s3_file_saved bucket=%s objectKey=%s sizeBytes=%d",
                bucketName,
                objectKey,
                content.getBytes(StandardCharsets.UTF_8).length
        );

        return "s3://" + bucketName + "/" + objectKey;
    }

    private String buildObjectKey(String fileName) {
        LocalDate today = LocalDate.now();

        return "%s/year=%d/month=%02d/day=%02d/%s".formatted(
                exportPrefix,
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth(),
                fileName
        );
    }
}
