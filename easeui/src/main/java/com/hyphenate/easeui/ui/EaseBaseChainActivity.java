package com.hyphenate.easeui.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.EasePaperLayer;
import com.hyphenate.easeui.widget.EaseToolbar;

public abstract class EaseBaseChainActivity extends EaseBaseActivity {

    public static final String EXTRA_MY_USERNAME = "EXTRA_MY_USERNAME";
    public static final String EXTRA_MY_USERPWD = "EXTRA_MY_USERPWD";
    public static final String EXTRA_NEED_LOGOUT = "EXTRA_NEED_LOGOUT";

    private EaseToolbar toolbar;
    private EasePaperLayer layer_paper;

    private String mMyUsername;
    private String mMyUserPwd;
    private boolean mNeedLogout;//是否在页面关闭的时候自动退出

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_base_chain;
    }

    @Override
    protected void initData() {
        super.initData();
        mMyUsername = getIntent().getStringExtra(EXTRA_MY_USERNAME);
        mMyUserPwd = getIntent().getStringExtra(EXTRA_MY_USERPWD);
        mNeedLogout = getIntent().getBooleanExtra(EXTRA_NEED_LOGOUT, false);
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
        if (mMyUsername != null && mMyUserPwd != null) {
            layer_paper.autoRefresh();

        } else {
            toolbar.setVisibility(View.GONE);

            loadMainFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNeedLogout) {
            assertLogout();
        }
    }

    private void login() {
        if (EMClient.getInstance().isConnected()) {//账号已登录
            String currentUsername = EMClient.getInstance().getCurrentUser();

            if (TextUtils.equals(mMyUsername, currentUsername)) {//同账号
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
        EMClient.getInstance().login(mMyUsername, mMyUserPwd, new EMCallBack() {

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

    /**
     * 退出环信
     */
    private void assertLogout() {
        if (EMClient.getInstance().isConnected()) {
            EMClient.getInstance().logout(true, null);
        }
    }

    protected abstract Fragment getMainFragment();

    private void loadMainFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, getMainFragment()).commitAllowingStateLoss();
    }

}