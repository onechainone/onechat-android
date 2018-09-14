package onemessageui.community.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.UnreadWeiboMsgResult;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboCatchModel;
import oneapp.onechat.oneandroid.onewallet.Constants;

import oneapp.onechat.oneandroid.onewallet.modle.ListResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import onemessageui.dialog.DialogUtil;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.community.adapter.WeiboListAdapter;

import onemessageui.dialog.PopupWindowUtil;
import onewalletui.ui.BaseActivity;
import onewalletui.ui.BaseFragment;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;

import static android.app.Activity.RESULT_OK;

public class WeiboListFragment extends BaseFragment implements View.OnClickListener {

    private final String Tag = "WeiboListActivity";
    private View layout;

    //未读微博消息数
    private int mNewMsgNum;
    private String mNewMsgImgurl;
    private ImageView mAvatar;
    private TextView mNewMsgNumTv;

    private String mGroupId;

    private BaseActivity activity;
    private Context mContext;
    private int mPage = CommonConstants.DEFAULT_PAGE_START;

    private SmartRefreshLayout swipeRefreshLayout;
    private View headerView, menuView;
    private View leftTypeView, rightTypeView;
    private ImageView mSendWeiboIv;
    private ListView mWeiboLv;
    private TextView mNoDataTv;
    private WeiboListAdapter mAdapter;
    private List<WeiboBean> mWeiboList;

    private TextView mSelectTypeTv, mSelectOrderTv;
    private int mSelectTypePos, mSelectOrderPos = 0;

    public static WeiboListFragment newInstance(String mGroupId) {
        WeiboListFragment fragment = new WeiboListFragment();
        Bundle args = new Bundle();
        if (!StringUtils.equalsNull(mGroupId)) {
            args.putSerializable(JumpParamsContants.INTENT_GROUP_ID, mGroupId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        layout = activity.getLayoutInflater().inflate(R.layout.fragment_weibo_list,
                null);
        mContext = activity;
        //注册EventBus
//        EventBus.getDefault().register(this);
        mWeiboList = new ArrayList<>();
        readArguments();
        initView();
        initListener();

//        initData();
        return layout;
    }

    private void readArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mGroupId = (String) bundle.getSerializable(JumpParamsContants.INTENT_GROUP_ID);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    //EventBus接受刷新会话列表消息
//    @Subscribe
//    public void onEvent(SendWeiboEvent event) {
//        mPage = CommonConstants.DEFAULT_PAGE_START;
//        initData();
//    }

    //EventBus接受刷新会话列表消息
//    @Subscribe
//    public void onEvent(DeleteWeiboEvent event) {
//        mPage = CommonConstants.DEFAULT_PAGE_START;
//        initData();
//    }


    private boolean ifInitData = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !ifInitData) {
            ifInitData = true;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.autoRefresh();
            }
            initData();
        }
    }

    private void initListener() {
        mWeiboLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - mWeiboLv.getHeaderViewsCount();
                if (position < 0) {
                    return;
                }
                WeiboBean sourceWeiboBean = mWeiboList.get(position);
                if (!StringUtils.equalsNull(mWeiboList.get(position).getType()) && mWeiboList.get(position).getType().equals(CommonConstants.WEIBO_TYPE_REPOST)) {
                    if (!StringUtils.equalsNull(mWeiboList.get(position).getYuan_weibo().getId())) {
                        sourceWeiboBean = mWeiboList.get(position).getYuan_weibo();
                    }
                }

                String user_is_pay = WeiboCatchModel.getUserIsPayMap().get(sourceWeiboBean.getId());
                if (user_is_pay == null) {
                    user_is_pay = sourceWeiboBean.getUser_is_pay();
                }

                if (StringUtils.equals(sourceWeiboBean.getIs_pay(), CommonConstants.NO_VALUE) || StringUtils.equals(user_is_pay, CommonConstants.TRUE_VALUE)) {

                    JumpAppPageUtil.jumpWeiboContentPage(mContext, mWeiboList.get(position).getId(), sourceWeiboBean.getId(), Tag);
                } else {
                    JumpAppPageUtil.jumpPayWeiboPage(activity, sourceWeiboBean.getId(), sourceWeiboBean.getAsset_code(), sourceWeiboBean.getReward_price(), mGroupId);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                initData();
                swipeRefreshLayout.finishLoadMore(Constants.MAX_REFRESH_LOADING_TIME);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = CommonConstants.DEFAULT_PAGE_START;
                initData();
                swipeRefreshLayout.finishRefresh(Constants.MAX_REFRESH_LOADING_TIME);
            }
        });

        leftTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindowUtil.showSelectMenuPopup(activity, mSelectTypePos, CommonConstants.WEIBO_TYPE_LIST, PopupWindowUtil.MENU_SHOW_TYPE_LEFT, leftTypeView, new DialogUtil.ConfirmCallBackObject<Integer>() {
                    @Override
                    public void onConfirmClick(Integer integer) {
                        mSelectTypePos = integer;
                        mSelectTypeTv.setText(CommonConstants.WEIBO_TYPE_LIST.get(mSelectTypePos).getTypeResId());
                        mPage = CommonConstants.DEFAULT_PAGE_START;
                        initData();
                    }
                });
            }
        });

        rightTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindowUtil.showSelectMenuPopup(activity, mSelectOrderPos, CommonConstants.WEIBO_ORDER_LIST, PopupWindowUtil.MENU_SHOW_TYPE_RIGHT, rightTypeView, new DialogUtil.ConfirmCallBackObject<Integer>() {
                    @Override
                    public void onConfirmClick(Integer integer) {
                        mSelectOrderPos = integer;
                        mSelectOrderTv.setText(CommonConstants.WEIBO_ORDER_LIST.get(mSelectOrderPos).getTypeResId());
                        mPage = CommonConstants.DEFAULT_PAGE_START;
                        initData();
                    }
                });
            }
        });

        //为ListView设置触摸事件和滚动事件，这是核心
