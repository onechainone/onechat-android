package onemessageui.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.graphenechain.models.MemoMessage;
import oneapp.onechat.oneandroid.graphenechain.models.UserChatItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemAssetItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemTextItemMessageBody;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.NetUtils;
import onemessageui.chat.utils.Constant;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.ListResult;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.WalletUtils;
import oneapp.onecore.graphenej.Util;
import onemessageui.dialog.DialogUtil;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

public class CommonUtils {

    public static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    public static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
//    public static final int REQUEST_CODE_HEADIMG_MENU = 26;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;
    public static final int RESULT_CODE_REWARD = 8;

    public static final int BOTTOM_SCROLL_NUM = 3;
    public static final int SHOW_TOP_SCROLL_NEW_MSG_NUM = 8;

    public static final String COPY_IMAGE = "ONECHATIMG";
    public static final Pattern WEB_PATTERN =
            Pattern.compile("(((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(.(html|htm|asp|php|jsp|shtml|nsp))");

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(ItemMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == ItemMessage.Direct.RECEIVE) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getString(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = getString(context, R.string.video);
                break;
            case TXT: // 文本消息
                if (!message.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                } else {
                    ItemTextItemMessageBody txtBody = (ItemTextItemMessageBody) message.getBody();
                    digest = getString(context, R.string.voice_call)
                            + txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = getString(context, R.string.file);
                break;
            case ASSET: // 普通文件消息
                digest = getString(context, R.string.msg_asset);
                break;
            case RED_PACKET: // 普通文件消息
                digest = getString(context, R.string.msg_red_packet);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static void sendAssetMessageByUid(Context context, String toChatUserId, String symbol, String value, String daoUuid) {
        if (!NetUtils.hasNetwork(context)) {
            Toast.makeText(
                    context,
                    context.getString(R.string.string_network_disconnect),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            if (toChatUserId.equals(OneAccountHelper.getAccountId())) {
                Toast.makeText(context, R.string.message_send_self, Toast.LENGTH_SHORT).show();
                return;
            }

            // 消息id
            String uuid = UUID.randomUUID().toString();

            ItemMessage message = ItemMessage.createSendMessage(ItemMessage.Type.ASSET);

            try {
                value = new BigDecimal(value).stripTrailingZeros().toPlainString();
            } catch (Exception e) {

            }
            // FIXME: 2017/7/31 chat group need to groud send
            ItemAssetItemMessageBody assetBody = new ItemAssetItemMessageBody(symbol, value);
            // 设置消息body
            message.addBody(assetBody);

            // 设置要发给谁,用户username或者小密圈groupid
            message.setReceipt(toChatUserId);
            message.setFrom(OneAccountHelper.getAccountId());
            message.setTo(toChatUserId);

            message.direct = ItemMessage.Direct.SEND;

            message.setChatType(ItemMessage.ChatType.Chat);

            message.setMsgId(uuid);
            String jsonParam = GsonUtils.objToJson(assetBody);

            MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
                    MemoMessage.MSG_TYPE_ASSET, MemoMessage.CMD_SEND_MSG, "", null,
                    null, null, jsonParam, uuid);

//                MainActivity.mSendWebsocket.sendFunds(false, "",
//						CommonConstants.CHAT_ASSET_AMOUNT,
//						CommonConstants.CHAT_ASSET_SYMBOL,
//                        message.getFrom(), message.getTo(),
//						tempMemo, true, true);

            String strJson = tempMemo.toString();

            // 保存聊天消息
            UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
                    tempMemo.getTime(), message.getFrom(), message.getTo(),
                    "", "", strJson,
                    CommonConstants.DEFAULT_DAO_CODE, daoUuid, uuid, 0,
                    ItemMessage.Status.CREATE.ordinal(), 0, 0, "");

            OneAccountHelper.getDatabase().putUserChat(chatItem);

            OneChatHelper.startMessageSender();

        } catch (Exception e) {

        }
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    // 该函数必须在ui 线程调用
    private static void sendSingleFirstMessage(Context context, String userId) {
        if (userId == null || userId.equals(OneAccountHelper.getAccountId())) {
            return;
        }
        String content = context.getResources().getString(R.string.default_chat_sentence);

        String uuid = UUID.randomUUID().toString();
        String groupUid = "";

        ItemMessage message = CommonHelperUtils.buildTextMessage(OneAccountHelper.getAccountId(), userId,
                content, ItemMessage.Direct.SEND, ItemMessage.ChatType.GroupChat, uuid);

        MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
                MemoMessage.MSG_TYPE_TXT, MemoMessage.CMD_SEND_MSG, content, null,
                groupUid, null, null, uuid);

        String strJson = tempMemo.toString();

        // 保存聊天消息
        UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
                tempMemo.getTime(), message.getFrom(), message.getTo(),
                "", "", strJson,
                CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
                ItemMessage.Status.CREATE.ordinal(), 0, 0, groupUid);

        OneAccountHelper.getDatabase().putUserChat(chatItem);

        OneChatHelper.startMessageSender();

        JumpAppPageUtil.jumpSingleChatPage(context, userId);
        // notify to update view
        // send
        Intent intent = new Intent();
        intent.setAction(CommonHelperUtils.getNewMessageBroadcastAction());

        // FIXME: 2017/7/31
        //要发送的内容
        intent.putExtra("from", "");
        intent.putExtra(JumpParamsContants.INTENT_MSG_IDS, uuid);

        //发送 一个无序广播
        context.sendBroadcast(intent);
    }

