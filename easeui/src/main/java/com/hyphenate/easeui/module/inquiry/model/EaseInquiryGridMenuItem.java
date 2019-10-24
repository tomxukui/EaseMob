package com.hyphenate.easeui.module.inquiry.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class EaseInquiryGridMenuItem implements Serializable {

    private String name;
    @Nullable
    private OnItemClickListener onItemClickListener;

    public EaseInquiryGridMenuItem(String name, @Nullable OnItemClickListener listener) {
        this.name = name;
        this.onItemClickListener = listener;
    }

    public EaseInquiryGridMenuItem() {
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

        void onItemClick(EaseInquiryGridMenuItem menuItem, int position);

    }

}
