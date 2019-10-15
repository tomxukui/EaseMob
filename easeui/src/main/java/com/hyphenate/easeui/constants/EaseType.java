package com.hyphenate.easeui.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EaseType {

    public static final String BROWSE = "BROWSE";//浏览模式
    public static final String CHAT = "CHAT";//聊天模式

    @StringDef({BROWSE, CHAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatMode {
    }

}