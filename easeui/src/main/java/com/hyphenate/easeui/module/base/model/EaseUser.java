package com.hyphenate.easeui.module.base.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class EaseUser implements Serializable {

    private String username;//主键
    private String nickname;//昵称
    private String avatar;//头像
    @Nullable
    private String memberId;//成员id

    public EaseUser(String username, String nickname, String avatar, @Nullable String memberId) {
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.memberId = memberId;
    }

    public EaseUser(String username, String nickname, String avatar) {
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public EaseUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Nullable
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(@Nullable String memberId) {
        this.memberId = memberId;
    }

}