package com.easeui.app.ui;

import android.content.Intent;
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
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.conversation.ui.EaseConversationsActivity;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryPatientActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_tom;
    private Button btn_jack;
    private AppCompatEditText et_username;
    private AppCompatEditText et_nickname;
    private AppCompatEditText et_avatar;
    private AppCompatEditText et_pwd;
    private Button btn_login;
    private Button btn_logout;
    private AppCompatEditText et_toUsername;
    private AppCompatEditText et_toNickname;
    private AppCompatEditText et_toAvatar;
    private Button btn_chat;
    private Button btn_conversationList;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();
    }

    private void initView() {
        btn_tom = findViewById(R.id.btn_tom);
        btn_jack = findViewById(R.id.btn_jack);
        et_username = findViewById(R.id.et_username);
        et_nickname = findViewById(R.id.et_nickname);
        et_avatar = findViewById(R.id.et_avatar);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);
        btn_logout = findViewById(R.id.btn_logout);
        et_toUsername = findViewById(R.id.et_toUsername);
        et_toNickname = findViewById(R.id.et_toNickname);
        et_toAvatar = findViewById(R.id.et_toAvatar);
        btn_chat = findViewById(R.id.btn_chat);
        btn_conversationList = findViewById(R.id.btn_conversationList);
    }

    private void setView() {
        btn_tom.setOnClickListener(v -> {
            et_username.setText("tom");
            et_nickname.setText("汤姆");
            et_avatar.setText("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1571199911001&di=46867437d9fc793deb524b946911bb69&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F8ebb64306b64f8d614bb59cc042ca46c571c99fcbac2-wliIxx_fw658");
            et_pwd.setText("123456");

            et_toUsername.setText("jack");
            et_toNickname.setText("杰克");
            et_toAvatar.setText("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1571794650&di=0e71fe6b3c283fcd59144de1b3207d74&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F016e7157b17fd30000018c1b234887.png");
        });

        btn_jack.setOnClickListener(v -> {
            et_username.setText("jack");
            et_nickname.setText("杰克");
            et_avatar.setText("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1571794650&di=0e71fe6b3c283fcd59144de1b3207d74&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F016e7157b17fd30000018c1b234887.png");
            et_pwd.setText("123456");

            et_toUsername.setText("tom");
            et_toNickname.setText("汤姆");
            et_toAvatar.setText("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1571199911001&di=46867437d9fc793deb524b946911bb69&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F8ebb64306b64f8d614bb59cc042ca46c571c99fcbac2-wliIxx_fw658");
        });

        btn_login.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String nickname = et_nickname.getText().toString().trim();
            String pwd = et_pwd.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                toast(et_username.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(nickname)) {
                toast(et_nickname.getHint().toString());
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

                } else {//不同账号
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

        btn_chat.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String nickname = et_nickname.getText().toString().trim();
            String avatar = et_avatar.getText().toString().trim();
            String pwd = et_pwd.getText().toString().trim();
            String toUsername = et_toUsername.getText().toString().trim();
            String toNickname = et_toNickname.getText().toString().trim();
            String toAvatar = et_toAvatar.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                toast(et_username.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(nickname)) {
                toast(et_nickname.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                toast(et_pwd.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(toUsername)) {
                toast(et_toUsername.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(toNickname)) {
                toast(et_toNickname.getHint().toString());
                return;
            }
            EaseUser fromUser = new EaseUser(username, nickname, avatar, null);
            EaseUser toUser = new EaseUser(toUsername, toNickname, toAvatar);

            Intent intent = EaseInquiryPatientActivity.buildIntent(MainActivity.this, fromUser, pwd, toUser, EaseType.CHAT);
            startActivity(intent);
        });

        btn_conversationList.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String nickname = et_nickname.getText().toString().trim();
            String avatar = et_avatar.getText().toString().trim();
            String pwd = et_pwd.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                toast(et_username.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(nickname)) {
                toast(et_nickname.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                toast(et_pwd.getHint().toString());
                return;
            }
            EaseUser fromUser = new EaseUser(username, nickname, avatar, null);

            Intent intent = EaseConversationsActivity.buildIntent(MainActivity.this, fromUser, pwd);
            startActivity(intent);
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
