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

    public static DoctorInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        DoctorInquiryFragment fragment = new DoctorInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        bundle.putString(EXTRA_CHAT_MODE, chatMode);
        fragment.setArguments(bundle);
        return fragment;
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
