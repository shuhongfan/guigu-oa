package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.SysRoleMenuMapper;
import com.shf.auth.service.SysRoleMenuService;
import com.shf.model.system.SysRoleMenu;
import org.springframework.stereotype.Service;

@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper,SysRoleMenu> implements SysRoleMenuService {
}
