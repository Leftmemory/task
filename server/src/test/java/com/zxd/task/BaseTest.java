package com.zxd.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/8/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-server-context.xml")
public class BaseTest {


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {

    }
}
