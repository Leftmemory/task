package com.zxd.task;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 17/8/11.
 */
public class MainTest {

//    public static void main(String[] args) {
//        SimpleDateFormat sdf = new SimpleDateFormat("YYYY/M/DD");
//
//        System.out.println(sdf.format(new Date()));

//        try {
//            //f4:5c:89:93:97:55
//            InetAddress ip = InetAddress.getLocalHost();
//            System.out.print(ip.toString());
//            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
//            long id;
//            if (network == null) {
//                id = 1;
//            } else {
//                byte[] mac = network.getHardwareAddress();
//                id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
//            }
//            byte[] mac = network.getHardwareAddress();
//            id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
//            System.out.println(id);
//        } catch (Exception e) {
//            System.out.println("error");
//        }
//    }

    public static void main(String[] args) {
        MainTest rc = new MainTest();
        while (true) {
            rc.f1(UUID.randomUUID().toString(), new Random().nextInt());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }
    }


    public String f1(String a, int b) {
        System.out.println(a + " " + b);
        return a;
    }
}
