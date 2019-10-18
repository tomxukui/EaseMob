package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowBigExpression;

/**
 * 表情
 */
public class EaseChatBigExpressionPresenter extends EaseChatTextPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowBigExpression(context, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
    }

}