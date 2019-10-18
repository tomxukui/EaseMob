package com.hyphenate.easeui.module.base.widget.message.row;

import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatRowPresenter;

/**
 * 自定义消息行的提供者
 */
public interface EaseCustomChatRowProvider {

    /**
     * 获取自定义消息类型的数量
     * <p>
     * ps:每一种自定义消息类型, 都必须有发送和接收两种类型, 所以数量是偶数
     */
    int getCustomChatRowTypeCount();

    /**
     * 根据消息获取自定义消息类型
     * <p>
     * ps:返回值必须大于0, 从1开始有序排列
     */
    int getCustomChatRowType(EMMessage message);

    /**
     * 根据消息返回自定义消息行
     */
    EaseChatRowPresenter getCustomChatRow(EMMessage message, int position, BaseAdapter adapter);

}