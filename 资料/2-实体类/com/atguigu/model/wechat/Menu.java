package com.atguigu.model.wechat;

import com.atguigu.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "菜单")
@TableName("wechat_menu")
public class Menu extends BaseEntity {

    @ApiModelProperty(value = "id")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "网页 链接，用户点击菜单可打开链接")
    private String url;

    @ApiModelProperty(value = "菜单KEY值，用于消息接口推送")
    @TableField("meun_key")
    private String meunKey;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}