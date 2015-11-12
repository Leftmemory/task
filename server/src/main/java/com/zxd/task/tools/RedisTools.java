package com.zxd.task.tools;

import com.google.common.collect.Lists;
import com.zxd.task.cache.JedisClient;
import com.zxd.task.mapper.RedisLimitHashMapper;
import com.zxd.task.model.RedisLimitHash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zxd on 15/11/12.
 */
@Slf4j
@Component
public class RedisTools {
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private RedisLimitHashMapper redisLimitHashMapper;

    public void exportHash2Excel(String key, String fileName) {
        Map<String, String> map = jedisClient.getAllHash(key);
        ExcelTools.exportExcel(fileName + ".xls", map);
    }

    public void exportLimitHash2Table(String key, Integer actId) {
        Map<String, String> map = jedisClient.getAllHash(key);
        int i = 0;
        List<RedisLimitHash> limitHashList = Lists.newArrayList();

        for (Map.Entry<String, String> e : map.entrySet()) {
            RedisLimitHash limitHash = new RedisLimitHash();
            String field = e.getKey();
            String value = e.getValue();
            String[] strs = field.split("_");
            limitHash.setUserId(Integer.parseInt(strs[0]));
            limitHash.setGoodsId(Integer.parseInt(strs[1]));
            limitHash.setTime(strs[2]);
            limitHash.setType(actId);
            limitHash.setNumber(Integer.parseInt(value));
            limitHashList.add(limitHash);
            i++;
            if (i == 100) {
                i = 0;
                redisLimitHashMapper.batchInsertSelective(limitHashList.get(0), limitHashList);
                limitHashList = Lists.newArrayList();
            }
        }
        if(!CollectionUtils.isEmpty(limitHashList)){
            redisLimitHashMapper.batchInsertSelective(limitHashList.get(0), limitHashList);
        }
    }
}
