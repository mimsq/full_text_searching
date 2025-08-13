package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.TOperationLog;
import java.util.List;

import com.serching.fulltextsearching.vo.OperationLogVo;
import org.apache.ibatis.annotations.Param;

public interface OperationLogMapper extends BaseMapper<TOperationLog> {
    List<OperationLogVo> selectOperationLogWithDetails(
        @Param("kbId") Long kbId,
        @Param("pageNum") int pageNum,
        @Param("pageSize") int pageSize);
}
