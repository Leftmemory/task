package com.zxd.task.timego;

/**
 * bu模型
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/3/16.
 */
public class BuModel {

    /**
     * 名称
     */
    private String name;
    /**
     * 权重
     */
    private int weight;

    /**
     * 最低分配数
     */
    private int min;

    /**
     * 分配数
     */
    private int number;

    public BuModel() {

    }

    public BuModel(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public BuModel(BuModel buModel) {
        this.name = buModel.getName();
        this.weight = buModel.getWeight();
        this.min = buModel.getMin();
        this.number = buModel.getNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
