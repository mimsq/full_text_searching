package com.serching.fulltextsearching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface TKnowledgeDocumentService extends IService<TKnowledgeDocument> {

    TKnowledgeDocument uploadDocument (TKnowledgeBase knowledgeBase, Long categoryId, MultipartFile file) throws IOException;

    TKnowledgeDocument updateDocument(TKnowledgeDocument tKnowledgeDocument);

    /**
     * 删除文档，同时删除MySQL和Elasticsearch中的数据
     * @param id 文档ID
     * @return 是否删除成功
     */
    boolean deleteDocument(Long id);
}