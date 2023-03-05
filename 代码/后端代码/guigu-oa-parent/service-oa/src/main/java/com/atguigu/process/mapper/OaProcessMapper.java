package com.atguigu.process.mapper;

import com.atguigu.model.process.Process;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-02-14
 */
public interface OaProcessMapper extends BaseMapper<Process> {

    //审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);
}
