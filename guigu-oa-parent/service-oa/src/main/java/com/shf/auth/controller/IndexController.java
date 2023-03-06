package com.shf.auth.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shf.auth.service.SysMenuService;
import com.shf.auth.service.SysRoleMenuService;
import com.shf.auth.service.SysUserService;
import com.shf.common.config.exception.GuiguException;
import com.shf.common.jwt.JwtHelper;
import com.shf.common.result.Result;
import com.shf.common.utils.MD5;
import com.shf.model.system.SysUser;
import com.shf.vo.system.LoginVo;
import com.shf.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
//        1. 获取输入用户名和密码
        String username = loginVo.getUsername();

//        2. 根据用户名查询数据库
        SysUser sysUser = sysUserService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));

//        3. 用户信息是否存在
        if (sysUser == null) {
            throw new GuiguException(201, "用户不存在");
        }

//        4. 判断密码是否正确
//        数据库存储MD5密码
        String password = sysUser.getPassword();

//        获取输入的密码
        String password_input = MD5.encrypt(loginVo.getPassword());

        if (!password_input.equals(password_input)) {
            throw new GuiguException(201, "密码错误");
        }

//        5. 判断用户是否被禁用
        if (sysUser.getStatus().intValue() == 0) {
            throw new GuiguException(201, "用户已经被禁用");
        }

//        6. 使用JWT根据用户id和用户名称生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

//        7.返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.ok(map);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
//        1. 从请求头获取用户信息（获取请求头token字符串）
        String token = request.getHeader("token");

//        2. 从token字符串获取用户id 或者 用户名称
        Long userId = JwtHelper.getUserId(token);

//        3. 根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = sysUserService.getById(userId);

//        4.根据用户id获取用户可以操作菜单列表
//        查询数据库动态构建路由结构,进行显示
        List<RouterVo> routerVoList = sysMenuService.findUserMenuListByUser(userId);

//        5. 根据用户id获取用户可以操作菜单列表
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

//        6. 返回相应的数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");

//        返回用户可以操作的菜单
        map.put("routers", routerVoList);

//        返回用户可以操作的按钮
        map.put("buttons", permsList);

        return Result.ok(map);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
