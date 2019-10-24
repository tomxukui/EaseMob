package com.easeui.app.module.doctor.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.easeui.app.R;
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.inquiry.callback.EaseOnInquiryListener;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;

public class DoctorInquiryFragment extends EaseInquiryFragment {

    private static final String EXTRA_TO_USER_MOBILE = "EXTRA_TO_USER_MOBILE";

    private MenuItem startInquiryMenuItem;
    private MenuItem closeInquiryMenuItem;

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
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        setOnInquiryListener(new EaseOnInquiryListener() {

            @Override
            public void onStartInquiry() {
            }

            @Override
            public void onCloseInquiry() {
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_doctor_inquiry, menu);

        startInquiryMenuItem = menu.findItem(R.id.action_startInquiry);
        startInquiryMenuItem.setVisible(false);
        startInquiryMenuItem.setOnMenuItemClickListener(item -> {
            startInquiry();
            return true;
        });

        closeInquiryMenuItem = menu.findItem(R.id.action_closeInquiry);
        startInquiryMenuItem.setVisible(false);
        closeInquiryMenuItem.setOnMenuItemClickListener(item -> {
            closeInquiry();
            return true;
        });
    }

    @Override
    protected void setInquiryStarted() {
        super.setInquiryStarted();
        if (startInquiryMenuItem != null) {
            startInquiryMenuItem.setVisible(false);
        }
        if (closeInquiryMenuItem != null) {
            closeInquiryMenuItem.setVisible(true);
        }
    }

    @Override
    protected void setInquiryClosed() {
        super.setInquiryClosed();
        if (startInquiryMenuItem != null) {
            startInquiryMenuItem.setVisible(true);
        }
        if (closeInquiryMenuItem != null) {
            closeInquiryMenuItem.setVisible(false);
        }
    }

}