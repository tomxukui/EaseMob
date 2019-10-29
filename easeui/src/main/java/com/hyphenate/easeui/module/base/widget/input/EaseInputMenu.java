package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;

/**
 * 聊天输入菜单
 */
public class EaseInputMenu extends LinearLayoutCompat implements EaseInputControlButton.OnToggleListener {

    private EaseInputControlMenu menu_control;
    private EaseInputPanelLayout layout_panel;

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
        layout_panel = view.findViewById(R.id.layout_panel);
    }

    private void setView() {
        menu_control.setOnControlListener(new EaseInputControlMenuBase.OnControlListener() {

            @Override
            public void onToggleVoice(boolean show) {
                layout_panel.close();

                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onToggleVoice(show);
                }
            }

            @Override
            public void onEditTextClicked() {
                layout_panel.close();

                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onEditTextClicked();
                }
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (mOnInputMenuListener != null) {
                    return mOnInputMenuListener.onPressToSpeakBtnTouch(v, event);
                }

                return false;
            }

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

        });
    }

    /**
     * 添加控制按钮和面板
     *
     * @param button      控制按钮
     * @param targetPanel 面板
     * @param position    控制按钮所在的位置
     */
    public void addView(EaseInputControlButton button, @Nullable View targetPanel, int position, LinearLayout.LayoutParams params) {
        //添加控制按钮
        menu_control.addView(button, position);
        button.addOnToggleListener(this);

        //添加面板
        layout_panel.addPanel(targetPanel);

        //必须重设边距, 不然不起效果
        if (params != null) {
            LayoutParams layoutParams = (LayoutParams) button.getLayoutParams();
            layoutParams.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
            button.setLayoutParams(layoutParams);
            button.requestLayout();
        }
    }

    public EaseInputControlMenu getControl() {
        return menu_control;
    }

    /**
     * 插入文字
     */
    public void insertText(String text) {
        menu_control.insertText(text);
    }

    /**
     * 隐藏菜单
     */
    public void hideExtendMenuContainer() {
        layout_panel.close();

        menu_control.hideExtendMenuContainer();
    }

    public boolean onBackPressed() {
        if (layout_panel.isOpen()) {
            hideExtendMenuContainer();
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onToggle(EaseInputControlButton button, boolean on) {
        //设置控制按钮
        for (int i = 0; i < menu_control.getChildCount(); i++) {
            View view = menu_control.getChildAt(i);

            if (view instanceof EaseInputControlButton && (view != button)) {
                view.setSelected(false);
            }
        }

        if (on) {
            menu_control.setTextEditView(true, button.isInputEnable(), false);

        } else {
            menu_control.setTextEditView(true, true, true);
        }

        //设置面板
        View panel = button.getTargetPanel();

        if (panel != null) {
            for (int i = 0; i < layout_panel.getChildCount(); i++) {
                View view = layout_panel.getChildAt(i);

                if (on && view == panel) {
                    view.setVisibility(View.VISIBLE);

                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setOnInputMenuListener(@Nullable OnInputMenuListener listener) {
        mOnInputMenuListener = listener;
    }

    public interface OnInputMenuListener {

        /**
         * 正在输入
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * 发送文字
         */
        void onSendMessage(String content);

        /**
         * 按压语音按键事件
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        void onEditTextClicked();

        void onToggleVoice(boolean show);

    }

}