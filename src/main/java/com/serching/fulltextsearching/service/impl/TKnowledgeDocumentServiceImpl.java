package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.mapper.TKnowledgeBaseMapper;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
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
    TKnowledgeDocumentMapper tKnowledgeDocumentMapper;

    @Autowired
    DifySyncService difySyncService;

    @Autowired
    private TKnowledgeBaseMapper tKnowledgeBaseMapper;

    @Autowired
    private KnowledgeBaseCategoryMapper knowledgeBaseCategoryMapper;

    private static final Logger logger = LoggerFactory.getLogger(TKnowledgeDocumentServiceImpl.class);


    @Override
    public TKnowledgeDocument uploadDocument(TKnowledgeBase kb, Long categoryId, MultipartFile file) throws IOException {
        // 1) 参数校验
        if (kb == null || kb.getId() == null) {
            throw new BusinessException(400, "知识库信息非法，需提供 knowledgeBase.id");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        //检查文件格式
        String originalName = file.getOriginalFilename();
        String suffix = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase()
                : "";
        if (!("txt".equals(suffix) || "md".equals(suffix))) {
            throw new BusinessException(400, "仅支持 .txt 或 .md 文件");
        }

        //保存临时文件并抽取文本
        String tmpPath = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID() + "_" + originalName;
        try{
            File local = new File(tmpPath);
            file.transferTo(local);
        }catch (IOException e){
            throw new BusinessException(500,"文件保存失败：" + e.getMessage(),e);
        }
        String content;

        try {
            content = documentTools.extractTextFromPath(tmpPath);
        }catch (Exception e){
            throw new BusinessException(500,"文件内容提取失败：" + e.getMessage(),e);
        }

        //上传到dify知识库中
        //获取dify知识库标识id
        String kbId = kb.getBaseId();
        if (kbId == null || kbId.isEmpty()) {
            throw new BusinessException(400, "knowledgeBase.baseId 不能为空");
        }
        //定义ducumentId，从存入dify之后响应中获取
        String difyDocmentId;
        try{
            difyDocmentId = difySyncService.createDocumentByFile(kbId,file);
        } catch (Exception e) {
            throw new BusinessException("Dify创建文档失败：" + e.getMessage());
        }




        //组装实体并保存
        TKnowledgeDocument doc = new TKnowledgeDocument();
        doc.setKbId(kb.getBaseId());//TKnowledgeDocument的KbId对应TKnowledgeBase的baseId,都是dify中用于标识的id，而非数据库或者ES中id（自增Long）
        doc.setCategoryId(categoryId);
        doc.setContent(content);
        doc.setTitle(originalName);
        doc.setDocSuffix(suffix);
        doc.setProcessingStatus(1);
        doc.setDocStatus(1);
        doc.setDelStatus(0);
        doc.setDocType(0);
        //doc.setDocMetadata();
        //doc.setFileId();后续处理
        //doc.setPreviewInfo();
        doc.setDifyDocumentId(difyDocmentId);//需要先上传到dify接受返回值才能填写
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());

        boolean mysqlSaved = this.save(doc);
        if (!mysqlSaved) throw new BusinessException(500, "文档保存失败");

        //TODO ES:预留同步位置


        return doc;
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
            //TODO ES:预留同步位置

            //TODO ES:预留同步位置

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
            //TODO ES:预留同步位置


            //TODO ES:预留同步位置


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