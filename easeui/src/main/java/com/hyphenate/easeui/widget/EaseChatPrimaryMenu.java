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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.R;

public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase {

    private EditText editText;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private ImageView faceNormal;
    private ImageView faceChecked;
    private RelativeLayout faceLayout;
    private Button buttonMore;

    private boolean ctrlPress = false;

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
        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);

        editText = view.findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = view.findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = view.findViewById(R.id.edittext_layout);
        buttonSetModeVoice = view.findViewById(R.id.btn_set_mode_voice);
        buttonSend = view.findViewById(R.id.btn_send);
        buttonPressToSpeak = view.findViewById(R.id.btn_press_to_speak);
        faceNormal = view.findViewById(R.id.iv_face_normal);
        faceChecked = view.findViewById(R.id.iv_face_checked);
        faceLayout = view.findViewById(R.id.rl_face);
        buttonMore = view.findViewById(R.id.btn_more);
    }

    private void setView() {
        edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);

        buttonSend.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendBtnClicked(editText.getText().toString());
                editText.setText("");
            }
        });

        buttonSetModeKeyboard.setOnClickListener(v -> {
            setModeKeyboard();
            showNormalFaceImage();

            if (listener != null) {
                listener.onToggleVoiceBtnClicked();
            }
        });

        buttonSetModeVoice.setOnClickListener(v -> {
            setModeVoice();
            showNormalFaceImage();

            if (listener != null) {
                listener.onToggleVoiceBtnClicked();
            }
        });

        buttonMore.setOnClickListener(v -> {
            buttonSetModeVoice.setVisibility(View.VISIBLE);
            buttonSetModeKeyboard.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.GONE);

            showNormalFaceImage();

            if (listener != null) {
                listener.onToggleExtendClicked();
            }
        });

        faceLayout.setOnClickListener(v -> {
            toggleFaceImage();

            if (listener != null) {
                listener.onToggleEmojiconClicked();
            }
        });

        editText.requestFocus();
        editText.setOnClickListener(v -> {
            edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
            faceNormal.setVisibility(View.VISIBLE);
            faceChecked.setVisibility(View.INVISIBLE);

            if (listener != null) {
                listener.onEditTextClicked();
            }
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);

            } else {
                edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    buttonMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);

                } else {
                    buttonMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
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
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    ctrlPress = true;

                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    ctrlPress = false;
                }
            }

            return false;
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && ctrlPress == true)) {
                listener.onSendBtnClicked(editText.getText().toString());
                editText.setText("");
                return true;

            } else {
                return false;
            }
        });

        buttonPressToSpeak.setOnTouchListener((v, event) -> {
            if (listener != null) {
                return listener.onPressToSpeakBtnTouch(v, event);
            }
            return false;
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
        editText.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }

    /**
     * show voice icon when speak bar is touched
     */
    protected void setModeVoice() {
        hideKeyboard();

        edittext_layout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        buttonMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }

    /**
     * show keyboard
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);

        if (TextUtils.isEmpty(editText.getText())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }
    }

    protected void toggleFaceImage() {
        if (faceNormal.getVisibility() == View.VISIBLE) {
            showSelectedFaceImage();
        } else {
            showNormalFaceImage();
        }
    }

    private void showNormalFaceImage() {
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }

    private void showSelectedFaceImage() {
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }

    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }

    @Override
    public void onTextInsert(CharSequence text) {
        int start = editText.getSelectionStart();
        Editable editable = editText.getEditableText();
        editable.insert(start, text);
        setModeKeyboard();
    }

    @Override
    public EditText getEditText() {
        return editText;
    }

}