package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    protected TextView tv_timestamp;
    protected ImageView iv_avatar;
    protected ViewGroup group_bubble;
    protected TextView tv_username;
    protected TextView tv_percentage;
    protected ProgressBar bar_progress;
    protected ImageView iv_status;
    protected TextView tv_ack;
    protected TextView tv_delivered;

    @Nullable
    protected OnItemClickListener mOnItemClickListener;

    @Nullable
    protected EaseMessageListItemStyle mListItemStyle;

    @Nullable
    private EaseChatRowActionCallback mActionCallback;

    protected EMMessage mMessage;
    protected int mPosition;
    protected BaseAdapter mAdapter;

    protected LayoutInflater mInflater;

    private final Handler mHandler = new Handler();

    public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        mInflater = LayoutInflater.from(context);
        mMessage = message;
        mPosition = position;
        mAdapter = adapter;

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mActionCallback != null) {
            mActionCallback.onDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    public void updateView(final EMMessage msg) {
        mHandler.post(() -> onViewUpdate(msg));
    }

    private void initView() {
        onInflateView();
        tv_timestamp = findViewById(R.id.tv_timestamp);
        iv_avatar = findViewById(R.id.iv_avatar);
        group_bubble = findViewById(R.id.group_bubble);
        tv_username = findViewById(R.id.tv_username);
        bar_progress = findViewById(R.id.bar_progress);
        iv_status = findViewById(R.id.iv_status);
        tv_ack = findViewById(R.id.tv_ack);
        tv_delivered = findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * set property according message and postion
     */
    public void setUpView(EMMessage message,
                          int position,
                          @Nullable OnItemClickListener listener,
                          @Nullable EaseChatRowActionCallback actionCallback,
                          @Nullable EaseMessageListItemStyle itemStyle) {
        mMessage = message;
        mPosition = position;
        mOnItemClickListener = listener;
        mActionCallback = actionCallback;
        mListItemStyle = itemStyle;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        if (tv_timestamp != null) {
            if (mPosition == 0) {
                tv_timestamp.setText(DateUtils.getTimestampString(new Date(mMessage.getMsgTime())));
                tv_timestamp.setVisibility(View.VISIBLE);

            } else {
                // show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = (EMMessage) mAdapter.getItem(mPosition - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(mMessage.getMsgTime(), prevMessage.getMsgTime())) {
                    tv_timestamp.setVisibility(View.GONE);

                } else {
                    tv_timestamp.setText(DateUtils.getTimestampString(new Date(mMessage.getMsgTime())));
                    tv_timestamp.setVisibility(View.VISIBLE);
                }
            }
        }

        //设置头像
        if (iv_avatar != null) {
            String avatar = EaseMessageUtil.getFromAvatar(mMessage, null);

            EaseUserUtil.setUserAvatar(iv_avatar, avatar, mMessage.direct() == Direct.SEND ? R.mipmap.ease_ic_chatfrom_portrait : R.mipmap.ease_ic_chatto_portrait);
        }

        //设置昵称
        if (tv_username != null) {
            String nickname = EaseMessageUtil.getFromNickname(mMessage, mMessage.getFrom());

            tv_username.setText(nickname);
        }

        if (EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            if (tv_delivered != null) {
                if (mMessage.isDelivered()) {
                    tv_delivered.setVisibility(View.VISIBLE);

                } else {
                    tv_delivered.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (EMClient.getInstance().getOptions().getRequireAck()) {
            if (tv_ack != null) {
                if (mMessage.isAcked()) {
                    if (tv_delivered != null) {
                        tv_delivered.setVisibility(View.INVISIBLE);
                    }

                    tv_ack.setVisibility(View.VISIBLE);

                } else {
                    tv_ack.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (mListItemStyle != null) {
            if (iv_avatar != null) {
                EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();

                if (avatarOptions != null && iv_avatar instanceof EaseImageView) {
                    EaseImageView avatarView = ((EaseImageView) iv_avatar);

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

            if (tv_username != null) {
                if (mMessage.direct() == Direct.SEND) {
                    tv_username.setVisibility(mListItemStyle.isShowFromUserNickname() ? View.VISIBLE : View.GONE);

                } else {
                    tv_username.setVisibility(mListItemStyle.isShowToUserNickname() ? View.VISIBLE : View.GONE);
                }
            }
        }


        //test
        if (tv_username != null) {
            tv_username.setVisibility(View.VISIBLE);
        }
        //end
    }

    private void setClickListener() {
        if (group_bubble != null) {
            group_bubble.setOnClickListener(v -> {
                if (mOnItemClickListener != null && mOnItemClickListener.onBubbleClick(mMessage)) {
                    return;
                }
                if (mActionCallback != null) {
                    mActionCallback.onBubbleClick(mMessage);
                }
            });

            group_bubble.setOnLongClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onBubbleLongClick(mMessage);
                }
                return true;
            });
        }

        if (iv_status != null) {
            iv_status.setOnClickListener(v -> {
                if (mOnItemClickListener != null && mOnItemClickListener.onResendClick(mMessage)) {
                    return;
                }
                if (mActionCallback != null) {
                    mActionCallback.onResendClick(mMessage);
                }
            });
        }

        if (iv_avatar != null) {
            iv_avatar.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    if (mMessage.direct() == Direct.SEND) {
                        mOnItemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());

                    } else {
                        mOnItemClickListener.onUserAvatarClick(mMessage.getFrom());
                    }
                }
            });

            iv_avatar.setOnLongClickListener(v -> {
                if (mOnItemClickListener != null) {
                    if (mMessage.direct() == Direct.SEND) {
                        mOnItemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());

                    } else {
                        mOnItemClickListener.onUserAvatarLongClick(mMessage.getFrom());
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

    public interface EaseChatRowActionCallback {

        void onResendClick(EMMessage message);

        void onBubbleClick(EMMessage message);

        void onDetachedFromWindow();

    }

}