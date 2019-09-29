package com.hyphenate.easeui.bean;

import com.hyphenate.chat.EMContact;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class EaseUser extends EMContact {

    protected String initialLetter;//简称
    protected String avatar;//头像

    public EaseUser(String username) {
        this.username = username;
    }

    public EaseUser(String username, String nick, String avatar) {
        this.username = username;
        this.nick = nick;
        this.avatar = avatar;
    }

    public String getInitialLetter() {
        if (initialLetter == null) {
            EaseCommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EaseUser)) {
            return false;
        }

        return getUsername().equals(((EaseUser) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }

}