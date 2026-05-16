package processing_service.v1.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentSubmittedEvent(Long documentId,
                                     String title, String ownerName,
                                     String fileName, LocalDateTime submittedAt)  {
}
