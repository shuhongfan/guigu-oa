package com.atguigu.process.controller;


import com.atguigu.common.result.Result;
import com.atguigu.model.process.Process;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-14
 */
@RestController
@RequestMapping(value = "/admin/process")
public class OaProcessController {

    @Autowired
    private OaProcessService processService;

    //审批管理列表
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page,limit);
        IPage<ProcessVo> pageModel =
                processService.selectPage(pageParam,processQueryVo);
        return Result.ok(pageModel);
    }
}

