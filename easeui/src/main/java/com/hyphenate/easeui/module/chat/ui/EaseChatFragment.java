package com.hyphenate.easeui.module.chat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.hyphenate.easeui.module.chat.provider.EaseChatInputMenuEvent;
import com.hyphenate.easeui.module.chat.provider.EaseChatInputMenuStyle;
import com.hyphenate.easeui.module.chat.provider.EaseChatMessageEvent;
import com.hyphenate.easeui.module.chat.provider.EaseChatMessageStyle;
import com.hyphenate.easeui.module.chat.provider.impl.EaseChatInputMenuDefaultStyle;
import com.hyphenate.easeui.module.chat.provider.impl.EaseChatMessageDefaultStyle;
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

    //输入菜单
    private EaseChatInputMenuStyle mInputMenuStyle;
    @Nullable
    private EaseChatInputMenuEvent mInputMenuEvent;

    //消息
    private EaseChatMessageStyle mMessageStyle;
    @Nullable
    private EaseChatMessageEvent mMessageEvent;

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

        //设置输入菜单样式
        mInputMenuStyle = getInputMenuStyle();

        //设置输入菜单事件
        mInputMenuEvent = getInputMenuEvent();

        //设置消息样式
        mMessageStyle = getMessageStyle();

        //设置消息事件
        mMessageEvent = getMessageEvent();
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

        //加载本地第一次消息列表
        loadLocalMessages();
    }

    /**
     * 设置消息列表控件
     */
    protected void setMessageList() {
        list_message.init(mMessageStyle.getMessageListItemStyle(), mMessageStyle.getCustomChatRowProvider());
        list_message.setOnItemClickListener(new EaseMessageListView.OnItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (mMessageEvent != null) {
                    mMessageEvent.onAvatarClick(username);
                }
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
                if (mMessageEvent != null) {
                    mMessageEvent.onAvatarLongClick(username);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                if (mMessageEvent != null) {
                    mMessageEvent.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                return mMessageEvent == null ? false : mMessageEvent.onMessageBubbleClick(message);
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

        refreshScrollToLast();

        mIsMessageInit = true;
    }

    /**
     * 设置输入菜单
     */
    protected void setInputMenu() {
        //添加表情菜单
        addFaceMenu(menu_input, 3);

        //添加更多菜单
        List<EaseMenuItem> menuItems = mInputMenuStyle.getMoreMenuItems();
        if (menuItems == null) {
            menuItems = new ArrayList<>();
        }
        if (mInputMenuStyle.pickCameraPhotoEnable()) {
            menuItems.add(0, createCameraMenuItem());
        }
        if (mInputMenuStyle.pickAlbumPhotoEnable()) {
            menuItems.add(0, createAlbumMenuItem());
        }
        addMoreMenu(menu_input, 4, menuItems);

        //设置是否开启语音菜单
        menu_input.getControl().setVoiceVisibility(mInputMenuStyle.voiceEnable());

        //设置是否开启表情菜单
        getFaceButton().setVisibility(mInputMenuStyle.faceEnable() ? View.VISIBLE : View.GONE);

        //设置是否开启更多菜单
        getMoreButton().setVisibility(mInputMenuStyle.moreEnable() ? View.VISIBLE : View.GONE);

        //扩展自定义菜单
        mInputMenuStyle.onExtendInputMenu(menu_input);

        //监听输入菜单
        menu_input.setOnInputMenuListener(new OnInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                if (getMoreButton() != null) {
                    getMoreButton().setVisibility(TextUtils.isEmpty(s.toString()) ? View.VISIBLE : View.GONE);
                }

                if (mInputMenuEvent != null) {
                    mInputMenuEvent.onTyping(s, start, before, count);
                }
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

                if (mInputMenuEvent != null) {
                    mInputMenuEvent.onEditTextClicked();
                }
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

                if (mInputMenuEvent != null) {
                    mInputMenuEvent.onToggleVoice(show);
                }
            }

        });
    }

    /**
     * 获取会话中的缓存消息
     */
    protected List<EMMessage> getConversationAllMessages() {
        return mConversation.getAllMessages();
    }

    /**
     * 加载本地第一次消息列表
     */
    protected void loadLocalMessages() {
        List<EMMessage> messages = getConversationAllMessages();
        int count = (messages == null ? 0 : messages.size());

        if (count < mConversation.getAllMsgCount() && count < mPageSize) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - count);
        }
    }

    /**
     * 加载本地更多消息列表
     */
    protected void loadMoreLocalMessages() {
        if (list_message.getFirstVisiblePosition() == 0 && mHaveMoreData) {
            List<EMMessage> messages;

            try {
                messages = mConversation.loadMoreMsgFromDB(getConversationAllMessages().size() == 0 ? "" : getConversationAllMessages().get(0).getMsgId(), mPageSize);

            } catch (Exception e) {
                list_message.setRefreshing(false);
                return;
            }

            if (messages != null && messages.size() > 0) {
                refreshScrollTo(messages.size() - 1);

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
        mMessageStyle.onSendMessageWithAttributes(message);

        //设置消息的回调
        message.setMessageStatusCallback(mMessageStatusCallback);

        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

        //刷新消息列表到最新那条
        if (mIsMessageInit) {
            refreshScrollToLast();
        }
    }

    /**
     * 消息状态回调
     */
    private EMCallBack mMessageStatusCallback = new EMCallBack() {

        @Override
        public void onSuccess() {
            if (mIsMessageInit) {
                runOnUiThread(() -> {
                    loadLastestMessages();
                    refreshMessages();
                });
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
        if (list_message != null) {
            list_message.setNewData(getConversationAllMessages());
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
    protected void scrollTo(int position) {
        if (list_message != null) {
            list_message.scrollTo(position);
        }
    }

    /**
     * 加载本地最新消息
     */
    protected void loadLastestMessages() {
    }

    /**
     * 刷新并滑动到指定位置
     */
    protected void refreshScrollToLast() {
        refreshMessages();
        scrollToLast();
    }

    /**
     * 刷新并滑动到指定位置
     */
    protected void refreshScrollTo(int position) {
        refreshMessages();
        scrollTo(position);
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
                        loadLastestMessages();
                        refreshScrollToLast();

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
     * 获取聊天输入菜单样式
     */
    protected EaseChatInputMenuStyle getInputMenuStyle() {
        return new EaseChatInputMenuDefaultStyle();
    }

    /**
     * 获取聊天输入菜单事件
     */
    protected EaseChatInputMenuEvent getInputMenuEvent() {
        return null;
    }

    /**
     * 获取聊天消息样式
     */
    protected EaseChatMessageStyle getMessageStyle() {
        return new EaseChatMessageDefaultStyle();
    }

    /**
     * 获取聊天消息事件
     */
    protected EaseChatMessageEvent getMessageEvent() {
        return null;
    }

}