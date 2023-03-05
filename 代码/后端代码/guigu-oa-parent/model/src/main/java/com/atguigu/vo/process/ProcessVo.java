package com.atguigu.vo.process;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "Process")
public class ProcessVo {

	private Long id;

	private Date createTime;

	@ApiModelProperty(value = "审批code")
	private String processCode;

	@ApiModelProperty(value = "用户id")
	private Long userId;
	private String name;

	@TableField("process_template_id")
	private Long processTemplateId;
	private String processTemplateName;

	@ApiModelProperty(value = "审批类型id")
	private Long processTypeId;
	private String processTypeName;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "描述")
	private String description;

	@ApiModelProperty(value = "表单属性")
	private String formProps;

	@ApiModelProperty(value = "表单选项")
	private String formOptions;

	@ApiModelProperty(value = "表单属性值")
	private String formValues;

	@ApiModelProperty(value = "流程实例id")
	private String processInstanceId;

	@ApiModelProperty(value = "当前审批人")
	private String currentAuditor;

	@ApiModelProperty(value = "状态（0：默认 1：审批中 2：审批通过 -1：驳回）")
	private Integer status;

	private String taskId;

}