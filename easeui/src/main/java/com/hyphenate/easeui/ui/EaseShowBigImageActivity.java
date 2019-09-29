package com.hyphenate.easeui.ui;

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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

/**
 * download and show original image
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {

    private static final String TAG = "ShowBigImage";

    private ProgressDialog pd;
    private EasePhotoView image;
    private ProgressBar loadLocalPb;
    private int default_res = R.drawable.ease_default_image;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_show_big_image;
    }

    @Override
    protected void initView() {
        super.initView();
        image = findViewById(R.id.image);
        loadLocalPb = findViewById(R.id.pb_load_local);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        default_res = getIntent().getIntExtra("default_image", R.mipmap.ease_ic_portrait);
        Uri uri = getIntent().getParcelableExtra("uri");
        localFilePath = getIntent().getExtras().getString("localUrl");
        String msgId = getIntent().getExtras().getString("messageId");

        //show the image if it exist in local path
        if (uri != null && new File(uri.getPath()).exists()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            bitmap = EaseImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (msgId != null) {
            downloadImage(msgId);
        } else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(v -> finish());
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
        File temp = new File(localFilePath);
        final String tempPath = temp.getParent() + "/temp_" + temp.getName();
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {
                runOnUiThread(() -> {
                    new File(tempPath).renameTo(new File(localFilePath));

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int screenWidth = metrics.widthPixels;
                    int screenHeight = metrics.heightPixels;

                    bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                    if (bitmap == null) {
                        image.setImageResource(default_res);
                    } else {
                        image.setImageBitmap(bitmap);
                        EaseImageCache.getInstance().put(localFilePath, bitmap);
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
                    image.setImageResource(default_res);
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
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
