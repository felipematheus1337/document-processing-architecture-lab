package processing_service.v1.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentEventRepository extends MongoRepository<ProcessingEvent, String> {

    List<ProcessingEvent> findByStatus(DocumentProcessingStatus status);
    Optional<ProcessingEvent> findByDocumentId(Long documentId);
}
