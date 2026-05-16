package document_service.v1.dto;

import lombok.Builder;

@Builder
public record DocumentResponse(Long id, String title, String owner, String status) {
}
