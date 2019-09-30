package com.hyphenate.easeui.module.image.ui;

import java.io.File;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseLoadLocalBigImgTask;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;
import com.hyphenate.util.ImageUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

/**
 * download and show original image
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {

    private static final String EXTRA_DEFAULT_IMAGE = "DEFAULT_IMAGE";
    private static final String EXTRA_URI = "EXTRA_URI";
    private static final String EXTRA_LOCAL_URL = "EXTRA_LOCAL_URL";
    private static final String EXTRA_MSG_ID = "EXTRA_MSG_ID";

    private ProgressDialog pd;
    private EasePhotoView iv_photo;
    private ProgressBar bar_loading;

    private int mDefaultImage;
    private Uri mUri;
    private String mLocalUrl;
    private String mMsgId;

    private Bitmap bitmap;
    private boolean isDownloaded;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_show_big_image;
    }

    @Override
    protected void initData() {
        super.initData();
        mDefaultImage = getIntent().getIntExtra(EXTRA_DEFAULT_IMAGE, R.drawable.ease_default_image);
        mUri = getIntent().getParcelableExtra(EXTRA_URI);
        mLocalUrl = getIntent().getStringExtra(EXTRA_LOCAL_URL);
        mMsgId = getIntent().getStringExtra(EXTRA_MSG_ID);
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

        if (mUri != null && new File(mUri.getPath()).exists()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            bitmap = EaseImageCache.getInstance().get(mUri.getPath());

            if (bitmap == null) {
                EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, mUri.getPath(), iv_photo, bar_loading, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }

            } else {
                iv_photo.setImageBitmap(bitmap);
            }

        } else if (mMsgId != null) {
            downloadImage(mMsgId);

        } else {
            iv_photo.setImageResource(mDefaultImage);
        }
    }

    /**
     * download image
     */
    @SuppressLint("NewApi")
    private void downloadImage(final String msgId) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        File temp = new File(mLocalUrl);
        final String tempPath = temp.getParent() + "/temp_" + temp.getName();
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {
                runOnUiThread(() -> {
                    new File(tempPath).renameTo(new File(mLocalUrl));

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int screenWidth = metrics.widthPixels;
                    int screenHeight = metrics.heightPixels;

                    bitmap = ImageUtils.decodeScaleImage(mLocalUrl, screenWidth, screenHeight);
                    if (bitmap == null) {
                        iv_photo.setImageResource(mDefaultImage);
                    } else {
                        iv_photo.setImageBitmap(bitmap);
                        EaseImageCache.getInstance().put(mLocalUrl, bitmap);
                        isDownloaded = true;
                    }
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    if (pd != null) {
                        pd.dismiss();
                    }
                });
            }

            public void onError(final int error, String msg) {
                File file = new File(tempPath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                runOnUiThread(() -> {
                    if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
                        return;
                    }
                    iv_photo.setImageResource(mDefaultImage);
                    pd.dismiss();
                    if (error == EMError.FILE_NOT_FOUND) {
                        EaseToastUtil.show(R.string.Image_expired);
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(() -> {
                    if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
                        return;
                    }
                    pd.setMessage(str2 + progress + "%");
                });
            }
        };

        EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
        msg.setMessageStatusCallback(callback);

        EMClient.getInstance().chatManager().downloadAttachment(msg);
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded) {
            setResult(RESULT_OK);
        }

        finish();
    }

    public static Intent buildIntent(Context context, Uri uri, @Nullable Integer defaultImage) {
        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_DEFAULT_IMAGE, defaultImage);
        return intent;
    }

    public static Intent buildIntent(Context context, String msgId, String localUrl, @Nullable Integer defaultImage) {
        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        intent.putExtra(EXTRA_MSG_ID, msgId);
        intent.putExtra(EXTRA_LOCAL_URL, localUrl);
        intent.putExtra(EXTRA_DEFAULT_IMAGE, defaultImage);
        return intent;
    }

}
