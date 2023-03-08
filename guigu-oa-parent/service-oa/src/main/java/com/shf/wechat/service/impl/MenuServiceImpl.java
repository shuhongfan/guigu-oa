package com.shf.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.wechat.mapper.MenuMapper;
import com.shf.wechat.service.MenuService;
import com.shf.model.wechat.Menu;
import com.shf.vo.wechat.MenuVo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private WxMpService wxMpService;

    /**
     * 获取全部菜单
     * @return
     */
    @Override
    public List<MenuVo> findMenuInfo() {
        ArrayList<MenuVo> list = new ArrayList<>();

//        1. 查询所有菜单list集合
        List<Menu> menuList = baseMapper.selectList(null);

//        2. 查询所有一级菜单 parentid=0，返回list集合
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());

//        3. 一级菜单list集合遍历，得到每个一级菜单
        for (Menu oneMenu : oneMenuList) {
//            一级菜单Menu --- MenuVo
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu, oneMenuVo);

//        4. 获取每个一级菜单里面所有的二级菜单id和parent_id比较
//            一级菜单id和其他菜单parent_id
            List<Menu> twoMenuList = menuList.stream().filter(menu -> menu.getParentId() == oneMenu.getId()).collect(Collectors.toList());
            ArrayList<MenuVo> twoMenuVoList = new ArrayList<>();
            for (Menu menu : twoMenuList) {
                MenuVo menuVo = new MenuVo();
                BeanUtils.copyProperties(menu, menuVo);
                twoMenuVoList.add(menuVo);
            }
            oneMenuVo.setChildren(twoMenuVoList);

//        5. 把一级菜单里面所有二级菜单获取到，封装一级菜单
            list.add(oneMenuVo);
        }

        return list;
    }

    /**
     * 同步菜单
     */
    @Override
    public void syncMenu() {
        //1 菜单数据查询出来，封装微信要求菜单格式
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://ggkt1.vipgz1.91tunnel.com/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://ggkt1.vipgz1.91tunnel.com#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        //2 调用工具里面的方法实现菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除菜单
     */
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
