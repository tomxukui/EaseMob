package com.easeui.app.module.doctor.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easeui.app.R;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.widget.input.EaseInputControlButton;
import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;
import com.hyphenate.easeui.module.inquiry.provider.EaseInquiryInputMenuProvider;
import com.hyphenate.easeui.module.inquiry.callback.EaseOnInquiryListener;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;

import java.util.ArrayList;
import java.util.List;

public class DoctorInquiryFragment extends EaseInquiryFragment {

    private static final String EXTRA_TO_USER_MOBILE = "EXTRA_TO_USER_MOBILE";

    //聊天状态
    private LinearLayout linear_status;
    private TextView tv_statusName;
    private TextView tv_round;

    private TextView tv_startVisit;

    private MenuItem inquiryMenuItem;

    private String mToUserMobile;
    private boolean mIsVisit;

    private LayoutInflater mInflater;

    public static DoctorInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser, String toUserMobile) {
        DoctorInquiryFragment fragment = new DoctorInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        bundle.putString(EXTRA_TO_USER_MOBILE, toUserMobile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mInflater = LayoutInflater.from(getContext());

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
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        //添加聊天状态
        frame_main_custom.removeAllViews();
        View statusView = mInflater.inflate(R.layout.widget_doctor_chat_status, frame_main_custom, false);
        linear_status = statusView.findViewById(R.id.linear_status);
        tv_statusName = statusView.findViewById(R.id.tv_statusName);
        tv_round = statusView.findViewById(R.id.tv_round);
        frame_main_custom.addView(statusView);

        //添加开始随访
        tv_startVisit = new TextView(getContext());
        tv_startVisit.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv_startVisit.setMinHeight(EaseDensityUtil.dp2px(49));
        tv_startVisit.setGravity(Gravity.CENTER);
        tv_startVisit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv_startVisit.setTextColor(Color.parseColor("#00B8C1"));
        tv_startVisit.setText("开启随访");
        tv_startVisit.setVisibility(View.GONE);
        frame_footer_custom.addView(tv_startVisit);
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

            @Override
            public void onStartVisit() {
            }

            @Override
            public void onCloseVisit() {
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startInquiry(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_doctor_inquiry, menu);

        inquiryMenuItem = menu.findItem(R.id.action_inquiry);
        setToolbarMenuItem();
    }

    @Override
    protected void setStartInquiryView() {
        super.setStartInquiryView();
//        tv_startVisit.setVisibility(View.GONE);
//        setMenuItem();
    }

    @Override
    protected void setCloseInquiryView() {
        super.setCloseInquiryView();
        mIsVisit = true;

//        tv_startVisit.setVisibility(View.VISIBLE);
//        setMenuItem();
    }

    private void setToolbarMenuItem() {
        if (inquiryMenuItem == null) {
            return;
        }

        if (mIsVisit) {//随访状态
            if (mIsClosed) {//随访关闭
                inquiryMenuItem.setVisible(false);

            } else {
                inquiryMenuItem.setVisible(true);
                inquiryMenuItem.setTitle("结束随访");
                inquiryMenuItem.setOnMenuItemClickListener(item -> {
                    EaseToastUtil.show("结束随访");
                    return true;
                });
            }

        } else {//问诊状态
            if (mIsClosed) {//问诊关闭
                inquiryMenuItem.setVisible(false);

            } else {
                inquiryMenuItem.setVisible(true);
                inquiryMenuItem.setTitle("结束问诊");
                inquiryMenuItem.setOnMenuItemClickListener(item -> {
                    EaseToastUtil.show("结束问诊");
                    return true;
                });
            }
        }
    }

    @Override
    protected EaseInquiryInputMenuProvider onSetInputMenu() {
        return new EaseInquiryInputMenuProvider() {

            EaseInputControlButton quotationsButton = null;

            @Override
            public void onToggleVoice(boolean show) {
                if (quotationsButton != null) {
                    quotationsButton.setSelected(false);
                }
            }

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void onEditTextClicked() {
                if (quotationsButton != null) {
                    quotationsButton.setSelected(false);
                }
            }

            @Override
            public void onExtendInputMenu(EaseInputMenu inputMenu) {
                View quotationsPanel = new View(getContext());
                quotationsPanel.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, EaseDensityUtil.dp2px(200)));
                quotationsPanel.setBackgroundColor(Color.BLUE);

                quotationsButton = new EaseInputControlButton(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(EaseDensityUtil.dp2px(10), 0, 0, 0);
                quotationsButton.setLayoutParams(layoutParams);
                quotationsButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                quotationsButton.setTextColor(Color.WHITE);
                quotationsButton.setText("常用语");
                quotationsButton.setPadding(EaseDensityUtil.dp2px(9), EaseDensityUtil.dp2px(5), EaseDensityUtil.dp2px(9), EaseDensityUtil.dp2px(5));
                quotationsButton.setGravity(Gravity.CENTER);
                quotationsButton.setMinWidth(EaseDensityUtil.dp2px(42));
                quotationsButton.setMinHeight(EaseDensityUtil.dp2px(20));
                quotationsButton.setBackgroundResource(R.drawable.btn_quotations);
                quotationsButton.setInputEnable(false);
                quotationsButton.setPanel(quotationsPanel);

                inputMenu.addView(quotationsButton, quotationsPanel, 0, layoutParams);
            }

            @Override
            public List<EaseMenuItem> onSetMoreMenuItems() {
                List<EaseMenuItem> menuItems = new ArrayList<>();
                menuItems.add(createAlbumMenuItem());
                menuItems.add(createCameraMenuItem());
                menuItems.add(new EaseMenuItem(R.mipmap.ic_write_case, "写病例", v -> EaseToastUtil.show("写病例")));
                return menuItems;
            }

        };
    }

}