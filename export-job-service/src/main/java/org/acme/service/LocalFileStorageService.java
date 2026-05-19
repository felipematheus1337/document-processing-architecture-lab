package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LocalFileStorageService implements StorageService {

    private static final Logger LOG = Logger.getLogger(LocalFileStorageService.class);

    @ConfigProperty(name = "export.path", defaultValue = "./exports")
    String exportPath;

    @Override
    public String save(String fileName, String content) {
        try {
            Path directory = Path.of(exportPath);
            Files.createDirectories(directory);

            Path filePath = directory.resolve(fileName);
            Files.writeString(filePath, content);

            LOG.infof(
                    "event=local_file_saved fileName=%s exportPath=%s sizeBytes=%d",
                    fileName,
                    filePath.toAbsolutePath(),
                    content.getBytes().length
            );

            return filePath.toAbsolutePath().toString();

        } catch (IOException exception) {
            LOG.errorf(exception,
                    "event=local_file_save_failed fileName=%s exportPath=%s errorMessage=%s",
                    fileName,
                    exportPath,
                    exception.getMessage()
            );

            throw new IllegalStateException("Error saving export file locally", exception);
        }
    }
}