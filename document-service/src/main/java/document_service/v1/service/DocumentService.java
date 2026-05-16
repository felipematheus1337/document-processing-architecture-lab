package document_service.v1.service;

import document_service.v1.dto.CreateDocumentRequest;
import document_service.v1.dto.DocumentResponse;
import document_service.v1.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;

    public  DocumentResponse getById(Long id) {
    }

    public List<DocumentResponse> list() {
    }

    public void create(CreateDocumentRequest request) {
    }
}
