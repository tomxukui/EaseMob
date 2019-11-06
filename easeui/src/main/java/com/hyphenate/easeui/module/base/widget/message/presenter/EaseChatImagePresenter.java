package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.image.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowImage;

/**
 * 图片
 */
public class EaseChatImagePresenter extends EaseChatFilePresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowImage(context, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(final EMMessage message) {
        super.handleReceiveMessage(message);
        getChatRow().updateView(message);

        message.setMessageStatusCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                getChatRow().updateView(message);
            }

            @Override
            public void onError(int code, String error) {
                getChatRow().updateView(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                getChatRow().updateView(message);
            }

        });
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();

        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                getChatRow().updateView(message);
                EMClient.getInstance().chatManager().downloadThumbnail(message);
            }

        } else {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                EMClient.getInstance().chatManager().downloadThumbnail(message);
                getChatRow().updateView(message);
                return;
            }
        }

        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Context context = getContext();

        if (context != null) {
            Intent intent = EaseShowBigImageActivity.buildIntent(context, message.getMsgId(), imgBody.getLocalUrl(), null);
            context.startActivity(intent);
        }
    }

}