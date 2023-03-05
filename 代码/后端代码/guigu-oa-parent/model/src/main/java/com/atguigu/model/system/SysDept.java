package com.atguigu.model.system;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "部门")
@TableName("sys_dept")
public class SysDept extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "部门名称")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "上级部门id")
	@TableField("parent_id")
	private Long parentId;

	@ApiModelProperty(value = "树结构")
	@TableField("tree_path")
	private String treePath;

	@ApiModelProperty(value = "排序")
	@TableField("sort_value")
	private Integer sortValue;

	@ApiModelProperty(value = "负责人")
	@TableField("leader")
	private String leader;

	@ApiModelProperty(value = "电话")
	@TableField("phone")
	private String phone;

	@ApiModelProperty(value = "状态（1正常 0停用）")
	@TableField("status")
	private Integer status;

	@ApiModelProperty(value = "下级部门")
	@TableField(exist = false)
	private List<SysDept> children;

}