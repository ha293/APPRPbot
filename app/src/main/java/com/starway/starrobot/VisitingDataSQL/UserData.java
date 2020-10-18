package com.starway.starrobot.VisitingDataSQL;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class UserData {
    private int Id;
    @ColumnInfo(name="用户名称")
    private String UserName;
    @ColumnInfo(name="用户学号")
    private String  UserId;

    public UserData(String userName, String userId) {
        UserName = userName;
        UserId = userId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
