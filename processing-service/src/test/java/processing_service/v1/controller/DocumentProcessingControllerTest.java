package processing_service.v1.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import processing_service.v1.domain.ProcessingEvent;
import processing_service.v1.domain.enumeration.DocumentProcessingStatus;
import processing_service.v1.service.DocumentProcessingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentProcessingController.class)
class DocumentProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentProcessingService documentProcessingService;

    @Test
    void getCompleted_returnsOkWithProcessingEventList() throws Exception {
        var events = List.of(
                ProcessingEvent.builder()
                        .id("abc-123")
                        .documentId(1L)
                        .status(DocumentProcessingStatus.COMPLETED)
                        .attempts(1)
                        .processedAt(LocalDateTime.now())
                        .build(),
                ProcessingEvent.builder()
                        .id("def-456")
                        .documentId(2L)
                        .status(DocumentProcessingStatus.COMPLETED)
                        .attempts(1)
                        .processedAt(LocalDateTime.now())
                        .build()
        );
        when(documentProcessingService.getCompleted()).thenReturn(events);

        mockMvc.perform(get("/api/v1/processing-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("abc-123"))
                .andExpect(jsonPath("$[0].documentId").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$[1].id").value("def-456"));
    }

    @Test
    void getCompleted_whenNoEvents_returnsOkWithEmptyList() throws Exception {
        when(documentProcessingService.getCompleted()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/processing-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
