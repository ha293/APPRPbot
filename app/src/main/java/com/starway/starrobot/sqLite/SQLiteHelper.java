package com.starway.starrobot.sqLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.starway.starrobot.service.NetworkService;
import com.starway.starrobot.utils.DatabaseContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-09 15:05
 * @version:
 * @purpose:
 * @Description:
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    String createTable = "CREATE TABLE sc (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name text, place TEXT, describe TEXT, flag integer, weight integer DEFAULT 0)";
    SiteSQL sqlSite = new SiteSQL();

    public static final int QUERY_ALL = 0; //查询所以地点
    public static final int QUERY_AVAILABLE = 1; //仅查询已启用的地点

    /**
     * 获取SQLite工具类
     *
     * @param context
     * @return
     */
    public static SQLiteHelper getSqLiteHelper(Context context) {
        return new SQLiteHelper(new DatabaseContext(context, NetworkService.FTP_HOME_PATH), "site.db", null, 1);
    }

    /**
     * @param context 上下文
     * @param name    数据库名称
     * @param factory 游标工厂
     * @param version 数据库版本
     */
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    // 创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("SqliteHelper", "数据库创建");
        String sql = createTable;
        db.execSQL(sql);
    }

    // 创建数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("SqliteHelper", "数据库更新");
    }


    /**
     * 添加SiteBean到数据库
     *
     * @param siteBean
     */
    public void insertSiteBean(SiteBean siteBean) {
        Log.e("SqliteHelper", "插入");
        SQLiteDatabase database = getReadableDatabase();//以读写的形式打开数据库
        database.execSQL(sqlSite.addSite(siteBean));//插入数据
        database.close();
    }

    /**
     * 更新SiteBean
     *
     * @param siteBean
     */
    public void updateSiteBean(SiteBean siteBean) {
        Log.e("SqliteHelper", "更新");
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL(sqlSite.updateSite(siteBean));//更新数据库
        database.close();
    }

    /**
     * 删除SiteBean
     *
     *
     */
    public void deleteSiteBean(int id) {
        Log.e("SqliteHelper", "删除");
        SQLiteDatabase db = getWritableDatabase();
        String sql = "id = ?";
        String wheres[] = {String.valueOf(id)};
        db.delete("sc", sql, wheres); // 数据库删除
        db.close(); // 关闭数据库
    }

    /**
     * 查询所有的SiteBean
     *
     * @return 所有SiteBean集合
     */
    public List<SiteBean> queryAllSiteBean(int type) {
        List<SiteBean> siteBeans = new ArrayList<SiteBean>();
        SQLiteDatabase database = getReadableDatabase();
        String sql = sqlSite.queryAllSite() + (type == QUERY_AVAILABLE ? "where flag!=-1" : "");
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            String describe = cursor.getString(cursor.getColumnIndex("describe"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            int weight = cursor.getInt(cursor.getColumnIndex("weight"));
            SiteBean siteBean = new SiteBean()
                    .setId(id)
                    .setDescribe(describe)
                    .setFlag(flag)
                    .setName(name)
                    .setPlace(place)
                    .setWeight(weight);
            siteBeans.add(siteBean);
        }
        cursor.close();//关掉游标
        database.close();
        return siteBeans;
    }

    public SiteBean queryStartPointSiteBean() {
        SiteBean siteBean = new SiteBean();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(sqlSite.queryStartSite(), null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            String describe = cursor.getString(cursor.getColumnIndex("describe"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            int weight = cursor.getInt(cursor.getColumnIndex("weight"));
            siteBean = new SiteBean()
                    .setId(id)
                    .setDescribe(describe)
                    .setFlag(flag)
                    .setName(name)
                    .setPlace(place)
                    .setWeight(weight);
        }
        cursor.close();//关掉游标
        database.close();
        return siteBean;
    }

    /**
     * 根据id查询SiteBean
     *
     * @param id
     * @return SiteBean
     */
    public SiteBean querySiteBeanById(int id) {
        SiteBean siteBean = new SiteBean();
        SQLiteDatabase database = getReadableDatabase();
        String[] columns = {"id", "name", "place", "describe", "flag", "weight"};
        String selection = "id=?";
        String[] selectionSite = {String.valueOf(id)};
        Cursor cursor = database.query("sc", columns, selection, selectionSite,
                null, null, null);
        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            String describe = cursor.getString(cursor.getColumnIndex("describe"));
            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
            int weight = cursor.getInt(cursor.getColumnIndex("weight"));
            siteBean.setId(id)
                    .setDescribe(describe)
                    .setFlag(flag)
                    .setName(name)
                    .setPlace(place)
                    .setWeight(weight);
        }
        cursor.close();
        database.close();
        return siteBean;
    }
}
