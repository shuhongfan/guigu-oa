package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.SysMenuMapper;
import com.shf.auth.service.SysMenuService;
import com.shf.auth.service.SysRoleMenuService;
import com.shf.auth.utils.MenuHelper;
import com.shf.common.config.exception.GuiguException;
import com.shf.model.system.SysMenu;
import com.shf.model.system.SysRoleMenu;
import com.shf.vo.system.AssginMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 菜单树形数据
     * @return
     */
    @Override
    public List<SysMenu> findNodes() {
//        全部权限列表
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        if (CollectionUtils.isEmpty(sysMenuList)) {
            return null;
        }

//        构建树形数据
        List<SysMenu> result = MenuHelper.buildTree(sysMenuList);
        return result;
    }

    /**
     * 根据id删除菜单
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        int count = count(wrapper);
        if (count > 0) {
            throw new GuiguException(201, "菜单不能删除");
        }

        sysMenuMapper.deleteById(id);
        return false;
    }

    /**
     * 根据角色获取授权权限数据
     * @return
     */
    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
//        1. 查询所有菜单 添加条件 status=1
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapper);

//        2. 根据角色id roleId查询 角色菜单关系表里面 角色id对应所有的菜单id
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(queryWrapper);

//        3. 根据获取菜单id,获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(sysRoleMenu -> sysRoleMenu.getMenuId()).collect(Collectors.toList());

//        3.1 拿着菜单id 和所有菜单集合里面id进行比较,如果相同封装
        allSysMenuList.stream().forEach(sysMenu -> sysMenu.setSelect(menuIdList.contains(sysMenu.getId())));

//        4. 返回规定格式菜单列表
        List<SysMenu> sysMenus = MenuHelper.buildTree(allSysMenuList);
        return sysMenus;
    }

    /**
     * 保存角色权限
     * @param  assginMenuVo
     */
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
//        1. 根据角色id 删除菜单角色表 分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, assginMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

//        2. 从参数里面获取角色新分配菜单id列表,进行遍历,把每个id数据添加菜单角色表
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        menuIdList.forEach(menuId->{
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        });
    }
}
