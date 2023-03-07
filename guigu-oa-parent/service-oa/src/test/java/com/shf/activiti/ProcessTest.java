package com.shf.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@SpringBootTest
public class ProcessTest {

    /**
     * 流程定义部署
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 启动流程实例
     */
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 查询任务
     */
    @Autowired
    private TaskService taskService;

    /**
     * 查询已处理任务
     */
    @Autowired
    private HistoryService historyService;

    /**
     * 单个文件部署
     */
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy);
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    @Test
    public void deployProcessByZip() {
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "process/qingjia.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程")
                .deploy();
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcess() {
//        创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");

//      输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动Id：" + processInstance.getActivityId());
    }

    /**
     * 查询当前个人待执行的任务
     */
    @Test
    public void findPendingTaskList() {
//        任务负责人
        String assignee = "zhangsan";

        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)  //只查询该任务负责人的任务
                .list();

        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan") //要查询的负责人
                .singleResult(); //返回一条

        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }

    /**
     * 查询已处理历史任务
     */
    @Test
    public void findProcessedTaskList() {
//        张三已处理过的历史任务
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("zhangsan")
                .finished()
                .list();

        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            System.out.println("流程实例id：" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id：" + historicTaskInstance.getId());
            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
            System.out.println("任务名称：" + historicTaskInstance.getName());
        }
    }

    /**
     * 查询流程定义
     */
    @Test
    public void findProcessDefinitionList(){
        List<ProcessDefinition> definitionList = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        //输出流程定义信息
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义 id="+processDefinition.getId());
            System.out.println("流程定义 name="+processDefinition.getName());
            System.out.println("流程定义 key="+processDefinition.getKey());
            System.out.println("流程定义 Version="+processDefinition.getVersion());
            System.out.println("流程部署ID ="+processDefinition.getDeploymentId());
        }
    }

    /**
     * 删除流程定义
     */
    public void deleteDeployment() {
        //部署id
        String deploymentId = "82e3bc6b-81da-11ed-8e03-7c57581a7819";
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式
        //repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 启动流程实例，添加businessKey
     */
    @Test
    public void startUpProcessAddBusinessKey(){
//        启动流程实例，指定业务标识businessKey，也就是请假申请单id
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("qingjia", "1001");
        System.out.println("业务id:"+instance.getBusinessKey());
    }


    /**
     * 全部流程实例挂起
     */
    @Test
    public void suspendProcessInstance() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia").singleResult();
//        获取到当前流程定义是否为暂停状态 suspended方法为true是暂停的，suspended方法为false是运行的
        boolean suspended = processDefinition.isSuspended();
        if (suspended) {
            // 暂定,那就可以激活
            // 参数1:流程定义的id  参数2:是否激活    参数3:时间点
            repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("流程定义:" + processDefinition.getId() + "激活");
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("流程定义:" + processDefinition.getId() + "挂起");
        }
    }

    /**
     * 单个流程实例挂起
     */
    @Test
    public void SingleSuspendProcessInstance() {
        String processInstanceId = "8bdff984-ab53-11ed-9b17-f8e43b734677";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //获取到当前流程定义是否为暂停状态   suspended方法为true代表为暂停   false就是运行的
        boolean suspended = processInstance.isSuspended();
        if (suspended) {
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例:" + processInstanceId + "激活");
        } else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例:" + processInstanceId + "挂起");
        }
    }

    /**
     * 流程部署
     */
    @Test
    public void deployProcess01() {
//      流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban01.bpmn20.xml")
                .name("加班申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startUpProcess01() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee1","zhangsan");
        variables.put("assignee2","lisi");

        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", variables);

        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    @Test
    public void deployProcess02() {
        // 流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban02.bpmn20.xml")
                .name("加班申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startUpProcess02() {
        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban");
        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    @Test
    public void deployProcess03() {
        // 流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban03.bpmn20.xml")
                .name("加班申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startUpProcess03() {
        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban");
        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    /**
     * 启动流程时设置变量
     */
    @Test
    public void startUpProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee1", "zhangsan");
        variables.put("assignee2", "lisi");

        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", variables);

        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    /**
     * 在任务办理时设置流程变量
     */
    @Test
    public void completTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")  //要查询的负责人
                .singleResult();//返回一条

        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee2", "zhao");
        //完成任务,参数：任务id
        taskService.complete(task.getId(), variables);
    }

    /**
     * 通过当前流程实例设置
     */
    @Test
    public void processInstanceIdSetVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee2", "wang");
        runtimeService.setVariables("1c347a90-82c6-11ed-96ca-7c57581a7819", variables);
    }

    @Test
    public void completLocalTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")  //要查询的负责人
                .singleResult();//返回一条

        // 设置local变量，作用域为该任务
        taskService.setVariableLocal(task.getId(),"assignee2","li");
        // 查看local变量
        System.out.println(taskService.getVariableLocal(task.getId(), "assignee2"));
        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }

    /**
     * 组任务办理流程
     */
    @Test
    public void deployProcess04() {
        // 流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban04.bpmn20.xml")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban");
        System.out.println(processInstance.getId());
    }

    /**
     * 查询组任务
     */
    @Test
    public void findGroupTaskList() {
        //查询组任务
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("tom01")//根据候选人查询
                .list();
        for (Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 拾取组任务
     */
    @Test
    public void claimTask(){
        //拾取任务,即使该用户不是候选人也能拾取(建议拾取时校验是否有资格)
        //校验该用户有没有拾取任务的资格
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("tom01")//根据候选人查询
                .singleResult();
        if(task!=null){
            //拾取任务
            taskService.claim(task.getId(), "tom01");
            System.out.println("任务拾取成功");
        }
    }

    /**
     * 查询个人待办任务
     */
    @Test
    public void findGroupPendingTaskList() {
        //任务负责人
        String assignee = "zhangsan01";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)//只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 办理个人任务
     */
    @Test
    public void completGroupTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan01")  //要查询的负责人
                .singleResult();//返回一条
        taskService.complete(task.getId());
    }

    /**
     * 归还组任务
     */
    @Test
    public void assigneeToGroupTask() {
        String taskId = "d96c3f28-825e-11ed-95b4-7c57581a7819";
        // 任务负责人
        String userId = "zhangsan01";
        // 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task != null) {
            // 如果设置为null，归还组任务,该 任务没有负责人
            taskService.setAssignee(taskId, null);
        }
    }


    /**
     * 任务交接
     */
    @Test
    public void assigneeToCandidateUser() {
        // 当前待办任务
        String taskId = "d96c3f28-825e-11ed-95b4-7c57581a7819";
        // 校验zhangsan01是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee("zhangsan01")
                .singleResult();
        if (task != null) {
            // 将此任务交给其它候选人zhangsan02办理该 任务
            taskService.setAssignee(taskId, "zhangsan02");
        }
    }


//    ============  排他网关  ====================
    //1 部署流程定义
    @Test
    public void deployProcess2() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia2.bpmn20.xml")
                .name("请假申请流程2")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    //2 启动流程实例
    @Test
    public void startProcessInstance2() {
        Map<String,Object> map = new HashMap<>();
        //设置请假天数
        map.put("day","3");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia2",map);
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    //3 查询个人的代办任务--zhao6
    @Test
    public void findTaskList2() {
        String assign = "gousheng";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for(Task task : list) {
            System.out.println("流程实例id："+task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    //完成任务
    @Test
    public void completTask2() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("gousheng")  //要查询的负责人
//                .taskAssignee("gouwa")
                .singleResult();//返回一条

        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }


    //    ============  并行网关  ====================
    //1 部署流程定义
    @Test
    public void deployProcess3() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia3.bpmn20.xml")
                .name("请假申请流程3")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    //2 启动流程实例
    @Test
    public void startProcessInstance3() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia3");
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    //3 查询个人的代办任务--zhao6
    @Test
    public void findTaskList3() {
//        String assign = "wuangwu";
//        String assign = "gouwa";
        String assign = "xiaoli";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for(Task task : list) {
            System.out.println("流程实例id："+task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    //完成任务
    @Test
    public void completTask3() {
        Task task = taskService.createTaskQuery()
//                .taskAssignee("gousheng")  //要查询的负责人
                .taskAssignee("gouwa")
                .singleResult();//返回一条

        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }

}
