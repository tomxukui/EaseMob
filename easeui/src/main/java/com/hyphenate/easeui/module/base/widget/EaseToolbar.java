package com.hyphenate.easeui.module.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.util.DensityUtil;

public class EaseToolbar extends Toolbar {

    private TextView tv_title;
    private TextView tv_left;

    private String mTitle;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private String mLeftText;
    private int mLeftTextColor;
    private int mLeftTextSize;

    public EaseToolbar(Context context) {
        this(context, null);
    }

    public EaseToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView(context);
        setView();
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mTitle = "";
        mTitleTextColor = Color.parseColor("#ffffff");
        mTitleTextSize = DensityUtil.sp2px(context, 18);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseToolbar, defStyleAttr, 0);

            mTitle = ta.getString(R.styleable.EaseToolbar_android_text);
            mTitleTextColor = ta.getColor(R.styleable.EaseToolbar_android_textColor, mTitleTextColor);
            mTitleTextSize = ta.getDimensionPixelSize(R.styleable.EaseToolbar_android_textSize, mTitleTextSize);
            mLeftText = ta.getString(R.styleable.EaseToolbar_toolbarLeftText);
            mLeftTextColor = ta.getColor(R.styleable.EaseToolbar_toolbarLeftTextColor, mTitleTextColor);
            mLeftTextSize = ta.getDimensionPixelSize(R.styleable.EaseToolbar_toolbarLeftTextSize, mTitleTextSize);

            ta.recycle();
        }
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.ease_toolbar, this);

        tv_title = view.findViewById(R.id.tv_title);
        tv_left = view.findViewById(R.id.tv_left);
    }

    private void setView() {
        tv_title.setText(mTitle == null ? "" : mTitle);
        tv_title.setTextColor(mTitleTextColor);
        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);

        tv_left.setText(mLeftText == null ? "" : mLeftText);
        tv_left.setTextColor(mLeftTextColor);
        tv_left.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
    }

    public void setTitle(String title) {
        this.mTitle = title;

        setView();
    }

    public void setLeftText(String text) {
        this.mLeftText = text;

        setView();
    }

    public TextView getTitleView() {
        return this.tv_title;
    }

    public void setOnLeftClickListener(OnClickListener listener) {
        tv_left.setOnClickListener(listener);
    }

    public void setOnTitleClickListener(OnClickListener listener) {
        tv_title.setOnClickListener(listener);
    }

}