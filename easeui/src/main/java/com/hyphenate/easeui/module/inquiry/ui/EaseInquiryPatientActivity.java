package com.hyphenate.easeui.module.inquiry.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;

public class EaseInquiryPatientActivity extends EaseInquiryActivity {

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryPatientFragment.newInstance(mFromUser, mToUser, mChatMode);
    }

    public static Intent buildIntent(Context context, EaseUser fromUser, String pwd, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        Intent intent = new Intent(context, EaseInquiryPatientActivity.class);
        intent.putExtra(EXTRA_FROM_USER, fromUser);
        intent.putExtra(EXTRA_PWD, pwd);
        intent.putExtra(EXTRA_TO_USER, toUser);
        intent.putExtra(EXTRA_CHAT_MODE, chatMode);
        return intent;
    }

}