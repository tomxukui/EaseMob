package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制器按钮
 */
public class EaseInputControlButton extends AppCompatButton {

    @Nullable
    private View mPanel;

    private boolean mInputEnable;

    @Nullable
    private List<OnToggleListener> mOnToggleListeners;

    public EaseInputControlButton(Context context) {
        super(context);
        setView();
    }

    public EaseInputControlButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setView();
    }

    public EaseInputControlButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setView();
    }

    private void setView() {
        setOnClickListener(v -> toggle(!isOn()));
    }

    /**
     * 获取关联的面板
     */
    @Nullable
    public View getPanel() {
        return mPanel;
    }

    /**
     * 设置关联的面板
     */
    public void setPanel(@Nullable View view) {
        mPanel = view;
    }

    /**
     * 显示关联的面板
     */
    public void showPanel() {
        if (mPanel != null) {
            mPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏关联的面板
     */
    public void hidePanel() {
        if (mPanel != null) {
            mPanel.setVisibility(View.GONE);
        }
    }

    /**
     * 是否可输入
     */
    public boolean isInputEnable() {
        return mInputEnable;
    }

    /**
     * 当选中的时候是否可以输入
     */
    public void setInputEnable(boolean inputEnable) {
        this.mInputEnable = inputEnable;
    }

    /**
     * 开关切换
     *
     * @param on 开关. true:开, false:关
     */
    public void toggle(boolean on) {
        setSelected(on);

        if (mOnToggleListeners != null) {
            for (OnToggleListener listener : mOnToggleListeners) {
                listener.onToggle(this, on);
            }
        }
    }

    /**
     * 是否开启
     */
    public boolean isOn() {
        return isSelected();
    }

    /**
     * 添加开关切换事件
     */
    public void addOnToggleListener(@Nullable OnToggleListener listener) {
        if (listener == null) {
            return;
        }

        if (mOnToggleListeners == null) {
            mOnToggleListeners = new ArrayList<>();
        }

        mOnToggleListeners.add(listener);
    }

    /**
     * 移除开关切换事件
     */
    public void removeOnToggleListener(OnToggleListener listener) {
        if (listener == null) {
            return;
        }

        if (mOnToggleListeners != null && mOnToggleListeners.contains(listener)) {
            mOnToggleListeners.remove(listener);
        }
    }

    /**
     * 开关切换事件
     */
    public interface OnToggleListener {

        void onToggle(EaseInputControlButton button, boolean on);
    }

}