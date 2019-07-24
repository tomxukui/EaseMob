package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFinishConversation;

public class EaseChatFinishConversationPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowFinishConversation(context, message, position, adapter);
    }

    @Override
    public void onResendClick(EMMessage message) {
    }

}