package com.hyphenate.easeui.module.conversation.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.ui.EaseBaseFragment;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeui.module.conversation.widget.EaseConversationListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * conversation list fragment
 */
public class EaseConversationsFragment extends EaseBaseFragment {

    private final static int MSG_REFRESH = 2;

    private EaseToolbar toolbar;
    private EaseConversationListView listView;

    private List<EMConversation> conversationList = new ArrayList<>();

    public static EaseConversationsFragment newInstance() {
        return new EaseConversationsFragment();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_fragment_conversations;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        listView = view.findViewById(R.id.listView);
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        conversationList.addAll(loadConversationList());
        listView.init(conversationList);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            EMConversation conversation = listView.getItem(position);
            EMConversation.EMConversationType type = conversation.getType();
            int chatType = 0;
            if (type == EMConversation.EMConversationType.Chat) {
                chatType = EaseConstant.CHATTYPE_SINGLE;

            } else if (type == EMConversation.EMConversationType.GroupChat) {
                chatType = EaseConstant.CHATTYPE_GROUP;

            } else if (type == EMConversation.EMConversationType.ChatRoom) {
                chatType = EaseConstant.CHATTYPE_CHATROOM;
            }

            if (chatType > 0) {
//                startActivity(new EaseChatActivity.Builder(getContext())
//                        .setChatType(chatType)
//                        .setToUser(conversation.conversationId())
//                        .setFinishConversationEnabled(true)
//                        .create());
            }
        });

        listView.setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return false;
        });

        EMClient.getInstance().addConnectionListener(mConnectionListener);
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(mConnectionListener);
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    /**
     * connected to server
     */
    protected void onConnectionConnected() {
    }

    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected() {
    }

    /**
     * refresh ui
     */
    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * load conversation list
     */
    protected List<EMConversation> loadConversationList() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<>();

        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }

        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<EMConversation> list = new ArrayList<>();

        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        return list;
    }

    /**
     * sort conversations according time stamp of last message
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, (con1, con2) -> {
            if (con1.first.equals(con2.first)) {
                return 0;

            } else if (con2.first.longValue() > con1.first.longValue()) {
                return 1;

            } else {
                return -1;
            }
        });
    }

    private final Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 0:
                    onConnectionDisconnected();
                    break;

                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH: {
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    listView.refresh();
                    break;
                }

                default:
                    break;
            }
        }

    };

    /**
     * 监听账号登录
     */
    protected EMConnectionListener mConnectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            handler.sendEmptyMessage(0);
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }

    };

    /**
     * 监听聊天信息变化
     */
    private final EMMessageListener mMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> list) {
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }

    };

}
