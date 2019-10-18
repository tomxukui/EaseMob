package com.hyphenate.easeui.module.conversation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bean.EaseAvatarOptions;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseSmileUtil;
import com.hyphenate.easeui.utils.EaseUserUtil;
import com.hyphenate.easeui.module.base.widget.EaseImageView;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EaseConversationsListAdapter extends ArrayAdapter<EMConversation> {

    private List<EMConversation> mConversations;
    private List<EMConversation> mCopyConversations;
    private ConversationFilter mConversationFilter;
    private boolean mNotiyfyByFilter;

    private ViewHolder mHolder;

    public EaseConversationsListAdapter(Context context, List<EMConversation> conversations) {
        super(context, 0, conversations);
        mConversations = conversations;

        mCopyConversations = new ArrayList<>();
        mCopyConversations.addAll(conversations);
    }

    @Override
    public int getCount() {
        return mConversations == null ? 0 : mConversations.size();
    }

    @Override
    public EMConversation getItem(int position) {
        if (position >= 0 && position < mConversations.size()) {
            return mConversations.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ease_item_list_converstations, parent, false);

            mHolder = new ViewHolder();
            mHolder.iv_avatar = convertView.findViewById(R.id.iv_avatar);
            mHolder.tv_unreadCount = convertView.findViewById(R.id.tv_unreadCount);
            mHolder.tv_name = convertView.findViewById(R.id.tv_name);
            mHolder.tv_time = convertView.findViewById(R.id.tv_time);
            mHolder.iv_state = convertView.findViewById(R.id.iv_state);
            mHolder.tv_message = convertView.findViewById(R.id.tv_message);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        EMConversation conversation = getItem(position);
        String username = conversation.conversationId();

        if (conversation.getType() == EMConversationType.GroupChat) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);

            mHolder.iv_avatar.setImageResource(R.drawable.ease_group_icon);
            mHolder.tv_name.setText(group != null ? group.getGroupName() : username);

        } else if (conversation.getType() == EMConversationType.ChatRoom) {
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);

            mHolder.iv_avatar.setImageResource(R.drawable.ease_group_icon);
            mHolder.tv_name.setText((room != null && !TextUtils.isEmpty(room.getName())) ? room.getName() : username);

        } else {
            EMMessage message = conversation.getLastMessage();
            String avatar;
            String nickname;

            if (message.direct() == EMMessage.Direct.SEND) {
                avatar = EaseMessageUtil.getToAvatar(message, null);
                nickname = EaseMessageUtil.getToNickname(message, message.getTo());

            } else {
                avatar = EaseMessageUtil.getFromAvatar(message, null);
                nickname = EaseMessageUtil.getFromNickname(message, message.getFrom());
            }

            EaseUserUtil.setUserAvatar(mHolder.iv_avatar, avatar, R.mipmap.ease_ic_chatto_portrait);
            mHolder.tv_name.setText(nickname);
        }

        EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();

        if (avatarOptions != null) {
            if (avatarOptions.getAvatarShape() != 0) {
                mHolder.iv_avatar.setShapeType(avatarOptions.getAvatarShape());
            }
            if (avatarOptions.getAvatarBorderWidth() != 0) {
                mHolder.iv_avatar.setBorderWidth(avatarOptions.getAvatarBorderWidth());
            }
            if (avatarOptions.getAvatarBorderColor() != 0) {
                mHolder.iv_avatar.setBorderColor(avatarOptions.getAvatarBorderColor());
            }
            if (avatarOptions.getAvatarRadius() != 0) {
                mHolder.iv_avatar.setRadius(avatarOptions.getAvatarRadius());
            }
        }

        if (conversation.getUnreadMsgCount() > 0) {
            mHolder.tv_unreadCount.setText("" + conversation.getUnreadMsgCount());
            mHolder.tv_unreadCount.setVisibility(View.VISIBLE);

        } else {
            mHolder.tv_unreadCount.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMsgCount() > 0) {
            EMMessage lastMessage = conversation.getLastMessage();

            mHolder.tv_message.setText(EaseSmileUtil.getSmiledText(getContext(), EaseMessageUtil.getMessageDigest(lastMessage)), BufferType.SPANNABLE);
            mHolder.tv_time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                mHolder.iv_state.setVisibility(View.VISIBLE);

            } else {
                mHolder.iv_state.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!mNotiyfyByFilter) {
            mCopyConversations.clear();
            mCopyConversations.addAll(mConversations);
            mNotiyfyByFilter = false;
        }
    }

    @Override
    public Filter getFilter() {
        if (mConversationFilter == null) {
            mConversationFilter = new ConversationFilter(mConversations);
        }
        return mConversationFilter;
    }

    private class ConversationFilter extends Filter {

        List<EMConversation> mOriginalValues;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = mCopyConversations;
                results.count = mCopyConversations.size();

            } else {
                if (mCopyConversations.size() > mOriginalValues.size()) {
                    mOriginalValues = mCopyConversations;
                }

                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.conversationId();

                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mConversations.clear();
            if (results.values != null) {
                mConversations.addAll((List<EMConversation>) results.values);
            }

            if (results.count > 0) {
                mNotiyfyByFilter = true;
                notifyDataSetChanged();

            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    static class ViewHolder {

        EaseImageView iv_avatar;
        TextView tv_unreadCount;
        TextView tv_name;
        TextView tv_time;
        ImageView iv_state;
        TextView tv_message;

    }

}