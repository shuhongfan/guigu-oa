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
import com.shf.model.system.SysUser;
import com.shf.vo.system.AssginMenuVo;
import com.shf.vo.system.MetaVo;
import com.shf.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * 根据用户id获取用户可以操作菜单列表
     * @param userId
     * @return
     */
    @Override
    public List<RouterVo> findUserMenuListByUser(Long userId) {
        List<SysMenu> sysMenuList = null;
//        1. 判断当前用户是否是管理员 userId=1是管理员
//        1.1 如果是管理员,查询所有菜单列表
        if (userId.longValue() == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
//        1.2 如果不是管理员,根据userId查询可以操作菜单列表
//        多表关联查询:用户角色关系表 角色菜单关系表 菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

//        2. 把查询出来数据列表构建成框架要求的路由数据结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
//        构建成框架要求的数据结构
        List<RouterVo> routerVoList = buildRouter(sysMenuTreeList);
        return routerVoList;
    }

    /**
     * 构建成框架要求的数据结构
     * @param menus
     * @return
     */
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
//        创建list集合，存储最终数据
        ArrayList<RouterVo> routerVos = new ArrayList<>();

        menus.forEach(menu->{
            RouterVo routerVo = new RouterVo();
            routerVo.setHidden(false);
            routerVo.setAlwaysShow(false);
            routerVo.setPath(getRouterPath(menu));
            routerVo.setComponent(menu.getComponent());
            routerVo.setMeta(new MetaVo(menu.getName(),menu.getIcon()));

//            下层数据部分
            List<SysMenu> children = menu.getChildren();
            if (menu.getType().intValue() == 1) {
//                加载出来下面隐藏路由
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());

                hiddenMenuList.forEach(hiddenMenu -> {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true); // 隐藏路由
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));

                    routerVos.add(hiddenRouter);
                });
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if (children.size() > 0) {
                        routerVo.setAlwaysShow(true);
                    }
//                    递归
                    routerVo.setChildren(buildRouter(children));
                }
            }
            routerVos.add(routerVo);
        });
        return routerVos;
    }

    /**
     * 获取路由地址
     * @param sysMenu
     * @return
     */
    public String getRouterPath(SysMenu sysMenu) {
        String routerPath = "/" + sysMenu.getPath();
        if (sysMenu.getParentId().intValue() != 0) {
            routerPath = sysMenu.getPath();
        }
        return routerPath;
    }

    /**
     * 根据用户id获取用户可以操作菜单列表
     * @param userId
     * @return
     */
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
//        1. 判断是否是管理员,如果是管理员,查询所有按钮列表
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
//        2. 如果不是管理员,根据userId查询可以操作按钮列表
//        多表关联查询:用户角色关系表 角色菜单关系表 菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

//        3.  从查询出来的数据里面,获取可以操作按钮值的list集合,返回
        List<String> permsList = sysMenuList.stream().filter(sysMenu -> sysMenu.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());

        return permsList;
    }
}
