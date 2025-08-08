package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.controller.TKnowledgeDocumentController;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.repository.TKnowledgeDocumentRepository;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import com.serching.fulltextsearching.utils.DocumentTools;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TKnowledgeDocumentServiceImpl extends ServiceImpl<TKnowledgeDocumentMapper, TKnowledgeDocument>
        implements TKnowledgeDocumentService {


    DocumentTools documentTools = new DocumentTools();

    @Autowired
    TKnowledgeDocumentRepository tKnowledgeDocumentRepository;

    @Autowired
    TKnowledgeDocumentMapper tKnowledgeDocumentMapper;

    private static final Logger logger = LoggerFactory.getLogger(TKnowledgeDocumentController.class);


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
            document.setFileSize(file.getSize());
            document.setSourcePath(filePath);
            document.setUpdatedAt(LocalDateTime.now());
            //参数设置完毕，传入数据库
            if (this.save(document)){
                Long id = document.getId();
                //处理elasticsearch的文档存储
                ESKnowledgeDocument esDocument = new ESKnowledgeDocument(id+"",file.getOriginalFilename(),content);
                try{
                    tKnowledgeDocumentRepository.save(esDocument);
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
        // 使用updateById方法更新文档
        boolean isUpdated = this.updateById(tKnowledgeDocument);
        if (isUpdated) {

// 生成文件名和路径（参考uploadDocument方法）
            String filename = tKnowledgeDocument.getId() + ".txt";
            String filePath = System.getProperty("java.io.tmpdir") + "/" + filename;

            // 创建txt文件并写入内容
            try {
                documentTools.createTextFile(filePath, tKnowledgeDocument.getContent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 更新sourcePath字段
            tKnowledgeDocument.setSourcePath(filePath);

            try{
                Long id = tKnowledgeDocument.getId();
                ESKnowledgeDocument esDocument = new ESKnowledgeDocument(id+"",tKnowledgeDocument.getTitle(),tKnowledgeDocument.getContent());
                tKnowledgeDocumentRepository.save(esDocument);
            }catch (Exception e){
                throw new RuntimeException("文档更新失败:"+e.getMessage(),e);
            }
            return tKnowledgeDocument;
        }
        return null;
    }
    
    @Override
    public TKnowledgeDocument createDocument(TKnowledgeDocument tKnowledgeDocument) {
        try {
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            if (tKnowledgeDocument.getCreatedAt() == null) {
                tKnowledgeDocument.setCreatedAt(now);
            }
            tKnowledgeDocument.setUpdatedAt(now);
            
            // 设置默认状态（如果未设置）
            if (tKnowledgeDocument.getProcessingStatus() == null) {
                tKnowledgeDocument.setProcessingStatus(1); // 已完成
            }
            if (tKnowledgeDocument.getDocStatus() == null) {
                tKnowledgeDocument.setDocStatus(1); // 启用
            }
            
            // 先保存到数据库以获取ID
            boolean saved = this.save(tKnowledgeDocument);
            Long id = tKnowledgeDocument.getId();
            if (saved) {
                // 生成文件名和路径（参考uploadDocument方法）
                String filename = tKnowledgeDocument.getId() + ".txt";
                String filePath = System.getProperty("java.io.tmpdir") + "/" + filename;
                
                // 创建txt文件并写入内容
                documentTools.createTextFile(filePath, tKnowledgeDocument.getContent());
                
                // 更新sourcePath字段
                tKnowledgeDocument.setSourcePath(filePath);
                this.updateById(tKnowledgeDocument);
                
                // 同步到 Elasticsearch
                try {
                    ESKnowledgeDocument esDocument = new ESKnowledgeDocument();
                    esDocument.setId(tKnowledgeDocument.getId().toString());
                    esDocument.setTitle(tKnowledgeDocument.getTitle());
                    esDocument.setContent(tKnowledgeDocument.getContent());
                    tKnowledgeDocumentRepository.save(esDocument);
                    logger.info("Elasticsearch 同步成功, 文档ID: {}", tKnowledgeDocument.getId());
                } catch (Exception e) {
                    logger.error("Elasticsearch 同步失败, 文档ID: {}", tKnowledgeDocument.getId(), e);
                }
                
                return tKnowledgeDocument;
            }
            
            logger.warn("文档创建失败: title={}", tKnowledgeDocument.getTitle());
            return null;
            
        } catch (Exception e) {
            logger.error("文档创建异常: ", e);
            throw new RuntimeException("文档创建失败: " + e.getMessage(), e);
        }
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
            
            // 删除本地文件（如果存在）
            String filePath = document.getSourcePath();
            if (filePath != null && !filePath.isEmpty()) {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            logger.info("本地文件删除成功: {}", filePath);
                        } else {
                            logger.warn("本地文件删除失败: {}", filePath);
                        }
                    }
                } catch (Exception e) {
                    logger.error("删除本地文件时发生异常: {}", filePath, e);
                }
            }
            
            // 删除Elasticsearch中的数据
            try {
                tKnowledgeDocumentRepository.deleteById(id.toString());
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