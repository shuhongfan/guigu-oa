package com.atguigu.vo.system;

import lombok.Data;

@Data
public class SysPostQueryVo {
	
	//@ApiModelProperty(value = "岗位编码")
	private String postCode;

	//@ApiModelProperty(value = "岗位名称")
	private String name;

	//@ApiModelProperty(value = "状态（1正常 0停用）")
	private Boolean status;


}

