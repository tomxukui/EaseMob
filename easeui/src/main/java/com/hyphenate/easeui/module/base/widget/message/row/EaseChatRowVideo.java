package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseAndroidLifecycleUtil;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseFileUtil;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.TextFormater;

import java.io.File;

public class EaseChatRowVideo extends EaseChatRowFile {

    private ImageView imageView;
    private TextView sizeView;
    private TextView timeLengthView;

    public EaseChatRowVideo(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_video : R.layout.ease_row_sent_video, this);
    }

    @Override
    protected void onFindViewById() {
        imageView = findViewById(R.id.chatting_content_iv);
        sizeView = findViewById(R.id.chatting_size_iv);
        timeLengthView = findViewById(R.id.chatting_length_iv);
        percentageView = findViewById(R.id.percentage);
    }

    @Override
    protected void onSetUpView() {
        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
        String localThumb = videoBody.getLocalThumb();

        if (localThumb != null) {
            showVideoThumbView(localThumb, videoBody.getThumbnailUrl(), message);
        }

        if (videoBody.getDuration() > 0) {
            String time = DateUtils.toTime(videoBody.getDuration());
            timeLengthView.setText(time);
        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
                sizeView.setText(size);
            }

        } else {
            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
                sizeView.setText(size);
            }
        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.ease_default_image);

            } else {
                imageView.setImageResource(R.drawable.ease_default_image);
                if (localThumb != null) {
                    showVideoThumbView(localThumb, videoBody.getThumbnailUrl(), message);
                }
            }

        } else {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING || videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.drawable.ease_default_image);

            } else {
                progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ease_default_image);
                showVideoThumbView(localThumb, videoBody.getThumbnailUrl(), message);
            }
        }
    }


    /**
     * 显示视频缩略图
     */
    protected void showVideoThumbView(String localThumb, String thumbnailUrl, EMMessage message) {
        if (EaseAndroidLifecycleUtil.canLoadImage(this)) {
            String imgPath = (EaseFileUtil.isFile(localThumb) ? localThumb : thumbnailUrl);

            Glide.with(this)
                    .load(imgPath)
                    .apply(new RequestOptions().placeholder(R.drawable.ease_default_image).error(R.drawable.ease_default_image).override(160))
                    .into(imageView);
        }

        if (!EaseFileUtil.isFile(localThumb)) {
            if (message.status() == EMMessage.Status.FAIL) {
                if (EaseCommonUtils.isNetWorkConnected(activity)) {
                    EMClient.getInstance().chatManager().downloadThumbnail(message);
                }
            }
        }
    }

}