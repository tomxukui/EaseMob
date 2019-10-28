package com.hyphenate.easeui.module.base.widget.input;

import android.support.annotation.Nullable;
import android.view.View;

import java.io.Serializable;

public class EaseMenuItem implements Serializable {

    private int icon;
    private String name;
    @Nullable
    private View.OnClickListener onClickListener;

    public EaseMenuItem(int icon, String name, @Nullable View.OnClickListener onClickListener) {
        this.icon = icon;
        this.name = name;
        this.onClickListener = onClickListener;
    }

    public EaseMenuItem(String name, @Nullable View.OnClickListener onClickListener) {
        this.name = name;
        this.onClickListener = onClickListener;
    }

    public EaseMenuItem() {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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