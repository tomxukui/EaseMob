package com.hyphenate.easeui.dao;

import com.hyphenate.easeui.bean.EaseUser;

import java.util.List;
import java.util.Map;

public class UserDao {

    public static final String TABLE_NAME = "contactuser";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nickname";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    /**
     * 保存联系人列表
     */
    public void saveContactList(List<EaseUser> contactList) {
        DbManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取联系人
     */
    public Map<String, EaseUser> getContactList() {
        return DbManager.getInstance().getContactList();
    }

    /**
     * 删除联系人
     */
    public void deleteContact(String username) {
        DbManager.getInstance().deleteContact(username);
    }

    /**
     * 保存联系人
     */
    public void saveContact(EaseUser user) {
        DbManager.getInstance().saveContact(user);
    }

}