package com.hyphenate.easeui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseChatPrimaryMenuBase.EaseChatPrimaryMenuListener;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;

/**
 * 聊天输入菜单
 */
public class EaseChatInputMenu extends LinearLayout {

    private EaseChatPrimaryMenuBase menu_primary;
    private EaseEmojiconMenu menu_emoji;
    private EaseChatExtendMenu menu_extend;
    private FrameLayout frame_extend;
    private LayoutInflater mInflater;

    private Handler handler = new Handler();
    private Context mContext;
    private ChatInputMenuListener listener;

    public EaseChatInputMenu(Context context) {
        this(context, null);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
        initView();
        setView();
    }

    private void initData(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    private void initView() {
        View view = mInflater.inflate(R.layout.ease_widget_chat_input_menu, this);

        menu_primary = view.findViewById(R.id.menu_primary);
        menu_emoji = view.findViewById(R.id.menu_emoji);
        frame_extend = view.findViewById(R.id.frame_extend);
        menu_extend = view.findViewById(R.id.menu_extend);
    }

    private void setView() {
        menu_primary.setChatPrimaryMenuListener(new EaseChatPrimaryMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null)
                    listener.onSendMessage(content);
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClicked() {
                toggleMore();
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (listener != null) {
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }

        });

        List<EaseEmojiconGroupEntity> emojiconGroupList = new ArrayList<>();
        emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1, Arrays.asList(EaseDefaultEmojiconDatas.getData())));
        menu_emoji.init(emojiconGroupList);
        menu_emoji.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if (emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION) {
                    if (emojicon.getEmojiText() != null) {
                        menu_primary.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(mContext, emojicon.getEmojiText()));
                    }

                } else {
                    if (listener != null) {
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                menu_primary.onEmojiconDeleteEvent();
            }

        });
    }

    /**
     * 添加网格菜单
     */
    public void addExtendMenuItem(int icon, String name, OnClickListener listener) {
        menu_extend.addMenuItem(icon, name, listener);
    }

    /**
     * insert text
     *
     * @param text
     */
    public void insertText(String text) {
        menu_primary.onTextInsert(text);
    }

    /**
     * show or hide extend menu
     */
    protected void toggleMore() {
        if (frame_extend.getVisibility() == View.GONE) {
            hideKeyboard();

            handler.postDelayed(() -> {
                frame_extend.setVisibility(View.VISIBLE);
                menu_extend.setVisibility(View.VISIBLE);
                menu_emoji.setVisibility(View.GONE);
            }, 50);

        } else {
            if (menu_emoji.getVisibility() == View.VISIBLE) {
                menu_emoji.setVisibility(View.GONE);
                menu_extend.setVisibility(View.VISIBLE);

            } else {
                frame_extend.setVisibility(View.GONE);
            }
        }
    }

    /**
     * show or hide emojicon
     */
    protected void toggleEmojicon() {
        if (frame_extend.getVisibility() == View.GONE) {
            hideKeyboard();

            handler.postDelayed(() -> {
                frame_extend.setVisibility(View.VISIBLE);
                menu_extend.setVisibility(View.GONE);
                menu_emoji.setVisibility(View.VISIBLE);
            }, 50);

        } else {
            if (menu_emoji.getVisibility() == View.VISIBLE) {
                frame_extend.setVisibility(View.GONE);
                menu_emoji.setVisibility(View.GONE);

            } else {
                menu_extend.setVisibility(View.GONE);
                menu_emoji.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * hide keyboard
     */
    private void hideKeyboard() {
        menu_primary.hideKeyboard();
    }

    /**
     * hide extend menu
     */
    public void hideExtendMenuContainer() {
        menu_extend.setVisibility(View.GONE);
        menu_emoji.setVisibility(View.GONE);
        frame_extend.setVisibility(View.GONE);
        menu_primary.onExtendMenuContainerHide();
    }

    /**
     * when back key pressed
     *
     * @return false--extend menu is on, will hide it first
     * true --extend menu is off
     */
    public boolean onBackPressed() {
        if (frame_extend.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;

        } else {
            return true;
        }
    }

    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    public interface ChatInputMenuListener {

        /**
         * 正在输入文字
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * 文字输入
         */
        void onSendMessage(String content);

        /**
         * 表情输入
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);

        /**
         * 语音输入
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);
    }

}