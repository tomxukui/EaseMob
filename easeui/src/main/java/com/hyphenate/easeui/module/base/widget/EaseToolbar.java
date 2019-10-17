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
import com.hyphenate.easeui.utils.DensityUtil;

public class EaseToolbar extends Toolbar {

    private TextView tv_title;
    private TextView tv_subtitle;

    private String mTitle;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private String mSubtitle;
    private int mSubtitleTextColor;
    private int mSubtitleTextSize;

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
        mTitle = null;
        mTitleTextColor = Color.parseColor("#ffffff");
        mTitleTextSize = DensityUtil.sp2px(18);
        mSubtitle = null;
        mSubtitleTextColor = Color.parseColor("#ffffff");
        mSubtitleTextSize = DensityUtil.sp2px(10);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseToolbar, defStyleAttr, 0);

            mTitle = ta.getString(R.styleable.EaseToolbar_toolbar_title);
            mTitleTextColor = ta.getColor(R.styleable.EaseToolbar_toolbar_title_textColor, mTitleTextColor);
            mTitleTextSize = ta.getDimensionPixelSize(R.styleable.EaseToolbar_toolbar_title_textSize, mTitleTextSize);

            mSubtitle = ta.getString(R.styleable.EaseToolbar_toolbar_subtitle);
            mSubtitleTextColor = ta.getColor(R.styleable.EaseToolbar_toolbar_subtitle_textColor, mSubtitleTextColor);
            mSubtitleTextSize = ta.getDimensionPixelSize(R.styleable.EaseToolbar_toolbar_subtitle_textSize, mSubtitleTextSize);

            ta.recycle();
        }
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.ease_toolbar, this);

        tv_title = view.findViewById(R.id.tv_title);
        tv_subtitle = view.findViewById(R.id.tv_subtitle);
    }

    private void setView() {
        tv_title.setText(mTitle == null ? "" : mTitle);
        tv_title.setTextColor(mTitleTextColor);
        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
        tv_title.setVisibility(mTitle == null ? VISIBLE : GONE);

        tv_subtitle.setText(mSubtitle == null ? "" : mSubtitle);
        tv_subtitle.setTextColor(mSubtitleTextColor);
        tv_subtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubtitleTextSize);
        tv_subtitle.setVisibility(mSubtitle == null ? VISIBLE : GONE);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        mTitle = title;

        tv_title.setText(mTitle == null ? "" : mTitle);
        tv_title.setVisibility(mTitle == null ? VISIBLE : GONE);
    }

    /**
     * 设置标题颜色
     */
    public void setTitleTextColor(int textColor) {
        mTitleTextColor = textColor;

        tv_title.setTextColor(mTitleTextColor);
    }

    /**
     * 设置标题字体大小
     */
    public void setTitleTextSize(int textSize) {
        mTitleTextSize = textSize;

        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
    }

    /**
     * 设置标题字体是否粗体
     */
    public void setTitleTextBold(boolean bold) {
        tv_title.getPaint().setFakeBoldText(bold);
    }

    /**
     * 设置副标题
     */
    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;

        tv_subtitle.setText(mSubtitle == null ? "" : mSubtitle);
        tv_subtitle.setVisibility(mSubtitle == null ? VISIBLE : GONE);
    }

    /**
     * 设置副标题颜色
     */
    public void setSubtitleTextColor(int textColor) {
        mSubtitleTextColor = textColor;

        tv_subtitle.setTextColor(mSubtitleTextColor);
    }

    /**
     * 设置副标题字体大小
     */
    public void setSubtitleTextSize(int textSize) {
        mSubtitleTextSize = textSize;

        tv_subtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubtitleTextSize);
    }

    /**
     * 设置标题点击事件
     */
    public void setOnTitleClickListener(@Nullable OnClickListener listener) {
        tv_title.setOnClickListener(listener);
    }

    /**
     * 设置副标题点击事件
     */
    public void setOnSubtitleClickListener(@Nullable OnClickListener listener) {
        tv_subtitle.setOnClickListener(listener);
    }

}