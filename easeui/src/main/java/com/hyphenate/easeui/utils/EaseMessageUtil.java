package com.hyphenate.easeui.utils;

import android.support.annotation.Nullable;

import com.hyphenate.chat.EMMessage;

public class EaseMessageUtil {

    public static final String FROM_NICKNAME = "send_nickname";//发送方的昵称
    public static final String FROM_AVATAR = "send_avatar";//发送方的头像
    public static final String TO_NICKNAME = "to_nickname";//被发送方的昵称
    public static final String TO_AVATAR = "to_avatar";//被发送方的头像

    /**
     * 获取发送者的头像
     */
    public static String getFromAvatar(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(FROM_AVATAR, defaultValue);
    }

    /**
     * 获取发送者的昵称
     */
    public static String getFromNickname(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(FROM_NICKNAME, defaultValue);
    }

    /**
     * 获取被发送者的头像
     */
    public static String getToAvatar(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(TO_AVATAR, defaultValue);
    }

    /**
     * 获取被发送者的昵称
     */
    public static String getToNickname(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(TO_NICKNAME, defaultValue);
    }

}