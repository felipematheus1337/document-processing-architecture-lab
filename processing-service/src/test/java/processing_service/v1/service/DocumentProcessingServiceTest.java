package processing_service.v1.service;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;
import processing_service.v1.repository.DocumentEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentProcessingServiceTest {

    @Mock
    private DocumentEventRepository repository;

    private DocumentProcessingService service;

    @BeforeEach
    void setUp() {
        service = new DocumentProcessingService(repository, ObservationRegistry.NOOP);
    }

    @Test
    void getCompleted_returnsEventsWithCompletedStatus() {
        var events = List.of(
                ProcessingEvent.builder().id("id-1").documentId(1L).status(DocumentProcessingStatus.COMPLETED).build(),
                ProcessingEvent.builder().id("id-2").documentId(2L).status(DocumentProcessingStatus.COMPLETED).build()
        );
        when(repository.findByStatus(DocumentProcessingStatus.COMPLETED)).thenReturn(events);

        var result = service.getCompleted();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("id-1");
        assertThat(result.get(0).getStatus()).isEqualTo(DocumentProcessingStatus.COMPLETED);
        assertThat(result.get(1).getId()).isEqualTo("id-2");
    }

    @Test
    void getCompleted_whenNoEvents_returnsEmptyList() {
        when(repository.findByStatus(DocumentProcessingStatus.COMPLETED)).thenReturn(List.of());

        var result = service.getCompleted();

        assertThat(result).isEmpty();
    }

    @Test
    void persist_savesEventToRepository() {
        var event = ProcessingEvent.builder()
                .id("id-1")
                .documentId(1L)
                .status(DocumentProcessingStatus.COMPLETED)
                .processedAt(LocalDateTime.now())
                .attempts(1)
                .build();

        service.persist(event);

        verify(repository).save(event);
    }

    @Test
    void getByDocumentId_whenEventExists_returnsEvent() {
        var event = ProcessingEvent.builder()
                .id("id-1")
                .documentId(1L)
                .status(DocumentProcessingStatus.COMPLETED)
                .build();
        when(repository.findByDocumentId(1L)).thenReturn(Optional.of(event));

        var result = service.getByDocumentId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("id-1");
        assertThat(result.get().getDocumentId()).isEqualTo(1L);
    }

    @Test
    void getByDocumentId_whenEventDoesNotExist_returnsEmpty() {
        when(repository.findByDocumentId(99L)).thenReturn(Optional.empty());

        var result = service.getByDocumentId(99L);

        assertThat(result).isEmpty();
    }
}
