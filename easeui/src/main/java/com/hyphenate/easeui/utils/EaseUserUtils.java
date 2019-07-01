package com.hyphenate.easeui.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;

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

    /**
     * 设置头像
     */
    public static void setUserAvatar(Context context, @Nullable EaseUser user, ImageView imageView) {
        String avatar = (user == null ? null : user.getAvatar());

        if (TextUtils.isEmpty(avatar)) {
            Glide.with(context)
                    .load(R.mipmap.ease_ic_portrait)
                    .into(imageView);

        } else {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());

                Glide.with(context)
                        .load(avatarResId)
                        .into(imageView);

            } catch (Exception e) {
                Glide.with(context)
                        .load(user.getAvatar())
                        .apply(RequestOptions.placeholderOf(R.mipmap.ease_ic_portrait))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(imageView);
            }
        }
    }

    /**
     * 设置头像
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        setUserAvatar(context, getUserInfo(username), imageView);
    }

    /**
     * 设置昵称
     */
    public static void setUserNick(String username, TextView textView) {
        EaseUser user = getUserInfo(username);
        String nickname = (user == null ? null : user.getNickname());

        textView.setText(TextUtils.isEmpty(nickname) ? username : nickname);
    }

}