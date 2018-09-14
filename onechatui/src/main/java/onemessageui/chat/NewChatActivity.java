package onemessageui.chat;

//
///**
// * Created by chunzhengwang on 2017/9/15.
// */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import javax.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.community.activity.WeiboListFragment;
import onemessageui.view.activity.GroupSettingActivity;
import onemessageui.widght.PagerSlidingTabStrip;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

public class NewChatActivity extends BaseActivity implements View.OnClickListener {
    public final String TAG = "UserTradeFragmentActivity";

    private static int NUM_OF_SCREENS = 2;

    // Screen ids
    private static final int CHAT_PAGE = 0;
    private static final int COMMUNITY_PAGE = 1;

    ViewPager viewPager;
    PagerSlidingTabStrip tabs;

    private static NewChatActivity m_pThis = null;

    private int chatType;
    private String toChatUsername, groupId, Name;

    private ImageView img_back;
    private TextView txt_title, txt_right;
    private ImageView img_right;
    private AppSectionsPagerAdapter pagerAdapter = null;

    private Unbinder unbind;

    private boolean ifInitView = false;

    private ChatFragment chatFragment;
    private WeiboListFragment weiboListFragment;
    private FinishBroadcastReceiver finishMessageStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ifOpenRightSlideBack = false;

        super.onCreate(savedInstanceState);

        finishMessageStatus = new FinishBroadcastReceiver();
        IntentFilter intentFilterMessageStatus = new IntentFilter(CommonHelperUtils.getFinishBroadcastAction());
        intentFilterMessageStatus.setPriority(5);
        registerReceiver(finishMessageStatus, intentFilterMessageStatus);

        m_pThis = this;
        onCreateView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        chatType = intent.getIntExtra(Constants.TYPE, Constants.CHATTYPE_SINGLE);
        Name = intent.getStringExtra(Constants.NAME);
        toChatUsername = intent.getStringExtra(Constants.User_ID);
        groupId = intent.getStringExtra(Constants.GROUP_ID);
        if (chatType == Constants.CHATTYPE_SINGLE) {
            finish();
            JumpAppPageUtil.jumpSingleChatPage(context, toChatUsername);
        } else {
            finish();
            JumpAppPageUtil.jumpGroupChatPage(context, groupId, Name);
        }
    }

    private void readArgument() {
        chatType = getIntent().getIntExtra(Constants.TYPE, Constants.CHATTYPE_SINGLE);
        Name = getIntent().getStringExtra(Constants.NAME);
        toChatUsername = getIntent().getStringExtra(Constants.User_ID);
        groupId = getIntent().getStringExtra(Constants.GROUP_ID);

    }

    private void findViewById() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_right = (TextView) findViewById(R.id.txt_right);
        img_right = (ImageView) findViewById(R.id.img_right);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.pager_tabs);
    }

    private void initViews() {
        // 设置消息页面为初始页面
        img_back.setVisibility(View.VISIBLE);
        txt_title.setText(Name);
        txt_right = (TextView) findViewById(R.id.txt_right);
        img_right = (ImageView) findViewById(R.id.img_right);

        if (chatType == Constants.CHATTYPE_SINGLE) {
            if (StringUtils.equalsNull(Name)) {
                UserContactItem userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(toChatUsername);
                if (userContactItem != null) {
                    Name = userContactItem.getUserName();
                    txt_title.setText(Name);
                }
            }

            tabs.setVisibility(View.GONE);
            NUM_OF_SCREENS = 1;

            img_right.setImageResource(R.drawable.group_setting_black_icon);
            img_right.setVisibility(View.GONE);
            txt_right.setVisibility(View.VISIBLE);
            txt_right.setText(R.string.switch_service_node);
            txt_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JumpAppPageUtil.jumpSetServiceNodePage(context);
                }
            });
        } else {
            UserGroupInfoItem mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId, false);
            if (mGroupInfo == null) {
                OneGroupHelper.GetItemGroupInfoRequest(groupId, new RequestSuccessListener<UserGroupInfoItem>() {
                    @Override
                    public void onResponse(UserGroupInfoItem userGroupInfoItem) {
                        if (isFinishing()) {
                            return;
                        }
                        if (userGroupInfoItem != null) {
                            UserGroupInfoItem groupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId, false);
                            String showGroupName = groupInfo.group_name;
                            if (showGroupName.length() > oneapp.onechat.oneandroid.onewallet.Constants.MAX_GROUP_NAME_LENGTH) {
                                showGroupName = showGroupName.substring(0, oneapp.onechat.oneandroid.onewallet.Constants.MAX_GROUP_NAME_LENGTH) + "...";
                            }
                            txt_title.setText(showGroupName + String.format(getString(R.string.format_bracket), groupInfo.getMembers_size() + ""));
                            if (groupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PRIVATE) {
                                tabs.setVisibility(View.GONE);
                                NUM_OF_SCREENS = 1;
                            } else {
                                tabs.setVisibility(View.VISIBLE);
                                NUM_OF_SCREENS = 2;
                            }
                        } else {
                            ToastUtils.simpleToast(R.string.you_are_group);
                            finish();
                        }
                    }
                });
            } else {
                String showGroupName = mGroupInfo.group_name;
                if (showGroupName.length() > oneapp.onechat.oneandroid.onewallet.Constants.MAX_GROUP_NAME_LENGTH) {
                    showGroupName = showGroupName.substring(0, oneapp.onechat.oneandroid.onewallet.Constants.MAX_GROUP_NAME_LENGTH) + "...";
                }
                txt_title.setText(showGroupName + String.format(getString(R.string.format_bracket), mGroupInfo.getMembers_size() + ""));

                if (mGroupInfo.public_status == CommonConstants.CHAT_GROUP_STATUS_PRIVATE) {
                    tabs.setVisibility(View.GONE);
                    NUM_OF_SCREENS = 1;
                } else {
                    tabs.setVisibility(View.VISIBLE);
                    NUM_OF_SCREENS = 2;
                }
            }

            img_right.setImageResource(R.drawable.group_setting_black_icon);
            img_right.setVisibility(View.VISIBLE);
            txt_right.setVisibility(View.GONE);
            img_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.start_Activity(context, GroupSettingActivity.class,
                            new BasicNameValuePair(Constants.GROUP_ID,
                                    groupId));
                }
            });
        }

        if (!ifInitView) {
            pagerAdapter = new AppSectionsPagerAdapter(m_pThis, m_pThis.getFM());
            viewPager.setAdapter(pagerAdapter);
            // Attach the view pager to the tab strip
            tabs.setViewPager(viewPager);
        } else {
            if (pagerAdapter != null) {
                pagerAdapter.notifyDataSetChanged();
            }
        }
        ifInitView = true;
