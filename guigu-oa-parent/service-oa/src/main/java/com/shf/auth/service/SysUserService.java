package com.shf.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.system.SysUser;

public interface SysUserService extends IService<SysUser> {
    /**
     * 更改用户状态
     * 用户状态：状态（1：正常 0：停用），当用户状态为正常时，可以访问后台系统，当用户状态停用后，不可以登录后台系统
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据用户名获取用户对象
     * @param username
     * @return
     */
    SysUser getUserByUserName(String username);
}
