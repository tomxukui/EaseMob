package com.hyphenate.easeui.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.hyphenate.easeui.EaseUI;

public class EaseToastUtil {

    public static void show(String msg, int gravity) {
        Toast toast = Toast.makeText(EaseUI.getInstance().getContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    public static void show(int resId, int gravity) {
        Toast toast = Toast.makeText(EaseUI.getInstance().getContext(), resId, Toast.LENGTH_SHORT);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    public static void show(String msg) {
        show(msg, Gravity.CENTER);
    }

    public static void show(int resId) {
        show(resId, Gravity.CENTER);
    }

}