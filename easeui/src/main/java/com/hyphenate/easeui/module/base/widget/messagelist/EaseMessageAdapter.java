package com.hyphenate.easeui.module.base.widget.messagelist;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.module.base.widget.messagelist.EaseMessageListView.OnItemClickListener;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.presenter.EaseChatBigExpressionPresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatFilePresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatFinishConversationPresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatImagePresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatLocationPresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatTextPresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatVideoPresenter;
import com.hyphenate.easeui.widget.presenter.EaseChatVoicePresenter;

import java.util.List;

public class EaseMessageAdapter extends BaseAdapter {

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 12;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 13;
    private static final int MESSAGE_TYPE_FINISH_CONVERSATION = 14;

    private ListView listView;

    private EMConversation mConversation;
    private EMMessage[] mMessages;

    private EaseMessageListItemStyle itemStyle;
    private EaseCustomChatRowProvider customRowProvider;
    private OnItemClickListener mOnItemClickListener;

    public EaseMessageAdapter(String username, EMConversation.EMConversationType conversationType, ListView listView) {
        this.listView = listView;
        mConversation = EMClient.getInstance().chatManager().getConversation(username, conversationType, true);
    }

    Handler handler = new Handler() {

        private void refreshList() {
            List<EMMessage> var = mConversation.getAllMessages();
            mMessages = var.toArray(new EMMessage[var.size()]);
            mConversation.markAllMessagesAsRead();
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {

                case HANDLER_MESSAGE_REFRESH_LIST: {
                    refreshList();
                }
                break;

                case HANDLER_MESSAGE_SELECT_LAST: {
                    if (mMessages != null && mMessages.length > 0) {
                        listView.setSelection(mMessages.length - 1);
                    }
                }
                break;

                case HANDLER_MESSAGE_SEEK_TO: {
                    int position = message.arg1;
                    listView.setSelection(position);
                }
                break;

                default:
                    break;
            }
        }
    };

    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }

        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * refresh and select the last
     */
    public void refreshSelectLast() {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;

        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * refresh and seek to the position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
    }

    protected EaseChatRowPresenter createChatRowPresenter(EMMessage message, int position) {
        if (customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null) {
            return customRowProvider.getCustomChatRow(message, position, this);
        }

        EaseChatRowPresenter presenter = null;

        switch (message.getType()) {

            case TXT: {
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_FINISH_CONVERSATION, false)) {//结束问诊消息
                    presenter = new EaseChatFinishConversationPresenter();

                } else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    presenter = new EaseChatBigExpressionPresenter();

                } else {
                    presenter = new EaseChatTextPresenter();
                }
            }
            break;

            case LOCATION: {
                presenter = new EaseChatLocationPresenter();
            }
            break;

            case FILE: {
                presenter = new EaseChatFilePresenter();
            }
            break;

            case IMAGE: {
                presenter = new EaseChatImagePresenter();
            }
            break;

            case VOICE: {
                presenter = new EaseChatVoicePresenter();
            }
            break;

            case VIDEO: {
                presenter = new EaseChatVideoPresenter();
            }
            break;

            default:
                break;
        }

        return presenter;
    }

    @Override
    public int getCount() {
        return mMessages == null ? 0 : mMessages.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        int count = 15;

        if (customRowProvider != null && customRowProvider.getCustomChatRowTypeCount() > 0) {
            count += customRowProvider.getCustomChatRowTypeCount();
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);

        if (message != null) {
            if (customRowProvider != null && customRowProvider.getCustomChatRowType(message) > 0) {
                return customRowProvider.getCustomChatRowType(message) + 14;
            }

            if (message.getType() == EMMessage.Type.TXT) {//文字
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_FINISH_CONVERSATION, false)) {//结束问诊消息
                    return MESSAGE_TYPE_FINISH_CONVERSATION;

                } else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {//表情消息
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;

                } else {//其他文本消息
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
                }

            } else if (message.getType() == EMMessage.Type.IMAGE) {//图片
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

            } else if (message.getType() == EMMessage.Type.LOCATION) {//定位
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;

            } else if (message.getType() == EMMessage.Type.VOICE) {//语音
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;

            } else if (message.getType() == EMMessage.Type.VIDEO) {//视频
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;

            } else if (message.getType() == EMMessage.Type.FILE) {//文件
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
            }
        }

        return -1;
    }

    @Override
    public EMMessage getItem(int position) {
        if (mMessages != null && position >= 0 && position < mMessages.length) {
            return mMessages[position];
        }

        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);

        EaseChatRowPresenter presenter;

        if (convertView == null) {
            presenter = createChatRowPresenter(message, position);
            convertView = presenter.createChatRow(parent.getContext(), message, position, this);
            convertView.setTag(presenter);

        } else {
            presenter = (EaseChatRowPresenter) convertView.getTag();
        }

        presenter.setup(message, position, mOnItemClickListener, itemStyle);

        return convertView;
    }

    public void setItemStyle(EaseMessageListItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        customRowProvider = rowProvider;
    }

}