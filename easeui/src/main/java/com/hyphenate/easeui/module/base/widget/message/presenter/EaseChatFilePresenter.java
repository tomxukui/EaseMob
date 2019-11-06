package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.module.file.ui.EaseShowFileActivity;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowFile;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;

/**
 * 文件
 */
public class EaseChatFilePresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowFile(context, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMNormalFileMessageBody fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        File file = new File(fileMessageBody.getLocalUrl());

        if (file.exists()) {
            EaseCompat.openFile(file, (Activity) getContext());

        } else {
            Intent intent = EaseShowFileActivity.buildIntent(getContext(), message);
            getContext().startActivity(intent);
        }

        if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());

            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

}