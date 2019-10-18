package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;

public class EaseChatRowFinishInquiry extends EaseChatRow {

    private TextView tv_content;

    public EaseChatRowFinishInquiry(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.ease_row_finish_inquiry, this);
    }

    @Override
    protected void onFindViewById() {
        tv_content = findViewById(R.id.tv_content);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        tv_content.setText(body.getMessage());
    }

}