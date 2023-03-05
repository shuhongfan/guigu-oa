package com.atguigu.model.system;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "角色菜单")
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "角色id")
	@TableField("role_id")
	private Long roleId;

	@ApiModelProperty(value = "菜单id")
	@TableField("menu_id")
	private Long menuId;

}