//        if (tabs.getTextView(ORDERS) != null) {
//            Drawable drawableWeiHui = getResources().getDrawable(R.drawable.icon_pull_down);
//            drawableWeiHui.setBounds(0, 0, UiUtils.dip2px(this, 8),
//                    UiUtils.dip2px(this, 8));//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
//            tabs.getTextView(ORDERS).setCompoundDrawables(null, null, drawableWeiHui, null);
//        }
    }

    private void setOnListener() {
        img_back.setOnClickListener(this);
    }

    //    @Override
    public View onCreateView() {

        setContentView(R.layout.activity_new_chat);
        View view = this.findViewById(android.R.id.content);

        this.unbind = ButterKnife.bind(this, view);

        readArgument();
        findViewById();
//        initViews();
        setOnListener();

        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbind.unbind();
        unregisterReceiver(finishMessageStatus);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        // 点击notification bar进入聊天页面，保证只有一个聊天页面
//        String username = intent.getStringExtra("userId");
//        if (!StringUtils.equalsNull(toChatUsername) && toChatUsername.equals(username))
//            super.onNewIntent(intent);
//        else {
//            finish();
//            startActivity(intent);
//        }
//
//    }


    @Override
    public void onClick(View v) {
        try {
            int i = v.getId();
            if (i == R.id.img_back) {
                finish();

            } else {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private <T extends Fragment> T createFragment(int index) {
        switch (index) {
            case CHAT_PAGE:
                return (T) ChatFragment.newInstance(chatType, toChatUsername, groupId, Name);
            case COMMUNITY_PAGE:
                return (T) WeiboListFragment.newInstance(groupId);

            default:
                throw new RuntimeException("Cannot create fragment, unknown screen item: ");
        }
    }

    private class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        private String chatTitle, communityTitle;


        public AppSectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            chatTitle = context.getString(R.string.group_chat);
            communityTitle = context.getString(R.string.group_community);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case CHAT_PAGE:
                    if (chatFragment == null)
                        chatFragment = createFragment(i);
                    return chatFragment;
                case COMMUNITY_PAGE:
                    if (weiboListFragment == null)
                        weiboListFragment = createFragment(i);
                    return weiboListFragment;
                default:
                    throw new RuntimeException("Cannot get item, unknown screen item: " + i);
            }
        }


        @Override
        public int getCount() {
            return NUM_OF_SCREENS;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            switch (i) {
                case CHAT_PAGE:
                    return chatTitle;
                case COMMUNITY_PAGE:
                    return communityTitle;
                default:
                    throw new RuntimeException("Cannot get item, unknown screen item: " + i);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case oneapp.onechat.oneandroid.onewallet.Constants.REQUEST_CODE_SET_WEIBO_PAY:
            case oneapp.onechat.oneandroid.onewallet.Constants.REQUEST_CODE_PAY_WEIBO:
                if (weiboListFragment != null)
                    weiboListFragment.onParentActivityResult(requestCode, resultCode, data);
                break;
            default:
                if (chatFragment != null)
                    chatFragment.onParentActivityResult(requestCode, resultCode, data);
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    /**
     * 消息广播接收者
     */
    private class FinishBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String activity = intent.getStringExtra(JumpParamsContants.INTENT_ACTIVITY);
            finish();
        }
    }
}

