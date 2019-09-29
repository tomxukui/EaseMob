package com.hyphenate.easeui.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.EaseConstant;

public class EaseInquiryActivity extends EaseBaseChainActivity {

    private static final String EXTRA_TO_USERNAME = "EXTRA_TO_USERNAME";//聊天对象
    private static final String EXTRA_CHAT_ENABLED = "EXTRA_CHAT_ENABLED";//是否具备聊天功能
    private static final String EXTRA_FINISH_CHAT_ENABLED = "EXTRA_FINISH_CHAT_ENABLED";//是否具备结束聊天功能

    private String mToUsername;
    private boolean mChatEnabled;//是否可以聊天
    private boolean mFinishChatEnabled;//是否可以结束聊天

    @Override
    protected void initData() {
        super.initData();
        mToUsername = getIntent().getStringExtra(EaseConstant.EXTRA_TO_USERNAME);
        mChatEnabled = getIntent().getBooleanExtra(EXTRA_CHAT_ENABLED, true);
        mFinishChatEnabled = getIntent().getBooleanExtra(EXTRA_FINISH_CHAT_ENABLED, false);
    }

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryFragment.newInstance(mToUsername, mChatEnabled, mFinishChatEnabled);
    }

    public static Intent buildIntent(Context context, String username, boolean chatEnabled, boolean finishChatEnabled) {
        Intent intent = new Intent(context, EaseInquiryActivity.class);
        intent.putExtra(EXTRA_TO_USERNAME, username);
        intent.putExtra(EXTRA_CHAT_ENABLED, chatEnabled);
        intent.putExtra(EXTRA_FINISH_CHAT_ENABLED, finishChatEnabled);
        return intent;
    }

}