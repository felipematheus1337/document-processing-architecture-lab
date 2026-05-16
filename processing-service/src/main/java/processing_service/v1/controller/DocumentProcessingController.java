package processing_service.v1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.service.DocumentProcessingService;

import java.util.List;

@RequestMapping("/api/v1/processing-events")
@RestController
@RequiredArgsConstructor
public class DocumentProcessingController {

    private final DocumentProcessingService service;

    @GetMapping
    public ResponseEntity<List<ProcessingEvent>> getCompleted() {
       return ResponseEntity.ok(service.getCompleted());
    }
}
