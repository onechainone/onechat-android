package onemessageui.chat.adpter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;

import java.io.File;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.rockerhieu.emojicon.EmojiconTextView;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.database.SCWallDatabaseContract;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemAssetItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemConversation;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemFileItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemImageItemMessageBodyItem;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemLocationItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemNormalItemFileItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemRedPacketMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemTextItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemVideoItemMessageBodyItem;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemVoiceItemMessageBodyItem;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.DateUtils;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.FileUtils;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.LatLng;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.TextFormater;
import onemessageui.chat.utils.Constant;
import onemessageui.chat.utils.ImageCache;
import onemessageui.chat.utils.ImageUtils;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.download.DownloadUtils;
import oneapp.onechat.oneandroid.onewallet.util.download.OnDownloadListener;
import oneapp.onecore.graphenej.Util;
import onemessageui.chat.ContextMenu;
import onemessageui.chat.ShowBigImage;
import onemessageui.chat.ShowNormalFileActivity;
import onemessageui.chat.task.LoadImageTask;
import onemessageui.chat.task.LoadVideoImageTask;
import onemessageui.chat.utils.LinkifySpannableUtils;
import onemessageui.chat.voice.VoicePlayClickListener;
import onemessageui.dialog.DialogUtil;
import onemessageui.utils.CommonUtils;
import onemessageui.view.activity.TencentMapActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

import static oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage.Type.TXT;

//import oneapp.onemessage.chat.TencentMapActivity;

public class MessageAdapter extends BaseAdapter {

    private final static String TAG = "msg";

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;
    private static final int MESSAGE_TYPE_SENT_ASSET = 16;
    private static final int MESSAGE_TYPE_RECV_ASSET = 17;
    private static final int MESSAGE_TYPE_SENT_RED_PACKET = 18;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 19;

    private static final int MESSAGE_TYPE_NUM = 20;

    public static final String IMAGE_DIR = "chat/image/";
    public static final String VOICE_DIR = "chat/audio/";
    public static final String VIDEO_DIR = "chat/video";

    private String username;
    private LayoutInflater inflater;
    private Activity activity;

    private UserContactItem userContactItem;
    private UserGroupInfoItem mGroupInfo;

    // reference to conversation object in chatsdk
    private ItemConversation conversation;
    private Context context;

    private Map<String, Timer> timers = new Hashtable<>();
    private Map<String, ViewHolder> mapUserView = new Hashtable<String, ViewHolder>();
    private MyDialogListener myDialogListener;

    public interface MyDialogListener {
        void refreshActivity(String fromId);
    }

    public void setMyDialogListener(MyDialogListener myDialogListener) {
        this.myDialogListener = myDialogListener;
    }

    public MessageAdapter(Context context, String username, int chatType) {
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;

        // FIXME: 2017/8/1
//		this.conversation = getInstance().getConversation(
//				username);
    }

    public MessageAdapter(Context context, String username, int chatType, ItemConversation conversation) {
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
        this.conversation = conversation;
        userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(username);

    }

    // public void setUser(String user) {
    // this.user = user;
    // }

    /**
     * 获取item数
     */
    public int getCount() {
        return conversation.getMsgCount();
    }

    /**
     * 刷新页面
     */
    public void refresh() {

        notifyDataSetChanged();
    }

    public void refreshGroupInfo() {
        if (mGroupInfo != null) {
            mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(mGroupInfo.group_uid, false);
        }
        refresh();
    }

    /**
     * 刷新页面
     */
    public void replace(ItemConversation conversation) {

        this.conversation = conversation;
        notifyDataSetChanged();
    }

