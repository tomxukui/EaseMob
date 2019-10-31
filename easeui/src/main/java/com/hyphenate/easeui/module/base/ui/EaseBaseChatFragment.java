package com.hyphenate.easeui.module.base.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bean.EaseEmojicon;
import com.hyphenate.easeui.bean.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.module.base.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.module.base.widget.emojicon.EaseEmojiconMenuBase;
import com.hyphenate.easeui.module.base.widget.input.EaseInputControlButton;
import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseInputMoreMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;
import com.hyphenate.easeui.utils.EaseDensityUtil;
import com.hyphenate.easeui.utils.EaseFileUtil;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseSmileUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.util.PathUtil;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EaseBaseChatFragment extends EaseBaseFragment {

    protected static final int REQUEST_CAMERA = 100;
    protected static final int REQUEST_ALBUM = 101;

    @Nullable
    private EaseInputControlButton mFaceButton;
    @Nullable
    private EaseInputControlButton mMoreButton;

    private File mCameraFile;//相机拍照的照片文件

    @Nullable
    private OnInputListener mOnInputListener;

    /**
     * 获取对方username
     */
    protected abstract String getToUsername();

    /**
     * 发送透传消息
     *
     * @param cmd 透传命令
     */
    protected void sendCmdMessage(String cmd) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.addBody(new EMCmdMessageBody(cmd));
        message.setTo(getToUsername());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * 发送文字消息
     *
     * @param content 文字内容
     */
    protected void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, getToUsername());
        sendMessage(message);
    }

    /**
     * 发送大表情消息
     *
     * @param expressioName 表情名称
     * @param identityCode  表情编号
     */
    protected void sendBigExpressionMessage(String expressioName, String identityCode) {
        EMMessage message = EaseMessageUtil.createExpressionMessage(getToUsername(), expressioName, identityCode);
        sendMessage(message);
    }

    /**
     * 发送大表情消息
     *
     * @param emojicon 表情
     */
    protected void sendBigExpressionMessage(EaseEmojicon emojicon) {
        EMMessage message = EaseMessageUtil.createExpressionMessage(getToUsername(), emojicon.getName(), emojicon.getIdentityCode());
        sendMessage(message);
    }

    /**
     * 发送语音消息
     *
     * @param filePath 音频文件路径
     * @param length   录音时长
     */
    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, getToUsername());
        sendMessage(message);
    }

    /**
     * 发送图片消息
     *
     * @param imagePath 图片路径
     */
    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, getToUsername());
        sendMessage(message);
    }

    /**
     * 发送文件消息
     *
     * @param filePath 文件路径
     */
    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, getToUsername());
        sendMessage(message);
    }

    /**
     * 发送消息
     */
    protected abstract void sendMessage(EMMessage message);

    /**
     * 发送图片
     */
    protected void sendPicByUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
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
            File file = new File(uri.getPath());
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

    /**
     * 添加表情菜单
     */
    protected void addFaceMenu(EaseInputMenu inputMenu, int position) {
        if (mFaceButton != null) {
            return;
        }

        EaseEmojiconMenu facePanel = new EaseEmojiconMenu(getContext());
        List<EaseEmojiconGroupEntity> data = new ArrayList<>();
        data.add(new EaseEmojiconGroupEntity(R.drawable.ee_1, Arrays.asList(EaseDefaultEmojiconDatas.getData())));
        facePanel.init(data);
        facePanel.setEmojiconMenuListener(new EaseEmojiconMenuBase.EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if (emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION) {
                    if (emojicon.getEmojiText() != null) {
                        inputMenu.getControl().appendEmojiconInput(EaseSmileUtil.getSmiledText(getContext(), emojicon.getEmojiText()));
                    }

                } else {
                    if (mOnInputListener != null) {
                        mOnInputListener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                inputMenu.getControl().deleteEmojiconInput();
            }

        });

        EaseInputControlButton faceButton = new EaseInputControlButton(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(EaseDensityUtil.dp2px(30), EaseDensityUtil.dp2px(30));
        layoutParams.setMargins(0, 0, EaseDensityUtil.dp2px(10), 0);
        faceButton.setLayoutParams(layoutParams);
        faceButton.setBackgroundResource(R.drawable.ease_btn_chat_face);
        faceButton.setInputEnable(true);
        faceButton.setPanel(facePanel);

        inputMenu.addView(faceButton, facePanel, position, layoutParams);

        mFaceButton = faceButton;
    }

    /**
     * 添加更多菜单
     */
    protected void addMoreMenu(EaseInputMenu inputMenu, int position, List<EaseMenuItem> menuItems) {
        if (mMoreButton != null) {
            return;
        }

        EaseInputMoreMenu morePanel = new EaseInputMoreMenu(getContext());
        morePanel.addMenuItems(menuItems);

        EaseInputControlButton moreButton = new EaseInputControlButton(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(EaseDensityUtil.dp2px(30), EaseDensityUtil.dp2px(30));
        layoutParams.setMargins(0, 0, EaseDensityUtil.dp2px(10), 0);
        moreButton.setLayoutParams(layoutParams);
        moreButton.setBackgroundResource(R.drawable.ease_btn_chat_more);
        moreButton.setInputEnable(false);
        moreButton.setPanel(morePanel);

        inputMenu.addView(moreButton, morePanel, position, layoutParams);

        mMoreButton = moreButton;
    }

    /**
     * 创建更多菜单中的相册选项
     */
    protected EaseMenuItem createAlbumMenuItem() {
        return new EaseMenuItem(R.mipmap.ease_ic_album, "相册", v -> requestPermission(data -> pickPhotoFromAlbum(), Permission.Group.CAMERA, Permission.Group.STORAGE));
    }

    /**
     * 创建更多菜单中的拍摄选项
     */
    protected EaseMenuItem createCameraMenuItem() {
        return new EaseMenuItem(R.mipmap.ease_ic_camera, "拍摄", v -> requestPermission(data -> pickPhotoFromCamera(), Permission.Group.CAMERA, Permission.Group.STORAGE));
    }

    /**
     * 获取人脸控制按钮
     */
    @Nullable
    protected EaseInputControlButton getFaceButton() {
        return mFaceButton;
    }

    /**
     * 获取更多控制按钮
     */
    @Nullable
    protected EaseInputControlButton getMoreButton() {
        return mMoreButton;
    }

    /**
     * 从相册中选择照片
     */
    protected void pickPhotoFromAlbum() {
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
     * 拍照获取照片
     */
    protected void pickPhotoFromCamera() {
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
     * 获取输入事件
     */
    protected OnInputListener getOnInputListener() {
        return mOnInputListener;
    }

    /**
     * 设置监听输入事件
     */
    protected void setOnInputListener(@Nullable OnInputListener listener) {
        mOnInputListener = listener;
    }

    /**
     * 输入事件
     */
    protected interface OnInputListener {

        /**
         * 正在输入文字
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * 文字输入
         */
        void onSendMessage(String content);

        /**
         * 表情输入
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);

        /**
         * 语音输入
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

    }

}