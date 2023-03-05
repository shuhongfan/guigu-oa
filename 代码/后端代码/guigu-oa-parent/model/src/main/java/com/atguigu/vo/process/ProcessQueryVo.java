package com.atguigu.vo.process;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Process")
public class ProcessQueryVo {

	@ApiModelProperty(value = "关键字")
	private String keyword;

	@ApiModelProperty(value = "用户id")
	private Long userId;

	@TableField("process_template_id")
	private Long processTemplateId;

	@ApiModelProperty(value = "审批类型id")
	private Long processTypeId;

	private String createTimeBegin;
	private String createTimeEnd;

	@ApiModelProperty(value = "状态（0：默认 1：审批中 2：审批通过 -1：驳回）")
	private Integer status;


}