package com.hyphenate.easeui.module.base.widget.inputview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;

public class EaseInputLayout extends LinearLayoutCompat {

    protected LinearLayout linear_control;//控制层
    protected FrameLayout frame_panel;//面板层

    public EaseInputLayout(Context context) {
        super(context);
        initView(context);
    }

    public EaseInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EaseInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        setOrientation(VERTICAL);
        setShowDividers(SHOW_DIVIDER_BEGINNING | SHOW_DIVIDER_MIDDLE);
        setDividerDrawable(EaseContextCompatUtil.getDrawable(R.drawable.ease_divider_horizontal_1));

        View view = inflate(context, R.layout.ease_widget_input_layout, this);
        linear_control = view.findViewById(R.id.linear_control);
        frame_panel = view.findViewById(R.id.frame_panel);
    }

    /**
     * 添加控制控件
     *
     * @param view  控件
     * @param index 布局的位置
     */
    public void addControlView(EaseInputControlView view, int index) {
        linear_control.addView(view, index);
    }

    /**
     * 添加控制控件
     *
     * @param view 控件
     */
    public void addControlView(EaseInputControlView view) {
        linear_control.addView(view);
    }

    /**
     * 添加面板控件
     *
     * @param view  控件
     * @param index 布局的位置
     */
    public void addPanelView(View view, int index) {
        frame_panel.addView(view, index);
    }

    /**
     * 添加面板控件
     *
     * @param view 控件
     */
    public void addPanelView(View view) {
        frame_panel.addView(view);
    }

}