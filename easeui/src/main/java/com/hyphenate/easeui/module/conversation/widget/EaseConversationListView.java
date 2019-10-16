package com.hyphenate.easeui.module.conversation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.module.conversation.adapter.EaseConversationsListAdapter;

import java.util.List;

public class EaseConversationListView extends ListView {

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

    public EMConversation getItem(int position) {
        return mListAdapter.getItem(position);
    }

    public void refresh() {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    public void filter(CharSequence str) {
        if (mListAdapter != null) {
            mListAdapter.getFilter().filter(str);
        }
    }

}