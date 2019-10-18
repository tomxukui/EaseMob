package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowFinishInquiry;

/**
 * 问诊结束
 */
public class EaseChatFinishInquiryPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowFinishInquiry(context, message, position, adapter);
    }

    @Override
    public void onResendClick(EMMessage message) {
    }

}