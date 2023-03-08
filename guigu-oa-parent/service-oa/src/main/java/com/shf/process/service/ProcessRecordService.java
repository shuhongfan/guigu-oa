package com.shf.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.process.ProcessRecord;

public interface ProcessRecordService extends IService<ProcessRecord> {

    /**
     * 记录操作行为
     * @param processId
     * @param status
     * @param description
     */
    void record(Long processId, Integer status, String description);
}
