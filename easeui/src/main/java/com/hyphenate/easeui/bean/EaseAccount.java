package com.hyphenate.easeui.bean;

public class EaseAccount extends EaseUser {

    protected String pwd;//登录密码

    public EaseAccount(String username, String pwd) {
        super(username);
        this.pwd = pwd;
    }

    public EaseAccount(String username, String pwd, String nick, String avatar) {
        super(username, nick, avatar);
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
