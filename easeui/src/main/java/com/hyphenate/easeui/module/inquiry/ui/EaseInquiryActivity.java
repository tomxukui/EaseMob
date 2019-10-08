package com.hyphenate.easeui.module.inquiry.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.bean.EaseAccount;
import com.hyphenate.easeui.bean.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChainActivity;
import com.hyphenate.easeui.utils.EaseContactUtil;

public class EaseInquiryActivity extends EaseBaseChainActivity {

    private static final String EXTRA_TO_USERNAME = "EXTRA_TO_USERNAME";//聊天对象
    private static final String EXTRA_CHAT_ENABLED = "EXTRA_CHAT_ENABLED";//是否具备聊天功能
    private static final String EXTRA_FINISH_CHAT_ENABLED = "EXTRA_FINISH_CHAT_ENABLED";//是否具备结束聊天功能
    private static final String EXTRA_SHOW_ENDED_MENU = "EXTRA_SHOW_ENDED_MENU";//已结束的菜单是否显示

    private String mToUsername;
    private boolean mChatEnabled;//是否可以聊天
    private boolean mFinishChatEnabled;//是否可以结束聊天
    private boolean mShowEndedMenu;//是否显示结束后的菜单

    @Override
    protected void initData() {
        super.initData();
        mToUsername = getIntent().getStringExtra(EXTRA_TO_USERNAME);
        mChatEnabled = getIntent().getBooleanExtra(EXTRA_CHAT_ENABLED, true);
        mFinishChatEnabled = getIntent().getBooleanExtra(EXTRA_FINISH_CHAT_ENABLED, false);
        mShowEndedMenu = getIntent().getBooleanExtra(EXTRA_SHOW_ENDED_MENU, true);
    }

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryFragment.newInstance(mToUsername, mChatEnabled, mFinishChatEnabled, mShowEndedMenu);
    }

    public static Intent buildIntent(Context context, EaseAccount account, EaseUser toUser, boolean chatEnabled, boolean finishChatEnabled, boolean showEndedMenu) {
        EaseContactUtil.getInstance().saveContact(account);
        EaseContactUtil.getInstance().saveContact(toUser);

        Intent intent = new Intent(context, EaseInquiryActivity.class);
        intent.putExtra(EXTRA_USERNAME, account.getUsername());
        intent.putExtra(EXTRA_PWD, account.getPwd());
        intent.putExtra(EXTRA_TO_USERNAME, toUser.getUsername());
        intent.putExtra(EXTRA_CHAT_ENABLED, chatEnabled);
        intent.putExtra(EXTRA_FINISH_CHAT_ENABLED, finishChatEnabled);
        intent.putExtra(EXTRA_SHOW_ENDED_MENU, showEndedMenu);
        return intent;
    }

}