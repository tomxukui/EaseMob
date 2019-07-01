package com.hyphenate.easeui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseContactUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EaseUI {

    private EaseUserProfileProvider userProvider;
    private EaseSettingsProvider settingsProvider;
    private EaseAvatarOptions avatarOptions;

    private Context mContext;
    private EaseNotifier notifier;
    private List<Activity> mActivities;

    private EaseUI() {
        mActivities = new ArrayList<>();
    }

    public static EaseUI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final EaseUI INSTANCE = new EaseUI();
    }

    public void init(Application context, EMOptions options) {
        mContext = context;

        registerActivities(context);

        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());

        } else {
            EMClient.getInstance().init(context, options);
        }

        initNotifier(context);

        EMClient.getInstance().addConnectionListener(mConnectionListener);

        if (userProvider == null) {
            userProvider = username -> EaseContactUtil.getInstance().getContact(username);
        }

        if (settingsProvider == null) {
            settingsProvider = new DefaultSettingsProvider();
        }
    }

    /**
     * 注册监听堆栈中的Activity列表
     */
    private void registerActivities(Application context) {
        context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivities.remove(activity);
            }

        });
    }

    protected EMOptions initChatOptions() {
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setRequireAck(false);
        options.setRequireDeliveryAck(false);
        return options;
    }

    private void initNotifier(Context context) {
        notifier = new EaseNotifier(context);
    }

    /**
     * 客户端连接事件
     */
    private final EMConnectionListener mConnectionListener = new EMConnectionListener() {

        @Override
        public void onConnected() {
            EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        }

        @Override
        public void onDisconnected(int i) {
            EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        }

    };

    /**
     * 消息监听事件
     */
    private final EMMessageListener mMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            EaseAtMessageHelper.get().parseMessages(messages);
            EaseUI.getInstance().getNotifier().notify(messages);

            for (EMMessage message : messages) {
                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                EaseDingMessageHelper.get().handleAckMessage(message);
            }
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
        }

    };

    public EaseNotifier getNotifier() {
        return notifier;
    }

    public void setAvatarOptions(EaseAvatarOptions avatarOptions) {
        this.avatarOptions = avatarOptions;
    }

    public EaseAvatarOptions getAvatarOptions() {
        return avatarOptions;
    }

    public void setUserProfileProvider(EaseUserProfileProvider userProvider) {
        this.userProvider = userProvider;
    }

    public EaseUserProfileProvider getUserProfileProvider() {
        return userProvider;
    }

    public void setSettingsProvider(EaseSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public EaseSettingsProvider getSettingsProvider() {
        return settingsProvider;
    }

    public interface EaseUserProfileProvider {

        EaseUser getUser(String username);

    }

    public interface EaseEmojiconInfoProvider {

        EaseEmojicon getEmojiconInfo(String emojiconIdentityCode);

        Map<String, Object> getTextEmojiconMapping();

    }

    private EaseEmojiconInfoProvider emojiconInfoProvider;

    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return emojiconInfoProvider;
    }

    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
        this.emojiconInfoProvider = emojiconInfoProvider;
    }

    public interface EaseSettingsProvider {
        boolean isMsgNotifyAllowed(EMMessage message);

        boolean isMsgSoundAllowed(EMMessage message);

        boolean isMsgVibrateAllowed(EMMessage message);

        boolean isSpeakerOpened();
    }

    protected class DefaultSettingsProvider implements EaseSettingsProvider {

        @Override
        public boolean isMsgNotifyAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(EMMessage message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }

    }

    public Context getContext() {
        return mContext;
    }

    public Activity getTopActivity() {
        int count = mActivities.size();

        if (count > 0) {
            return mActivities.get(count - 1);

        } else {
            return null;
        }
    }

    /**
     * 退出环信
     */
    public void logout() {
        if (EMClient.getInstance().isConnected()) {
            EMClient.getInstance().logout(true, null);
        }
    }

}