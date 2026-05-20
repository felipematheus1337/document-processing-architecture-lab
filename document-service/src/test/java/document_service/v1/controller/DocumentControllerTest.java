package document_service.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import document_service.v1.dto.CreateDocumentRequest;
import document_service.v1.dto.DocumentResponse;
import document_service.v1.exception.BusinessException;
import document_service.v1.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void list_returnsOkWithDocumentList() throws Exception {
        var documents = List.of(
                DocumentResponse.builder().id(1L).title("Doc A").owner("Owner A").status("RECEIVED").build(),
                DocumentResponse.builder().id(2L).title("Doc B").owner("Owner B").status("PROCESSING").build()
        );
        when(documentService.list()).thenReturn(documents);

        mockMvc.perform(get("/api/v1/document-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Doc A"))
                .andExpect(jsonPath("$[0].owner").value("Owner A"))
                .andExpect(jsonPath("$[0].status").value("RECEIVED"));
    }

    @Test
    void list_whenEmpty_returnsOkWithEmptyList() throws Exception {
        when(documentService.list()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/document-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_returnsOkWithDocument() throws Exception {
        var document = DocumentResponse.builder().id(1L).title("Doc A").owner("Owner A").status("RECEIVED").build();
        when(documentService.getById(1L)).thenReturn(document);

        mockMvc.perform(get("/api/v1/document-service/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Doc A"))
                .andExpect(jsonPath("$.owner").value("Owner A"))
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void getById_whenNotFound_returns500() throws Exception {
        when(documentService.getById(99L)).thenThrow(new BusinessException("Document not found."));

        mockMvc.perform(get("/api/v1/document-service/99"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void create_returnsCreated() throws Exception {
        var request = new CreateDocumentRequest("Title", "Description", "Owner", "file.pdf");
        doNothing().when(documentService).create(any(CreateDocumentRequest.class));

        mockMvc.perform(post("/api/v1/document-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void create_whenServiceThrows_returns500() throws Exception {
        var request = new CreateDocumentRequest("Title", "Description", "Owner", "file.pdf");
        doThrow(new RuntimeException("Unexpected error")).when(documentService).create(any());

        mockMvc.perform(post("/api/v1/document-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
}
