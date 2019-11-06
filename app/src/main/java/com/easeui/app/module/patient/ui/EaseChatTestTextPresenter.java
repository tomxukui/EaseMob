package com.easeui.app.module.patient.ui;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatTextPresenter;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowText;

public class EaseChatTestTextPresenter extends EaseChatTextPresenter {

    int color;

    public EaseChatTestTextPresenter(int color) {
        this.color = color;
    }

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        EaseChatRowText rowText = (EaseChatRowText) super.onCreateChatRow(context, message, position, adapter);
        rowText.setBackgroundColor(color);
        return rowText;
    }

}