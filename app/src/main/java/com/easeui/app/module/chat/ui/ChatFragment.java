package com.easeui.app.module.chat.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.chat.ui.EaseChatFragment;

public class ChatFragment extends EaseChatFragment {

    @Override
    protected void setToolbar() {
        super.setToolbar();
        toolbar.setBackgroundColor(Color.parseColor("#2693FF"));
        toolbar.setTitleTextColor(Color.WHITE);
    }

    public static ChatFragment newInstance(EaseUser fromUser, EaseUser toUser) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        fragment.setArguments(bundle);
        return fragment;
    }

}