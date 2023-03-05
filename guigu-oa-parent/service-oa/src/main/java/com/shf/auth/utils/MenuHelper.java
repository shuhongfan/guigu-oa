package com.shf.auth.utils;

import com.shf.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    /**
     * 构建树形数据
     * @param sysMenuList
     * @return
     */
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList){
//        创建list集合，用于最终数据
        ArrayList<SysMenu> trees = new ArrayList<>();

//        把所有菜单数据进行遍历
        sysMenuList.stream().forEach(sysMenu -> {
//            递归入口 parentId=0
            if (sysMenu.getParentId().longValue() == 0) {
                trees.add(getChildren(sysMenu, sysMenuList));
            }
        });
        return trees;
    }


    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<>());

//        递归遍历所有菜单数据，判断id和parentId对应关系
        sysMenuList.stream().forEach(child->{
            if (sysMenu.getId().longValue() == child.getParentId().longValue()) {
                sysMenu.getChildren().add(getChildren(child, sysMenuList));
            }
        });

        return sysMenu;
    }
}
