package com.hyphenate.easeui.widget;

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
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class EaseChatMessageList extends FrameLayout {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected EMConversation conversation;
    protected EaseMessageAdapter messageAdapter;

    protected String toChatUsername;

    public EaseChatMessageList(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public EaseChatMessageList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EaseChatMessageList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);

        swipeRefreshLayout = view.findViewById(R.id.chat_swipe_layout);
        listView = view.findViewById(R.id.list);
    }

    public void init(String toChatUsername, EMConversation.EMConversationType conversationType, @Nullable EaseMessageListItemStyle listItemStyle, @Nullable EaseCustomChatRowProvider customChatRowProvider) {
        this.toChatUsername = toChatUsername;

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, conversationType, true);

        messageAdapter = new EaseMessageAdapter(getContext(), toChatUsername, conversationType, listView);
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

    public ListView getListView() {
        return listView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public EMMessage getItem(int position) {
        return messageAdapter.getItem(position);
    }

    public interface MessageListItemClickListener {

        /**
         * there is default handling when bubble is clicked, if you want handle it, return true
         * another way is you implement in onBubbleClick() of chat row
         *
         * @param message
         * @return
         */
        boolean onBubbleClick(EMMessage message);

        boolean onResendClick(EMMessage message);

        void onBubbleLongClick(EMMessage message);

        void onUserAvatarClick(String username);

        void onUserAvatarLongClick(String username);

        void onMessageInProgress(EMMessage message);

    }

    /**
     * 设置子项点击事件
     */
    public void setItemClickListener(MessageListItemClickListener listener) {
        if (messageAdapter != null) {
            messageAdapter.setItemClickListener(listener);
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

}