package com.easeui.app.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.conversation.ui.EaseConversationsActivity;

public class ConverstationsActivity extends EaseConversationsActivity {

    @Override
    protected Fragment getMainFragment() {
        return ConversationsFragment.newInstance();
    }

    /**
     * 组建Intent
     *
     * @param context  上下文
     * @param fromUser 自己账号
     * @param pwd      密码. 如果确定环信账号已登录, 则可不传, 否则需要传入登录密码, 页面会自动登录
     */
    public static Intent buildIntent(Context context, EaseUser fromUser, @Nullable String pwd) {
        Intent intent = new Intent(context, ConverstationsActivity.class);
        intent.putExtra(EXTRA_FROM_USER, fromUser);
        intent.putExtra(EXTRA_PWD, pwd);
        return intent;
    }

}