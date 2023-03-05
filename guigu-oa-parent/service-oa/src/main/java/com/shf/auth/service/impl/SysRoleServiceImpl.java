package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.SysRoleMapper;
import com.shf.auth.service.SysRoleService;
import com.shf.auth.service.SysUserRoleService;
import com.shf.model.system.SysRole;
import com.shf.model.system.SysUserRole;
import com.shf.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 根据用户获取角色数据
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
//        1. 查询所有角色，返回list集合
        List<SysRole> allRoleList = baseMapper.selectList(null);

//        2. 根据userid查询角色用户关系表，查询userid对应所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);

//        从查询出来的用户id对应角色list集合，获取所有角色id
        List<Long> existRoleIdList = existUserRoleList.stream()
                .map(SysUserRole::getRoleId).collect(Collectors.toList());

//        3. 根据查询所有角色id，找到对应角色信息
//        根据角色id到所有的角色的list集合进行比较
        List<SysRole> assignRoleList = allRoleList.stream()
                .filter(sysRole -> existRoleIdList.contains(sysRole.getId())).collect(Collectors.toList());

//        4. 把得到两部分数据封装map集合，返回
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignRoleList);
        roleMap.put("allRolesList", allRoleList);
        return roleMap;
    }

    /**
     * 分配角色
     * @param assginRoleVo
     */
    @Override
    @Transactional
    public void doAssign(AssginRoleVo assginRoleVo) {
//        1. 把用户之前分配角色数据删除，用户角色关系表里面，根据userid删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, assginRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

//        2. 重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        roleIdList.forEach(roleId->{
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        });

    }
}
