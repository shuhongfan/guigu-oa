package com.atguigu.model.process;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Process")
@TableName("oa_process")
public class Process extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "审批code")
	@TableField("process_code")
	private String processCode;

	@ApiModelProperty(value = "用户id")
	@TableField("user_id")
	private Long userId;

	@ApiModelProperty(value = "审批模板id")
	@TableField("process_template_id")
	private Long processTemplateId;

	@ApiModelProperty(value = "审批类型id")
	@TableField("process_type_id")
	private Long processTypeId;

	@ApiModelProperty(value = "标题")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "描述")
	@TableField("description")
	private String description;

	@ApiModelProperty(value = "表单值")
	@TableField("form_values")
	private String formValues;

	@ApiModelProperty(value = "流程实例id")
	@TableField("process_instance_id")
	private String processInstanceId;

	@ApiModelProperty(value = "当前审批人")
	@TableField("current_auditor")
	private String currentAuditor;

	@ApiModelProperty(value = "状态（0：默认 1：审批中 2：审批通过 -1：驳回）")
	@TableField("status")
	private Integer status;
}