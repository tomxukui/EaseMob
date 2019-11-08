package com.hyphenate.easeui.module.chat.provider;

import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;

import java.util.List;

public interface EaseChatInputMenuStyle {

    /**
     * 是否开启语音
     */
    boolean voiceEnable();

    /**
     * 是否开启表情
     */
    boolean faceEnable();

    /**
     * 是否开启更多菜单
     */
    boolean moreEnable();

    /**
     * 是否开启相册取照片
     */
    boolean pickAlbumPhotoEnable();

    /**
     * 是否开启相机拍照
     */
    boolean pickCameraPhotoEnable();

    /**
     * 设置更多菜单的选项
     */
    List<EaseMenuItem> getMoreMenuItems();

    /**
     * 扩展自定义菜单
     */
    void onExtendInputMenu(EaseInputMenu inputMenu);

}