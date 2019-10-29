package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class EaseInputPanelLayout extends FrameLayout {

    public EaseInputPanelLayout(@NonNull Context context) {
        super(context);
    }

    public EaseInputPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseInputPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 添加面板
     *
     * @param view 面板控件
     */
    public void addPanel(View view) {
        if (view == null) {
            return;
        }

        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }

        addView(view);

        view.setVisibility(View.GONE);
    }

    /**
     * 是否已打开
     */
    public boolean isOpen() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            if (view.getVisibility() == View.VISIBLE) {
                return true;
            }
        }

        return false;
    }

    /**
     * 关闭
     */
    public void close() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            view.setVisibility(View.GONE);
        }
    }

}