package com.hyphenate.easeui.module.base.widget.message.row;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bean.EaseAvatarOptions;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseMessageUtil;
import com.hyphenate.easeui.utils.EaseUserUtil;
import com.hyphenate.easeui.module.base.widget.message.EaseMessageListView.OnItemClickListener;
import com.hyphenate.easeui.module.base.widget.EaseImageView;
import com.hyphenate.util.DateUtils;

import java.util.Date;

public abstract class EaseChatRow extends LinearLayout {

    public interface EaseChatRowActionCallback {

        void onResendClick(EMMessage message);

        void onBubbleClick(EMMessage message);

        void onDetachedFromWindow();

    }

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected OnItemClickListener itemClickListener;
    protected EaseMessageListItemStyle itemStyle;

    private EaseChatRowActionCallback itemActionCallback;

    public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        this.activity = (Activity) context;
        inflater = LayoutInflater.from(context);

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        itemActionCallback.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    public void updateView(final EMMessage msg) {
        activity.runOnUiThread(() -> onViewUpdate(msg));
    }

    private void initView() {
        onInflateView();
        timeStampView = findViewById(R.id.timestamp);
        userAvatarView = findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = findViewById(R.id.tv_userid);
        progressBar = findViewById(R.id.progress_bar);
        statusView = findViewById(R.id.msg_status);
        ackedView = findViewById(R.id.tv_ack);
        deliveredView = findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * set property according message and postion
     *
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
                          OnItemClickListener itemClickListener,
                          EaseChatRowActionCallback itemActionCallback,
                          EaseMessageListItemStyle itemStyle) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
        this.itemActionCallback = itemActionCallback;
        this.itemStyle = itemStyle;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        if (timeStampView != null) {
            if (position == 0) {
                timeStampView.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timeStampView.setVisibility(View.VISIBLE);

            } else {
                // show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timeStampView.setVisibility(View.GONE);

                } else {
                    timeStampView.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timeStampView.setVisibility(View.VISIBLE);
                }
            }
        }

        //设置头像
        if (userAvatarView != null) {
            String avatar = EaseMessageUtil.getFromAvatar(message, null);

            EaseUserUtil.setUserAvatar(userAvatarView, avatar, message.direct() == Direct.SEND ? R.mipmap.ease_ic_chatfrom_portrait : R.mipmap.ease_ic_chatto_portrait);
        }

        //设置昵称
        if (usernickView != null) {
            String nickname = EaseMessageUtil.getFromNickname(message, message.getFrom());

            usernickView.setText(nickname);
        }

        if (EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            if (deliveredView != null) {
                if (message.isDelivered()) {
                    deliveredView.setVisibility(View.VISIBLE);

                } else {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (EMClient.getInstance().getOptions().getRequireAck()) {
            if (ackedView != null) {
                if (message.isAcked()) {
                    if (deliveredView != null) {
                        deliveredView.setVisibility(View.INVISIBLE);
                    }

                    ackedView.setVisibility(View.VISIBLE);

                } else {
                    ackedView.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (itemStyle != null) {
            if (userAvatarView != null) {
                EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();

                if (avatarOptions != null && userAvatarView instanceof EaseImageView) {
                    EaseImageView avatarView = ((EaseImageView) userAvatarView);

                    if (avatarOptions.getAvatarShape() != 0) {
                        avatarView.setShapeType(avatarOptions.getAvatarShape());
                    }
                    if (avatarOptions.getAvatarBorderWidth() != 0) {
                        avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
                    }
                    if (avatarOptions.getAvatarBorderColor() != 0) {
                        avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
                    }
                    if (avatarOptions.getAvatarRadius() != 0) {
                        avatarView.setRadius(avatarOptions.getAvatarRadius());
                    }
                }
            }

            if (usernickView != null) {
                if (message.direct() == Direct.SEND) {
                    usernickView.setVisibility(itemStyle.isShowFromUserNickname() ? View.VISIBLE : View.GONE);

                } else {
                    usernickView.setVisibility(itemStyle.isShowToUserNickname() ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    private void setClickListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(v -> {
                if (itemClickListener != null && itemClickListener.onBubbleClick(message)) {
                    return;
                }
                if (itemActionCallback != null) {
                    itemActionCallback.onBubbleClick(message);
                }
            });

            bubbleLayout.setOnLongClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onBubbleLongClick(message);
                }
                return true;
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(v -> {
                if (itemClickListener != null && itemClickListener.onResendClick(message)) {
                    return;
                }
                if (itemActionCallback != null) {
                    itemActionCallback.onResendClick(message);
                }
            });
        }

        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    if (message.direct() == Direct.SEND) {
                        itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
                    } else {
                        itemClickListener.onUserAvatarClick(message.getFrom());
                    }
                }
            });
            userAvatarView.setOnLongClickListener(v -> {
                if (itemClickListener != null) {
                    if (message.direct() == Direct.SEND) {
                        itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
                    } else {
                        itemClickListener.onUserAvatarLongClick(message.getFrom());
                    }
                    return true;
                }
                return false;
            });
        }
    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh view when message status change
     */
    protected abstract void onViewUpdate(EMMessage msg);

    /**
     * setup view
     */
    protected abstract void onSetUpView();

}