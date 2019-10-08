package com.hyphenate.easeui.module.inquiry.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.hyphenate.easeui.R;

public class EaseInquiryEndedMenu extends LinearLayoutCompat {

    public EaseInquiryEndedMenu(Context context) {
        super(context);
        initView(context);
    }

    public EaseInquiryEndedMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EaseInquiryEndedMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_inquiry_ended_menu, this);
    }

}