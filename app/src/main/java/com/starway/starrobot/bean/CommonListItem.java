package com.starway.starrobot.bean;

/**
 * Created by iBelieve on 2018/4/17.
 */

public class CommonListItem {
    private int id;
    private String title;
    private Object object;

    public CommonListItem() {
    }

    public CommonListItem(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public CommonListItem(int id, String title, Object object) {
        this.id = id;
        this.title = title;
        this.object = object;
    }

    public int getId() {
        return id;
    }

    public CommonListItem setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CommonListItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public Object getObject() {
        return object;
    }

    public CommonListItem setObject(Object object) {
        this.object = object;
        return this;
    }
}
