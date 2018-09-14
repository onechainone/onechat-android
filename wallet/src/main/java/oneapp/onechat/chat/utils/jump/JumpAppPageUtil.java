package oneapp.onechat.chat.utils.jump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.List;

import oneapp.onechat.chat.MainActivity;
import oneapp.onechat.chat.WalletApplication;
import oneapp.onechat.chat.sharesdk.sms.RegisterPage;
import oneapp.onechat.chat.view.AccountCreateActivity;
import oneapp.onechat.chat.view.AccountRestoreActivity;
import oneapp.onechat.chat.view.NotificationActivity;
import oneapp.onechat.chat.view.RegisterGuideActivity;
import oneapp.onechat.chat.view.SelectSkinActivity;
import oneapp.onechat.chat.view.SetUserInfoActivity;
import oneapp.onechat.chat.view.SettingActivity;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneRedpacketHelper;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import onemessageui.chat.NewChatActivity;
import onemessageui.chat.utils.LinkifySpannableUtils;
import onemessageui.community.activity.ImagePagerActivity;
import onemessageui.community.activity.PayWeiboActivity;
import onemessageui.community.activity.SendWeiboActivity;
import onemessageui.community.activity.SetWeiboPayActivity;
import onemessageui.community.activity.WeiboContentActivity;
import onemessageui.community.activity.WeiboListActivity;
import onemessageui.community.activity.WeiboMsgListActivity;
import onemessageui.community.video.VideoPlayActivity;
import onemessageui.dialog.DialogUtil;
import onemessageui.utils.CommonUtils;
import onemessageui.view.activity.AddFriendActivity;
import oneapp.onechat.chat.view.AddServiceNodeActivity;
import onemessageui.view.activity.CheckSignActivity;
import onemessageui.view.activity.CreateSeedActivity;
import onemessageui.view.activity.DeleteGroupMemberActivity;
import onemessageui.view.activity.DeriveBrainkeyActivity;
import onemessageui.view.activity.FriendApplyActivity;
import onemessageui.view.activity.FriendsListActivity;
import onemessageui.view.activity.GroupApplyActivity;
import onemessageui.view.activity.GroupListActivity;
import onemessageui.view.activity.GroupMembersActivity;
import onemessageui.view.activity.GroupQrCodeActivity;
import onemessageui.view.activity.InputSeedActivity;
import onemessageui.view.activity.MyQrCodeActivity;
import onemessageui.view.activity.NativeWebViewActivity;
import onemessageui.view.activity.OtherUserInfoActivity;
import oneapp.onechat.chat.view.SelectLangurageActivity;
import onemessageui.view.activity.SendRedPacketActivity;
import onemessageui.view.activity.SimpleWebViewActivity;
import onemessageui.view.activity.SocialRedPacketActivity;
import onemessageui.view.activity.SwitchServiceNodeActivity;
import onemessageui.view.activity.TestServiceNodeActivity;
import onemessageui.view.activity.UnlockActivity;
import onemessageui.view.activity.WaitReviewActivity;
import onewalletui.ui.BaseActivity;
import onemessageui.view.activity.MakeSureSeedActivity;
import oneapp.onechat.chat.view.ShowSeedActivity;
import oneapp.onechat.androidapp.R;

public class JumpAppPageUtil {


    private static void jump(Context context, BaseJumpBean jumpBean) {
        jump(context, jumpBean, false);
    }

    private static void jump(Context context, BaseJumpBean jumpBean, boolean ifJumpNewActivity) {
        if (jumpBean != null) {
            final Context contexts = (context != null) ? context
                    : WalletApplication.getInstance();
            final Intent intent = new Intent(contexts,
                    jumpBean.getWhichActivity());
            if (jumpBean.getParamsMap() != null) {
                intent.putExtra(JumpParamsContants.INTENT_PARAMS_MAP,
                        jumpBean.getParamsMap());
            }
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (ifJumpNewActivity) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            contexts.startActivity(intent);
        } else {
            // 跳转失败

        }
    }


