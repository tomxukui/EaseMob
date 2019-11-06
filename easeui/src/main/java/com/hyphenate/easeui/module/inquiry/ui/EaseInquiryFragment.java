package com.hyphenate.easeui.module.inquiry.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.chat.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseMessageUtil;

/**
 * 问诊页面-单聊
 */
public class EaseInquiryFragment extends EaseChatFragment {

    //透传类型
//    private static final String CMD_START_INQUIRY = "cmd_start_conversation";//开始问诊
//    private static final String CMD_CLOSE_INQUIRY = "cmd_close_conversation";//结束问诊

    protected boolean mIsClosed;//问诊是否已关闭

    public static EaseInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser) {
        EaseInquiryFragment fragment = new EaseInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 设置开始问诊
     */
    protected void setInquiryStarted() {
        mIsClosed = false;
    }

    /**
     * 设置关闭问诊
     */
    protected void setInquiryClosed() {
        mIsClosed = true;
    }

    /**
     * 开始问诊
     */
    protected void startInquiry(@Nullable String msg) {
        //设置文字内容
        String content = (msg == null ? EaseContextCompatUtil.getString(R.string.ease_inquiry_start) : msg);

        //创建文字消息
        EMMessage message = EMMessage.createTxtSendMessage(content, mToUser.getUsername());

        //设置消息类型是关闭问诊
        EaseMessageUtil.setAction(message, EaseMessageUtil.ACTION_START_INQUIRY);

        //发送消息
        sendMessage(message);

//        //刷新消息列表
//        if (mIsMessageInit) {
//            list_message.refreshSelectLast();
//        }

        setInquiryStarted();
        setStartInquiryView();
    }

    /**
     * 结束问诊
     *
     * @param msg 为空则使用默认文字
     */
    protected void closeInquiry(@Nullable String msg) {
        //设置文字内容
        String content = (msg == null ? EaseContextCompatUtil.getString(R.string.ease_inquiry_close) : msg);

        //创建文字消息
        EMMessage message = EMMessage.createTxtSendMessage(content, mToUser.getUsername());

        //设置消息类型是关闭问诊
        EaseMessageUtil.setAction(message, EaseMessageUtil.ACTION_CLOSE_INQUIRY);

        //发送消息
        sendMessage(message);

//        //刷新消息列表
//        if (mIsMessageInit) {
//            list_message.refreshSelectLast();
//        }

        //结束问诊
        setInquiryClosed();
        setCloseInquiryView();
        hideSoftKeyboard();
    }

    /**
     * 设置问诊开始的控件
     */
    protected void setStartInquiryView() {
        menu_input.setVisibility(View.VISIBLE);
    }

    /**
     * 设置问诊关闭的控件
     */
    protected void setCloseInquiryView() {
        menu_input.setVisibility(View.GONE);
    }

//    /**
//     * 消息回调
//     */
//    private final EMMessageListener mMessageListener = new EMMessageListener() {
//
//        @Override
//        public void onMessageReceived(List<EMMessage> messages) {
//            runOnUiThread(() -> {
//                for (EMMessage message : messages) {
//                    String username = message.getFrom();
//
//                    if (username.equals(mToUser.getUsername()) || message.getTo().equals(mToUser.getUsername()) || message.conversationId().equals(mToUser.getUsername())) {
//                        list_message.refreshSelectLast();
//                        mConversation.markMessageAsRead(message.getMsgId());
//                    }
//                }
//
//                //处理最新消息的Action
//                EMMessage lastMessage = messages.get(messages.size() - 1);
//
//                handleLastMessageAction(lastMessage);
//            });
//        }
//
//        @Override
//        public void onCmdMessageReceived(List<EMMessage> messages) {
////            runOnUiThread(() -> {
////                for (EMMessage msg : messages) {
////                    EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
////
////                    if (msg.getFrom().equals(mToUser.getUsername())) {
////                        if (CMD_START_INQUIRY.equals(body.action())) {
////                            setInquiryStarted();
////                            setStartInquiryView();
////
////                        } else if (CMD_CLOSE_INQUIRY.equals(body.action())) {
////                            setInquiryClosed();
////                            setCloseInquiryView();
////                            hideSoftKeyboard();
////                        }
////                    }
////                }
////            });
//        }
//
//        @Override
//        public void onMessageRead(List<EMMessage> list) {
//            if (mIsMessageInit) {
//                runOnUiThread(() -> list_message.refresh());
//            }
//        }
//
//        @Override
//        public void onMessageDelivered(List<EMMessage> list) {
//            if (mIsMessageInit) {
//                runOnUiThread(() -> list_message.refresh());
//            }
//        }
//
//        @Override
//        public void onMessageRecalled(List<EMMessage> list) {
//            if (mIsMessageInit) {
//                runOnUiThread(() -> list_message.refresh());
//            }
//        }
//
//        @Override
//        public void onMessageChanged(EMMessage emMessage, Object o) {
//            if (mIsMessageInit) {
//                runOnUiThread(() -> list_message.refresh());
//            }
//        }
//
//    };

}