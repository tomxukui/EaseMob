package com.hyphenate.easeui.utils;

import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;

public class EaseCommonUtils {

    public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode) {
        EMMessage message = EMMessage.createTxtSendMessage("[" + expressioName + "]", toChatUsername);
        if (identityCode != null) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
        }
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
    }

    /**
     * 获取消息摘要
     */
    public static String getMessageDigest(EMMessage message) {
        String digest;

        switch (message.getType()) {

            case LOCATION: {
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    digest = EaseContextCompatUtil.getString(R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;

                } else {
                    digest = EaseContextCompatUtil.getString(R.string.location_prefix);
                }
            }
            break;

            case IMAGE: {
                digest = EaseContextCompatUtil.getString(R.string.picture);
            }
            break;

            case VOICE: {
                digest = EaseContextCompatUtil.getString(R.string.voice_prefix);
            }
            break;

            case VIDEO: {
                digest = EaseContextCompatUtil.getString(R.string.video);
            }
            break;

            case TXT: {
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();

                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    digest = EaseContextCompatUtil.getString(R.string.voice_call) + txtBody.getMessage();

                } else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    digest = EaseContextCompatUtil.getString(R.string.video_call) + txtBody.getMessage();

                } else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    if (!TextUtils.isEmpty(txtBody.getMessage())) {
                        digest = txtBody.getMessage();

                    } else {
                        digest = EaseContextCompatUtil.getString(R.string.dynamic_expression);
                    }

                } else {
                    digest = txtBody.getMessage();
                }
            }
            break;

            case FILE: {
                digest = EaseContextCompatUtil.getString(R.string.file);
            }
            break;

            default:
                return "";

        }

        return digest;
    }

    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     *
     * @param message return
     *                <p>
     *                \~english
     *                check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     * @param message
     * @return
     */
    public static boolean isSilentMessage(EMMessage message) {
        return message.getBooleanAttribute("em_ignore_notification", false);
    }

}