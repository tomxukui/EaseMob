package com.hyphenate.easeui.module.inquiry.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.easeui.bean.EaseAccount;
import com.hyphenate.easeui.bean.EaseUser;
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.utils.EaseContactUtil;

public class EaseInquiryPatientActivity extends EaseInquiryActivity {

    @Override
    protected Fragment getMainFragment() {
        return EaseInquiryPatientFragment.newInstance(mToUsername, mChatMode);
    }

    public static Intent buildIntent(Context context, EaseAccount account, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        EaseContactUtil.getInstance().saveContact(account);
        EaseContactUtil.getInstance().saveContact(toUser);

        Intent intent = new Intent(context, EaseInquiryPatientActivity.class);
        intent.putExtra(EXTRA_USERNAME, account.getUsername());
        intent.putExtra(EXTRA_PWD, account.getPwd());
        intent.putExtra(EXTRA_TO_USERNAME, toUser.getUsername());
        intent.putExtra(EXTRA_CHAT_MODE, chatMode);
        return intent;
    }

}