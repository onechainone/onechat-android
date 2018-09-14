package onemessageui.mpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mpush.api.Constants;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.mpush.bean.OrderChangeBean;
import onemessageui.mpush.bean.PushAddGroupBean;
import onemessageui.mpush.bean.PushAddUserBean;
import onemessageui.mpush.bean.PushJoinGroupBean;
import onemessageui.mpush.bean.PushMsgBean;
import onemessageui.mpush.bean.PushUrlBean;
import onemessageui.mpush.bean.PushWeiboMsgBean;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MPushService.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            byte[] bytes = intent.getByteArrayExtra(MPushService.EXTRA_PUSH_MESSAGE);
            int messageId = intent.getIntExtra(MPushService.EXTRA_PUSH_MESSAGE_ID, 0);
            String message = new String(bytes, Constants.UTF_8);

            PushMsgBean pushMsgBean = GsonUtils.jsonToObj(message, PushMsgBean.class);
            if (pushMsgBean == null || StringUtils.equalsNull(pushMsgBean.getType())) {
                return;
            }
            String notifyTitle = "", notifyContent = "";
            boolean ifInputDb = true;
            switch (pushMsgBean.getType()) {
                case PushUtils.PUSH_TYPE_JOIN_GROUP:
                    PushJoinGroupBean pushJoinGroupBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushJoinGroupBean.class);
                    if (pushJoinGroupBean != null) {
                        pushMsgBean.setClassify_key(PushUtils.PUSH_KEY_GROUP_ID);
                        pushMsgBean.setClassify_id(pushJoinGroupBean.getGroup_uid());
                        notifyTitle = pushJoinGroupBean.getNickname();
                        notifyContent = pushJoinGroupBean.getGroup_name();
                    }
                    break;

                case PushUtils.PUSH_TYPE_WEIBO_REWARD:
                case PushUtils.PUSH_TYPE_WEIBO_COMMENT:
                case PushUtils.PUSH_TYPE_WEIBO_PAY:
                    PushWeiboMsgBean weiboMsgBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushWeiboMsgBean.class);
                    if (weiboMsgBean != null) {
                        pushMsgBean.setClassify_key(PushUtils.PUSH_KEY_GROUP_ID);
                        pushMsgBean.setClassify_id(weiboMsgBean.getGroup_uid());
                        switch (pushMsgBean.getType()) {
                            case PushUtils.PUSH_TYPE_WEIBO_REWARD:
                                notifyTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_reward_weibo);
                                notifyContent = weiboMsgBean.getWeibo_content();
                                break;
                            case PushUtils.PUSH_TYPE_WEIBO_COMMENT:
                                notifyTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_comment_weibo);
                                notifyContent = weiboMsgBean.getComment_content();
                                break;
                            case PushUtils.PUSH_TYPE_WEIBO_PAY:
                                notifyTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_pay_weibo);
                                notifyContent = weiboMsgBean.getWeibo_content();
                                break;
                        }
                    }
                    break;
                case PushUtils.PUSH_TYPE_URL:
                    PushUrlBean pushUrlBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushUrlBean.class);
                    if (pushUrlBean != null) {
                        pushMsgBean.setClassify_key(PushUtils.PUSH_KEY_URL);
                        pushMsgBean.setClassify_id(pushMsgBean.getMsgId());
                        notifyTitle = pushUrlBean.getTitle();
                        notifyContent = pushUrlBean.getMessage();
                    }
                    break;
                case PushUtils.PUSH_TYPE_ADD_USER:
                    PushAddUserBean pushAddUserBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushAddUserBean.class);
                    if (pushAddUserBean != null) {
                        pushMsgBean.setClassify_key(PushUtils.PUSH_KEY_ACCOUNT_NAME);
                        pushMsgBean.setClassify_id(pushAddUserBean.getAccount_name());
                        notifyTitle = pushAddUserBean.getNickname();
                        notifyContent = pushAddUserBean.getRemark();
                    }
                    break;
                case PushUtils.PUSH_TYPE_ADD_GROUP:
                    PushAddGroupBean pushAddGroupBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushAddGroupBean.class);
                    if (pushAddGroupBean != null) {
                        pushMsgBean.setClassify_key(PushUtils.PUSH_KEY_GROUP_ID);
                        pushMsgBean.setClassify_id(pushAddGroupBean.getGroup_uid());
                        notifyTitle = pushAddGroupBean.getGroup_name();
                        notifyContent = pushAddGroupBean.getRemark();
                    }
                    break;
                case PushUtils.PUSH_TYPE_ORDER_PUSH:
                    OrderChangeBean orderChangeBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), OrderChangeBean.class);
                    if (orderChangeBean != null) {
                        pushMsgBean.setClassify_key(PushUtils.PUSH_KEY_ORDER);
                        pushMsgBean.setClassify_id(orderChangeBean.getId());
                        notifyTitle = context.getString(R.string.order_has_change);
                        notifyContent = context.getString(R.string.order_id) + orderChangeBean.getId();
                    }
                    break;
                default:
                    ifInputDb = false;
                    break;
            }
            if (!ifInputDb) {
                return;
            }

            boolean ifSendBroadcast = true;
            boolean ifShow = StringUtils.equals(pushMsgBean.getShow(), ServiceConstants.SERVICE_SAFE_STATUS_YES);
            if (ifShow) {
//                ifSendBroadcast = BtsHelper.getDatabase().putUserPush(pushMsgBean) > 0;
            } else {
                ifSendBroadcast = true;
            }

            if (ifSendBroadcast) {
// send notify
                Intent pushIntent = new Intent();
                pushIntent.setAction(CommonHelperUtils.getPushMessageBroadcastAction());
                pushIntent.putExtra(JumpParamsContants.INTENT_TYPE, pushMsgBean.getType());
                pushIntent.putExtra(JumpParamsContants.INTENT_PUSH_ID, pushMsgBean.getMsgId());
                //发送 一个无序广播
                context.sendBroadcast(pushIntent);
            }

            if (messageId > 0) MPush.I.ack(messageId);


            if (ifSendBroadcast) {
                if (ifShow) {
                    if (OneAccountHelper.getAppStatus() != CommonConstants.APP_STATUS_RUNING) {
                        if (StringUtils.equalsNull(notifyTitle) && StringUtils.equalsNull(notifyContent)) {
                            return;
                        }
                        if (TextUtils.isEmpty(message)) return;
                        NotificationDO ndo = new NotificationDO();
                        Intent it = new Intent(context, MyReceiver.class);
                        it.setAction(MPushService.ACTION_NOTIFICATION_OPENED);
                        it.putExtra(Notifications.EXTRA_MESSAGE_ID, pushMsgBean.getMsgId());
                        if (TextUtils.isEmpty(ndo.getMsgId())) ndo.setMsgId(pushMsgBean.getMsgId());
                        if (TextUtils.isEmpty(ndo.getTitle())) ndo.setTitle(notifyTitle);
                        if (TextUtils.isEmpty(ndo.getTicker())) ndo.setTicker(notifyTitle);
                        if (TextUtils.isEmpty(ndo.getContent())) ndo.setContent(notifyContent);
                        Notifications.I.notify(ndo, it);
                    } else {
                        Intent dialogIntent = new Intent();
                        dialogIntent.setAction(CommonConstants.ALL_ACTIVITY_BROADCAST);
                        dialogIntent.putExtra(oneapp.onechat.oneandroid.onewallet.Constants.ARG_TYPE, CommonConstants.BROADCAST_TYPE_DIALOG_PUSH);
                        dialogIntent.putExtra(JumpParamsContants.INTENT_PUSH_ID, pushMsgBean.getMsgId());
                        //发送 一个无序广播
                        context.sendBroadcast(dialogIntent);
                    }
                } else {

                }
            }

        } else if (MPushService.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            String msgId = intent.getStringExtra(Notifications.EXTRA_MESSAGE_ID);
//            PushMsgBean pushMsgBean = BtsHelper.getDatabase().getPushMsgById(msgId);
            Notifications.I.clean(intent);

//            JumpAppPageUtil.jumpMainPage(context);
//            Toast.makeText(context, "通知被点击了， msgId=" + msgId, Toast.LENGTH_SHORT).show();
        } else if (MPushService.ACTION_KICK_USER.equals(intent.getAction())) {
//            Toast.makeText(context, "用户被踢下线了", Toast.LENGTH_SHORT).show();
        } else if (MPushService.ACTION_BIND_USER.equals(intent.getAction())) {
//            Toast.makeText(context, "绑定用户:"
//                            + intent.getStringExtra(MPushService.EXTRA_USER_ID)
//                            + (intent.getBooleanExtra(MPushService.EXTRA_BIND_RET, false) ? "成功" : "失败")
//                    , Toast.LENGTH_SHORT).show();
        } else if (MPushService.ACTION_UNBIND_USER.equals(intent.getAction())) {
//            Toast.makeText(context, "解绑用户:"
//                            + (intent.getBooleanExtra(MPushService.EXTRA_BIND_RET, false)
//                            ? "成功"
//                            : "失败")
//                    , Toast.LENGTH_SHORT).show();
        } else if (MPushService.ACTION_CONNECTIVITY_CHANGE.equals(intent.getAction())) {
//            Toast.makeText(context, intent.getBooleanExtra(MPushService.EXTRA_CONNECT_STATE, false)
//                            ? "MPUSH连接建立成功"
//                            : "MPUSH连接断开"
//                    , Toast.LENGTH_SHORT).show();
        } else if (MPushService.ACTION_HANDSHAKE_OK.equals(intent.getAction())) {
//            Toast.makeText(context, "MPUSH握手成功, 心跳:" + intent.getIntExtra(MPushService.EXTRA_HEARTBEAT, 0)
//                    , Toast.LENGTH_SHORT).show();
        }
    }

}