    public ItemMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取item类型
     */
    public int getItemViewType(int position) {
        ItemMessage message = conversation.getMessage(position);
        if (message.getType() == TXT) {
            if (message.getBooleanAttribute(
                    Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
                return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL
                        : MESSAGE_TYPE_SENT_VOICE_CALL;
            else if (message.getBooleanAttribute(
                    Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL
                        : MESSAGE_TYPE_SENT_VIDEO_CALL;
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT
                    : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == ItemMessage.Type.IMAGE) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE
                    : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == ItemMessage.Type.LOCATION) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION
                    : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == ItemMessage.Type.VOICE) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE
                    : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == ItemMessage.Type.VIDEO) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO
                    : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == ItemMessage.Type.FILE) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE
                    : MESSAGE_TYPE_SENT_FILE;
        }
        if (message.getType() == ItemMessage.Type.ASSET) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ASSET
                    : MESSAGE_TYPE_SENT_ASSET;
        }
        if (message.getType() == ItemMessage.Type.RED_PACKET) {
            return message.direct == ItemMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET
                    : MESSAGE_TYPE_SENT_RED_PACKET;
        }

        return -1;// invalid
    }

    public int getViewTypeCount() {
        return MESSAGE_TYPE_NUM;
    }

    private View createViewByMessage(ItemMessage message, int position) {
        switch (message.getType()) {
            case LOCATION:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_location, null) : inflater
                        .inflate(R.layout.row_sent_location, null);
            case IMAGE:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_picture, null) : inflater
                        .inflate(R.layout.row_sent_picture, null);

            case VOICE:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_voice, null) : inflater
                        .inflate(R.layout.row_sent_voice, null);
            case VIDEO:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_video, null) : inflater
                        .inflate(R.layout.row_sent_video, null);
            case FILE:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_file, null) : inflater
                        .inflate(R.layout.row_sent_file, null);
            case ASSET:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_asset, null) : inflater
                        .inflate(R.layout.row_sent_asset, null);
            case RED_PACKET:
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_red_packet, null) : inflater
                        .inflate(R.layout.row_sent_red_packet, null);
            default:
                // 语音通话
                if (message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
                    return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                            .inflate(R.layout.row_received_voice_call, null)
                            : inflater.inflate(R.layout.row_sent_voice_call, null);
                    // 视频通话
                else if (message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                    return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                            .inflate(R.layout.row_received_video_call, null)
                            : inflater.inflate(R.layout.row_sent_video_call, null);
                return message.direct == ItemMessage.Direct.RECEIVE ? inflater
                        .inflate(R.layout.row_received_message, null) : inflater
                        .inflate(R.layout.row_sent_message, null);
        }
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemMessage message = getItem(position);
        ItemMessage.ChatType chatType = message.getChatType();
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position);
            if (message.getType() == ItemMessage.Type.IMAGE) {
                try {
                    holder.iv = ((ImageView) convertView
                            .findViewById(R.id.iv_sendPicture));
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.progressBar);
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                    holder.tv_img_status = (TextView) convertView.findViewById(R.id.tv_img_status);
                } catch (Exception e) {
                }

            } else if (message.getType() == TXT) {

                try {
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    // 这里是文字内容
                    holder.tv = (EmojiconTextView) convertView
                            .findViewById(R.id.tv_chatcontent);
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }

                // 语音通话及视频通话
                if (message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                        || message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    holder.iv = (ImageView) convertView
                            .findViewById(R.id.iv_call_icon);
                    holder.tv = (EmojiconTextView) convertView
                            .findViewById(R.id.tv_chatcontent);
                }

            } else if (message.getType() == ItemMessage.Type.VOICE) {
                try {
                    holder.iv = ((ImageView) convertView
                            .findViewById(R.id.iv_voice));
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.tv = (TextView) convertView
                            .findViewById(R.id.tv_length);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                    holder.ll_container = convertView
                            .findViewById(R.id.ll_voice_container);
                    holder.iv_read_status = (ImageView) convertView
                            .findViewById(R.id.iv_unread_voice);
                } catch (Exception e) {
                }
            } else if (message.getType() == ItemMessage.Type.LOCATION) {
                try {
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.tv = (TextView) convertView
                            .findViewById(R.id.tv_location);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }
            } else if (message.getType() == ItemMessage.Type.VIDEO) {
                try {
                    holder.iv = ((ImageView) convertView
                            .findViewById(R.id.chatting_content_iv));
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.tv = (TextView) convertView
                            .findViewById(R.id.percentage);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.progressBar);
                    holder.size = (TextView) convertView
                            .findViewById(R.id.chatting_size_iv);
                    holder.timeLength = (TextView) convertView
                            .findViewById(R.id.chatting_length_iv);
                    holder.playBtn = (ImageView) convertView
                            .findViewById(R.id.chatting_status_btn);
                    holder.container_status_btn = (LinearLayout) convertView
                            .findViewById(R.id.container_status_btn);
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);

                } catch (Exception e) {
                }
            } else if (message.getType() == ItemMessage.Type.FILE) {
                try {
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.tv_file_name = (TextView) convertView
                            .findViewById(R.id.tv_file_name);
                    holder.tv_file_size = (TextView) convertView
                            .findViewById(R.id.tv_file_size);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    holder.tv_file_download_state = (TextView) convertView
                            .findViewById(R.id.tv_file_state);
                    holder.ll_container = convertView
                            .findViewById(R.id.ll_file_container);
                    // 这里是进度值
                    holder.tv = (TextView) convertView
                            .findViewById(R.id.percentage);
                } catch (Exception e) {
                }
                try {
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }

            } else if (message.getType() == ItemMessage.Type.ASSET) {
                try {
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    holder.tv_asset_symbol = (TextView) convertView
                            .findViewById(R.id.tv_asset_symbol);
                    holder.tv_asset_value = (TextView) convertView
                            .findViewById(R.id.tv_asset_value);
                } catch (Exception e) {
                }
            } else if (message.getType() == ItemMessage.Type.RED_PACKET) {
                try {
                    holder.tv_userId = (TextView) convertView
                            .findViewById(R.id.tv_userid);
                    holder.head_iv = (ImageView) convertView
                            .findViewById(R.id.iv_userhead);
                    holder.head_iv_bg = (ImageView) convertView
                            .findViewById(R.id.iv_userhead_bg);
                    holder.pb = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    holder.ll_container = convertView
                            .findViewById(R.id.ll_red_packet_container);
                    holder.tv_red_packet_msg = (TextView) convertView.findViewById(R.id.tv_red_packet_msg);
                } catch (Exception e) {
                }
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (message.direct == ItemMessage.Direct.SEND) {
            holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
        }

        if (chatType == ItemMessage.ChatType.GroupChat) {
            if (mGroupInfo == null) {
                mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(message.getGroupUid(), false);
            }
            if (mGroupInfo == null) {
                holder.head_iv_bg.setVisibility(View.GONE);
            } else if (StringUtils.equals(mGroupInfo.owner, message.getFrom())) {
                holder.head_iv_bg.setVisibility(View.VISIBLE);
                holder.head_iv_bg.setImageResource(R.drawable.group_owner_avatar_bg);
            } else if (mGroupInfo.getGroupAdminMap().containsKey(message.getFrom())) {
                holder.head_iv_bg.setVisibility(View.VISIBLE);
                holder.head_iv_bg.setImageResource(R.drawable.group_admin_avatar_bg);
            } else {
                holder.head_iv_bg.setVisibility(View.GONE);
            }
        } else {
            holder.head_iv_bg.setVisibility(View.GONE);
        }

        // 小密圈时，显示接收的消息的发送人的名称
        if (chatType == ItemMessage.ChatType.GroupChat
                && message.direct == ItemMessage.Direct.RECEIVE) {
            userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(message.getFrom());

            holder.tv_userId.setVisibility(View.VISIBLE);
            if (userContactItem != null) {
                holder.tv_userId.setText(userContactItem.getUserName());
            } else {
                //查询该群员
                OneChatHelper.RequestUserInfoById(message.getFrom(), new RequestSuccessListener<UserInfoBean>() {
                    @Override
                    public void onResponse(UserInfoBean userInfoBean) {
                        if (userInfoBean != null) {
                            holder.tv_userId.setText(userInfoBean.getNickname());
                        }
                    }
                });
                holder.tv_userId.setText(message.getFrom());
            }
        } else {
            if (message.direct == ItemMessage.Direct.RECEIVE)
                holder.tv_userId.setVisibility(View.GONE);
        }
        // 如果是发送的消息并且不是小密圈消息，显示已读textview
        if (message.direct == ItemMessage.Direct.SEND
                && chatType != ItemMessage.ChatType.GroupChat) {
            holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
            holder.tv_delivered = (TextView) convertView
                    .findViewById(R.id.tv_delivered);
            if (holder.tv_ack != null) {
                if (message.isAcked) {
                    if (holder.tv_delivered != null) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                    holder.tv_ack.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);

                    // check and display msg delivered ack status
                    if (holder.tv_delivered != null) {
                        if (message.isDelivered) {
                            holder.tv_delivered.setVisibility(View.VISIBLE);
                        } else {
                            holder.tv_delivered.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        } else {
            // 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
            if ((message.getType() == TXT || message.getType() == ItemMessage.Type.LOCATION)
                    && !message.isAcked && chatType != ItemMessage.ChatType.GroupChat) {
                // 不是语音通话记录
                if (!message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    try {
                        // hhr
//						getInstance().ackMessageRead(
//								message.getFrom(), message.getMsgId());
                        // 发送已读回执
                        message.isAcked = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            switch (message.getType()) {
                // 根据消息type显示item
                case IMAGE: // 图片
                    handleImageMessage(message, holder, position, convertView);
                    break;
                case TXT: // 文本
                    if (message.getBooleanAttribute(
                            Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                            || message.getBooleanAttribute(
                            Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                        // 音视频通话
                        handleCallMessage(message, holder, position);
                    else
                        handleTextMessage(message, holder, position);
                    break;
                case LOCATION: // 位置
                    handleLocationMessage(message, holder, position, convertView);
                    break;
                case VOICE: // 语音
                    handleVoiceMessage(message, holder, position, convertView);
                    break;
                case VIDEO: // 视频
//                    handleVideoMessage(message, holder, position, convertView);
                    break;
                case FILE: // 一般文件
//                    handleFileMessage(message, holder, position, convertView);
                    break;
                case ASSET: // 资产
                    handleAssetMessage(message, holder, position, convertView);
                    break;
                case RED_PACKET: // 红包
                    handleRedPacketMessage(message, holder, position, convertView);
                    break;
                default:
                    // not supported
            }
        } catch (Exception e) {

        }
        // FIXME: 2017/11/10 hs
        try {
            if (mapUserView.containsKey(message.getMsgId())) {
                mapUserView.remove(message.getMsgId());
            }
            mapUserView.put(message.getMsgId(), holder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (message.direct == ItemMessage.Direct.SEND) {
            holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//            final View statusView = convertView.findViewById(R.id.msg_status);
            // 重发按钮点击事件
            holder.staus_iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        OneAccountHelper.getDatabase().updateUserChatStatusSend(message.getMsgId(),
                                ItemMessage.Status.CREATE.ordinal());
                        holder.pb.setVisibility(View.VISIBLE);
                        holder.staus_iv.setVisibility(View.GONE);
                        if (message.getChatType() == ItemMessage.ChatType.Chat) {
                            OneChatHelper.startMessageSender();

                        } else if (message.getChatType() == ItemMessage.ChatType.GroupChat) {
                            if (mGroupInfo == null) {
                                mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(message.getGroupUid(), false);
                            }
                            String strJson = OneAccountHelper.getDatabase().getUserChatByUuid(message.getMsgId(), SCWallDatabaseContract.UserChat.COLUMN_MEMO_MESSAGE);
                            String encryptStrJson = Util.bytesToHex(Util.encryptAES(strJson.getBytes(Charsets.UTF_8), mGroupInfo.encrypt_key.getBytes(Charsets.UTF_8)));
                            String msgType = "";
                            //添加群聊请求
                            String messageContent = "";

                            if (message.getType() == ItemMessage.Type.TXT) {
                                if (mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
                                    ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();
                                    messageContent = txtBody.getMessage();
                                }
                                msgType = CommonUtils.getMsgTypeByMemoString(messageContent);
                            } else if (message.getType() == ItemMessage.Type.VOICE) {
                                msgType = Constants.MSG_TYPE_VOICE;
                            } else if (message.getType() == ItemMessage.Type.IMAGE) {
                                msgType = Constants.MSG_TYPE_IMG;
                            }
                            OneGroupHelper.AddGroupMessageInfo(message.getGroupUid(), encryptStrJson, messageContent, msgType, message.getMsgId(), null);
                        }
                        // Toast.makeText(activity, R.string.message_resend, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonHelperUtils.updateMessageStatus(message.getMsgId(), ItemMessage.Status.FAIL.ordinal());
                    }

//					// 显示重发消息的自定义alertdialog
//					Intent intent = new Intent(activity, AlertDialog.class);
//					intent.putExtra("msg",
//							activity.getString(R.string.confirm_resend));
//					intent.putExtra("title",
//							activity.getString(R.string.resend));
//					intent.putExtra("cancel", true);
//					intent.putExtra("position", position);
//					if (message.getType() == ItemMessage.Type.TXT)
//						activity.startActivityForResult(intent,
//								CommonUtils.REQUEST_CODE_TEXT);
//					else if (message.getType() == ItemMessage.Type.VOICE)
//						activity.startActivityForResult(intent,
//								CommonUtils.REQUEST_CODE_VOICE);
//					else if (message.getType() == ItemMessage.Type.IMAGE)
//						activity.startActivityForResult(intent,
//								CommonUtils.REQUEST_CODE_PICTURE);
//					else if (message.getType() == ItemMessage.Type.LOCATION)
//						activity.startActivityForResult(intent,
//								CommonUtils.REQUEST_CODE_LOCATION);
//					else if (message.getType() == ItemMessage.Type.FILE)
//						activity.startActivityForResult(intent,
//								CommonUtils.REQUEST_CODE_FILE);
//					else if (message.getType() == ItemMessage.Type.VIDEO)
//						activity.startActivityForResult(intent,
//								CommonUtils.REQUEST_CODE_VIDEO);

                }
            });

        } else {
            final String st = context.getResources().getString(
                    R.string.Into_the_blacklist);

            // 长按头像，移入黑名单
//            holder.head_iv.setOnLongClickListener(new OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//					Intent intent = new Intent(activity, AlertDialog.class);
//					intent.putExtra("msg", st);
//					intent.putExtra("cancel", true);
//					intent.putExtra("position", position);
//					activity.startActivityForResult(intent,
//							CommonUtils.REQUEST_CODE_ADD_TO_BLACKLIST);


//                    if (message.getChatType() == ItemMessage.ChatType.GroupChat && mGroupInfo != null && mGroupInfo.owner != null && mGroupInfo.owner.equals(BtsHelper.mMeAccountId)) {
//                        DialogUtil.simpleDialog(context, context.getString(R.string.sure_delete_user), new DialogUtil.ConfirmCallBackInf() {
//                            @Override
//                            public void onConfirmClick(String content) {
//                                List<String> deleteMemberIds = new ArrayList<>();
//                                deleteMemberIds.add(message.getFrom());
//
//                                //更新群聊请求
//                                RequestUtils.DeleteGroupMemberRequest(message.getGroupUid(), deleteMemberIds, new RequestSuccessListener<Boolean>() {
//                                    @Override
//                                    public void onResponse(Boolean result) {
//                                        if (result) {
//                                            ToastUtils.simpleToast(R.string.delete_success);
//                                        } else {
//                                            ToastUtils.simpleToast(R.string.delete_failed);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    activity.startActivityForResult((new Intent(activity,
//                                    ContextMenu.class)).putExtra("position", position)
//                                    .putExtra("chattype", ItemMessage.ChatType.GroupChat.ordinal()),
//                            CommonUtils.REQUEST_CODE_HEADIMG_MENU);
//                    }

//                    return true;
//                }
//            });
            holder.head_iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击头像
                    myDialogListener.refreshActivity(message.getFrom());

                }
            });
        }

        if (message.getFrom().equals(OneAccountHelper.getAccountId())) {
            onewalletui.util.ImageUtils.displayAvatarNetImage(context, UserInfoUtils.getUserAvatar(), holder.head_iv, UserInfoUtils.getUserInfo().getSex());
        } else {
            if (userContactItem != null) {
                onewalletui.util.ImageUtils.displayAvatarNetImage(context, userContactItem.avatar, holder.head_iv, userContactItem.getSex());
            } else {
                holder.head_iv.setImageResource(R.drawable.default_head_man);
            }
        }

        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);

        if (position == 0) {
            timestamp.setText(TimeUtils.toSecondTimeString(message.getMsgTime()));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(message.getMsgTime(), conversation
                    .getMessage(position - 1).getMsgTime())) {
                timestamp.setVisibility(View.GONE);
            } else {
                timestamp.setText(TimeUtils.toSecondTimeString(message.getMsgTime()));
                timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    /**
     * 文本消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(final ItemMessage message, ViewHolder holder,
                                   final int position) {
        ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();
        // FIXME: 2017/12/19 hs
        holder.tv.setText(txtBody.getMessage());
        LinkifySpannableUtils.getInstance().setSpan(context, holder.tv);
        // 设置长按事件监听
        holder.tv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult((new Intent(activity,
                                ContextMenu.class)).putExtra("position", position)
                                .putExtra("type", TXT.ordinal()).putExtra(JumpParamsContants.INTENT_CHAT_TYPE, message.getChatType().ordinal()),
                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == ItemMessage.Direct.SEND) {
//            holder.pb.setTag(position);
            switch (message.status) {
                case SUCCESS: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // FIXME: 2017/8/1
                    // 发送消息
//				sendMsgInBackground(message, holder);

                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);

//				holder.pb.setVisibility(View.INVISIBLE);
//				holder.staus_iv.setVisibility(View.GONE);
                    break;
            }
        }

    }

    private ViewHolder getUserView(String uuid) {
        ViewHolder tempView = null;

        try {
            tempView = mapUserView.get(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            tempView = null;
        }

        return tempView;
    }

    public void updateMessageStatus(ItemMessage newMessage) {
        ViewHolder tempView = null;

        try {
            tempView = getUserView(newMessage.getMsgId());
            if (tempView != null) {
                if (newMessage.status == ItemMessage.Status.FAIL) {
                    tempView.pb.setVisibility(View.GONE);
                    tempView.staus_iv.setVisibility(View.VISIBLE);
                } else if (newMessage.status == ItemMessage.Status.CREATE) {
                    tempView.pb.setVisibility(View.VISIBLE);
                    tempView.staus_iv.setVisibility(View.GONE);
                } else {
                    tempView.pb.setVisibility(View.GONE);
                    tempView.staus_iv.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            tempView = null;
        }
    }


    /**
     * 音视频通话记录
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleCallMessage(ItemMessage message, ViewHolder holder,
                                   final int position) {
        ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();
        holder.tv.setText(txtBody.getMessage());

    }

    /**
     * 图片消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleImageMessage(final ItemMessage message,
                                    final ViewHolder holder, final int position, View convertView) {
        holder.iv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult((new Intent(activity,
                                ContextMenu.class)).putExtra("position", position)
                                .putExtra("type", ItemMessage.Type.IMAGE.ordinal()).putExtra(JumpParamsContants.INTENT_CHAT_TYPE, message.getChatType().ordinal()),
                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        holder.iv.setImageResource(R.drawable.default_msg_img);
        holder.tv_img_status.setVisibility(View.VISIBLE);
        holder.tv_img_status.setText(R.string.loading);
//        holder.pb.setVisibility(View.VISIBLE);
        final ItemImageItemMessageBodyItem imgBody = (ItemImageItemMessageBodyItem) message.getBody();
        if (imgBody.getLocalUrl() != null && new File(imgBody.getLocalUrl()).exists()) {
            // String filePath = imgBody.getLocalUrl();
            String localPath = imgBody.getLocalUrl();
            showImageView(localPath, holder.iv, localPath,
                    imgBody.getRemoteUrl(), message, holder.tv_img_status);

            if (message.direct == ItemMessage.Direct.RECEIVE) {
                holder.pb.setVisibility(View.GONE);
            }
        } else {
            final String remotePath = imgBody.getRemoteUrl();
            if (!StringUtils.equalsNull(remotePath)) {
                if (NetUtils.hasNetwork(context) && !ImageUtils.mLoadingImgCatch.containsKey(message.getMsgId())) {
                    ImageUtils.mLoadingImgCatch.put(message.getMsgId(), ImageUtils.IMG_STATUS_LOADING_NET);
                    DownloadUtils.download(remotePath, PathUtil.getInstance().getDownloadPath(), false, new OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(File file) {

                            try {
                                byte[] imgByte = BaseUtils.File2byte(file.getPath());
                                byte[] decryptImgByte = Util.decryptAES(imgByte, imgBody.getEncryptKey().getBytes(Charsets.UTF_8));

                                File decryptImgFile = BaseUtils.byte2File(decryptImgByte, PathUtil.getInstance().getImagePath(), file.getName());

                                showImageView(decryptImgFile.getPath(),
                                        holder.iv, decryptImgFile.getPath(), remotePath, message, holder.tv_img_status);

                                imgBody.setLocalUrl(decryptImgFile.getPath());
                                OneAccountHelper.getDatabase().updateUserChatJsonParam(message.getMsgId(), GsonUtils.objToJson(imgBody));

                                if (message.direct == ItemMessage.Direct.RECEIVE) {
                                    holder.pb.setVisibility(View.GONE);
                                }
                                boolean b = file.delete();
                            } catch (Exception e) {
                                //过期
                                holder.iv.setImageResource(R.drawable.default_msg_img);
                                holder.tv_img_status.setVisibility(View.VISIBLE);
                                holder.tv_img_status.setText(R.string.img_overdue);
                                ImageUtils.mLoadingImgCatch.remove(message.getMsgId());
                            }
                        }

                        @Override
                        public void onDownloading(int progress) {

                        }

                        @Override
                        public void onDownloadFailed() {
                            //过期
                            holder.iv.setImageResource(R.drawable.default_msg_img);
                            holder.tv_img_status.setVisibility(View.VISIBLE);
                            holder.tv_img_status.setText(R.string.img_overdue);
                            ImageUtils.mLoadingImgCatch.remove(message.getMsgId());
                        }

                        @Override
                        public void onStartDownload() {
                        }
                    });
                }
            } else {
                //过期
                holder.iv.setImageResource(R.drawable.default_msg_img);
                holder.tv_img_status.setVisibility(View.VISIBLE);
                holder.tv_img_status.setText(R.string.img_overdue);
            }
        }

        if (message.direct == ItemMessage.Direct.SEND) {
//            holder.pb.setTag(position);
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    holder.staus_iv.setVisibility(View.GONE);
                    holder.pb.setVisibility(View.VISIBLE);
                    break;
                default:
                    sendPictureMessage(message, holder);
                    break;
            }
        }
    }


    /**
     * 语音消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVoiceMessage(final ItemMessage message,
                                    final ViewHolder holder, final int position, View convertView) {
        holder.ll_container.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult((new Intent(activity,
                                ContextMenu.class)).putExtra("position", position)
                                .putExtra(JumpParamsContants.INTENT_CHAT_TYPE, message.getChatType().ordinal()).putExtra("type", ItemMessage.Type.VOICE.ordinal()),
                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        final ItemVoiceItemMessageBodyItem voiceBody = (ItemVoiceItemMessageBodyItem) message.getBody();

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holder.ll_container.getLayoutParams();

        int voiceLength = voiceBody.getVoiceLength();
        int MIN_LENGTH = 50, MAX_LENGTH = 160;
        float barLen;
        if (voiceLength <= 10) {
            barLen = MIN_LENGTH + voiceLength * 0.05f * (MAX_LENGTH - MIN_LENGTH);
        } else {
            barLen = MIN_LENGTH + 0.5f * (MAX_LENGTH - MIN_LENGTH) + (voiceLength - 10) * 0.01f * (MAX_LENGTH - MIN_LENGTH);
        }

        linearParams.width = UiUtils.dip2px(context, barLen);
        holder.ll_container.setLayoutParams(linearParams);

        holder.tv.setText(voiceBody.getVoiceLength() + "''");

        holder.ll_container.setOnClickListener(new VoicePlayClickListener(message,
                holder.iv, holder.iv_read_status, this, activity, username));

        if (message.direct == ItemMessage.Direct.RECEIVE) {
            if (voiceBody.isIfClick()) {
                holder.iv_read_status.setVisibility(View.GONE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (message.direct == ItemMessage.Direct.SEND) {
//            holder.pb.setTag(position);
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 视频消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVideoMessage(final ItemMessage message,
                                    final ViewHolder holder, final int position, View convertView) {

        ItemVideoItemMessageBodyItem videoBody = (ItemVideoItemMessageBodyItem) message.getBody();
        // final File image=new File(PathUtil.getInstance().getVideoPath(),
        // videoBody.getFileName());
        String localThumb = videoBody.getLocalThumb();

        holder.iv.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(new Intent(activity,
                                ContextMenu.class).putExtra("position", position)
                                .putExtra(JumpParamsContants.INTENT_CHAT_TYPE, message.getChatType().ordinal()).putExtra("type", ItemMessage.Type.VIDEO.ordinal()),
                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (localThumb != null) {

            showVideoThumbView(localThumb, holder.iv,
                    videoBody.getThumbnailUrl(), message);
        }
        if (videoBody.getLength() > 0) {
            String time = DateUtils.toTimeBySecond(videoBody.getLength());
            holder.timeLength.setText(time);
        }
        holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);

        if (message.direct == ItemMessage.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody
                        .getVideoFileLength());
                holder.size.setText(size);
            }
        } else {
            if (videoBody.getLocalUrl() != null
                    && new File(videoBody.getLocalUrl()).exists()) {
                String size = TextFormater.getDataSize(new File(videoBody
                        .getLocalUrl()).length());
                holder.size.setText(size);
            }
        }

        if (message.direct == ItemMessage.Direct.RECEIVE) {

            // System.err.println("it is receive msg");
            if (message.status == ItemMessage.Status.INPROGRESS) {
                // System.err.println("!!!! back receive");
                holder.iv.setImageResource(R.drawable.default_image);
                showDownloadImageProgress(message, holder);

            } else {
                // System.err.println("!!!! not back receive, show image directly");
                holder.iv.setImageResource(R.drawable.default_image);
                if (localThumb != null) {
                    showVideoThumbView(localThumb, holder.iv,
                            videoBody.getThumbnailUrl(), message);
                }

            }

            return;
        }
        if (message.direct == ItemMessage.Direct.SEND) {
//            holder.pb.setTag(position);
            // until here ,deal with send video msg
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    if (timers.containsKey(message.getMsgId()))
                        return;
                    // set a timer
                    final Timer timer = new Timer();
                    timers.put(message.getMsgId(), timer);
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    holder.pb.setVisibility(View.VISIBLE);
                                    holder.tv.setVisibility(View.VISIBLE);
                                    holder.tv.setText(message.progress + "%");
                                    if (message.status == ItemMessage.Status.SUCCESS) {
                                        holder.pb.setVisibility(View.GONE);
                                        holder.tv.setVisibility(View.GONE);
                                        // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
                                        timer.cancel();
                                    } else if (message.status == ItemMessage.Status.FAIL) {
                                        holder.pb.setVisibility(View.GONE);
                                        holder.tv.setVisibility(View.GONE);
                                        // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                                        // message.setProgress(0);
                                        holder.staus_iv.setVisibility(View.VISIBLE);
                                        Toast.makeText(
                                                activity,
                                                activity.getString(R.string.send_fail)
                                                        + activity
                                                        .getString(R.string.connect_failuer_toast),
                                                Toast.LENGTH_SHORT).show();
                                        timer.cancel();
                                    }

                                }
                            });

                        }
                    }, 0, 500);
                    break;
                default:
                    // sendMsgInBackground(message, holder);
                    sendPictureMessage(message, holder);

            }
        }
    }

    /**
     * 文件消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleFileMessage(final ItemMessage message,
                                   final ViewHolder holder, int position, View convertView) {
        final ItemNormalItemFileItemMessageBody fileMessageBody = (ItemNormalItemFileItemMessageBody) message
                .getBody();
        final String filePath = fileMessageBody.getLocalUrl();
        holder.tv_file_name.setText(fileMessageBody.getFileName());
        holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody
                .getFileSize()));
        holder.ll_container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    // 文件存在，直接打开
                    FileUtils.openFile(file, (Activity) context);
                } else {
                    // 下载
                    // TODO 下载文件
                    context.startActivity(new Intent(context,
                            ShowNormalFileActivity.class).putExtra("msgbody",
                            fileMessageBody));
                }
                if (message.direct == ItemMessage.Direct.RECEIVE
                        && !message.isAcked) {
                    try {
                        // hhr
//						getInstance().ackMessageRead(
//								message.getFrom(), message.getMsgId());
                        message.isAcked = true;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        String st1 = context.getResources().getString(R.string.Have_downloaded);
        String st2 = context.getResources()
                .getString(R.string.Did_not_download);
        if (message.direct == ItemMessage.Direct.RECEIVE) { // 接收的消息
            System.err.println("it is receive msg");
            File file = new File(filePath);
            if (file != null && file.exists()) {
                holder.tv_file_download_state.setText(st1);
            } else {
                holder.tv_file_download_state.setText(st2);
            }
            return;
        }
        if (message.direct == ItemMessage.Direct.SEND) {
            // until here, deal with send voice msg
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.INVISIBLE);
                    holder.tv.setVisibility(View.INVISIBLE);
                    holder.staus_iv.setVisibility(View.INVISIBLE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.INVISIBLE);
                    holder.tv.setVisibility(View.INVISIBLE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    if (timers.containsKey(message.getMsgId()))
                        return;
                    // set a timer
                    final Timer timer = new Timer();
                    timers.put(message.getMsgId(), timer);
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    holder.pb.setVisibility(View.VISIBLE);
                                    holder.tv.setVisibility(View.VISIBLE);
                                    holder.tv.setText(message.progress + "%");
                                    if (message.status == ItemMessage.Status.SUCCESS) {
                                        holder.pb.setVisibility(View.INVISIBLE);
                                        holder.tv.setVisibility(View.INVISIBLE);
                                        timer.cancel();
                                    } else if (message.status == ItemMessage.Status.FAIL) {
                                        holder.pb.setVisibility(View.INVISIBLE);
                                        holder.tv.setVisibility(View.INVISIBLE);
                                        holder.staus_iv.setVisibility(View.VISIBLE);
                                        Toast.makeText(
                                                activity,
                                                activity.getString(R.string.send_fail)
                                                        + activity
                                                        .getString(R.string.connect_failuer_toast),
                                                Toast.LENGTH_SHORT).show();
                                        timer.cancel();
                                    }

                                }
                            });

                        }
                    }, 0, 500);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
            }

        }
    }

    /**
     * 处理位置消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleLocationMessage(final ItemMessage message,
                                       final ViewHolder holder, final int position, View convertView) {
        TextView locationView = ((TextView) convertView
                .findViewById(R.id.tv_location));
        ItemLocationItemMessageBody locBody = (ItemLocationItemMessageBody) message.getBody();
        locationView.setText(locBody.getAddress());
        LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
        locationView.setOnClickListener(new MapClickListener(loc, locBody
                .getAddress()));
        locationView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult((new Intent(activity,
                                ContextMenu.class)).putExtra("position", position)
                                .putExtra(JumpParamsContants.INTENT_CHAT_TYPE, message.getChatType().ordinal()).putExtra("type", ItemMessage.Type.LOCATION.ordinal()),
                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == ItemMessage.Direct.SEND) {
//            holder.pb.setTag(position);
            // deal with send message
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    sendMsgInBackground(message, holder);
                    break;
            }
        }
    }

    /**
     * 处理资产消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleAssetMessage(final ItemMessage message,
                                    final ViewHolder holder, final int position, View convertView) {
        TextView symbolTv = ((TextView) convertView
                .findViewById(R.id.tv_asset_symbol));
        ItemAssetItemMessageBody assetBody = (ItemAssetItemMessageBody) message.getBody();
        symbolTv.setText(assetBody.getSymbol());
        TextView valueTv = ((TextView) convertView
                .findViewById(R.id.tv_asset_value));
        String value = assetBody.getValue();
        try {
            value = new BigDecimal(value).stripTrailingZeros().toPlainString();
        } catch (Exception e) {

        }
        valueTv.setText(value);
//        TextView mReceiverTv = (TextView) convertView.findViewById(R.id.tv_receiver);
//        UserContactItem userTo = BtsHelper.getDatabase().getUserContactItemById(message.getTo());
//
//        if (userTo != null)
//            mReceiverTv.setText(context.getString(R.string.send_to) + userTo.getUserName());

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 交易详情
                 */
//                Toast.makeText(context, R.string.feature_come_soon, Toast.LENGTH_SHORT).show();
            }
        });
//        convertView.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                activity.startActivityForResult((new Intent(activity,
//                                ContextMenu.class)).putExtra("position", position)
//                                .putExtra("type", ItemMessage.Type.LOCATION.ordinal()),
//                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
//                return false;
//            }
//        });

        if (message.direct == ItemMessage.Direct.SEND) {
            // deal with send message
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    sendMsgInBackground(message, holder);
            }
        }
    }

    /**
     * 处理红包消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleRedPacketMessage(final ItemMessage message,
                                        final ViewHolder holder, final int position, View convertView) {

        holder.ll_container.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult((new Intent(activity,
                                ContextMenu.class)).putExtra("position", position)
                                .putExtra(JumpParamsContants.INTENT_CHAT_TYPE, message.getChatType().ordinal()).putExtra("type", ItemMessage.Type.RED_PACKET.ordinal()),
                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        final ItemRedPacketMessageBody redPacketBody = (ItemRedPacketMessageBody) message.getBody();

        if (redPacketBody == null) {
            return;
        }
        if (redPacketBody.isIfClick()) {
            holder.ll_container.setBackgroundResource(R.drawable.msg_red_packet_clicked);
        } else {
            holder.ll_container.setBackgroundResource(R.drawable.msg_red_packet_no_click);
        }

        if (!StringUtils.equalsNull(redPacketBody.getRed_packet_msg())) {
            holder.tv_red_packet_msg.setText(redPacketBody.getRed_packet_msg());
        }
        holder.ll_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 点击红包
                 */
                String redPacketId = redPacketBody.getRed_packet_id();
                if (!StringUtils.equalsNull(redPacketId)) {
                    if (redPacketBody.isIfClick()) {

                        JumpAppPageUtil.jumpRedPacketInfoPage(context, redPacketId);

                    } else {
                        DialogUtil.redPacketDialog(context, message.getFrom(), redPacketId, redPacketBody.getRed_packet_msg(), new DialogUtil.ConfirmCallBackInf() {
                            @Override
                            public void onConfirmClick(String content) {
                                redPacketBody.setIfClick(true);
                                OneAccountHelper.getDatabase().updateUserChatJsonParam(message.getMsgId(), GsonUtils.objToJson(redPacketBody));
                                refresh();
                            }
                        });
                    }
                }

            }
        });
