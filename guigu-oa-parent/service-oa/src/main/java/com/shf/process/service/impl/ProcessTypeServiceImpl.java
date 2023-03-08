package com.shf.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.process.mapper.ProcessTypeMapper;
import com.shf.process.service.ProcessTemplateService;
import com.shf.process.service.ProcessTypeService;
import com.shf.model.process.ProcessTemplate;
import com.shf.model.process.ProcessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {

    @Autowired
    private ProcessTemplateService processTemplateService;

    /**
     * 获取全部审批分类及模板
     * @return
     */
    @Override
    public List<ProcessType> findProcessType() {
//        1. 查询所有审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);

//        2.遍历返回所有审批分类list集合
        for (ProcessType processType : processTypeList) {
//        3. 得到每个审批分类，根据审批分类id查询对应审批模板
            Long id = processType.getId(); // 审批分类id

//            根据审批分类id查询对应审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = Wrappers.<ProcessTemplate>lambdaQuery().eq(ProcessTemplate::getProcessTypeId, id);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

//        4. 根据审批分类id查询对应审批模板数据(List)封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
