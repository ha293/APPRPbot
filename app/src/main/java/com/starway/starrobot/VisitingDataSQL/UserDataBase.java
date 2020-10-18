package com.starway.starrobot.VisitingDataSQL;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {UserData.class},version = 1,exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao getUserDao();
}
