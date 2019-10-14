package com.hyphenate.easeui.module.inquiry.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.inquiry.adapter.EaseInquiryEndedMenuRecyclerAdapter;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryEndedMenuItem;
import com.hyphenate.easeui.utils.ContextCompatUtil;
import com.hyphenate.easeui.utils.DensityUtil;

import java.util.List;

public class EaseInquiryEndedMenu extends LinearLayoutCompat {

    private RecyclerView recycler_menu;

    private GridLayoutManager mLayoutManager;
    private EaseInquiryEndedMenuRecyclerAdapter mRecyclerAdapter;

    private int mSpaceCount;

    public EaseInquiryEndedMenu(Context context) {
        super(context);
        initData(context);
        initView(context);
        setView();
    }

    public EaseInquiryEndedMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
        initView(context);
        setView();
    }

    public EaseInquiryEndedMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
        initView(context);
        setView();
    }

    private void initData(Context context) {
        mSpaceCount = 2;
        mLayoutManager = new GridLayoutManager(context, mSpaceCount);
        mRecyclerAdapter = new EaseInquiryEndedMenuRecyclerAdapter();
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_inquiry_ended_menu, this);
        recycler_menu = view.findViewById(R.id.recycler_menu);
    }

    private void setView() {
        recycler_menu.setLayoutManager(mLayoutManager);
        recycler_menu.addItemDecoration(new EaseGridItemDecoration());
        recycler_menu.setAdapter(mRecyclerAdapter);
    }

    public void setData(List<EaseInquiryEndedMenuItem> menuItems) {
        int count = (menuItems == null ? 0 : menuItems.size());
        mSpaceCount = count == 1 ? 1 : 2;

        mLayoutManager.setSpanCount(mSpaceCount);
        recycler_menu.setLayoutManager(mLayoutManager);
        mRecyclerAdapter.setData(menuItems);
    }

    class EaseGridItemDecoration extends RecyclerView.ItemDecoration {

        private Paint mPaint;
        private int mSpace;
        private int mPadding;

        public EaseGridItemDecoration() {
            mSpace = DensityUtil.dp2px(1);
            mPadding = DensityUtil.dp2px(10);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(ContextCompatUtil.getColor(R.color.ease_bg));
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(mSpace);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int row = position / mSpaceCount;
            int column = position % mSpaceCount;

            if (column < mSpaceCount - 1) {
                outRect.right = mSpace;
            }

            if (row > 0) {
                outRect.top = mSpace;
            }
        }

        private int getRowCount(RecyclerView parent) {
            int count = parent.getChildCount();

            int rowCount = (count / mSpaceCount);
            if (rowCount * mSpaceCount < count) {
                rowCount++;
            }

            return rowCount;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int count = parent.getChildCount();

            if (count <= 1) {
                return;
            }

            int rowCount = getRowCount(parent);
            float itemWidth = 1f * parent.getWidth() / mSpaceCount;
            float itemHeight = 1f * parent.getHeight() / rowCount;

            for (int i = 0; i < parent.getChildCount(); i++) {
                int row = i / mSpaceCount;
                int column = i % mSpaceCount;

                c.save();
                c.translate(column * itemWidth, row * itemHeight);
                if (column < mSpaceCount - 1) {
                    c.drawRect(itemWidth - mSpace, mPadding, itemWidth, itemHeight - mPadding, mPaint);
                }
                if (row > 0) {
                    c.drawRect(mPadding, 0, itemWidth - mPadding, mSpace, mPaint);
                }
                c.restore();
            }
        }

    }

}