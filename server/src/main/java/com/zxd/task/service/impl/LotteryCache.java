package com.zxd.task.service.impl;

import com.zxd.task.cache.JedisManager;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zxd on 2015/6/16.
 * 抽奖缓存
 */
@Service
public class LotteryCache extends JedisManager {
    private static final String LOTTERY_COMMON_CONFIG = "lottery_common_config_%d";

    private static final String LOTTERY_RATE_CONFIG = "lottery_rate_config";

    public String getRateConfig(String filed) {
        return getHash(LOTTERY_RATE_CONFIG, filed, String.class);
    }
}
