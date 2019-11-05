package com.hyphenate.easeui.module.inquiry.provider;

import com.hyphenate.easeui.module.chat.provider.EaseChatInputMenuProvider;

public abstract class EaseInquiryInputMenuProvider extends EaseChatInputMenuProvider {

    @Override
    public boolean voiceEnable() {
        return false;
    }

    @Override
    public boolean faceEnable() {
        return false;
    }

}