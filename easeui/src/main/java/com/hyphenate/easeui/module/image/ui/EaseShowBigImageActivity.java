package com.hyphenate.easeui.module.image.ui;

import java.io.File;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.utils.AndroidLifecycleUtil;
import com.hyphenate.easeui.module.base.widget.photoview.EasePhotoView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

/**
 * download and show original image
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {

    private static final String EXTRA_DEFAULT_IMG = "EXTRA_DEFAULT_IMG";
    private static final String EXTRA_MSG_ID = "EXTRA_MSG_ID";
    private static final String EXTRA_IMG_PATH = "EXTRA_IMG_PATH";

    private EasePhotoView iv_photo;
    private ProgressBar bar_loading;

    private int mDefaultImg;
    private String mMsgId;
    private String mImgPath;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_show_big_image;
    }

    @Override
    protected void initData() {
        super.initData();
        mDefaultImg = getIntent().getIntExtra(EXTRA_DEFAULT_IMG, R.drawable.ease_default_image);
        mMsgId = getIntent().getStringExtra(EXTRA_MSG_ID);
        mImgPath = getIntent().getStringExtra(EXTRA_IMG_PATH);
    }

    @Override
    protected void initView() {
        super.initView();
        iv_photo = findViewById(R.id.iv_photo);
        bar_loading = findViewById(R.id.bar_loading);
    }

    @Override
    protected void setView() {
        super.setView();
        iv_photo.setOnClickListener(v -> finish());

        if (TextUtils.isEmpty(mImgPath)) {
            iv_photo.setImageResource(mDefaultImg);

        } else {
            File file = new File(mImgPath);

            if (file != null && file.exists() && file.isFile()) {
                showImageFile(mImgPath);

            } else if (!TextUtils.isEmpty(mMsgId)) {
                loadShowImageFile(mMsgId, mImgPath);

            } else {
                iv_photo.setImageResource(mDefaultImg);
            }
        }
    }

    /**
     * 显示本地图片
     */
    private void showImageFile(String filePath) {
        if (AndroidLifecycleUtil.canLoadImage(this)) {
            Glide.with(this)
                    .load(filePath)
                    .apply(new RequestOptions().error(mDefaultImg).fallback(mDefaultImg))
                    .into(iv_photo);
        }
    }

    /**
     * 下载并显示本地图片
     */
    private void loadShowImageFile(String msgId, String filePath) {
        bar_loading.setVisibility(View.VISIBLE);

        EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
        message.setMessageStatusCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    if (isFinishing()) {
                        return;
                    }

                    bar_loading.setVisibility(View.GONE);

                    showImageFile(filePath);
                });
            }

            @Override
            public void onError(int i, String s) {
                runOnUiThread(() -> {
                    if (isFinishing()) {
                        return;
                    }

                    bar_loading.setVisibility(View.GONE);

                    iv_photo.setImageResource(mDefaultImg);
                });
            }

            @Override
            public void onProgress(int i, String s) {
            }

        });

        EMClient.getInstance().chatManager().downloadAttachment(message);
    }

    public static Intent buildIntent(Context context, String msgId, String imgPath, @Nullable Integer defaultImg) {
        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        intent.putExtra(EXTRA_MSG_ID, msgId);
        intent.putExtra(EXTRA_IMG_PATH, imgPath);
        intent.putExtra(EXTRA_DEFAULT_IMG, defaultImg);
        return intent;
    }

}