package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.service.ElasticsearchSyncService;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import com.serching.fulltextsearching.utils.DocumentTools;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.service.DifySyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TKnowledgeDocumentServiceImpl extends ServiceImpl<TKnowledgeDocumentMapper, TKnowledgeDocument>
        implements TKnowledgeDocumentService {


    @Autowired
    DocumentTools documentTools;

    @Autowired
    ElasticsearchSyncService elasticsearchSyncService;


    @Autowired
    TKnowledgeDocumentMapper tKnowledgeDocumentMapper;

    @Autowired
    DifySyncService difySyncService;

    private static final Logger logger = LoggerFactory.getLogger(TKnowledgeDocumentServiceImpl.class);


    @Override
    public TKnowledgeDocument uploadDocument(MultipartFile file) throws IOException {
        try {
            //生成唯一文件名
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            //定义保存路径(可根据需要修改)
            String filePath = System.getProperty("java.io.tmpdir") + "/" + filename;
            //保存文件到本地路径
            File localFile = new File(filePath);
            file.transferTo(localFile);

            //提取文件内容
            String content = documentTools.extractTextFromPath(filePath);

            //处理mysql文档存储
            TKnowledgeDocument document = new TKnowledgeDocument();
            document.setTitle(file.getOriginalFilename());
            document.setContent(content);
            document.setDocSuffix(documentTools.getFileExtension(file.getOriginalFilename()));
            document.setCreatedAt(LocalDateTime.now());
//            document.setCreatedBy();
            document.setProcessingStatus(1);
            document.setDocStatus(1);
            document.setUpdatedAt(LocalDateTime.now());
            //参数设置完毕，传入数据库
            if (this.save(document)){
                Long id = document.getId();
                //处理elasticsearch的文档存储
                ESKnowledgeDocument esDocument = new ESKnowledgeDocument(id+"",file.getOriginalFilename(),content);
                try{
                    elasticsearchSyncService.syncDocumentToEs(esDocument);
                }catch (Exception e){
                    throw new RuntimeException("文档保存失败:"+e.getMessage(),e);
                }
                //保存成功，返回对象信息
                return document;
            }


        }catch (IOException e){
            throw new RuntimeException("文件保存失败:" + e.getMessage(),e);
        }catch (Exception e){
            throw new RuntimeException("文档处理失败:"+e.getMessage(),e);
        }
        //保存失败，返回空
        return null;
    }

    @Override
    public TKnowledgeDocument updateDocument(TKnowledgeDocument tKnowledgeDocument) {
        // 1.更新本地数据库
        tKnowledgeDocument.setUpdatedAt(LocalDateTime.now());
        boolean isUpdated = this.updateById(tKnowledgeDocument);
        
        if (isUpdated) {
            // 若有内容则生成/覆盖临时文件
            if (tKnowledgeDocument.getContent() != null) {
                String filename = tKnowledgeDocument.getId() + ".txt";
                String filePath = System.getProperty("java.io.tmpdir") + "/" + filename;
                try {
                    documentTools.createTextFile(filePath, tKnowledgeDocument.getContent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // 2.更新Elasticsearch
            try{
                ESKnowledgeDocument esDocument = new ESKnowledgeDocument(tKnowledgeDocument.getId()+"",tKnowledgeDocument.getTitle(),tKnowledgeDocument.getContent());
                elasticsearchSyncService.syncDocumentToEs(esDocument);
            }catch (Exception e){
                throw new RuntimeException("文档更新失败:"+e.getMessage(),e);
            }
            // 3.更新Dify
            try {
                // 使用 kbId + difyDocumentId 更新 Dify 中的文档
                boolean difySyncResult = difySyncService.updateDocumentInDify(tKnowledgeDocument);
                if (difySyncResult) {
                    logger.info("Dify 同步更新成功, 本地ID: {}, Dify文档ID: {}", 
                        tKnowledgeDocument.getId(), tKnowledgeDocument.getDifyDocumentId());
                } else {
                    logger.warn("Dify 同步更新失败, 本地ID: {}, Dify文档ID: {}", 
                        tKnowledgeDocument.getId(), tKnowledgeDocument.getDifyDocumentId());
                }
            } catch (Exception e) {
                logger.error("Dify 同步更新异常, 本地ID: {}", tKnowledgeDocument.getId(), e);
            }

            return tKnowledgeDocument;
        }
        return null;
    }

    
    @Override
    public boolean deleteDocument(Long id) {
        try {
            // 先查询文档是否存在
            TKnowledgeDocument document = this.getById(id);
            if (document == null) {
                logger.warn("文档不存在: id={}", id);
                return false;
            }

            //先同步删除Dify 中的文档
            try{
                boolean difySyncResult = difySyncService.removeDocumentFromDify(document);
                if (difySyncResult) {
                    logger.info("Dify 同步删除成功, 本地ID: {}, Dify文档ID: {}", 
                        document.getId(), document.getDifyDocumentId());
                } else {
                    logger.warn("Dify 同步删除失败, 本地ID: {}, Dify文档ID: {}", 
                        document.getId(), document.getDifyDocumentId());
                }
            } catch (Exception e) {
                logger.error("Dify 同步删除异常, 本地ID: {}", document.getId(), e);
            }

            // 不再从数据库字段读取本地文件路径，删除逻辑略过
            
            // 删除Elasticsearch中的数据（后续再做修改）
            try {
                elasticsearchSyncService.deleteDocumentFromEs(id.toString());
                logger.info("Elasticsearch 文档删除成功, 文档ID: {}", id);
            } catch (Exception e) {
                logger.error("Elasticsearch 文档删除失败, 文档ID: {}", id, e);
                // 即使ES删除失败，也继续删除MySQL数据
            }
            
            // 删除MySQL中的数据
            boolean deleted = this.removeById(id);
            if (deleted) {
                logger.info("MySQL 文档删除成功, 文档ID: {}", id);
                return true;
            } else {
                logger.warn("MySQL 文档删除失败, 文档ID: {}", id);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("文档删除异常, 文档ID: {}", id, e);
            throw new RuntimeException("文档删除失败: " + e.getMessage(), e);
        }
    }

}