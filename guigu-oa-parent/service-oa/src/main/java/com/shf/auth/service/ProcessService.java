package com.shf.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.vo.process.ApprovalVo;
import com.shf.vo.process.ProcessFormVo;
import com.shf.vo.process.ProcessQueryVo;
import com.shf.vo.process.ProcessVo;
import com.shf.model.process.Process;

import java.util.Map;

public interface ProcessService extends IService<Process> {
    /**
     * 获取分页列表
     * @param pageParam
     * @param processQueryVo
     * @return
     */
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    /**
     * 流程部署
     * @param deployPath
     */
    void deployByZip(String deployPath);

    /**
     * 启动流程
     * @param processFormVo
     */
    void startUp(ProcessFormVo processFormVo);

    /**
     * 查询待处理任务列表
     * @param pageParam
     * @return
     */
    IPage<ProcessVo> findPending(Page<Process> pageParam);

    /**
     * 获取审批详情
     * @param id
     * @return
     */
     Map<String, Object> show(Long id);

    /**
     * 审批
     * @param approvalVo
     */
    void approve(ApprovalVo approvalVo);

    /**
     * 已处理
     * @param pageParam
     * @return
     */
    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    /**
     * 已发起
     * @param pageParam
     * @return
     */
    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