    //发送群消息
    public static void sendMessageToGroup(final Context context, final String groupId, final String message) {
        final UserGroupInfoItem groupInfoItem = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId, false);

        if (groupInfoItem == null) {
            OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
                @Override
                public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                    if (userGroupInfoItem != null) {
                        OneGroupHelper.getMemberListFromGroup(groupId, null);
                        sendMessageToGroup(context, groupId, message);
                    } else {
//                        ToastUtils.simpleToast(R.string.send_fail);
                    }
                }
            });
            return;
        }

        String uuid = UUID.randomUUID().toString();

        MemoMessage tempMemo = new MemoMessage(BtsApplication.getAdjustTimeNowMillis(),
                MemoMessage.MSG_TYPE_TXT, MemoMessage.CMD_SEND_MSG, message, null,
                groupId, null, null, uuid);

        String strJson = tempMemo.toString();

        // 保存聊天消息
        final UserChatItem chatItem = new UserChatItem(null, null, tempMemo.getTime(), tempMemo.getTime(),
                tempMemo.getTime(), OneAccountHelper.getAccountId(), groupId,
                "", "", strJson,
                CommonConstants.DEFAULT_DAO_CODE, "", uuid, 0,
                ItemMessage.Status.CREATE.ordinal(), 0, 0, groupId);

        String encryptStrJson = Util.bytesToHex(Util.encryptAES(strJson.getBytes(Charsets.UTF_8), groupInfoItem.encrypt_key.getBytes(Charsets.UTF_8)));
        String msgType = CommonUtils.getMsgTypeByMemoString(strJson);


        String messageContent = "";
        if (groupInfoItem.public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
            messageContent = message;
        }
        OneGroupHelper.AddGroupMessageInfo(groupId, encryptStrJson, messageContent, msgType, uuid, null);

        OneAccountHelper.getDatabase().putUserChat(chatItem);
        JumpAppPageUtil.jumpGroupChatPage(context, groupInfoItem.group_uid, groupInfoItem.group_name);

    }

    // 主动加入群聊
    public static void joinChatGroup(final Context context, final String groupId, final boolean ifNeedPsw) {
        UserGroupInfoItem groupInfoItem = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId, false);
        if (groupInfoItem != null) {
            //已加入群聊
            JumpAppPageUtil.jumpGroupChatPage(context, groupInfoItem.group_uid, groupInfoItem.group_name);
            return;
        }
        if (ifNeedPsw) {
            DialogUtil.inputPswDialog(context, context.getString(R.string.input_join_group_psw), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String psw) {
                    requestJoinGroup(context, groupId, ifNeedPsw, psw);
                }
            });
        } else {
            requestJoinGroup(context, groupId, ifNeedPsw, "");
        }

    }

    private static void requestJoinGroup(final Context context, final String groupId, final boolean ifNeedPsw, String psw) {
        //申请加群请求
        OneGroupHelper.applyToJoinGroup(groupId, psw, new RequestSuccessListener<Integer>() {
            @Override
            public void onResponse(Integer result) {
                if (OneOpenHelper.checkResultCode(result)) {
                    OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
                        @Override
                        public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                            if (userGroupInfoItem != null) {
                                OneGroupHelper.getMemberListFromGroup(groupId, null);
                                sendMessageToGroup(context, groupId, String.format(context.getResources().getString(R.string.join_group_tip), UserInfoUtils.getUserInfo().getNickname()));
                            } else {
                                ToastUtils.simpleToast(R.string.join_group_error);
                            }
                        }
                    });
                } else {
                    switch (result) {
                        case ServiceConstants.REQUEST_CODE_PSW_ERROR:
                            joinChatGroup(context, groupId, ifNeedPsw);
                            ToastUtils.simpleToast(R.string.group_password_error);
                            break;
                        case ServiceConstants.REQUEST_CODE_JOIN_GROUP_NEED_ADMIN:
                            ToastUtils.simpleToast(R.string.pls_wait_examine);

                            break;
                        case ServiceConstants.REQUEST_CODE_ALREADY_IN_GROUP:
                            OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
                                @Override
                                public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                                    if (userGroupInfoItem != null) {
                                        OneGroupHelper.getMemberListFromGroup(groupId, null);
                                        JumpAppPageUtil.jumpGroupChatPage(context, userGroupInfoItem.group_uid, userGroupInfoItem.group_name);
                                    } else {
                                        ToastUtils.simpleToast(R.string.join_group_error);
                                    }
                                }
                            });
                            ToastUtils.simpleToast(R.string.already_in_group);
                            break;
                        case ServiceConstants.REQUEST_CODE_GROUP_NOT_EXIST:
                            ToastUtils.simpleToast(R.string.group_not_exist);
                            break;
                        case ServiceConstants.REQUEST_CODE_GROUP_IN_BLACK:
                            ToastUtils.simpleToast(R.string.in_group_blacklist);
                            break;
                        case ServiceConstants.REQUEST_CODE_GROUP_COUNT_EXCEED:
                            ToastUtils.simpleToast(R.string.group_created_count_exceed);
                            break;
                        case ServiceConstants.REQUEST_ERROR:
                            ToastUtils.simpleToast(R.string.try_again);
                            break;
                        default:
                            ToastUtils.simpleToast(context.getString(R.string.join_group_error) + " " + result);
                            break;
                    }
                }
            }
        });
    }


    /**
     * 添加好友
     *
     * @param accountName
     */
    public static void addFriend(final BaseActivity context, String accountName, final boolean ifFinishActivity) {

        if (accountName.length() < CommonConstants.MIN_LENGTH_ACCOUNT_NAME) {
            Toast.makeText(context, R.string.account_name_should_be_longer, Toast.LENGTH_SHORT).show();
        } else {
            context.showLoadingDialog(context.getString(R.string.accountname_add_loading));

            final UserContactItem tempOne = OneAccountHelper.getDatabase().getUserContactItemByName(accountName);
            if (tempOne != null) {
                JumpAppPageUtil.jumpOtherUserInfoPage(context, tempOne.getId());
                if (ifFinishActivity) {
                    context.finish();
                }
                context.hideLoadingDialog();
                return;
            }

            OneChatHelper.RequestUserInfoByName(accountName, new RequestSuccessListener<UserInfoBean>() {
                @Override
                public void onResponse(UserInfoBean userInfoBean) {
                    if (userInfoBean != null && !StringUtils.equalsNull(userInfoBean.getAccount_id())) {
                        JumpAppPageUtil.jumpOtherUserInfoPage(context, userInfoBean.getAccount_id());
                        context.hideLoadingDialog();
                        if (ifFinishActivity) {
                            context.finish();
                        }
                    } else {
                        ToastUtils.simpleToast(R.string.account_name_not_exist);
                        context.hideLoadingDialog();
                    }
                }
            });

        }
    }

    /**
     * 同意添加好友
     *
     * @param accountName
     */
    public static void agreeAddFriend(final BaseActivity context, String accountName, final boolean ifFinishActivity) {

        if (accountName.length() < CommonConstants.MIN_LENGTH_ACCOUNT_NAME) {
            Toast.makeText(context, R.string.account_name_should_be_longer, Toast.LENGTH_SHORT).show();
        } else {
            context.showLoadingDialog(context.getString(R.string.accountname_add_loading));

            final UserContactItem tempOne = OneAccountHelper.getDatabase().getUserContactItemByName(accountName);
            if (tempOne != null) {
                if (tempOne.getStatusFriend() != UserContactItem.StatusFriend.FRIEND.ordinal()) {
                    tempOne.setStatusFriend(UserContactItem.StatusFriend.FRIEND.ordinal());
                    OneAccountHelper.getDatabase().userContactInsertOrUpdate(tempOne);
                    // 发送第一条消息
                    sendSingleFirstMessage(context, tempOne.id);

                } else {
                    Toast.makeText(context, R.string.is_already_added, Toast.LENGTH_SHORT).show();
                    JumpAppPageUtil.jumpSingleChatPage(context, tempOne.id);
                }
                if (ifFinishActivity) {
                    context.finish();
                }
                context.hideLoadingDialog();
                return;
            }

            OneChatHelper.RequestUserInfoByName(accountName, new RequestSuccessListener<UserInfoBean>() {
                @Override
                public void onResponse(UserInfoBean userInfoBean) {
                    if (userInfoBean != null && !StringUtils.equalsNull(userInfoBean.getAccount_id())) {
                        // 发送第一条消息
                        sendSingleFirstMessage(context, userInfoBean.getAccount_id());
                        context.hideLoadingDialog();
                        if (ifFinishActivity) {
                            context.finish();
                        }
                    } else {
                        ToastUtils.simpleToast(R.string.account_name_not_exist);
                        context.hideLoadingDialog();
                    }
                }
            });

//            new WebsocketWorkerThread(new GetAccountByName(accountName, new WitnessResponseListener() {
//                @Override
//                public void onSuccess(WitnessResponse response) {
//                    final AccountProperties accountProperties = (AccountProperties) response.result;
//
//                    if (accountProperties != null) {
//
//                        final UserContactItem tempOne = new UserContactItem(accountProperties.id, accountProperties.name,
//                                accountProperties.owner.getKeyAuthList().get(0).getAddress().toString(),
//                                accountProperties.active.getKeyAuthList().get(0).getAddress().toString(),
//                                accountProperties.options.getMemoKey().getAddress().toString(),
//                                "", "", "", "",
//                                UserContactItem.StatusFriend.FRIEND.ordinal(), "");
//
//                        OneAccountHelper.getDatabase().userContactInsertOrUpdate(tempOne);
//
//                        // 更新界面
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                // 发送第一条消息
//                                sendFirstMessage(context, tempOne);
//
//                                if (ifFinishActivity) {
//                                    context.finish();
//                                }
//                                context.hideLoadingDialog();
//                            }
//                        });
//                    } else {
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.simpleToast(R.string.account_name_not_exist);
//                                context.hideLoadingDialog();
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onError(BaseResponse.Error error) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtils.simpleToast(R.string.add_friend_error);
//
//                            context.hideLoadingDialog();
//                        }
//                    });
//                }
//            })).start();

        }
    }

    public static String getMsgTypeByMemoString(String memoString) {
        String msgType = "";
        if (!StringUtils.equalsNull(memoString)) {
            if (WEB_PATTERN.matcher(memoString).find()) {
                msgType = Constants.MSG_TYPE_URL;
            }
        }
        return msgType;
    }

    public static void createGroup(final BaseActivity activity, final String memberUids, final int group_public_status, final String groupname, final boolean ifHashGid, String firstMsg, boolean ifCheckUids) {
        if (StringUtils.equalsNull(memberUids)) {
            return;
        }

        final String sendMessage;
        if (StringUtils.equalsNull(firstMsg)) {
            sendMessage = UserInfoUtils.getUserNick() + activity.getString(R.string.create_a_group);
        } else {
            sendMessage = firstMsg;
        }

        activity.showLoadingDialog(activity.getString(R.string.create_groupchat_loading));
        List<String> memberIdList = GsonUtils.jsonToObj(memberUids, new TypeToken<List<String>>() {
        }.getType());

        if (memberIdList == null) {
            activity.hideLoadingDialog();
            return;
        }
        if (!memberIdList.contains(OneAccountHelper.getAccountId())) {
            activity.hideLoadingDialog();
            ToastUtils.simpleToast(R.string.no_create_group_auth);
            return;
        }

        boolean ifHamMe = false;

        List<String> missUids = new ArrayList<>();
        for (String uid : memberIdList) {
            if (uid.equals(OneAccountHelper.getAccountId())) {
                ifHamMe = true;
                if (group_public_status == CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
                    break;
                }
            }
            if (group_public_status != CommonConstants.CHAT_GROUP_STATUS_PUBLIC) {
                UserContactItem userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(uid);
                if (userContactItem == null) {
                    missUids.add(uid);
                }
            }
        }

        if (!ifHamMe) {
            activity.hideLoadingDialog();
            ToastUtils.simpleToast(R.string.no_create_group_auth);
            return;
        }

        if (missUids.size() > 0 && ifCheckUids) {
            OneChatHelper.GetOtherUserInfoListRequest(missUids, new RequestSuccessListener<ListResult<UserInfoBean>>() {
                @Override
                public void onResponse(ListResult<UserInfoBean> userInfoBeanListResult) {
                    createGroup(activity, memberUids, group_public_status, groupname, ifHashGid, sendMessage, false);
                }
            });
            return;
        }

        final String groupUid;
        if (ifHashGid) {
            Collections.sort(memberIdList, new Comparator<String>() {
                public int compare(String uid1, String uid2) {
                    if (!StringUtils.equalsNull(uid1) && !StringUtils.equalsNull(uid2)) {
                        Long uidLong1 = StringUtils.getLongValue(OneAccountHelper.getInviteCodeById(uid1));
                        Long uidLong2 = StringUtils.getLongValue(OneAccountHelper.getInviteCodeById(uid2));
                        return uidLong1.compareTo(uidLong2);
                    } else
                        return 0;
                }
            });
            groupUid = StringUtils.getSHA1Hash(memberIdList.toString().replaceAll(" ", ""));

        } else {
            groupUid = UUID.randomUUID().toString();
        }

        UserGroupInfoItem tempRet = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupUid, false);
        if (tempRet != null && StringUtils.equals(tempRet.group_name, groupname)) {
            activity.hideLoadingDialog();
            //群已经存在
            JumpAppPageUtil.jumpGroupChatPage(activity, groupUid, tempRet.group_name);
            return;
        }

        long timeUpdate = BtsApplication.getAdjustTimeNowMillis();
        long timeCreate = timeUpdate;

        String encrypt_key = WalletUtils.generateRandomId(CommonConstants.DEFAULTGROUP_PASSWORD_LENGTH);

        String strMemberList = CommonHelperUtils.chatMemberListToJson(memberIdList, null, encrypt_key, group_public_status);

        final UserGroupInfoItem group = new UserGroupInfoItem(null,
                timeCreate, timeUpdate, groupname, groupUid, "", OneAccountHelper.getAccountId(),
                group_public_status, "", memberIdList.size(), 0, strMemberList, encrypt_key, null);

        //添加群聊请求
        OneGroupHelper.createGroupWithConfiguration(group, new RequestSuccessListener<Integer>() {
            @Override
            public void onResponse(Integer code) {
                switch (code) {
                    case ServiceConstants.REQUEST_RESULT_CODE_OK:
//                        ToastUtils.simpleToast(R.string.pls_wait_apply);
                        activity.hideLoadingDialog();
                        OneAccountHelper.getDatabase().putGroupInfo(group);
                        sendMessageToGroup(activity, group.group_uid, sendMessage);
                        JumpAppPageUtil.jumpGroupChatPage(activity, group.group_uid, group.group_name);
                        break;
                    case ServiceConstants.REQUEST_CODE_GROUP_HAS_EXIST://群已创建
                        OneGroupHelper.GetItemGroupInfoRequest(groupUid, new RequestSuccessListener<UserGroupInfoItem>() {
                            @Override
                            public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                                if (userGroupInfoItem != null) {
                                    if (!userGroupInfoItem.group_name.equals(groupname)) {
                                        //更新群请求
                                        userGroupInfoItem.group_name = groupname;
                                        OneGroupHelper.updateGroupWithConfiguration(userGroupInfoItem, null);
                                        sendMessageToGroup(activity, userGroupInfoItem.group_uid, sendMessage);
                                    }
                                    OneAccountHelper.getDatabase().putGroupInfo(userGroupInfoItem);
                                    JumpAppPageUtil.jumpGroupChatPage(activity, userGroupInfoItem.group_uid, userGroupInfoItem.group_name);
                                } else {
                                    ToastUtils.simpleToast(R.string.join_group_error);
                                }
                                activity.hideLoadingDialog();
                            }
                        });
                        break;
                    case ServiceConstants.REQUEST_CODE_GROUP_COUNT_EXCEED:
                        activity.hideLoadingDialog();
                        ToastUtils.simpleToast(R.string.group_created_count_exceed);
                        break;
                    case ServiceConstants.REQUEST_ERROR:
                        activity.hideLoadingDialog();
                        ToastUtils.simpleToast(R.string.try_again);
                        break;
                    default:
                        activity.hideLoadingDialog();
                        ToastUtils.simpleToast(getString(activity, R.string.erro) + " " + code);
                        break;
                }
            }
        });
    }
}
