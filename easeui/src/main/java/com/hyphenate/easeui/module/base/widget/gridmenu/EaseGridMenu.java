package com.hyphenate.easeui.module.base.widget.gridmenu;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.easeui.R;

import java.util.List;

public class EaseGridMenu extends LinearLayoutCompat {

    private RecyclerView recycler_menu;

    private GridLayoutManager mLayoutManager;
    private EaseGridMenuRecyclerAdapter mRecyclerAdapter;

    private int mSpaceCount;

    public EaseGridMenu(Context context) {
        super(context);
        initData(context);
        initView(context);
        setView();
    }

    public EaseGridMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
        initView(context);
        setView();
    }

    public EaseGridMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
        initView(context);
        setView();
    }

    private void initData(Context context) {
        mSpaceCount = 2;
        mLayoutManager = new GridLayoutManager(context, mSpaceCount);
        mRecyclerAdapter = new EaseGridMenuRecyclerAdapter();
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_grid_menu, this);
        recycler_menu = view.findViewById(R.id.recycler_menu);
    }

    private void setView() {
        recycler_menu.setLayoutManager(mLayoutManager);
        recycler_menu.addItemDecoration(new EaseGridItemDecoration(mSpaceCount));
        recycler_menu.setAdapter(mRecyclerAdapter);
    }

    public void setSpaceCount(int count) {
        mSpaceCount = count;
    }

    public void setData(List<EaseGridMenuItem> menuItems) {
        int count = (menuItems == null ? 0 : menuItems.size());
        mSpaceCount = count == 1 ? 1 : 2;

        mLayoutManager.setSpanCount(mSpaceCount);
        recycler_menu.setLayoutManager(mLayoutManager);
        mRecyclerAdapter.setData(menuItems);
    }

    public boolean isEmpty() {
        return mRecyclerAdapter == null || mRecyclerAdapter.isEmpty();
    }

}