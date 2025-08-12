package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Dify 同步服务接口
 * 用于处理文档与 Dify 知识库的同步操作
 */
public interface DifySyncService {
    
    /**
     * 更新 Dify 知识库中的文档
     * @param document 要更新的文档
     * @return 是否更新成功
     */
    boolean updateDocumentInDify(TKnowledgeDocument document);
    
    /**
     * 从 Dify 知识库中删除文档
     * @param document 要删除的文档
     * @return 是否删除成功
     */
    boolean removeDocumentFromDify(TKnowledgeDocument document);

    /**
     * 通过文件在Dify知识库创建文档
     *
     * @param datasetId 知识库ID
     * @param file 要上传的文件
     * @return 文档在Dify中的唯一标识ID
     * @throws Exception 当文件上传失败或API调用出错时抛出
     */
    String createDocumentByFile(String datasetId, File file) throws Exception;


    /**
     * 通过文件在Dify知识库更新文档
     *
     * @param datasetId 知识库ID
     * @param documentId Dify文档ID
     * @param file 要上传的文件
     * @return 是否更新成功
     * @throws Exception 当文件上传失败或API调用出错时抛出
     */
    boolean updateDocumentByFile(String datasetId, String documentId, File file) throws Exception;
}
