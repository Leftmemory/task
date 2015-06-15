package com.zxd.task.service.impl;

import com.zxd.task.mapper.CityMapper;
import com.zxd.task.model.City;
import com.zxd.task.service.CityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zxd on 2015/6/15.
 */
@Service
public class CityServiceImpl implements CityService {

    @Resource
    CityMapper cityMapper;

    @Override
    public City getCityById(Integer cityId) {
        return cityMapper.selectByPrimaryKey(cityId);
    }
}
