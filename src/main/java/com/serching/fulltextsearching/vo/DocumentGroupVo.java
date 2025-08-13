package com.serching.fulltextsearching.vo;

import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import lombok.Data;

import java.util.List;

@Data
public class DocumentGroupVo
{
    private List<TKnowledgeBaseCategory> categoryList;
    private List<TKnowledgeDocument> documentList;
}
