package com.shf.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.system.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单树形数据
     * @return
     */
    List<SysMenu> findNodes();

}
