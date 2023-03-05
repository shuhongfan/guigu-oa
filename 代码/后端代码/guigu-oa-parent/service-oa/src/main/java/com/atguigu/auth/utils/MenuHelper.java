package com.atguigu.auth.utils;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    //使用递归方法建菜单
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        //创建list集合，用于最终数据
        List<SysMenu> trees = new ArrayList<>();
        //把所有菜单数据进行遍历
        for(SysMenu sysMenu:sysMenuList) {
            //递归入口进入
            //parentId=0是入口
            if(sysMenu.getParentId().longValue()==0) {
                trees.add(getChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }

    public static SysMenu getChildren(SysMenu sysMenu,
                                      List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        //遍历所有菜单数据，判断 id 和 parentId对应关系
        for(SysMenu it: sysMenuList) {
            if(sysMenu.getId().longValue() == it.getParentId().longValue()) {
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it,sysMenuList));
            }
        }
        return sysMenu;
    }
}
