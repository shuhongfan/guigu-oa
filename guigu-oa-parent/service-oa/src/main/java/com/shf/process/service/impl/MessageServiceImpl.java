package com.shf.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shf.auth.service.SysUserService;
import com.shf.model.process.Process;
import com.shf.model.process.ProcessTemplate;
import com.shf.model.system.SysUser;
import com.shf.process.service.MessageService;
import com.shf.process.service.ProcessService;
import com.shf.process.service.ProcessTemplateService;
import com.shf.security.custom.LoginUserInfoHelper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    /**
     * 推送待审批人员
     * @param processId
     * @param userId
     * @param taskId
     */
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        //查询流程信息
        Process process = processService.getById(processId);

        //根据userid查询要推送人信息
        SysUser sysUser = sysUserService.getById(userId);

        //查询审批模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        //获取提交审批人的信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());

        //获取要给的消息人的openid
        String openId = sysUser.getOpenId();
        if(StringUtils.isEmpty(openId)) {
            //TODO 为了测试，添加默认值，当前自己的openid
            openId = "oY0ph50qlbcBhSNokcTlH1lvlPew";
        }

        //设置消息发送信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                //给谁发送消息，openid值
                .toUser(openId)
                //创建模板信息的id值
                .templateId("\tek3zQvaEDZWW8-p4ak76oEFa_dFb-PkN4bOAsLR35Uk")
                //点击消息，跳转的地址
                .url("http://ggkt1.vipgz1.91tunnel.com/#/show/" + processId + "/" + taskId)
                .build();

//        设置内容
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }

        //设置模板里面参数值
        templateMessage
                .addData(new WxMpTemplateData("first",
                        submitSysUser.getName()+"提交"+processTemplate.getName()+",请注意查看","#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        //调用方法发送
        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println(msg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 审批后推送提交审批人员
     * @param processId
     * @param userId
     * @param status
     */
    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        Process process = processService.getById(processId);
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if(StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("I0kVeto7T0WIDP6tyoHh-hx83wa9_pe7Nx9eT93-6sc")//模板id
                .url("http://oa.atguigu.cn/#/show/"+processId+"/0")//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = null;
        try {
            msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        log.info("推送消息返回：{}", msg);
    }
}
