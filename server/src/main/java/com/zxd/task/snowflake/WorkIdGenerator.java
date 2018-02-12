package com.zxd.task.snowflake;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.util.Random;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/17.
 */
public class WorkIdGenerator {

    private String zkServer = "127.0.0.1:2181";

    private int timeout = 500000;

    private ZkClient zkClient = null;

    private String ip = null;

    private final String R_NODE = "/snow";

    private final String MAX_NUM_NODE = "/maxNum";

    private final Integer MAX_WORK_ID = 1 << 10;//机器名最大值

    public void init() {
        zkClient = new ZkClient(zkServer, timeout);

        //初始化根节点
        if (!zkClient.exists(R_NODE)) {
            zkClient.createPersistent(R_NODE);
        }

        String baseNode = R_NODE + "/test";

        if (!zkClient.exists(baseNode)) {
            zkClient.createPersistent(baseNode);
        }

        if (!zkClient.exists(baseNode + MAX_NUM_NODE)) {
            zkClient.createPersistent(baseNode + MAX_NUM_NODE, -1L);
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

        String baseNode = R_NODE + "/test";

        //当前ip已经生成过，则直接从zk获取
        String curNode = baseNode + "/" + ip;
        if (zkClient.exists(curNode)) {
            return zkClient.readData(curNode);
        }

        Long workId = null;
        //基于zk已达到最大值，生成新的workId
        for (int i = 0; i < 100; i++) {
            try {
                Stat stat = new Stat();
                Long max = zkClient.readData(baseNode + MAX_NUM_NODE, stat);
                if (max >= MAX_WORK_ID >> 2) {
                    continue;
                }
                Stat stat1 = zkClient.writeDataReturnStat(baseNode + MAX_NUM_NODE, ++max, stat.getVersion());
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
        zkClient.createEphemeral(baseNode + "/workId" + workId);
        //以机器ip位节点名，workId位data，创建zk永久接口
        zkClient.createPersistent(curNode, workId);

        return workId;
    }

    private Long randomGenerate() {
        //512 ~ 612
        return new Random().nextInt(MAX_WORK_ID >> 2) + 100L;
    }
}
