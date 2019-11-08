package com.hyphenate.easeui.module.inquiry.provider;

import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;
import com.hyphenate.easeui.module.chat.provider.EaseChatInputMenuStyle;

import java.util.List;

public class EaseChatInputMenuInquiryStyle implements EaseChatInputMenuStyle {

    @Override
    public boolean voiceEnable() {
        return false;
    }

    @Override
    public boolean faceEnable() {
        return false;
    }

    @Override
    public boolean moreEnable() {
        return true;
    }

    @Override
    public boolean pickAlbumPhotoEnable() {
        return true;
    }

    @Override
    public boolean pickCameraPhotoEnable() {
        return true;
    }

    @Override
    public List<EaseMenuItem> getMoreMenuItems() {
        return null;
    }

    @Override
    public void onExtendInputMenu(EaseInputMenu inputMenu) {
    }

}
