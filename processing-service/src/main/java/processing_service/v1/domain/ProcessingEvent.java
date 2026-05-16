package processing_service.v1.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;

import java.time.LocalDateTime;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingEvent {

    @Id
    private String id;

    private Long documentId;
    private String title;
    private String ownerName;
    private String fileName;
    private DocumentProcessingStatus status;
    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;
    private Integer attempts;
    private String errorMessage;


}
