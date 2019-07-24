package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseContactUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseToolbar;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.PathUtil;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EaseChatFragment extends EaseBaseFragment {

    private static final String EXTRA_CHAT_ENABLED = "EXTRA_CHAT_ENABLED";//是否具备聊天功能
    private static final String EXTRA_FINISH_CONVERSATION_ENABLED = "EXTRA_FINISH_CONVERSATION_ENABLED";//是否具备结束问诊功能
    private static final String EXTRA_LOCATION_ENABLED = "EXTRA_LOCATION_ENABLED";//是否具备定位功能

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_ALBUM = 3;

    //handle消息类型
    private static final int MSG_TYPING_BEGIN = 0;
    private static final int MSG_TYPING_END = 1;
    private static final int MSG_FINISH_CONVERSATION = 2;

    //透传类型
    private static final String ACTION_TYPING_BEGIN = "TypingBegin";
    private static final String ACTION_TYPING_END = "TypingEnd";
    private static final String ACTION_CLOSE_CONVERSATION = "cmd_close_conversation";

    private static final int TYPING_SHOW_TIME = 5000;//显示正在输入的时长

    private EaseToolbar toolbar;
    private TextView tv_offline;
    private EaseChatMessageList list_message;
    private EaseChatInputMenu input_menu;
    private EaseVoiceRecorderView voice_recorder;
    private MenuItem menuItem;

    private int mChatType;//聊天类型
    private String mToUsername;//对方username
    private boolean mShowUserNick;//是否显示昵称
    private boolean mTurnOnTyping;//"正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
    private boolean mIsRoaming;//是否漫游
    private String mForwardMsgId;//发送这条消息
    private boolean mChatEnabled;//是否可以聊天
    private boolean mFinishConversationEnabled;//是否可以结束问诊
    private boolean mLocatinEnable;//是否可以定位

    private ExecutorService mFetchQueue;
    private EMConversation mConversation;

    private File mCameraFile;
    private boolean mIsMessagesInited;//消息列表是否已初始化
    private EaseChatFragmentHelper mChatFragmentHelper;

    private GroupListener mGroupListener;
    private ChatRoomListener mChatRoomListener;

    protected int mPagesize = 20;
    protected boolean mHaveMoreData = true;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case MSG_TYPING_BEGIN: {//正在输入...
                    if (!mTurnOnTyping) {
                        return;
                    }

                    if (mChatType != EaseConstant.CHATTYPE_SINGLE) {
                        return;
                    }

                    if (hasMessages(MSG_TYPING_END)) {
                        removeMessages(MSG_TYPING_END);

                    } else {
                        EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                        EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_BEGIN);
                        body.deliverOnlineOnly(true);
                        beginMsg.addBody(body);
                        beginMsg.setTo(mToUsername);
                        EMClient.getInstance().chatManager().sendMessage(beginMsg);
                    }

                    sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
                }
                break;

                case MSG_TYPING_END: {//输入结束...
                    if (!mTurnOnTyping) {
                        return;
                    }

                    if (mChatType != EaseConstant.CHATTYPE_SINGLE) {
                        return;
                    }

                    removeCallbacksAndMessages(null);
                    EMMessage endMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                    EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_END);
                    body.deliverOnlineOnly(true);
                    endMsg.addBody(body);
                    endMsg.setTo(mToUsername);
                    EMClient.getInstance().chatManager().sendMessage(endMsg);
                }
                break;

                case MSG_FINISH_CONVERSATION: {//结束聊天
                    //透传发送结束聊天的消息
                    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
                    EMCmdMessageBody body = new EMCmdMessageBody(ACTION_CLOSE_CONVERSATION);
                    message.addBody(body);
                    message.setTo(mToUsername);
                    EMClient.getInstance().chatManager().sendMessage(message);

                    //新增一条文本消息到本地数据库
                    if (mConversation != null) {
                        EMMessage textMessage = EMMessage.createTxtSendMessage("本次问诊已结束", mToUsername);
                        textMessage.setAttribute(EaseConstant.MESSAGE_ATTR_FINISH_CONVERSATION, true);
                        mConversation.insertMessage(textMessage);
                    }

                    //刷新消息列表
                    if (mIsMessagesInited) {
                        list_message.refreshSelectLast();
                    }
                }
                break;

                default:
                    break;

            }
        }

    };

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_fragment_chat;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mChatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            mToUsername = bundle.getString(EaseConstant.EXTRA_TO_USERNAME);
            mTurnOnTyping = bundle.getBoolean(EaseConstant.EXTRA_TURN_ON_TYPING, false);
            mShowUserNick = bundle.getBoolean(EaseConstant.EXTRA_SHOW_NICKNAME, true);
            mIsRoaming = bundle.getBoolean(EaseConstant.EXTRA_IS_ROAMING, false);
            mForwardMsgId = bundle.getString(EaseConstant.EXTRA_FORWARD_MSG_ID);
            mChatEnabled = bundle.getBoolean(EXTRA_CHAT_ENABLED, true);
            mFinishConversationEnabled = bundle.getBoolean(EXTRA_FINISH_CONVERSATION_ENABLED, false);
            mLocatinEnable = bundle.getBoolean(EXTRA_LOCATION_ENABLED, false);
        }

        if (mIsRoaming) {
            mFetchQueue = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        tv_offline = view.findViewById(R.id.tv_offline);
        voice_recorder = view.findViewById(R.id.voice_recorder);
        list_message = view.findViewById(R.id.list_message);
        input_menu = view.findViewById(R.id.input_menu);
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
        setTitle();
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            mGroupListener = new GroupListener();
            EMClient.getInstance().groupManager().addGroupChangeListener(mGroupListener);

        } else if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            mChatRoomListener = new ChatRoomListener();
            EMClient.getInstance().chatroomManager().addChatRoomChangeListener(mChatRoomListener);

            onChatRoomViewCreation();
        }

        if (mChatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        //离线提示
        tv_offline.setOnClickListener(v -> onChatRoomViewCreation());

        //设置消息列表
        list_message.setShowUserNick(mShowUserNick);
        list_message.getSwipeRefreshLayout().setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.getSwipeRefreshLayout().setOnRefreshListener(() -> mHandler.postDelayed(() -> loadMoreMessages(), 600));

        //设置功能菜单
        input_menu.setVisibility(mChatEnabled ? View.VISIBLE : View.GONE);
        input_menu.addExtendMenuItem(R.mipmap.ease_ic_camera, "拍照", v -> requestPermission(data -> pickPhotoFromCamera(), Permission.Group.CAMERA, Permission.Group.STORAGE));
        input_menu.addExtendMenuItem(R.mipmap.ease_ic_album, "相册", v -> requestPermission(data -> pickPhotoFromAlbum(), Permission.Group.CAMERA, Permission.Group.STORAGE));
        if (mLocatinEnable) {
            input_menu.addExtendMenuItem(R.mipmap.ease_ic_location, "定位", v -> {
                Intent intent = new Intent(getContext(), EaseBaiduMapActivity.class);

                startActivityForResult(intent, REQUEST_LOCATION);
            });
        }
        if (mFinishConversationEnabled) {
            input_menu.addExtendMenuItem(R.mipmap.ease_ic_finish, "结束", v -> mHandler.sendEmptyMessage(MSG_FINISH_CONVERSATION));
        }
        input_menu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                mHandler.sendEmptyMessage(MSG_TYPING_BEGIN);
            }

            @Override
            public void onSendMessage(String content) {
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
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }

        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);

        if (mForwardMsgId != null) {
            forwardMessage(mForwardMsgId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsMessagesInited) {
            list_message.refresh();
        }

        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(mToUsername);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.sendEmptyMessage(MSG_TYPING_END);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);

        if (mGroupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupListener);
        }

        if (mChatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(mChatRoomListener);
        }

        if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(mToUsername);
        }
    }

    protected void onConversationInit() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToUsername, EaseCommonUtils.getConversationType(mChatType), true);
        mConversation.markAllMessagesAsRead();

        if (mIsRoaming) {
            if (mFetchQueue != null) {
                mFetchQueue.execute(() -> {
                    try {
                        EMClient.getInstance().chatManager().fetchHistoryMessages(mToUsername, EaseCommonUtils.getConversationType(mChatType), mPagesize, "");
                        final List<EMMessage> msgs = mConversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            mConversation.loadMoreMsgFromDB(msgId, mPagesize - msgCount);
                        }
                        list_message.refreshSelectLast();

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                });
            }

        } else {
            final List<EMMessage> msgs = mConversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < mConversation.getAllMsgCount() && msgCount < mPagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                mConversation.loadMoreMsgFromDB(msgId, mPagesize - msgCount);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ease_menu_chat, menu);
        menuItem = menu.findItem(R.id.ease_action);

        //设置右边按钮
        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {//单聊
            menuItem.setIcon(R.mipmap.ease_ic_clear);
            menuItem.setTitle("清空");
            menuItem.setOnMenuItemClickListener(item -> {
                emptyHistory();
                return true;
            });
            menuItem.setVisible(mChatEnabled);

        } else {
            menuItem.setIcon(R.mipmap.ease_ic_group);
            menuItem.setTitle("成员列表");
            menuItem.setOnMenuItemClickListener(item -> {
                toGroupDetails();
                return true;
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                onBackPressed();
            }
            break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        String title = mToUsername;

        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
            EaseUser user = EaseUserUtils.getUserInfo(mToUsername);
            if (user != null) {
                title = user.getNickname();
            }

        } else if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(mToUsername);
            if (group != null) {
                title = group.getGroupName();
            }
        }

        toolbar.setTitle(title);
    }

    protected void onMessageListInit() {
        list_message.init(mToUsername, mChatType, mChatFragmentHelper != null ? mChatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

        list_message.getListView().setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            input_menu.hideExtendMenuContainer();
            return false;
        });

        mIsMessagesInited = true;
    }

    protected void setListItemClickListener() {
        list_message.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (mChatFragmentHelper != null) {
                    mChatFragmentHelper.onAvatarClick(username);
                }
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
                if (mChatFragmentHelper != null) {
                    mChatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                if (mChatFragmentHelper != null) {
                    mChatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (mChatFragmentHelper == null) {
                    return false;
                }

                return mChatFragmentHelper.onMessageBubbleClick(message);
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(mMessageStatusCallback);
            }

        });
    }

    /**
     * 加载更多消息列表
     */
    private void loadMoreMessages() {
        if (mIsRoaming) {
            loadMoreRoamingMessages();

        } else {
            loadMoreLocalMessages();
        }
    }

    /**
     * 加载本地更多消息列表
     */
    private void loadMoreLocalMessages() {
        if (list_message.getListView().getFirstVisiblePosition() == 0 && mHaveMoreData) {
            List<EMMessage> messages;

            try {
                messages = mConversation.loadMoreMsgFromDB(mConversation.getAllMessages().size() == 0 ? "" : mConversation.getAllMessages().get(0).getMsgId(), mPagesize);

            } catch (Exception e) {
                list_message.getSwipeRefreshLayout().setRefreshing(false);
                return;
            }

            if (messages.size() > 0) {
                list_message.refreshSeekTo(messages.size() - 1);

                if (messages.size() != mPagesize) {
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
     * 加载更多漫游消息列表
     */
    private void loadMoreRoamingMessages() {
        if (!mHaveMoreData) {
            EaseToastUtil.show(R.string.no_more_messages);
            list_message.getSwipeRefreshLayout().setRefreshing(false);
            return;
        }

        if (mFetchQueue != null) {
            mFetchQueue.execute(() -> {
                try {
                    List<EMMessage> messages = mConversation.getAllMessages();
                    EMClient.getInstance().chatManager().fetchHistoryMessages(mToUsername, EaseCommonUtils.getConversationType(mChatType), mPagesize, (messages != null && messages.size() > 0) ? messages.get(0).getMsgId() : "");

                } catch (HyphenateException e) {
                    e.printStackTrace();

                } finally {
                    mHandler.post(() -> loadMoreLocalMessages());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CAMERA: {
                if (resultCode == Activity.RESULT_OK) {
                    if (mCameraFile != null && mCameraFile.exists()) {
                        sendImageMessage(mCameraFile.getAbsolutePath());
                    }
                }
            }
            break;

            case REQUEST_ALBUM: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();

                        if (uri != null) {
                            sendPicByUri(uri);
                        }
                    }
                }
            }
            break;

            case REQUEST_LOCATION: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        String address = data.getStringExtra("address");

                        if (!TextUtils.isEmpty(address)) {
                            sendLocationMessage(latitude, longitude, address);

                        } else {
                            EaseToastUtil.show(R.string.unable_to_get_loaction);
                        }
                    }
                }
            }
            break;

            default:
                break;

        }
    }

    public void onBackPressed() {
        if (input_menu.onBackPressed()) {
            finish();

            if (mChatType == EaseConstant.CHATTYPE_GROUP) {
                EaseAtMessageHelper.get().removeAtMeGroup(mToUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }

            if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(mToUsername);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");

        EMClient.getInstance().chatroomManager().joinChatRoom(mToUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                mHandler.post(() -> {
                    if (getActivity().isFinishing() || !mToUsername.equals(value.getId())) {
                        return;
                    }

                    pd.dismiss();

                    EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(mToUsername);

                    if (room != null) {
                        toolbar.setTitle(room.getName());

                    } else {
                        toolbar.setTitle(mToUsername);
                    }

                    onConversationInit();
                    onMessageListInit();
                    tv_offline.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                mHandler.post(() -> pd.dismiss());

                finish();
            }

        });
    }

    /**
     * 输入@
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) || mChatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }

        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null) {
            username = user.getNickname();
        }

        input_menu.insertText((autoAddAtSymbol ? "@" : "") + username + " ");
    }

    /**
     * 输入@
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }

    /**
     * 发送文字消息
     */
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);

        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, mToUsername);
            sendMessage(message);
        }
    }

    /**
     * 发送@文字消息, 仅支持群聊
     */
    @SuppressWarnings("ConstantConditions")
    protected void sendAtMessage(String content) {
        if (mChatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }

        EMMessage message = EMMessage.createTxtSendMessage(content, mToUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(mToUsername);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);

        } else {
            List<String> atMessageUsernames = EaseAtMessageHelper.get().getAtMessageUsernames(content);

            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseAtMessageHelper.get().atListToJsonArray(atMessageUsernames));
        }

        sendMessage(message);
    }

    /**
     * 发送大表情消息
     */
    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(mToUsername, name, identityCode);
        sendMessage(message);
    }

    /**
     * 发送语音消息
     */
    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mToUsername);
        sendMessage(message);
    }

    /**
     * 发送图片消息
     */
    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, mToUsername);
        sendMessage(message);
    }

    /**
     * 发送定位消息
     */
    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, mToUsername);
        sendMessage(message);
    }

    /**
     * 发送视频消息
     */
    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, mToUsername);
        sendMessage(message);
    }

    /**
     * 发送文件消息
     */
    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, mToUsername);
        sendMessage(message);
    }

    /**
     * 发送消息
     */
    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }

        if (mChatFragmentHelper != null) {
            mChatFragmentHelper.onSetMessageAttributes(message);
        }

        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
            message.setChatType(ChatType.Chat);

            EaseUser myUser = EaseUserUtils.getUserInfo(EMClient.getInstance().getCurrentUser());
            EaseUser toUser = EaseUserUtils.getUserInfo(mToUsername);

            if (myUser != null && toUser != null) {
                message.setAttribute("send_nickname", myUser.getNickname());
                message.setAttribute("send_avatar", myUser.getAvatar());
                message.setAttribute("to_nickname", toUser.getNickname());
                message.setAttribute("to_avatar", toUser.getAvatar());
            }

        } else if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);

        } else if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }

        message.setMessageStatusCallback(mMessageStatusCallback);

        // Send message.
        EMClient.getInstance().chatManager().sendMessage(message);

        //refresh ui
        if (mIsMessagesInited) {
            list_message.refreshSelectLast();
        }
    }

    protected EMCallBack mMessageStatusCallback = new EMCallBack() {

        @Override
        public void onSuccess() {
            if (mIsMessagesInited) {
                list_message.refresh();
            }
        }

        @Override
        public void onError(int code, String error) {
            if (mIsMessagesInited) {
                list_message.refresh();
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            if (mIsMessagesInited) {
                list_message.refresh();
            }
        }

    };

    /**
     * 发送图片
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                EaseToastUtil.show(R.string.cant_find_pictures, Gravity.CENTER);
                return;
            }
            sendImageMessage(picturePath);

        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                EaseToastUtil.show(R.string.cant_find_pictures, Gravity.CENTER);
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }
    }

    /**
     * 发送文件
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = EaseCompat.getPath(getActivity(), uri);
        if (filePath == null) {
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            EaseToastUtil.show(R.string.File_does_not_exist);
            return;
        }

        sendFileMessage(filePath);
    }

    /**
     * 拍照获取照片
     */
    private void pickPhotoFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            EaseToastUtil.show(R.string.sd_card_does_not_exist);
            return;
        }

        mCameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser() + System.currentTimeMillis() + ".jpg");
        mCameraFile.getParentFile().mkdirs();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), mCameraFile));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * 从相册中选择照片
     */
    private void pickPhotoFromAlbum() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        startActivityForResult(intent, REQUEST_ALBUM);
    }

    /**
     * 空的历史记录
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, (confirmed, bundle) -> {
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
     * 打开群详情
     */
    protected void toGroupDetails() {
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(mToUsername);
            if (group == null) {
                EaseToastUtil.show(R.string.gorup_not_found);
                return;
            }

            if (mChatFragmentHelper != null) {
                mChatFragmentHelper.onEnterToChatDetails();
            }

        } else if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (mChatFragmentHelper != null) {
                mChatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    private void setChatableView() {
        if (input_menu != null) {
            input_menu.setVisibility(mChatEnabled ? View.VISIBLE : View.GONE);
        }

        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {//单聊
            if (menuItem != null) {
                menuItem.setVisible(mChatEnabled);
            }
        }
    }

    /**
     * 转发消息
     */
    protected void forwardMessage(String msgId) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(msgId);
        EMMessage.Type type = forward_msg.getType();

        switch (type) {

            case TXT: {
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
            }
            break;

            case IMAGE: {
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
            }
            break;

            default:
                break;
        }

        if (forward_msg.getChatType() == ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * 聊天室回调
     */
    class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(final String roomId, final String roomName) {
            mHandler.post(() -> {
                if (roomId.equals(mToUsername)) {
                    EaseToastUtil.show(R.string.the_current_chat_room_destroyed);

                    finish();
                }
            });
        }

        @Override
        public void onRemovedFromChatRoom(final int reason, final String roomId, final String roomName, final String participant) {
            mHandler.post(() -> {
                if (roomId.equals(mToUsername)) {
                    if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                        EaseToastUtil.show(R.string.quiting_the_chat_room);

                        finish();

                    } else {//BE_KICKED_FOR_OFFLINE
                        EaseToastUtil.show("User be kicked for offline");

                        tv_offline.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        @Override
        public void onMemberJoined(final String roomId, final String participant) {
            if (roomId.equals(mToUsername)) {
                mHandler.post(() -> EaseToastUtil.show("member join:" + participant));
            }
        }

        @Override
        public void onMemberExited(final String roomId, final String roomName, final String participant) {
            if (roomId.equals(mToUsername)) {
                mHandler.post(() -> EaseToastUtil.show("member exit:" + participant));
            }
        }

    }

    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        this.mChatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {

        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();

    }

    /**
     * 消息回调
     */
    private final EMMessageListener mMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            mHandler.post(() -> {
                if (messages.size() > 0) {
                    EMMessage message = messages.get(messages.size() - 1);

                    if (message.getChatType() == ChatType.Chat) {
                        EaseContactUtil.getInstance().saveContact(message);

                        setTitle();
                    }
                }

                for (EMMessage message : messages) {
                    String username = null;
                    if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                        username = message.getTo();

                    } else {
                        username = message.getFrom();
                    }

                    if (username.equals(mToUsername) || message.getTo().equals(mToUsername) || message.conversationId().equals(mToUsername)) {
                        list_message.refreshSelectLast();
                        mConversation.markMessageAsRead(message.getMsgId());
                    }
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            for (final EMMessage msg : messages) {
                final EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();

                mHandler.post(() -> {
                    if (ACTION_TYPING_BEGIN.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        toolbar.setTitle(getString(R.string.alert_during_typing));

                    } else if (ACTION_TYPING_END.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        toolbar.setTitle(mToUsername);

                    } else if (ACTION_CLOSE_CONVERSATION.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        mChatEnabled = false;

                        setChatableView();
                    }
                });
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            if (mIsMessagesInited) {
                mHandler.post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            if (mIsMessagesInited) {
                mHandler.post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            if (mIsMessagesInited) {
                mHandler.post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            if (mIsMessagesInited) {
                mHandler.post(() -> list_message.refresh());
            }
        }

    };

    /**
     * 群回调
     */
    private final class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            mHandler.post(() -> {
                if (TextUtils.equals(mToUsername, groupId)) {
                    EaseToastUtil.show(R.string.you_are_group);

                    finish();
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            mHandler.post(() -> {
                if (TextUtils.equals(mToUsername, groupId)) {
                    EaseToastUtil.show(R.string.the_current_group_destroyed);

                    finish();
                }
            });
        }

    }

    public static class Builder {

        private Bundle mBundle;

        public Builder() {
            mBundle = new Bundle();
        }

        public Builder setChatType(int chatType) {
            mBundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
            return this;
        }

        public Builder setToUser(String username) {
            mBundle.putString(EaseConstant.EXTRA_TO_USERNAME, username);
            return this;
        }

        public Builder setTurnOnTyping(boolean turnOnTyping) {
            mBundle.putBoolean(EaseConstant.EXTRA_TURN_ON_TYPING, turnOnTyping);
            return this;
        }

        public Builder setShowUserNick(boolean showUserNick) {
            mBundle.putBoolean(EaseConstant.EXTRA_SHOW_NICKNAME, showUserNick);
            return this;
        }

        public Builder setIsRoaming(boolean isRoaming) {
            mBundle.putBoolean(EaseConstant.EXTRA_IS_ROAMING, isRoaming);
            return this;
        }

        public Builder setForwardMsgId(String forwardMsgId) {
            mBundle.putString(EaseConstant.EXTRA_FORWARD_MSG_ID, forwardMsgId);
            return this;
        }

        public Builder setChatEnabled(boolean chatable) {
            mBundle.putBoolean(EXTRA_CHAT_ENABLED, chatable);
            return this;
        }

        public Builder setFinishConversationEnabled(boolean enabled) {
            mBundle.putBoolean(EXTRA_FINISH_CONVERSATION_ENABLED, enabled);
            return this;
        }

        public Builder setLocatinEnable(boolean enabled) {
            mBundle.putBoolean(EXTRA_LOCATION_ENABLED, enabled);
            return this;
        }

        public EaseChatFragment create() {
            EaseChatFragment fragment = new EaseChatFragment();
            fragment.setArguments(mBundle);
            return fragment;
        }

    }

}