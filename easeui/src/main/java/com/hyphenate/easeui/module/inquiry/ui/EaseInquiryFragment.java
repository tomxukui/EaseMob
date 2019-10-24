package com.hyphenate.easeui.module.inquiry.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bean.EaseEmojicon;
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.model.EaseUser;
import com.hyphenate.easeui.module.base.ui.EaseBaseFragment;
import com.hyphenate.easeui.module.inquiry.callback.EaseOnInquiryListener;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryEndedMenuItem;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryMoreMenuItem;
import com.hyphenate.easeui.module.inquiry.widget.EaseInquiryGridMenu;
import com.hyphenate.easeui.utils.EaseFileUtil;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.dialog.EaseAlertDialog;
import com.hyphenate.easeui.module.base.widget.inputmenu.EaseChatInputMenu;
import com.hyphenate.easeui.module.base.widget.inputmenu.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView;
import com.hyphenate.easeui.module.base.widget.EaseToolbar;
import com.hyphenate.easeui.module.base.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.module.base.widget.message.row.EaseCustomChatRowProvider;
import com.hyphenate.util.PathUtil;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 问诊页面-单聊
 */
public class EaseInquiryFragment extends EaseBaseFragment {

    protected static final String EXTRA_FROM_USER = "EXTRA_FROM_USER";
    protected static final String EXTRA_TO_USER = "EXTRA_TO_USER";
    protected static final String EXTRA_CHAT_MODE = "EXTRA_CHAT_MODE";

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_ALBUM = 2;

    //透传类型
    private static final String ACTION_CLOSE_INQUIRY = "cmd_close_conversation";//结束问诊

    protected LinearLayout linear_container;
    protected EaseToolbar toolbar;
    protected FrameLayout frame_main;
    protected EaseMessageListView list_message;
    protected EaseVoiceRecorderView voice_recorder;
    protected TextView tv_availableCount;
    protected FrameLayout frame_footer;
    protected EaseChatInputMenu menu_input;
    protected EaseInquiryGridMenu menu_ended;

    protected EMConversation mConversation;//会话
    protected EaseUser mFromUser;
    protected EaseUser mToUser;
    @EaseType.ChatMode
    protected String mChatMode;//问诊模式
    protected boolean mIsMessageInit;//消息是否已加载
    protected boolean mIsClosed;//问诊是否已关闭

    protected int mPageSize = 20;//消息分页一页最多数量
    protected boolean mHaveMoreData = true;//是否有更多消息
    protected File mCameraFile;//相机拍照照片文件

    @Nullable
    private EaseOnInquiryListener mOnInquiryListener;

    public static EaseInquiryFragment newInstance(EaseUser fromUser, EaseUser toUser, @EaseType.ChatMode String chatMode) {
        EaseInquiryFragment fragment = new EaseInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_FROM_USER, fromUser);
        bundle.putSerializable(EXTRA_TO_USER, toUser);
        bundle.putString(EXTRA_CHAT_MODE, chatMode);
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
            mChatMode = bundle.getString(EXTRA_CHAT_MODE);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        linear_container = view.findViewById(R.id.linear_container);
        toolbar = view.findViewById(R.id.toolbar);
        frame_main = view.findViewById(R.id.frame_main);
        list_message = view.findViewById(R.id.list_message);
        voice_recorder = view.findViewById(R.id.voice_recorder);
        tv_availableCount = view.findViewById(R.id.tv_availableCount);
        frame_footer = view.findViewById(R.id.frame_footer);
        menu_input = view.findViewById(R.id.menu_input);
        menu_ended = view.findViewById(R.id.menu_ended);
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
        onConversationInit();
        onMessageListInit();

        //设置消息列表
        list_message.getSwipeRefreshLayout().setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.getSwipeRefreshLayout().setOnRefreshListener(() -> getHandler().postDelayed(() -> loadMoreLocalMessages(), 600));

        //设置输入框的更多菜单
        List<EaseInquiryMoreMenuItem> moreMenuItems = new ArrayList<>();
        moreMenuItems.add(new EaseInquiryMoreMenuItem(R.mipmap.ease_ic_camera, "拍照", (itemModel, position) -> requestPermission(data -> pickPhotoFromCamera(), Permission.Group.CAMERA, Permission.Group.STORAGE)));
        moreMenuItems.add(new EaseInquiryMoreMenuItem(R.mipmap.ease_ic_album, "相册", (itemModel, position) -> requestPermission(data -> pickPhotoFromAlbum(), Permission.Group.CAMERA, Permission.Group.STORAGE)));
        List<EaseInquiryMoreMenuItem> otherMoreMenuItems = getMoreMenuItems();
        if (otherMoreMenuItems != null) {
            moreMenuItems.addAll(otherMoreMenuItems);
        }
        for (int i = 0; i < moreMenuItems.size(); i++) {
            EaseInquiryMoreMenuItem menuItem = moreMenuItems.get(i);
            EaseInquiryMoreMenuItem.OnItemClickListener listener = menuItem.getOnItemClickListener();

            int finalI = i;
            menu_input.addExtendMenuItem(menuItem.getResId(), menuItem.getName(), v -> {
                if (listener != null) {
                    listener.onItemClick(menuItem, finalI);
                }
            });
        }

