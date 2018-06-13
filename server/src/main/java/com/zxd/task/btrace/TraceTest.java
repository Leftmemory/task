package com.zxd.task.btrace;

import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Duration;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.Return;

import static com.sun.btrace.BTraceUtils.*;

/**
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/5/31.
 */
@BTrace
public class TraceTest {

    @OnMethod(clazz = "com.netease.kaola.act.web.controller.ajax.ActivityInfoAjaxController",
            method = "listApply",
            location = @Location(Kind.RETURN))
    public static void m0(@Duration long duration) {
        println("\n==== com.netease.kaola.act.web.controller.ajax.ActivityInfoAjaxController#listApply ====");
        println(strcat("duration(ms): ", str(duration / 1000)));

    }


    @OnMethod(
            clazz="com.zxd.task.MainTest",
            method="f1",
            location = @Location(Kind.RETURN)
    )
    public static void onF1(@Duration long duration) {
        println("Hello BTrace");
        println(strcat("duration(ms): ", str(duration / 1000)));
    }


    @OnMethod(
            clazz="com.netease.kaola.act.compose.serviceimpl.activity.ActivityInfoComposeImpl",
            method="queryPageActivityVOsbyQueryParam",
            location = @Location(Kind.RETURN)
    )
    public static void queryPageActivityVOsbyQueryParam(@Duration long duration) {
        println("queryPageActivityVOsbyQueryParam");
        println(strcat("duration(ms): ", str(duration / 1000)));
    }


    @OnMethod(
            clazz="com.netease.kaola.act.web.controller.ajax.ActivityInfoAjaxController",
            method="fillAndFilterConference",
            location = @Location(Kind.RETURN)
    )
    public static void fillAndFilterConference(@Duration long duration) {
        println("queryPageActivityVOsbyQueryParam");
        println(strcat("duration(ms): ", str(duration / 1000)));
    }

}
