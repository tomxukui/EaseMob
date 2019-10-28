package com.hyphenate.easeui.module.base.widget.inputmenu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public abstract class EaseChatInputControlMenuBase extends LinearLayoutCompat {

    @Nullable
    protected OnItemClickListener listener;

    public EaseChatInputControlMenuBase(Context context) {
        super(context);
    }

    public EaseChatInputControlMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseChatInputControlMenuBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * emoji icon input event
     */
    public abstract void onEmojiconInputEvent(CharSequence emojiContent);

    /**
     * emoji icon delete event
     */
    public abstract void onEmojiconDeleteEvent();

    /**
     * hide extend menu
     */
    public abstract void onExtendMenuContainerHide();

    /**
     * insert text
     */
    public abstract void onTextInsert(CharSequence text);

    public abstract EditText getEditText();

    public interface OnItemClickListener {

        /**
         * 点击发送按钮
         */
        void onSendBtnClick(String content);

        /**
         * 正在回复
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * 按压语音按键
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        /**
         * 点击切换语音按钮
         */
        void onToggleVoiceBtnClicked();

        /**
         * 点击切换扩展按钮
         */
        void onToggleExtendClick();

        /**
         * 点击切换表情按钮
         */
        void onToggleEmojiconClick();

        /**
         * 点击输入框
         */
        void onEditTextClicked();

    }

}