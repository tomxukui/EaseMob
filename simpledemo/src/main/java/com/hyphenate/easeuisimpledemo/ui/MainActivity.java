package com.hyphenate.easeuisimpledemo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.bean.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.utils.EaseContactUtil;
import com.hyphenate.easeuisimpledemo.R;

public class MainActivity extends EaseBaseActivity {

    private Button[] mTabs;
    private EaseConversationListFragment conversationListFragment;
    private EaseContactListFragment contactListFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;

    private int index;
    private int currentTabIndex;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        EaseContactUtil.getInstance().saveContactList(createUsers());
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mTabs = new Button[3];
        mTabs[0] = findViewById(R.id.btn_conversation);
        mTabs[1] = findViewById(R.id.btn_address_list);
        mTabs[2] = findViewById(R.id.btn_setting);
        mTabs[0].setSelected(true);

        conversationListFragment = new EaseConversationListFragment();
        contactListFragment = EaseContactListFragment.newInstance();
        settingFragment = new SettingsFragment();
        contactListFragment.setContactListItemClickListener(user -> {
            Intent intent = new EaseChatActivity.Builder(MainActivity.this)
                    .setChatType(EaseConstant.CHATTYPE_SINGLE)
                    .setToUser(user.getUsername())
                    .create();

            startActivity(intent);
        });
        fragments = new Fragment[]{conversationListFragment, contactListFragment, settingFragment};
        // add and show first fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
                .commit();
    }

    /**
     * onTabClicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_address_list:
                index = 1;
                break;
            case R.id.btn_setting:
                index = 2;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab as selected.
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    private List<EaseUser> createUsers() {
        List<EaseUser> users = new ArrayList<>();

        EaseUser tomUser = new EaseUser("tom");
        tomUser.setNickname("汤姆1");
        users.add(tomUser);

        EaseUser jackUser = new EaseUser("jack");
        jackUser.setNickname("杰克1");
        jackUser.setAvatar("http://b-ssl.duitang.com/uploads/item/201501/28/20150128164857_ijBZi.thumb.700_0.jpeg");
        users.add(jackUser);

        for (int i = 0; i < 10; i++) {
            EaseUser user = new EaseUser("us_" + i);
            user.setNickname("nk_" + i);
            user.setAvatar(i % 2 == 0 ? "http://img3.imgtn.bdimg.com/it/u=2229346952,2661940409&fm=26&gp=0.jpg" : "http://img5.imgtn.bdimg.com/it/u=4176990357,3620816754&fm=26&gp=0.jpg");
            users.add(user);
        }

        return users;
    }

}