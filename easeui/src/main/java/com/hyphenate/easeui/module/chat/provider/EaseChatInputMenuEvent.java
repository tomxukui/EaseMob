package com.hyphenate.easeui.module.chat.provider;

public interface EaseChatInputMenuEvent {

    /**
     * 语音切换事件
     */
    void onToggleVoice(boolean show);

    /**
     * 正在文字输入事件
     */
    void onTyping(CharSequence s, int start, int before, int count);

    /**
     * 点击输入框事件
     */
    void onEditTextClicked();

}