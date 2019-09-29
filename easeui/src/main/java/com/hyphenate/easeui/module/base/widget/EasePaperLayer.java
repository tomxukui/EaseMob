package com.hyphenate.easeui.module.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.easeui.R;

public class EasePaperLayer extends FrameLayout {

    private FrameLayout frame_mask;
    private ProgressBar bar_progress;
    private LinearLayout linear_msg;
    private AppCompatImageView iv_icon;
    private TextView tv_msg;

    private boolean mIsLoading;
    private int mEmptyIcon;
    private String mEmptyMsg;
    private int mErrorIcon;

    private OnRefreshListener mOnRefreshListener;

    public EasePaperLayer(@NonNull Context context) {
        this(context, null);
    }

    public EasePaperLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasePaperLayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView(context);
        setView();
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        mEmptyIcon = R.mipmap.ease_ic_empty;
        mErrorIcon = R.mipmap.ease_ic_error;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EasePaperLayer, defStyleAttr, 0);

            mEmptyIcon = ta.getResourceId(R.styleable.EasePaperLayer_epl_empty_icon, mEmptyIcon);
            mEmptyMsg = ta.getString(R.styleable.EasePaperLayer_epl_empty_msg);
            mErrorIcon = ta.getResourceId(R.styleable.EasePaperLayer_epl_error_icon, mErrorIcon);

            ta.recycle();
        }

        if (TextUtils.isEmpty(mEmptyMsg)) {
            mEmptyMsg = "暂无信息";
        }
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.ease_view_paper_layer, this);

        frame_mask = view.findViewById(R.id.frame_mask);
        bar_progress = view.findViewById(R.id.bar_progress);
        linear_msg = view.findViewById(R.id.linear_msg);
        iv_icon = view.findViewById(R.id.iv_icon);
        tv_msg = view.findViewById(R.id.tv_msg);
    }

    private void setView() {
        linear_msg.setOnClickListener(v -> autoRefresh());
    }

    public void autoRefresh() {
        if (mIsLoading) {
            return;
        }

        mIsLoading = true;

        frame_mask.bringToFront();
        setLoadingView();

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(this);
        }
    }

    private void setLoadingView() {
        frame_mask.setVisibility(View.VISIBLE);
        bar_progress.setVisibility(View.VISIBLE);
        linear_msg.setVisibility(View.GONE);
    }

    private void setSuccessView() {
        bar_progress.setVisibility(View.GONE);
        frame_mask.setVisibility(View.GONE);
        linear_msg.setVisibility(View.GONE);
    }

    private void setEmptyView() {
        bar_progress.setVisibility(View.GONE);
        frame_mask.setVisibility(View.VISIBLE);
        linear_msg.setVisibility(View.VISIBLE);

        iv_icon.setImageResource(mEmptyIcon);
        tv_msg.setText(mEmptyMsg);
    }

    private void setErrorView(String msg) {
        frame_mask.setVisibility(View.VISIBLE);
        bar_progress.setVisibility(View.GONE);
        linear_msg.setVisibility(View.VISIBLE);

        iv_icon.setImageResource(mErrorIcon);
        tv_msg.setText(msg);
    }

    public void finishSuccess() {
        finishSuccess(false);
    }

    public void finishSuccess(boolean isEmpty) {
        if (!mIsLoading) {
            return;
        }

        if (isEmpty) {
            setEmptyView();

        } else {
            setSuccessView();
        }

        mIsLoading = false;
    }

    public void finishFailure(String msg) {
        if (!mIsLoading) {
            return;
        }

        setErrorView(msg);

        mIsLoading = false;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public interface OnRefreshListener {

        void onRefresh(EasePaperLayer view);

    }

}
