package com.hyphenate.easeui.utils;

import android.support.annotation.Nullable;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.model.EaseUser;

public class EaseMessageUtil {

    private static final String FROM_NICKNAME = "send_nickname";//发送方的昵称
    private static final String FROM_AVATAR = "send_avatar";//发送方的头像
    private static final String FROM_MEMBER_ID = "send_memberid";//发送方的成员id
    private static final String TO_NICKNAME = "to_nickname";//被发送方的昵称
    private static final String TO_AVATAR = "to_avatar";//被发送方的头像
    private static final String TO_MEMBER_ID = "to_memberid";//被发送方的成员id

    /**
     * 获取发送者的头像
     */
    public static String getFromAvatar(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(FROM_AVATAR, defaultValue);
    }

    /**
     * 设置发送者的头像
     */
    public static void setFromAvatar(EMMessage message, String avatar) {
        message.setAttribute(FROM_AVATAR, avatar);
    }

    /**
     * 获取发送者的昵称
     */
    public static String getFromNickname(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(FROM_NICKNAME, defaultValue);
    }

    /**
     * 设置发送者的昵称
     */
    public static void setFromNickname(EMMessage message, String nickname) {
        message.setAttribute(FROM_NICKNAME, nickname);
    }

    /**
     * 获取发送者的成员id
     */
    public static String getFromMemberId(EMMessage message) {
        return message.getStringAttribute(FROM_MEMBER_ID, null);
    }

    /**
     * 设置发送者的成员id
     */
    public static void setFromMemberId(EMMessage message, String memberId) {
        message.setAttribute(FROM_MEMBER_ID, memberId);
    }

    /**
     * 获取被发送者的头像
     */
    public static String getToAvatar(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(TO_AVATAR, defaultValue);
    }

    /**
     * 设置被发送者的头像
     */
    public static void setToAvatar(EMMessage message, String avatar) {
        message.setAttribute(TO_AVATAR, avatar);
    }

    /**
     * 获取被发送者的昵称
     */
    public static String getToNickname(EMMessage message, @Nullable String defaultValue) {
        return message.getStringAttribute(TO_NICKNAME, defaultValue);
    }

    /**
     * 设置被发送者的昵称
     */
    public static void setToNickname(EMMessage message, String nickname) {
        message.setAttribute(TO_NICKNAME, nickname);
    }

    /**
     * 获取被发送者的成员id
     */
    public static String getToMemberId(EMMessage message) {
        return message.getStringAttribute(TO_MEMBER_ID, null);
    }

    /**
     * 设置被发送者的成员id
     */
    public static void setToMemberId(EMMessage message, String memberId) {
        message.setAttribute(TO_MEMBER_ID, memberId);
    }

    /**
     * 设置用户信息
     */
    public static void setUserMessage(EMMessage message, EaseUser fromUser, EaseUser toUser) {
        setFromAvatar(message, fromUser.getAvatar());
        setFromNickname(message, fromUser.getNickname());
        setFromMemberId(message, fromUser.getMemberId());
        setToAvatar(message, toUser.getAvatar());
        setToNickname(message, toUser.getNickname());
        setToMemberId(message, toUser.getMemberId());
    }

    private static EaseUser assertGetFromUser(EMMessage message) {
        EaseUser user = new EaseUser();
        user.setUsername(message.getFrom());
        user.setAvatar(getFromAvatar(message, null));
        user.setNickname(getFromNickname(message, null));
        user.setMemberId(getFromMemberId(message));
        return user;
    }

    private static EaseUser assertGetToUser(EMMessage message) {
        EaseUser user = new EaseUser();
        user.setUsername(message.getTo());
        user.setAvatar(getToAvatar(message, null));
        user.setNickname(getToNickname(message, null));
        user.setMemberId(getToMemberId(message));
        return user;
    }

    /**
     * 根据消息获取发送者
     */
    public static EaseUser getFromUser(EMMessage message) {
        if (message.direct() == EMMessage.Direct.SEND) {//自己发送的消息
            return assertGetFromUser(message);

        } else {//对方发送的消息
            return assertGetToUser(message);
        }
    }

    /**
     * 根据消息获取被发送者
     */
    public static EaseUser getToUser(EMMessage message) {
        if (message.direct() == EMMessage.Direct.SEND) {//自己发送的消息
            return assertGetToUser(message);

        } else {//对方发送的消息
            return assertGetFromUser(message);
        }
    }

}