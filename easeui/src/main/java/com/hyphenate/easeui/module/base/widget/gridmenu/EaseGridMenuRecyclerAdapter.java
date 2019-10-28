package com.hyphenate.easeui.module.base.widget.gridmenu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.easeui.R;

import java.util.List;

public class EaseGridMenuRecyclerAdapter extends RecyclerView.Adapter<EaseGridMenuRecyclerAdapter.ViewHolder> {

    private List<EaseGridMenuItem> mMenuItems;

    @Override
    public int getItemCount() {
        return mMenuItems == null ? 0 : mMenuItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_grid_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        EaseGridMenuItem menuItem = mMenuItems.get(position);

        vh.tv_name.setText(menuItem.getName());
        vh.tv_name.setOnClickListener(menuItem.getOnClickListener());
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

    public void setData(List<EaseGridMenuItem> menuItems) {
        mMenuItems = menuItems;
        notifyDataSetChanged();
    }

}