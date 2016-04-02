package com.zxd.task.service.impl;

import com.zxd.task.mapper.RegionMapper;
import com.zxd.task.model.Region;
import com.zxd.task.service.CityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zxd on 2015/6/15.
 */
@Service
public class CityServiceImpl implements CityService {

    @Resource
    RegionMapper regionMapper;

    @Override
    public Region getCityById(Integer id) {
        return regionMapper.selectByPrimaryKey(id);
    }
}
