package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.dto.EsSearchResult;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.service.ElasticsearchSyncService;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import com.serching.fulltextsearching.utils.DocumentTools;
import com.serching.fulltextsearching.service.DifySyncService;
import com.serching.fulltextsearching.config.FileUploadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        String originalNameTest = file.getOriginalFilename();
        String suffix = (originalNameTest != null && originalNameTest.contains("."))
                ? originalNameTest.substring(originalNameTest.lastIndexOf(".") + 1).toLowerCase()
                : "";
        if (!("txt".equals(suffix) || "md".equals(suffix))) {
            throw new BusinessException(400, "目前仅支持 .txt 或 .md 文件");
        }

        // 确保临时目录存在
        String tempDir = fileUploadConfig.getTempDir();
        Path tempDirPath;
        
        // 处理路径，确保是绝对路径
        if (tempDir.startsWith("./") || tempDir.startsWith(".\\")) {
            // 相对路径，转换为绝对路径
            String currentDir = System.getProperty("user.dir");
            tempDirPath = Paths.get(currentDir, tempDir.substring(2));
        } else if (tempDir.startsWith("/") || (tempDir.length() > 1 && tempDir.charAt(1) == ':')) {
            // 绝对路径
            tempDirPath = Paths.get(tempDir);
        } else {
            // 相对路径，相对于当前工作目录
            tempDirPath = Paths.get(System.getProperty("user.dir"), tempDir);
        }
        
        // 确保目录存在
        if (!Files.exists(tempDirPath)) {
            try {
                Files.createDirectories(tempDirPath);
                logger.info("创建临时目录: {}", tempDirPath.toAbsolutePath());
            } catch (IOException e) {
                logger.error("创建临时目录失败: {}", tempDirPath.toAbsolutePath(), e);
                throw new BusinessException(500, "创建临时目录失败：" + e.getMessage(), e);
            }
        }
        
        // 验证目录权限
        if (!Files.isDirectory(tempDirPath) || !Files.isWritable(tempDirPath)) {
            throw new BusinessException(500, "临时目录不可写或不是目录: " + tempDirPath.toAbsolutePath());
        }

        //保存临时文件并抽取文本
        String originalName = file.getOriginalFilename();
        Path sourceDirPath = tempDirPath; // 使用配置的目录作为源文件目录
        Path sourceFilePath = sourceDirPath.resolve(originalName); // 保持原文件名
        File sourceFile = sourceFilePath.toFile();
        
        logger.info("准备保存文件到: {}", sourceFile.getAbsolutePath());
        
        try {
            // 将MultipartFile转换为File
            file.transferTo(sourceFile);
            
            // 验证文件是否成功创建
            if (!sourceFile.exists()) {
                throw new BusinessException(500, "文件创建失败：文件不存在");
            }
            
            if (sourceFile.length() == 0) {
                throw new BusinessException(500, "文件创建失败：文件大小为0");
            }
            
            logger.info("文件创建成功: {} (大小: {} bytes)", sourceFile.getAbsolutePath(), sourceFile.length());
            
        } catch (IOException e) {
            logger.error("文件保存失败: {}", e.getMessage(), e);
            // 尝试清理可能创建的不完整文件
            if (sourceFile.exists()) {
                try {
                    Files.delete(sourceFilePath);
                    logger.info("清理不完整的文件: {}", sourceFile.getAbsolutePath());
                } catch (IOException cleanupEx) {
                    logger.warn("清理不完整文件失败: {}", sourceFile.getAbsolutePath(), cleanupEx);
                }
            }
            throw new BusinessException(500, "文件保存失败：" + e.getMessage(), e);
        }

        String content;
        try {
            content = documentTools.extractTextFromPath(sourceFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("文件内容提取失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件内容提取失败：" + e.getMessage(), e);
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
            difyDocmentId = difySyncService.createDocumentByFile(kbId,sourceFile);
        } catch (Exception e) {
            throw new BusinessException("Dify创建文档失败：" + e.getMessage());
        }




        //组装实体并保存
        TKnowledgeDocument doc = new TKnowledgeDocument();
        doc.setKbId(kb.getId());//TKnowledgeDocument的KbId对应TKnowledgeBase的baseId,都是dify中用于标识的id，而非数据库或者ES中id（自增Long）
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

        // ES同步
        Long id = doc.getId();
        //处理elasticsearch的文档存储
        ESKnowledgeDocument esDocument = new ESKnowledgeDocument(id+"",file.getOriginalFilename(),content);
        try{
            elasticsearchSyncService.syncDocumentToEs(esDocument);
        }catch (Exception e){
            throw new RuntimeException("文档保存失败:"+e.getMessage(),e);
        }

        return doc;
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
                    String tempDir = fileUploadConfig.getTempDir();
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