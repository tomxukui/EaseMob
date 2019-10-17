package com.hyphenate.easeui.module.inquiry.delegate;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;

import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeui.module.inquiry.provider.EaseInquiryProvider;
import com.hyphenate.easeui.module.inquiry.ui.EaseInquiryFragment;

public class EaseInquiryDelegate {

    private EaseInquiryProvider mProvider;

    public EaseInquiryDelegate(@Nullable EaseInquiryProvider provider) {
        mProvider = provider;
    }

    /**
     * 初始化标题栏
     */
    public void onInitToolbar(EaseInquiryFragment fragment, EaseToolbar toolbar, EaseUser toUser) {
        //设置标题
        String title = toUser.getNickname();
        if (TextUtils.isEmpty(title)) {
            title = toUser.getUsername();
        }
        toolbar.setTitle(title);

        //设置返回点击事件
        toolbar.setOnBackClickListener(v -> fragment.onBackPressed());

        //触发自定义标题栏
        if (mProvider != null) {
            mProvider.onInitToolbar(toolbar);
        }
    }

    /**
     * 是否显示返回键
     */
    public boolean IsShowBackBtn() {
        return mProvider == null ? true : mProvider.isShowBackBtn();
    }

    /**
     * 创建标题栏的菜单
     */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mProvider != null) {
            mProvider.onCreateOptionsMenu(menu, inflater);
        }
    }


}
