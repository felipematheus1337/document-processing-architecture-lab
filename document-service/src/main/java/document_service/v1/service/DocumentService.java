package document_service.v1.service;

import document_service.v1.domain.DocumentEntity;
import document_service.v1.domain.enumeration.DocumentStatus;
import document_service.v1.dto.CreateDocumentRequest;
import document_service.v1.dto.DocumentResponse;
import document_service.v1.exception.BusinessException;
import document_service.v1.producer.DocumentEventProducer;
import document_service.v1.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository repository;
    private final DocumentEventProducer producer;

    public  DocumentResponse getById(Long id) {
        var document = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Document not found."));

        return DocumentResponse
                .builder()
                .id(document.getId())
                .owner(document.getOwnerName())
                .title(document.getTitle())
                .status(document.getStatus().toString())
                .build();
    }

    public List<DocumentResponse> list() {
        return repository.findAll()
                .stream()
                .map(document -> {
                    return DocumentResponse
                            .builder()
                            .id(document.getId())
                            .owner(document.getOwnerName())
                            .title(document.getTitle())
                            .status(document.getStatus().toString())
                            .build();
                })
                .toList();
    }

    @Transactional
    public void create(CreateDocumentRequest request) {
        var documentToSave = DocumentEntity
                .builder()
                .fileName(request.fileName())
                .ownerName(request.ownerName())
                .description(request.description())
                .title(request.title())
                .status(DocumentStatus.RECEIVED)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(documentToSave);

        producer.publish(documentToSave);
    }

    @Transactional
    public void persist(DocumentEntity document) {
        log.info("Persisting the entity with owner: {}", document.getOwnerName());
        repository.save(document);
    }
}
