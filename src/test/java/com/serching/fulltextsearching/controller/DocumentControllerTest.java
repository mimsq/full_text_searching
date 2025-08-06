package com.serching.fulltextsearching.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serching.fulltextsearching.entity.DocumentEntity;
import com.serching.fulltextsearching.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DocumentEntity testDocument;

    @BeforeEach
    void setUp() {
        testDocument = new DocumentEntity();
        testDocument.setId("1");
        testDocument.setFilename("test.txt");
        testDocument.setContent("This is test content");
        testDocument.setContentType("text/plain");
    }

    @Test
    void testUploadDocument() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is test content".getBytes()
        );

        when(documentService.saveDocument(any())).thenReturn(testDocument);

        mockMvc.perform(multipart("/api/documents/upload")
                .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.filename").value("test.txt"));
    }

    @Test
    void testSearchDocuments() throws Exception {
        List<DocumentEntity> documents = new ArrayList<>();
        documents.add(testDocument);

        when(documentService.searchDocuments("test")).thenReturn(documents);

        mockMvc.perform(get("/api/documents/search")
                .param("keyword", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].filename").value("test.txt"));
    }

    @Test
    void testGetDocument() throws Exception {
        when(documentService.getDocumentById("1")).thenReturn(testDocument);

        mockMvc.perform(get("/api/documents/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.filename").value("test.txt"));
    }

    @Test
    void testGetDocumentNotFound() throws Exception {
        when(documentService.getDocumentById("1")).thenReturn(null);

        mockMvc.perform(get("/api/documents/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllDocuments() throws Exception {
        List<DocumentEntity> documents = new ArrayList<>();
        documents.add(testDocument);

        when(documentService.getAllDocuments()).thenReturn(documents);

        mockMvc.perform(get("/api/documents")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}