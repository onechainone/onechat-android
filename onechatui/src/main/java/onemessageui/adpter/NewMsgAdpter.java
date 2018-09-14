package onemessageui.adpter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.bean.ItemConversationListBean;
import oneapp.onechat.oneandroid.onemessage.bean.PublicMsgInfo;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemTextItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.common.UserUtils;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.chat.utils.Constant;
import onemessageui.common.ViewHolder;
import onemessageui.dialog.WarnTipDialog;
import onemessageui.widght.swipe.SwipeLayout;
import onewalletui.util.ImageUtils;
import sdk.android.onechatui.R;

public class NewMsgAdpter extends BaseAdapter {
    protected Context context;
    private List<ItemConversationListBean> conversationList;
    private WarnTipDialog Tipdialog;
    private int deleteID;
    //    private String ChatID;
    private String userid;
    private Hashtable<String, String> ChatRecord = new Hashtable<String, String>();
    public PublicMsgInfo PublicMsg = null;

    public NewMsgAdpter(Context ctx, List<ItemConversationListBean> objects) {
        context = ctx;
        conversationList = objects;
        userid = UserUtils.getUserID(context);
    }

    public void setPublicMsg(PublicMsgInfo Msg) {
        PublicMsg = Msg;
    }

    public PublicMsgInfo getPublicMsg() {
        return PublicMsg;
    }

