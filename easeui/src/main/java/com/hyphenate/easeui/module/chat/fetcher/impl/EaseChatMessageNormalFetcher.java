package com.hyphenate.easeui.module.chat.fetcher.impl;

import com.hyphenate.easeui.module.chat.fetcher.EaseChatMessageFetcher;

public class EaseChatMessageNormalFetcher implements EaseChatMessageFetcher {

    @Override
    public int getPageSize() {
        return 20;
    }

}
