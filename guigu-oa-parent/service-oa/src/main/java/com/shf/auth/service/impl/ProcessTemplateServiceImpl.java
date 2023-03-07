package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.ProcessTemplateMapper;
import com.shf.auth.service.ProcessService;
import com.shf.auth.service.ProcessTemplateService;
import com.shf.auth.service.ProcessTypeService;
import com.shf.model.process.ProcessTemplate;
import com.shf.model.process.ProcessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProcessTemplateServiceImpl extends ServiceImpl<ProcessTemplateMapper, ProcessTemplate> implements ProcessTemplateService {

    @Autowired
    private ProcessTypeService processTypeService;

    @Autowired
    private ProcessService processService;

    /**
     * 获取分页列表
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam) {
//        1. 调用mapper的方法实现分页查询
        LambdaQueryWrapper<ProcessTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ProcessTemplate::getId);
        IPage<ProcessTemplate> page = baseMapper.selectPage(pageParam, queryWrapper);

//        2. 第一部分页查询返回分页数据，从分页数据获取列表list集合
        List<ProcessTemplate> processTemplateList = page.getRecords();

//        3. 遍历list集合，得到每个对象的审批类型id
        for (ProcessTemplate processTemplate : processTemplateList) {
//            得到每个对象的审批类型id
            Long processTypeId = processTemplate.getProcessTypeId();

//            4. 根据审批类型id，查询获取对应名称
            LambdaQueryWrapper<ProcessType> wrapper = Wrappers.<ProcessType>lambdaQuery().eq(ProcessType::getId, processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            if (processType == null) {
                continue;
            }
//            5.完成最终封装processTypeName
            processTemplate.setProcessTypeName(processType.getName());
        }

        return page;
    }

    /**
     * 发布
     * @param id
     */
    @Override
    @Transactional
    public void publish(Long id) {
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

//        发布在线流程设计
        if (!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())) {
            processService.deployByZip(processTemplate.getProcessDefinitionPath());
        }
    }
}
