package com.atguigu.vo.system;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;

@Data
public class SysLoginLogQueryVo {
	
	@ApiModelProperty(value = "用户账号")
	private String username;

	private String createTimeBegin;
	private String createTimeEnd;

}

