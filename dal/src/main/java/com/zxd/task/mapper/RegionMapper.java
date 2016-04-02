package com.zxd.task.mapper;

import com.zxd.task.model.Region;

public interface RegionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Region record);

    int insertSelective(Region record);

    Region selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Region record);

    int updateByPrimaryKeyWithBLOBs(Region record);

    int updateByPrimaryKey(Region record);
}