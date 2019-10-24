package com.hyphenate.easeui.module.inquiry.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryGridMenuItem;

import java.util.List;

public class EaseInquiryEndedMenuRecyclerAdapter extends RecyclerView.Adapter<EaseInquiryEndedMenuRecyclerAdapter.ViewHolder> {

    private List<EaseInquiryGridMenuItem> mMenuItems;

    public EaseInquiryEndedMenuRecyclerAdapter() {
    }

    @Override
    public int getItemCount() {
        return mMenuItems == null ? 0 : mMenuItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_recycler_inquiry_ended_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        EaseInquiryGridMenuItem menuItem = mMenuItems.get(position);

        vh.tv_name.setText(menuItem.getName());
        vh.tv_name.setOnClickListener(v -> {
            EaseInquiryGridMenuItem.OnItemClickListener listener = menuItem.getOnItemClickListener();

            if (listener != null) {
                listener.onItemClick(menuItem, position);
            }
        });
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;

        public ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
        }

    }

    public void setData(List<EaseInquiryGridMenuItem> menuItems) {
        mMenuItems = menuItems;
        notifyDataSetChanged();
    }

}