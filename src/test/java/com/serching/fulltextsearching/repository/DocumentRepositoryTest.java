package com.serching.fulltextsearching.repository;

import com.serching.fulltextsearching.entity.DocumentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void testSaveAndFindDocument() {
        // 创建测试文档
        DocumentEntity document = new DocumentEntity();
        document.setId("test-id-1");
        document.setContent("This is a test document content for Elasticsearch");
        document.setFilename("test.txt");
        document.setContentType("text/plain");
        document.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // 保存文档
        DocumentEntity savedDocument = documentRepository.save(document);

        // 验证保存成功
        assertThat(savedDocument).isNotNull();
        assertThat(savedDocument.getId()).isEqualTo("test-id-1");
        assertThat(savedDocument.getFilename()).isEqualTo("test.txt");

        // 根据ID查找文档
        DocumentEntity foundDocument = documentRepository.findById("test-id-1").orElse(null);
        assertThat(foundDocument).isNotNull();
        assertThat(foundDocument.getContent()).isEqualTo("This is a test document content for Elasticsearch");
    }

    @Test
    void testFindByContentContaining() {
        // 创建测试文档
        DocumentEntity document = new DocumentEntity();
        document.setId("test-id-2");
        document.setContent("This document contains the word Elasticsearch and testing");
        document.setFilename("test2.txt");
        document.setContentType("text/plain");
        document.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        documentRepository.save(document);

        // 搜索包含关键词的文档
        List<DocumentEntity> results = documentRepository.findByContentContaining("Elasticsearch");

        // 验证搜索结果
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getContent()).contains("Elasticsearch");
    }

    @Test
    void testFindByFilename() {
        // 创建测试文档
        DocumentEntity document = new DocumentEntity();
        document.setId("test-id-3");
        document.setContent("Another test document");
        document.setFilename("document.pdf");
        document.setContentType("application/pdf");
        document.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        documentRepository.save(document);

        // 根据文件名搜索
        List<DocumentEntity> results = documentRepository.findByFilename("document.pdf");

        // 验证搜索结果
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFilename()).isEqualTo("document.pdf");
        assertThat(results.get(0).getContentType()).isEqualTo("application/pdf");
    }

    @Test
    void testDocumentEntityCreation() {
        // 创建测试文档
        DocumentEntity document = new DocumentEntity();
        document.setId("test-id-1");
        document.setContent("This is a test document content");
        document.setFilename("test.txt");
        document.setContentType("text/plain");
        document.setCreatedDate("2023-01-01T10:00:00");

        // 验证文档属性
        assertThat(document.getId()).isEqualTo("test-id-1");
        assertThat(document.getContent()).isEqualTo("This is a test document content");
        assertThat(document.getFilename()).isEqualTo("test.txt");
        assertThat(document.getContentType()).isEqualTo("text/plain");
        assertThat(document.getCreatedDate()).isEqualTo("2023-01-01T10:00:00");
    }
}