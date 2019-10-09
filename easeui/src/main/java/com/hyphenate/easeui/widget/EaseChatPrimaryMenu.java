package com.hyphenate.easeui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.R;

public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase {

    private Button btn_setVoiceMode;
    private Button btn_setKeyboardMode;
    private TextView tv_sendVoice;
    private EditText et_sendText;
    private ImageView iv_face;
    private ImageButton ib_more;
    private Button btn_send;

    private boolean mCtrlPress = false;

    public EaseChatPrimaryMenu(Context context) {
        super(context);
        initView(context);
        setView();
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setView();
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setView();
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);

        btn_setVoiceMode = view.findViewById(R.id.btn_setVoiceMode);
        btn_setKeyboardMode = view.findViewById(R.id.btn_setKeyboardMode);
        tv_sendVoice = view.findViewById(R.id.tv_sendVoice);
        et_sendText = view.findViewById(R.id.et_sendText);
        iv_face = view.findViewById(R.id.iv_face);
        ib_more = view.findViewById(R.id.ib_more);
        btn_send = view.findViewById(R.id.btn_send);
    }

    private void setView() {
        btn_setKeyboardMode.setOnClickListener(v -> {
            setModeKeyboard();
            iv_face.setSelected(false);

            if (listener != null) {
                listener.onToggleVoiceBtnClicked();
            }
        });

        btn_setVoiceMode.setOnClickListener(v -> {
            setModeVoice();
            iv_face.setSelected(false);

            if (listener != null) {
                listener.onToggleVoiceBtnClicked();
            }
        });

        tv_sendVoice.setOnTouchListener((v, event) -> {
            if (listener != null) {
                return listener.onPressToSpeakBtnTouch(v, event);
            }
            return false;
        });

        et_sendText.setOnClickListener(v -> {
            iv_face.setSelected(false);

            if (listener != null) {
                listener.onEditTextClicked();
            }
        });
        et_sendText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    ib_more.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);

                } else {
                    ib_more.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }

                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
        et_sendText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mCtrlPress = true;

                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    mCtrlPress = false;
                }
            }

            return false;
        });
        et_sendText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && mCtrlPress)) {
                listener.onSendBtnClicked(et_sendText.getText().toString());
                et_sendText.setText("");
                return true;

            } else {
                return false;
            }
        });

        iv_face.setOnClickListener(v -> {
            iv_face.setSelected(!iv_face.isSelected());

            if (listener != null) {
                listener.onToggleEmojiconClicked();
            }
        });

        ib_more.setOnClickListener(v -> {
            btn_setVoiceMode.setVisibility(View.VISIBLE);
            btn_setKeyboardMode.setVisibility(View.GONE);
            tv_sendVoice.setVisibility(View.GONE);
            et_sendText.setVisibility(View.VISIBLE);
            iv_face.setVisibility(View.VISIBLE);
            iv_face.setSelected(false);

            if (listener != null) {
                listener.onToggleExtendClicked();
            }
        });

        btn_send.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendBtnClicked(et_sendText.getText().toString());
                et_sendText.setText("");
            }
        });
    }

    /**
     * set recorder view when speak icon is touched
     *
     * @param voiceRecorderView
     */
    public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView) {
    }

    /**
     * append emoji icon to editText
     *
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        et_sendText.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(et_sendText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            et_sendText.dispatchKeyEvent(event);
        }
    }

    /**
     * 显示语音模式
     */
    protected void setModeVoice() {
        hideKeyboard();

        btn_setVoiceMode.setVisibility(View.GONE);
        btn_setKeyboardMode.setVisibility(View.VISIBLE);
        tv_sendVoice.setVisibility(View.VISIBLE);
        et_sendText.setVisibility(View.GONE);
        iv_face.setVisibility(View.GONE);
        iv_face.setSelected(false);
        btn_send.setVisibility(View.GONE);
        ib_more.setVisibility(View.VISIBLE);
    }

    /**
     * 显示键盘模式
     */
    protected void setModeKeyboard() {
        btn_setKeyboardMode.setVisibility(View.GONE);
        btn_setVoiceMode.setVisibility(View.VISIBLE);
        tv_sendVoice.setVisibility(View.GONE);
        et_sendText.setVisibility(View.VISIBLE);
        et_sendText.requestFocus();
        iv_face.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(et_sendText.getText())) {
            ib_more.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);

        } else {
            ib_more.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onExtendMenuContainerHide() {
        iv_face.setSelected(false);
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = et_sendText.getSelectionStart();
        Editable editable = et_sendText.getEditableText();
        editable.insert(start, text);

        setModeKeyboard();
    }

    @Override
    public EditText getEditText() {
        return et_sendText;
    }

}