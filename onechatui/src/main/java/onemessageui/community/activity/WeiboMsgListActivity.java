package onemessageui.community.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboMsgBean;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.ListResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.community.adapter.WeiboMsgAdapter;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

public class WeiboMsgListActivity extends BaseActivity implements View.OnClickListener {

    private final String Tag = "WeiboListActivity";

    private TextView txt_title;
    private ImageView img_back;

    private View footView;

    private Context mContext;
    private int mPage = CommonConstants.DEFAULT_PAGE_START;

    private SmartRefreshLayout swipeRefreshLayout;
    private ListView mWeiboMsgLv;
    private WeiboMsgAdapter mAdapter;
    private List<WeiboMsgBean> mWeiboMsgList;
    private TextView mNoDataTv;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_msg);
        mContext = this;

        mWeiboMsgList = new ArrayList<>();
        initView();
        readArguments();
        initListener();
        showLoadingDialog("");
        initData(CommonConstants.WEIBO_NO_READ_MSG);

    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, String> paramMap = (HashMap<String, String>) sear;
            groupId = paramMap.get(JumpParamsContants.INTENT_GROUP_ID);
        }

    }

    private void initListener() {
        mWeiboMsgLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JumpAppPageUtil.jumpWeiboContentPage(mContext, mWeiboMsgList.get(position).getWeibo_id(), mWeiboMsgList.get(position).getWeibo_id(), Tag);

            }
        });

        swipeRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                initData(CommonConstants.WEIBO_NO_READ_MSG);
//                initData(CommonConstants.WEIBO_ALL_MSG);
                swipeRefreshLayout.finishLoadMore(Constants.MAX_REFRESH_LOADING_TIME);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = CommonConstants.DEFAULT_PAGE_START;
//                initData(CommonConstants.WEIBO_ALL_MSG);
                initData(CommonConstants.WEIBO_NO_READ_MSG);
                swipeRefreshLayout.finishRefresh(Constants.MAX_REFRESH_LOADING_TIME);
            }
        });
    }

    private void initData(String readType) {

        OneCommunityHelper.getWeiboMsgList(readType, groupId, mPage, new RequestSuccessListener<ListResult<WeiboMsgBean>>() {
            @Override
            public void onResponse(ListResult<WeiboMsgBean> weiboMsgListResult) {
                if (OneOpenHelper.ifListBeanHasNoNull(weiboMsgListResult)) {
                    if (mPage == CommonConstants.DEFAULT_PAGE_START) {
                        mWeiboMsgList.clear();
                    }

                    mWeiboMsgList.addAll(weiboMsgListResult.getData().getList());

                    mAdapter.notifyDataSetChanged();
                } else {
                    if (mPage > CommonConstants.DEFAULT_PAGE_START) {
                        ToastUtils.simpleToast(getString(R.string.no_next_page));
                    } else {
                        mWeiboMsgList.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.finishRefresh();
                    swipeRefreshLayout.finishLoadMore();
                }
                hideLoadingDialog();
            }
        });

    }

    /***
     * 载入视图
     */
    private void initView() {

        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.weibo_msg);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.swipeContainer);

        mWeiboMsgLv = (ListView) findViewById(R.id.weibo_msg_lv);

        mAdapter = new WeiboMsgAdapter(mContext, mWeiboMsgList);
        initFootView();
        //mWeiboMsgLv.getRefreshableView().addFooterView(footView);
        mWeiboMsgLv.setAdapter(mAdapter);
        mNoDataTv = (TextView) findViewById(R.id.txt_no_order);
        mWeiboMsgLv.setEmptyView(mNoDataTv);
    }

    private void initFootView() {
        footView = View.inflate(mContext, R.layout.foot_weibo_msg,
                null);
        TextView mMoreMsgTv = (TextView) footView.findViewById(R.id.tv_more_weibo_msg);
        mMoreMsgTv.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_more_weibo_msg) {//查看更多
            mWeiboMsgLv.removeFooterView(footView);
            initData(CommonConstants.WEIBO_ALL_MSG);

        } else {
        }
    }

}
