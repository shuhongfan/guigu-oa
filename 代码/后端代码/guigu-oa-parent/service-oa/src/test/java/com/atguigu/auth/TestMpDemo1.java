package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.model.system.SysRole;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpDemo1 {

    //注入
    @Autowired
    private SysRoleMapper mapper;

    //查询所有记录
    @Test
    public void getAll() {
        List<SysRole> list = mapper.selectList(null);
        System.out.println(list);
    }

    //添加操作
    @Test
    public void add() {
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员1");
        sysRole.setRoleCode("role1");
        sysRole.setDescription("角色管理员1");

        int rows = mapper.insert(sysRole);
        System.out.println(rows);
        System.out.println(sysRole.getId());
    }

    //修改操作
    @Test
    public void update() {
        //根据id查询
        SysRole role = mapper.selectById(10);
        //设置修改值
        role.setRoleName("atguigu角色管理员");
        //调用方法实现最终修改
        int rows = mapper.updateById(role);
        System.out.println(rows);
    }

    //删除操作
    @Test
    public void deleteId() {
        int rows = mapper.deleteById(10);
    }

    //批量删除
    @Test
    public void testDeleteBatchIds() {
        int result = mapper.deleteBatchIds(Arrays.asList(1, 2));
        System.out.println(result);
    }

    //条件查询
    @Test
    public void testQuery1() {
        //创建QueryWrapper对象，调用方法封装条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","总经理");
        //调用mp方法实现查询操作
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(list);
    }

    @Test
    public void testQuery2() {
        //LambdaQueryWrapper，调用方法封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName,"总经理");
        //调用mp方法实现查询操作
        List<SysRole> list = mapper.selectList(wrapper);
        System.out.println(list);
    }
}
