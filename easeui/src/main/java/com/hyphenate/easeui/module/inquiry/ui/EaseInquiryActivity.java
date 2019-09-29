package com.hyphenate.easeui.module.inquiry.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.domain.EaseAccount;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChainActivity;
import com.hyphenate.easeui.utils.EaseContactUtil;

public class EaseInquiryActivity extends EaseBaseChainActivity {

    private static final String EXTRA_TO_USER = "EXTRA_TO_USER";//聊天对象
    private static final String EXTRA_CHAT_ENABLED = "EXTRA_CHAT_ENABLED";//是否具备聊天功能
    private static final String EXTRA_FINISH_CHAT_ENABLED = "EXTRA_FINISH_CHAT_ENABLED";//是否具备结束聊天功能

    private EaseUser mToUser;
    private boolean mChatEnabled;//是否可以聊天
    private boolean mFinishChatEnabled;//是否可以结束聊天

    @Override
    protected void initData() {
        super.initData();
        mToUser = (EaseUser) getIntent().getSerializableExtra(EXTRA_TO_USER);
        mChatEnabled = getIntent().getBooleanExtra(EXTRA_CHAT_ENABLED, true);
        mFinishChatEnabled = getIntent().getBooleanExtra(EXTRA_FINISH_CHAT_ENABLED, false);
    }

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryFragment.newInstance(mToUser.getUsername(), mChatEnabled, mFinishChatEnabled);
    }

    public static Intent buildIntent(Context context, EaseAccount account, EaseUser toUser, boolean chatEnabled, boolean finishChatEnabled) {
        EaseContactUtil.getInstance().saveContact(account);
        EaseContactUtil.getInstance().saveContact(toUser);

        Intent intent = new Intent(context, EaseInquiryActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, account);
        intent.putExtra(EXTRA_TO_USER, toUser);
        intent.putExtra(EXTRA_CHAT_ENABLED, chatEnabled);
        intent.putExtra(EXTRA_FINISH_CHAT_ENABLED, finishChatEnabled);
        return intent;
    }

}