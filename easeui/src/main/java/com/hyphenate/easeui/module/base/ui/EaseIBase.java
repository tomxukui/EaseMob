package com.hyphenate.easeui.module.base.ui;

import android.support.annotation.Nullable;

import com.yanzhenjie.permission.Action;

import java.util.List;

public interface EaseIBase {

    void hideSoftKeyboard();

    void requestPermission(Action<List<String>> granted, @Nullable Action<List<String>> denied, String... permissions);

    void requestPermission(Action<List<String>> granted, @Nullable Action<List<String>> denied, String[]... groups);

    void requestPermission(Action<List<String>> granted, String... permissions);

    void requestPermission(Action<List<String>> granted, String[]... groups);

    boolean hasPermissions(String... permissions);

    boolean hasPermissions(String[]... groups);

}