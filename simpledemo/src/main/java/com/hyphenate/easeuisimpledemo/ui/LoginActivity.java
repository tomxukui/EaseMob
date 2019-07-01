package com.hyphenate.easeuisimpledemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeuisimpledemo.R;

/**
 * 登录页面
 */
public class LoginActivity extends EaseBaseActivity {

    private EditText et_username;
    private EditText et_password;
    private Button btn_login;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
    }

    @Override
    protected void setView() {
        super.setView();
        btn_login.setOnClickListener(v -> login());
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (EMClient.getInstance().isLoggedInBefore()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void login() {
        String username = et_username.getText().toString();
        String pwd = et_password.getText().toString();

        EMClient.getInstance().login(username, pwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String error) {
                runOnUiThread(() -> EaseToastUtil.show(error));
            }

        });
    }

}