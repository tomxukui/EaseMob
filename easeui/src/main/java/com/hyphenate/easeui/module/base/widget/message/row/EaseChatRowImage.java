package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseAndroidLifecycleUtil;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseImageUtil;
import com.hyphenate.easeui.utils.EaseFileUtil;

import java.io.File;

public class EaseChatRowImage extends EaseChatRowFile {

    protected ImageView iv_image;

    private EMImageMessageBody imgBody;

    public EaseChatRowImage(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView(LayoutInflater inflater) {
        inflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        tv_percentage = findViewById(R.id.tv_percentage);
        iv_image = findViewById(R.id.iv_image);
    }

    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) mMessage.getBody();

        if (mMessage.direct() == EMMessage.Direct.SEND) {//已发送的消息
            String filePath = imgBody.getLocalUrl();
            String thumbPath = EaseImageUtil.getThumbnailImagePath(filePath);
            showImageView(thumbPath, filePath);
        }
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        if (msg.direct() == EMMessage.Direct.SEND) {//已发送的消息
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                super.onViewUpdate(msg);

            } else {
                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING || imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                    bar_progress.setVisibility(View.INVISIBLE);
                    tv_percentage.setVisibility(View.INVISIBLE);
                    iv_image.setImageResource(R.drawable.ease_default_image);

                } else {
                    bar_progress.setVisibility(View.GONE);
                    tv_percentage.setVisibility(View.GONE);
                    iv_image.setImageResource(R.drawable.ease_default_image);

                    String thumbPath = imgBody.thumbnailLocalPath();
                    if (!new File(thumbPath).exists()) {
                        thumbPath = EaseImageUtil.getThumbnailImagePath(imgBody.getLocalUrl());
                    }
                    showImageView(thumbPath, imgBody.getLocalUrl());
                }
            }

        } else {//已接收的消息
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    iv_image.setImageResource(R.drawable.ease_default_image);

                } else {
                    bar_progress.setVisibility(View.INVISIBLE);
                    tv_percentage.setVisibility(View.INVISIBLE);
                    iv_image.setImageResource(R.drawable.ease_default_image);
                }

            } else if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    bar_progress.setVisibility(View.VISIBLE);
                    tv_percentage.setVisibility(View.VISIBLE);

                } else {
                    bar_progress.setVisibility(View.INVISIBLE);
                    tv_percentage.setVisibility(View.INVISIBLE);
                }

            } else {
                bar_progress.setVisibility(View.GONE);
                tv_percentage.setVisibility(View.GONE);
                iv_image.setImageResource(R.drawable.ease_default_image);

                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                    thumbPath = EaseImageUtil.getThumbnailImagePath(imgBody.getLocalUrl());
                }

                showImageView(thumbPath, imgBody.getLocalUrl());
            }
        }
    }

    /**
     * 显示图片
     */
    protected void showImageView(String thumbernailPath, String localFullSizePath) {
        if (EaseAndroidLifecycleUtil.canLoadImage(this)) {
            String imgPath = (EaseFileUtil.isFile(localFullSizePath) ? localFullSizePath : thumbernailPath);

            Glide.with(this)
                    .load(imgPath)
                    .apply(new RequestOptions().placeholder(R.drawable.ease_default_image).error(R.drawable.ease_default_image).override(EaseDensityUtil.dp2px(146)))
                    .into(iv_image);
        }
    }

}
