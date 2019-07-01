package com.hyphenate.easeui.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyphenate.easeui.EaseUI;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    private static DbHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private DbHelper(Context context) {
        super(context, "easeui_db.db", null, DATABASE_VERSION);
    }

    public static DbHelper getInstance() {
        if (instance == null) {
            instance = new DbHelper(EaseUI.getInstance().getContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        }
    }

    public void closeDb() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            instance = null;
        }
    }

}