package com.hyphenate.easeui.module.inquiry.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChainActivity;

public class EaseInquiryActivity extends EaseBaseChainActivity {

    protected static final String EXTRA_TO_USER = "EXTRA_TO_USER";
    protected static final String EXTRA_CHAT_MODE = "EXTRA_CHAT_MODE";

    protected EaseUser mToUser;
    @EaseType.ChatMode
    protected String mChatMode;

    @Override
    protected void initData() {
        super.initData();
        mToUser = (EaseUser) getIntent().getSerializableExtra(EXTRA_TO_USER);
        mChatMode = getIntent().getStringExtra(EXTRA_CHAT_MODE);
    }

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryFragment.newInstance(mFromUser, mToUser, mChatMode);
    }

    public static Intent buildIntent(Context context, EaseUser fromUser, String pwd, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        Intent intent = new Intent(context, EaseInquiryActivity.class);
        intent.putExtra(EXTRA_FROM_USER, fromUser);
        intent.putExtra(EXTRA_PWD, pwd);
        intent.putExtra(EXTRA_TO_USER, toUser);
        intent.putExtra(EXTRA_CHAT_MODE, chatMode);
        return intent;
    }

}