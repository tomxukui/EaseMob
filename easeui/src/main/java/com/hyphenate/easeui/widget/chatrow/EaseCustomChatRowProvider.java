package com.hyphenate.easeui.widget.chatrow;

import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.presenter.EaseChatRowPresenter;

/**
 * 自定义chat row提供者
 */
public interface EaseCustomChatRowProvider {

    /**
     * 获取多少种类型的自定义chatrow
     * 注意，每一种chatrow至少有两种type：发送type和接收type
     */
    int getCustomChatRowTypeCount();

    /**
     * 获取chatrow type，必须大于0, 从1开始有序排列
     */
    int getCustomChatRowType(EMMessage message);

    /**
     * 根据给定message返回chat row
     */
    EaseChatRowPresenter getCustomChatRow(EMMessage message, int position, BaseAdapter adapter);

}