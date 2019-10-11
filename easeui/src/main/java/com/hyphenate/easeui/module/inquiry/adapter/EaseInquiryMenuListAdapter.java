package com.hyphenate.easeui.module.inquiry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryMenuItem;

import java.util.List;

public class EaseInquiryMenuListAdapter extends BaseAdapter {

    private List<EaseInquiryMenuItem> mMenuItems;

    private ViewHolder mViewHolder;

    public EaseInquiryMenuListAdapter(List<EaseInquiryMenuItem> menuItems) {
        this.mMenuItems = menuItems;
    }

    @Override
    public int getCount() {
        return mMenuItems == null ? 0 : mMenuItems.size();
    }

    @Override
    public EaseInquiryMenuItem getItem(int position) {
        return mMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_list_inquiry_menu, parent, false);

            mViewHolder = new ViewHolder();
            mViewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            mViewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        EaseInquiryMenuItem menuItem = getItem(position);

        mViewHolder.iv_icon.setImageResource(menuItem.getResId());
        mViewHolder.tv_name.setText(menuItem.getName());

        return convertView;
    }

    class ViewHolder {

        ImageView iv_icon;
        TextView tv_name;

    }

}