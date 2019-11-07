package com.hyphenate.easeui.module.base.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class EaseDuration implements Serializable {

    private long startTimeStamp;
    private Long endTimeStamp;

    public EaseDuration(long startTimeStamp, @Nullable Long endTimeStamp) {
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }

    public EaseDuration() {
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp == null ? System.currentTimeMillis() : endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

}
