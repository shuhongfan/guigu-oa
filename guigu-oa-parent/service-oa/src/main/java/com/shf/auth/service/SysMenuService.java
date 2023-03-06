package com.shf.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.system.SysMenu;
import com.shf.model.system.SysUser;
import com.shf.vo.system.AssginMenuVo;
import com.shf.vo.system.RouterVo;

import java.io.Serializable;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单树形数据
     * @return
     */
    List<SysMenu> findNodes();

    /**
     * 根据id删除菜单
     * @param id
     * @return
     */
    boolean removeById(Serializable id);

    /**
     * 根据角色获取授权权限数据
     * @return
     */
    List<SysMenu> findSysMenuByRoleId(Long roleId);

    /**
     * 保存角色权限
     * @param  assginMenuVo
     */
    void doAssign(AssginMenuVo assginMenuVo);

    /**
     * 根据用户id获取用户可以操作菜单列表
     * @param userId
     * @return
     */
    List<RouterVo> findUserMenuListByUser(Long userId);

    /**
     * 根据用户id获取用户可以操作菜单列表
     * @param userId
     * @return
     */
    List<String> findUserPermsByUserId(Long userId);
}
