package com.hyphenate.easeui.utils;

import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.module.base.model.EaseDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EaseMessageCache {

    private List<EMMessage> mMessages;
    private List<EaseDuration> mDurations;
    private int mDurationIndex;
    private int mPageSize;

    public EaseMessageCache(List<EaseDuration> durations, int pageSize) {
        mMessages = new ArrayList<>();
        mDurations = (durations == null ? new ArrayList<>() : durations);
        Collections.sort(mDurations, new DurationComparator());
        mDurationIndex = 0;
        mPageSize = (pageSize <= 0 ? 15 : pageSize);
    }

    /**
     * 获取所有的消息列表
     */
    public List<EMMessage> getMessages() {
        return mMessages;
    }

    public List<EMMessage> fetchMessages(EMConversation conversation) {
        if (conversation == null || mDurations.isEmpty()) {
            return null;
        }

        List<EMMessage> messages = new ArrayList<>();
        int pageSize = mPageSize;

        while (mDurationIndex >= 0 && mDurationIndex < mDurations.size() && pageSize > 0) {
            EaseDuration duration = mDurations.get(mDurationIndex);
            long startTimeStamp = duration.getStartTimeStamp();
            long endTimeStamp = duration.getEndTimeStamp();

            if (mMessages.size() > 0) {
                endTimeStamp = Math.min(endTimeStamp, mMessages.get(0).getMsgTime());
            }

            List<EMMessage> tempMessages = conversation.searchMsgFromDB(startTimeStamp, endTimeStamp, pageSize);

            if (tempMessages != null) {
                messages.addAll(tempMessages);
                mMessages.addAll(0, tempMessages);
            }

            pageSize -= (tempMessages == null ? 0 : tempMessages.size());

            if (pageSize > 0) {
                mDurationIndex++;
            }
        }

        //test
        for (int i = 0; i < messages.size(); i++) {
            EMMessage message = messages.get(i);
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();

            Log.e("ddd", String.format("%d: msgId:%s, msgTime:%d, content:%s", i, message.getMsgId(), message.getMsgTime(), txtBody.getMessage()));
        }
        //end

        return messages;
    }

    class DurationComparator implements Comparator<EaseDuration> {

        @Override
        public int compare(EaseDuration t1, EaseDuration t2) {
            long len = t1.getStartTimeStamp() - t2.getStartTimeStamp();

            if (len > 0) {
                return 1;

            } else if (len < 0) {
                return -1;

            } else {
                return 0;
            }
        }

    }


    private static class MessageCache {

        TreeMap<Long, Object> sortedMessages = new TreeMap(new MessageComparator());
        Map<String, EMMessage> messages = new HashMap();
        Map<String, Long> idTimeMap = new HashMap();
        boolean hasDuplicateTime = false;
        final boolean sortByServerTime = EMClient.getInstance().getChatConfigPrivate().b().isSortMessageByServerTime();

        public synchronized EMMessage getMessage(String msgId) {
            return msgId != null && !msgId.isEmpty() ? this.messages.get(msgId) : null;
        }

        public synchronized void addMessages(List<EMMessage> messages) {
            Iterator iterator = messages.iterator();

            while (iterator.hasNext()) {
                EMMessage message = (EMMessage) iterator.next();
                this.addMessage(message);
            }
        }

        public synchronized void addMessage(EMMessage message) {
            if (message != null && message.getMsgTime() != 0L && message.getMsgTime() != -1L && message.getMsgId() != null && !message.getMsgId().isEmpty() && message.getType() != EMMessage.Type.CMD) {
                String msgId = message.getMsgId();
                long time;
                if (this.messages.containsKey(msgId)) {
                    time = this.idTimeMap.get(msgId);
                    this.sortedMessages.remove(time);
                    this.messages.remove(msgId);
                    this.idTimeMap.remove(msgId);
                }

                time = this.sortByServerTime ? message.getMsgTime() : message.localTime();

                if (this.sortedMessages.containsKey(time)) {
                    this.hasDuplicateTime = true;

                    Object object = this.sortedMessages.get(time);

                    if (object != null) {
                        if (object instanceof EMMessage) {
                            LinkedList list = new LinkedList();
                            list.add(object);
                            list.add(message);
                            this.sortedMessages.put(time, list);

                        } else if (object instanceof List) {
                            List list = (List) object;
                            list.add(message);
                        }
                    }

                } else {
                    this.sortedMessages.put(time, message);
                }

                this.messages.put(msgId, message);
                this.idTimeMap.put(msgId, time);
            }
        }

        public synchronized void removeMessage(String msgId) {
            if (msgId != null && !msgId.isEmpty()) {
                EMMessage message = this.messages.get(msgId);

                if (message != null) {
                    Long time = this.idTimeMap.get(msgId);

                    if (time != null) {
                        if (this.hasDuplicateTime && this.sortedMessages.containsKey(time)) {
                            Object object = this.sortedMessages.get(time);
                            if (object != null && object instanceof List) {
                                List list = (List) object;
                                Iterator iterator = list.iterator();

                                while (iterator.hasNext()) {
                                    EMMessage itemMessage = (EMMessage) iterator.next();
                                    if (itemMessage != null && itemMessage.getMsgId() != null && itemMessage.getMsgId().equals(msgId)) {
                                        list.remove(itemMessage);
                                        break;
                                    }
                                }
                            } else {
                                this.sortedMessages.remove(time);
                            }
                        } else {
                            this.sortedMessages.remove(time);
                        }

                        this.idTimeMap.remove(msgId);
                    }

                    this.messages.remove(msgId);
                }
            }
        }

        public synchronized List<EMMessage> getAllMessages() {
            ArrayList list = new ArrayList();
            Iterator iterator;
            Object object;

            if (!this.hasDuplicateTime) {
                iterator = this.sortedMessages.values().iterator();

                while (iterator.hasNext()) {
                    object = iterator.next();
                    list.add(object);
                }

            } else {
                iterator = this.sortedMessages.values().iterator();

                while (iterator.hasNext()) {
                    object = iterator.next();

                    if (object != null) {
                        if (object instanceof List) {
                            list.addAll((List) object);

                        } else {
                            list.add(object);
                        }
                    }
                }
            }

            return list;
        }

        public synchronized EMMessage getLastMessage() {
            if (this.sortedMessages.isEmpty()) {
                return null;

            } else {
                Object object = this.sortedMessages.lastEntry().getValue();
                if (object == null) {
                    return null;

                } else if (object instanceof EMMessage) {
                    return (EMMessage) object;

                } else if (object instanceof List) {
                    List list = (List) object;
                    return list.size() > 0 ? (EMMessage) list.get(list.size() - 1) : null;

                } else {
                    return null;
                }
            }
        }

        public synchronized void clear() {
            this.sortedMessages.clear();
            this.messages.clear();
            this.idTimeMap.clear();
        }

        public synchronized boolean isEmpty() {
            return this.sortedMessages.isEmpty();
        }

        class MessageComparator implements Comparator<Long> {

            @Override
            public int compare(Long t1, Long t2) {
                long len = t1 - t2;

                if (len > 0L) {
                    return 1;

                } else {
                    return len == 0L ? 0 : -1;
                }
            }

        }

    }

}