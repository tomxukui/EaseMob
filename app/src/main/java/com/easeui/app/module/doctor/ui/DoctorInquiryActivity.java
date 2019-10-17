package com.easeui.app.module.doctor.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryActivity;

public class DoctorInquiryActivity extends EaseInquiryActivity {

    @Override
    protected Fragment getMainFragment() {
        return DoctorInquiryFragment.newInstance(mFromUser, mToUser, mChatMode, "12345678910");
    }

    /**
     * 组建Intent
     *
     * @param context  上下文
     * @param fromUser 自己账号
     * @param pwd      密码. 如果确定环信账号已登录, 则可不传, 否则需要传入登录密码, 页面会自动登录
     * @param toUser   对方账号
     * @param chatMode 问诊模式
     */
    public static Intent buildIntent(Context context, EaseUser fromUser, @Nullable String pwd, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        Intent intent = new Intent(context, DoctorInquiryActivity.class);
        intent.putExtra(EXTRA_FROM_USER, fromUser);
        intent.putExtra(EXTRA_PWD, pwd);
        intent.putExtra(EXTRA_TO_USER, toUser);
        intent.putExtra(EXTRA_CHAT_MODE, chatMode);
        return intent;
    }

}