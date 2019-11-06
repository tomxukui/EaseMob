package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;

public abstract class EaseChatRowPresenter implements EaseChatRow.EaseChatRowActionCallback {

    private EaseChatRow chatRow;

    private BaseAdapter adapter;
    private EMMessage message;
    private int position;

    private EaseMessageListView.OnItemClickListener mOnItemClickListener;

    @Override
    public void onResendClick(final EMMessage message) {
        Context context = getContext();

        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.resend)
                    .setMessage(R.string.confirm_resend)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        message.setStatus(EMMessage.Status.CREATE);
                        handleSendMessage(message);
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onBubbleClick(EMMessage message) {
    }

    @Override
    public void onDetachedFromWindow() {
    }

    public EaseChatRow createChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        this.adapter = adapter;
        chatRow = onCreateChatRow(context, message, position, adapter);
        return chatRow;
    }

    public void setup(EMMessage msg, int position, EaseMessageListView.OnItemClickListener listener, EaseMessageListItemStyle itemStyle) {
        this.message = msg;
        this.position = position;
        mOnItemClickListener = listener;

        chatRow.setUpView(message, position, listener, this, itemStyle);

        handleMessage();
    }

    protected void handleSendMessage(EMMessage message) {
        getChatRow().updateView(message);

        if (message.status() == EMMessage.Status.INPROGRESS) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onMessageInProgress(message);
            }
        }
    }

    protected void handleReceiveMessage(EMMessage message) {
    }

    protected abstract EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter);

    protected EaseChatRow getChatRow() {
        return chatRow;
    }

    protected BaseAdapter getAdapter() {
        return adapter;
    }

    protected EMMessage getMessage() {
        return message;
    }

    protected int getPosition() {
        return position;
    }

    @Nullable
    protected Context getContext() {
        return chatRow == null ? null : chatRow.getContext();
    }

    private void handleMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            handleSendMessage(message);

        } else if (message.direct() == EMMessage.Direct.RECEIVE) {
            handleReceiveMessage(message);
        }
    }

}