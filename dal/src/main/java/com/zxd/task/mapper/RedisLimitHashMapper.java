package com.zxd.task.mapper;

import com.zxd.task.model.RedisLimitHash;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RedisLimitHashMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedisLimitHash record);

    int insertSelective(RedisLimitHash record);

    int batchInsertSelective(@Param("record")RedisLimitHash record,
                             @Param("list")List<RedisLimitHash> list);


    RedisLimitHash selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedisLimitHash record);

    int updateByPrimaryKey(RedisLimitHash record);
}