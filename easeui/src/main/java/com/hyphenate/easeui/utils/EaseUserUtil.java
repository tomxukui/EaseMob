package com.hyphenate.easeui.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class EaseUserUtil {

    /**
     * 设置头像
     *
     * @param imageView          控件
     * @param avatar             头像
     * @param defaultAvatarResId 默认图
     */
    public static void setUserAvatar(ImageView imageView, String avatar, int defaultAvatarResId) {
        if (AndroidLifecycleUtil.canLoadImage(imageView)) {
            Glide.with(imageView)
                    .load(avatar)
                    .apply(new RequestOptions().placeholder(defaultAvatarResId).error(defaultAvatarResId).fallback(defaultAvatarResId))
                    .into(imageView);
        }
    }

}