package document_service.v1.dto;

import document_service.v1.domain.enumeration.DocumentStatus;

public record CreateDocumentRequest(String title, String description, String ownerName,
                                    String fileName, DocumentStatus status) {
}
