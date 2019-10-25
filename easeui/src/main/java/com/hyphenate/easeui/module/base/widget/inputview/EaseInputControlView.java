package com.hyphenate.easeui.module.base.widget.inputview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

public class EaseInputControlView extends AppCompatTextView {

    @Nullable
    private OnSelectListener mOnSelectListener;

    public EaseInputControlView(Context context) {
        super(context);
    }

    public EaseInputControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseInputControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setSelected(boolean selected) {
        boolean same = (isSelected() == selected);
        super.setSelected(selected);
        if (!same) {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelected(EaseInputControlView.this, selected);
            }
        }
    }

    public void setOnSelectListener(@Nullable OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    public interface OnSelectListener {

        void onSelected(View view, boolean selected);

    }

}