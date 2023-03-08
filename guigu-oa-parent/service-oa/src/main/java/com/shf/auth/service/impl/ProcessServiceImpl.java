package com.shf.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.ProcessMapper;
import com.shf.auth.service.*;
import com.shf.common.result.Result;
import com.shf.model.process.Process;
import com.shf.model.process.ProcessRecord;
import com.shf.model.process.ProcessTemplate;
import com.shf.model.system.SysUser;
import com.shf.security.custom.LoginUserInfoHelper;
import com.shf.vo.process.ApprovalVo;
import com.shf.vo.process.ProcessFormVo;
import com.shf.vo.process.ProcessQueryVo;
import com.shf.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Proc;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    /**
     * 获取分页列表
     * @param pageParam
     * @param processQueryVo
     * @return
     */
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        return baseMapper.selectPage(pageParam, processQueryVo);
    }

    /**
     * 流程部署
     * @param deployPath
     */
    @Override
    public void deployByZip(String deployPath) {
//        定义ZIP输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

//        流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    /**
     * 启动流程
     * @param processFormVo
     */
    @Override
    public void startUp(ProcessFormVo processFormVo) {
//        1. 根据当前用户id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

//        2. 根据审批模板id把模板信息查询
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());

//        3. 保存提交审批信息倒业务表 oa_process
        Process process = new Process();
//        processFormVo 复制到 process对象里面
        BeanUtils.copyProperties(processFormVo,process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        baseMapper.insert(process);

//        4. 启动流程实例 -  RuntimeService
//        4.1 流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();

//        4.2 业务key processId
        String businessKey = String.valueOf(process.getId());

//        4.3 流程参数 form表单json数据，转换map集合
        String formValues = processFormVo.getFormValues();
//        formData
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");

//        遍历formData得到内容，封装map集合
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("data", map);

//        启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);

//        5. 查询下一个审批人
//        审批人可能为多个
        List<Task> taskList = getCurrentTaskList(processInstance.getId());
        ArrayList<String> nameList = new ArrayList<>();
        taskList.forEach(task -> {
            String assigneeName = task.getAssignee();
            SysUser user = sysUserService.getUserByUserName(assigneeName);
            String name = user.getName();
            nameList.add(name);

//        6. 推送消息
        });
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(nameList.toArray(), ",") + "审批");

//        7. 业务和流程关联
        baseMapper.updateById(process);

//        8. 记录草纸审批信息记录
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    /**
     * 查询待处理任务列表
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
//        1. 封装查询条件，根据当前登录的用户名称
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();

//        2. 调用方法分页条件查询，返回list集合，代办任务集合
//        listPage两个参数 开始位置，每页显示记录数
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<Task> taskList = query.listPage(begin, size);
        long totalCount = query.count();

//        3. 封装返回list集合数据到List<ProcessVo>里面
        ArrayList<ProcessVo> processVoArrayList = new ArrayList<>();
        for (Task task : taskList) {
//            从task获取流程实例id
            String processInstanceId = task.getProcessInstanceId();

//            根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();

//            从流程实例对象获取业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }

//            根据业务key获取process对象
            Process process = baseMapper.selectById(businessKey);

//            Process对象 复制 ProcessVo 对象
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());

//            放到list集合
            processVoArrayList.add(processVo);
        }

//        4. 封装返回IPage集合
        Page<ProcessVo> page = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        page.setRecords(processVoArrayList);
        return page;
    }

    /**
     * 获取审批详情
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> show(Long id) {
//        1. 根据流程id获取流程信息Process
        Process process = baseMapper.selectById(id);

//        2. 根据流程id获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> processRecordList = processRecordService.list(wrapper);

//        3. 根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

//        4. 判断当前用户是否可以审批
//        可以看到信息不一定能审批，不能重复审批
        boolean isApprove = false;
        List<Task> taskList = getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : taskList) {
            String username = LoginUserInfoHelper.getUsername();
            if (task.getAssignee().equals(username)) {
                isApprove = true;
            }
        }

//        5. 查询数据封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    /**
     * 审批
     * @param approvalVo
     */
    @Override
    public void approve(ApprovalVo approvalVo) {
//        1. 从approvalVo获取任务id，根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

//        2. 判断审批状态值
        if (approvalVo.getStatus() == 1) {
//        2.1 状态值=1 审批通过
            HashMap<String, Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);
        } else {
//        2.2 状态值=-1 驳回，流程直接结束
            endTask(taskId);
        }

//        3.记录审批相关过程信息
        String description = approvalVo.getStatus().intValue() == 1 ? "已经通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), description);

//        4. 查询下一个审批人，更新流程表记录process表记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
//        查询任务
        List<Task> taskList = getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)) {
            ArrayList<String> assigneeList = new ArrayList<>();
            for (Task task : taskList) {
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getUserByUserName(assignee);
                assigneeList.add(sysUser.getName());

//                消息推送
            }
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
            process.setStatus(1);
        } else {
            if (approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（通过）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（驳回）");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);
    }

    /**
     * 结束流程
     * @param taskId
     */
    private void endTask(String taskId) {
//        1. 根据任务id获取任务对象 task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

//        2. 获取流程定义模型 BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

//        3. 获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = endEventList.get(0);

//        4. 当前流向节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());

//        5. 清理当前流动方向
        currentFlowNode.getOutgoingFlows().clear();

//        6. 创建信流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);

//        7. 当前节点指向新方向
        ArrayList newSequenceFlowList = new ArrayList();
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

//        8. 完成当前任务
        taskService.complete(taskId);
    }


    /**
     * 已处理
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
//        封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished();

//        调用放条件分页查询,返回list集合
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<HistoricTaskInstance> historicTaskInstanceList = query.listPage(begin, size);
        long totalCount = query.count();

//        遍历返回list集合,封装list集合
        ArrayList<ProcessVo> processVoArrayList = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
//            流程实例id
            String processInstanceId = historicTaskInstance.getProcessInstanceId();
//            根据流程实例id查询获取process信息
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId, processInstanceId);
            Process process = baseMapper.selectOne(wrapper);

            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
//            放到list
            processVoArrayList.add(processVo);
        }

//        IPage封装分页查询所有数据,返回
        Page<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        pageModel.setRecords(processVoArrayList);
        return pageModel;
    }

    /**
     * 已发起
     * @param pageParam
     * @return
     */
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo item : page.getRecords()) {
            item.setTaskId("0");
        }
        return page;
    }

    /**
     * 获取当前任务列表
     * @param processInstanceId
     * @return
     */
    private List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return taskList;
    }

}
