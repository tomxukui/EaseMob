package com.easeui.app.module.module;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class InquiryMenuItem implements Serializable {

    private int resId;
    private String name;
    @Nullable
    private OnItemClickListener onItemClickListener;

    public InquiryMenuItem(int resId, String name, @Nullable OnItemClickListener listener) {
        this.resId = resId;
        this.name = name;
        this.onItemClickListener = listener;
    }

    public InquiryMenuItem() {
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(InquiryMenuItem itemModel, int position);

    }

}