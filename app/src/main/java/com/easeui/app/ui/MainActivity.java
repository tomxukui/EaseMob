package com.easeui.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.easeui.app.R;
import com.easeui.app.dialog.LoadingDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText et_username;
    private AppCompatEditText et_realname;
    private AppCompatEditText et_pwd;
    private Button btn_login;
    private Button btn_logout;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();
    }

    private void initView() {
        et_username = findViewById(R.id.et_username);
        et_realname = findViewById(R.id.et_realname);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);
        btn_logout = findViewById(R.id.btn_logout);
    }

    private void setView() {
        btn_login.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String realname = et_realname.getText().toString().trim();
            String pwd = et_pwd.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                toast(et_username.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(realname)) {
                toast(et_realname.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                toast(et_pwd.getHint().toString());
                return;
            }

            if (EMClient.getInstance().isConnected()) {//账号已登录
                String currentUsername = EMClient.getInstance().getCurrentUser();

                if (TextUtils.equals(username, currentUsername)) {//同账号
                    toast("账号已登录");

                } else {
                    showLoadingDialog("切换账号中...");

                    EMClient.getInstance().logout(true, new EMCallBack() {

                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> EMClient.getInstance().login(username, pwd, new EMCallBack() {

                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        dismissLoadingDialog();

                                        toast("切换账号成功");
                                    });
                                }

                                @Override
                                public void onError(int i, String s) {
                                    runOnUiThread(() -> {
                                        dismissLoadingDialog();

                                        toast("切换账号失败");
                                    });
                                }

                                @Override
                                public void onProgress(int i, String s) {
                                }

                            }));
                        }

                        @Override
                        public void onError(int i, String s) {
                            runOnUiThread(() -> {
                                dismissLoadingDialog();

                                toast("账号切换失败");
                            });
                        }

                        @Override
                        public void onProgress(int i, String s) {
                        }

                    });
                }

            } else {//账号未登录
                showLoadingDialog("登录中...");

                EMClient.getInstance().login(username, pwd, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            dismissLoadingDialog();

                            toast("登录成功");
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(() -> {
                            dismissLoadingDialog();

                            toast("登录失败");
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }

                });
            }
        });

        btn_logout.setOnClickListener(v -> {
            if (EMClient.getInstance().isConnected()) {
                showLoadingDialog("登出中...");

                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            dismissLoadingDialog();

                            toast("账号登出成功");
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(() -> {
                            dismissLoadingDialog();

                            toast("账号登出失败");
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }

                });

            } else {
                toast("账号未登录");
            }
        });
    }

    private void showLoadingDialog(String message) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog.Builder(this)
                    .create();
        }

        mLoadingDialog.show(message, true, false, null, null);
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }

            mLoadingDialog = null;
        }
    }

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
