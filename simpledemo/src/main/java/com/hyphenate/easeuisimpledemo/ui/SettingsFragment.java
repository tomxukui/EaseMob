package com.hyphenate.easeuisimpledemo.ui;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.module.base.ui.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeuisimpledemo.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsFragment extends EaseBaseFragment {

    private EaseToolbar toolbar;
    private Button btn_logout;

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        btn_logout = view.findViewById(R.id.btn_logout);
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        btn_logout.setOnClickListener(v -> logout());
    }

    private void logout() {
        EMClient.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String error) {
                EaseToastUtil.show(error);
            }

        });
    }

}