//        convertView.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//        activity.startActivityForResult((new Intent(activity,
//                                ContextMenu.class)).putExtra("position", position)
//                                .putExtra("type", ItemMessage.Type.LOCATION.ordinal()),
//                        CommonUtils.REQUEST_CODE_CONTEXT_MENU);
//                return false;
//            }
//        });

        if (message.direct == ItemMessage.Direct.SEND) {
            // deal with send message
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                case CREATE:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    sendMsgInBackground(message, holder);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param message
     * @param holder
     */

    public void sendMsgInBackground(final ItemMessage message,
                                    final ViewHolder holder) {

        // FIXME: 2017/8/1

//		holder.staus_iv.setVisibility(View.GONE);
//		holder.pb.setVisibility(View.VISIBLE);
//
//		final long start = System.currentTimeMillis();
//		// 调用sdk发送异步发送方法
//		getInstance().sendMessage(message, new ICallBack() {
//
//			@Override
//			public void onSuccess() {
//
//				updateSendedView(message, holder);
//			}
//
//			@Override
//			public void onError(int code, String error) {
//
//				updateSendedView(message, holder);
//			}
//
//			@Override
//			public void onProgress(int progress, String status) {
//			}
//
//		});

    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     */
    private void showDownloadImageProgress(final ItemMessage message,
                                           final ViewHolder holder) {
        System.err.println("!!! show download image progress");
        // final ItemImageItemMessageBodyItem msgbody = (ItemImageItemMessageBodyItem)
        // message.getBody();
        final ItemFileItemMessageBody msgbody = (ItemFileItemMessageBody) message.getBody();
        if (holder.pb != null)
            holder.pb.setVisibility(View.VISIBLE);
        if (holder.tv != null)
            holder.tv.setVisibility(View.VISIBLE);

//		msgbody.setDownloadCallback(new ICallBack() {
//
//			@Override
//			public void onSuccess() {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						// message.setBackReceive(false);
//						if (message.getType() == ItemMessage.Type.IMAGE) {
//							holder.pb.setVisibility(View.GONE);
//							holder.tv.setVisibility(View.GONE);
//						}
//						notifyDataSetChanged();
//					}
//				});
//			}
//
//			@Override
//			public void onError(int code, String message) {
//
//			}
//
//			@Override
//			public void onProgress(final int progress, String status) {
//				if (message.getType() == ItemMessage.Type.IMAGE) {
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							holder.tv.setText(progress + "%");
//
//						}
//					});
//				}
//
//			}
//
//		});
    }

    /*
     * send message with new sdk
     */
    private void sendPictureMessage(final ItemMessage message,
                                    final ViewHolder holder) {
        // FIXME: 2017/8/1
        try {
//			String to = message.getTo();
//
//			// before send, update ui
//			holder.staus_iv.setVisibility(View.GONE);
//			holder.pb.setVisibility(View.VISIBLE);
//			holder.tv.setVisibility(View.VISIBLE);
//			holder.tv.setText("0%");
//
//			final long start = System.currentTimeMillis();
//			// if (chatType == CommonUtils.CHATTYPE_SINGLE) {
//			getInstance().sendMessage(message, new ICallBack() {
//
//				@Override
//				public void onSuccess() {
//					Log.d(TAG, "send image message successfully");
//					activity.runOnUiThread(new Runnable() {
//						public void run() {
//							// send success
//							holder.pb.setVisibility(View.GONE);
//							holder.tv.setVisibility(View.GONE);
//						}
//					});
//				}
//
//				@Override
//				public void onError(int code, String error) {
//
//					activity.runOnUiThread(new Runnable() {
//						public void run() {
//							holder.pb.setVisibility(View.GONE);
//							holder.tv.setVisibility(View.GONE);
//							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
//							holder.staus_iv.setVisibility(View.VISIBLE);
//							Toast.makeText(
//									activity,
//									activity.getString(R.string.send_fail)
//											+ activity
//													.getString(R.string.connect_failuer_toast),
//									0).show();
//						}
//					});
//				}
//
//				@Override
//				public void onProgress(final int progress, String status) {
//					activity.runOnUiThread(new Runnable() {
//						public void run() {
//							holder.tv.setText(progress + "%");
//						}
//					});
//				}
//
//			});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final ItemMessage message,
                                  final ViewHolder holder) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.getType() == ItemMessage.Type.VIDEO) {
                    holder.tv.setVisibility(View.GONE);
                }
                if (message.status == ItemMessage.Status.SUCCESS) {
                    // if (message.getType() == ItemMessage.Type.FILE) {
                    // holder.pb.setVisibility(View.INVISIBLE);
                    // holder.staus_iv.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.pb.setVisibility(View.GONE);
                    // holder.staus_iv.setVisibility(View.GONE);
                    // }

                } else if (message.status == ItemMessage.Status.FAIL) {
                    // if (message.getType() == ItemMessage.Type.FILE) {
                    // holder.pb.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.pb.setVisibility(View.GONE);
                    // }
                    // holder.staus_iv.setVisibility(View.VISIBLE);
                    Toast.makeText(
                            activity,
                            activity.getString(R.string.send_fail)
                                    + activity
                                    .getString(R.string.connect_failuer_toast),
                            Toast.LENGTH_SHORT).show();
                }

                notifyDataSetChanged();
            }
        });
    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath,
                                  final ImageView iv, final String localFullSizePath,
                                  String remoteDir, final ItemMessage message, TextView statusTv) {
        final String remote = remoteDir;
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);

        if (bitmap != null) {
            ImageUtils.mLoadingImgCatch.remove(message.getMsgId());

            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            statusTv.setVisibility(View.GONE);

            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 查看大图
                    System.err.println("image view on click");
                    Intent intent = new Intent(activity, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
                        System.err.println("here need to check why download everytime");
                    } else {
                        // The local full size pic does not exist yet.
                        // ShowBigImage needs to download it from the server
                        // first
                        // intent.putExtra("", message.get);
                        ItemImageItemMessageBodyItem body = (ItemImageItemMessageBodyItem) message
                                .getBody();
                        intent.putExtra("secret", body.getEncryptKey());
                        intent.putExtra("remotepath", remote);
                    }
                    if (message != null && message.direct == ItemMessage.Direct.RECEIVE
                            && !message.isAcked
                            && message.getChatType() != ItemMessage.ChatType.GroupChat) {
                        try {
                            // hhr
//							getInstance().ackMessageRead(
//									message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    activity.startActivity(intent);
                }
            });
            return true;
        } else {
            if (ImageUtils.mLoadingImgCatch.containsKey(message.getMsgId()) && ImageUtils.mLoadingImgCatch.get(message.getMsgId()) == ImageUtils.IMG_STATUS_LOADING_BITMAP) {
                return true;
            }
            ImageUtils.mLoadingImgCatch.put(message.getMsgId(), ImageUtils.IMG_STATUS_LOADING_BITMAP);
            new LoadImageTask().execute(thumbernailPath, localFullSizePath,
                    remote, message.getChatType(), iv, activity, message, statusTv);
            return true;
        }

    }

    /**
     * 展示视频缩略图
     *
     * @param localThumb   本地缩略图路径
     * @param iv
     * @param thumbnailUrl 远程缩略图路径
     * @param message
     */
    private void showVideoThumbView(String localThumb, ImageView iv,
                                    String thumbnailUrl, final ItemMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ItemVideoItemMessageBodyItem videoBody = (ItemVideoItemMessageBodyItem) message
                            .getBody();
                    // TODO 打开视频页面
                    System.err.println("video view is on click");
                    // Intent intent = new Intent(activity,
                    // ShowVideoActivity.class);
                    // intent.putExtra("localpath", videoBody.getLocalUrl());
                    // intent.putExtra("secret", videoBody.getEncryptKey());
                    // intent.putExtra("remotepath", videoBody.getRemoteUrl());
                    // if (message != null
                    // && message.direct == ItemMessage.Direct.RECEIVE
                    // && !message.isAcked
                    // && message.getChatType() != ChatType.GroupChat) {
                    // message.isAcked = true;
                    // try {
                    // getInstance().ackMessageRead(
                    // message.getFrom(), message.getMsgId());
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                    // }
                    // activity.startActivity(intent);

                }
            });

        } else {
            new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv,
                    activity, message, this);
        }

    }

    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView head_iv_bg;
        ImageView head_iv;
        TextView tv_userId;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        View ll_container;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;

        TextView tv_asset_symbol;
        TextView tv_asset_value;

        TextView tv_red_packet_msg;

        TextView tv_img_status;
    }

    /*
     * 点击地图消息listener
     */
    class MapClickListener implements OnClickListener {

        LatLng location;
        String address;

        public MapClickListener(LatLng loc, String address) {
            location = loc;
            this.address = address;

        }

        @Override
        public void onClick(View v) {// TODO 打开百度
            Intent intent;
            intent = new Intent(context, TencentMapActivity.class);
            intent.putExtra("latitude", location.latitude);
            intent.putExtra("longitude", location.longitude);
            intent.putExtra("address", address);
            activity.startActivity(intent);
        }

    }

}