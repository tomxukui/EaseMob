package com.hyphenate.easeui.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hyphenate.easeui.bean.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DbManager {

    private static DbManager mDbManager = new DbManager();

    private DbHelper mDbHelper;

    private DbManager() {
        mDbHelper = DbHelper.getInstance();
    }

    public static synchronized DbManager getInstance() {
        if (mDbManager == null) {
            mDbManager = new DbManager();
        }
        return mDbManager;
    }

    /**
     * 保存联系人列表
     */
    synchronized public void saveContactList(List<EaseUser> contactList) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (EaseUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNickname() != null) {
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNickname());
                }
                if (user.getAvatar() != null) {
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                }
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取联系人
     */
    synchronized public Map<String, EaseUser> getContactList() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Map<String, EaseUser> users = new Hashtable<String, EaseUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                EaseUser user = new EaseUser(username);
                user.setNickname(nick);
                user.setAvatar(avatar);
                EaseCommonUtils.setUserInitialLetter(user);
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 删除联系人
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * 保存联系人
     */
    synchronized public void saveContact(EaseUser user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNickname() != null) {
            values.put(UserDao.COLUMN_NAME_NICK, user.getNickname());
        }
        if (user.getAvatar() != null) {
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    synchronized public void closeDb() {
        if (mDbHelper != null) {
            mDbHelper.closeDb();
        }
        mDbManager = null;
    }

}