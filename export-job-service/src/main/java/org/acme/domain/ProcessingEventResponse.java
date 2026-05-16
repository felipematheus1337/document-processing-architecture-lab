package org.acme.domain;

import java.time.LocalDateTime;

public record ProcessingEventResponse(
        String id,
        Long documentId,
        String title,
        String ownerName,
        String fileName,
        String status,
        LocalDateTime receivedAt,
        LocalDateTime processedAt,
        Integer attempts,
        String errorMessage
) {
}