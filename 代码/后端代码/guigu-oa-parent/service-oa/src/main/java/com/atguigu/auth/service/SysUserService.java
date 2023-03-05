package com.atguigu.auth.service;

import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-02
 */
public interface SysUserService extends IService<SysUser> {

    //更新状态
    void updateStatus(Long id, Integer status);

    //根据用户名进行查询
    SysUser getUserByUserName(String username);

    Map<String, Object> getCurrentUser();
}
