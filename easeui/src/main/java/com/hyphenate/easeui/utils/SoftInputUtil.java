package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by tom on 2016/9/28.
 */
public class SoftInputUtil {

    private static final long SHOW_SPACE = 200L;

    /**
     * 显示软键盘
     */
    public static void show(View view) {
        if (view != null) {
            Context context = view.getContext();

            if (context != null) {
                view.requestFocus();
                view.postDelayed(() -> {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                }, SHOW_SPACE);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hide(View view) {
        if (view != null) {
            Context context = view.getContext();

            if (context != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hide(Activity activity) {
        if (activity != null) {
            hide(activity.getWindow().getDecorView());
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hide(Fragment fragment) {
        if (fragment != null) {
            hide(fragment.getActivity());
        }
    }

}