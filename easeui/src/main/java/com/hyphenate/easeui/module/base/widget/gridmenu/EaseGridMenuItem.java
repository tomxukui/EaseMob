package com.hyphenate.easeui.module.base.widget.gridmenu;

import android.support.annotation.Nullable;
import android.view.View;

import java.io.Serializable;

public class EaseGridMenuItem implements Serializable {

    private String name;
    @Nullable
    private View.OnClickListener onClickListener;

    public EaseGridMenuItem(String name, @Nullable View.OnClickListener onClickListener) {
        this.name = name;
        this.onClickListener = onClickListener;
    }

    public EaseGridMenuItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}