package com.hyphenate.easeuisimpledemo.ui;

import android.content.Intent;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatActivity;
import com.hyphenate.easeuisimpledemo.R;

public class LaunchActivity extends EaseBaseActivity {

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.btn_login).setOnClickListener(v -> startActivity(new Intent(LaunchActivity.this, LoginActivity.class)));

        findViewById(R.id.btn_login_chat).setOnClickListener(v -> {
            Intent intent = new EaseChatActivity.Builder(LaunchActivity.this)
                    .setChatType(EaseConstant.CHATTYPE_SINGLE)
                    .setToUser("tom")
                    .needLogin("e3bb93c9396848508cfa2cc1bff27b3d", "XldfBS379c")
                    .create();

            startActivity(intent);
        });
    }

}
