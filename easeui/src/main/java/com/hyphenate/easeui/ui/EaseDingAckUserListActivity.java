package com.hyphenate.easeui.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.module.base.ui.EaseBaseActivity;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;

import java.util.ArrayList;
import java.util.List;

public class EaseDingAckUserListActivity extends EaseBaseActivity {

    private EaseToolbar toolbar;
    private ListView ackUserListView;

    private EMMessage msg;

    private AckUserAdapter userAdapter;
    private List<String> userList;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_ding_ack_user_list;
    }

    @Override
    protected void initData() {
        super.initData();
        msg = getIntent().getParcelableExtra("msg");
        userList = new ArrayList<>();
        userAdapter = new AckUserAdapter(this, userList);
    }

    @Override
    protected void initView() {
        super.initView();
        toolbar = findViewById(R.id.toolbar);
        ackUserListView = findViewById(R.id.list_view);
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
    }

    @Override
    protected void setView() {
        super.setView();
        ackUserListView.setAdapter(userAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> list = EaseDingMessageHelper.get().getAckUsers(msg);
        userList.clear();
        if (list != null) {
            userList.addAll(list);
        }
        userAdapter.notifyDataSetChanged();

        EaseDingMessageHelper.get().setUserUpdateListener(msg, userUpdateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EaseDingMessageHelper.get().setUserUpdateListener(msg, null);
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelper.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    userList.clear();
                    userList.addAll(list);

                    runOnUiThread(() -> userAdapter.notifyDataSetChanged());
                }
            };

    private static class AckUserAdapter extends BaseAdapter {

        private Context context;
        private List<String> userList;

        public AckUserAdapter(Context context, List<String> userList) {
            this.context = context;
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.ease_row_ding_ack_user, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.nameView.setText(userList.get(position));

            return convertView;
        }

        private static class ViewHolder {

            public TextView nameView;

            public ViewHolder(View contentView) {
                nameView = contentView.findViewById(R.id.username);
            }
        }

    }

}