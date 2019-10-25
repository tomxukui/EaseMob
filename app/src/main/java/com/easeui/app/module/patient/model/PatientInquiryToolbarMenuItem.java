package com.easeui.app.module.patient.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class PatientInquiryToolbarMenuItem implements Serializable {

    private int resId;
    private String name;
    @Nullable
    private OnItemClickListener onItemClickListener;

    public PatientInquiryToolbarMenuItem(int resId, String name, @Nullable OnItemClickListener listener) {
        this.resId = resId;
        this.name = name;
        this.onItemClickListener = listener;
    }

    public PatientInquiryToolbarMenuItem() {
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

        void onItemClick(PatientInquiryToolbarMenuItem itemModel, int position);

    }

}