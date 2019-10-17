package com.hyphenate.easeui.module.base.widget.messagelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class EaseMessageListView extends FrameLayout {

    protected SwipeRefreshLayout refreshLayout;
    protected ListView listView;

    protected EMConversation conversation;
    protected EaseMessageAdapter messageAdapter;

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

    public void init(String toChatUsername, EMConversation.EMConversationType conversationType, @Nullable EaseMessageListItemStyle listItemStyle, @Nullable EaseCustomChatRowProvider customChatRowProvider) {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, conversationType, true);

        messageAdapter = new EaseMessageAdapter(toChatUsername, conversationType, listView);
        messageAdapter.setItemStyle(listItemStyle);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);

        listView.setAdapter(messageAdapter);

        refreshSelectLast();
    }

    /**
     * 刷新
     */
    public void refresh() {
        if (messageAdapter != null) {
            messageAdapter.refresh();
        }
    }

    /**
     * 刷新并滑动到最下面
     */
    public void refreshSelectLast() {
        if (messageAdapter != null) {
            messageAdapter.refreshSelectLast();
        }
    }

    /**
     * 刷新并滑动到指定位置
     */
    public void refreshSeekTo(int position) {
        if (messageAdapter != null) {
            messageAdapter.refreshSeekTo(position);
        }
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

    /**
     * 设置自定义子项的提供者
     */
    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        if (messageAdapter != null) {
            messageAdapter.setCustomChatRowProvider(rowProvider);
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