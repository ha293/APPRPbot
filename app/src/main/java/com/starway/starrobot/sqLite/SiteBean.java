package com.starway.starrobot.sqLite;

import java.io.Serializable;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-09 15:07
 * @version:
 * @purpose:
 * @Description:
 */
public class SiteBean implements Serializable {
    /**
     * 序列化的版本号
     */
    private static final long serialVersionUID = 1L;
    /**
     * 组件id
     */
    private int id;
    /**
     *询问名字
     */
    private String name;
    /**
     * 地点名
     */
    private String place;
    /**
     * 地点解说
     */
    private String describe;
    /**
     * 是否启用 0 未启用 1 启用 2 起点
     */
    private int flag;
    /**
     * 权重
     */
    private int weight;

    public int getId() {
        return id;
    }

    public SiteBean setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SiteBean setName(String name) {
        this.name = name == null ? null : name.trim();
        return this;
    }

    public String getPlace() {
        return place;
    }

    public SiteBean setPlace(String place) {
        this.place = place == null ? null : place.trim();
        return this;
    }

    public String getDescribe() {
        return describe;
    }

    public SiteBean setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
        return this;
    }

    public int getFlag() {
        return flag;
    }

    public SiteBean setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public SiteBean setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return "SiteBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", describe='" + describe + '\'' +
                ", flag=" + flag +
                ", weight=" + weight +
                '}';
    }
}
