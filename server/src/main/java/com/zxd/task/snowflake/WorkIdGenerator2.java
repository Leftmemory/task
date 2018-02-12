package com.zxd.task.snowflake;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/17.
 */
public class WorkIdGenerator2 {

    private String zkServer = "127.0.0.1:2181";

    private int timeout = 500000;

    private CuratorFramework zkClient = null;

    private String ip = null;

    private final String R_NODE = "/snow3";

    private final String MAX_NUM_NODE = "/maxNum";

    private final Integer MAX_WORK_ID = 1 << 10;//机器名最大值

    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.builder().connectString(zkServer).retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000).connectionTimeoutMs(timeout).build();
        zkClient.start();

        try {
            //初始化根节点

            String baseNode = R_NODE + "/test";

            if (zkClient.checkExists().forPath(baseNode + MAX_NUM_NODE) == null) {
                zkClient.create().creatingParentsIfNeeded().forPath(baseNode + MAX_NUM_NODE, serialize(-1L));
            }

        } catch (Exception e) {

        }

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.toString().replace("\\.", "_").replace("/", "_");
            System.out.println("ip :" + ip);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Long getWorkId() {
        if (ip == null) {
            System.out.println("error");
        }
        try {
            String baseNode = R_NODE + "/test";

            //当前ip已经生成过，则直接从zk获取
            String curNode = baseNode + "/" + ip;
            if (zkClient.checkExists().forPath(curNode) != null) {
                return deserialize(zkClient.getData().forPath(curNode));
            }

            Long workId = null;
            //基于zk已达到最大值，生成新的workId
            for (int i = 0; i < 100; i++) {
                try {
                    Stat stat = new Stat();
                    Long max = deserialize(zkClient.getData().storingStatIn(stat).forPath(baseNode + MAX_NUM_NODE));
                    if (max >= MAX_WORK_ID >> 2) {
                        continue;
                    }
                    Stat stat1 = zkClient.setData().withVersion(stat.getVersion()).forPath(baseNode + MAX_NUM_NODE,
                            serialize(++max));
                    if (stat1.getVersion() == stat.getVersion() + 1) {
                        workId = max;
                        break;
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("--");
                }
            }

            //如果zk获取不到，则取随机数
            if (workId == null) {
                workId = randomGenerate();
            }
            //以workId值为节点名，创建临时节点
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(baseNode + "/workId/" +
                    workId);
            //以机器ip位节点名，workId位data，创建zk永久接口
            zkClient.create().forPath(curNode, serialize(workId));

            return workId;
        } catch (Exception e) {
            throw new RuntimeException("初始化workId失败", e);
        }
    }

    private Long randomGenerate() {
        //512 ~ 612
        return new Random().nextInt(MAX_WORK_ID >> 2) + 100L;
    }


    private <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        ho.writeObject(obj);
        ho.close();
        return os.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        return (T) hi.readObject();
    }
}
