package document_service.v1.producer;

import document_service.v1.domain.DocumentEntity;
import document_service.v1.domain.enumeration.DocumentStatus;
import document_service.v1.event.DocumentSubmittedEvent;
import document_service.v1.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventProducer {

    private static final String BINDING_NAME = "documentSubmitted-out-0";

    private final StreamBridge streamBridge;
    private final DocumentService service;

    public void publish(DocumentEntity document) {

        try {
            if (checkStatusIdempotence(document.getStatus())) {
                var payload = DocumentSubmittedEvent
                        .builder()
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .fileName(document.getFileName())
                        .ownerName(document.getOwnerName())
                        .submittedAt(LocalDateTime.now())
                        .build();
                boolean hasSend = streamBridge.send(BINDING_NAME, payload);
                if (hasSend) {
                    document.setStatus(DocumentStatus.PROCESSING);
                } else {
                    document.setStatus(DocumentStatus.FAILED);
                }
                service.persist(document);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Failed to publish the message...");
            document.setStatus(DocumentStatus.FAILED);
            service.persist(document);
        }

    }

    private boolean checkStatusIdempotence(DocumentStatus status) {
        return DocumentStatus.RECEIVED.equals(status);
    }

}
