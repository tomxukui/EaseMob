package com.easeui.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.hyphenate.easeui.EaseUI;

public class Mapp extends Application {

    private boolean mEaseUIInited = false;//环信是否已经初始化

    @Override
    public void onCreate() {
        super.onCreate();
        initEaseUI();
    }

    /**
     * 初始化环信
     */
    private void initEaseUI() {
        if (mEaseUIInited) {
            return;
        }

        if (!isMainProcess()) {
            return;
        }

        EaseUI.getInstance().init(this, null);

        mEaseUIInited = true;
    }

    /**
     * 是否是主线程
     */
    private boolean isMainProcess() {
        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }

        return false;
    }

}