    /**
     * 跳转其它用户信息界面
     *
     * @param context
     */
    public static void jumpOtherUserInfoPage(final Context context, final String mUserAccountId) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(OtherUserInfoActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(mUserAccountId)) {
            if (!mUserAccountId.equals(BtsHelper.mMeAccountId)) {
                map.put(JumpParamsContants.INTENT_USER_ACCOUNT_ID, mUserAccountId);
                baseBean.setParamsMap(map);
                jump(context, baseBean);
            } else {
                //mUserAccountId是自己
            }
        }
    }

    /**
     * 跳转其它用户信息界面
     *
     * @param context
     */
    public static void jumpOtherUserInfoPageByName(final Context context, final String mUserAccountName) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(OtherUserInfoActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(mUserAccountName)) {
            if (!mUserAccountName.equals(BtsHelper.mMeAccountName)) {
                map.put(JumpParamsContants.INTENT_ACCOUNT_NAME, mUserAccountName);
                baseBean.setParamsMap(map);
                jump(context, baseBean);
            } else {
                //mUserAccountName
            }
        }
    }

    /**
     * 跳转显示脑钱包界面
     *
     * @param context
     */
    public static void jumpShowSeedPage(final Context context, final boolean ifFirstCreateWallet) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(ShowSeedActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        map.put(JumpParamsContants.INTENT_IF_FIRST_CREATE_ACCOUNT, ifFirstCreateWallet);
        baseBean.setParamsMap(map);
        jump(context, baseBean);
    }


    /**
     * 跳转通讯录、好友列表界面
     *
     * @param context
     */
    public static void jumpFriendListPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(FriendsListActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转group列表界面
     *
     * @param context
     */
    public static void jumpGroupListPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(GroupListActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转与用户聊天界面
     *
     * @param context
     */
    public static void jumpSingleChatPage(Context context, final String mUserAccountId) {
        if (mUserAccountId.equals(BtsHelper.mMeAccountId))
            return;
        Intent intent = new Intent(
                context,
                NewChatActivity.class);
        intent.putExtra(oneapp.onechat.oneandroid.onemessage.Constants.TYPE,
                oneapp.onechat.oneandroid.onemessage.Constants.CHATTYPE_SINGLE);
        intent.putExtra(oneapp.onechat.oneandroid.onemessage.Constants.User_ID,
                mUserAccountId);
        context.startActivity(intent);
    }

    /**
     * 跳转群聊天界面
     *
     * @param context
     */
    public static void jumpGroupChatPage(Context context, String groupId, String groupName) {
        if (StringUtils.equalsNull(groupId))
            return;
        Intent intent = new Intent(context, NewChatActivity.class);
        intent.putExtra(oneapp.onechat.oneandroid.onemessage.Constants.NAME, groupName);
        intent.putExtra(oneapp.onechat.oneandroid.onemessage.Constants.TYPE, oneapp.onechat.oneandroid.onemessage.Constants.CHATTYPE_GROUP);
        intent.putExtra(oneapp.onechat.oneandroid.onemessage.Constants.GROUP_ID, groupId);
        context.startActivity(intent);

    }

    /**
     * 跳转手势解密界面
     *
     * @param context
     */
    public static void jumpUnlockPage(Context context) {
        if (!OneAccountHelper.ifHasAccountInfo() || SharePreferenceUtils.contains(SharePreferenceUtils.SP_IF_SET_HAND_LOCK)) {

            final BaseJumpBean baseBean = new BaseJumpBean();
            baseBean.setWhichActivity(UnlockActivity.class);

            jump(context, baseBean);
        }
    }

    /**
     * 跳转修改手势解密界面
     *
     * @param context
     */
    public static void jumpResetUnlockPage(Context context) {
        Intent intent = new Intent(
                context,
                UnlockActivity.class);
        intent.putExtra(oneapp.onechat.oneandroid.onemessage.Constants.TYPE,
                "reset");
        context.startActivity(intent);
    }

    /**
     * 跳转主页
     *
     * @param context
     */
    public static void jumpMainPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(MainActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        baseBean.setParamsMap(map);
        jump(context, baseBean);
    }

    /**
     * 跳转主页
     *
     * @param context
     */
    public static void jumpMainPageWithOutParam(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(MainActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转并刷新主页
     *
     * @param context
     */
    public static void jumpNewMainPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(MainActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        baseBean.setParamsMap(map);
        jump(context, baseBean, true);
    }

    /**
     * 跳转切换语言界面
     *
     * @param context
     */
    public static void jumpSelectLanguragePage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SelectLangurageActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转我的二维码
     *
     * @param context
     */
    public static void jumpMyQrCodePage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(MyQrCodeActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转添加好友
     *
     * @param context
     */
    public static void jumpAddFriendPage(Context context, String accountName) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(AddFriendActivity.class);
        if (!StringUtils.equalsNull(accountName)) {
            final HashMap<String, Object> map = new HashMap<>();
            map.put(JumpParamsContants.INTENT_ACCOUNT_NAME, accountName);
            baseBean.setParamsMap(map);
        }
        jump(context, baseBean);
    }

    public static void jumpAccountCreatePage(Context context, String seed) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(AccountCreateActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(seed)) {
            map.put(JumpParamsContants.INTENT_SEED, seed);

            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }

//    public static void jumpAccountCreatePageTwo(Context context) {
//        final BaseJumpBean baseBean = new BaseJumpBean();
//        baseBean.setWhichActivity(AccountCreateActivity.class);
//        jump(context, baseBean);
//    }

    public static void jumpAccountRestorePage(Context context) {
        Intent intent = new Intent(context, AccountRestoreActivity.class);
        context.startActivity(intent);

    }

    public static void jumpCreateSeedPage(Context context, String seed, boolean firstSeed) {
        Intent intent = new Intent(context, CreateSeedActivity.class);
        if (!StringUtils.equalsNull(seed)) {
            intent.putExtra(JumpParamsContants.INTENT_SEED, seed);
        }
        intent.putExtra(JumpParamsContants.INTENT_FIRST_SEED, firstSeed);
        context.startActivity(intent);
    }

    /**
     * 跳转到IOU转账界面
     *
     * @param context
     */
    public static void jumpSendRedPacketPage(final Context context, final String groupId, int redPacketType, String assetCode) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SendRedPacketActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(groupId)) {
            map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
        }
        if (!StringUtils.equalsNull(assetCode)) {
            map.put(JumpParamsContants.INTENT_ASSET_CODE, assetCode);
        }

        map.put(JumpParamsContants.INTENT_RED_PACKET_TYPE, redPacketType);
        baseBean.setParamsMap(map);
        jump(context, baseBean);
    }

    public static void jumpRedPacketInfoPage(final Context context, final String redPacketId) {
        jumpNativeWebView(context, OneRedpacketHelper.redpacketDetailH5FromRedpacketId(redPacketId), context.getString(R.string.red_packet_info), CommonConstants.H5_TYPE_RED_PACKET);
    }


    /**
     * 跳转到IOU转账界面
     *
     * @param context
     */
    public static void jumpMakeSureSeedPage(final Context context, String seed, boolean firstSeed) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(MakeSureSeedActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(seed)) {
            map.put(JumpParamsContants.INTENT_SEED, seed);
            map.put(JumpParamsContants.INTENT_FIRST_SEED, firstSeed);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }

    }
//
//    public static void jumpMakeSureSeedPageTwo(final Context context) {
//        final BaseJumpBean baseBean = new BaseJumpBean();
//        baseBean.setWhichActivity(MakeSureSeedActivity.class);
//        jump(context, baseBean);
//    }

    /**
     * 跳转到H5界面
     *
     * @param context
     */
    public static void jumpNativeWebView(final Context context, final String url, final String title, int type) {
        jumpNativeWebView(context, url, title, type, null);
    }

    /**
     * 跳转到H5界面
     *
     * @param context
     */
    public static void jumpNativeWebView(final Context context, final String url, final String title, int type, String groupId) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(NativeWebViewActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(url)) {
            map.put(JumpParamsContants.INTENT_WEB_URL, url);
            map.put(JumpParamsContants.INTENT_TITLE, title);
            map.put(JumpParamsContants.INTENT_TYPE, type);
            if (!StringUtils.equalsNull(groupId)) {
                map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
            }
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }

    /**
     * 跳转到H5界面
     *
     * @param context
     */
    public static void jumpSimpleWebView(final Context context, final String url) {

        Utils.start_Activity(context,
                SimpleWebViewActivity.class,
                new BasicNameValuePair(oneapp.onechat.oneandroid.onemessage.Constants.URL, url));
    }


    /**
     * 跳转到H5界面
     *
     * @param context
     */
    public static void jumpSocialRedPacketPage(final Context context, final String redPacketId) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SocialRedPacketActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(redPacketId)) {
            map.put(JumpParamsContants.INTENT_RED_PACKET_ID, redPacketId);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }

    }

    /**
     * @param context
     */
    public static void jumpDeleteGroupMember(final Context context, final String groupId, int type) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(DeleteGroupMemberActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(groupId)) {
            map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }

    /**
     * 跳转到群成员界面
     *
     * @param context
     */
    public static void jumpAllGroupMember(final Context context, final String groupId, boolean isInDeleteMode) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(GroupMembersActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(groupId)) {
            map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
            map.put(JumpParamsContants.INTENT_ISINDELETEMODE, isInDeleteMode);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }

    /**
     * 跳转到切换节点界面
     *
     * @param context
     */
    public static void jumpSetServiceNodePage(final Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SwitchServiceNodeActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转到刷新节点界面
     *
     * @param context
     */
    public static void jumpConfigServiceNodePage(final Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(TestServiceNodeActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转到输入助记词界面
     *
     * @param activity
     */
    public static void jumpInputSeedPage(Activity activity, boolean ifForResult, String seed) {
        Intent intent = new Intent(activity, InputSeedActivity.class);
        intent.putExtra(JumpParamsContants.INTENT_IF_FOR_RESULT,
                ifForResult);
        intent.putExtra(JumpParamsContants.INTENT_INPUT_SEED,
                seed);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_INPUT_SEED_WORD);
    }

    /**
     * 跳转到导出加密助记词界面
     */
    public static void jumpDeriveBrainkeyPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(DeriveBrainkeyActivity.class);
        jump(context, baseBean);
    }


    /**
     * 跳转发送社区
     */
    public static void jumpSendWeiboPage(Activity activity, String groupId) {
        Intent intent = new Intent(activity, SendWeiboActivity.class);
        intent.putExtra(JumpParamsContants.INTENT_GROUP_ID,
                groupId);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_SET_WEIBO_PAY);
    }


    /**
     * 跳转到播放视频界面
     *
     * @param context
     * @param from    来自于哪个页面
     */

    public static void jumpVideoPlayPage(Context context, String video_url, String from) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setFrom(from);
        baseBean.setWhichActivity(VideoPlayActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(video_url)) {
            map.put(JumpParamsContants.INTENT_VIDEO_URL, video_url);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }


    /**
     * 打开图片查看器
     */
    public static void jumpImagePage(Context context, String from, int position, List<String> imgUrls) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setFrom(from);
        baseBean.setWhichActivity(ImagePagerActivity.class);

        final HashMap<String, Object> map = new HashMap<>();
        map.put(JumpParamsContants.POSITION, position);
        map.put(JumpParamsContants.IMGURLS, imgUrls);
        baseBean.setParamsMap(map);

        jump(context, baseBean);

    }

    /**
     * 跳转到微博详情界面
     *
     * @param context
     * @param from    来自于哪个页面
     */
    public static void jumpWeiboContentPage(Context context, String weiboId, String sourceWeiboId, String from) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setFrom(from);
        baseBean.setWhichActivity(WeiboContentActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(weiboId) && !StringUtils.equalsNull(sourceWeiboId)) {
            map.put(JumpParamsContants.WEIBO_ID, weiboId);
            map.put(JumpParamsContants.SOURCE_WEIBO_ID, sourceWeiboId);
            baseBean.setParamsMap(map);
        }
        jump(context, baseBean);
    }

    /**
     * 跳转到微博消息通知界面
     *
     * @param context
     * @param from    来自于哪个页面
     */
    public static void jumpWeiboMsg(Context context, String from, String groupId) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setFrom(from);
        baseBean.setWhichActivity(WeiboMsgListActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(groupId)) {
            map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }

    /**
     * 跳转到微博界面
     *
     * @param context
     */
    public static void jumpWeiboList(Context context, String groupId) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(WeiboListActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(groupId)) {
            map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }

    /**
     * 跳转到收费设置页面
     *
     * @param activity
     */
    public static void jumpSetWeiboPayPage(Activity activity, String payAssetCode, String payValue) {
        Intent intent = new Intent(activity, SetWeiboPayActivity.class);
        intent.putExtra(JumpParamsContants.INTENT_ASSET_CODE, payAssetCode);
        intent.putExtra(JumpParamsContants.INTENT_VALUE, payValue);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_SET_WEIBO_PAY);
    }

    /**
     * 跳转到付费微博或打赏页面
     *
     * @param activity
     */
    public static void jumpPayWeiboPage(Activity activity, String webId, String mAssetCode, String mPayValue, String groupId) {
        Intent intent = new Intent(activity, PayWeiboActivity.class);
        intent.putExtra(JumpParamsContants.WEIBO_ID,
                webId);
        intent.putExtra(JumpParamsContants.INTENT_ASSET_CODE,
                mAssetCode);
        intent.putExtra(JumpParamsContants.INTENT_VALUE,
                mPayValue);
        intent.putExtra(JumpParamsContants.INTENT_GROUP_ID,
                groupId);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_PAY_WEIBO);

    }

    /**
     * 跳转到赞赏消息页面
     *
     * @param activity
     */
    public static void jumpPayMsgPage(Activity activity, String userId, String msgId) {
        Intent intent = new Intent(activity, PayWeiboActivity.class);
        intent.putExtra(JumpParamsContants.INTENT_USER_ACCOUNT_ID,
                userId);
        intent.putExtra(JumpParamsContants.INTENT_MSG_ID,
                msgId);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_REWARD_MSG);

    }

    /**
     * 跳转到群二维码页面
     *
     * @param context
     */
    public static void jumpGroupQrCodePage(Context context, String groupId, String groupUrl, String id, String description) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(GroupQrCodeActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.equalsNull(groupId)) {
            map.put(JumpParamsContants.INTENT_GROUP_ID, groupId);
            map.put(JumpParamsContants.INTENT_ADD_GROUP_URL, groupUrl);
            map.put(JumpParamsContants.INTENT_SINGLE_GROUP_ID, id);
            map.put(JumpParamsContants.INTENT_GROUP_DESCRIPTION, description);
            baseBean.setParamsMap(map);
            jump(context, baseBean);
        }
    }


    /**
     * 跳转群邀请界面
     *
     * @param context
     */
    public static void jumpGroupApplyPage(final Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(GroupApplyActivity.class);
        jump(context, baseBean);

    }

    /**
     * 跳转好友邀请界面
     *
     * @param context
     */
    public static void jumpFriendApplyPage(final Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(FriendApplyActivity.class);
        jump(context, baseBean);

    }


    /**
     * 跳转到等待审核用户界面
     */
    public static void jumpWaitReviewPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(WaitReviewActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转到注册和回复账号界面
     *
     * @param context
     */
    public static void jumpRegisterGuidePage(Context context, int from) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(RegisterGuideActivity.class);
        final HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.ARG_FROM, from);
        baseBean.setParamsMap(map);

        jump(context, baseBean);
    }

    /**
     * 跳转设置界面
     *
     * @param context
     */
    public static void jumpSettingPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SettingActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转到绑定手机号界面
     */
    public static void jumpBindPhonePage(Context context) {
        if (StringUtils.equalsNull(UserInfoUtils.getUserInfo().getMobile())) {
            Intent intent = new Intent(context, RegisterPage.class);
            context.startActivity(intent);
        } else {
            DialogUtil.tipDialog(context, context.getString(R.string.had_bind_phone));
        }
    }

    /**
     * 跳转到添加节点界面
     */
    public static void jumpAddServiceNodePage(Activity context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(AddServiceNodeActivity.class);
        jump(context, baseBean);
    }
    /**
     * 跳转到新消息通知界面
     */
    public static void jumpNewNatification(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(NotificationActivity.class);
        jump(context, baseBean);
    }


    /**
     * 跳转换肤界面
     *
     * @param context
     */
    public static void jumpSelectSkinPage(Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SelectSkinActivity.class);
        jump(context, baseBean);
    }

    /**
     * 跳转到修改信息界面
     *
     * @param context
     */
    public static void jumpSetUserInfoPage(final Context context) {
        final BaseJumpBean baseBean = new BaseJumpBean();
        baseBean.setWhichActivity(SetUserInfoActivity.class);
        jump(context, baseBean);
    }

}
