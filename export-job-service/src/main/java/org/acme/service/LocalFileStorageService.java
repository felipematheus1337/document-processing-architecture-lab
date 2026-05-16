package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class LocalFileStorageService {

    @ConfigProperty(name = "export.path", defaultValue = "./exports")
    String exportPath;

    public Path save(String fileName, String content) {
        try {
            Path directory = Path.of(exportPath);
            Files.createDirectories(directory);

            Path filePath = directory.resolve(fileName);
            Files.writeString(filePath, content);

            return filePath;
        } catch (IOException e) {
            throw new IllegalStateException("Error saving export file locally", e);
        }
    }
}