package processing_service.v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;
import processing_service.v1.repository.DocumentEventRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentProcessingService {

    private final DocumentEventRepository repository;

    public List<ProcessingEvent> getCompleted() {
        log.info("Buscando documentos que estão com status de completo...");
        return repository.findByStatus(DocumentProcessingStatus.COMPLETED);
    }

    @Transactional
    public ProcessingEvent persist(ProcessingEvent event) {
        return repository.save(event);
    }

    public Optional<ProcessingEvent> getByDocumentId(Long documentId) {
        return repository.findByDocumentId(documentId);
    }
}
