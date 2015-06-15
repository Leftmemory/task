package com.zxd.task.controller;

import com.zxd.task.model.City;
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

    @RequestMapping(value = "/get_user", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public City getUser(Integer uid){
        City city = cityService.getCityById(1);
        return city;
    }
}
