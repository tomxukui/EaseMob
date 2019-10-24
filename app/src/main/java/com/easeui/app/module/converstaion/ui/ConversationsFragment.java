package com.easeui.app.module.converstaion.ui;

import android.content.Intent;

import com.easeui.app.module.patient.ui.PatientInquiryActivity;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.conversation.ui.EaseConversationsFragment;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;

public class ConversationsFragment extends EaseConversationsFragment {

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Override
    protected void onItemClickListener(EMConversation conversation, int position) {
        EMConversation.EMConversationType type = conversation.getType();

        if (type == EMConversation.EMConversationType.Chat) {
            EMMessage message = conversation.getLastMessage();
            EaseUser fromUser = EaseMessageUtil.getFromUser(message);
            EaseUser toUser = EaseMessageUtil.getToUser(message);

            Intent intent = PatientInquiryActivity.buildIntent(getContext(), fromUser, null, toUser);
            startActivity(intent);

        } else if (type == EMConversation.EMConversationType.GroupChat) {
            EaseToastUtil.show("暂不支持");

        } else if (type == EMConversation.EMConversationType.ChatRoom) {
            EaseToastUtil.show("暂不支持");
        }
    }

}