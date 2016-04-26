package com.zxd.task;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by zxd on 15/9/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-server-context.xml")
@Transactional
public class TestSender {

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test() {

        String exchange = "zxd.self";//指定交换机
        String routingKey = "test_routing_key";//指定路由

        Map<String, Object> map = Maps.newHashMap();
        map.put("queueName", "zxd.self");
        map.put("exchangeName", exchange);

        rabbitTemplate.convertAndSend(exchange, routingKey, map.toString());
        System.out.println("------------------------");
    }

    @Test
    public void test1() {

        String exchange = "zxdtest";//指定交换机
        String routingKey = "test_share";//指定路由

        Map<String, Object> map = Maps.newHashMap();
//        map.put("queueName", "test_zxd_local_trade");
        map.put("exchangeName", exchange);
        map.put("operateKey", "FDXD");
        map.put("userId", 117076);
        map.put("orderId", 343503);
        map.put("orderSn", "B15112305001003");

        rabbitTemplate.convertAndSend(exchange, routingKey, map.toString());
        System.out.println("------------------------");
    }
}
