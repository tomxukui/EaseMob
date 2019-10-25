package com.hyphenate.easeui.module.inquiry.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.easeui.module.base.widget.inputview.EaseInputControlView;
import com.hyphenate.easeui.module.base.widget.inputview.EaseInputLayout;
import com.hyphenate.easeui.utils.EaseDensityUtil;

public class EaseInquiryInputView extends EaseInputLayout {

    public EaseInquiryInputView(Context context) {
        super(context);
    }

    public EaseInquiryInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseInquiryInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context) {
        super.initView(context);
        linear_control.setPadding(EaseDensityUtil.dp2px(15), EaseDensityUtil.dp2px(10), EaseDensityUtil.dp2px(15), EaseDensityUtil.dp2px(10));

        EaseInputControlView button1 = new EaseInputControlView(context);
        button1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button1.setText("btn1");

        EaseInputControlView button2 = new EaseInputControlView(context);
        button2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button2.setText("btn2");

        addControlView(button1);
        addControlView(button2);

        View view1 = new View(context);
        view1.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        view1.setBackgroundColor(Color.parseColor("#214332"));

        View view2 = new View(context);
        view2.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        view2.setBackgroundColor(Color.parseColor("#213432"));

        addPanelView(view1);
        addPanelView(view2);
    }

}