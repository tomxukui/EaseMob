package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseSmileUtil;

import java.util.List;

public class EaseChatRowText extends EaseChatRow {

    private TextView tv_bubble_text;

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        tv_bubble_text = findViewById(R.id.tv_bubble_text);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtil.getSmiledText(getContext(), txtBody.getMessage());
        tv_bubble_text.setText(span, BufferType.SPANNABLE);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        switch (msg.status()) {

            case CREATE:
                onMessageCreate();
                break;

            case SUCCESS:
                onMessageSuccess();
                break;

            case FAIL:
                onMessageError();
                break;

            case INPROGRESS:
                onMessageInProgress();
                break;

            default:
                break;

        }
    }

    public void onAckUserUpdate(final int count) {
        if (tv_ack != null) {
            tv_ack.post(() -> {
                tv_ack.setVisibility(VISIBLE);
                tv_ack.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
            });
        }
    }

    private void onMessageCreate() {
        progressBar.setVisibility(View.VISIBLE);
        iv_status.setVisibility(View.GONE);
    }

    private void onMessageSuccess() {
        progressBar.setVisibility(View.GONE);
        iv_status.setVisibility(View.GONE);

        if (EaseDingMessageHelper.get().isDingMessage(message) && tv_ack != null) {
            tv_ack.setVisibility(VISIBLE);
            List<String> userList = EaseDingMessageHelper.get().getAckUsers(message);
            int count = userList == null ? 0 : userList.size();
            tv_ack.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
        }

        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    private void onMessageError() {
        progressBar.setVisibility(View.GONE);
        iv_status.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        iv_status.setVisibility(View.GONE);
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener = list -> onAckUserUpdate(list.size());

}