package com.zxd.task;

import com.zxd.task.model.Region;
import com.zxd.task.service.CityService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/8/15.
 */
public class CityServiceTest extends BaseTest{

    @Autowired
    private CityService cityService;

    @Test
    public void getCityByIdTest() {
        Region region = cityService.getCityById(1);
        Assert.assertNotNull(region);
    }
}
