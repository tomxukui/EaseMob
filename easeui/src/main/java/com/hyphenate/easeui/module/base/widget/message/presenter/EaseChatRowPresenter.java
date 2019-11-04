package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;

public abstract class EaseChatRowPresenter implements EaseChatRow.EaseChatRowActionCallback {

    private EaseChatRow chatRow;

    private Context context;
    private BaseAdapter adapter;
    private EMMessage message;
    private int position;

    private EaseMessageListView.OnItemClickListener itemClickListener;

    @Override
    public void onResendClick(final EMMessage message) {
        new AlertDialog.Builder(getContext())
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

    @Override
    public void onBubbleClick(EMMessage message) {
    }

    @Override
    public void onDetachedFromWindow() {
    }

    public EaseChatRow createChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        this.context = cxt;
        this.adapter = adapter;
        chatRow = onCreateChatRow(cxt, message, position, adapter);
        return chatRow;
    }

    public void setup(EMMessage msg, int position, EaseMessageListView.OnItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        this.message = msg;
        this.position = position;
        this.itemClickListener = itemClickListener;

        chatRow.setUpView(message, position, itemClickListener, this, itemStyle);

        handleMessage();
    }

    protected void handleSendMessage(final EMMessage message) {
        getChatRow().updateView(message);

        if (message.status() == EMMessage.Status.INPROGRESS) {
            if (this.itemClickListener != null) {
                this.itemClickListener.onMessageInProgress(message);
            }
        }
    }

    protected void handleReceiveMessage(EMMessage message) {
    }

    protected abstract EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter);

    protected EaseChatRow getChatRow() {
        return chatRow;
    }

    protected Context getContext() {
        return context;
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

    private void handleMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            handleSendMessage(message);

        } else if (message.direct() == EMMessage.Direct.RECEIVE) {
            handleReceiveMessage(message);
        }
    }

}