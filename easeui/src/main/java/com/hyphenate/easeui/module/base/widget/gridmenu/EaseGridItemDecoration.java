package com.hyphenate.easeui.module.base.widget.gridmenu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseDensityUtil;

public class EaseGridItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    private int mSpaceCount;
    private int mSpace;
    private int mPadding;

    public EaseGridItemDecoration(int spaceCount) {
        mSpaceCount = spaceCount;

        mSpace = EaseDensityUtil.dp2px(1);
        mPadding = EaseDensityUtil.dp2px(10);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(EaseContextCompatUtil.getColor(R.color.ease_bg));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mSpace);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mSpaceCount <= 0) {
            return;
        }

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
        if (mSpaceCount <= 0) {
            return;
        }

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