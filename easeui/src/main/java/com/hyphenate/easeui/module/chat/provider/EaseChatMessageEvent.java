package com.hyphenate.easeui.module.chat.provider;

import com.hyphenate.chat.EMMessage;

public interface EaseChatMessageEvent {

    /**
     * 点击头像事件
     */
    void onAvatarClick(String username);

    /**
     * 长按头像事件
     */
    void onAvatarLongClick(String username);

    /**
     * 点击消息事件
     */
    boolean onMessageBubbleClick(EMMessage message);

    /**
     * 长按消息事件
     */
    void onMessageBubbleLongClick(EMMessage message);

}