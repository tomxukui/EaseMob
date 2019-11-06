package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
    protected View onInflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.ease_row_close_inquiry, this, false);
    }

    @Override
    protected void onFindViewById(View view) {
        tv_content = view.findViewById(R.id.tv_content);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody body = (EMTextMessageBody) mMessage.getBody();
        tv_content.setText(body.getMessage());
    }

}