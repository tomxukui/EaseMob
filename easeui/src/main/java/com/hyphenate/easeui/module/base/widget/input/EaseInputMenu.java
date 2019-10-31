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

    private EaseInputControlLayout layout_control;
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

        layout_control = view.findViewById(R.id.layout_control);
        layout_panel = view.findViewById(R.id.layout_panel);
    }

    private void setView() {
        layout_control.setOnInputMenuListener(new OnInputMenuListener() {

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
            public void onEditFocusChange(boolean hasFocus) {
                if (mOnInputMenuListener != null) {
                    mOnInputMenuListener.onEditFocusChange(hasFocus);
                }
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
                    mOnInputMenuListener.onSendBtnClick(content);
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
        layout_control.addView(button, position);
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

    public EaseInputControlLayout getControl() {
        return layout_control;
    }

    /**
     * 插入文字
     */
    public void insertText(String text) {
        layout_control.insertText(text);
    }

    @Override
    public void onToggle(EaseInputControlButton button, boolean on) {
        //设置控制按钮
        for (int i = 0; i < layout_control.getChildCount(); i++) {
            View view = layout_control.getChildAt(i);

            if (view instanceof EaseInputControlButton && (view != button)) {
                view.setSelected(false);
            }
        }

        if (on) {
            layout_control.closeVoice();
            layout_control.setTextEditView(true, button.isInputEnable(), false);

        } else {
            layout_control.setTextEditView(true, true, true);
        }

        //设置面板
        View panel = button.getPanel();

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

    /**
     * 收缩输入菜单
     * 触发场景:用户点击消息列表, 原本弹起的输入菜单降到最低
     * 1.如果是文字模式, 聚焦文字输入框;如果是语音模式, 则不对文字输入框操作, 因为输入框是隐藏的
     * 2.关闭控制按钮
     * 3.隐藏所有的面板
     * 4.关闭键盘
     */
    public void shrink() {
        if (!layout_control.isVoiceMode()) {
            layout_control.setTextEditView(true, true, false);
        }

        //关闭所有的控制按钮
        for (int i = 0; i < layout_control.getChildCount(); i++) {
            View view = layout_control.getChildAt(i);

            if (view instanceof EaseInputControlButton) {
                view.setSelected(false);
            }
        }

        //隐藏所有的面板
        for (int i = 0; i < layout_panel.getChildCount(); i++) {
            View view = layout_panel.getChildAt(i);
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否可以返回
     * 1.如果面版正在显示, 则先隐藏所有的面板
     * 2.如果面板都隐藏着, 则可以返回
     */
    public boolean onBackPressed() {
        if (layout_panel.isOpen()) {
            shrink();
            return false;

        } else {
            return true;
        }
    }

    public void setOnInputMenuListener(@Nullable OnInputMenuListener listener) {
        mOnInputMenuListener = listener;
    }

}