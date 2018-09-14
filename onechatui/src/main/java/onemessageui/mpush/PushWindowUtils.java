//package onemessageui.mpush;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.PixelFormat;
//import android.os.Build;
//import android.support.v4.app.ActivityCompat;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.WindowManager.LayoutParams;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.common.collect.ImmutableList;
//
//import org.apache.http.message.BasicNameValuePair;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import sdk.android.onechatui.R;
//import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
//import oneapp.onechat.oneandroid.onemessage.CommonConstants;
//import oneapp.onechat.oneandroid.onemessage.common.Utils;
//import oneapp.onechat.chat.MainActivity;
//import onemessageui.dialog.DialogUtil;
//import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
//import oneapp.onechat.oneandroid.onewallet.network.RequestSuccessListener;
//import oneapp.onechat.oneandroid.onewallet.network.RequestUtils;
//import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
//import oneapp.onechat.oneandroid.onewallet.ui.BaseActivity;
//import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
//import onemessageui.mpush.bean.PushAddGroupBean;
//import onemessageui.mpush.bean.PushAddUserBean;
//import onemessageui.mpush.bean.PushMsgBean;
//import onemessageui.mpush.bean.PushWeiboMsgBean;
//import onemessageui.utils.CommonUtils;
//import onemessageui.view.activity.SimpleWebViewActivity;
//import onewalletui.util.ImageUtils;
//import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
//import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
//import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
//import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
//import oneapp.onechat.oneandroid.onewallet.util.jump.JumpAppPageUtil;
//
//import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
//
///**
// * Created by 何帅 on 2018/5/19.
// */
//
//public class PushWindowUtils {
//    private static final String LOG_TAG = "WindowUtils";
//    private static View mView = null;
//    private static WindowManager mWindowManager = null;
//    private static Context mContext = null;
//
//    private static float int_x = 0;
//    private static float int_y = 0;
//
//    public static Boolean isShown = false;
//
//    /**
//     * 显示弹出框
//     *
//     * @param context
//     */
//    public static void showPopupWindow(final Context context, PushMsgBean pushMsgBean) {
//        try {
//            if (isShown || pushMsgBean == null || StringUtils.equals(pushMsgBean.getShow(), ServiceConstants.SERVICE_SAFE_STATUS_NO)) {
//                return;
//            }
//
//            isShown = true;
//
//            // 获取应用的Context
//            mContext = context.getApplicationContext();
//            // 获取WindowManager
//            mWindowManager = (WindowManager) mContext
//                    .getSystemService(Context.WINDOW_SERVICE);
//
//            mView = setUpView(context, pushMsgBean);
//
//            if (mView != null) {
//                mWindowManager.addView(mView, getWindowManagerParams());
//            }
//
//            // 初始化定时器
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    hidePopupWindow();
//                }
//            }, 2500);
//
//        } catch (Exception e) {
//
//        }
//    }
//
//    /**
//     * Protected method used by
//     */
//    private static WindowManager.LayoutParams getWindowManagerParams() {
//        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        layoutParams.width = LayoutParams.MATCH_PARENT;
//        layoutParams.height = LayoutParams.WRAP_CONTENT;
//        layoutParams.format = PixelFormat.TRANSLUCENT;
//        layoutParams.windowAnimations = R.style.AnimTop;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (MainActivity.getInstance() != null) {
//                ActivityCompat.requestPermissions(MainActivity.getInstance(), new String[]{SYSTEM_ALERT_WINDOW}, 0);
//            }
//            layoutParams.type = LayoutParams.TYPE_PHONE;
//        } else {
//            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        }
//        layoutParams.gravity = Gravity.TOP;
////        layoutParams.x = this.mStyle.xOffset;
////        layoutParams.y = this.mStyle.yOffset;
//        return layoutParams;
//    }
//
//    private static View setUpView(final Context context, final PushMsgBean pushMsgBean) {
//        View view;
//        view = LayoutInflater.from(context).inflate(R.layout.push_simple_window_view, null);
//
//        view.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        int_x = event.getRawX();
//                        int_y = event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (Math.abs(int_y) - Math.abs(event.getRawY()) > 100.0) {
//                            hidePopupWindow();
//                        } else {
//                        }
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//
//                        break;
//                }
//                return false;
//            }
//        });
//
////        view.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                hidePopupWindow();
////            }
////        });
//
//
//        ImageView userAvatar = (ImageView) view.findViewById(R.id.iv_user_avatar);
//        TextView pushTitleTv = (TextView) view.findViewById(R.id.tv_push_title);
//        TextView mPushContent = (TextView) view.findViewById(R.id.tv_push_content);
//        TextView createTime = (TextView) view.findViewById(R.id.tv_time);
//        TextView tvRefuse = (TextView) view.findViewById(R.id.tv_refuse);
//        TextView tvAgree = (TextView) view.findViewById(R.id.tv_agree);
//
//        String pushTitle = "", pushContent = "";
//        if (pushMsgBean == null || StringUtils.equalsNull(pushMsgBean.getType())) {
//            view = null;
//        } else {
//            final String content = pushMsgBean.getContent();
//            switch (pushMsgBean.getType()) {
//                case PushUtils.PUSH_TYPE_JOIN_GROUP:
//                    tvRefuse.setVisibility(View.VISIBLE);
//                    tvAgree.setVisibility(View.VISIBLE);
//                    final PushJoinGroupBean pushJoinGroupBean = GsonUtils.jsonToObj(content, PushJoinGroupBean.class);
//                    if (pushJoinGroupBean != null) {
//                        pushTitleTv.setText(pushJoinGroupBean.getNickname() + context.getString(R.string.audit_join) + pushJoinGroupBean.getGroup_name());
//                        mPushContent.setText(context.getString(R.string.onechat_id) + pushJoinGroupBean.getAccount_name());
//                        createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//
//                        ImageUtils.displayCircleNetImage(context, pushJoinGroupBean.getAvatar_url(), userAvatar, R.drawable.default_group);
//
//                        tvRefuse.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                List<String> memberList = ImmutableList.of(
//                                        pushJoinGroupBean.getAccount_id()
//                                );
//                                String members_uid = GsonUtils.objToJson(memberList);
//                                RequestUtils.GetAddGroupAuditRequest(pushJoinGroupBean.getAccount_id(), pushJoinGroupBean.getGroup_uid(), String.valueOf(false), new RequestSuccessListener<List<String>>() {
//                                    @Override
//                                    public void onResponse(List<String> result) {
//                                        if (result != null) {
//                                            BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                            ToastUtils.simpleToast(R.string.success);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                        tvAgree.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                List<String> memberList = ImmutableList.of(
//                                        pushJoinGroupBean.getAccount_id()
//                                );
//                                String members_uid = GsonUtils.objToJson(memberList);
//                                RequestUtils.GetAddGroupAuditRequest(pushJoinGroupBean.getAccount_id(), pushJoinGroupBean.getGroup_uid(), String.valueOf(true), new RequestSuccessListener<List<String>>() {
//                                    @Override
//                                    public void onResponse(List<String> result) {
//                                        if (result != null) {
//                                            BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                            ToastUtils.simpleToast(R.string.success);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
//                    break;
//
//                case PushUtils.PUSH_TYPE_WEIBO_REWARD:
//                case PushUtils.PUSH_TYPE_WEIBO_COMMENT:
//                case PushUtils.PUSH_TYPE_WEIBO_PAY:
//
//                    tvRefuse.setVisibility(View.GONE);
//                    tvAgree.setVisibility(View.GONE);
//
//                    final PushWeiboMsgBean weiboMsgBean = GsonUtils.jsonToObj(content, PushWeiboMsgBean.class);
//                    if (weiboMsgBean != null) {
//                        switch (pushMsgBean.getType()) {
//                            case PushUtils.PUSH_TYPE_WEIBO_REWARD:
//                                pushTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_reward_weibo);
//                                pushContent = weiboMsgBean.getWeibo_content();
//                                break;
//                            case PushUtils.PUSH_TYPE_WEIBO_COMMENT:
//                                pushTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_comment_weibo);
//                                pushContent = weiboMsgBean.getComment_content();
//                                break;
//                            case PushUtils.PUSH_TYPE_WEIBO_PAY:
//                                pushTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_pay_weibo);
//                                pushContent = weiboMsgBean.getWeibo_content();
//                                break;
//                        }
//                        pushTitleTv.setText(pushTitle);
//                        mPushContent.setText(pushContent);
//                        createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                        ImageUtils.displayAvatarNetImage(context, weiboMsgBean.getAvatar_url(), userAvatar, null);
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                JumpAppPageUtil.jumpWeiboContentPage(context, weiboMsgBean.getWeibo_id(), weiboMsgBean.getWeibo_id(), "");
//                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                            }
//                        });
//                    }
//                    break;
//                case PushUtils.PUSH_TYPE_URL:
//
//                    tvRefuse.setVisibility(View.GONE);
//                    tvAgree.setVisibility(View.GONE);
//
//                    final PushUrlBean pushUrlBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushUrlBean.class);
//                    if (pushUrlBean != null) {
//                        pushTitleTv.setText(pushUrlBean.getTitle());
//                        mPushContent.setText(pushUrlBean.getMessage());
//                        createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                        ImageUtils.displayCircleNetImage(context, pushUrlBean.getImg_url(), userAvatar, R.drawable.share_icon);
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Utils.start_Activity(context,
//                                        SimpleWebViewActivity.class,
//                                        new BasicNameValuePair(oneapp.onechat.oneandroid.onemessage.Constants.URL, pushUrlBean.getWeb_url()));
//                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                            }
//                        });
//                    }
//                    break;
//                case PushUtils.PUSH_TYPE_ADD_USER:
//                    tvRefuse.setVisibility(View.VISIBLE);
//                    tvAgree.setVisibility(View.VISIBLE);
//                    final PushAddUserBean pushAddUserBean = GsonUtils.jsonToObj(content, PushAddUserBean.class);
//                    if (pushAddUserBean != null) {
//                        final String accountName = pushAddUserBean.getAccount_name();
//                        pushTitleTv.setText(pushAddUserBean.getNickname() + context.getString(R.string.apply_add_friend));
//                        mPushContent.setText(pushAddUserBean.getRemark());
//                        createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                        ImageUtils.displayAvatarNetImage(context, pushAddUserBean.getAvatar_url(), userAvatar, null);
//
//                        tvRefuse.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                RequestUtils.AgreeOrFixRemarkRequest(accountName, ServiceConstants.SERVICE_SAFE_STATUS_NO, null, new RequestSuccessListener<MapResult>() {
//                                    @Override
//                                    public void onResponse(MapResult result) {
//                                        if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                            BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                            ToastUtils.simpleToast(R.string.success);
//                                        } else {
//                                            ToastUtils.simpleToast(R.string.erro);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                        tvAgree.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                RequestUtils.AgreeOrFixRemarkRequest(accountName, ServiceConstants.SERVICE_SAFE_STATUS_YES, null, new RequestSuccessListener<MapResult>() {
//                                    @Override
//                                    public void onResponse(MapResult result) {
//                                        if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                            BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                            ToastUtils.simpleToast(R.string.success);
//                                            CommonUtils.agreeAddFriend((BaseActivity) context, accountName, false);
//                                        } else {
//                                            ToastUtils.simpleToast(R.string.erro);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//
//                        mPushContent.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                DialogUtil.tipDialog(context, pushAddUserBean.getRemark(), true);
//                            }
//                        });
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                JumpAppPageUtil.jumpOtherUserInfoPageByName(context, accountName);
//                            }
//                        });
//                    }
//                    break;
//
//                case PushUtils.PUSH_TYPE_ADD_GROUP:
//                    tvRefuse.setVisibility(View.VISIBLE);
//                    tvAgree.setVisibility(View.VISIBLE);
//                    final PushAddGroupBean pushAddGroupBean = GsonUtils.jsonToObj(content, PushAddGroupBean.class);
//                    if (pushAddGroupBean != null) {
//                        final String groupUid = pushAddGroupBean.getGroup_uid();
//                        pushTitleTv.setText(pushAddGroupBean.getGroup_name() + context.getString(R.string.invite_you_join_group));
//                        mPushContent.setText(pushAddGroupBean.getRemark());
//                        createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                        ImageUtils.displayCircleNetImage(context, pushAddGroupBean.getAvatar_url(), userAvatar, R.drawable.default_group);
//
//                        tvRefuse.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                RequestUtils.EnterIntoGroupRequest(groupUid, ServiceConstants.SERVICE_SAFE_STATUS_NO, new RequestSuccessListener<MapResult>() {
//                                    @Override
//                                    public void onResponse(MapResult result) {
//                                        if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                            BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                            ToastUtils.simpleToast(R.string.success);
//                                            CommonUtils.sendMessageToGroup(context, groupUid, String.format(context.getResources().getString(R.string.join_group_tip), UserInfoUtils.getUserInfo().getNickname()));
//                                        } else {
//                                            ToastUtils.simpleToast(R.string.erro);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                        tvAgree.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                RequestUtils.EnterIntoGroupRequest(groupUid, ServiceConstants.SERVICE_SAFE_STATUS_YES, new RequestSuccessListener<MapResult>() {
//                                    @Override
//                                    public void onResponse(MapResult result) {
//                                        if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                            BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                            ToastUtils.simpleToast(R.string.success);
//                                            CommonUtils.sendMessageToGroup(context, groupUid, String.format(context.getResources().getString(R.string.join_group_tip), UserInfoUtils.getUserInfo().getNickname()));
//                                        } else {
//                                            ToastUtils.simpleToast(R.string.erro);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    }
//                    break;
//
//                case PushUtils.PUSH_TYPE_ORDER_PUSH:
//                    tvRefuse.setVisibility(View.GONE);
//                    tvAgree.setVisibility(View.GONE);
//
//                    final OrderChangeBean orderChangeBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), OrderChangeBean.class);
//                    if (orderChangeBean != null) {
//                        pushTitleTv.setText(context.getString(R.string.order_has_change));
//                        mPushContent.setText(context.getString(R.string.order_id) + orderChangeBean.getId());
//                        createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                        ImageUtils.displayAvatarNetImage(context, UserInfoUtils.getUserAvatar(), userAvatar, null);
//
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                JumpAppPageUtil.jumpNativeWebView(context, ServiceConstants.GetMoneyGoAndOutUrlWithUid(orderChangeBean.getUni_uuid()), "", CommonConstants.H5_TYPE_SIMPLE);
//                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                            }
//                        });
//                    }
//                    break;
//                default:
//                    view = null;
//                    break;
//            }
//        }
//        return view;
//    }
//
//    /**
//     * 隐藏弹出框
//     */
//    public static void hidePopupWindow() {
//        try {
//            if (null != mView) {
//                mWindowManager.removeView(mView);
//                mView = null;
//                isShown = false;
//            }
//
//        } catch (Exception e) {
//
//        }
//
//    }
//
//    /**
//     * 对话框
//     *
//     * @param context
//     * @return
//     */
//    public static Dialog pushDialog(final Context context, final PushMsgBean pushMsgBean) {
//
//        if (context == null) {
//            return null;
//        }
//        boolean ifShow = true;
//        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.push_simple_window_view, null,
//                false);
//        final Dialog dialog = new Dialog(context, R.style.PushDialogStyle);
//        try {
//            mContext = context;
//            dialog.setContentView(layout);
//
//            View view = layout.findViewById(R.id.view_main);
//
//            view.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            int_x = event.getRawX();
//                            int_y = event.getRawY();
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            if (Math.abs(int_y) - Math.abs(event.getRawY()) > 100.0) {
//                                dialog.dismiss();
//                            } else {
//                            }
//                            break;
//                        case MotionEvent.ACTION_CANCEL:
//
//                            break;
//                        case MotionEvent.ACTION_UP:
//
//                            break;
//                    }
//                    return false;
//                }
//            });
//
//
//            ImageView userAvatar = (ImageView) layout.findViewById(R.id.iv_user_avatar);
//            TextView pushTitleTv = (TextView) layout.findViewById(R.id.tv_push_title);
//            TextView mPushContent = (TextView) layout.findViewById(R.id.tv_push_content);
//            TextView createTime = (TextView) layout.findViewById(R.id.tv_time);
//            TextView tvRefuse = (TextView) layout.findViewById(R.id.tv_refuse);
//            TextView tvAgree = (TextView) layout.findViewById(R.id.tv_agree);
//
//            String pushTitle = "", pushContent = "";
//            if (pushMsgBean == null || StringUtils.equalsNull(pushMsgBean.getType())) {
//                ifShow = false;
//            } else {
//                final String content = pushMsgBean.getContent();
//                switch (pushMsgBean.getType()) {
//                    case PushUtils.PUSH_TYPE_JOIN_GROUP:
//                        tvRefuse.setVisibility(View.VISIBLE);
//                        tvAgree.setVisibility(View.VISIBLE);
//                        final PushJoinGroupBean pushJoinGroupBean = GsonUtils.jsonToObj(content, PushJoinGroupBean.class);
//                        if (pushJoinGroupBean != null) {
//                            pushTitleTv.setText(pushJoinGroupBean.getNickname() + context.getString(R.string.audit_join) + pushJoinGroupBean.getGroup_name());
//                            mPushContent.setText(context.getString(R.string.onechat_id) + pushJoinGroupBean.getAccount_name());
//                            createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//
//                            ImageUtils.displayCircleNetImage(context, pushJoinGroupBean.getAvatar_url(), userAvatar, R.drawable.default_group);
//
//                            tvRefuse.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    List<String> memberList = ImmutableList.of(
//                                            pushJoinGroupBean.getAccount_id()
//                                    );
//                                    String members_uid = GsonUtils.objToJson(memberList);
//                                    RequestUtils.GetAddGroupAuditRequest(pushJoinGroupBean.getAccount_id(), pushJoinGroupBean.getGroup_uid(), String.valueOf(false), new RequestSuccessListener<List<String>>() {
//                                        @Override
//                                        public void onResponse(List<String> result) {
//                                            if (result != null) {
//                                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                                dialog.dismiss();
//                                                ToastUtils.simpleToast(R.string.success);
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//                            tvAgree.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    List<String> memberList = ImmutableList.of(
//                                            pushJoinGroupBean.getAccount_id()
//                                    );
//                                    String members_uid = GsonUtils.objToJson(memberList);
//                                    RequestUtils.GetAddGroupAuditRequest(pushJoinGroupBean.getAccount_id(), pushJoinGroupBean.getGroup_uid(), String.valueOf(true), new RequestSuccessListener<List<String>>() {
//                                        @Override
//                                        public void onResponse(List<String> result) {
//                                            if (result != null) {
//                                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                                dialog.dismiss();
//                                                ToastUtils.simpleToast(R.string.success);
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                        break;
//
//                    case PushUtils.PUSH_TYPE_WEIBO_REWARD:
//                    case PushUtils.PUSH_TYPE_WEIBO_COMMENT:
//                    case PushUtils.PUSH_TYPE_WEIBO_PAY:
//
//                        tvRefuse.setVisibility(View.GONE);
//                        tvAgree.setVisibility(View.GONE);
//
//                        final PushWeiboMsgBean weiboMsgBean = GsonUtils.jsonToObj(content, PushWeiboMsgBean.class);
//                        if (weiboMsgBean != null) {
//                            switch (pushMsgBean.getType()) {
//                                case PushUtils.PUSH_TYPE_WEIBO_REWARD:
//                                    pushTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_reward_weibo);
//                                    pushContent = weiboMsgBean.getWeibo_content();
//                                    break;
//                                case PushUtils.PUSH_TYPE_WEIBO_COMMENT:
//                                    pushTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_comment_weibo);
//                                    pushContent = weiboMsgBean.getComment_content();
//                                    break;
//                                case PushUtils.PUSH_TYPE_WEIBO_PAY:
//                                    pushTitle = weiboMsgBean.getNickname() + context.getString(R.string.push_pay_weibo);
//                                    pushContent = weiboMsgBean.getWeibo_content();
//                                    break;
//                            }
//                            pushTitleTv.setText(pushTitle);
//                            mPushContent.setText(pushContent);
//                            createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                            ImageUtils.displayAvatarNetImage(context, weiboMsgBean.getAvatar_url(), userAvatar, null);
//
//                            view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    JumpAppPageUtil.jumpWeiboContentPage(context, weiboMsgBean.getWeibo_id(), weiboMsgBean.getWeibo_id(), "");
//                                    BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//                        break;
//                    case PushUtils.PUSH_TYPE_URL:
//
//                        tvRefuse.setVisibility(View.GONE);
//                        tvAgree.setVisibility(View.GONE);
//
//                        final PushUrlBean pushUrlBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), PushUrlBean.class);
//                        if (pushUrlBean != null) {
//                            pushTitleTv.setText(pushUrlBean.getTitle());
//                            mPushContent.setText(pushUrlBean.getMessage());
//                            createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                            ImageUtils.displayCircleNetImage(context, pushUrlBean.getImg_url(), userAvatar, R.drawable.share_icon);
//
//                            view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Utils.start_Activity(context,
//                                            SimpleWebViewActivity.class,
//                                            new BasicNameValuePair(oneapp.onechat.oneandroid.onemessage.Constants.URL, pushUrlBean.getWeb_url()));
//                                    BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//                        break;
//                    case PushUtils.PUSH_TYPE_ADD_USER:
//                        tvRefuse.setVisibility(View.VISIBLE);
//                        tvAgree.setVisibility(View.VISIBLE);
//                        final PushAddUserBean pushAddUserBean = GsonUtils.jsonToObj(content, PushAddUserBean.class);
//                        if (pushAddUserBean != null) {
//                            final String accountName = pushAddUserBean.getAccount_name();
//                            pushTitleTv.setText(pushAddUserBean.getNickname() + context.getString(R.string.apply_add_friend));
//                            mPushContent.setText(pushAddUserBean.getRemark());
//                            createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                            ImageUtils.displayAvatarNetImage(context, pushAddUserBean.getAvatar_url(), userAvatar, null);
//
//                            tvRefuse.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    RequestUtils.AgreeOrFixRemarkRequest(accountName, ServiceConstants.SERVICE_SAFE_STATUS_NO, null, new RequestSuccessListener<MapResult>() {
//                                        @Override
//                                        public void onResponse(MapResult result) {
//                                            if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                                dialog.dismiss();
//                                                ToastUtils.simpleToast(R.string.success);
//                                            } else {
//                                                ToastUtils.simpleToast(R.string.erro);
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//                            tvAgree.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    RequestUtils.AgreeOrFixRemarkRequest(accountName, ServiceConstants.SERVICE_SAFE_STATUS_YES, null, new RequestSuccessListener<MapResult>() {
//                                        @Override
//                                        public void onResponse(MapResult result) {
//                                            if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                                dialog.dismiss();
//                                                ToastUtils.simpleToast(R.string.success);
//                                                CommonUtils.agreeAddFriend((BaseActivity) context, accountName, false);
//                                            } else {
//                                                ToastUtils.simpleToast(R.string.erro);
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//
//                            mPushContent.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    DialogUtil.tipDialog(context, pushAddUserBean.getRemark(), true);
//                                }
//                            });
//
//                            view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    JumpAppPageUtil.jumpOtherUserInfoPageByName(context, accountName);
//                                }
//                            });
//                        }
//                        break;
//
//                    case PushUtils.PUSH_TYPE_ADD_GROUP:
//                        tvRefuse.setVisibility(View.VISIBLE);
//                        tvAgree.setVisibility(View.VISIBLE);
//                        final PushAddGroupBean pushAddGroupBean = GsonUtils.jsonToObj(content, PushAddGroupBean.class);
//                        if (pushAddGroupBean != null) {
//                            final String groupUid = pushAddGroupBean.getGroup_uid();
//                            pushTitleTv.setText(pushAddGroupBean.getGroup_name() + context.getString(R.string.invite_you_join_group));
//                            mPushContent.setText(pushAddGroupBean.getRemark());
//                            createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                            ImageUtils.displayCircleNetImage(context, pushAddGroupBean.getAvatar_url(), userAvatar, R.drawable.default_group);
//
//                            tvRefuse.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    RequestUtils.EnterIntoGroupRequest(groupUid, ServiceConstants.SERVICE_SAFE_STATUS_NO, new RequestSuccessListener<MapResult>() {
//                                        @Override
//                                        public void onResponse(MapResult result) {
//                                            if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                                dialog.dismiss();
//                                                ToastUtils.simpleToast(R.string.success);
//                                                CommonUtils.sendMessageToGroup(context, groupUid, String.format(context.getResources().getString(R.string.join_group_tip), UserInfoUtils.getUserInfo().getNickname()));
//                                            } else {
//                                                ToastUtils.simpleToast(R.string.erro);
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//                            tvAgree.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    RequestUtils.EnterIntoGroupRequest(groupUid, ServiceConstants.SERVICE_SAFE_STATUS_YES, new RequestSuccessListener<MapResult>() {
//                                        @Override
//                                        public void onResponse(MapResult result) {
//                                            if (RequestUtils.checkResultCode(result) || result.getCode() == ServiceConstants.REQUEST_CODE_HAS_DETAIL) {
//                                                BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                                dialog.dismiss();
//                                                ToastUtils.simpleToast(R.string.success);
//                                                CommonUtils.sendMessageToGroup(context, groupUid, String.format(context.getResources().getString(R.string.join_group_tip), UserInfoUtils.getUserInfo().getNickname()));
//                                            } else {
//                                                ToastUtils.simpleToast(R.string.erro);
//                                            }
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                        break;
//
//                    case PushUtils.PUSH_TYPE_ORDER_PUSH:
//                        tvRefuse.setVisibility(View.GONE);
//                        tvAgree.setVisibility(View.GONE);
//
//                        final OrderChangeBean orderChangeBean = GsonUtils.jsonToObj(pushMsgBean.getContent(), OrderChangeBean.class);
//                        if (orderChangeBean != null) {
//                            pushTitleTv.setText(context.getString(R.string.order_has_change));
//                            mPushContent.setText(context.getString(R.string.order_id) + orderChangeBean.getId());
//                            createTime.setText(TimeUtils.toSecondTimeString(pushMsgBean.getTimestamp()));
//                            ImageUtils.displayAvatarNetImage(context, UserInfoUtils.getUserAvatar(), userAvatar, null);
//
//                            view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    JumpAppPageUtil.jumpNativeWebView(context, ServiceConstants.GetMoneyGoAndOutUrlWithUid(orderChangeBean.getUni_uuid()), "", CommonConstants.H5_TYPE_SIMPLE);
//                                    BtsHelper.getDatabase().deletePushMsg(pushMsgBean);
//                                    dialog.dismiss();
//                                }
//                            });
//                        }
//                        break;
//                    default:
//                        ifShow = false;
//                        break;
//                }
//            }
//            if (ifShow) {
//                dialog.setCancelable(true);
//                dialog.setCanceledOnTouchOutside(true);
//
//                final Window dialogWindow = dialog.getWindow();
//                final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//                dialogWindow.setGravity(Gravity.TOP);
//                dialogWindow.setBackgroundDrawableResource(R.color.toumin);
//
//                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                lp.dimAmount = 0;
//                lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//
//                dialogWindow.setAttributes(lp);
//
//                dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                dialog.show();
//
//                // 初始化定时器
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                    }
//                }, 2500);
//
//            }
//        } catch (Exception e) {
//
//        }
//        return dialog;
//    }
//}
