package com.hyphenate.easeui.module.inquiry.provider;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.message.row.EaseCustomChatRowProvider;

public class EaseInquiryMessageProvider {

    /**
     * 点击头像事件
     */
    public void onAvatarClick(String username) {
    }

    /**
     * 长按头像事件
     */
    public void onAvatarLongClick(String username) {
    }

    /**
     * 点击消息事件
     */
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    /**
     * 长按消息事件
     */
    public void onMessageBubbleLongClick(EMMessage message) {
    }

    /**
     * 添加发送消息时附带扩展消息
     */
    public void onSendMessageWithAttributes(EMMessage message) {
    }

    /**
     * 获取自定义聊天消息样式提供者
     */
    public EaseCustomChatRowProvider getCustomChatRowProvider() {
        return null;
    }

    /**
     * 获取聊天消息样式
     */
    public EaseMessageListItemStyle getMessageListItemStyle() {
        return null;
    }

}