package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.dto.EsSearchResult;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeFile;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.mapper.TKnowledgeFileMapper;
import com.serching.fulltextsearching.service.*;
import com.serching.fulltextsearching.utils.DocumentTools;
import com.serching.fulltextsearching.config.FileUploadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TKnowledgeDocumentServiceImpl extends ServiceImpl<TKnowledgeDocumentMapper, TKnowledgeDocument>
        implements TKnowledgeDocumentService {


    @Autowired
    private DocumentTools documentTools;

    @Autowired
    private DifySyncService difySyncService;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Autowired
    private ElasticsearchSyncService elasticsearchSyncService;

    @Autowired
    private TKnowledgeFileMapper knowledgeFileMapper;

    @Autowired
    private TKnowledgeFileService knowledgeFileService;

    private static final Logger logger = LoggerFactory.getLogger(TKnowledgeDocumentServiceImpl.class);


    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 300)//5分钟超时
    public TKnowledgeDocument uploadDocument(TKnowledgeBase kb, Long categoryId, MultipartFile file) throws IOException {
        logger.info("开始上传文档: {}", file.getOriginalFilename());

        try {
            // 1) 参数校验
            validateUploadParameters(kb, file);

            // 2) 检查文件格式
            String suffix = validateFileFormat(file);

            // 3) 保存文件到本地
            Path sourceFile = saveFileToLocal(file);

            // 4) 提取文本内容
            String content = extractFileContent(sourceFile);

            // 5) 上传到 Dify 知识库
            String difyDocumentId = uploadToDify(kb, sourceFile);

            // 6) 保存文件信息到数据库
            TKnowledgeFile knowledgeFile = saveFileInfo(file, sourceFile);

            // 7) 保存文档信息到数据库
            TKnowledgeDocument doc = saveDocumentInfo(kb, categoryId, content,
                    file.getOriginalFilename(), suffix,
                    knowledgeFile, difyDocumentId);

            // 8) 异步同步到 Elasticsearch（不影响事务）
            syncToElasticsearch(doc, content);

            logger.info("文档上传成功: {}, 文档ID: {}", file.getOriginalFilename(), doc.getId());
            return doc;

        } catch (Exception e) {
            logger.error("文档上传失败: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }


    /**
    * 参数校验
    */
    private void validateUploadParameters(TKnowledgeBase kb,MultipartFile file){
        if (kb == null|| kb.getId() == null){
            throw new BusinessException(400,"知识库或知识库id不能为空");
        }

        if (file == null|| file.isEmpty()){
            throw new BusinessException(400,"文件不能为空");
        }
    }





    /**
     * 检查文件格式
     */
    private String validateFileFormat(MultipartFile file){
        String originalName = file.getOriginalFilename();
        String suffix = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase()
                : "";
        if (!("txt".equals(suffix) || "md".equals(suffix))) {
            throw new BusinessException(400, "目前仅支持 .txt 或 .md 文件");
        }
        return suffix;
    }



    /**
     * 保存文件到本地
     */
    private Path saveFileToLocal(MultipartFile file) throws IOException {
        // 确保永久目录存在
        String permanentDir = fileUploadConfig.getPermanentDir();
        Path permanentPath = resolvePermanentPath(permanentDir);

        logger.info("配置的永久目录: {}", permanentDir);
        logger.info("解析后的永久目录: {}", permanentPath.toAbsolutePath());

        // 确保目录存在
        createDirectoryIfNotExists(permanentPath);

        // 验证目录权限
        validateDirectoryPermissions(permanentPath);

        // 保存文件
        String originalName = file.getOriginalFilename();
        Path sourceFilePath = permanentPath.resolve(originalName);
        File sourceFile = sourceFilePath.toFile();

        logger.info("准备保存文件到: {}", sourceFile.getAbsolutePath());

        try {
            // 将MultipartFile转换为File
            file.transferTo(sourceFile);

            // 验证文件是否成功创建
            validateFileCreation(sourceFile);

            logger.info("文件创建成功: {} (大小: {} bytes)", sourceFile.getAbsolutePath(), sourceFile.length());
            return sourceFilePath;

        } catch (IOException e) {
            logger.error("文件保存失败: {}", e.getMessage(), e);
            cleanupIncompleteFile(sourceFilePath);
            throw new BusinessException(500, "文件保存失败：" + e.getMessage(), e);
        }
    }


    /**
     * 解析永久目录路径
     */
    private Path resolvePermanentPath(String permanentDir) {
        if (permanentDir.startsWith("E:") || permanentDir.startsWith("D:") || permanentDir.startsWith("C:")) {
            // Windows绝对路径
            return Paths.get(permanentDir).normalize();
        } else if (permanentDir.startsWith("/")) {
            // Unix绝对路径
            return Paths.get(permanentDir).normalize();
        } else {
            // 相对路径，转换为绝对路径
            return Paths.get(System.getProperty("user.dir"), permanentDir).normalize();
        }
    }


    /**
     * 创建目录（如果不存在）
     */
    private void createDirectoryIfNotExists(Path permanentPath) {
        if (!Files.exists(permanentPath)) {
            try {
                Files.createDirectories(permanentPath);
                logger.info("创建目录: {}", permanentPath.toAbsolutePath());
            } catch (IOException e) {
                logger.error("创建目录失败: {}", permanentPath.toAbsolutePath(), e);
                throw new BusinessException(500, "创建目录失败：" + e.getMessage(), e);
            }
        }
    }

    /**
     * 验证目录权限
     */
    private void validateDirectoryPermissions(Path permanentPath) {
        if (!Files.isDirectory(permanentPath) || !Files.isWritable(permanentPath)) {
            throw new BusinessException(500, "目录不可写或不是目录: " + permanentPath.toAbsolutePath());
        }
    }


    /**
     * 验证文件创建
     */
    private void validateFileCreation(File sourceFile) {
        if (!sourceFile.exists()) {
            throw new BusinessException(500, "文件创建失败：文件不存在");
        }
        if (sourceFile.length() == 0) {
            throw new BusinessException(500, "文件创建失败：文件大小为0");
        }
    }

    /**
     * 清理不完整的文件
     */
    private void cleanupIncompleteFile(Path sourceFilePath) {
        try {
            if (Files.exists(sourceFilePath)) {
                Files.delete(sourceFilePath);
                logger.info("清理不完整的文件: {}", sourceFilePath);
            }
        } catch (IOException cleanupEx) {
            logger.warn("清理不完整文件失败: {}", sourceFilePath, cleanupEx);
        }
    }

    /**
     * 提取文件内容
     */
    private String extractFileContent(Path sourceFile) {
        try {
            String content = documentTools.extractTextFromPath(sourceFile.toAbsolutePath().toString());
            if (content == null || content.trim().isEmpty()) {
                throw new BusinessException(500, "文件内容为空或提取失败");
            }
            logger.info("文件内容提取成功，长度: {} 字符", content.length());
            return content;
        } catch (Exception e) {
            logger.error("文件内容提取失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件内容提取失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传到 Dify
     */
    private String uploadToDify(TKnowledgeBase kb, Path sourceFile) {
        String kbId = kb.getBaseId();
        if (kbId == null || kbId.isEmpty()) {
            throw new BusinessException(400, "knowledgeBase.baseId 不能为空");
        }

        try {
            logger.info("开始上传到 Dify，知识库ID: {}", kbId);
            String difyDocumentId = difySyncService.createDocumentByFile(kbId, sourceFile.toFile());
            logger.info("Dify 上传成功，文档ID: {}", difyDocumentId);
            return difyDocumentId;
        } catch (Exception e) {
            logger.error("Dify 上传失败: {}", e.getMessage(), e);
            throw new BusinessException("Dify创建文档失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件信息到数据库
     */
    private TKnowledgeFile saveFileInfo(MultipartFile file, Path sourceFile) {
        try {
            TKnowledgeFile knowledgeFile = knowledgeFileService.saveFileInfo(
                    file,
                    sourceFile.getParent().toString(),
                    1L // 默认userId后续对接user模块后实现真正的获取
            );
            logger.info("文件信息保存成功，文件ID: {}", knowledgeFile.getId());
            return knowledgeFile;
        } catch (Exception e) {
            logger.error("文件信息保存失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件信息保存失败：" + e.getMessage(), e);
        }
    }

    /**
     * 保存文档信息到数据库
     */
    private TKnowledgeDocument saveDocumentInfo(TKnowledgeBase kb, Long categoryId, String content,
                                                String originalName, String suffix, TKnowledgeFile knowledgeFile,
                                                String difyDocumentId) {
        try {
            TKnowledgeDocument doc = new TKnowledgeDocument();
            doc.setKbId(kb.getId());
            doc.setCategoryId(categoryId);
            doc.setContent(content);
            doc.setTitle(originalName);
            doc.setDocSuffix(suffix);
            doc.setProcessingStatus(1); // 1: 处理完成
            doc.setDocStatus(1);
            doc.setDelStatus(0);
            doc.setDocType(0);
            doc.setFileId(knowledgeFile.getId());
            doc.setDifyDocumentId(difyDocumentId);
            doc.setCreatedAt(LocalDateTime.now());
            doc.setUpdatedAt(LocalDateTime.now());

            boolean mysqlSaved = this.save(doc);
            if (!mysqlSaved) {
                throw new BusinessException(500, "文档保存失败");
            }

            logger.info("文档信息保存成功，文档ID: {}", doc.getId());
            return doc;
        } catch (Exception e) {
            logger.error("文档信息保存失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "文档信息保存失败：" + e.getMessage(), e);
        }
    }

    /**
     * 同步到 Elasticsearch
     * 使用 @Async 注解，不影响主事务
     */
    public void syncToElasticsearch(TKnowledgeDocument doc, String content) {
        try {
            logger.info("开始异步同步到 Elasticsearch，文档ID: {}", doc.getId());

            ESKnowledgeDocument esDocument = new ESKnowledgeDocument(
                    doc.getId().toString(),
                    doc.getTitle(),
                    content
            );

            boolean syncResult = elasticsearchSyncService.syncDocumentToEs(esDocument);

            if (!syncResult) {
                throw new BusinessException(500, "Elasticsearch 同步失败");
            }

            logger.info("Elasticsearch 同步成功，文档ID: {}", doc.getId());
        } catch (Exception e) {
            logger.error("Elasticsearch 同步失败，文档ID: {}, 错误: {}", doc.getId(), e.getMessage(), e);
            throw new BusinessException(500, "Elasticsearch 同步失败：" + e.getMessage(), e);
        }
    }























    //更新
    @Override
    public TKnowledgeDocument updateDocument(TKnowledgeDocument tKnowledgeDocument) {

        logger.info("开始更新文档，接收到的数据: {}", tKnowledgeDocument);

        //验证必要字段
        if (tKnowledgeDocument.getId() == null){
            throw new BusinessException(400,"文档ID不能为空");
        }

        //查询现有文档
        TKnowledgeDocument existingDoc = this.getById(tKnowledgeDocument.getId());
        if (existingDoc == null) {
            throw new BusinessException(404, "文档不存在");
        }

        // 检查内容是否真的改变了
        boolean contentChanged = false;
        if (tKnowledgeDocument.getContent() != null &&
                !tKnowledgeDocument.getContent().equals(existingDoc.getContent())) {
            contentChanged = true;
        }

        // 检查标题是否改变
        boolean titleChanged = false;
        if (tKnowledgeDocument.getTitle() != null &&
                !tKnowledgeDocument.getTitle().equals(existingDoc.getTitle())) {
            titleChanged = true;
        }

        // 1.更新本地数据库
        //设置更新时间
        tKnowledgeDocument.setUpdatedAt(LocalDateTime.now());

        //更新数据库
        boolean isUpdated = this.updateById(tKnowledgeDocument);
        
        if (isUpdated) {
            // 若有内容则生成/覆盖临时文件
            if (contentChanged||titleChanged) {


                
                try {
                    //获取配置的目录
                    String tempDir = fileUploadConfig.getPermanentDir();
                    Path tempDirPath = Paths.get(tempDir);



                    // 确保目录存在
                    if (!Files.exists(tempDirPath)) {
                        Files.createDirectories(tempDirPath);
                    }

                    // 使用title作为文件名（保持与上传时一致）
                    String filename = tKnowledgeDocument.getTitle();
                    if (filename == null || filename.trim().isEmpty()) {
                        // 如果title为空，使用ID作为文件名
                        String suffix = tKnowledgeDocument.getDocSuffix() != null ?
                                tKnowledgeDocument.getDocSuffix() : "txt";
                        filename = tKnowledgeDocument.getId() + "." + suffix;
                    } else {
                        // 确保文件名有正确的扩展名
                        if (!filename.contains(".")) {
                            String suffix = tKnowledgeDocument.getDocSuffix() != null ?
                                    tKnowledgeDocument.getDocSuffix() : "txt";
                            filename = filename + "." + suffix;
                        }
                    }


                    // 构建完整文件路径
                    Path filePath = tempDirPath.resolve(filename);

                    // 删除旧文件（如果存在）
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        logger.info("删除旧文件: {}", filePath);
                    }

                    // 创建新文件
                    if (tKnowledgeDocument.getContent() != null) {
                        Files.write(filePath, tKnowledgeDocument.getContent().getBytes("UTF-8"));
                        logger.info("创建新文件: {}", filePath);

                        //dify知识库id可能会有变化，从新传回的实体类中获取
                        String kbDifyId = knowledgeBaseService.getKnowledgeDetail(tKnowledgeDocument.getKbId()).getBaseId();
                        // 现在调用Dify更新API，传入File对象
                        try {
                            boolean difySyncResult = difySyncService.updateDocumentByFile(
                                    kbDifyId,
                                    existingDoc.getDifyDocumentId(),
                                    filePath.toFile()  // 传入File对象，不是路径字符串
                            );

                            if (difySyncResult) {
                                logger.info("Dify 同步更新成功, 本地ID: {}, Dify文档ID: {}, 文件: {}",
                                        tKnowledgeDocument.getId(), existingDoc.getDifyDocumentId(), filename);
                            } else {
                                logger.warn("Dify 同步更新失败, 本地ID: {}, Dify文档ID: {}",
                                        tKnowledgeDocument.getId(), existingDoc.getDifyDocumentId());
                            }
                        } catch (Exception e) {
                            logger.error("Dify 同步更新异常, 本地ID: {}", tKnowledgeDocument.getId(), e);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Dify 同步更新异常, 本地ID: {}", tKnowledgeDocument.getId(), e);
                }
            }



            //ES同步
            try{
                ESKnowledgeDocument esDocument = new ESKnowledgeDocument(tKnowledgeDocument.getId()+"",tKnowledgeDocument.getTitle(),tKnowledgeDocument.getContent());
                elasticsearchSyncService.syncDocumentToEs(esDocument);
            }catch (Exception e){
                throw new RuntimeException("文档更新到ES失败:"+e.getMessage(),e);
            }


            return tKnowledgeDocument;
        }
        return null;
    }

    @Override
    public PageResult<TKnowledgeDocument> pageByKbId(Long kbId, long current, long size) {
        if (kbId == null) {
            throw new BusinessException(400, "知识库ID不能为空");
        }
        if (current <= 0) {
            current = 1;
        }
        if (size <= 0) {
            size = 10;
        }

        Page<TKnowledgeDocument> page = new Page<>(current, size);
        QueryWrapper<TKnowledgeDocument> wrapper = new QueryWrapper<>();
        wrapper.eq("kb_id", kbId)
               .orderByDesc("updated_at");

        Page<TKnowledgeDocument> result = this.page(page, wrapper);
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getSize(),
                result.getCurrent(),
                result.getPages()
        );
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

    @Override
    public PageResult<TKnowledgeDocument> search(String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            throw new BusinessException(400, "keyword 不能为空");
        }
        EsSearchResult es = elasticsearchSyncService.searchDocumentIds(keyword, page, size);
        List<String> idStrs = es.getIds();

        if (idStrs == null || idStrs.isEmpty()) {
            PageResult<TKnowledgeDocument> pr = new PageResult<>();
            pr.setRecords(Collections.emptyList());
            pr.setTotal(0);
            pr.setSize(size);
            pr.setCurrent(page);
            pr.setPages(0);
            return pr;
        }

        List<Long> ids = idStrs.stream().map(Long::valueOf).collect(Collectors.toList());
        List<TKnowledgeDocument> list = this.list(new QueryWrapper<TKnowledgeDocument>().in("id", ids));
        Map<Long, TKnowledgeDocument> map = list.stream().collect(Collectors.toMap(TKnowledgeDocument::getId, Function.identity()));
        List<TKnowledgeDocument> ordered = ids.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList());

        long total = es.getTotal();
        long pages = size > 0 ? (total + size - 1) / size : 0;

        PageResult<TKnowledgeDocument> pr = new PageResult<>();
        pr.setRecords(ordered);
        pr.setTotal(total);
        pr.setSize(size);
        pr.setCurrent(page);
        pr.setPages(pages);
        return pr;
    }
    
}