package com.hyphenate.easeui.module.base.widget.input;

import android.view.MotionEvent;
import android.view.View;

public interface OnInputMenuListener {

    /**
     * 语音切换事件
     */
    void onToggleVoice(boolean show);

    /**
     * 正在文字输入事件
     */
    void onTyping(CharSequence s, int start, int before, int count);

    /**
     * 点击发送按钮事件
     */
    void onSendBtnClick(String content);

    /**
     * 点击输入框事件
     */
    void onEditTextClicked();

    /**
     * 按压语音按键事件
     */
    boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

}