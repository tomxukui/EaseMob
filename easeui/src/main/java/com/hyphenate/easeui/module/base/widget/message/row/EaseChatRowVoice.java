package com.hyphenate.easeui.module.base.widget.message.row;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.R;

public class EaseChatRowVoice extends EaseChatRowFile {

    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStatusView;

    private AnimationDrawable voiceAnimation;

    public EaseChatRowVoice(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected View onInflateView(LayoutInflater inflater) {
        return inflater.inflate(mMessage.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_voice : R.layout.ease_row_sent_voice, this);
    }

    @Override
    protected void onFindViewById(View view) {
        voiceImageView = view.findViewById(R.id.iv_voice);
        voiceLengthView = view.findViewById(R.id.tv_length);
        readStatusView = view.findViewById(R.id.iv_unread_voice);
    }

    @Override
    protected void onSetUpView() {
        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) mMessage.getBody();

        if (voiceBody.getLength() > 0) {
            voiceLengthView.setText(voiceBody.getLength() + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);

        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }

        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);

        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
        }

        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (mMessage.isListened()) {
                // hide the unread icon
                readStatusView.setVisibility(View.INVISIBLE);

            } else {
                readStatusView.setVisibility(View.VISIBLE);
            }

            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    bar_progress.setVisibility(View.VISIBLE);

                } else {
                    bar_progress.setVisibility(View.INVISIBLE);
                }

            } else {
                bar_progress.setVisibility(View.INVISIBLE);
            }
        }

        // To avoid the item is recycled by listview and slide to this item again but the animation is stopped.
        EaseChatRowVoicePlayer voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext());
        if (voicePlayer.isPlaying() && mMessage.getMsgId().equals(voicePlayer.getCurrentPlayingId())) {
            startVoicePlayAnimation();
        }
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);
        // Only the received message has the attachment download status.
        if (mMessage.direct() == EMMessage.Direct.SEND) {
            return;
        }

        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) msg.getBody();
        if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING || voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            bar_progress.setVisibility(View.VISIBLE);

        } else {
            bar_progress.setVisibility(View.INVISIBLE);
        }
    }

    public void startVoicePlayAnimation() {
        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.voice_from_icon);

        } else {
            voiceImageView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
        voiceAnimation.start();

        // Hide the voice item not listened status view.
        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            readStatusView.setVisibility(View.INVISIBLE);
        }
    }

    public void stopVoicePlayAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }

        if (mMessage.direct() == EMMessage.Direct.RECEIVE) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);

        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
        }
    }

}