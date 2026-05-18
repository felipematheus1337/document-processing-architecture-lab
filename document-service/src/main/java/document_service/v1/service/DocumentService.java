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
import org.slf4j.MDC;
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
        log.info("event=document_creation_started title={} ownerName={} fileName={}",
                request.title(),
                request.ownerName(),
                request.fileName()
        );

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

        MDC.put("documentId", String.valueOf(documentToSave.getId()));

        log.info("event=document_persisted status={} title={} ownerName={}",
                documentToSave.getStatus(),
                documentToSave.getTitle(),
                documentToSave.getOwnerName()
        );

        producer.publish(documentToSave);

        log.info("event=document_creation_finished status={}", documentToSave.getStatus());

        MDC.remove("documentId");

    }

}
