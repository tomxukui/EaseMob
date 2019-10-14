package com.hyphenate.easeui.module.inquiry.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryEndedMenuItem;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryMenuItem;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.util.ArrayList;
import java.util.List;

public class EaseInquiryPatientFragment extends EaseInquiryFragment {

    public static EaseInquiryPatientFragment newInstance(String toUsername) {
        EaseInquiryPatientFragment fragment = new EaseInquiryPatientFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_TO_USERNAME, toUsername);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    protected List<EaseInquiryMenuItem> getMenuItems() {
        List<EaseInquiryMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new EaseInquiryMenuItem(R.mipmap.ease_menu_doctor, "医生介绍", (itemModel, position) -> {
            EaseToastUtil.show("医生介绍");
        }));
        menuItems.add(new EaseInquiryMenuItem(R.mipmap.ease_menu_appoint, "去挂号", (itemModel, position) -> {
            EaseToastUtil.show("去挂号");
        }));
        menuItems.add(new EaseInquiryMenuItem(R.mipmap.ease_menu_inquiry_info, "问诊信息", (itemModel, position) -> {
            EaseToastUtil.show("问诊信息");
        }));
        return menuItems;
    }

    @Nullable
    @Override
    protected List<EaseInquiryEndedMenuItem> getEndedMenuItems() {
        List<EaseInquiryEndedMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new EaseInquiryEndedMenuItem("送心意", (menuItem, position) -> EaseToastUtil.show("送心意")));
        menuItems.add(new EaseInquiryEndedMenuItem("再次咨询", (menuItem, position) -> EaseToastUtil.show("再次咨询")));

        menuItems.add(new EaseInquiryEndedMenuItem("送心意1", (menuItem, position) -> EaseToastUtil.show("送心意")));
        menuItems.add(new EaseInquiryEndedMenuItem("再次咨询1", (menuItem, position) -> EaseToastUtil.show("再次咨询")));
        return menuItems;
    }

}
