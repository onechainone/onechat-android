package onemessageui.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.graphenechain.database.HistoricalTransferEntry;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.interfaces.ViewDelegate;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.bean.ApplyNumResult;
import oneapp.onechat.oneandroid.onemessage.bean.ItemConversationListBean;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemConversation;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.common.CommonHelperUtils;
import oneapp.onechat.oneandroid.onewallet.modle.ListResult;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.GsonUtils;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import onemessageui.adpter.NewMsgAdpter;
import onemessageui.chat.NewChatActivity;
import onemessageui.dialog.TitleMenu.TitlePopup;
import onemessageui.mpush.PushUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

//消息
public class Fragment_Msg extends MainImmersionFragment implements OnClickListener,
        OnItemClickListener, ViewDelegate {
    public final String TAG = this.getClass().getName();

    private Activity ctx;
    private View layout;

    private TextView mTitleTv;
    private ImageView img_right;
    private TitlePopup titlePopup;

    private ImageView mContactsIv;

    private TextView mNewApplyTv;

    public RelativeLayout errorItem;
    public TextView errorText;
    private ListView lvContact;
    private NewMsgAdpter adpter;
    private List<ItemConversationListBean> conversationList = new ArrayList<>();
    private SmartRefreshLayout swipeRefreshLayout;

    List<String> account_Ids = new ArrayList<>();
    List<String> group_Ids = new ArrayList<>();
    List<String> new_account_Ids = new ArrayList<>();
    List<String> new_group_Ids = new ArrayList<>();

    private NewMessageBroadcastReceiver msgReceiver;
    private NewPushBroadcastReceiver pushReceiver;
    private ImageView img_select;
    //    private DrawerLayout msgDrawerLayout;
    private View mShowPushView, mLeftView, mMainView;
    private TextView mLeftPushNumTv, mMainPushNumTv, mClearPushTv;
    private ListView mPushLv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // 注册一个接收消息的BroadcastReceiver
            msgReceiver = new NewMessageBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(CommonHelperUtils.getNewMessageBroadcastAction());
            intentFilter.setPriority(3);

            this.getActivity().registerReceiver(msgReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            pushReceiver = new NewPushBroadcastReceiver();
            IntentFilter intentPushMessageStatus = new IntentFilter(CommonHelperUtils.getPushMessageBroadcastAction());
            intentPushMessageStatus.setPriority(5);
            getActivity().registerReceiver(pushReceiver, intentPushMessageStatus);
        } catch (Exception e) {

        }

    }

    @Override
    public void onDestroy() {
        // 注销消息处理句柄
        super.onDestroy();

        // 注销广播
        try {
            this.getActivity().unregisterReceiver(msgReceiver);
            msgReceiver = null;
            getActivity().unregisterReceiver(pushReceiver);
            pushReceiver = null;
        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (layout == null) {
        ctx = this.getActivity();
        layout = ctx.getLayoutInflater().inflate(R.layout.fragment_msg,
                null);

        try {
            initImmersionBar();
            mImmersionBar.titleBar(layout.findViewById(R.id.layout_title_view));
            mImmersionBar.init();

        } catch (Exception e) {
            e.printStackTrace();
        }


        mMainView = layout.findViewById(R.id.view_main);
        mLeftView = layout.findViewById(R.id.view_left_msg);

        mLeftPushNumTv = (TextView) layout.findViewById(R.id.notification_number);
        mMainPushNumTv = (TextView) layout.findViewById(R.id.tv_main_push_msg);
        mClearPushTv = (TextView) layout.findViewById(R.id.clean_group_notification);
        mPushLv = (ListView) layout.findViewById(R.id.notification_lv);

//        msgDrawerLayout = (DrawerLayout) layout.findViewById(R.id.msg_drawer);
        mShowPushView = layout.findViewById(R.id.view_push_msg);

        lvContact = (ListView) layout.findViewById(R.id.listview);
        lvContact.setEmptyView(layout.findViewById(R.id.txt_nochat));
        View listFooter = new View(getActivity());
        listFooter.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.page_bottom_temp));
        lvContact.addFooterView(listFooter);
        swipeRefreshLayout = (SmartRefreshLayout) layout.findViewById(R.id.swipeContainer);

        errorItem = (RelativeLayout) layout
                .findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem
                .findViewById(R.id.tv_connect_errormsg);

        mTitleTv = (TextView) layout.findViewById(R.id.txt_title);
        mTitleTv.setText(R.string.title_home);
        img_right = (ImageView) layout.findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.add_black_icon);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(this);
        img_select = (ImageView) layout.findViewById(R.id.img_select);
        img_select.setVisibility(View.VISIBLE);
        img_select.setOnClickListener(this);

        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(getActivity(), ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, TitlePopup.popFirstNumber);

        mContactsIv = (ImageView) layout.findViewById(R.id.img_back);
        mContactsIv.setImageResource(R.drawable.address_book_black_icon);

        mContactsIv.setVisibility(View.VISIBLE);

        mNewApplyTv = (TextView) layout.findViewById(R.id.tv_new_apply_num);

        setOnListener();
