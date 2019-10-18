package com.hyphenate.easeui.module.video.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.io.File;

/**
 * 查看视频
 */
public class EaseShowVideoActivity extends EaseBaseActivity {

    private RelativeLayout loadingLayout;
    private ProgressBar progressBar;

    private String localFilePath;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_show_video;
    }

    @Override
    protected void initView() {
        super.initView();
        loadingLayout = findViewById(R.id.loading_layout);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final EMMessage message = getIntent().getParcelableExtra("msg");
        if (!(message.getBody() instanceof EMVideoMessageBody)) {
            EaseToastUtil.show("Unsupported message body");
            finish();
            return;
        }

        EMVideoMessageBody messageBody = (EMVideoMessageBody) message.getBody();

        localFilePath = messageBody.getLocalUrl();

        if (localFilePath != null && new File(localFilePath).exists()) {
            showLocalVideo(localFilePath);

        } else {
            downloadVideo(message);
        }
    }

    /**
     * show local video
     *
     * @param localPath -- local path of the video file
     */
    private void showLocalVideo(String localPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(EaseCompat.getUriForFile(this, new File(localPath)),
                "video/mp4");
        // 注意添加该flag,用于Android7.0以上设备获取相册文件权限.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
        finish();
    }

    /**
     * download video file
     */
    private void downloadVideo(EMMessage message) {
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    loadingLayout.setVisibility(View.GONE);
                    progressBar.setProgress(0);
                    showLocalVideo(localFilePath);
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                runOnUiThread(() -> progressBar.setProgress(progress));

            }

            @Override
            public void onError(final int error, String msg) {
                File file = new File(localFilePath);
                if (file.exists()) {
                    file.delete();
                }
                runOnUiThread(() -> {
                    if (error == EMError.FILE_NOT_FOUND) {
                        EaseToastUtil.show(R.string.Video_expired);
                    }
                });
            }
        });
        EMClient.getInstance().chatManager().downloadAttachment(message);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}