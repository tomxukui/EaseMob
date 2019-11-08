package com.hyphenate.easeui.module.chat.provider.impl;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.message.row.EaseCustomChatRowProvider;
import com.hyphenate.easeui.module.chat.provider.EaseChatMessageStyle;

public class EaseChatMessageDefaultStyle implements EaseChatMessageStyle {

    @Override
    public EaseCustomChatRowProvider getCustomChatRowProvider() {
        return null;
    }

    @Override
    public EaseMessageListItemStyle getMessageListItemStyle() {
        return null;
    }

    @Override
    public void onSendMessageWithAttributes(EMMessage message) {
    }

}