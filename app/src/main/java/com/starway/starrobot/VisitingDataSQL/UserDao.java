package com.starway.starrobot.VisitingDataSQL;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao   //对数据库操作的一个接口。
public interface UserDao {
    @Insert
    void insertUserDatas(UserData...userData);

    @Delete
    void deleteUserDatas(UserData...userData);

    @Query("DELETE FROM USERDATA")//查询元素然后删除
    void deleteAllUserData();

    @Query("SELECT * FROM USERDATA ORDER BY ID DESC")//查询全部，，，降序展示
    List<UserData> UserDataList();
}
