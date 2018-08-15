package com.zxd.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
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

    @Resource(name = "dataSource")
    BasicDataSource dataSource;

    @Before
    public void setUp() throws Exception {
        Connection conn = dataSource.getConnection();

        // 创建ScriptRunner，用于执行SQL脚本
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setErrorLogWriter(null);
        runner.setLogWriter(null);
        // 执行SQL脚本，初始化内存数据库
        runner.runScript(Resources.getResourceAsReader("h2/sql/schema.sql"));//初始化表
        runner.runScript(Resources.getResourceAsReader("h2/data/import-data.sql"));//初始化数据
        // 关闭连接
        conn.close();
    }

    @Test
    public void test() {

    }
}
