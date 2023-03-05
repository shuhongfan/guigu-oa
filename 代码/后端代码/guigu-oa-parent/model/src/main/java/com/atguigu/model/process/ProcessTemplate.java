package com.atguigu.model.process;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "ProcessTemplate")
@TableName("oa_process_template")
public class ProcessTemplate extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "模板名称")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "图标路径")
	@TableField("icon_url")
	private String iconUrl;

	@ApiModelProperty(value = "processTypeId")
	@TableField("process_type_id")
	private Long processTypeId;

	@ApiModelProperty(value = "表单属性")
	@TableField("form_props")
	private String formProps;

	@ApiModelProperty(value = "表单选项")
	@TableField("form_options")
	private String formOptions;

	@ApiModelProperty(value = "描述")
	@TableField("description")
	private String description;

	@ApiModelProperty(value = "流程定义key")
	@TableField("process_definition_key")
	private String processDefinitionKey;

	@ApiModelProperty(value = "流程定义上传路process_model_id")
	@TableField("process_definition_path")
	private String processDefinitionPath;

	@ApiModelProperty(value = "流程定义模型id")
	@TableField("process_model_id")
	private String processModelId;

	@ApiModelProperty(value = "状态")
	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private String processTypeName;
}