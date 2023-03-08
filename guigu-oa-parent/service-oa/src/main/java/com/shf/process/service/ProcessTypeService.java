package com.shf.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.process.ProcessType;

import java.util.List;

public interface ProcessTypeService extends IService<ProcessType> {
    /**
     * 获取全部审批分类及模板
     * @return
     */
    List<ProcessType> findProcessType();
}
