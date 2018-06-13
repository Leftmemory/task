package com.zxd.task.timego;

/**
 * 结果模型
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/3/16.
 */
public class ResultModel {

    private String date;

    private String schedule;

    private String firstBu;

    private int firstBuCount;

    private String secondBu;

    public ResultModel(String date, String schedule, String firstBu, int firstBuCount) {
        this.date = date;
        this.schedule = schedule;
        this.firstBu = firstBu;
        this.firstBuCount = firstBuCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getFirstBu() {
        return firstBu;
    }

    public void setFirstBu(String firstBu) {
        this.firstBu = firstBu;
    }

    public String getSecondBu() {
        return secondBu;
    }

    public void setSecondBu(String secondBu) {
        this.secondBu = secondBu;
    }

    public int getFirstBuCount() {
        return firstBuCount;
    }

    public void setFirstBuCount(int firstBuCount) {
        this.firstBuCount = firstBuCount;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "date='" + date + '\'' +
                ", schedule='" + schedule + '\'' +
                ", firstBu='" + firstBu + '\'' +
                ", secondBu='" + secondBu + '\'' +
                '}';
    }
}
