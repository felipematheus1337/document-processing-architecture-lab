package processing_service.v1.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;
import processing_service.v1.event.DocumentSubmittedEvent;
import processing_service.v1.exception.BusinessException;
import processing_service.v1.metrics.ProcessingMetrics;
import processing_service.v1.service.DocumentProcessingService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentProcessorConsumer implements Consumer<Message<DocumentSubmittedEvent>> {

    private final DocumentProcessingService service;
    private final ProcessingMetrics processingMetrics;

    @Override
    public void accept(Message<DocumentSubmittedEvent> message) {
        try {
            log.info("Processando evento de persistir documento no MongoDB.");

            processingMetrics.incrementDocumentEventsConsumed("DocumentSubmittedEvent");

            DocumentSubmittedEvent submittedEvent = message.getPayload();

            ProcessingEvent existingProcessingEvent = verifyIfProcessingEventExists(submittedEvent.documentId());

            Integer attempts = 1;

            if (existingProcessingEvent != null) {
                checkIdempotenceStatus(existingProcessingEvent);
                attempts = existingProcessingEvent.getAttempts() + 1;
            }

            String correlationId = Optional
                    .ofNullable(message.getHeaders().get("correlationId", String.class))
                    .filter(value -> !value.isBlank())
                    .orElse(UUID.randomUUID().toString());

            MDC.put("correlationId", correlationId);
            MDC.put("documentId", String.valueOf(submittedEvent.documentId()));

            log.info("event=document_submitted_event_received topic=document-submitted eventType=DocumentSubmittedEvent");

            ProcessingEvent processingEvent = ProcessingEvent
                    .builder()
                    .processedAt(LocalDateTime.now())
                    .documentId(submittedEvent.documentId())
                    .attempts(attempts)
                    .status(DocumentProcessingStatus.COMPLETED)
                    .build();

            service.persist(processingEvent);

            log.info("event=document_submitted_event_processed status=SUCCESS");

        } catch (Exception e) {
            log.error("event=document_submitted_event_processing_failed errorMessage={}",
                    e.getMessage(),
                    e
            );
            processingMetrics.incrementDocumentProcessingFailed(e.getClass().getSimpleName());

        } finally {
            MDC.remove("correlationId");
            MDC.remove("documentId");
            MDC.remove("processingEventId");
        }
    }

    private void checkIdempotenceStatus(ProcessingEvent processingEvent) {
        if (DocumentProcessingStatus.COMPLETED.equals(processingEvent.getStatus())
                || DocumentProcessingStatus.FAILED.equals(processingEvent.getStatus())) {
            throw new BusinessException();
        }
    }

    private ProcessingEvent verifyIfProcessingEventExists(Long documentId) {
        var optEvent = service.getByDocumentId(documentId);
        return optEvent.orElse(null);
    }
}
