package com.atguigu.process.service;

import com.atguigu.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {

    void record(Long processId,Integer status,String description);
}