    public Hashtable<String, String> getChatRecord() {
        return ChatRecord;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_item_msg, parent, false);
        }
        ImageView img_avar = ViewHolder.get(convertView,
                R.id.contactitem_avatar_iv);
        ImageView ivIsGroup = ViewHolder.get(convertView, R.id.iv_is_group);
        TextView txt_name = ViewHolder.get(convertView, R.id.txt_name);
        TextView txt_state = ViewHolder.get(convertView, R.id.txt_state);
        TextView txt_del = ViewHolder.get(convertView, R.id.txt_del);
        EmojiconTextView txt_content = ViewHolder.get(convertView, R.id.txt_content);
        TextView txt_time = ViewHolder.get(convertView, R.id.txt_time);
        TextView unreadLabel = ViewHolder.get(convertView, R.id.unread_msg_number);
        SwipeLayout swipe = ViewHolder.get(convertView, R.id.swipe);
        TextView txt_tip = ViewHolder.get(convertView, R.id.txt_tip);
        TextView txt_special_msg = ViewHolder.get(convertView, R.id.txt_special_msg);
        View lineView = ViewHolder.get(convertView, R.id.view_bottom_line);
        TextView groupAutheTv = ViewHolder.get(convertView, R.id.txt_group_authen);
        if (position == conversationList.size() - 1) {
            lineView.setVisibility(View.GONE);
        } else {
            lineView.setVisibility(View.VISIBLE);
        }

        if (PublicMsg != null && position == 0) {
//			txt_name.setText(R.string.official_accounts);
//			img_avar.setImageResource(R.drawable.icon_public);
//			txt_time.setText(PublicMsg.getTime());
//			txt_content.setText(PublicMsg.getContent());
//			unreadLabel.setText("3");
//			unreadLabel.setVisibility(View.VISIBLE);
//			swipe.setSwipeEnabled(false);
        } else {
            swipe.setSwipeEnabled(true);
            // 获取与此用户/群组的会话
            final ItemConversationListBean conversation = conversationList.get(position);
            // 获取用户username或者群组groupid
            String ChatID = conversation.getUsername();
            txt_del.setTag(ChatID);
            if (conversation.isGroup()) {
                ivIsGroup.setVisibility(View.VISIBLE);
                UserGroupInfoItem info = OneAccountHelper.getDatabase().getUserGroupInfoItemById(ChatID, false);
                if (info != null) {
                    if (StringUtils.getBooleanValue(info.authen)) {
                        groupAutheTv.setVisibility(View.VISIBLE);
                    } else {
                        groupAutheTv.setVisibility(View.GONE);
                    }
                    txt_name.setText(info.group_name);
                    ImageUtils.displayCircleNetImage(context, info.getGroupAvatarUrl(), img_avar, R.drawable.default_group);

                } else {
                    // // FIXME: 2017/8/1 暂时不用获取
                    OneGroupHelper.GetItemGroupInfoRequest(ChatID, new RequestSuccessListener<UserGroupInfoItem>() {
                        @Override
                        public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                            if (userGroupInfoItem != null) {
                                notifyDataSetChanged();
                            }
                        }
                    });
                    txt_name.setText(context.getResources().getString(R.string.default_group_name));
                    groupAutheTv.setVisibility(View.GONE);
                }
            } else {
                groupAutheTv.setVisibility(View.GONE);
                ivIsGroup.setVisibility(View.GONE);
                UserContactItem tempUser = OneAccountHelper.getDatabase().getUserContactItemById(ChatID);
                if (tempUser != null) {
                    txt_name.setText(tempUser.getUserName());
                    ImageUtils.displayAvatarNetImage(context, tempUser.avatar, img_avar, tempUser.getSex());
                    // FIXME: 2018/1/15 hs 不是好友自动设为好友
//                    if (tempUser.getStatusFriend() != UserContactItem.StatusFriend.FRIEND.ordinal()) {
//                        tempUser.setStatusFriend(UserContactItem.StatusFriend.FRIEND.ordinal());
//                        OneAccountHelper.getDatabase().userContactInsertOrUpdate(tempUser);
//                    }
                } else {
                    txt_name.setText(context.getResources().getString(R.string.default_friend_name));
                    OneChatHelper.RequestUserInfoById(ChatID, new RequestSuccessListener<UserInfoBean>() {
                        @Override
                        public void onResponse(UserInfoBean userInfoBean) {
                            if (userInfoBean != null) {
                                notifyDataSetChanged();
                            }
                        }
                    });
//					UserUtils.initUserInfo(context, ChatID, img_avar, txt_name);// 获取用户信息
                }
            }

            int unReadNum = conversation.getUnreadMsgCount();

            if (unReadNum > 0) {
                // 显示与此用户的消息未读数
                String unReadNumString = String.valueOf(unReadNum);
                if (unReadNum > CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM) {
                    unReadNumString = CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM + context.getString(R.string.add_symbol);
                }
                unreadLabel.setText(unReadNumString);
                unreadLabel.setVisibility(View.VISIBLE);
            } else {
                unreadLabel.setVisibility(View.INVISIBLE);
            }

            if (!StringUtils.equalsNull(conversation.getExtraTip())) {
                switch (conversation.getExtraTip()) {
                    case CommonConstants.MSG_EXTRA_TYPE_AT_ME:
                        txt_tip.setVisibility(View.VISIBLE);
                        txt_tip.setText(context.getString(R.string.someone_at_me));
                        break;
                }
            } else {
                txt_tip.setVisibility(View.GONE);
                txt_tip.setText("");
            }

            if (conversation.getItemMessage() != null) {
                // 把最后一条消息的内容作为item的message内容
                ItemMessage lastMessage = conversation.getItemMessage();
                if (lastMessage == null) {
                    return convertView;
                }
                String sendUserName = "";
                if (conversation.isGroup()) {
                    UserContactItem lastMsgUser = OneAccountHelper.getDatabase().getUserContactItemById(lastMessage.getFrom());
                    if (lastMsgUser != null)
                        sendUserName = lastMsgUser.getUserName() + ": ";
                }

                String contentString = getMessageDigest(lastMessage, context);
                switch (lastMessage.getType()) {
                    case ASSET:
                    case RED_PACKET:
                        txt_special_msg.setText(contentString);
                        txt_content.setText(sendUserName);
                        break;
                    default:
                        txt_special_msg.setText("");
                        contentString = sendUserName + contentString;
                        txt_content.setText(contentString);
                        break;
                }

                txt_time.setText(TimeUtils.toSecondTimeString(lastMessage.getMsgTime()));

                if (lastMessage.status == ItemMessage.Status.SUCCESS) {
                    txt_state.setText("送达");
                    // txt_state.setBackgroundResource(R.drawable.btn_bg_orgen);
                } else if (lastMessage.status == ItemMessage.Status.FAIL) {
                    txt_state.setText("失败");
                    // txt_state.setBackgroundResource(R.drawable.btn_bg_red);
                } else if (lastMessage.direct == ItemMessage.Direct.RECEIVE) {
                    txt_state.setText("已读");
                    txt_state.setBackgroundResource(R.drawable.btn_bg_blue);
                }
            }

            txt_del.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteID = position;
                    Tipdialog = new WarnTipDialog(context,
                            getString(context, R.string.make_sure_delete_msg));
                    Tipdialog.setBtnOkLinstener(onclick);
                    Tipdialog.show();
                }
            });
        }
        return convertView;
    }

    private DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (conversationList == null || conversationList.size() <= deleteID) {
                return;
            }
            ItemConversationListBean conversation = conversationList.get(deleteID);

            // FIXME: 2017/8/1 delete huihua
            if (conversation.isGroup()) {
                OneAccountHelper.getDatabase().deleteMessageByGroupid(conversation.getUsername());
            } else {
                OneAccountHelper.getDatabase().deleteMessageByToid(conversation.getUsername());
            }

            ToastUtils.simpleToast(R.string.delete_success);
            conversationList.remove(deleteID);
            notifyDataSetChanged();
            Tipdialog.dismiss();
        }
    };

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(ItemMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == ItemMessage.Direct.RECEIVE) {
                    digest = getString(context, R.string.location_recv);
                    String name = message.getFrom();
//				if (GloableParams.UserInfos != null) {
//					UserContactItem user = BtsApplication.getDatabase().getUserContactItemById(message.getFrom());
//					if (user != null && null != user.getUserName())
//						name = user.getUserName();
//				}

                    UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(message.getFrom());
                    if (user != null && null != user.getUserName())
                        name = user.getUserName();

                    digest = String.format(digest, name);
                    return digest;
                } else {
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
//                ItemImageItemMessageBodyItem imageBody = (ItemImageItemMessageBodyItem) message.getBody();
//                digest = getString(context, R.string.picture)
//                        + imageBody.getFileName();
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getString(context, R.string.voice_msg);
                break;
            case VIDEO: // 视频消息
                digest = getString(context, R.string.video);
                break;
            case TXT: // 文本消息
                if (!message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();

                    // FIXME: 2017/8/1
                    if (txtBody != null) {
                        digest = txtBody.getMessage();
                        if (digest != null && digest.length() > CommonConstants.MAX_SEND_MSG_SIZE) {
                            digest = digest.substring(0, CommonConstants.MAX_SEND_MSG_SIZE);
                        }
                    }
                } else {
                    ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();
                    digest = getString(context, R.string.voice_call)
                            + txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = getString(context, R.string.file);
                break;
            case ASSET: // 资产信息
                digest = getString(context, R.string.msg_asset);
                break;
            case RED_PACKET://红包
                digest = getString(context, R.string.msg_red_packet);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }
        return digest;
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
