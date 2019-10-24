package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowCloseInquiry;

/**
 * 问诊结束
 */
public class EaseChatCloseInquiryPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowCloseInquiry(context, message, position, adapter);
    }

    @Override
    public void onResendClick(EMMessage message) {
    }

}