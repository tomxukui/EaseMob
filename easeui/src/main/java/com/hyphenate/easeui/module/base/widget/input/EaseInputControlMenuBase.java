package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public abstract class EaseInputControlMenuBase extends LinearLayoutCompat {

    @Nullable
    protected OnControlListener mOnControlListener;

    public EaseInputControlMenuBase(Context context) {
        super(context);
    }

    public EaseInputControlMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseInputControlMenuBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置文字输入控件
     *
     * @param show      是否显示
     * @param fource    是否聚焦
     * @param softInput 是否显示键盘
     */
    public abstract void setTextEditView(boolean show, boolean fource, boolean softInput);

    /**
     * 设置键盘的显示和隐藏
     */
    public abstract void showSoftInput(boolean show);

    /**
     * 在输入框中追加一个表情
     */
    public abstract void appendEmojiconInput(CharSequence emojiContent);

    /**
     * 删除输入框的一个表情
     */
    public abstract void deleteEmojiconInput();

    /**
     * 插入文字
     */
    public abstract void insertText(CharSequence text);

    public abstract EditText getEditText();

    public abstract void hideExtendMenuContainer();

    @Nullable
    public OnControlListener getOnControlListener() {
        return mOnControlListener;
    }

    public void setOnControlListener(@Nullable OnControlListener listener) {
        mOnControlListener = listener;
    }

    public interface OnControlListener {

        /**
         * 语音切换事件
         */
        void onToggleVoice(boolean show);

        /**
         * 正在回复事件
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

}