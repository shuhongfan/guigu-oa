package com.shf.auth.service.impl;

import com.shf.auth.service.SysMenuService;
import com.shf.auth.service.SysUserService;
import com.shf.auth.service.UserDetailsService;
import com.shf.model.system.SysUser;
import com.shf.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 根据用户名获取用户对象（获取不到直接抛异常）
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserByUserName(username);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }

//        根据userId查询用户操作权限数据
        List<String> perms = sysMenuService.findUserPermsByUserId(sysUser.getId());

//        创建list集合，封装最终权限数据
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

//        查询list集合遍历
        perms.forEach(perm->authorities.add(new SimpleGrantedAuthority(perm.trim())));

        return new CustomUser(sysUser, authorities);
    }
}
