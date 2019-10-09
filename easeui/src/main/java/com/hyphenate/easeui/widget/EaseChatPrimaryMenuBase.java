package com.hyphenate.easeui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public abstract class EaseChatPrimaryMenuBase extends LinearLayoutCompat {

    @Nullable
    protected EaseChatPrimaryMenuListener listener;

    public EaseChatPrimaryMenuBase(Context context) {
        super(context);
    }

    public EaseChatPrimaryMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseChatPrimaryMenuBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatPrimaryMenuListener(EaseChatPrimaryMenuListener listener) {
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

    public interface EaseChatPrimaryMenuListener {

        /**
         * when send button clicked
         */
        void onSendBtnClicked(String content);

        /**
         * when typing on the edit-text layout.
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * when speak button is touched
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        /**
         * toggle on/off voice button
         */
        void onToggleVoiceBtnClicked();

        /**
         * toggle on/off extend menu
         */
        void onToggleExtendClicked();

        /**
         * toggle on/off emoji icon
         */
        void onToggleEmojiconClicked();

        /**
         * on text input is clicked
         */
        void onEditTextClicked();

    }

}