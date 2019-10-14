package com.hyphenate.easeui.module.inquiry.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.inquiry.adapter.EaseInquiryEndedMenuRecyclerAdapter;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryEndedMenuItem;
import com.hyphenate.easeui.utils.ContextCompatUtil;
import com.hyphenate.easeui.utils.DensityUtil;

import java.util.List;

public class EaseInquiryEndedMenu extends LinearLayoutCompat {

    private static final int SPAN_COUNT = 2;

    private RecyclerView recycler_menu;

    private EaseInquiryEndedMenuRecyclerAdapter mRecyclerAdapter;

    public EaseInquiryEndedMenu(Context context) {
        super(context);
        initData();
        initView(context);
        setView(context);
    }

    public EaseInquiryEndedMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        initView(context);
        setView(context);
    }

    public EaseInquiryEndedMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        initView(context);
        setView(context);
    }

    private void initData() {
        mRecyclerAdapter = new EaseInquiryEndedMenuRecyclerAdapter();
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.ease_widget_inquiry_ended_menu, this);
        recycler_menu = view.findViewById(R.id.recycler_menu);
    }

    private void setView(Context context) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, SPAN_COUNT);
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new EaseGridItemDecoration());
        recycler_menu.setAdapter(mRecyclerAdapter);
    }

    public void setData(List<EaseInquiryEndedMenuItem> menuItems) {
        mRecyclerAdapter.setData(menuItems);
    }

    class EaseGridItemDecoration extends RecyclerView.ItemDecoration {

        private Paint mPaint;
        private int mDiverWidth;

        private final Rect mBounds = new Rect();

        public EaseGridItemDecoration() {
            mDiverWidth = DensityUtil.dp2px(1);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(ContextCompatUtil.getColor(R.color.ease_bg));
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(mDiverWidth);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % SPAN_COUNT;

            outRect.left = column * mDiverWidth / SPAN_COUNT;
            outRect.right = mDiverWidth - (column + 1) * mDiverWidth / SPAN_COUNT;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                parent.getDecoratedBoundsWithMargins(view, mBounds);

                Log.e("ddd", mBounds.toString());

                c.drawRect(mBounds, mPaint);
            }
        }

    }

}