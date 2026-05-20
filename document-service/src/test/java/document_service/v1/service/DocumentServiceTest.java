package document_service.v1.service;

import document_service.v1.domain.DocumentEntity;
import document_service.v1.domain.enumeration.DocumentStatus;
import document_service.v1.dto.CreateDocumentRequest;
import document_service.v1.exception.BusinessException;
import document_service.v1.metrics.DocumentMetrics;
import document_service.v1.producer.DocumentEventProducer;
import document_service.v1.repository.DocumentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @Mock
    private DocumentEventProducer producer;

    @Mock
    private DocumentMetrics documentMetrics;

    private DocumentService service;

    @BeforeEach
    void setUp() {
        service = new DocumentService(repository, producer, documentMetrics, ObservationRegistry.NOOP);
    }

    @Test
    void getById_whenDocumentExists_returnsDocumentResponse() {
        var entity = DocumentEntity.builder()
                .id(1L)
                .title("My Document")
                .ownerName("Felipe")
                .status(DocumentStatus.RECEIVED)
                .createdAt(LocalDateTime.now())
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        var result = service.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("My Document");
        assertThat(result.owner()).isEqualTo("Felipe");
        assertThat(result.status()).isEqualTo("RECEIVED");
    }

    @Test
    void getById_whenDocumentDoesNotExist_throwsBusinessException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Document not found.");
    }

    @Test
    void list_returnsMappedDocumentResponses() {
        var entities = List.of(
                DocumentEntity.builder().id(1L).title("Doc A").ownerName("Owner A").status(DocumentStatus.RECEIVED).build(),
                DocumentEntity.builder().id(2L).title("Doc B").ownerName("Owner B").status(DocumentStatus.PROCESSING).build()
        );
        when(repository.findAll()).thenReturn(entities);

        var result = service.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).title()).isEqualTo("Doc A");
        assertThat(result.get(0).owner()).isEqualTo("Owner A");
        assertThat(result.get(0).status()).isEqualTo("RECEIVED");
        assertThat(result.get(1).title()).isEqualTo("Doc B");
    }

    @Test
    void list_whenNoDocuments_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        var result = service.list();

        assertThat(result).isEmpty();
    }

    @Test
    void create_persistsDocumentWithReceivedStatusAndPublishesEvent() {
        var request = new CreateDocumentRequest("My Title", "Description", "Felipe", "doc.pdf");

        service.create(request);

        verify(repository).save(any(DocumentEntity.class));
        verify(documentMetrics).incrementDocumentsCreated(DocumentStatus.RECEIVED.name());
        verify(producer).publish(any(DocumentEntity.class));
    }
}
