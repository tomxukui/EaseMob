package com.hyphenate.easeui.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.io.File;

public class EaseShowNormalFileActivity extends EaseBaseActivity {

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
		final EMMessage message = getIntent().getParcelableExtra("msg");
        if (!(message.getBody() instanceof EMFileMessageBody)) {
            EaseToastUtil.show("Unsupported message body");
            finish();
            return;
        }
        final File file = new File(((EMFileMessageBody)message.getBody()).getLocalUrl());

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EaseCompat.openFile(file, EaseShowNormalFileActivity.this);
                        finish();
                    }
                });

            }

            @Override
            public void onError(final int code, final String error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(file != null && file.exists()&&file.isFile())
                            file.delete();
                        String str4 = getResources().getString(R.string.Failed_to_download_file);
                        if (code == EMError.FILE_NOT_FOUND) {
                            str4 = getResources().getString(R.string.File_expired);
                        }
                        EaseToastUtil.show(str4+message);
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
	}


}
