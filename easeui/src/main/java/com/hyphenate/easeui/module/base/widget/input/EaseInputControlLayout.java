package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
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
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseSoftInputUtil;

public class EaseInputControlLayout extends LinearLayoutCompat {

    private Button btn_voice;
    private TextView tv_sendVoice;
    private FrameLayout frame_text;
    private EditText et_text;
    private TextView tv_text;
    private Button btn_send;

    private OnInputMenuListener mOnInputMenuListener;

    private boolean mCtrlPress = false;

    public EaseInputControlLayout(Context context) {
        super(context);
        initView(context);
        setView();
    }

    public EaseInputControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setView();
    }

    public EaseInputControlLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setView();
    }

    private void initView(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, EaseDensityUtil.dp2px(10), 0, EaseDensityUtil.dp2px(10));

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_input_control_layout, this);

        btn_voice = view.findViewById(R.id.btn_voice);
        tv_sendVoice = view.findViewById(R.id.tv_sendVoice);
        frame_text = view.findViewById(R.id.frame_text);
        et_text = view.findViewById(R.id.et_text);
        tv_text = view.findViewById(R.id.tv_text);
        btn_send = view.findViewById(R.id.btn_send);
    }

    private void setView() {
        btn_voice.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());

            if (v.isSelected()) {
                //显示发送语音按钮
                tv_sendVoice.setVisibility(View.VISIBLE);

                //关闭键盘
                setTextEditView(false, false, false);

            } else {
                //隐藏发送语音按钮
                tv_sendVoice.setVisibility(View.GONE);

                //显示键盘
                setTextEditView(true, true, true);
            }

            if (mOnInputMenuListener != null) {
                mOnInputMenuListener.onToggleVoice(v.isSelected());
            }
        });

        tv_sendVoice.setOnTouchListener((v, event) -> {
            if (mOnInputMenuListener != null) {
                return mOnInputMenuListener.onPressToSpeakBtnTouch(v, event);
            }

            return false;
        });

        et_text.setOnClickListener(v -> {
            //关闭语音
            btn_voice.setSelected(false);
            tv_sendVoice.setVisibility(View.GONE);

            //开启输入框
            setTextEditView(true, true, true);

            if (mOnInputMenuListener != null) {
                mOnInputMenuListener.onEditTextClicked();
            }
        });
        et_text.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                setSendBtnVisibility(!TextUtils.isEmpty(et_text.getText().toString()));

            } else {
                setSendBtnVisibility(false);
            }

            if (mOnInputMenuListener != null) {
                mOnInputMenuListener.onEditFocusChange(hasFocus);
            }
        });
        et_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSendBtnVisibility(!TextUtils.isEmpty(s.toString()));

                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onTyping(s, start, before, count);
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
                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onSendBtnClick(et_text.getText().toString());
                }

                et_text.setText("");
                return true;

            } else {
                return false;
            }
        });

        tv_text.setOnClickListener(v -> {
            setTextEditView(true, true, true);

            if (mOnInputMenuListener != null) {
                mOnInputMenuListener.onEditTextClicked();
            }
        });

        btn_send.setOnClickListener(v -> {
            String text = et_text.getText().toString();

            if (mOnInputMenuListener != null) {
                mOnInputMenuListener.onSendBtnClick(text);
            }

            et_text.setText("");
        });
    }

    /**
     * 设置文字输入控件
     *
     * @param show      是否显示
     * @param fource    是否聚焦
     * @param softInput 是否显示键盘
     */
    public void setTextEditView(boolean show, boolean fource, boolean softInput) {
        if (show) {
            frame_text.setVisibility(View.VISIBLE);

            if (fource) {
                tv_text.setVisibility(View.GONE);

                et_text.setVisibility(View.VISIBLE);
                et_text.requestFocus();

                showSoftInput(softInput);

            } else {
                tv_text.setVisibility(View.VISIBLE);

                et_text.setVisibility(View.GONE);

                showSoftInput(false);
            }

        } else {
            frame_text.setVisibility(View.GONE);

            showSoftInput(false);
        }
    }

    /**
     * 设置键盘的显示和隐藏
     */
    public void showSoftInput(boolean show) {
        if (show) {
            EaseSoftInputUtil.show(et_text);

        } else {
            EaseSoftInputUtil.hide(et_text);
        }
    }

    /**
     * 在输入框中追加一个表情
     */
    public void appendEmojiconInput(CharSequence emojiContent) {
        et_text.append(emojiContent);
    }

    /**
     * 删除输入框的一个表情
     */
    public void deleteEmojiconInput() {
        if (!TextUtils.isEmpty(et_text.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            et_text.dispatchKeyEvent(event);
        }
    }

    /**
     * 插入文字
     */
    public void insertText(CharSequence text) {
        int start = et_text.getSelectionStart();
        Editable editable = et_text.getEditableText();
        editable.insert(start, text);

        btn_voice.setSelected(false);
        tv_sendVoice.setVisibility(View.GONE);

        setTextEditView(true, true, true);
    }

    /**
     * 判断输入框是否没有文字
     */
    public boolean isTextEmpty() {
        return TextUtils.isEmpty(et_text.getText().toString());
    }

    /**
     * 关闭语音模式
     */
    public void closeVoice() {
        btn_voice.setSelected(false);
        tv_sendVoice.setVisibility(View.GONE);
    }

    /**
     * 判断是否是语音模式
     */
    public boolean isVoiceMode() {
        return tv_sendVoice.getVisibility() == View.VISIBLE;
    }

    /**
     * 设置语音是否显示
     */
    public void setVoiceVisibility(boolean show) {
        btn_voice.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置发送按钮是否显示
     */
    public void setSendBtnVisibility(boolean show) {
        btn_send.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setOnInputMenuListener(@Nullable OnInputMenuListener listener) {
        mOnInputMenuListener = listener;
    }

}