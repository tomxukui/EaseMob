package com.hyphenate.easeui.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.hyphenate.easeui.EaseUI;

/**
 * Created by xukui on 2017/8/9.
 */

public class ContextCompatUtil {

    public static final int getColor(@ColorRes int id) {
        return ContextCompat.getColor(EaseUI.getInstance().getContext(), id);
    }

    public static final Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(EaseUI.getInstance().getContext(), id);
    }

}