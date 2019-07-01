package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by tom on 2016/9/28.
 */
public class EaseSoftInputUtil {

    /**
     * 显示软键盘
     */
    public static void show(Context context) {
        if (context == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     */
    public static void hide(Activity context) {
        if (context == null) {
            return;
        }

        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanger != null) {
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}