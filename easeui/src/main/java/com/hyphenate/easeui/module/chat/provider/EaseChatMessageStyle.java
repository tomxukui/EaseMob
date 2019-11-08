package com.hyphenate.easeui.module.chat.provider;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.message.row.EaseCustomChatRowProvider;

public interface EaseChatMessageStyle {

    /**
     * 获取自定义聊天消息样式提供者
     */
    EaseCustomChatRowProvider getCustomChatRowProvider();

    /**
     * 获取聊天消息样式
     */
    EaseMessageListItemStyle getMessageListItemStyle();

    /**
     * 添加发送消息时附带扩展消息
     */
    void onSendMessageWithAttributes(EMMessage message);

}