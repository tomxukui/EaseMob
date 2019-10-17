package com.easeui.app.module.doctor.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.easeui.app.R;
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;
import com.hyphenate.easeui.utils.EaseToastUtil;

public class DoctorInquiryFragment extends EaseInquiryFragment {

    private static final String EXTRA_TO_USER_MOBILE = "EXTRA_TO_USER_MOBILE";

    private String mToUserMobile;

    public static DoctorInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser, @EaseType.ChatMode String chatMode, String toUserMobile) {
        DoctorInquiryFragment fragment = new DoctorInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        bundle.putString(EXTRA_CHAT_MODE, chatMode);
        bundle.putString(EXTRA_TO_USER_MOBILE, toUserMobile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mToUserMobile = bundle.getString(EXTRA_TO_USER_MOBILE);
        }
    }

    @Override
    protected void setToolbar() {
        super.setToolbar();
        toolbar.setNavigationIcon(R.mipmap.ic_back_black);
        toolbar.setTitleTextBold(true);
        toolbar.setSubtitle(mToUserMobile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_doctor_inquiry, menu);

        menu.findItem(R.id.action_finishInquiry).setOnMenuItemClickListener(item -> {
            EaseToastUtil.show("结束问诊");
            return true;
        });
    }

}
