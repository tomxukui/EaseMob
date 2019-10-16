package com.hyphenate.easeui.module.conversation.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ListView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.module.conversation.adapter.EaseConversationsListAdapter;

import java.util.List;

public class EaseConversationListView extends ListView {

    protected final int MSG_REFRESH_ADAPTER_DATA = 1;

    protected EaseConversationsListAdapter mListAdapter;

    public EaseConversationListView(Context context) {
        super(context);
    }

    public EaseConversationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EaseConversationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<EMConversation> conversations) {
        mListAdapter = new EaseConversationsListAdapter(getContext(), conversations);
        setAdapter(mListAdapter);
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {

                case MSG_REFRESH_ADAPTER_DATA:
                    if (mListAdapter != null) {
                        mListAdapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }
        }

    };

    public EMConversation getItem(int position) {
        return mListAdapter.getItem(position);
    }

    public void refresh() {
        if (!mHandler.hasMessages(MSG_REFRESH_ADAPTER_DATA)) {
            mHandler.sendEmptyMessage(MSG_REFRESH_ADAPTER_DATA);
        }
    }

    public void filter(CharSequence str) {
        mListAdapter.getFilter().filter(str);
    }

}
