package com.zxd.task.service.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by zxd on 2015/6/16.
 */
@Service
public class LotteryService {

    @Resource
    LotteryCache lotteryCache;

    public String drawLottery() {
        String result = "";
        String field = "normal_lottery";
        String rateConfig = lotteryCache.getRateConfig(field);
        Gson gson = new GsonBuilder().create();
        Map<String, Integer> confMap = gson.fromJson(rateConfig, new TypeToken<Map<String, Integer>>() {
        }.getType());

        Map<String, Integer> rateMap = Maps.newLinkedHashMap();
        int rateNum = 0;
        //得到每个物品对应的概率
        for (Map.Entry<String, Integer> e : confMap.entrySet()) {
            rateNum = rateNum + e.getValue();
            rateMap.put(e.getKey(), rateNum);
        }

        int random = (int) (Math.random() * rateNum);
        //匹配随机数落在的区间
        for (Map.Entry<String, Integer> e : rateMap.entrySet()) {
            if (random < e.getValue()) {
                result = e.getKey();
                break;
            }
        }

        return result;
    }
}
