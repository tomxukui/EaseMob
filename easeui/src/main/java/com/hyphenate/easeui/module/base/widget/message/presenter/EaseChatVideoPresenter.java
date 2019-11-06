package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.module.video.ui.EaseShowVideoActivity;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowVideo;

/**
 * 视频
 */
public class EaseChatVideoPresenter extends EaseChatFilePresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowVideo(context, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();

        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {

        } else {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                EMClient.getInstance().chatManager().downloadThumbnail(message);
                return;
            }
        }

        Intent intent = new Intent(getContext(), EaseShowVideoActivity.class);
        intent.putExtra("msg", message);

        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getContext().startActivity(intent);
    }

}