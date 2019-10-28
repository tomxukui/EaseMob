package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseSoftInputUtil;

public class EaseInputControlMenu extends EaseInputControlMenuBase {

    private Button btn_voice;
    private TextView tv_sendVoice;
    private FrameLayout frame_text;
    private EditText et_text;
    private TextView tv_text;
    private Button btn_face;
    private Button btn_more;
    private Button btn_send;

    private boolean mCtrlPress = false;

    public EaseInputControlMenu(Context context) {
        super(context);
        initView(context);
        setView();
    }

    public EaseInputControlMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setView();
    }

    public EaseInputControlMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setView();
    }

    private void initView(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, EaseDensityUtil.dp2px(10), 0, EaseDensityUtil.dp2px(10));

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_input_control_menu, this);

        btn_voice = view.findViewById(R.id.btn_voice);
        tv_sendVoice = view.findViewById(R.id.tv_sendVoice);
        frame_text = view.findViewById(R.id.frame_text);
        et_text = view.findViewById(R.id.et_text);
        tv_text = view.findViewById(R.id.tv_text);
        btn_face = view.findViewById(R.id.btn_face);
        btn_more = view.findViewById(R.id.btn_more);
        btn_send = view.findViewById(R.id.btn_send);
    }

    private void setView() {
        btn_voice.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());

            if (v.isSelected()) {
                tv_sendVoice.setVisibility(View.VISIBLE);

                setTextEditView(false, false, false);

                btn_face.setSelected(false);

                setSendBtn(false);

            } else {
                tv_sendVoice.setVisibility(View.GONE);

                setTextEditView(true, true, true);

                btn_face.setSelected(false);

                setSendBtn(et_text.getText().toString());
            }

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

        et_text.setOnClickListener(v -> {
            btn_face.setSelected(false);
            btn_more.setSelected(false);

            if (listener != null) {
                listener.onEditTextClicked();
            }
        });
        et_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSendBtn(!TextUtils.isEmpty(s));

                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_text.setText(s);

                if (TextUtils.isEmpty(tv_text.getText().toString())) {
                    tv_text.setText(EaseContextCompatUtil.getString(R.string.ease_chat_input_hint));
                    tv_text.setTextColor(EaseContextCompatUtil.getColor(R.color.ease_chat_edit_hint));

                } else {
                    tv_text.setTextColor(EaseContextCompatUtil.getColor(R.color.ease_chat_edit_text));
                }
            }

        });
        et_text.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mCtrlPress = true;

                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    mCtrlPress = false;
                }
            }

            return false;
        });
        et_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && mCtrlPress)) {
                listener.onSendBtnClick(et_text.getText().toString());
                et_text.setText("");
                return true;

            } else {
                return false;
            }
        });

        tv_text.setOnClickListener(v -> {
            btn_face.setSelected(false);
            btn_more.setSelected(false);

            setTextEditView(true, true, true);

            if (listener != null) {
                listener.onEditTextClicked();
            }
        });

        btn_face.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());

            btn_voice.setSelected(false);
            tv_sendVoice.setVisibility(View.GONE);

            setTextEditView(true, true, !v.isSelected());

            btn_more.setSelected(false);

            if (listener != null) {
                listener.onToggleEmojiconClick();
            }
        });

        btn_more.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());

            btn_voice.setSelected(false);
            tv_sendVoice.setVisibility(View.GONE);

            setTextEditView(true, !v.isSelected(), !v.isSelected());

            btn_face.setSelected(false);

            if (listener != null) {
                listener.onToggleExtendClick();
            }
        });

        btn_send.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendBtnClick(et_text.getText().toString());
                et_text.setText("");
            }
        });
    }

    /**
     * 设置文字输入控件
     *
     * @param show      是否显示
     * @param fource    是否聚焦
     * @param softInput 是否显示键盘
     */
    private void setTextEditView(boolean show, boolean fource, boolean softInput) {
        if (show) {
            frame_text.setVisibility(View.VISIBLE);

            if (fource) {
                tv_text.setVisibility(View.GONE);

                et_text.setVisibility(View.VISIBLE);
                et_text.requestFocus();

                if (softInput) {
                    EaseSoftInputUtil.show(et_text);

                } else {
                    EaseSoftInputUtil.hide(et_text);
                }

            } else {
                tv_text.setVisibility(View.VISIBLE);

                et_text.setVisibility(View.GONE);

                EaseSoftInputUtil.hide(et_text);
            }

        } else {
            frame_text.setVisibility(View.GONE);

            EaseSoftInputUtil.hide(et_text);
        }
    }

    /**
     * 设置发送按钮
     *
     * @param show 是否显示发送按钮
     */
    private void setSendBtn(boolean show) {
        btn_more.setSelected(false);

        if (show) {
            btn_more.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);

        } else {
            btn_more.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.GONE);
        }
    }

    private void setSendBtn(String content) {
        setSendBtn(!TextUtils.isEmpty(content));
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
        et_text.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(et_text.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            et_text.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onExtendMenuContainerHide() {
        btn_face.setSelected(false);
        btn_more.setSelected(false);
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = et_text.getSelectionStart();
        Editable editable = et_text.getEditableText();
        editable.insert(start, text);

        btn_voice.setSelected(false);
        tv_sendVoice.setVisibility(View.GONE);

        setTextEditView(true, true, true);

        btn_face.setSelected(false);

        setSendBtn(et_text.getText().toString());
    }

    @Override
    public EditText getEditText() {
        return et_text;
    }

}