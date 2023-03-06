package com.shf.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shf.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据userId查询可以操作菜单列表
     *
     * @param userId
     * @return
     */
    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}