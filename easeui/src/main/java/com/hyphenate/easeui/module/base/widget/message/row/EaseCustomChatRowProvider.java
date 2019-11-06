package com.hyphenate.easeui.module.base.widget.message.row;

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
    int getCustomTypeCount();

    /**
     * 根据消息获取自定义消息类型
     * <p>
     * ps:返回值必须从0开始有序递增
     */
    int getCustomType(EMMessage message);

    /**
     * 根据消息返回自定义消息行
     */
    EaseChatRowPresenter getCustomChatRow(int customType, EMMessage message, int position);

}