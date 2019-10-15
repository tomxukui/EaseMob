package com.hyphenate.easeui.utils;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.bean.EaseUser;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * 通过username获取user
     */
    public static EaseUser getUserInfo(String username) {
        return (userProvider == null ? null : userProvider.getUser(username));
    }

}