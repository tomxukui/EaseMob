package com.hyphenate.easeui.module.inquiry.provider;

import com.hyphenate.easeui.module.base.widget.input.EaseInputMenu;
import com.hyphenate.easeui.module.base.widget.input.EaseMenuItem;

import java.util.List;

public abstract class EaseInquiryInputMenuProvider {

    /**
     * 语音切换事件
     */
    public void onToggleVoice(boolean show) {
    }

    /**
     * 正在文字输入事件
     */
    public void onTyping(CharSequence s, int start, int before, int count) {
    }

    /**
     * 点击输入框事件
     */
    public void onEditTextClicked() {
    }

    /**
     * 扩展自定义菜单
     */
    public void onExtendInputMenu(EaseInputMenu inputMenu) {
    }

    /**
     * 是否开启语音
     */
    public boolean voiceEnable() {
        return true;
    }

    /**
     * 是否开启表情
     */
    public boolean faceEnable() {
        return true;
    }

    /**
     * 是否开启更多菜单
     */
    public boolean moreEnable() {
        return true;
    }

    /**
     * 设置更多菜单的选项
     */
    public abstract List<EaseMenuItem> onSetMoreMenuItems();

}