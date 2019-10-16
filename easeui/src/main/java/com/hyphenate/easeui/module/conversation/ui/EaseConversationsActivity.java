package com.hyphenate.easeui.module.conversation.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChainActivity;

public class EaseConversationsActivity extends EaseBaseChainActivity {

    @Override
    protected Fragment getMainFragment() {
        return EaseConversationsFragment.newInstance();
    }

    public static Intent buildIntent(Context context, EaseUser fromUser, String pwd) {
        Intent intent = new Intent(context, EaseConversationsActivity.class);
        intent.putExtra(EXTRA_FROM_USER, fromUser);
        intent.putExtra(EXTRA_PWD, pwd);
        return intent;
    }

}