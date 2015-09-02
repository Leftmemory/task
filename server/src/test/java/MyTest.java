import com.zxd.task.cache.JedisClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by zxd on 15/6/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-server-context.xml")
public class MyTest {

    @Autowired
    private JedisClient jedisClient;
    private static int flag = 0;

    @Test
    public void test() {
        Object obj = jedisClient.getAllKeys("*");
        Object obj1 = jedisClient.getAllHkeys("lottery_rate_config");
        System.out.println(obj.toString());
    }


    @Test
    public void testRedis() {
        while (flag < 1000) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    flag++;
                    while (true) {
                        jedisClient.setHash("test_zxd","test"+flag,flag,0);
                        List<String> list = jedisClient.getListRange("mylist");
                        System.out.println(list.toString());
                    }
                }
            }).start();
        }
        System.out.println(1);
    }
}
