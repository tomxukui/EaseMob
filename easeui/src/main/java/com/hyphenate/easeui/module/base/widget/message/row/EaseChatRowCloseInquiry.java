package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;

public class EaseChatRowCloseInquiry extends EaseChatRow {

    private TextView tv_content;

    public EaseChatRowCloseInquiry(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.ease_row_close_inquiry, this);
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