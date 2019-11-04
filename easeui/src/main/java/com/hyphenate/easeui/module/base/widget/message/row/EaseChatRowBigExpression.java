package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.bean.EaseEmojicon;

/**
 * big emoji icons
 */
public class EaseChatRowBigExpression extends EaseChatRowText {

    private ImageView iv_image;

    public EaseChatRowBigExpression(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        mInflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_bigexpression : R.layout.ease_row_sent_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        tv_percentage = findViewById(R.id.tv_percentage);
        iv_image = findViewById(R.id.iv_image);
    }

    @Override
    public void onSetUpView() {
        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
        EaseEmojicon emojicon = null;
        if (EaseUI.getInstance().getEmojiconInfoProvider() != null) {
            emojicon = EaseUI.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
        }

        if (emojicon != null) {
            if (emojicon.getBigIcon() != 0) {
                Glide.with(activity)
                        .load(emojicon.getBigIcon())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                        .into(iv_image);

            } else if (emojicon.getBigIconPath() != null) {
                Glide.with(activity)
                        .load(emojicon.getBigIconPath())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_expression))
                        .into(iv_image);

            } else {
                iv_image.setImageResource(R.drawable.ease_default_expression);
            }
        }
    }

}