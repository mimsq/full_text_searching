package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.DocumentEntity;
import com.serching.fulltextsearching.repository.DocumentRepository;
import com.serching.fulltextsearching.service.impl.DocumentServiceImpl;
import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private Tika tika;

    private DocumentServiceImpl documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentService = new DocumentServiceImpl(tika, documentRepository);
    }

    @Test
    void testExtractText() throws Exception {
        String content = "This is test content";
        InputStream mockInputStream = new ByteArrayInputStream(content.getBytes());
        when(tika.parseToString((InputStream) any())).thenReturn(content);

        String extractedText = documentService.extractText(mockInputStream);

        assertEquals(content, extractedText);
        verify(tika, times(1)).parseToString((InputStream) any());
    }

    @Test
    void testSaveDocument() throws Exception {
        // 模拟multipart文件的内容
        String content = "This is test content";
        InputStream mockInputStream = new ByteArrayInputStream(content.getBytes());
        when(tika.parseToString((InputStream) any())).thenReturn(content);

        DocumentEntity mockDocument = new DocumentEntity();
        mockDocument.setId("1");
        mockDocument.setContent(content);
        mockDocument.setFilename("test.txt");
        mockDocument.setContentType("text/plain");

        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(mockDocument);

        // 创建一个简单的模拟MultipartFile
        org.springframework.web.multipart.MultipartFile mockFile = mock(org.springframework.web.multipart.MultipartFile.class);
        when(mockFile.getInputStream()).thenReturn(mockInputStream);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getContentType()).thenReturn("text/plain");

        DocumentEntity savedDocument = documentService.saveDocument(mockFile);

        assertNotNull(savedDocument);
        assertEquals("test.txt", savedDocument.getFilename());
        assertEquals("text/plain", savedDocument.getContentType());
        verify(documentRepository, times(1)).save(any(DocumentEntity.class));
    }

    @Test
    void testGetDocumentById() {
        DocumentEntity mockDocument = new DocumentEntity();
        mockDocument.setId("1");
        mockDocument.setContent("test content");

        when(documentRepository.findById("1")).thenReturn(Optional.of(mockDocument));

        DocumentEntity document = documentService.getDocumentById("1");

        assertNotNull(document);
        assertEquals("1", document.getId());
        verify(documentRepository, times(1)).findById("1");
    }

    @Test
    void testGetDocumentByIdNotFound() {
        when(documentRepository.findById("1")).thenReturn(Optional.empty());

        DocumentEntity document = documentService.getDocumentById("1");

        assertNull(document);
        verify(documentRepository, times(1)).findById("1");
    }

    @Test
    void testGetAllDocuments() {
        documentService.getAllDocuments();
        verify(documentRepository, times(1)).findAll();
    }
}