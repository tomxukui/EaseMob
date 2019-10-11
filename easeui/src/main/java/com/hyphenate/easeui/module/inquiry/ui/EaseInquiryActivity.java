package com.hyphenate.easeui.module.inquiry.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.bean.EaseAccount;
import com.hyphenate.easeui.bean.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChainActivity;
import com.hyphenate.easeui.utils.EaseContactUtil;

public class EaseInquiryActivity extends EaseBaseChainActivity {

    protected static final String EXTRA_TO_USERNAME = "EXTRA_TO_USERNAME";

    protected String mToUsername;

    @Override
    protected void initData() {
        super.initData();
        mToUsername = getIntent().getStringExtra(EXTRA_TO_USERNAME);
    }

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryFragment.newInstance(mToUsername);
    }

    public static Intent buildIntent(Context context, EaseAccount account, EaseUser toUser) {
        EaseContactUtil.getInstance().saveContact(account);
        EaseContactUtil.getInstance().saveContact(toUser);

        Intent intent = new Intent(context, EaseInquiryActivity.class);
        intent.putExtra(EXTRA_USERNAME, account.getUsername());
        intent.putExtra(EXTRA_PWD, account.getPwd());
        intent.putExtra(EXTRA_TO_USERNAME, toUser.getUsername());
        return intent;
    }

}