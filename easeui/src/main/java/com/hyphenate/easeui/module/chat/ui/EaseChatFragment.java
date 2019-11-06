package com.hyphenate.easeui.module.chat.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChatFragment;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeui.module.base.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;
import com.hyphenate.easeui.module.base.widget.input.OnInputMenuListener;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;
import com.hyphenate.easeui.module.chat.provider.EaseChatInputMenuProvider;
import com.hyphenate.easeui.module.chat.provider.EaseChatMessageProvider;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * 单人聊天页面
 */
public class EaseChatFragment extends EaseBaseChatFragment {

    protected static final String EXTRA_FROM_USER = "EXTRA_FROM_USER";
    protected static final String EXTRA_TO_USER = "EXTRA_TO_USER";

    protected LinearLayout linear_container;
    protected EaseToolbar toolbar;
    protected FrameLayout frame_main;
    protected EaseMessageListView list_message;
    protected FrameLayout frame_main_custom;
    protected EaseVoiceRecorderView voice_recorder;
    protected LinearLayoutCompat linear_footer;
    protected FrameLayout frame_footer_custom;
    protected EaseInputMenu menu_input;

    protected EMConversation mConversation;//会话

    protected int mPageSize = 20;//消息分页一页最多数量
    protected boolean mHaveMoreData = true;//是否有更多消息
    protected boolean mIsMessageInit;//消息是否已加载

    protected EaseUser mFromUser;
    protected EaseUser mToUser;

    private EaseChatMessageProvider mMessageProvider;
    private EaseChatInputMenuProvider mInputMenuProvider;

    public static EaseChatFragment newInstance(EaseUser fromUser, EaseUser toUser) {
        EaseChatFragment fragment = new EaseChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_fragment_chat;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFromUser = (EaseUser) bundle.getSerializable(EXTRA_FROM_USER);
            mToUser = (EaseUser) bundle.getSerializable(EXTRA_TO_USER);
        }

        mMessageProvider = onSetMessageRow();
        if (mMessageProvider == null) {
            mMessageProvider = new EaseChatMessageProvider();
        }

