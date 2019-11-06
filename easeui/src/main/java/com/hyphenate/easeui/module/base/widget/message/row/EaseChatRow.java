package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
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

    protected TextView tv_timestamp;//时间
    protected ImageView iv_avatar;//头像
    protected ViewGroup group_bubble;//消息内容
    protected TextView tv_username;//用户名
    protected TextView tv_percentage;//进度百分比
    protected ProgressBar bar_progress;//进度条
    protected ImageView iv_status;//消息状态
    protected TextView tv_ack;//已读标识
    protected TextView tv_delivered;//到达标识

    @Nullable
    protected OnItemClickListener mOnItemClickListener;

    @Nullable
    protected EaseMessageListItemStyle mListItemStyle;

    @Nullable
    private EaseChatRowActionCallback mActionCallback;

    protected EMMessage mMessage;
    protected int mPosition;
    protected BaseAdapter mAdapter;

    public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        mMessage = message;
        mPosition = position;
        mAdapter = adapter;

        initView(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mActionCallback != null) {
            mActionCallback.onDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = onInflateView(inflater);

        tv_timestamp = view.findViewById(R.id.tv_timestamp);
        iv_avatar = view.findViewById(R.id.iv_avatar);
        group_bubble = view.findViewById(R.id.group_bubble);
        tv_username = view.findViewById(R.id.tv_username);
        bar_progress = view.findViewById(R.id.bar_progress);
        iv_status = view.findViewById(R.id.iv_status);
        tv_ack = view.findViewById(R.id.tv_ack);
        tv_delivered = view.findViewById(R.id.tv_delivered);

        onFindViewById(view);
    }

    public void updateView(EMMessage message) {
        onViewUpdate(message);
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
        //设置时间
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

        //设置到达标识
        if (EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            if (tv_delivered != null) {
                if (mMessage.isDelivered()) {
                    tv_delivered.setVisibility(View.VISIBLE);

                } else {
                    tv_delivered.setVisibility(View.INVISIBLE);
                }
            }
        }

        //设置已读标识
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

    protected abstract View onInflateView(LayoutInflater inflater);

    protected abstract void onFindViewById(View view);

    protected abstract void onViewUpdate(EMMessage msg);

    protected abstract void onSetUpView();

    public interface EaseChatRowActionCallback {

        void onResendClick(EMMessage message);

        void onBubbleClick(EMMessage message);

        void onDetachedFromWindow();

    }

}