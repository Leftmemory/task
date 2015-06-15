import com.google.common.collect.Lists;
import com.zxd.task.cache.JedisClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by zxd on 15/6/15.
 */
@Slf4j
public class MyTest {

    @Test
    public void test() {
        String domain = "127.0.0.1:6379";
        String password = "12qwASZX";
        JedisClient jedisClient = new JedisClient(domain, password);
        String name = jedisClient.getHash("test_key", "name", String.class);
        System.out.println(name);
    }
}
