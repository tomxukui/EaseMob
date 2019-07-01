package com.hyphenate.easeui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.utils.EasePermissionUtil;
import com.hyphenate.easeui.utils.EaseSoftInputUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

public abstract class EaseBaseActivity extends AppCompatActivity implements EaseIBase {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();

            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        initData();
        setContentView(getLayoutResID());
        initView();
        initActionBar();
        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EaseUI.getInstance().getNotifier().reset();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract int getLayoutResID();

    protected void initData() {
    }

    protected void initActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示默认标题
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键
        }
    }

    protected void initView() {
    }

    protected void setView() {
    }

    protected Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }

        return mHandler;
    }

    @Override
    public void hideSoftKeyboard() {
        EaseSoftInputUtil.hide(this);
    }

    @Override
    public void requestPermission(Action<List<String>> granted, @Nullable Action<List<String>> denied, String... permissions) {
        EasePermissionUtil.requestPermission(this, AndPermission.with(this), granted, denied, permissions);
    }

    @Override
    public void requestPermission(Action<List<String>> granted, @Nullable Action<List<String>> denied, String[]... groups) {
        EasePermissionUtil.requestPermission(this, AndPermission.with(this), granted, denied, groups);
    }

    @Override
    public void requestPermission(Action<List<String>> granted, String... permissions) {
        requestPermission(granted, data -> EaseToastUtil.show("权限获取失败"), permissions);
    }

    @Override
    public void requestPermission(Action<List<String>> granted, String[]... groups) {
        requestPermission(granted, data -> EaseToastUtil.show("权限获取失败"), groups);
    }

    @Override
    public boolean hasPermissions(String... permissions) {
        return AndPermission.hasPermissions(this, permissions);
    }

    @Override
    public boolean hasPermissions(String[]... groups) {
        return AndPermission.hasPermissions(this, groups);
    }

}