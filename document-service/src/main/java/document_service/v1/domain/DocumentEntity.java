package document_service.v1.domain;

import document_service.v1.domain.enumeration.DocumentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table
@Entity(name = "tb_document")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String ownerName;
    private String fileName;
    private DocumentStatus status;
    private LocalDateTime createdAt;


}
