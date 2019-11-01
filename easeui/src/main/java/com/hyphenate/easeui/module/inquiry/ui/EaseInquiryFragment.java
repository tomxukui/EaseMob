package com.hyphenate.easeui.module.inquiry.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseChatFragment;
import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;
import com.hyphenate.easeui.module.base.widget.input.OnInputMenuListener;
import com.hyphenate.easeui.module.inquiry.provider.EaseInquiryInputMenuProvider;
import com.hyphenate.easeui.module.inquiry.callback.EaseOnInquiryListener;
import com.hyphenate.easeui.module.inquiry.provider.EaseInquiryMessageProvider;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.dialog.EaseAlertDialog;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeui.module.base.widget.EaseVoiceRecorderView;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * 问诊页面-单聊
 */
public class EaseInquiryFragment extends EaseBaseChatFragment {

    protected static final String EXTRA_FROM_USER = "EXTRA_FROM_USER";
    protected static final String EXTRA_TO_USER = "EXTRA_TO_USER";

    //透传类型
//    private static final String CMD_START_INQUIRY = "cmd_start_conversation";//开始问诊
//    private static final String CMD_CLOSE_INQUIRY = "cmd_close_conversation";//结束问诊

    protected LinearLayout linear_container;
    protected EaseToolbar toolbar;
    protected FrameLayout frame_main;
    protected EaseMessageListView list_message;
    protected FrameLayout frame_main_custom;
    protected EaseVoiceRecorderView voice_recorder;
    protected TextView tv_availableCount;
    protected LinearLayoutCompat linear_footer;
    protected FrameLayout frame_footer_custom;
    protected EaseInputMenu menu_input;

    protected EMConversation mConversation;//会话
    protected EaseUser mFromUser;
    protected EaseUser mToUser;
    protected boolean mIsMessageInit;//消息是否已加载
    protected boolean mIsClosed;//问诊是否已关闭
    protected int mPageSize = 20;//消息分页一页最多数量
    protected boolean mHaveMoreData = true;//是否有更多消息

    @Nullable
    private EaseOnInquiryListener mOnInquiryListener;

    private EaseInquiryMessageProvider mMessageProvider;
    private EaseInquiryInputMenuProvider mInputMenuProvider;

