package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

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
import com.hyphenate.easeui.model.EaseDingMessageHelper;
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

    protected static final String TAG = "EaseChatFragment";

    private static final String EXTRA_CHAT_ABLE = "EXTRA_CHAT_ABLE";

    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;

    protected static final int MSG_TYPING_BEGIN = 0;
    protected static final int MSG_TYPING_END = 1;

    protected static final String ACTION_TYPING_BEGIN = "TypingBegin";
    protected static final String ACTION_TYPING_END = "TypingEnd";
    protected static final String ACTION_CLOSE_CONVERSATION = "cmd_close_conversation";

    protected static final int TYPING_SHOW_TIME = 5000;

    private EaseToolbar toolbar;
    private EaseChatMessageList list_message;
    private EaseChatInputMenu input_menu;
    private EaseVoiceRecorderView voice_recorder;
    private View layout_alert_kicked_off;
    private MenuItem menuItem;

    private int mChatType;//聊天类型
    private String mToUsername;//对方username
    private boolean mShowUserNick;//是否显示昵称
    private boolean mTurnOnTyping;//"正在输入"功能的开关，打开后本设备发送消息将持续发送cmd类型消息通知对方"正在输入"
    private boolean mIsRoaming;//是否漫游
    private String mForwardMsgId;//发送这条消息
    private boolean mChatable;//是否可以聊天

    private ExecutorService mFetchQueue;
    private EMConversation mConversation;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case MSG_TYPING_BEGIN: {// Notify typing start
                    if (!mTurnOnTyping) {
                        return;
                    }

                    if (mChatType != EaseConstant.CHATTYPE_SINGLE) {
                        return;
                    }

                    if (hasMessages(MSG_TYPING_END)) {
                        removeMessages(MSG_TYPING_END);

                    } else {
                        // Send TYPING-BEGIN cmd msg
                        EMMessage beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
                        EMCmdMessageBody body = new EMCmdMessageBody(ACTION_TYPING_BEGIN);
                        // Only deliver this cmd msg to online users
                        body.deliverOnlineOnly(true);
                        beginMsg.addBody(body);
                        beginMsg.setTo(mToUsername);
                        EMClient.getInstance().chatManager().sendMessage(beginMsg);
                    }

                    sendEmptyMessageDelayed(MSG_TYPING_END, TYPING_SHOW_TIME);
                }
                break;

                case MSG_TYPING_END: {
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

                default:
                    break;

            }
        }

    };

    protected Handler handler = new Handler();
    protected File cameraFile;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected GroupListener groupListener;
    protected ChatRoomListener chatRoomListener;
    protected EMMessage contextMenuMessage;

    private boolean isMessageListInited;

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
            mChatable = bundle.getBoolean(EXTRA_CHAT_ABLE, true);
        }

        if (mIsRoaming) {
            mFetchQueue = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        voice_recorder = view.findViewById(R.id.voice_recorder);
        list_message = view.findViewById(R.id.list_message);
        layout_alert_kicked_off = view.findViewById(R.id.layout_alert_kicked_off);
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
            groupListener = new GroupListener();
            EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);

        } else if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            chatRoomListener = new ChatRoomListener();
            EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener);

            onChatRoomViewCreation();
        }

        if (mChatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        list_message.setShowUserNick(mShowUserNick);
        list_message.getSwipeRefreshLayout().setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.getSwipeRefreshLayout().setOnRefreshListener(() -> handler.postDelayed(() -> {
            if (!mIsRoaming) {
                loadMoreLocalMessage();

            } else {
                loadMoreRoamingMessages();
            }
        }, 600));

        layout_alert_kicked_off.setOnClickListener(v -> onChatRoomViewCreation());

        input_menu.addExtendMenuItem(R.drawable.ease_chat_takepic_selector, "拍照", v -> requestPermission(data -> selectPicFromCamera(), Permission.Group.CAMERA, Permission.Group.STORAGE));
        input_menu.addExtendMenuItem(R.drawable.ease_chat_image_selector, "相册", v -> requestPermission(data -> selectPicFromLocal(), Permission.Group.CAMERA, Permission.Group.STORAGE));
//        input_menu.addExtendMenuItem(R.drawable.ease_chat_location_selector, "定位", v -> requestPermission(data -> startActivityForResult(EaseBaiduMapActivity.buildIntent(getContext()), REQUEST_CODE_MAP), Permission.Group.LOCATION));
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

        input_menu.setVisibility(mChatable ? View.VISIBLE : View.GONE);
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
        if (isMessageListInited) {
            list_message.refresh();
        }

        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(mToUsername);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(MSG_TYPING_END);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);

        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }

        if (chatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener);
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
                        EMClient.getInstance().chatManager().fetchHistoryMessages(mToUsername, EaseCommonUtils.getConversationType(mChatType), pagesize, "");
                        final List<EMMessage> msgs = mConversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < mConversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            mConversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
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
            if (msgCount < mConversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                mConversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
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
            menuItem.setIcon(R.drawable.ease_mm_title_remove);
            menuItem.setTitle("清空");
            menuItem.setOnMenuItemClickListener(item -> {
                emptyHistory();
                return true;
            });
            menuItem.setVisible(mChatable);

        } else {
            menuItem.setIcon(R.drawable.ease_to_group_details_normal);
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
        list_message.init(mToUsername, mChatType, chatFragmentHelper != null ? chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

        list_message.getListView().setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            input_menu.hideExtendMenuContainer();
            return false;
        });

        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        list_message.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(username);
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
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;

                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
                    return false;
                }

                return chatFragmentHelper.onMessageBubbleClick(message);
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(messageStatusCallback);
            }

        });
    }

    private void loadMoreLocalMessage() {
        if (list_message.getListView().getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages = mConversation.loadMoreMsgFromDB(mConversation.getAllMessages().size() == 0 ? "" : mConversation.getAllMessages().get(0).getMsgId(), pagesize);

            } catch (Exception e1) {
                list_message.getSwipeRefreshLayout().setRefreshing(false);
                return;
            }

            if (messages.size() > 0) {
                list_message.refreshSeekTo(messages.size() - 1);
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }

            } else {
                haveMoreData = false;
            }

            isloading = false;

        } else {
            EaseToastUtil.show(R.string.no_more_messages);
        }

        list_message.getSwipeRefreshLayout().setRefreshing(false);
    }

    private void loadMoreRoamingMessages() {
        if (!haveMoreData) {
            EaseToastUtil.show(R.string.no_more_messages);
            list_message.getSwipeRefreshLayout().setRefreshing(false);
            return;
        }

        if (mFetchQueue != null) {
            mFetchQueue.execute(() -> {
                try {
                    List<EMMessage> messages = mConversation.getAllMessages();
                    EMClient.getInstance().chatManager().fetchHistoryMessages(mToUsername, EaseCommonUtils.getConversationType(mChatType), pagesize, (messages != null && messages.size() > 0) ? messages.get(0).getMsgId() : "");

                } catch (HyphenateException e) {
                    e.printStackTrace();

                } finally {
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(() -> loadMoreLocalMessage());
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CODE_CAMERA: {
                if (resultCode == Activity.RESULT_OK) {
                    if (cameraFile != null && cameraFile.exists()) {
                        sendImageMessage(cameraFile.getAbsolutePath());
                    }
                }
            }
            break;

            case REQUEST_CODE_LOCAL: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }
                    }
                }
            }
            break;

            case REQUEST_CODE_MAP: {
                if (resultCode == Activity.RESULT_OK) {
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    if (locationAddress != null && !locationAddress.equals("")) {
                        sendLocationMessage(latitude, longitude, locationAddress);
                    } else {
                        EaseToastUtil.show(R.string.unable_to_get_loaction);
                    }
                }
            }
            break;

            case REQUEST_CODE_DING_MSG: {
                if (resultCode == Activity.RESULT_OK) {
                    String msgContent = data.getStringExtra("msg");
                    EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(mToUsername, msgContent);
                    sendMessage(dingMsg);
                }
            }
            break;

            default:
                break;

        }
    }

    public void onBackPressed() {
        if (input_menu.onBackPressed()) {
            getActivity().finish();

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
                getActivity().runOnUiThread(() -> {
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

                    layout_alert_kicked_off.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                getActivity().runOnUiThread(() -> pd.dismiss());
                getActivity().finish();
            }

        });
    }

    /**
     * input @
     *
     * @param username
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
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }

    //send message
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, mToUsername);
            sendMessage(message);
        }
    }

    /**
     * send @ message, only support group chat message
     *
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (mChatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, mToUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(mToUsername);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);
    }

    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(mToUsername, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mToUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, mToUsername);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, mToUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, mToUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, mToUsername);
        sendMessage(message);
    }

    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            chatFragmentHelper.onSetMessageAttributes(message);
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

        message.setMessageStatusCallback(messageStatusCallback);

        // Send message.
        EMClient.getInstance().chatManager().sendMessage(message);

        //refresh ui
        if (isMessageListInited) {
            list_message.refreshSelectLast();
        }
    }

    protected EMCallBack messageStatusCallback = new EMCallBack() {

        @Override
        public void onSuccess() {
            if (isMessageListInited) {
                list_message.refresh();
            }
        }

        @Override
        public void onError(int code, String error) {
            if (isMessageListInited) {
                list_message.refresh();
            }
        }

        @Override
        public void onProgress(int progress, String status) {
            if (isMessageListInited) {
                list_message.refresh();
            }
        }
    };

    /**
     * send image
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
     * send file
     *
     * @param uri
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
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            EaseToastUtil.show(R.string.sd_card_does_not_exist);
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser() + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)), REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * clear the conversation history
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, (confirmed, bundle) -> {
            if (confirmed) {
                if (mConversation != null) {
                    mConversation.clearAllMessages();
                }
                list_message.refresh();
                haveMoreData = true;
            }
        }, true).show();
    }

    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(mToUsername);
            if (group == null) {
                EaseToastUtil.show(R.string.gorup_not_found);
                return;
            }

            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }

        } else if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    private void setChatableView() {
        if (input_menu != null) {
            input_menu.setVisibility(mChatable ? View.VISIBLE : View.GONE);
        }

        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {//单聊
            if (menuItem != null) {
                menuItem.setVisible(mChatable);
            }
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
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
     * listen chat room event
     */
    class ChatRoomListener extends EaseChatRoomListener {

        @Override
        public void onChatRoomDestroyed(final String roomId, final String roomName) {
            getActivity().runOnUiThread(() -> {
                if (roomId.equals(mToUsername)) {
                    EaseToastUtil.show(R.string.the_current_chat_room_destroyed);
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                }
            });
        }

        @Override
        public void onRemovedFromChatRoom(final int reason, final String roomId, final String roomName, final String participant) {
            getActivity().runOnUiThread(() -> {
                if (roomId.equals(mToUsername)) {
                    if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                        EaseToastUtil.show(R.string.quiting_the_chat_room);
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    } else { // BE_KICKED_FOR_OFFLINE
                        EaseToastUtil.show("User be kicked for offline");
                        layout_alert_kicked_off.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        @Override
        public void onMemberJoined(final String roomId, final String participant) {
            if (roomId.equals(mToUsername)) {
                getActivity().runOnUiThread(() -> EaseToastUtil.show("member join:" + participant));
            }
        }

        @Override
        public void onMemberExited(final String roomId, final String roomName, final String participant) {
            if (roomId.equals(mToUsername)) {
                getActivity().runOnUiThread(() -> EaseToastUtil.show("member exit:" + participant));
            }
        }

    }

    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
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
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         *
         * @param username
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
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
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

        public Builder setChatable(boolean chatable) {
            mBundle.putBoolean(EXTRA_CHAT_ABLE, chatable);
            return this;
        }

        public EaseChatFragment create() {
            EaseChatFragment fragment = new EaseChatFragment();
            fragment.setArguments(mBundle);
            return fragment;
        }

    }

    private final EMMessageListener mMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            getActivity().runOnUiThread(() -> {
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

                getActivity().runOnUiThread(() -> {
                    if (ACTION_TYPING_BEGIN.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        toolbar.setTitle(getString(R.string.alert_during_typing));

                    } else if (ACTION_TYPING_END.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        toolbar.setTitle(mToUsername);

                    } else if (ACTION_CLOSE_CONVERSATION.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        mChatable = false;

                        setChatableView();
                    }
                });
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            if (isMessageListInited) {
                getActivity().runOnUiThread(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            if (isMessageListInited) {
                getActivity().runOnUiThread(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            if (isMessageListInited) {
                getActivity().runOnUiThread(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            if (isMessageListInited) {
                getActivity().runOnUiThread(() -> list_message.refresh());
            }
        }

    };

    /**
     * listen the group event
     */
    private final class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            getActivity().runOnUiThread(() -> {
                if (mToUsername.equals(groupId)) {
                    EaseToastUtil.show(R.string.you_are_group);
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            getActivity().runOnUiThread(() -> {
                if (mToUsername.equals(groupId)) {
                    EaseToastUtil.show(R.string.the_current_group_destroyed);
                    Activity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                }
            });
        }

    }

}