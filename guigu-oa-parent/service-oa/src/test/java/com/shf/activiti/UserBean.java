package com.shf.activiti;


import org.springframework.stereotype.Component;

@Component
public class UserBean {

    public String getUsername(int id) {
        if(id == 1) {
            return "zhangsan";
        }
        if(id == 2) {
            return "lisi";
        }
        return "admin";
    }
}