package com.zxd.task.controller;

import com.google.common.collect.Maps;
import com.zxd.task.service.impl.LotteryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by zxd on 2015/6/16.
 */
@Controller
@RequestMapping("lottery")
public class LotteryController {

    @Resource
    LotteryService lotteryService;

    @RequestMapping(value = "/normal_lottery", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Map drawNormalLottery() {
        String name = lotteryService.drawLottery();
        Map<String, String> map = Maps.newHashMap();
        map.put("lottery", name);
        return map;
    }
}
