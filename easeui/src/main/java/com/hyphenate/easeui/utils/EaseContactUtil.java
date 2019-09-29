package com.hyphenate.easeui.utils;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.dao.UserDao;
import com.hyphenate.easeui.bean.EaseUser;

import java.util.List;
import java.util.Map;

public class EaseContactUtil {

    private UserDao mUserDao;

    private EaseContactUtil() {
    }

    public static EaseContactUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final EaseContactUtil INSTANCE = new EaseContactUtil();
    }

    /**
     * 保存用户数据
     */
    public void saveContact(EMMessage message) {
        if (message == null) {
            return;
        }

        String sendNickname = message.getStringAttribute("send_nickname", null);
        String sendAvatar = message.getStringAttribute("send_avatar", null);
        String toNickname = message.getStringAttribute("to_nickname", null);
        String toAvatar = message.getStringAttribute("to_avatar", null);

        String myUsername = EMClient.getInstance().getCurrentUser();
        boolean isSender = TextUtils.equals(myUsername, message.getFrom());//是否是发送者
        String toUsername = isSender ? message.getTo() : message.getFrom();

        if (isSender) {
            saveContact(myUsername, sendNickname, sendAvatar);
            saveContact(toUsername, toNickname, toAvatar);

        } else {
            saveContact(myUsername, toNickname, toAvatar);
            saveContact(toUsername, sendNickname, sendAvatar);
        }
    }

    /**
     * 保存用户数据
     */
    public void saveContact(String username, String nickname, String avatar) {
        EaseUser user = getUserDao().getContactList().get(username);
        if (user == null) {
            user = new EaseUser(username);
        }
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        getUserDao().saveContact(user);
    }

    /**
     * 保存用户数据
     */
    public void saveContact(EaseUser user) {
        getUserDao().saveContact(user);
    }

    /**
     * 获取联系人列表
     */
    public Map<String, EaseUser> getContactList() {
        return getUserDao().getContactList();
    }

    /**
     * 获取联系人
     */
    public EaseUser getContact(String username) {
        return getContactList().get(username);
    }

    /**
     * 删除联系人
     */
    public void deleteContact(String username) {
        getUserDao().deleteContact(username);
    }

    /**
     * 保存联系人列表
     */
    public void saveContactList(List<EaseUser> contactList) {
        getUserDao().saveContactList(contactList);
    }

    private UserDao getUserDao() {
        if (mUserDao == null) {
            mUserDao = new UserDao();
        }

        return mUserDao;
    }

}