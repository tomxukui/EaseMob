package com.hyphenate.easeui.module.inquiry.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.module.base.ui.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseContactUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.PathUtil;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;

/**
 * 问诊页面-单聊
 */
public class EaseInquiryFragment extends EaseBaseFragment {

    private static final String EXTRA_TO_USERNAME = "EXTRA_TO_USERNAME";//聊天对象
    private static final String EXTRA_CHAT_ENABLED = "EXTRA_CHAT_ENABLED";//是否具备聊天功能
    private static final String EXTRA_FINISH_CHAT_ENABLED = "EXTRA_FINISH_CHAT_ENABLED";//是否具备结束聊天功能

    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_ALBUM = 3;

    //handle消息类型
    private static final int MSG_FINISH_CONVERSATION = 2;

    //透传类型
    private static final String ACTION_CLOSE_CONVERSATION = "cmd_close_conversation";

    private EaseToolbar toolbar;
    private EaseChatMessageList list_message;
    private EaseChatInputMenu input_menu;
    private EaseVoiceRecorderView voice_recorder;
    private MenuItem menuItem;

    private String mToUsername;//对方username
    private boolean mChatEnabled;//是否可以聊天
    private boolean mFinishChatEnabled;//是否可以结束问诊

    private EMConversation mConversation;

    private File mCameraFile;
    private boolean mIsMessagesInited;//消息列表是否已初始化
    private EaseChatFragmentHelper mChatFragmentHelper;

    protected int mPagesize = 20;
    protected boolean mHaveMoreData = true;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

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

                    //关闭聊天功能
                    mChatEnabled = false;
                    setChatableView();
                }
                break;

                default:
                    break;

            }
        }

    };

    public static EaseInquiryFragment newInstance(String toUsername, boolean chatEnabled, boolean finishChatEnabled) {
        EaseInquiryFragment fragment = new EaseInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TO_USERNAME, toUsername);
        bundle.putBoolean(EXTRA_CHAT_ENABLED, chatEnabled);
        bundle.putBoolean(EXTRA_FINISH_CHAT_ENABLED, finishChatEnabled);
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
            mToUsername = bundle.getString(EXTRA_TO_USERNAME);
            mChatEnabled = bundle.getBoolean(EXTRA_CHAT_ENABLED, true);
            mFinishChatEnabled = bundle.getBoolean(EXTRA_FINISH_CHAT_ENABLED, false);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
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
        onConversationInit();
        onMessageListInit();

        //设置消息列表
        list_message.setShowUserNick(true);
        list_message.getSwipeRefreshLayout().setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.getSwipeRefreshLayout().setOnRefreshListener(() -> mHandler.postDelayed(() -> loadMoreLocalMessages(), 600));

        //设置功能菜单
        input_menu.setVisibility(mChatEnabled ? View.VISIBLE : View.GONE);
        input_menu.addExtendMenuItem(R.mipmap.ease_ic_camera, "拍照", v -> requestPermission(data -> pickPhotoFromCamera(), Permission.Group.CAMERA, Permission.Group.STORAGE));
        input_menu.addExtendMenuItem(R.mipmap.ease_ic_album, "相册", v -> requestPermission(data -> pickPhotoFromAlbum(), Permission.Group.CAMERA, Permission.Group.STORAGE));
        if (mFinishChatEnabled) {
            input_menu.addExtendMenuItem(R.mipmap.ease_ic_finish, "结束", v -> mHandler.sendEmptyMessage(MSG_FINISH_CONVERSATION));
        }
        input_menu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsMessagesInited) {
            list_message.refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    protected void onConversationInit() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToUsername, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);
        mConversation.markAllMessagesAsRead();

        List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPagesize - msgCount);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ease_menu_chat, menu);
        menuItem = menu.findItem(R.id.ease_action);

        //设置右边按钮
        menuItem.setIcon(R.mipmap.ease_ic_clear);
        menuItem.setTitle("清空");
        menuItem.setOnMenuItemClickListener(item -> {
            emptyHistory();
            return true;
        });
        menuItem.setVisible(mChatEnabled);
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

        EaseUser user = EaseUserUtils.getUserInfo(mToUsername);
        if (user != null) {
            title = user.getNickname();
        }

        toolbar.setTitle(title);
    }

    protected void onMessageListInit() {
        list_message.init(mToUsername, EaseConstant.CHATTYPE_SINGLE, mChatFragmentHelper != null ? mChatFragmentHelper.onSetCustomChatRowProvider() : null);
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

            default:
                break;

        }
    }

    public void onBackPressed() {
        if (input_menu.onBackPressed()) {
            finish();
        }
    }

    /**
     * 发送文字消息
     */
    protected void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToUsername);
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

        message.setChatType(ChatType.Chat);

        EaseUser myUser = EaseUserUtils.getUserInfo(EMClient.getInstance().getCurrentUser());
        EaseUser toUser = EaseUserUtils.getUserInfo(mToUsername);

        if (myUser != null && toUser != null) {
            message.setAttribute("send_nickname", myUser.getNickname());
            message.setAttribute("send_avatar", myUser.getAvatar());
            message.setAttribute("to_nickname", toUser.getNickname());
            message.setAttribute("to_avatar", toUser.getAvatar());
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

    private void setChatableView() {
        if (input_menu != null) {
            input_menu.setVisibility(mChatEnabled ? View.VISIBLE : View.GONE);
        }

        if (menuItem != null) {
            menuItem.setVisible(mChatEnabled);
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
                    String username = message.getFrom();

                    if (username.equals(mToUsername) || message.getTo().equals(mToUsername) || message.conversationId().equals(mToUsername)) {
                        list_message.refreshSelectLast();
                        mConversation.markMessageAsRead(message.getMsgId());
                    }
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            mHandler.post(() -> {
                for (EMMessage msg : messages) {
                    EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();

                    if (ACTION_CLOSE_CONVERSATION.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        mChatEnabled = false;

                        setChatableView();
                    }
                }
            });
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

}