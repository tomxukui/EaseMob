package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.utils.EasePermissionUtil;
import com.hyphenate.easeui.utils.EaseSoftInputUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

public abstract class EaseBaseFragment extends Fragment implements EaseIBase {

    private ActionBar mActionBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResID(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view, savedInstanceState);
        initActionBar();
        setView(savedInstanceState);
    }

    protected abstract int getLayoutResID();

    protected void initData(Bundle savedInstanceState) {
    }

    protected void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (getActivity() != null && (getActivity() instanceof AppCompatActivity)) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
    }

    protected void initActionBar() {
        if (getActivity() != null && (getActivity() instanceof AppCompatActivity)) {
            mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);//不显示默认标题
            mActionBar.setDisplayHomeAsUpEnabled(canBack());//显示返回键
            setHasOptionsMenu(true);
        }
    }

    protected boolean canBack() {
        return false;
    }

    protected void initView(View view, Bundle savedInstanceState) {
    }

    protected void setView(Bundle savedInstanceState) {
    }

    @Override
    public void hideSoftKeyboard() {
        EaseSoftInputUtil.hide(getActivity());
    }

    protected void finish() {
        Activity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    @Override
    public void requestPermission(Action<List<String>> granted, @Nullable Action<List<String>> denied, String... permissions) {
        EasePermissionUtil.requestPermission(getContext(), AndPermission.with(this), granted, denied, permissions);
    }

    @Override
    public void requestPermission(Action<List<String>> granted, @Nullable Action<List<String>> denied, String[]... groups) {
        EasePermissionUtil.requestPermission(getContext(), AndPermission.with(this), granted, denied, groups);
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