package com.serching.fulltextsearching.vo;

import com.serching.fulltextsearching.entity.KnowledgeBaseCategory;
import com.serching.fulltextsearching.entity.KnowledgeDocument;
import lombok.Data;

import java.util.List;

@Data
public class DocumentGroupVo
{
    private List<KnowledgeBaseCategory> categoryList;
    private List<KnowledgeDocument> documentList;
}