//        mWeiboLv.setOnTouchListener(onTouchListener);
    }

    private void initData() {

        OneCommunityHelper.getArticleList(mGroupId, mSelectTypePos, mSelectOrderPos, mPage, new RequestSuccessListener<ListResult<WeiboBean>>() {
            @Override
            public void onResponse(ListResult<WeiboBean> weiboListResult) {
                List<WeiboBean> mTempList = null;

                if (mPage == CommonConstants.DEFAULT_PAGE_START) {
                    mWeiboList.clear();
                    WeiboCatchModel.clearZanMap();
                }

                if (OneOpenHelper.ifListBeanHasNoNull(weiboListResult)) {
                    mTempList = weiboListResult.getData().getList();
                }

                if (mTempList == null || mTempList.size() == 0) {
                    if (mPage > CommonConstants.DEFAULT_PAGE_START) {
//                        ToastUtils.simpleToast(getString(R.string.no_next_page));
                    }
                } else {
                    mWeiboList.addAll(mTempList);
                }
                mAdapter.notifyDataSetChanged();

                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.finishRefresh();
                    swipeRefreshLayout.finishLoadMore();
                }
            }
        });

        OneCommunityHelper.getUnreadMsgCount(mGroupId, new RequestSuccessListener<UnreadWeiboMsgResult>() {
            @Override
            public void onResponse(final UnreadWeiboMsgResult unreadWeiboMsgResult) {
                if (unreadWeiboMsgResult != null) {

                    mNewMsgNum = unreadWeiboMsgResult.getNoread_times();
                    mNewMsgImgurl = unreadWeiboMsgResult.getAvatar_url();
                    String sex = unreadWeiboMsgResult.getSex();
                    if (mNewMsgNum > 0) {
                        if (mWeiboLv.getHeaderViewsCount() < 1)
                            mWeiboLv.addHeaderView(headerView);
                        mNewMsgNumTv.setText(String.format(getString(R.string.new_chat_message_num), mNewMsgNum));
                        ImageUtils.displayAvatarNetImage(mContext, mNewMsgImgurl, mAvatar, sex);
                    } else {
                        mWeiboLv.removeHeaderView(headerView);
                    }
                } else {
                    mWeiboLv.removeHeaderView(headerView);
                }
            }
        });
    }

    /***
     * 载入视图
     */
    private void initView() {
        mSendWeiboIv = (ImageView) layout.findViewById(R.id.iv_send_weibo);
        mSendWeiboIv.setOnClickListener(this);
        swipeRefreshLayout = (SmartRefreshLayout) layout.findViewById(R.id.swipeContainer);
        mWeiboLv = (ListView) layout.findViewById(R.id.weibo_lv);

        mAdapter = new WeiboListAdapter(mContext, mWeiboList, mGroupId);
        mNoDataTv = (TextView) layout.findViewById(R.id.txt_no_order);
        mWeiboLv.setAdapter(mAdapter);
        mWeiboLv.setEmptyView(mNoDataTv);
        mSelectTypeTv = (TextView) layout.findViewById(R.id.tv_select_type);
        mSelectOrderTv = (TextView) layout.findViewById(R.id.tv_select_order);
        menuView = layout.findViewById(R.id.menu_view);

        mSelectTypeTv.setText(CommonConstants.WEIBO_TYPE_LIST.get(mSelectTypePos).getTypeResId());
        mSelectOrderTv.setText(CommonConstants.WEIBO_ORDER_LIST.get(mSelectOrderPos).getTypeResId());

        initHeadView();
        leftTypeView = layout.findViewById(R.id.view_select_type);
        rightTypeView = layout.findViewById(R.id.view_select_order);
    }

    private void initHeadView() {
        headerView = View.inflate(mContext, R.layout.head_weibo_msg,
                null);
        LinearLayout mWeiboMsgLl = (LinearLayout) headerView.findViewById(R.id.ll_weibo_msg);
        mWeiboMsgLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpAppPageUtil.jumpWeiboMsg(mContext, Tag, mGroupId);
                mWeiboLv.removeHeaderView(headerView);
            }
        });

        mAvatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        mNewMsgNumTv = (TextView) headerView.findViewById(R.id.tv_weibo_msg_num);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_send_weibo) {
            JumpAppPageUtil.jumpSendWeiboPage(activity, mGroupId);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (WeiboCatchModel.IS_UPDATA_ZAN) {
        mAdapter.notifyDataSetChanged();
//            WeiboCatchModel.IS_UPDATA_ZAN = false;
//        }
    }

    AnimatorSet backAnimatorSet;//这是显示头尾元素使用的动画

    private void animateBack() {
        //先清除其他动画
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            hideAnimatorSet.cancel();
        }
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            backAnimatorSet = new AnimatorSet();
            //下面两句是将头尾元素放回初始位置。
            ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(mSendWeiboIv, "translationY", mSendWeiboIv.getTranslationY(), 0f);
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(footerAnimator);
            backAnimatorSet.setDuration(350);
            backAnimatorSet.playTogether(animators);
            backAnimatorSet.start();
        }
    }

    AnimatorSet hideAnimatorSet;//这是隐藏头尾元素使用的动画

    private void animateHide() {
        //先清除其他动画
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            backAnimatorSet.cancel();
        }
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            hideAnimatorSet = new AnimatorSet();
            ObjectAnimator footerAnimator = ObjectAnimator.ofFloat(mSendWeiboIv, "translationY", mSendWeiboIv.getTranslationY(), UiUtils.dip2px(mContext, 70));//将Button隐藏到下面
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(footerAnimator);
            hideAnimatorSet.setDuration(350);
            hideAnimatorSet.playTogether(animators);
            hideAnimatorSet.start();
        }
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {


        float lastY = 0f;
        float currentY = 0f;
        //下面两个表示滑动的方向，大于0表示向下滑动，小于0表示向上滑动，等于0表示未滑动
        int lastDirection = 0;
        int currentDirection = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = event.getY();
                    currentY = event.getY();
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
                case MotionEvent.ACTION_MOVE:

                    float tmpCurrentY = event.getY();
                    //if (Math.abs(tmpCurrentY - lastY) > 1) {//滑动距离大于touchslop时才进行判断
                    currentY = tmpCurrentY;
                    currentDirection = (int) (currentY - lastY);
                    if (lastDirection != currentDirection) {
                        //如果与上次方向不同，则执行显/隐动画
                        if (currentDirection < 0) {
                            animateHide();
                        } else {
                            animateBack();
                        }
                    }
                    //}

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //手指抬起的时候要把currentDirection设置为0，这样下次不管向哪拉，都与当前的不同（其实在ACTION_DOWN里写了之后这里就用不着了……）
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
            }
            return false;
        }
    };

    public void onParentActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_SET_WEIBO_PAY:
                if (resultCode == RESULT_OK) {
                    mPage = CommonConstants.DEFAULT_PAGE_START;
                    initData();
                }
                break;
            case Constants.REQUEST_CODE_PAY_WEIBO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String weiboId = data.getStringExtra(JumpParamsContants.INTENT_WEIBO_ID);
                        JumpAppPageUtil.jumpWeiboContentPage(activity, weiboId, weiboId, weiboId);
                    }
                }
                break;
        }

    }
}
