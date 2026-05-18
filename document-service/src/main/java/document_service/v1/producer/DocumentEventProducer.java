package document_service.v1.producer;

import document_service.v1.constants.ConstantsUtils;
import document_service.v1.domain.DocumentEntity;
import document_service.v1.domain.enumeration.DocumentStatus;
import document_service.v1.event.DocumentSubmittedEvent;
import document_service.v1.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventProducer {

    private static final String BINDING_NAME = "documentSubmitted-out-0";

    private final StreamBridge streamBridge;
    private final DocumentRepository repository;

    public void publish(DocumentEntity document) {

        try {
            if (checkStatusIdempotence(document.getStatus())) {
                String correlationId = MDC.get("correlationId");

                var payload = DocumentSubmittedEvent
                        .builder()
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .fileName(document.getFileName())
                        .ownerName(document.getOwnerName())
                        .submittedAt(LocalDateTime.now())
                        .build();

                Message<DocumentSubmittedEvent> message = MessageBuilder
                        .withPayload(payload)
                        .setHeader(ConstantsUtils.CORRELATION_ID_MDC_KEY, correlationId)
                        .setHeader("eventType", "DocumentSubmittedEvent")
                        .setHeader("documentId", String.valueOf(document.getId()))
                        .build();

                boolean hasSend = streamBridge.send(BINDING_NAME, payload);
                if (hasSend) {
                    document.setStatus(DocumentStatus.PROCESSING);
                } else {
                    log.error("event=document_event_publish_failed binding={} documentId={}",
                            BINDING_NAME,
                            document.getId());

                    document.setStatus(DocumentStatus.FAILED);
                }
                repository.save(document);

                log.info("event=document_event_published binding={} eventType=DocumentSubmittedEvent documentId={}",
                        BINDING_NAME,
                        document.getId()
                );
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Failed to publish the message...");
            document.setStatus(DocumentStatus.FAILED);
            repository.save(document);
        }

    }

    private boolean checkStatusIdempotence(DocumentStatus status) {
        return DocumentStatus.RECEIVED.equals(status);
    }

}
