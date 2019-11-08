package com.hyphenate.easeui.module.chat.provider.impl;

import com.hyphenate.easeui.module.chat.provider.EaseChatMessageFetcher;

public class EaseChatMessageDefaultFetcher implements EaseChatMessageFetcher {

    @Override
    public int getPageSize() {
        return 20;
    }

}
