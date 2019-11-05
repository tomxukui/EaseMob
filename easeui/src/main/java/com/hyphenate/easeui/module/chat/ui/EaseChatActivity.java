package com.hyphenate.easeui.module.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChainActivity;

public class EaseChatActivity extends EaseBaseChainActivity {

    protected static final String EXTRA_TO_USER = "EXTRA_TO_USER";

    protected EaseUser mToUser;

    @Override
    protected void initData() {
        super.initData();
        mToUser = (EaseUser) getIntent().getSerializableExtra(EXTRA_TO_USER);
    }

    @Override
    protected Fragment getMainFragment() {
        return EaseChatFragment.newInstance(mFromUser, mToUser);
    }

    /**
     * 组建Intent
     *
     * @param context  上下文
     * @param fromUser 自己账号
     * @param pwd      密码. 如果确定环信账号已登录, 则可不传, 否则需要传入登录密码, 页面会自动登录
     * @param toUser   对方账号
     */
    public static Intent buildIntent(Context context, EaseUser fromUser, @Nullable String pwd, EaseUser toUser) {
        Intent intent = new Intent(context, EaseChatActivity.class);
        intent.putExtra(EXTRA_FROM_USER, fromUser);
        intent.putExtra(EXTRA_PWD, pwd);
        intent.putExtra(EXTRA_TO_USER, toUser);
        return intent;
    }

}