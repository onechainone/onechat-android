/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package onemessageui.chat.voice;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.common.base.Charsets;

import java.io.File;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemVoiceItemMessageBodyItem;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.download.DownloadUtils;
import oneapp.onechat.oneandroid.onewallet.util.download.OnDownloadListener;
import oneapp.onecore.graphenej.Util;

public class VoicePlayClickListener implements View.OnClickListener {

    public static String playMsgId;
    ItemMessage message;
    ItemVoiceItemMessageBodyItem voiceBody;
    ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    MediaPlayer mediaPlayer = null;
    ImageView iv_read_status;
    Activity activity;
    private ItemMessage.ChatType chatType;
    private BaseAdapter adapter;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;

    public static void stopCurrentPlay() {
        if (currentPlayListener != null) {
            try {
                if (isPlaying) {
                    currentPlayListener.stopPlayVoice();
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * @param message
     * @param v
     * @param iv_read_status
     * @param activity
     */
    public VoicePlayClickListener(ItemMessage message, ImageView v,
                                  ImageView iv_read_status, BaseAdapter adapter, Activity activity,
                                  String username) {
        this.message = message;
        voiceBody = (ItemVoiceItemMessageBodyItem) message.getBody();
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        voiceIconView = v;
        this.activity = activity;
        this.chatType = message.getChatType();

        if (StringUtils.equalsNull(voiceBody.getLocalUrl()) || !new File(voiceBody.getLocalUrl()).exists()) {
            downloadRemoteFile(false);
        }
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if (message.direct == ItemMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
        } else {
            voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        playMsgId = null;
        adapter.notifyDataSetChanged();
    }

    private void playVoice(String filePath) {
        if (filePath == null || !(new File(filePath).exists())) {
            ToastUtils.simpleToast(R.string.file_overdue);
            return;
        }
        playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity
                .getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        boolean ifUserSpeaker = true;//EMChatManager.getInstance().getChatOptions().getUseSpeaker()
        if (ifUserSpeaker) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            isPlaying = true;
            currentPlayListener = this;
            mediaPlayer.start();
            showAnimation();

            // 如果是接收的消息
            if (message.direct == ItemMessage.Direct.RECEIVE) {
                try {
                    if (!message.isAcked) {
                        message.isAcked = true;
                        // 告知对方已读这条消息
                    }
                } catch (Exception e) {
                    message.isAcked = false;
                }
                if (!message.isListened() && iv_read_status != null
                        && iv_read_status.getVisibility() == View.VISIBLE) {
                    // 隐藏自己未播放这条语音消息的标志
                    voiceBody.setIfClick(true);
                    OneAccountHelper.getDatabase().updateUserChatJsonParam(message.getMsgId(), GsonUtils.objToJson(voiceBody));
                    iv_read_status.setVisibility(View.GONE);
//					EMChatManager.getInstance().setMessageListened(message);
                }

            }

        } catch (Exception e) {
        }
    }

    // show the voice playing animation
    private void showAnimation() {
        // play voice, and start animation
        if (message.direct == ItemMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceIconView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v) {
        String st = activity.getResources().getString(
                R.string.landscape);
        if (isPlaying) {
            if (playMsgId != null
                    && playMsgId.equals(message
                    .getMsgId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }
        String localUrl = voiceBody.getLocalUrl();
        File file = null;
        if (!StringUtils.equalsNull(localUrl)) {
            file = new File(localUrl);
        }
        if (file != null && file.exists() && file.isFile()) {
            playVoice(localUrl);
        } else {
            downloadRemoteFile(true);
        }
    }

    private void downloadRemoteFile(final boolean isClick) {
        if (!NetUtils.hasNetwork(OneAccountHelper.getContext())) {
            return;
        }
        String remotePath = voiceBody.getRemoteUrl();
        DownloadUtils.download(remotePath, PathUtil.getInstance().getDownloadPath(), false, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                if (isClick && file == null) {
                    ToastUtils.simpleToast(R.string.file_overdue);
                    return;
                }
                try {
                    byte[] voiceByte = BaseUtils.File2byte(file.getPath());
                    byte[] decryptVoiceByte = Util.decryptAES(voiceByte, voiceBody.getEncryptKey().getBytes(Charsets.UTF_8));

                    File decryptVoiceFile = BaseUtils.byte2File(decryptVoiceByte, PathUtil.getInstance().getVoicePath(), file.getName());

                    voiceBody.setLocalUrl(decryptVoiceFile.getPath());
                    message.addBody(voiceBody);

                    OneAccountHelper.getDatabase().updateUserChatJsonParam(message.getMsgId(), GsonUtils.objToJson(voiceBody));

                    boolean b = file.delete();
                    if (isClick) {
                        playVoice(voiceBody.getLocalUrl());
                    }
                } catch (Exception e) {
                    if (isClick) {
                        ToastUtils.simpleToast(R.string.file_overdue);
                    }
                }
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                if (isClick) {
                    ToastUtils.simpleToast(R.string.file_overdue);
                }
            }

            @Override
            public void onStartDownload() {

            }
        });
    }
}