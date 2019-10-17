package com.easeui.app.module.patient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easeui.app.R;
import com.easeui.app.module.patient.model.PatientInquiryMenuItem;

import java.util.List;

public class PatientInquiryMenuListAdapter extends BaseAdapter {

    private List<PatientInquiryMenuItem> mMenuItems;

    private ViewHolder mViewHolder;

    public PatientInquiryMenuListAdapter(List<PatientInquiryMenuItem> menuItems) {
        this.mMenuItems = menuItems;
    }

    @Override
    public int getCount() {
        return mMenuItems == null ? 0 : mMenuItems.size();
    }

    @Override
    public PatientInquiryMenuItem getItem(int position) {
        return mMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_patient_inquiry_menu, parent, false);

            mViewHolder = new ViewHolder();
            mViewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            mViewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            mViewHolder.v_line = convertView.findViewById(R.id.v_line);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        PatientInquiryMenuItem menuItem = getItem(position);

        mViewHolder.iv_icon.setImageResource(menuItem.getResId());
        mViewHolder.tv_name.setText(menuItem.getName());
        mViewHolder.v_line.setVisibility((position < getCount() - 1) ? View.VISIBLE : View.GONE);

        return convertView;
    }

    class ViewHolder {

        ImageView iv_icon;
        TextView tv_name;
        View v_line;

    }

}