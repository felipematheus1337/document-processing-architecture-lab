package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class StorageServiceResolver {

    private final LocalFileStorageService localFileStorageService;
    private final S3FileStorageService s3FileStorageService;

    @ConfigProperty(name = "storage.provider", defaultValue = "local")
    String storageProvider;

    public StorageServiceResolver(
            LocalFileStorageService localFileStorageService,
            S3FileStorageService s3FileStorageService
    ) {
        this.localFileStorageService = localFileStorageService;
        this.s3FileStorageService = s3FileStorageService;
    }

    public String save(String fileName, String content) {
        if ("s3".equalsIgnoreCase(storageProvider)) {
            return s3FileStorageService.save(fileName, content);
        }

        return localFileStorageService.save(fileName, content);
    }
}
