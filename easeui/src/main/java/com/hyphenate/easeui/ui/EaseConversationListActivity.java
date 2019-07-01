package com.hyphenate.easeui.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class EaseConversationListActivity extends EaseBaseChainActivity {

    @Override
    protected Fragment getMainFragment() {
        return EaseConversationListFragment.newInstance();
    }

    public static class Builder {

        private Intent mIntent;

        public Builder(Context context) {
            mIntent = new Intent(context, EaseConversationListActivity.class);
        }

        public Builder needLogin(String username, String pwd) {
            mIntent.putExtra(EXTRA_MY_USERNAME, username);
            mIntent.putExtra(EXTRA_MY_USERPWD, pwd);
            return this;
        }

        public Builder needLogout(boolean need) {
            mIntent.putExtra(EXTRA_NEED_LOGOUT, need);
            return this;
        }

        public Intent create() {
            return mIntent;
        }

    }

}