        mInputMenuProvider = onSetInputMenu();
        if (mInputMenuProvider == null) {
            mInputMenuProvider = new EaseChatInputMenuProvider() {

                @Override
                public List<EaseMenuItem> onSetMoreMenuItems() {
                    List<EaseMenuItem> menuItems = new ArrayList<>();
                    menuItems.add(createAlbumMenuItem());
                    menuItems.add(createCameraMenuItem());
                    return menuItems;
                }

            };
        }
    }

    @Override
    public String getToUsername() {
        return mToUser == null ? null : mToUser.getUsername();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        linear_container = view.findViewById(R.id.linear_container);
        toolbar = view.findViewById(R.id.toolbar);
        frame_main = view.findViewById(R.id.frame_main);
        list_message = view.findViewById(R.id.list_message);
        frame_main_custom = view.findViewById(R.id.frame_main_custom);
        voice_recorder = view.findViewById(R.id.voice_recorder);
        linear_footer = view.findViewById(R.id.linear_footer);
        frame_footer_custom = view.findViewById(R.id.frame_footer_custom);
        menu_input = view.findViewById(R.id.menu_input);
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
        setToolbar();
    }

    /**
     * 设置标题栏
     */
    protected void setToolbar() {
        //设置标题
        String title = mToUser.getNickname();
        if (TextUtils.isEmpty(title)) {
            title = mToUser.getUsername();
        }
        toolbar.setTitle(title);

        //设置返回点击事件
        toolbar.setOnBackClickListener(v -> onBackPressed());
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        //初始化会话
        initConversation();

        //加载本地第一次消息列表
        loadFirstLocalMessages();

        //设置消息列表控件
        setMessageList();

        //设置输入菜单
        setInputMenu();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addMessageListener(mMessageListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsMessageInit) {
            refreshMessages();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeMessageListener(mMessageListener);
    }

    /**
     * 初始化会话
     */
    protected void initConversation() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToUser.getUsername(), EMConversation.EMConversationType.Chat, true);
        mConversation.markAllMessagesAsRead();
    }

    /**
     * 设置消息列表控件
     */
    protected void setMessageList() {
        list_message.init(mMessageProvider.getMessageListItemStyle(), mMessageProvider.getCustomChatRowProvider());
        list_message.setOnItemClickListener(new EaseMessageListView.OnItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                mMessageProvider.onAvatarClick(username);
            }

            @Override
            public boolean onResendClick(final EMMessage message) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.resend)
                        .setMessage(R.string.confirm_resend)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            message.setStatus(EMMessage.Status.CREATE);
                            sendMessage(message);
                        })
                        .create()
                        .show();
                return true;
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                mMessageProvider.onAvatarLongClick(username);
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                mMessageProvider.onMessageBubbleLongClick(message);
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                return mMessageProvider.onMessageBubbleClick(message);
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(mMessageStatusCallback);
            }

        });
        list_message.getListView().setOnTouchListener((v, event) -> {
            menu_input.shrink();
            return false;
        });

        list_message.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.setOnRefreshListener(() -> getHandler().postDelayed(() -> loadMoreLocalMessages(), 600));

        refreshMessages();
        scrollToLast();

        mIsMessageInit = true;
    }

    /**
     * 设置输入菜单
     */
    protected void setInputMenu() {
        //添加表情菜单
        addFaceMenu(menu_input, 3);

        //添加更多菜单
        addMoreMenu(menu_input, 4, mInputMenuProvider.onSetMoreMenuItems());

        //设置是否开启语音菜单
        menu_input.getControl().setVoiceVisibility(mInputMenuProvider.voiceEnable());

        //设置是否开启表情菜单
        getFaceButton().setVisibility(mInputMenuProvider.faceEnable() ? View.VISIBLE : View.GONE);

        //设置是否开启更多菜单
        getMoreButton().setVisibility(mInputMenuProvider.moreEnable() ? View.VISIBLE : View.GONE);

        //扩展自定义菜单
        mInputMenuProvider.onExtendInputMenu(menu_input);

        //监听输入菜单
        menu_input.setOnInputMenuListener(new OnInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                if (getMoreButton() != null) {
                    getMoreButton().setVisibility(TextUtils.isEmpty(s.toString()) ? View.VISIBLE : View.GONE);
                }

                mInputMenuProvider.onTyping(s, start, before, count);
            }

            @Override
            public void onSendBtnClick(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    if (!hasPermissions(Permission.Group.STORAGE, Permission.Group.MICROPHONE)) {
                        requestPermission(null, Permission.Group.STORAGE, Permission.Group.MICROPHONE);
                        return true;
                    }
                }

                return voice_recorder.onPressToSpeakBtnTouch(v, event, (voiceFilePath, voiceTimeLength) -> sendVoiceMessage(voiceFilePath, voiceTimeLength));
            }

            @Override
            public void onEditFocusChange(boolean hasFocus) {
                if (getMoreButton() != null) {
                    if (hasFocus) {
                        getMoreButton().setVisibility(menu_input.getControl().isTextEmpty() ? View.VISIBLE : View.GONE);

                    } else {
                        getMoreButton().setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onEditTextClicked() {
                if (getFaceButton() != null) {
                    getFaceButton().setSelected(false);
                }

                if (getMoreButton() != null) {
                    getMoreButton().setVisibility(menu_input.getControl().isTextEmpty() ? View.VISIBLE : View.GONE);
                    getMoreButton().setSelected(false);
                }

                mInputMenuProvider.onEditTextClicked();
            }

            @Override
            public void onToggleVoice(boolean show) {
                if (getFaceButton() != null) {
                    getFaceButton().setSelected(false);
                }

                if (getMoreButton() != null) {
                    getMoreButton().setSelected(false);

                    if (show) {
                        getMoreButton().setVisibility(View.VISIBLE);

                    } else {
                        getMoreButton().setVisibility(menu_input.getControl().isTextEmpty() ? View.VISIBLE : View.GONE);
                    }
                }

                mInputMenuProvider.onToggleVoice(show);
            }

        });
    }

    /**
     * 加载本地第一次消息列表
     */
    protected void loadFirstLocalMessages() {
        List<EMMessage> messages = mConversation.getAllMessages();
        int msgCount = (messages == null ? 0 : messages.size());
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }
    }

    /**
     * 加载本地更多消息列表
     */
    protected void loadMoreLocalMessages() {
        if (list_message.getFirstVisiblePosition() == 0 && mHaveMoreData) {
            List<EMMessage> messages;

            try {
                messages = mConversation.loadMoreMsgFromDB(mConversation.getAllMessages().size() == 0 ? "" : mConversation.getAllMessages().get(0).getMsgId(), mPageSize);

            } catch (Exception e) {
                list_message.setRefreshing(false);
                return;
            }

            if (messages != null && messages.size() > 0) {
                refreshMessages();
                scrollTo(messages.size() - 1);

                if (messages.size() != mPageSize) {
                    mHaveMoreData = false;
                }

            } else {
                mHaveMoreData = false;
            }

        } else {
            EaseToastUtil.show(R.string.no_more_messages);
        }

        list_message.setRefreshing(false);
    }

    /**
     * 页面返回
     */
    protected void onBackPressed() {
        if (menu_input.onBackPressed()) {
            finish();
        }
    }

    @Override
    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }

        //设置消息是单聊类型
        message.setChatType(EMMessage.ChatType.Chat);

        //设置消息的通用扩展消息
        EaseMessageUtil.setUserMessage(message, mFromUser, mToUser);

        //设置消息的自定义设置
        mMessageProvider.onSendMessageWithAttributes(message);

        //设置消息的回调
        message.setMessageStatusCallback(mMessageStatusCallback);

        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

        //刷新消息列表到最新那条
        if (mIsMessageInit) {
            refreshMessages();
            scrollToLast();
        }
    }

    /**
     * 消息状态回调
     */
    private EMCallBack mMessageStatusCallback = new EMCallBack() {

        @Override
        public void onSuccess() {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

        @Override
        public void onError(int code, String error) {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

    };

    /**
     * 刷新消息列表
     */
    protected void refreshMessages() {
        mConversation.markAllMessagesAsRead();

        if (list_message != null) {
            list_message.setNewData(mConversation.getAllMessages());
        }
    }

    /**
     * 滑动到最新位置
     */
    protected void scrollToLast() {
        if (list_message != null) {
            list_message.scrollToLast();
        }
    }

    /**
     * 滑动到指定位置
     */
    public void scrollTo(int position) {
        if (list_message != null) {
            list_message.scrollTo(position);
        }
    }

    /**
     * 清空所有聊天消息
     */
    protected void clearAllMessages() {
        if (mConversation != null) {
            mConversation.clearAllMessages();
        }
    }

    /**
     * 插入文字
     */
    protected void insertText(String text) {
        menu_input.insertText(text);
    }

    /**
     * 消息回调
     */
    private final EMMessageListener mMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            runOnUiThread(() -> {
                for (EMMessage message : messages) {
                    String username = message.getFrom();

                    if (username.equals(mToUser.getUsername()) || message.getTo().equals(mToUser.getUsername()) || message.conversationId().equals(mToUser.getUsername())) {
                        refreshMessages();
                        scrollToLast();

                        mConversation.markMessageAsRead(message.getMsgId());
                    }
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            if (mIsMessageInit) {
                runOnUiThread(() -> refreshMessages());
            }
        }

    };

    /**
     * 设置消息
     */
    protected EaseChatMessageProvider onSetMessageRow() {
        return null;
    }

    /**
     * 设置输入菜单
     */
    protected EaseChatInputMenuProvider onSetInputMenu() {
        return null;
    }

}