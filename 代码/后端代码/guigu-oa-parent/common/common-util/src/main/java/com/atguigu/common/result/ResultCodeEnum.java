package com.atguigu.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    LOGIN_ERROR(208,"认证失败")
    ;

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code,String message) {
        this.code = code;
        this.message = message;
    }
}
