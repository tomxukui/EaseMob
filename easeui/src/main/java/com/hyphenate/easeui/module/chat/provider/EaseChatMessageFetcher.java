package com.hyphenate.easeui.module.chat.provider;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;

public interface EaseChatMessageFetcher {

    /**
     * 获取分页的每页最大数量
     */
    int getPageSize();

    /**
     * 从数据库中获取消息列表, 仅限第一次加载的时候触发一次
     */
    boolean loadFirstMessages(EMConversation conversation, EaseMessageListView listView);

    /**
     * 从数据库中获取之前更早的消息列表
     */
    void loadMoreEarlierMessages(EMConversation conversation, EaseMessageListView listView);

    /**
     * 刷新当前消息列表
     */
    void refreshMessages(EMConversation conversation, EaseMessageListView listView);

    /**
     * 列表滑动到最新一条的位置
     */
    void scrollToLast(EaseMessageListView listView);

    /**
     * 列表滑动到指定位置
     */
    void scrollTo(EaseMessageListView listView, int position);

}