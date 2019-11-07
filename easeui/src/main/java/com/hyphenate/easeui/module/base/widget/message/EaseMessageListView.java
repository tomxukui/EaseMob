package com.hyphenate.easeui.module.base.widget.message;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.message.row.EaseCustomChatRowProvider;

import java.util.List;

public class EaseMessageListView extends FrameLayout {

    protected SwipeRefreshLayout refreshLayout;
    protected ListView listView;

    protected EaseMessageListAdapter messageAdapter;

    public EaseMessageListView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public EaseMessageListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EaseMessageListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        listView = view.findViewById(R.id.listView);
    }

    public void init(@Nullable EaseMessageListItemStyle listItemStyle, @Nullable EaseCustomChatRowProvider customChatRowProvider) {
        messageAdapter = new EaseMessageListAdapter();
        messageAdapter.setItemStyle(listItemStyle);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);

        listView.setAdapter(messageAdapter);
    }

    /**
     * 设置新数据
     */
    public void setNewData(List<EMMessage> messages) {
        if (messageAdapter != null) {
            messageAdapter.setNewData(messages);
        }
    }

    /**
     * 添加数据
     */
    public void addData(List<EMMessage> messages) {
        if (messageAdapter != null) {
            messageAdapter.addData(messages);
        }
    }

    /**
     * 滑动到最新位置
     */
    public void scrollToLast() {
        if (messageAdapter != null) {
            int count = messageAdapter.getCount();

            if (count > 0) {
                listView.setSelection(count - 1);
            }
        }
    }

    /**
     * 滑动到指定位置
     */
    public void scrollTo(int position) {
        if (messageAdapter != null && position >= 0 && position < messageAdapter.getCount()) {
            listView.setSelection(position);
        }
    }

    /**
     * 设置刷新开始和结束
     */
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    /**
     * 设置刷新监听事件
     */
    public void setOnRefreshListener(@Nullable SwipeRefreshLayout.OnRefreshListener listener) {
        refreshLayout.setOnRefreshListener(listener);
    }

    /**
     * 设置刷新颜色
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        refreshLayout.setColorSchemeResources(colorResIds);
    }

    /**
     * 获取显示的第一个位置
     */
    public int getFirstVisiblePosition() {
        return listView.getFirstVisiblePosition();
    }

    /**
     * 获取下拉刷新控件
     */
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return refreshLayout;
    }

    /**
     * 获取列表控件
     */
    public ListView getListView() {
        return listView;
    }

    /**
     * 获取指定位置的数据
     */
    public EMMessage getItem(int position) {
        return messageAdapter.getItem(position);
    }

    /**
     * 设置子项点击事件
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        if (messageAdapter != null) {
            messageAdapter.setOnItemClickListener(listener);
        }
    }

    public interface OnItemClickListener {

        /**
         * 消息点击
         */
        boolean onBubbleClick(EMMessage message);

        /**
         * 消息长按
         */
        void onBubbleLongClick(EMMessage message);

        /**
         * 重新发送点击
         */
        boolean onResendClick(EMMessage message);

        /**
         * 头像点击
         */
        void onUserAvatarClick(String username);

        /**
         * 头像长按
         */
        void onUserAvatarLongClick(String username);

        /**
         * 消息进度
         */
        void onMessageInProgress(EMMessage message);

    }

}