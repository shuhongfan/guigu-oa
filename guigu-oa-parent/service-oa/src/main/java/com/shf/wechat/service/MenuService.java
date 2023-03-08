package com.shf.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shf.model.wechat.Menu;
import com.shf.vo.wechat.MenuVo;

import java.util.List;

public interface MenuService extends IService<Menu> {
    /**
     * 获取全部菜单
     * @return
     */
    List<MenuVo> findMenuInfo();

    /**
     * 同步菜单
     */
    void syncMenu();

    /**
     * 删除菜单
     */
    void removeMenu();
}
