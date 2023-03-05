package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.SysMenuMapper;
import com.shf.auth.service.SysMenuService;
import com.shf.model.system.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 菜单树形数据
     * @return
     */
    @Override
    public List<SysMenu> findNodes() {
//        全部权限列表
        List<SysMenu> sysMenuList = list();
        if (CollectionUtils.isEmpty(sysMenuList)) {
            return null;
        }

//        构建树形数据


        return null;
    }
}
