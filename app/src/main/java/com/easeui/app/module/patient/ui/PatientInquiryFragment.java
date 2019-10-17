package com.easeui.app.module.patient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;

import com.easeui.app.R;
import com.easeui.app.module.patient.adapter.PatientInquiryMenuListAdapter;
import com.easeui.app.module.patient.model.PatientInquiryMenuItem;
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryEndedMenuItem;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;
import com.hyphenate.easeui.utils.ContextCompatUtil;
import com.hyphenate.easeui.utils.DensityUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientInquiryFragment extends EaseInquiryFragment {

    //标题栏菜单
    private ListPopupWindow mPopupMenu;
    private PatientInquiryMenuListAdapter mMenuListAdapter;

    public static PatientInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        PatientInquiryFragment fragment = new PatientInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        bundle.putString(EXTRA_CHAT_MODE, chatMode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        List<PatientInquiryMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new PatientInquiryMenuItem(R.mipmap.ic_menu_doctor, "医生介绍", (itemModel, position) -> {
            EaseToastUtil.show("医生介绍");
        }));
        menuItems.add(new PatientInquiryMenuItem(R.mipmap.ic_menu_appoint, "去挂号", (itemModel, position) -> {
            EaseToastUtil.show("去挂号");
        }));
        menuItems.add(new PatientInquiryMenuItem(R.mipmap.ic_menu_inquiry_info, "问诊信息", (itemModel, position) -> {
            EaseToastUtil.show("问诊信息");
        }));
        mMenuListAdapter = new PatientInquiryMenuListAdapter(menuItems);
    }

    @Override
    protected void setToolbar() {
        super.setToolbar();
        toolbar.setBackgroundColor(Color.parseColor("#2693FF"));
        toolbar.setTitleTextColor(Color.WHITE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_patient_inquiry, menu);

        menu.findItem(R.id.action_more).setOnMenuItemClickListener(item -> {
            showPopupMenu();
            return true;
        });
    }

    /**
     * 显示标题栏菜单
     */
    private void showPopupMenu() {
        if (mPopupMenu == null) {
            mPopupMenu = new ListPopupWindow(getContext());
            mPopupMenu.setContentWidth(DensityUtil.dp2px(138));
            mPopupMenu.setBackgroundDrawable(ContextCompatUtil.getDrawable(com.hyphenate.easeui.R.drawable.ease_bg_menu));
            mPopupMenu.setDropDownGravity(Gravity.RIGHT);
            mPopupMenu.setHorizontalOffset(DensityUtil.dp2px(-5));
            mPopupMenu.setVerticalOffset(DensityUtil.dp2px(4));
            mPopupMenu.setAdapter(mMenuListAdapter);
            mPopupMenu.setOnItemClickListener((parent, view, position, id) -> {
                PatientInquiryMenuItem menuItem = mMenuListAdapter.getItem(position);

                PatientInquiryMenuItem.OnItemClickListener listener = menuItem.getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(menuItem, position);
                }
            });
            mPopupMenu.setAnchorView(toolbar);
        }

        if (!mPopupMenu.isShowing()) {
            mPopupMenu.show();
        }
    }

    @Nullable
    @Override
    protected List<EaseInquiryEndedMenuItem> getEndedMenuItems() {
        List<EaseInquiryEndedMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new EaseInquiryEndedMenuItem("送心意", (menuItem, position) -> EaseToastUtil.show("送心意")));
        menuItems.add(new EaseInquiryEndedMenuItem("再次咨询", (menuItem, position) -> EaseToastUtil.show("再次咨询")));
        return menuItems;
    }

}
