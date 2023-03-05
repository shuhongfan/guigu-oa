package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysMenuService;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-02
 */
@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    //查询所有菜单和角色分配的菜单
    @ApiOperation("查询所有菜单和角色分配的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        List<SysMenu> list = sysMenuService.findMenuByRoleId(roleId);
        return Result.ok(list);
    }

    @ApiOperation("角色分配菜单")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo) {
        sysMenuService.doAssign(assginMenuVo);
        return Result.ok();
    }

    //菜单列表接口
    @ApiOperation("菜单列表")
    @GetMapping("findNodes")
    public Result findNodes() {

        List<SysMenu> list = sysMenuService.findNodes();
        return Result.ok(list);
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu) {
        sysMenuService.save(sysMenu);
        return Result.ok();
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);
        return Result.ok();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysMenuService.removeMenuById(id);
        return Result.ok();
    }

}

