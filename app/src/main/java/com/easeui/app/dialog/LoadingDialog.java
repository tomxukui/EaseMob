package com.easeui.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.easeui.app.R;

/**
 * Created by xukui on 2018/3/27.
 */
public class LoadingDialog extends Dialog {

    private TextView tv_message;

    private String mMessage;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.MaskDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        initWindow();
        initView();
        setView();
    }

    private void initWindow() {
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
    }

    private void initView() {
        tv_message = findViewById(R.id.tv_message);
    }

    private void setView() {
        if (TextUtils.isEmpty(mMessage)) {
            tv_message.setVisibility(View.GONE);
            tv_message.setText("");

        } else {
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText(mMessage);
        }
    }

    private void setMessage(String message) {
        mMessage = message;
    }

    public void show(String message, boolean isCancel, boolean isCanceledOnTouchOutside, @Nullable OnCancelListener onCancelListener, @Nullable OnDismissListener onDismissListener) {
        setMessage(message);
        setCancelable(isCancel);
        setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        setOnCancelListener(onCancelListener);
        setOnDismissListener(onDismissListener);

        if (!isShowing()) {
            show();
        }

        setView();
    }

    public static class Builder {

        private LoadingDialog mDialog;

        public Builder(Context context) {
            mDialog = new LoadingDialog(context);
        }

        public Builder setMessage(String message) {
            mDialog.setMessage(message);
            return this;
        }

        public Builder setCancelable(boolean isCancel) {
            mDialog.setCancelable(isCancel);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean isCancel) {
            mDialog.setCanceledOnTouchOutside(isCancel);
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener listener) {
            mDialog.setOnDismissListener(listener);
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            mDialog.setOnCancelListener(listener);
            return this;
        }

        public LoadingDialog create() {
            return mDialog;
        }

    }

}