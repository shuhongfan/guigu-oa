package com.shf.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.auth.mapper.ProcessTypeMapper;
import com.shf.auth.service.ProcessTypeService;
import com.shf.model.process.ProcessType;
import org.springframework.stereotype.Service;

@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {
}
