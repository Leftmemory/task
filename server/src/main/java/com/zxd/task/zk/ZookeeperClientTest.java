package com.zxd.task.zk;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author zxd
 * @since 17/1/22.
 */
public class ZookeeperClientTest {

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1:2181";
//        ZooKeeper zk = new ZooKeeper(host, 500000, new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//                System.out.println(watchedEvent.toString());
//
//            }
//        });
        ZkClient zk = new ZkClient(host, 500000);

        String path="/zk";
        Stat stat = new Stat();
        Long max = zk.readData(path, stat);
        Stat stat1 = zk.writeDataReturnStat(path, max++, stat.getVersion());
        zk.close();



    }
}