//        }
//        else {
//            ViewGroup parent = (ViewGroup) layout.getParent();
//            if (parent != null) {
//                parent.removeView(layout);
//            }
//        }


        return layout;
    }

    private void refreshAllGroupInfo() {
        List<String> groupIds = OneAccountHelper.getDatabase().getAllUserGroupIds(0);
        OneGroupHelper.GetGroupInfoListRequest(groupIds, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            refreshMessage(isHidden());
            updatePushMsgList();
            if (!isHidden()) {
                requestNewApplyNum();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler refreshGroupMsgHandler = new Handler();
    private int refreshTime = oneapp.onechat.oneandroid.onewallet.Constants.REFRESH_GROUP_MSG_TIME_MAIN;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startRequestGroupMsgHandler();
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshMessage(hidden);
        if (!hidden) {
            requestNewApplyNum();
        }
    }

    //获取新申请数量
    private void requestNewApplyNum() {
        OneAccountHelper.GetApplyNumRequest(new RequestSuccessListener<ApplyNumResult>() {
            @Override
            public void onResponse(ApplyNumResult applyNumResult) {
                if (applyNumResult != null) {

                    long num = applyNumResult.getGroup_count() + applyNumResult.getUser_count();
                    String unReadNumString = String.valueOf(num);
                    if (num > CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM) {
                        unReadNumString = CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM + getString(R.string.add_symbol);
                    }
                    if (num > 0) {
                        mNewApplyTv.setVisibility(View.VISIBLE);
                        mNewApplyTv.setText(unReadNumString);
                    } else {
                        mNewApplyTv.setVisibility(View.GONE);
                    }
                } else {
                    mNewApplyTv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void refreshMessage(boolean hidden) {

        if (!hidden) {
            refreshTime = oneapp.onechat.oneandroid.onewallet.Constants.REFRESH_GROUP_MSG_TIME_MAIN;
            startRequestGroupMsgHandler();
            BtsApplication.getInstance().shortWebsocketStart();
            refresh();
        } else {
            refreshTime = oneapp.onechat.oneandroid.onewallet.Constants.REFRESH_GROUP_MSG_TIME_MAIN_OTHER;
//            stopRequestGroupMsgHandler();
            refreshGroupMsgHandler.postDelayed(runnable, refreshTime);//每5秒执行一次runnable.
        }
    }

    void startRequestGroupMsgHandler() {
        requestGroupMsg();
        refreshGroupMsgHandler.postDelayed(runnable, refreshTime);//每5秒执行一次runnable.
    }

    void stopRequestGroupMsgHandler() {
        refreshGroupMsgHandler.removeCallbacks(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRequestGroupMsgHandler();
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //请求群消息
    void requestGroupMsg() {
//        RequestUtils.GetGroupMessageInfo(getContext(), null);
        OneGroupHelper.syncGroupChatInfo();

    }


    private void initViews() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.finishRefresh();
        }
        if (StringUtils.equalsNull(OneAccountHelper.getAccountId())) {
            return;
        }
        conversationList.clear();

        conversationList.addAll(loadConversations());

        if (adpter == null) {
            adpter = new NewMsgAdpter(getActivity(), conversationList);
            lvContact.setAdapter(adpter);
        } else {
            adpter.refresh();
        }

        if (conversationList.size() <= 0) {
            if (!StringUtils.equalsNull(OneAccountHelper.getAccountId())) {
                List<HistoricalTransferEntry> newData = OneAccountHelper.getDatabase().getUserChat(OneAccountHelper.getAccountId(),
                        CommonConstants.MAX_LOAD_MESSAGE_SIZE);
                OneAccountHelper.getDatabase().putUserChat(newData, true);
            }
        }
    }

    boolean ifRequestConversationInfo = false;

    /**
     * 获取所有会话
     *
     * @return +
     */
    private List<ItemConversationListBean> loadConversations() {

        List<ItemConversationListBean> list = OneChatHelper.getChatConversationList();
        if (!ifRequestConversationInfo) {
            for (ItemConversationListBean itemConversationListBean : list) {
                if (!itemConversationListBean.isGroup()) {
                    new_account_Ids.add(itemConversationListBean.getUsername());
                } else {
//                    new_group_Ids.icon_recive_red(itemConversationListBean.getNickname());
                }
            }
            getChatUserAndGroupInfo();
            refreshAllGroupInfo();
            ifRequestConversationInfo = true;
        }
        return list;
    }


    private void getChatUserAndGroupInfo() {
        if (!ifRequestConversationInfo) {
//        RequestUtils.GetGroupMessageInfo(getContext(), 0, null);
            OneChatHelper.GetOtherUserInfoListRequest(new_account_Ids, new RequestSuccessListener<ListResult<UserInfoBean>>() {
                @Override
                public void onResponse(ListResult<UserInfoBean> userInfoBeanListResult) {
                    initViews();
                }
            });

            OneGroupHelper.GetGroupInfoListRequest(new_group_Ids, new RequestSuccessListener<ListResult<UserGroupInfoItem>>() {
                @Override
                public void onResponse(ListResult<UserGroupInfoItem> userGroupInfoItemListResult) {
                    initViews();
                }
            });
        }
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(
            List<ItemConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<ItemConversation>() {
            @Override
            public int compare(final ItemConversation con1,
                               final ItemConversation con2) {

                ItemMessage con2LastMessage = con2.getLastMessage();
                ItemMessage con1LastMessage = con1.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage
                        .getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage
                        .getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    private void setOnListener() {
        lvContact.setOnItemClickListener(this);
        lvContact.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//                if (adpter != null) {
//                    adpter.clearDeleteMap();
//                }
            }
        });
        errorItem.setOnClickListener(this);
        mContactsIv.setOnClickListener(this);
        mShowPushView.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestGroupMsg();
                swipeRefreshLayout.finishRefresh(oneapp.onechat.oneandroid.onewallet.Constants.MAX_REFRESH_LOADING_TIME / 2);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (adpter.PublicMsg != null && position == 0) {
//			// 打开订阅号列表页面
//			ZanUtils.start_Activity(getActivity(), PublishMsgListActivity.class);
        } else {

            if (conversationList.size() <= position) {
                return;
            }
            ItemConversationListBean conversation = conversationList.get(position);

            Hashtable<String, String> ChatRecord = adpter.getChatRecord();
            if (ChatRecord != null) {

                // 未读消息置0
//                conversation.deleteUnreadMsgCountRecord();
                if (conversation.isGroup()) {

                    Intent intent = new Intent(getActivity(), NewChatActivity.class);
                    String groupUid = conversation.getUsername();
                    UserGroupInfoItem info = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupUid, false);
                    if (info != null) {
                        intent.putExtra(Constants.TYPE,
                                Constants.CHATTYPE_GROUP);
                        intent.putExtra(Constants.GROUP_ID, groupUid);
                        intent.putExtra(Constants.NAME, info.group_name);// 设置标题
                        getActivity().startActivity(intent);
                    } else {
                        intent.putExtra(Constants.TYPE,
                                Constants.CHATTYPE_GROUP);
                        intent.putExtra(Constants.GROUP_ID, groupUid);
                        intent.putExtra(Constants.NAME, R.string.group_chats);// 设置标题
                        getActivity().startActivity(intent);
                    }
                } else {
                    JumpAppPageUtil.jumpSingleChatPage(getActivity(), conversation.getUsername());
//                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                    String userId = conversation.getUsername();
//                    UserContactItem user = OneAccountHelper.getDatabase().getUserContactItemById(userId);
//                    if (user != null) {
//                        intent.putExtra(Constants.NAME, user.getUserName());// 设置昵称
//                        intent.putExtra(Constants.TYPE,
//                                Constants.CHATTYPE_SINGLE);
//                        intent.putExtra(Constants.User_ID,
//                                conversation.getUsername());
//                        getActivity().startActivity(intent);
//                    } else {
//                        intent.putExtra(Constants.NAME, "好友");
//                        intent.putExtra(Constants.TYPE,
//                                Constants.CHATTYPE_SINGLE);
//                        intent.putExtra(Constants.User_ID,
//                                conversation.getUsername());
//                        getActivity().startActivity(intent);
//                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            JumpAppPageUtil.jumpFriendListPage(getContext());

        } else if (i == R.id.img_select) {//TODO:跳转到群查询h5页面
            JumpAppPageUtil.jumpNativeWebView(getContext(), ServiceConstants.GetGroupSearchUrl().getHost_url(), "", CommonConstants.H5_TYPE_GROUP);

        } else if (i == R.id.img_right) {
            titlePopup.show(img_right);
        }
    }


    /**
     * Refreshes table data by assigning a new adapter.
     * This method should be called whenever there is fresh data in the transfers database table.
     *
     * @param reset: If true, the current transfer list is discarded, and a new query is made to the database.
     */
    private void updateTableView(boolean reset) {
        refresh();

//		UserAccount account = new UserAccount(accountId);
//
//		if (reset) {
//			loadMoreCounter = 1;
//		}
//
//		// Calculate how many items to fetch depending on how many times
//		// the 'load more' button has been pressed. Maybe later we can modify the
//		// getTransactions method to accept ranges and simplify this code.
//		int limit = SCWallDatabase.DEFAULT_TRANSACTION_BATCH_SIZE * loadMoreCounter;
//		List<HistoricalTransferEntry> newData = database.getTransactions(account, limit);
//
//		// Here we check if the SortableTableView has its default adapter or our own instance.
//		if (transfersView.getDataAdapter() instanceof TransfersTableAdapter && !reset) {
//			Log.d(TAG, "updating table view");
//			tableAdapter = (TransfersTableAdapter) transfersView.getDataAdapter();
//			List<HistoricalTransferEntry> existingData = tableAdapter.getData();
//			boolean found = true;
//			for (HistoricalTransferEntry newEntry : newData) {
//				for (HistoricalTransferEntry existingEntry : existingData) {
//					if (newEntry.getHistoricalTransfer().getId().equals(existingEntry.getHistoricalTransfer().getId())) {
//						found = true;
//						break;
//					}
//				}
//				if (!found) {
//					existingData.icon_recive_red(newEntry);
//				}
//				found = false;
//			}
//
//		} else {
//			tableAdapter = new TransfersTableAdapter(getContext(), account, newData.toArray(new HistoricalTransferEntry[newData.size()]));
//
//			transfersView.setDataAdapter(tableAdapter);
//		}
//		//Notifies the attached observers that the underlying data has been changed and any View
//		// reflecting the data set should refresh itself.
//		tableAdapter.notifyDataSetChanged();
//
//		if (transfersView.getColumnComparator(0) == null) {
//			updateSortTable();
//		}
    }

    private void updatePushMsgList() {
//        List<PushMsgBean> pushMsgBeans = OneAccountHelper.getDatabase().getPushMsgList(CommonConstants.MIAX_SHOW_UNREAD_MSG_NUM);
//        if (pushMsgBeans.size() > 0) {
//            mShowPushView.setVisibility(View.VISIBLE);
//        } else {
////            mShowPushView.setVisibility(View.GONE);
//        }
//        if (pushMsgBeans.size() > 0) {
//            mLeftPushNumTv.setVisibility(View.VISIBLE);
//            mMainPushNumTv.setVisibility(View.VISIBLE);
//        } else {
//            mLeftPushNumTv.setVisibility(View.GONE);
//            mMainPushNumTv.setVisibility(View.GONE);
//        }
//        mLeftPushNumTv.setText(String.valueOf(pushMsgBeans.size()));
//        mMainPushNumTv.setText(String.valueOf(pushMsgBeans.size()));
//
//        mClearPushTv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updatePushMsgList();
//            }
//        });

    }

    @Override
    public void updateView(Boolean bUpdate) {
        updateTableView(bUpdate);
    }


    /**
     * 新消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

                String from = intent.getStringExtra("from");
                // 消息id
                String msgIds = intent.getStringExtra(JumpParamsContants.INTENT_MSG_IDS);

                List<String> msgIdList = GsonUtils.jsonToObj(msgIds, new TypeToken<List<String>>() {
                }.getType());
//                List<HistoricalTransferEntry> historicalTransferEntries = OneAccountHelper.getDatabase().getUserChatMemoMessageByUuids(msgIdList);
                initViews();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 推送消息广播接收者
     */
    private class NewPushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String pushType = intent.getStringExtra(JumpParamsContants.INTENT_TYPE);

                if (!StringUtils.equalsNull(pushType)) {
                    switch (pushType) {
                        case PushUtils.PUSH_TYPE_ADD_USER:
                        case PushUtils.PUSH_TYPE_ADD_GROUP:
                            requestNewApplyNum();
                            break;
                    }
                }
                updatePushMsgList();

                String pushId = intent.getStringExtra(JumpParamsContants.INTENT_PUSH_ID);

                if (pushId != null && OneAccountHelper.getAppStatus() == CommonConstants.APP_STATUS_RUNING) {
                    boolean ifNotifyPush = true;
                    if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY)) {
                        ifNotifyPush = (boolean) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_NEW_PUSH_NOTIFY);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
