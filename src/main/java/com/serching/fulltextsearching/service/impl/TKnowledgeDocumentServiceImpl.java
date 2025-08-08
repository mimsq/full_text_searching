package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.repository.TKnowledgeDocumentRepository;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import com.serching.fulltextsearching.utils.DocumentTools;
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


    DocumentTools documentTools = new DocumentTools();

    @Autowired
    TKnowledgeDocumentRepository tKnowledgeDocumentRepository;

    @Autowired
    TKnowledgeDocumentMapper tKnowledgeDocumentMapper;


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
    public TKnowledgeDocument saveDocument(TKnowledgeDocument tKnowledgeDocument) {
        if (this.save(tKnowledgeDocument)){
            try{
                Long id = tKnowledgeDocument.getId();
                ESKnowledgeDocument esDocument = new ESKnowledgeDocument(id+"",tKnowledgeDocument.getTitle(),tKnowledgeDocument.getContent());
                tKnowledgeDocumentRepository.save(esDocument);
            }catch (Exception e){
                throw new RuntimeException("文档保存失败:"+e.getMessage(),e);
            }
            return tKnowledgeDocument;
        }
        return null;
    }
}
