package processing_service.v1.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;
import processing_service.v1.event.DocumentSubmittedEvent;
import processing_service.v1.exception.BusinessException;
import processing_service.v1.service.DocumentProcessingService;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentProcessorConsumer implements Consumer<DocumentSubmittedEvent> {

    private final DocumentProcessingService service;

    @Override
    public void accept(DocumentSubmittedEvent submittedEvent) {
        try {
            log.info("Processando evento de persistir documento no MongoDB.");

            if (submittedEvent == null) {
                throw new BusinessException();
            }

            ProcessingEvent existingProcessingEvent = verifyIfProcessingEventExists(submittedEvent.documentId());

            Integer attempts = 1;

            if (existingProcessingEvent != null) {
                checkIdempotenceStatus(existingProcessingEvent);
                attempts = existingProcessingEvent.getAttempts() + 1;
            }

            ProcessingEvent processingEvent = ProcessingEvent
                    .builder()
                    .processedAt(LocalDateTime.now())
                    .documentId(submittedEvent.documentId())
                    .attempts(attempts)
                    .status(DocumentProcessingStatus.COMPLETED)
                    .build();

            service.persist(processingEvent);

        } catch (Exception e) {
            log.error("Erro ao processar evento de documento: {}", e.getMessage(), e);

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
