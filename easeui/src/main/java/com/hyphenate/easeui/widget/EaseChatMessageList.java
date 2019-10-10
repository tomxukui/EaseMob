package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
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
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class EaseChatMessageList extends FrameLayout {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected EMConversation conversation;
    protected EaseMessageAdapter messageAdapter;
    protected EaseMessageListItemStyle itemStyle;

    protected int chatType;
    protected String toChatUsername;

    public EaseChatMessageList(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public EaseChatMessageList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs, 0);
        initView(context);
    }

    public EaseChatMessageList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList, defStyleAttr, 0);

            EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
            builder.showAvatar(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true))
                    .showUserNick(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false))
                    .myBubbleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground))
                    .otherBuddleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground));
            itemStyle = builder.build();

            ta.recycle();
        }
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this);

        swipeRefreshLayout = view.findViewById(R.id.chat_swipe_layout);
        listView = view.findViewById(R.id.list);
    }

    /**
     * init widget
     *
     * @param toChatUsername
     * @param chatType
     * @param customChatRowProvider
     */
    public void init(String toChatUsername, int chatType, EaseCustomChatRowProvider customChatRowProvider) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        messageAdapter = new EaseMessageAdapter(getContext(), toChatUsername, chatType, listView);
        messageAdapter.setItemStyle(itemStyle);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);
        // set message adapter
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

    public void setShowUserNick(boolean showUserNick) {
        itemStyle.setShowUserNick(showUserNick);
    }

    public boolean isShowUserNick() {
        return itemStyle.isShowUserNick();
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