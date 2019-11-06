package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowLocation;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 定位
 */
public class EaseChatLocationPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowLocation(context, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());

            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
//        getContext().startActivity(EaseBaiduMapActivity.buildIntent(getContext(), locBody.getLatitude(), locBody.getLongitude()));
    }

}