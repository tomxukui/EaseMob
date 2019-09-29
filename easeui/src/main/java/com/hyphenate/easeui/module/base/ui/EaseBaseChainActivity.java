package com.hyphenate.easeui.module.base.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.widget.EasePaperLayer;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;

public abstract class EaseBaseChainActivity extends EaseBaseActivity {

    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static final String EXTRA_PWD = "EXTRA_PWD";

    private EaseToolbar toolbar;
    private EasePaperLayer layer_paper;

    private String mUsername;
    private String mPwd;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_base_chain;
    }

    @Override
    protected void initData() {
        super.initData();
        mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        mPwd = getIntent().getStringExtra(EXTRA_PWD);
    }

    @Override
    protected void initView() {
        super.initView();
        toolbar = findViewById(R.id.toolbar);
        layer_paper = findViewById(R.id.layer_paper);
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
    }

    @Override
    protected void setView() {
        super.setView();
        layer_paper.setOnRefreshListener(view -> login());
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        layer_paper.autoRefresh();
    }

    private void login() {
        if (EMClient.getInstance().isConnected()) {//账号已登录
            if (TextUtils.equals(mUsername, EMClient.getInstance().getCurrentUser())) {//同账号
                layer_paper.finishSuccess();
                toolbar.setVisibility(View.GONE);

                loadMainFragment();

            } else {//不同账号
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> assertlogin());
                    }

                    @Override
                    public void onError(int i, String message) {
                        runOnUiThread(() -> layer_paper.finishFailure(message));
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }

                });
            }

        } else {//账号未登录
            assertlogin();
        }
    }

    /**
     * 登录环信
     */
    private void assertlogin() {
        EMClient.getInstance().login(mUsername, mPwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    layer_paper.finishSuccess();
                    toolbar.setVisibility(View.GONE);

                    loadMainFragment();
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, final String message) {
                runOnUiThread(() -> layer_paper.finishFailure(message));
            }

        });
    }

    protected abstract Fragment getMainFragment();

    private void loadMainFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, getMainFragment()).commitAllowingStateLoss();
    }

}