        //监听输入框的输入
        menu_input.setChatInputMenuListener(new ChatInputMenuListener() {

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

        //设置问诊结束后的底部菜单
        menu_ended.setData(getEndedMenuItems());

        //设置问诊模式
        if (EaseType.BROWSE.equals(mChatMode)) {//问诊模式
            tv_availableCount.setVisibility(View.GONE);
            menu_input.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
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
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    protected void onConversationInit() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToUser.getUsername(), EMConversation.EMConversationType.Chat, true);
        mConversation.markAllMessagesAsRead();

        List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }
    }

    protected void onMessageListInit() {
        list_message.init(mToUser.getUsername(), EMConversation.EMConversationType.Chat, getMessageListItemStyle(), getCustomChatRowProvider());
        list_message.setOnItemClickListener(new EaseMessageListView.OnItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                onAvatarClick(username);
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
                onAvatarLongClick(username);
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                onMessageBubbleLongClick(message);
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                return onMessageBubbleClick(message);
            }

            @Override
            public void onMessageInProgress(EMMessage message) {
                message.setMessageStatusCallback(mMessageStatusCallback);
            }

        });
        list_message.getListView().setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            menu_input.hideExtendMenuContainer();
            return false;
        });

        mIsMessageInit = true;
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
        if (menu_input.onBackPressed()) {
            finish();
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

    /**
     * 发送文字消息
     */
    protected void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mToUser.getUsername());
        sendMessage(message);
    }

    /**
     * 发送大表情消息
     */
    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseMessageUtil.createExpressionMessage(mToUser.getUsername(), name, identityCode);
        sendMessage(message);
    }

    /**
     * 发送语音消息
     */
    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mToUser.getUsername());
        sendMessage(message);
    }

    /**
     * 发送图片消息
     */
    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, mToUser.getUsername());
        sendMessage(message);
    }

    /**
     * 发送文件消息
     */
    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, mToUser.getUsername());
        sendMessage(message);
    }

    /**
     * 发送消息
     */
    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }

        message.setChatType(ChatType.Chat);

        EaseMessageUtil.setUserMessage(message, mFromUser, mToUser);

        onSetMessageAttributes(message);

        message.setMessageStatusCallback(mMessageStatusCallback);

        // Send message.
        EMClient.getInstance().chatManager().sendMessage(message);

        //refresh ui
        if (mIsMessageInit) {
            list_message.refreshSelectLast();
        }
    }

    protected EMCallBack mMessageStatusCallback = new EMCallBack() {

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
        if (!EaseFileUtil.isSdcardExist()) {
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
     * 清空所有聊天消息
     */
    protected void clearAllMessages() {
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
     * 结束问诊
     */
    protected void closeInquiry() {
        //透传发送结束聊天的消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.addBody(new EMCmdMessageBody(ACTION_CLOSE_INQUIRY));
        message.setTo(mToUser.getUsername());
        EMClient.getInstance().chatManager().sendMessage(message);

        //新增一条文本消息到本地数据库
        if (mConversation != null) {
            EMMessage textMessage = EMMessage.createTxtSendMessage("本次问诊已结束", mToUser.getUsername());
            textMessage.setAttribute(EaseConstant.MESSAGE_ATTR_FINISH_CONVERSATION, true);
            mConversation.insertMessage(textMessage);
        }

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
     * 设置问诊关闭的控件
     */
    protected void setCloseInquiryView() {
        tv_availableCount.setVisibility(View.GONE);
        menu_input.setVisibility(View.GONE);
        menu_ended.setVisibility(menu_ended.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /**
     * 消息回调
     */
    private final EMMessageListener mMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            getHandler().post(() -> {
                for (EMMessage message : messages) {
                    String username = message.getFrom();

                    if (username.equals(mToUser.getUsername()) || message.getTo().equals(mToUser.getUsername()) || message.conversationId().equals(mToUser.getUsername())) {
                        list_message.refreshSelectLast();
                        mConversation.markMessageAsRead(message.getMsgId());
                    }
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            getHandler().post(() -> {
                for (EMMessage msg : messages) {
                    EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();

                    if (ACTION_CLOSE_INQUIRY.equals(body.action()) && msg.getFrom().equals(mToUser.getUsername())) {
                        setInquiryClosed();
                        setCloseInquiryView();
                        hideSoftKeyboard();
                    }
                }
            });
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            if (mIsMessageInit) {
                getHandler().post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            if (mIsMessageInit) {
                getHandler().post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            if (mIsMessageInit) {
                getHandler().post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            if (mIsMessageInit) {
                getHandler().post(() -> list_message.refresh());
            }
        }

    };

    /**
     * 获取结束问诊后的底部菜单子项集合, 如果为空, 则隐藏
     */
    @Nullable
    protected List<EaseInquiryEndedMenuItem> getEndedMenuItems() {
        return null;
    }

    /**
     * 获取输入框的更多菜单子项集合
     */
    @Nullable
    protected List<EaseInquiryMoreMenuItem> getMoreMenuItems() {
        return null;
    }

    /**
     * 添加自定义消息
     */
    protected void onSetMessageAttributes(EMMessage message) {
    }

    /**
     * 点击头像事件
     */
    protected void onAvatarClick(String username) {
    }

    /**
     * 长按头像事件
     */
    protected void onAvatarLongClick(String username) {
    }

    /**
     * 点击消息事件
     */
    protected boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    /**
     * 长按消息事件
     */
    protected void onMessageBubbleLongClick(EMMessage message) {
    }

    /**
     * 获取自定义聊天消息样式提供者
     */
    protected EaseCustomChatRowProvider getCustomChatRowProvider() {
        return null;
    }

    /**
     * 获取聊天消息样式
     */
    protected EaseMessageListItemStyle getMessageListItemStyle() {
        return null;
    }

}