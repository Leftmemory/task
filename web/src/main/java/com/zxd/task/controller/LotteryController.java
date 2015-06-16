package com.zxd.task.controller;

import com.zxd.task.service.impl.LotteryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
    public String drawNormalLottery() {
        String name = lotteryService.drawLottery();
        return name;
    }
}
