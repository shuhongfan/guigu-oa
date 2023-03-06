package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.SysUserMapper;
import com.shf.auth.service.SysUserService;
import com.shf.model.system.SysUser;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    /**
     * 更改用户状态
     * 用户状态：状态（1：正常 0：停用），当用户状态为正常时，可以访问后台系统，当用户状态停用后，不可以登录后台系统
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Long id, Integer status) {
//        根据userid查询用户对象
        SysUser sysUser = baseMapper.selectById(id);
//        设置修改值
        sysUser.setStatus(status);
        baseMapper.updateById(sysUser);
    }

    /**
     * 根据用户名获取用户对象
     * @param username
     * @return
     */
    @Override
    public SysUser getUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = baseMapper.selectOne(wrapper);
        return sysUser;
    }
}
