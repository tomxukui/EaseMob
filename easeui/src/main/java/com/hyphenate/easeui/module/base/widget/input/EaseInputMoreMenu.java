package com.hyphenate.easeui.module.base.widget.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseDensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 更多菜单
 */
public class EaseInputMoreMenu extends GridView {

    private int mNumColumns;
    private List<ChatMenuItemModel> mItemModels;

    private ItemAdapter mItemAdapter;

    private LayoutInflater mInflater;

    public EaseInputMoreMenu(Context context) {
        super(context);
        initData(context, null, 0);
        initView();
    }

    public EaseInputMoreMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
        initView();
    }

    public EaseInputMoreMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView();
    }

    private void initData(Context context, AttributeSet attrs, int defStyle) {
        mInflater = LayoutInflater.from(context);
        mNumColumns = 4;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseInputMoreMenu, defStyle, 0);
            mNumColumns = ta.getInt(R.styleable.EaseInputMoreMenu_numColumns, mNumColumns);
            ta.recycle();
        }

        mItemAdapter = new ItemAdapter();
    }

    private void initView() {
        setNumColumns(mNumColumns);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setGravity(Gravity.CENTER_VERTICAL);
        setVerticalSpacing(EaseDensityUtil.dp2px(6));
        setAdapter(mItemAdapter);
    }

    public void addMenuItem(int icon, String name, OnClickListener listener) {
        if (mItemModels == null) {
            mItemModels = new ArrayList<>();
        }
        mItemModels.add(new ChatMenuItemModel(icon, name, listener));
        mItemAdapter.notifyDataSetChanged();
    }

    private class ItemAdapter extends BaseAdapter {

        ViewHolder vh;

        @Override
        public int getCount() {
            return mItemModels == null ? 0 : mItemModels.size();
        }

        @Override
        public ChatMenuItemModel getItem(int position) {
            return mItemModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ease_item_input_more_menu, parent, false);

                vh = new ViewHolder();
                vh.linear_menu = convertView.findViewById(R.id.linear_menu);
                vh.iv_icon = convertView.findViewById(R.id.iv_icon);
                vh.tv_name = convertView.findViewById(R.id.tv_name);

                convertView.setTag(vh);

            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            ChatMenuItemModel model = getItem(position);

            vh.linear_menu.setOnClickListener(model.clickListener);
            vh.iv_icon.setImageResource(model.icon);
            vh.tv_name.setText(model.name);

            return convertView;
        }

        class ViewHolder {

            LinearLayout linear_menu;
            ImageView iv_icon;
            TextView tv_name;

        }
    }

    class ChatMenuItemModel {

        int icon;
        String name;
        OnClickListener clickListener;

        public ChatMenuItemModel(int icon, String name, OnClickListener clickListener) {
            this.icon = icon;
            this.name = name;
            this.clickListener = clickListener;
        }

    }

}
