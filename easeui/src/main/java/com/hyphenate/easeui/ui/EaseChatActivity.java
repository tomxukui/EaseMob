package com.hyphenate.easeui.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;

public class EaseChatActivity extends EaseBaseChainActivity {

    private static final String EXTRA_CHAT_ENABLED = "EXTRA_CHAT_ENABLED";//是否具备聊天功能
    private static final String EXTRA_FINISH_CONVERSATION_ENABLED = "EXTRA_FINISH_CONVERSATION_ENABLED";//是否具备结束问诊功能
    private static final String EXTRA_LOCATION_ENABLED = "EXTRA_LOCATION_ENABLED";//是否具备定位功能

    private int mChatType;
    private String mToUsername;
    private boolean mShowUserNick;//是否显示昵称
    private boolean mTurnOnTyping;//"正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
    private boolean mIsRoaming;//是否漫游
    private String mForwardMsgId;//是否滑动到指定消息
    private boolean mChatEnabled;//是否可以聊天
    private boolean mFinishConversationEnabled;//是否可以结束问诊
    private boolean mLocatinEnable;//是否可以定位

    @Override
    protected void initData() {
        super.initData();
        mChatType = getIntent().getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        mToUsername = getIntent().getStringExtra(EaseConstant.EXTRA_TO_USERNAME);
        mTurnOnTyping = getIntent().getBooleanExtra(EaseConstant.EXTRA_TURN_ON_TYPING, false);
        mShowUserNick = getIntent().getBooleanExtra(EaseConstant.EXTRA_SHOW_NICKNAME, true);
        mIsRoaming = getIntent().getBooleanExtra(EaseConstant.EXTRA_IS_ROAMING, false);
        mForwardMsgId = getIntent().getStringExtra(EaseConstant.EXTRA_FORWARD_MSG_ID);
        mChatEnabled = getIntent().getBooleanExtra(EXTRA_CHAT_ENABLED, true);
        mFinishConversationEnabled = getIntent().getBooleanExtra(EXTRA_FINISH_CONVERSATION_ENABLED, false);
        mLocatinEnable = getIntent().getBooleanExtra(EXTRA_LOCATION_ENABLED, false);
    }

    @Override
    protected Fragment getMainFragment() {
        return new EaseChatFragment.Builder()
                .setChatType(mChatType)
                .setToUser(mToUsername)
                .setTurnOnTyping(mTurnOnTyping)
                .setShowUserNick(mShowUserNick)
                .setIsRoaming(mIsRoaming)
                .setForwardMsgId(mForwardMsgId)
                .setChatEnabled(mChatEnabled)
                .setFinishConversationEnabled(mFinishConversationEnabled)
                .setLocatinEnable(mLocatinEnable)
                .create();
    }

    public static class Builder {

        private Intent mIntent;

        public Builder(Context context) {
            mIntent = new Intent(context, EaseChatActivity.class);
        }

        public Builder setChatType(int chatType) {
            mIntent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
            return this;
        }

        public Builder needLogin(String username, String pwd) {
            mIntent.putExtra(EXTRA_MY_USERNAME, username);
            mIntent.putExtra(EXTRA_MY_USERPWD, pwd);
            return this;
        }

        public Builder needLogout(boolean need) {
            mIntent.putExtra(EXTRA_NEED_LOGOUT, need);
            return this;
        }

        public Builder setToUser(String username) {
            mIntent.putExtra(EaseConstant.EXTRA_TO_USERNAME, username);
            return this;
        }

        public Builder setTurnOnTyping(boolean turnOnTyping) {
            mIntent.putExtra(EaseConstant.EXTRA_TURN_ON_TYPING, turnOnTyping);
            return this;
        }

        public Builder setShowUserNick(boolean showUserNick) {
            mIntent.putExtra(EaseConstant.EXTRA_SHOW_NICKNAME, showUserNick);
            return this;
        }

        public Builder setIsRoaming(boolean isRoaming) {
            mIntent.putExtra(EaseConstant.EXTRA_IS_ROAMING, isRoaming);
            return this;
        }

        public Builder setForwardMsgId(String forwardMsgId) {
            mIntent.putExtra(EaseConstant.EXTRA_FORWARD_MSG_ID, forwardMsgId);
            return this;
        }

        public Builder setChatEnabled(boolean chatable) {
            mIntent.putExtra(EXTRA_CHAT_ENABLED, chatable);
            return this;
        }

        public Builder setFinishConversationEnabled(boolean enabled) {
            mIntent.putExtra(EXTRA_FINISH_CONVERSATION_ENABLED, enabled);
            return this;
        }

        public Builder setLocatinEnable(boolean enabled) {
            mIntent.putExtra(EXTRA_LOCATION_ENABLED, enabled);
            return this;
        }

        public Builder setMessage(EMMessage message) {
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                setChatType(EaseConstant.CHATTYPE_SINGLE);

            } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                setChatType(EaseConstant.CHATTYPE_GROUP);

            } else if (message.getChatType() == EMMessage.ChatType.ChatRoom) {
                setChatType(EaseConstant.CHATTYPE_CHATROOM);
            }
            setToUser(message.getUserName());
            return this;
        }

        public Intent create() {
            return mIntent;
        }

    }

}