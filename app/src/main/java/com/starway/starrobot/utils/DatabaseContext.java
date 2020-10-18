package com.starway.starrobot.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by iBelieve on 2018/5/6.
 */

public class DatabaseContext extends ContextWrapper {

    private String basePath;

    public DatabaseContext(Context base, String path) {
        super(base);
        if (!path.endsWith("/")) {
            path += "/";
        }
        this.basePath = path;
    }

    @Override
    public File getDatabasePath(String name) {
        if (!name.endsWith(".db")) {
            name += ".db";
        }
        File result = new File(basePath + name);
        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return openOrCreateDatabase(name, mode, factory);
    }
}
