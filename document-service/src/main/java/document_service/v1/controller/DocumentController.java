package document_service.v1.controller;

import document_service.v1.dto.CreateDocumentRequest;
import document_service.v1.dto.DocumentResponse;
import document_service.v1.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateDocumentRequest request) {
        service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
