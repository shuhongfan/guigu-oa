package com.shf.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.process.ProcessTemplate;

public interface ProcessTemplateService extends IService<ProcessTemplate> {

    /**
     * 获取分页列表
     * @param pageParam
     * @return
     */
    IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam);

    /**
     * 发布
     * @param id
     */
    void publish(Long id);
}
