package com.hyphenate.easeui.module.base.widget.message;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView.OnItemClickListener;
import com.hyphenate.easeui.module.base.widget.message.row.EaseCustomChatRowProvider;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatBigExpressionPresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatFilePresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatFinishInquiryPresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatImagePresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatLocationPresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatRowPresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatTextPresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatVideoPresenter;
import com.hyphenate.easeui.module.base.widget.message.presenter.EaseChatVoicePresenter;

import java.util.List;

public class EaseMessageListAdapter extends BaseAdapter {

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_SENT_TXT = 0;//发送的文字
    private static final int MESSAGE_TYPE_RECV_TXT = 1;//接收的文字
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;//发送的图片
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;//接收的图片
    private static final int MESSAGE_TYPE_SENT_LOCATION = 4;//发送的定位
    private static final int MESSAGE_TYPE_RECV_LOCATION = 5;//接收的定位
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;//发送的语音
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;//接收的语音
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;//发送的视频
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;//接收的视频
    private static final int MESSAGE_TYPE_SENT_FILE = 10;//发送的文件
    private static final int MESSAGE_TYPE_RECV_FILE = 11;//接收的文件
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 12;//发送的表情
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 13;//接收的表情
    private static final int MESSAGE_TYPE_SENT_FINISH_INQUIRY = 14;//发送的问诊结束
    private static final int MESSAGE_TYPE_RECV_FINISH_INQUIRY = 15;//接收的问诊结束

    private ListView listView;

    private EMConversation mConversation;
    private EMMessage[] mMessages;

    private EaseMessageListItemStyle mListItemStyle;
    private EaseCustomChatRowProvider mCustomRowProvider;
    private OnItemClickListener mOnItemClickListener;

    public EaseMessageListAdapter(String username, EMConversation.EMConversationType conversationType, ListView listView) {
        this.listView = listView;
        mConversation = EMClient.getInstance().chatManager().getConversation(username, conversationType, true);
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

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

        private void refreshList() {
            List<EMMessage> messages = mConversation.getAllMessages();
            mMessages = messages.toArray(new EMMessage[messages.size()]);
            mConversation.markAllMessagesAsRead();
            notifyDataSetChanged();
        }

    };

    public void refresh() {
        if (mHandler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }

        Message msg = mHandler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        mHandler.sendMessage(msg);
    }

    /**
     * refresh and select the last
     */
    public void refreshSelectLast() {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;

        mHandler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        mHandler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * refresh and seek to the position
     */
    public void refreshSeekTo(int position) {
        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
    }

    protected EaseChatRowPresenter createChatRowPresenter(EMMessage message, int position) {
        if (mCustomRowProvider != null && mCustomRowProvider.getCustomChatRow(message, position, this) != null) {
            return mCustomRowProvider.getCustomChatRow(message, position, this);
        }

        EaseChatRowPresenter presenter = null;

        switch (message.getType()) {

            case TXT: {
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_FINISH_CONVERSATION, false)) {//结束问诊
                    presenter = new EaseChatFinishInquiryPresenter();

                } else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {//表情
                    presenter = new EaseChatBigExpressionPresenter();

                } else {//文字
                    presenter = new EaseChatTextPresenter();
                }
            }
            break;

            case LOCATION: {//定位
                presenter = new EaseChatLocationPresenter();
            }
            break;

            case FILE: {//文件
                presenter = new EaseChatFilePresenter();
            }
            break;

            case IMAGE: {//图片
                presenter = new EaseChatImagePresenter();
            }
            break;

            case VOICE: {//语音
                presenter = new EaseChatVoicePresenter();
            }
            break;

            case VIDEO: {//视频
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
        int count = 16;

        if (mCustomRowProvider != null && mCustomRowProvider.getCustomChatRowTypeCount() > 0) {
            count += mCustomRowProvider.getCustomChatRowTypeCount();
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);

        if (message != null) {
            if (mCustomRowProvider != null && mCustomRowProvider.getCustomChatRowType(message) > 0) {
                return mCustomRowProvider.getCustomChatRowType(message) + 16;
            }

            if (message.getType() == EMMessage.Type.TXT) {
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_FINISH_CONVERSATION, false)) {//结束问诊
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FINISH_INQUIRY : MESSAGE_TYPE_SENT_FINISH_INQUIRY;

                } else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {//表情
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;

                } else {//文本消息
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

        presenter.setup(message, position, mOnItemClickListener, mListItemStyle);

        return convertView;
    }

    public void setItemStyle(EaseMessageListItemStyle itemStyle) {
        mListItemStyle = itemStyle;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        mCustomRowProvider = rowProvider;
    }

}