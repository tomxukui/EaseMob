package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;

public class EaseChatRowLocation extends EaseChatRow {

    private TextView locationView;
    private EMLocationMessageBody locBody;

    public EaseChatRowLocation(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView(LayoutInflater inflater) {
        inflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_location : R.layout.ease_row_sent_location, this);
    }

    @Override
    protected void onFindViewById() {
        locationView = findViewById(R.id.tv_location);
    }

    @Override
    protected void onSetUpView() {
        locBody = (EMLocationMessageBody) mMessage.getBody();
        locationView.setText(locBody.getAddress());
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

        }
    }

    private void onMessageCreate() {
        bar_progress.setVisibility(View.VISIBLE);
        iv_status.setVisibility(View.GONE);
    }

    private void onMessageSuccess() {
        bar_progress.setVisibility(View.GONE);
        iv_status.setVisibility(View.GONE);
    }

    private void onMessageError() {
        bar_progress.setVisibility(View.GONE);
        iv_status.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
        bar_progress.setVisibility(View.VISIBLE);
        iv_status.setVisibility(View.GONE);
    }

}