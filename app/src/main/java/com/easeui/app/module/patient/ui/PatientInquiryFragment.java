package com.easeui.app.module.patient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easeui.app.R;
import com.easeui.app.module.patient.adapter.PatientInquiryMenuListAdapter;
import com.easeui.app.module.patient.model.PatientInquiryToolbarMenuItem;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.widget.gridmenu.EaseGridMenu;
import com.hyphenate.easeui.module.base.widget.gridmenu.EaseGridMenuItem;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;
import com.hyphenate.easeui.module.chat.provider.EaseChatInputMenuProvider;
import com.hyphenate.easeui.module.inquiry.provider.EaseInquiryInputMenuProvider;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.util.ArrayList;
import java.util.List;

public class PatientInquiryFragment extends EaseInquiryFragment {

    //聊天状态
    private LinearLayout linear_status;
    private TextView tv_statusName;
    private TextView tv_round;

    //底部菜单
    private EaseGridMenu mFooterMenu;

    //标题栏菜单
    private ListPopupWindow mToolbarMenu;
    private PatientInquiryMenuListAdapter mMenuListAdapter;

    private LayoutInflater mInflater;

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
        mInflater = LayoutInflater.from(getContext());

        List<PatientInquiryToolbarMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new PatientInquiryToolbarMenuItem(R.mipmap.ic_menu_doctor, "医生介绍", (itemModel, position) -> {
            EaseToastUtil.show("医生介绍");
        }));
        menuItems.add(new PatientInquiryToolbarMenuItem(R.mipmap.ic_menu_appoint, "去挂号", (itemModel, position) -> {
            EaseToastUtil.show("去挂号");
        }));
        menuItems.add(new PatientInquiryToolbarMenuItem(R.mipmap.ic_menu_inquiry_info, "问诊信息", (itemModel, position) -> {
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
            if (mToolbarMenu != null && mToolbarMenu.isShowing()) {
                mToolbarMenu.dismiss();

            } else {
                showToolbarMenu();
            }
            return true;
        });
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        //添加聊天状态
        frame_main_custom.removeAllViews();
        View statusView = mInflater.inflate(R.layout.widget_patient_chat_status, frame_main_custom, false);
        linear_status = statusView.findViewById(R.id.linear_status);
        tv_statusName = statusView.findViewById(R.id.tv_statusName);
        tv_round = statusView.findViewById(R.id.tv_round);
        frame_main_custom.addView(statusView);

        //添加底部菜单
        frame_footer_custom.removeAllViews();
        mFooterMenu = new EaseGridMenu(getContext());
        mFooterMenu.setVisibility(View.GONE);
        frame_footer_custom.addView(mFooterMenu);
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        List<EaseGridMenuItem> footerMenuItems = new ArrayList<>();
        footerMenuItems.add(new EaseGridMenuItem("送心意", v -> EaseToastUtil.show("送心意")));
        footerMenuItems.add(new EaseGridMenuItem("再次咨询", v -> EaseToastUtil.show("再次咨询")));
        mFooterMenu.setData(footerMenuItems);
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
    private void showToolbarMenu() {
        if (mToolbarMenu == null) {
            mToolbarMenu = new ListPopupWindow(getContext());
            mToolbarMenu.setContentWidth(EaseDensityUtil.dp2px(138));
            mToolbarMenu.setBackgroundDrawable(EaseContextCompatUtil.getDrawable(R.drawable.ease_bg_menu));
            mToolbarMenu.setDropDownGravity(Gravity.RIGHT);
            mToolbarMenu.setHorizontalOffset(EaseDensityUtil.dp2px(-5));
            mToolbarMenu.setVerticalOffset(EaseDensityUtil.dp2px(4));
            mToolbarMenu.setModal(true);
            mToolbarMenu.setAdapter(mMenuListAdapter);
            mToolbarMenu.setAnimationStyle(R.style.ease_pop);
            mToolbarMenu.setOnItemClickListener((parent, view, position, id) -> {
                PatientInquiryToolbarMenuItem menuItem = mMenuListAdapter.getItem(position);

                PatientInquiryToolbarMenuItem.OnItemClickListener listener = menuItem.getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(menuItem, position);
                }
            });
            mToolbarMenu.setAnchorView(toolbar);
        }

        if (!mToolbarMenu.isShowing()) {
            mToolbarMenu.show();
        }
    }

    /**
     * 关闭标题栏菜单
     */
    private void dismissToolbarMenu() {
        if (mToolbarMenu != null && mToolbarMenu.isShowing()) {
            mToolbarMenu.dismiss();
        }
    }

    @Override
    protected EaseChatInputMenuProvider onSetInputMenu() {
        return new EaseInquiryInputMenuProvider() {

            @Override
            public List<EaseMenuItem> onSetMoreMenuItems() {
                return null;
            }

        };
    }

}