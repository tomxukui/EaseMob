package com.hyphenate.easeui.module.file.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.io.File;

/**
 * 查看文件
 */
public class EaseShowFileActivity extends EaseBaseActivity {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private ProgressBar progressBar;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_show_file;
    }

    @Override
    protected void initView() {
        super.initView();
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EMMessage message = getIntent().getParcelableExtra(EXTRA_MESSAGE);

        if (!(message.getBody() instanceof EMFileMessageBody)) {
            EaseToastUtil.show("Unsupported message body");
            finish();
            return;
        }

        final File file = new File(((EMFileMessageBody) message.getBody()).getLocalUrl());

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    EaseCompat.openFile(file, EaseShowFileActivity.this);
                    finish();
                });

            }

            @Override
            public void onError(final int code, final String error) {
                runOnUiThread(() -> {
                    if (file != null && file.exists() && file.isFile()) {
                        file.delete();
                    }

                    String str4 = EaseContextCompatUtil.getString(R.string.Failed_to_download_file);
                    if (code == EMError.FILE_NOT_FOUND) {
                        str4 = EaseContextCompatUtil.getString(R.string.File_expired);
                    }

                    EaseToastUtil.show(str4 + message);
                    finish();
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                runOnUiThread(() -> progressBar.setProgress(progress));
            }
        });

        EMClient.getInstance().chatManager().downloadAttachment(message);
    }

    public static Intent buildIntent(Context context, EMMessage message) {
        Intent intent = new Intent(context, EaseShowFileActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

}