    public static EaseInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser) {
        EaseInquiryFragment fragment = new EaseInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_fragment_inquiry;
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
            mMessageProvider = new EaseInquiryMessageProvider();
        }

        mInputMenuProvider = onSetInputMenu();
        if (mInputMenuProvider == null) {
            mInputMenuProvider = new EaseInquiryInputMenuProvider() {

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
        tv_availableCount = view.findViewById(R.id.tv_availableCount);
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
            list_message.refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setOnInquiryListener(null);
        removeMessageListener(mMessageListener);
    }

    /**
     * 初始化会话
     */
    protected void initConversation() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToUser.getUsername(), EMConversation.EMConversationType.Chat, true);
        mConversation.markAllMessagesAsRead();

        List<EMMessage> messages = mConversation.getAllMessages();
        int msgCount = (messages == null ? 0 : messages.size());
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }

        //处理最新消息的动作
        if (messages != null && messages.size() > 0) {
            EMMessage lastMessage = messages.get(messages.size() - 1);

            handleLastMessageAction(lastMessage);
        }
    }

    /**
     * 设置消息列表控件
     */
    protected void setMessageList() {
        list_message.init(mToUser.getUsername(), EMConversation.EMConversationType.Chat, mMessageProvider.getMessageListItemStyle(), mMessageProvider.getCustomChatRowProvider());
        list_message.setOnItemClickListener(new EaseMessageListView.OnItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                mMessageProvider.onAvatarClick(username);
            }

            @Override
            public boolean onResendClick(final EMMessage message) {
                new EaseAlertDialog(getContext(), R.string.resend, R.string.confirm_resend, null, (confirmed, bundle) -> {
                    if (!confirmed) {
                        return;
                    }

                    message.setStatus(EMMessage.Status.CREATE);
                    sendMessage(message);
                }, true).show();
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

        list_message.getSwipeRefreshLayout().setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.getSwipeRefreshLayout().setOnRefreshListener(() -> {
            getHandler().postDelayed(() -> loadMoreLocalMessages(), 600);
        });

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
     * 加载本地更多消息列表
     */
    private void loadMoreLocalMessages() {
        if (list_message.getListView().getFirstVisiblePosition() == 0 && mHaveMoreData) {
            List<EMMessage> messages;

            try {
                messages = mConversation.loadMoreMsgFromDB(mConversation.getAllMessages().size() == 0 ? "" : mConversation.getAllMessages().get(0).getMsgId(), mPageSize);

            } catch (Exception e) {
                list_message.getSwipeRefreshLayout().setRefreshing(false);
                return;
            }

            if (messages.size() > 0) {
                list_message.refreshSeekTo(messages.size() - 1);

                if (messages.size() != mPageSize) {
                    mHaveMoreData = false;
                }

            } else {
                mHaveMoreData = false;
            }

        } else {
            EaseToastUtil.show(R.string.no_more_messages);
        }

        list_message.getSwipeRefreshLayout().setRefreshing(false);
    }

    /**
     * 页面返回
     */
    protected void onBackPressed() {
        if (menu_input.onBackPressed()) {
            finish();
        }
    }

    /**
     * 设置开始问诊
     */
    protected void setInquiryStarted() {
        mIsClosed = false;

        if (mOnInquiryListener != null) {
            mOnInquiryListener.onStartInquiry();
        }
    }

    /**
     * 设置关闭问诊
     */
    protected void setInquiryClosed() {
        mIsClosed = true;

        if (mOnInquiryListener != null) {
            mOnInquiryListener.onCloseInquiry();
        }
    }

    /**
     * 设置问诊的监听事件
     */
    protected void setOnInquiryListener(@Nullable EaseOnInquiryListener listener) {
        mOnInquiryListener = listener;
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
            list_message.refreshSelectLast();
        }
    }

    /**
     * 消息状态回调
     */
    private EMCallBack mMessageStatusCallback = new EMCallBack() {

        @Override
        public void onSuccess() {
            if (mIsMessageInit) {
                list_message.refresh();
            }
        }

        @Override
        public void onError(int code, String error) {
            if (mIsMessageInit) {
                list_message.refresh();
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            if (mIsMessageInit) {
                list_message.refresh();
            }
        }

    };

    /**
     * 清空所有聊天消息
     */
    protected void clearAllMessages() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getContext(), null, msg, null, (confirmed, bundle) -> {
            if (confirmed) {
                if (mConversation != null) {
                    mConversation.clearAllMessages();
                }

                list_message.refresh();
                mHaveMoreData = true;
            }
        }, true).show();
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

        //刷新消息列表
        if (mIsMessageInit) {
            list_message.refreshSelectLast();
        }

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

        //刷新消息列表
        if (mIsMessageInit) {
            list_message.refreshSelectLast();
        }

        //结束问诊
        setInquiryClosed();
        setCloseInquiryView();
        hideSoftKeyboard();
    }

    /**
     * 设置问诊开始的控件
     */
    protected void setStartInquiryView() {
        tv_availableCount.setVisibility(View.VISIBLE);
        menu_input.setVisibility(View.VISIBLE);
    }

    /**
     * 设置问诊关闭的控件
     */
    protected void setCloseInquiryView() {
        tv_availableCount.setVisibility(View.GONE);
        menu_input.setVisibility(View.GONE);
    }

    /**
     * 处理最新消息的动作
     */
    protected void handleLastMessageAction(EMMessage message) {
        if (message == null) {
            return;
        }

        String action = EaseMessageUtil.getAction(message);

        if (action == null) {
            return;
        }

        if (EaseMessageUtil.ACTION_START_INQUIRY.equals(action)) {//开始问诊
            setInquiryStarted();
            setStartInquiryView();

        } else if (EaseMessageUtil.ACTION_CLOSE_INQUIRY.equals(action)) {//结束问诊
            setInquiryClosed();
            setCloseInquiryView();
            hideSoftKeyboard();
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
                        list_message.refreshSelectLast();
                        mConversation.markMessageAsRead(message.getMsgId());
                    }
                }

                //处理最新消息的Action
                EMMessage lastMessage = messages.get(messages.size() - 1);

                handleLastMessageAction(lastMessage);
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
//            runOnUiThread(() -> {
//                for (EMMessage msg : messages) {
//                    EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();
//
//                    if (msg.getFrom().equals(mToUser.getUsername())) {
//                        if (CMD_START_INQUIRY.equals(body.action())) {
//                            setInquiryStarted();
//                            setStartInquiryView();
//
//                        } else if (CMD_CLOSE_INQUIRY.equals(body.action())) {
//                            setInquiryClosed();
//                            setCloseInquiryView();
//                            hideSoftKeyboard();
//                        }
//                    }
//                }
//            });
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            if (mIsMessageInit) {
                runOnUiThread(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            if (mIsMessageInit) {
                runOnUiThread(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            if (mIsMessageInit) {
                runOnUiThread(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            if (mIsMessageInit) {
                runOnUiThread(() -> list_message.refresh());
            }
        }

    };

    /**
     * 设置消息
     */
    protected EaseInquiryMessageProvider onSetMessageRow() {
        return null;
    }

    /**
     * 设置输入菜单
     */
    protected EaseInquiryInputMenuProvider onSetInputMenu() {
        return null;
    }

}