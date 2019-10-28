package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bean.EaseEmojicon;
import com.hyphenate.easeui.bean.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.module.base.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.module.base.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseSmileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聊天输入菜单
 */
public class EaseInputMenu extends LinearLayoutCompat {

    private EaseInputControlMenuBase menu_control;
    private EaseInputMoreMenu menu_more;
    private EaseEmojiconMenu menu_emoji;

    private OnInputMenuListener mOnInputMenuListener;

    public EaseInputMenu(Context context) {
        super(context);
        initView(context);
        setView();
    }

    public EaseInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setView();
    }

    public EaseInputMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setView();
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        setDividerDrawable(EaseContextCompatUtil.getDrawable(R.drawable.ease_divider_horizontal_1));

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_input_menu, this);

        menu_control = view.findViewById(R.id.menu_control);
        menu_more = view.findViewById(R.id.menu_more);
        menu_emoji = view.findViewById(R.id.menu_emoji);
    }

    private void setView() {
        menu_control.setOnItemClickListener(new EaseInputControlMenuBase.OnItemClickListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void onSendBtnClick(String content) {
                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onSendMessage(content);
                }
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClick() {
                toggleMore();
            }

            @Override
            public void onToggleEmojiconClick() {
                toggleEmojicon();
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (mOnInputMenuListener != null) {
                    return mOnInputMenuListener.onPressToSpeakBtnTouch(v, event);
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
                        menu_control.onEmojiconInputEvent(EaseSmileUtil.getSmiledText(getContext(), emojicon.getEmojiText()));
                    }

                } else {
                    if (mOnInputMenuListener != null) {
                        mOnInputMenuListener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                menu_control.onEmojiconDeleteEvent();
            }

        });
    }

    /**
     * 添加更多菜单选项
     */
    public void addMoreMenuItem(EaseMenuItem menuItem) {
        menu_more.addMenuItem(menuItem);
    }

    /**
     * 添加更多菜单选项列表
     */
    public void addMoreMenuItems(List<EaseMenuItem> menuItems) {
        menu_more.addMenuItems(menuItems);
    }

    /**
     * 插入文字
     */
    public void insertText(String text) {
        menu_control.onTextInsert(text);
    }

    /**
     * 显示或隐藏更多按钮
     */
    protected void toggleMore() {
        boolean show = menu_more.getVisibility() == VISIBLE;

        animShowView(menu_more, !show);
        animShowView(menu_emoji, false);
    }

    /**
     * 显示或隐藏表情
     */
    protected void toggleEmojicon() {
        boolean show = menu_emoji.getVisibility() == VISIBLE;

        animShowView(menu_emoji, !show);
        animShowView(menu_more, false);
    }

    /**
     * 隐藏菜单
     */
    public void hideExtendMenuContainer() {
        animShowView(menu_more, false);
        animShowView(menu_emoji, false);

        menu_control.onExtendMenuContainerHide();
    }

    private void animShowView(View view, boolean show) {
        if (show && view.getVisibility() != View.VISIBLE) {
            ViewCompat.animate(view)
                    .alpha(1f)
                    .setDuration(50)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(View view) {
                            super.onAnimationStart(view);
                            view.setVisibility(View.VISIBLE);
                            view.setAlpha(0f);
                        }
                    })
                    .start();

        } else if (!show && view.getVisibility() == View.VISIBLE) {
            view.setAlpha(0f);
            view.setVisibility(View.GONE);
        }
    }

    /**
     * when back key pressed
     *
     * @return false--extend menu is on, will hide it first
     * true --extend menu is off
     */
    public boolean onBackPressed() {
        if (menu_more.getVisibility() == View.VISIBLE || menu_emoji.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;

        } else {
            return true;
        }
    }

    public void setOnInputMenuListener(OnInputMenuListener listener) {
        mOnInputMenuListener = listener;
    }

    public interface OnInputMenuListener {

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