package com.hyphenate.easeui.module.inquiry.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.hyphenate.easeui.bean.EaseUser;
import com.hyphenate.easeui.constants.EaseType;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.module.base.ui.EaseBaseFragment;
import com.hyphenate.easeui.module.inquiry.adapter.EaseInquiryMenuListAdapter;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryEndedMenuItem;
import com.hyphenate.easeui.module.inquiry.model.EaseInquiryMenuItem;
import com.hyphenate.easeui.module.inquiry.widget.EaseInquiryEndedMenu;
import com.hyphenate.easeui.utils.ContextCompatUtil;
import com.hyphenate.easeui.utils.DensityUtil;
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

    protected static final String EXTRA_TO_USERNAME = "EXTRA_TO_USERNAME";
    protected static final String EXTRA_CHAT_MODE = "EXTRA_CHAT_MODE";

    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_ALBUM = 3;

    //透传类型
    private static final String ACTION_CLOSE_CONVERSATION = "cmd_close_conversation";

    private File mCameraFile;

    private EaseChatFragmentHelper mChatFragmentHelper;

    private EaseToolbar toolbar;
    private EaseChatMessageList list_message;
    private EaseChatInputMenu menu_input;
    private EaseInquiryEndedMenu menu_ended;
    private EaseVoiceRecorderView voice_recorder;
    private TextView tv_availableCount;

    //标题栏菜单
    private ListPopupWindow mPopupMenu;
    private EaseInquiryMenuListAdapter mMenuListAdapter;

    //主要参数
    private EMConversation mConversation;
    private String mToUsername;//对方主键
    @EaseType.ChatMode
    private String mChatMode;//聊天模式
    private boolean mIsFinished;//问诊是否已结束
    private boolean mIsMessagesInited;//消息列表是否已初始化
    private int mPageSize = 20;
    private boolean mHaveMoreData = true;

    public static EaseInquiryFragment newInstance(String toUsername, @EaseType.ChatMode String chatMode) {
        EaseInquiryFragment fragment = new EaseInquiryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TO_USERNAME, toUsername);
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
            mToUsername = bundle.getString(EXTRA_TO_USERNAME);
            mChatMode = bundle.getString(EXTRA_CHAT_MODE);
        }

        mMenuListAdapter = new EaseInquiryMenuListAdapter(getMenuItems());
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        voice_recorder = view.findViewById(R.id.voice_recorder);
        list_message = view.findViewById(R.id.list_message);
        menu_input = view.findViewById(R.id.menu_input);
        tv_availableCount = view.findViewById(R.id.tv_availableCount);
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
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setTitle();
    }

    @Override
    protected void setView(Bundle savedInstanceState) {
        super.setView(savedInstanceState);
        onConversationInit();
        onMessageListInit();

        //设置消息列表
        list_message.setShowUserNick(false);
        list_message.getSwipeRefreshLayout().setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
        list_message.getSwipeRefreshLayout().setOnRefreshListener(() -> getHandler().postDelayed(() -> loadMoreLocalMessages(), 600));

        //设置功能菜单
        menu_input.addExtendMenuItem(R.mipmap.ease_ic_camera, "拍照", v -> requestPermission(data -> pickPhotoFromCamera(), Permission.Group.CAMERA, Permission.Group.STORAGE));
        menu_input.addExtendMenuItem(R.mipmap.ease_ic_album, "相册", v -> requestPermission(data -> pickPhotoFromAlbum(), Permission.Group.CAMERA, Permission.Group.STORAGE));
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

        menu_ended.setData(getEndedMenuItems());

        if (EaseType.BROWSE.equals(mChatMode)) {//浏览模式
            setBrowseView();

        } else {
            setChatView();
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
        if (msgCount < mConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ease_menu_inquiry, menu);

        MenuItem mMoreMenuItem = menu.findItem(R.id.action_more);
        mMoreMenuItem.setVisible(!mMenuListAdapter.isEmpty());
        mMoreMenuItem.setOnMenuItemClickListener(item -> {
            showPopupMenu();
            return true;
        });
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
            menu_input.hideExtendMenuContainer();
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

    /**
     * 设置浏览控件
     */
    private void setBrowseView() {
        tv_availableCount.setVisibility(View.GONE);
        menu_input.setVisibility(View.GONE);
        menu_ended.setVisibility(View.GONE);
    }

    /**
     * 设置聊天控件
     */
    private void setChatView() {
        if (mIsFinished) {
            tv_availableCount.setVisibility(View.GONE);
            menu_input.setVisibility(View.GONE);
            menu_ended.setVisibility(menu_ended.isEmpty() ? View.GONE : View.VISIBLE);

        } else {
            tv_availableCount.setVisibility(View.VISIBLE);
            menu_input.setVisibility(View.VISIBLE);
            menu_ended.setVisibility(View.GONE);
        }
    }

    /**
     * 结束问诊
     */
    protected void finishInquiry() {
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

        //结束问诊
        mIsFinished = true;
        setChatView();
    }

    /**
     * 显示标题栏菜单
     */
    private void showPopupMenu() {
        if (mPopupMenu == null) {
            mPopupMenu = new ListPopupWindow(getContext());
            mPopupMenu.setContentWidth(DensityUtil.dp2px(138));
            mPopupMenu.setBackgroundDrawable(ContextCompatUtil.getDrawable(R.drawable.ease_bg_menu));
            mPopupMenu.setDropDownGravity(Gravity.RIGHT);
            mPopupMenu.setHorizontalOffset(DensityUtil.dp2px(-5));
            mPopupMenu.setVerticalOffset(DensityUtil.dp2px(4));
            mPopupMenu.setAdapter(mMenuListAdapter);
            mPopupMenu.setOnItemClickListener((parent, view, position, id) -> {
                EaseInquiryMenuItem menuItem = mMenuListAdapter.getItem(position);

                EaseInquiryMenuItem.OnItemClickListener listener = menuItem.getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(menuItem, position);
                }
            });
            mPopupMenu.setAnchorView(toolbar);
        }

        if (!mPopupMenu.isShowing()) {
            mPopupMenu.show();
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
            getHandler().post(() -> {
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
            getHandler().post(() -> {
                for (EMMessage msg : messages) {
                    EMCmdMessageBody body = (EMCmdMessageBody) msg.getBody();

                    if (ACTION_CLOSE_CONVERSATION.equals(body.action()) && msg.getFrom().equals(mToUsername)) {
                        mIsFinished = true;
                        setChatView();
                    }
                }
            });
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            if (mIsMessagesInited) {
                getHandler().post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            if (mIsMessagesInited) {
                getHandler().post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            if (mIsMessagesInited) {
                getHandler().post(() -> list_message.refresh());
            }
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            if (mIsMessagesInited) {
                getHandler().post(() -> list_message.refresh());
            }
        }

    };

    /**
     * 获取菜单子项集合, 如果为空, 则隐藏
     */
    @Nullable
    protected List<EaseInquiryMenuItem> getMenuItems() {
        return null;
    }

    /**
     * 获取结束问诊后的底部菜单子项集合, 如果为空, 则隐藏
     */
    @Nullable
    protected List<EaseInquiryEndedMenuItem> getEndedMenuItems() {
        return null;
    }

}