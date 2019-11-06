package com.hyphenate.easeui.module.base.widget.message.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseContextCompatUtil;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRow;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowVoice;
import com.hyphenate.easeui.module.base.widget.message.row.EaseChatRowVoicePlayer;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;

/**
 * 语音
 */
public class EaseChatVoicePresenter extends EaseChatFilePresenter {

    private EaseChatRowVoicePlayer voicePlayer;

    @Override
    protected EaseChatRow onCreateChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        voicePlayer = EaseChatRowVoicePlayer.getInstance(context);
        return new EaseChatRowVoice(context, message, position, adapter);
    }

    @Override
    public void onBubbleClick(final EMMessage message) {
        String msgId = message.getMsgId();

        if (voicePlayer.isPlaying()) {
            voicePlayer.stop();
            ((EaseChatRowVoice) getChatRow()).stopVoicePlayAnimation();

            String playingId = voicePlayer.getCurrentPlayingId();
            if (msgId.equals(playingId)) {
                return;
            }
        }

        if (message.direct() == EMMessage.Direct.SEND) {
            String localPath = ((EMVoiceMessageBody) message.getBody()).getLocalUrl();
            File file = new File(localPath);

            if (file.exists() && file.isFile()) {
                playVoice(message);
                ((EaseChatRowVoice) getChatRow()).startVoicePlayAnimation();

            } else {
                asyncDownloadVoice(message);
            }

        } else {
            final String st = EaseContextCompatUtil.getString(R.string.Is_download_voice_click_later);

            if (message.status() == EMMessage.Status.SUCCESS) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    play(message);

                } else {
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();

                    switch (voiceBody.downloadStatus()) {

                        case PENDING:// Download not begin
                        case FAILED:// Download failed
                        {
                            getChatRow().updateView(getMessage());
                            asyncDownloadVoice(message);
                        }
                        break;

                        case DOWNLOADING: {// During downloading
                            EaseToastUtil.show(st);
                        }
                        break;

                        case SUCCESSED: {// Download success
                            play(message);
                        }
                        break;

                        default:
                            break;

                    }
                }

            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                EaseToastUtil.show(st);

            } else if (message.status() == EMMessage.Status.FAIL) {
                EaseToastUtil.show(st);

                asyncDownloadVoice(message);
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }
    }

    private void asyncDownloadVoice(final EMMessage message) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                EMClient.getInstance().chatManager().downloadAttachment(message);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                getChatRow().updateView(getMessage());
            }

        }.execute();
    }

    private void play(EMMessage message) {
        String localPath = ((EMVoiceMessageBody) message.getBody()).getLocalUrl();
        File file = new File(localPath);
        if (file.exists() && file.isFile()) {
            ackMessage(message);
            playVoice(message);
            ((EaseChatRowVoice) getChatRow()).startVoicePlayAnimation();
        }
    }

    private void ackMessage(EMMessage message) {
        EMMessage.ChatType chatType = message.getChatType();

        if (!message.isAcked() && chatType == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());

            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }

        if (!message.isListened()) {
            EMClient.getInstance().chatManager().setVoiceMessageListened(message);
        }
    }

    private void playVoice(EMMessage msg) {
        voicePlayer.play(msg, mp -> {
            ((EaseChatRowVoice) getChatRow()).stopVoicePlayAnimation();
        });
    }

}