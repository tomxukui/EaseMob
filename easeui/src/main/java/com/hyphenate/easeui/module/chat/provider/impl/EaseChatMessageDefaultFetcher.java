package com.hyphenate.easeui.module.chat.provider.impl;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;
import com.hyphenate.easeui.module.chat.provider.EaseChatMessageFetcher;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.util.List;

public class EaseChatMessageDefaultFetcher implements EaseChatMessageFetcher {

    protected boolean mHaveMoreData = true;//是否有更多消息

    @Override
    public int getPageSize() {
        return 20;
    }

    @Override
    public boolean loadFirstMessages(EMConversation conversation, EaseMessageListView listView) {
        List<EMMessage> messages = conversation.getAllMessages();
        int count = (messages == null ? 0 : messages.size());

        if (count < conversation.getAllMsgCount() && count < getPageSize()) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }

            conversation.loadMoreMsgFromDB(msgId, getPageSize() - count);
        }

        refreshMessages(conversation, listView);
        scrollToLast(listView);

        return true;
    }

    @Override
    public void loadMoreEarlierMessages(EMConversation conversation, EaseMessageListView listView) {
        if (listView.getFirstVisiblePosition() == 0 && mHaveMoreData) {
            List<EMMessage> messages = conversation.getAllMessages();

            try {
                messages = conversation.loadMoreMsgFromDB(messages.size() == 0 ? "" : messages.get(0).getMsgId(), getPageSize());

            } catch (Exception e) {
                listView.setRefreshing(false);
                return;
            }

            if (messages != null && messages.size() > 0) {
                refreshMessages(conversation, listView);
                scrollTo(listView, messages.size() - 1);

                if (messages.size() != getPageSize()) {
                    mHaveMoreData = false;
                }

            } else {
                mHaveMoreData = false;
            }

        } else {
            EaseToastUtil.show(R.string.no_more_messages);
        }

        listView.setRefreshing(false);
    }

    @Override
    public void refreshMessages(EMConversation conversation, EaseMessageListView listView) {
        listView.setNewData(conversation.getAllMessages());
    }

    @Override
    public void scrollToLast(EaseMessageListView listView) {
        listView.scrollToLast();
    }

    @Override
    public void scrollTo(EaseMessageListView listView, int position) {
        listView.scrollTo(position);
    }

    @Override
    public void markAllMessagesAsRead(EMConversation conversation) {
        conversation.markAllMessagesAsRead();
    }

}