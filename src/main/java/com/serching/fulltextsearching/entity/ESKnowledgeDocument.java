package com.serching.fulltextsearching.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "t_knowledge_document")
@Data
@AllArgsConstructor
public class ESKnowledgeDocument {


    @Id
    private String id;

    private String title;

    private String content;




}
