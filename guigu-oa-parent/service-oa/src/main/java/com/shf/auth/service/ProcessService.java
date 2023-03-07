package com.shf.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.vo.process.ProcessQueryVo;
import com.shf.vo.process.ProcessVo;

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
}
