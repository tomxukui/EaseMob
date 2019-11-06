package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.util.TextFormater;

import java.io.File;

public class EaseChatRowFile extends EaseChatRow {

    protected TextView fileNameView;
    protected TextView fileSizeView;
    protected TextView fileStateView;

    private EMNormalFileMessageBody fileMessageBody;

    public EaseChatRowFile(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView(LayoutInflater inflater) {
        inflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_file : R.layout.ease_row_sent_file, this);
    }

    @Override
    protected void onFindViewById() {
        fileNameView = findViewById(R.id.tv_file_name);
        fileSizeView = findViewById(R.id.tv_file_size);
        fileStateView = findViewById(R.id.tv_file_state);
        tv_percentage = findViewById(R.id.tv_percentage);
    }

    @Override
    protected void onSetUpView() {
        fileMessageBody = (EMNormalFileMessageBody) mMessage.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        fileSizeView.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            File file = new File(filePath);

            if (file.exists()) {
                fileStateView.setText(R.string.Have_downloaded);

            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }
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

    private void onMessageCreate() {
        bar_progress.setVisibility(View.VISIBLE);

        if (tv_percentage != null) {
            tv_percentage.setVisibility(View.INVISIBLE);
        }

        if (iv_status != null) {
            iv_status.setVisibility(View.INVISIBLE);
        }
    }

    private void onMessageSuccess() {
        bar_progress.setVisibility(View.INVISIBLE);

        if (tv_percentage != null) {
            tv_percentage.setVisibility(View.INVISIBLE);
        }

        if (iv_status != null) {
            iv_status.setVisibility(View.INVISIBLE);
        }
    }

    private void onMessageError() {
        bar_progress.setVisibility(View.INVISIBLE);

        if (tv_percentage != null) {
            tv_percentage.setVisibility(View.INVISIBLE);
        }

        if (iv_status != null) {
            iv_status.setVisibility(View.VISIBLE);
        }
    }

    private void onMessageInProgress() {
        bar_progress.setVisibility(View.VISIBLE);

        if (tv_percentage != null) {
            tv_percentage.setVisibility(View.VISIBLE);
            tv_percentage.setText(mMessage.progress() + "%");
        }

        if (iv_status != null) {
            iv_status.setVisibility(View.INVISIBLE);
        }
    }

}