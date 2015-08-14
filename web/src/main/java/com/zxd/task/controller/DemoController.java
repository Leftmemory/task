package com.zxd.task.controller;

import com.zxd.task.model.Region;
import com.zxd.task.service.CityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by zxd on 2015/6/14.
 */
@Controller
@RequestMapping("demo")
public class DemoController {

    @Resource
    CityService cityService;

    @RequestMapping(value = "/get_user", method = RequestMethod.GET)
    @ResponseBody
    public Region getUser(Integer uid) {
        Region region = cityService.getCityById(1);
        System.out.println(region.toString());
        return region;
    }
}
