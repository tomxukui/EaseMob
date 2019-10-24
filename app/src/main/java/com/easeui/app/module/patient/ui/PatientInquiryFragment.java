package com.easeui.app.module.patient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.easeui.app.R;
import com.easeui.app.module.patient.adapter.PatientInquiryMenuListAdapter;
import com.easeui.app.module.patient.model.PatientInquiryMenuItem;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.inquiry.callback.EaseOnInquiryListener;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryGridMenuItem;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;
import com.hyphenate.easeui.module.inquiry.widget.EaseInquiryGridMenu;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientInquiryFragment extends EaseInquiryFragment {

    //底部菜单
    private EaseInquiryGridMenu mFooterMenu;

    //标题栏菜单
    private ListPopupWindow mPopupMenu;
    private PatientInquiryMenuListAdapter mMenuListAdapter;

    public static PatientInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser) {
        PatientInquiryFragment fragment = new PatientInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
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

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        frame_footer_custom.removeAllViews();
        mFooterMenu = new EaseInquiryGridMenu(getContext());
        mFooterMenu.setVisibility(View.GONE);
        frame_footer_custom.addView(mFooterMenu);
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        List<EaseInquiryGridMenuItem> footerMenuItems = new ArrayList<>();
        footerMenuItems.add(new EaseInquiryGridMenuItem("送心意", (menuItem, position) -> EaseToastUtil.show("送心意")));
        footerMenuItems.add(new EaseInquiryGridMenuItem("再次咨询", (menuItem, position) -> EaseToastUtil.show("再次咨询")));
        mFooterMenu.setData(footerMenuItems);

        setOnInquiryListener(new EaseOnInquiryListener() {

            @Override
            public void onStartInquiry() {
                EaseToastUtil.show("问诊开始");
            }

            @Override
            public void onCloseInquiry() {
                EaseToastUtil.show("问诊已结束");
            }

        });
    }

    @Override
    protected void setStartInquiryView() {
        super.setStartInquiryView();
        mFooterMenu.setVisibility(View.GONE);
    }

    @Override
    protected void setCloseInquiryView() {
        super.setCloseInquiryView();
        mFooterMenu.setVisibility(View.VISIBLE);
    }

    /**
     * 显示标题栏菜单
     */
    private void showPopupMenu() {
        if (mPopupMenu == null) {
            mPopupMenu = new ListPopupWindow(getContext());
            mPopupMenu.setContentWidth(EaseDensityUtil.dp2px(138));
            mPopupMenu.setBackgroundDrawable(EaseContextCompatUtil.getDrawable(com.hyphenate.easeui.R.drawable.ease_bg_menu));
            mPopupMenu.setDropDownGravity(Gravity.RIGHT);
            mPopupMenu.setHorizontalOffset(EaseDensityUtil.dp2px(-5));
            mPopupMenu.setVerticalOffset(EaseDensityUtil.dp2px(4));
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

}