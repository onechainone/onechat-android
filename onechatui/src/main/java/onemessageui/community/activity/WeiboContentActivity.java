package onemessageui.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.community.bean.CommentBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboCatchModel;
import oneapp.onechat.oneandroid.onemessage.community.bean.ZanResult;
import oneapp.onechat.oneandroid.onemessage.community.bean.ZanshangBean;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.KeyBoardUtils;
import oneapp.onechat.oneandroid.onewallet.util.Keyboard;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.community.adapter.WeiboCommentAdapter;
import onemessageui.community.adapter.WeiboImgAdapter;
import onemessageui.community.adapter.WeiboZanAdapter;
import onemessageui.dialog.DialogUtil;
import onemessageui.widght.GridViewForScrollView;
import onemessageui.widght.ListViewForScrollView;
import onewalletui.ui.BaseActivity;
import onewalletui.util.ImageUtils;
import onewalletui.util.UiUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;

public class WeiboContentActivity extends BaseActivity implements View.OnClickListener {

    private final String Tag = "WeiboContentActivity";

    private TextView txt_title;
    private ImageView img_back, img_right;

    private String mSourceWeiboId;//原微博Id
    private String mWeiboId;//该微博Id

    private Context mContext;
    private String mWeiboType;
    //用户类型
    private String mUserType;

    private String mZanType, mCaiType;

    //视频
    private FrameLayout mVideoFl;
    private ImageView mVideoIv;
    private TextView mVideoTipTv;

    private TextView mVideoTimeTv;

    //原微博
    private WeiboBean mSourceWeiboBean;

    private EditText mReplyEt;
    //赏
    private ImageView mShangTv;
    //发送按钮
    private ImageView mSeandReplyTv;
    private ImageView mAvatar;
    private TextView mContentTv;
    private TextView mNameTv;
    private TextView mJianjieTv;
    private TextView mTimeTv;

    //评论数，赞赏数
    private TextView mCommentNumTv;
    private TextView mShangNumTv;
    private Drawable bottomLine;

    //点赞
    private TextView mZanNumTv;
    private ImageView mZanIv;
    //点踩
    private TextView mCaiNumTv;
    private ImageView mCaiIv;

    private GridViewForScrollView mImgGv;
    private ListViewForScrollView mReplyLv;

    private WeiboCommentAdapter mCommentAdapter;
    private List<CommentBean> mCommentList;
    //赞赏列表
    private List<ZanshangBean> mZhanshanglist;
    private WeiboZanAdapter mZanAdapter;

    //其它视频列表
    private RecyclerView mOtherVideoRv;
    private List<WeiboBean> mOtherVideoList;
    private LinearLayoutManager layoutManager;
    //    private VideoRecyclerAdapter mOtherVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_content);
        mContext = this;
        //注册EventBus
//        EventBus.getDefault().register(this);
        readArguments();
        initView();
        //bindWeiboDetail();
        initData();
    }


    private void initData() {
        showLoadingDialog("");
        OneCommunityHelper.getArticleDetail( mSourceWeiboId, new RequestSuccessListener<WeiboBean>() {
            @Override
            public void onResponse(WeiboBean weiboDetailBeanResult) {
                hideLoadingDialog();
                if (weiboDetailBeanResult != null) {
                    mSourceWeiboBean = weiboDetailBeanResult;
                    bindWeiboDetail();
                    mCommentAdapter.setGroupId(mSourceWeiboBean.getGroup_uid());

                    OneCommunityHelper.getArticleRewardList(mSourceWeiboId, mSourceWeiboBean.getGroup_uid(), new RequestSuccessListener<List<ZanshangBean>>() {
                        @Override
                        public void onResponse(List<ZanshangBean> zanshangBeanList) {
                            hideLoadingDialog();
                            if (zanshangBeanList != null) {
                                mZhanshanglist.clear();
                                mZhanshanglist.addAll(zanshangBeanList);
                                mZanAdapter.notifyDataSetChanged();
                                mShangNumTv.setText(getString(R.string.zanshang) + " " + zanshangBeanList.size());
                            } else {
                            }

                        }
                    });

                    OneCommunityHelper.getCommentListFromArticle(mSourceWeiboId, mSourceWeiboBean.getGroup_uid(), new RequestSuccessListener<List<CommentBean>>() {
                        @Override
                        public void onResponse(List<CommentBean> commentBeanList) {
                            hideLoadingDialog();
                            if (commentBeanList != null) {
                                mCommentList.clear();
                                mCommentList.addAll(commentBeanList);
                                mCommentAdapter.notifyDataSetChanged();
                                mCommentNumTv.setText(getString(R.string.comment) + " " + commentBeanList.size());
                            } else {
                            }
                        }
                    });
                } else {
                    ToastUtils.simpleToast(R.string.no_message);
                    finish();
                }

            }
        });


    }

    //获取其他视频
    private void initOtherVideo() {
//        if (mOtherVideoList == null) {
//            showLoadingDialog("");
//            RequestUtils.GetOtherVideoRequest(this, mSourceWeiboBean.getUid(), new RequestSuccessListener<Result<ListBean<WeiboBean>>>() {
//                @Override
//                public void onResponse(Result<ListBean<WeiboBean>> videoListBean) {
//                    mOtherVideoList = videoListBean.getPayload().getList();
//                    //倒序
////                    Collections.reverse(mOtherVideoList);
//                    mOtherVideoAdapter = new VideoRecyclerAdapter(mContext, mOtherVideoList);
//                    mOtherVideoRv.setAdapter(mOtherVideoAdapter);
//                    mOtherVideoRv.setVisibility(View.VISIBLE);
//                    mOtherVideoAdapter.setSelect(mSourceWeiboId);
//                    mOtherVideoAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            if (!mSourceWeiboId.equals(mOtherVideoList.get(position).getId())) {
//                                mSourceWeiboId = mOtherVideoList.get(position).getId();
//                                mWeiboId = mSourceWeiboId;
//                                initData();
//                                mCommentNumTv.performClick();
//                                mOtherVideoAdapter.setSelect(mSourceWeiboId);
//                            }
//                        }
//                    });
//                }
//            });
//        }
    }


    //显示微博详情
    private void bindWeiboDetail() {

        if (!StringUtils.equalsNull(mSourceWeiboBean.getVideo_duration()))
            mVideoTimeTv.setText(TimeUtils.getVideoTime(mSourceWeiboBean.getVideo_duration()));

        if (!StringUtils.equalsNull(mSourceWeiboBean.getContent_desc())) {
            mVideoTipTv.setText(mSourceWeiboBean.getContent_desc());
            mVideoTipTv.setVisibility(View.VISIBLE);
        } else {
            mVideoTipTv.setText("");
            mVideoTipTv.setVisibility(View.GONE);
        }


        //只有视频有打赏数
//        if (!WeiboListAdapter.WEIBO_TYPE_VIDEO.equals(mSourceWeiboBean.getType())) {
//            mShangNumTv.setVisibility(View.GONE);
//        } else {
//            mShangNumTv.setVisibility(View.VISIBLE);
//        }

        mWeiboType = mSourceWeiboBean.getType();
        mUserType = mSourceWeiboBean.getUser_type();

        //判断是否赞过
        bindLikeStatus();


        //只有视频类型才出现打赏
        if (!StringUtils.equalsNull(mWeiboType) && mWeiboType.equals(CommonConstants.WEIBO_TYPE_VIDEO)) {

            initOtherVideo();
            mVideoFl.setVisibility(View.VISIBLE);
            //mShangTv.setVisibility(View.VISIBLE);

//            if (mSourceWeiboBean.getAccount_name().equals(UserModel.getHxId())) {
//                mShangTv.setVisibility(View.GONE);
//            } else if ((boolean) SPUtils.get(mContext, SPUtils.SP_IF_FIRST_SHANG, true))
//                DialogUtil.shangTipDialog(mContext);

            ImageUtils.displaySimpleNetImage(mContext, mSourceWeiboBean.getVideo_jietu_url(), mVideoIv);
            //ImageLoader.getInstance().displayImage(mSourceWeiboBean.getVideo_jietu_url(), mVideoIv, ImageLoaderHelper.getInstance().getSimpleDisplayImageOptions());

        } else {
            mVideoFl.setVisibility(View.GONE);
            //mShangTv.setVisibility(View.GONE);
        }


        if (!StringUtils.equalsNull(mSourceWeiboBean.getBest_type()) && mSourceWeiboBean.getBest_type().equals(CommonConstants.TRUE_VALUE)) {
            mShangNumTv.setVisibility(View.GONE);
            mShangTv.setVisibility(View.GONE);
        } else {
            mShangNumTv.setVisibility(View.VISIBLE);
            mShangTv.setVisibility(View.VISIBLE);
        }

        //自己无法给自己打赏
        if (!StringUtils.equalsNull(mSourceWeiboBean.getAccount_name()) && mSourceWeiboBean.getAccount_name().equals(OneAccountHelper.getMeAccountName())) {
            mShangTv.setVisibility(View.GONE);
        } else {
            if (SharePreferenceUtils.contains(mContext, SharePreferenceUtils.SP_IF_FIRST_SHANG))
                DialogUtil.shangTipDialog(mContext);
            mShangTv.setVisibility(View.VISIBLE);
        }

        ImageUtils.displayAvatarNetImage(mContext, mSourceWeiboBean.getAvatar_url(), mAvatar, mSourceWeiboBean.getSex());
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.equalsNull(mSourceWeiboBean.getAccount_name())) {
                    if (!mSourceWeiboBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                        JumpAppPageUtil.jumpOtherUserInfoPage(mContext, mSourceWeiboBean.getAccount_name());
                }
            }
        });
        mTimeTv.setText(TimeUtils.toSecondTimeString(mSourceWeiboBean.getCreate_time()));
//        mCommentNumTv.setText(mSourceWeiboBean.getVolume());
//        mShangNumTv.setText(mSourceWeiboBean.getReward_count());
//        if (StringUtils.equalsNull(mSourceWeiboBean.getReward_count()))
//            mShangTextTv.setVisibility(View.GONE);
        mNameTv.setText(mSourceWeiboBean.getNickname());

        if (StringUtils.equalsNull(mSourceWeiboBean.getIntroduce())) {
            mJianjieTv.setVisibility(View.GONE);
        } else {
            mJianjieTv.setText(mSourceWeiboBean.getIntroduce());
        }

        if (WeiboCatchModel.getJinghuaWeiboMap().containsKey(mSourceWeiboId) || StringUtils.equals(mSourceWeiboBean.getWeibo_jinghua(), CommonConstants.TRUE_VALUE)) {
            UiUtils.addGoodWeiboIcon(this, mContentTv, mSourceWeiboBean.getContent());
        } else {
            mContentTv.setText(mSourceWeiboBean.getContent());
        }

        if (mSourceWeiboBean.getImg_list() != null) {
            mImgGv.setVisibility(View.VISIBLE);
            WeiboImgAdapter mImgAdapter = new WeiboImgAdapter(mContext, mSourceWeiboBean.getImg_list());
            mImgGv.setAdapter(mImgAdapter);
        } else {
            mImgGv.setVisibility(View.GONE);
        }
        mImgGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JumpAppPageUtil.jumpImagePage(mContext, "", position, mSourceWeiboBean.getImg_list_max());
            }
        });

        mReplyEt.setOnClickListener(this);
        mReplyEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //点击发送
                    showLoadingDialog("");
                    OneCommunityHelper.commentArticle(mReplyEt.getText().toString(), mWeiboId, mSourceWeiboBean.getAccount_name(), mSourceWeiboBean.getGroup_uid(), new RequestSuccessListener<MapResult>() {
                        @Override
                        public void onResponse(MapResult listBeanResult) {
                            initData();
                            mReplyEt.setText("");
//                            mCommentList.add(1,new CommentBean());
//                            mCommentAdapter.notifyDataSetChanged();
//                            EventBus.getDefault().post(new SendWeiboEvent());
//                            finish();
                        }
                    });
                    return true;
                }
                return false;

            }
        });
    }

    private void bindLikeStatus() {
        //判断是否赞过
        switch (mSourceWeiboBean.getIs_like()) {
            case CommonConstants.WEIBO_ZAN_TYPE_NO:
                mZanType = CommonConstants.WEIBO_ZAN;
                mCaiType = CommonConstants.WEIBO_ZAN;
                mZanIv.setImageResource(R.drawable.icon_go_up_gray);
                mZanNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
                mCaiIv.setImageResource(R.drawable.icon_go_down_gray);
                mCaiNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
                break;
            case CommonConstants.WEIBO_ZAN_TYPE_LIKE:
                mZanType = CommonConstants.WEIBO_CANCLE_ZAN;
                mCaiType = CommonConstants.WEIBO_ZAN;
                mZanIv.setImageResource(R.drawable.icon_go_up_ok);
                mZanNumTv.setTextColor(getResources().getColor(R.color.zan_num_red));
                mCaiIv.setImageResource(R.drawable.icon_go_down_gray);
                mCaiNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
                break;
            case CommonConstants.WEIBO_ZAN_TYPE_DISLIKE:
                mZanType = CommonConstants.WEIBO_ZAN;
                mCaiType = CommonConstants.WEIBO_CANCLE_ZAN;
                mZanIv.setImageResource(R.drawable.icon_go_up_gray);
                mZanNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
                mCaiIv.setImageResource(R.drawable.icon_go_down_ok);
                mCaiNumTv.setTextColor(getResources().getColor(R.color.zan_num_red));
                break;
        }

        mZanNumTv.setText(mSourceWeiboBean.getLikes_count());
        mCaiNumTv.setText(mSourceWeiboBean.getDislikes_count());

        if (mZanNumTv.getText().equals("0"))
            mZanNumTv.setText("");
        if (mCaiNumTv.getText().equals("0"))
            mCaiNumTv.setText("");

    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, String> paramMap = (HashMap<String, String>) sear;

            mWeiboId = paramMap.get(JumpParamsContants.WEIBO_ID);
            mSourceWeiboId = paramMap.get(JumpParamsContants.SOURCE_WEIBO_ID);
        }

    }

    /***
     * 载入视图
     */
    private void initView() {

        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.article_detail);
        img_right = (ImageView) findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.weibo_more_icon);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSourceWeiboBean == null) {
                    return;
                }
                DialogUtil.weiboMoreDialog(mContext, mWeiboId, mSourceWeiboId, mSourceWeiboBean, true, mSourceWeiboBean.getGroup_uid(), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String callBackType) {
                        if (StringUtils.equals(callBackType, DialogUtil.CALL_BACK_TYPE_DELETE)) {
                            finish();
                        } else {
                            bindWeiboDetail();
                        }
//                            KeyBoardUtils.TimerHideKeyboard(mReplyEt);
                    }
                });
            }
        });
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mReplyEt = (EditText) findViewById(R.id.reply_et);
        mReplyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSourceWeiboBean == null) {
                    return;
                }
                if (StringUtils.equalsNull(mSourceWeiboBean.getBest_type()) || !mSourceWeiboBean.getBest_type().equals(CommonConstants.TRUE_VALUE)) {

                    if (!StringUtils.equalsNull(s.toString())) {
                        mSeandReplyTv.setVisibility(View.VISIBLE);
                        mShangTv.setVisibility(View.GONE);
                    } else {
                        mSeandReplyTv.setVisibility(View.GONE);
                        if (mSourceWeiboBean != null && !StringUtils.equalsNull(mSourceWeiboBean.getAccount_name()) && !mSourceWeiboBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()))
                            mShangTv.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mShangTv = (ImageView) findViewById(R.id.tv_shang);
        mShangTv.setOnClickListener(this);

        mSeandReplyTv = (ImageView) findViewById(R.id.tv_send);
        mSeandReplyTv.setOnClickListener(this);

        bottomLine = getResources().getDrawable(R.drawable.bottom_line);
        bottomLine.setBounds(0, 0, bottomLine.getMinimumWidth(), bottomLine.getMinimumHeight());
        mCommentNumTv = (TextView) findViewById(R.id.tv_comment_num);
        mShangNumTv = (TextView) findViewById(R.id.tv_shang_num);

        mCommentNumTv.setOnClickListener(this);
        mShangNumTv.setOnClickListener(this);

        mZanNumTv = (TextView) findViewById(R.id.tv_zan_num);
        mZanIv = (ImageView) findViewById(R.id.iv_zan);
        mZanIv.setOnClickListener(this);
        mCaiNumTv = (TextView) findViewById(R.id.tv_cai_num);
        mCaiIv = (ImageView) findViewById(R.id.iv_cai);
        mCaiIv.setOnClickListener(this);

        mAvatar = (ImageView) findViewById(R.id.iv_user_avatar);
        mImgGv = (GridViewForScrollView) findViewById(R.id.gv_image);
        mContentTv = (TextView) findViewById(R.id.tv_weibo_content);
        mNameTv = (TextView) findViewById(R.id.tv_user_name);
        mJianjieTv = (TextView) findViewById(R.id.tv_user_jianjie);

        mTimeTv = (TextView) findViewById(R.id.tv_weibo_time);
        mReplyLv = (ListViewForScrollView) findViewById(R.id.lv_reply_list);
        mReplyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //if (mReplyLv.getAdapter().)
                DialogUtil.weiboReplyDetailDialog(WeiboContentActivity.this, mSourceWeiboBean.getGroup_uid(), mSourceWeiboId, mCommentList.get(position), mSourceWeiboBean.getAccount_name(), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        switch (content) {
                            case DialogUtil.REPLY:
                                DialogUtil.replyWeiboDialog((BaseActivity) mContext, mCommentList.get(position), mSourceWeiboBean.getId(), mWeiboId, mSourceWeiboBean.getGroup_uid(), new DialogUtil.ConfirmCallBackInf() {
                                    @Override
                                    public void onConfirmClick(String content) {

                                        if (!StringUtils.equalsNull(content))
                                            initData();

                                        Keyboard.hideKeyboard(context);
                                    }
                                });
                                break;
                            case DialogUtil.DELETE:
                                initData();
                                break;
                        }
                    }
                });
            }
        });
        mCommentList = new ArrayList<>();
        mCommentAdapter = new WeiboCommentAdapter(mContext, mCommentList);

        mZhanshanglist = new ArrayList<>();
        mZanAdapter = new WeiboZanAdapter(mContext, mZhanshanglist);

        mReplyLv.setAdapter(mCommentAdapter);

        mCommentAdapter.setReplyListener(new WeiboCommentAdapter.ReplyListener() {
            @Override
            public void onResponse(CommentBean commentBean) {
                DialogUtil.replyWeiboDialog((BaseActivity) mContext, commentBean, mSourceWeiboBean.getId(), mWeiboId, mSourceWeiboBean.getGroup_uid(), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {

                        if (!StringUtils.equalsNull(content))
                            initData();

                        KeyBoardUtils.TimerHideKeyboard(mReplyEt);
                    }
                });
            }
        });

        mVideoFl = (FrameLayout) findViewById(R.id.fl_video);
        mVideoFl.setOnClickListener(this);
        mVideoIv = (ImageView) findViewById(R.id.iv_video);
        mVideoTimeTv = (TextView) findViewById(R.id.tv_video_time);
        mVideoTipTv = (TextView) findViewById(R.id.tv_video_tip);
        setListView();

        mOtherVideoRv = (RecyclerView) findViewById(R.id.rv_other_video);
        mOtherVideoRv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mOtherVideoRv.setLayoutManager(layoutManager);
    }

    public void setListView() {
        /**
         * 设置ListView布局显示的属性
         */
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UiUtils.dip2px(mContext, 52));

        TextView emptyTv = (TextView) findViewById(R.id.empty);
        mReplyLv.setEmptyView(emptyTv);// 设置清空以后显示的内容

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_shang) {
            JumpAppPageUtil.jumpPayWeiboPage(this, mSourceWeiboBean.getId(), "", "", mSourceWeiboBean.getGroup_uid());
//                //赏
//                PopupWindowUtil.showShangWindow(this, mContext, v, new DialogUtil.ConfirmCallBackInf() {
//                    @Override
//                    public void onConfirmClick(final String money) {
//                        RequestUtils.RepostPayWeiboRequest(WeiboContentActivity.this, mSourceWeiboBean.getId(), money, new RequestSuccessListener<MapResult>() {
//                            @Override
//                            public void onResponse(MapResult mapResult) {
//                                ToastUtils.shangToast("X" + money);
//                                initData();
//                            }
//                        });
//                    }
//                });

        } else if (i == R.id.tv_send) {//发送评论
            //点击发送
            showLoadingDialog();
            OneCommunityHelper.commentArticle(mReplyEt.getText().toString(), mWeiboId, mSourceWeiboBean.getAccount_name(), mSourceWeiboBean.getGroup_uid(), new RequestSuccessListener<MapResult>() {
                @Override
                public void onResponse(MapResult listBeanResult) {
                    initData();
                    mReplyEt.setText("");
//                            mCommentList.add(1,new CommentBean());
//                            mCommentAdapter.notifyDataSetChanged();
//                            EventBus.getDefault().post(new SendWeiboEvent());
//                            finish();
                }
            });

        } else if (i == R.id.reply_et) {//必须付费才能评论
//                if (!StringUtils.equalsNull(mSourceWeiboBean.getVideo_bofang_url())) {
//                    if (!StringUtils.equalsNull(mSourceWeiboBean.getBest_type()) && mSourceWeiboBean.getBest_type().equals(CommonConstants.TRUE_VALUE) && !StringUtils.equalsNull(mSourceWeiboBean.getIs_pay()) && !mSourceWeiboBean.getIs_pay().equals(CommonConstants.TRUE_VALUE)) {
//                        DialogUtil.payGoodVideoDialog(mContext, mSourceWeiboBean.getReward_price(), new DialogUtil.ConfirmCallBackInf() {
//                            @Override
//                            public void onConfirmClick(String content) {
//                                RequestUtils.RepostPayWeiboRequest(WeiboContentActivity.this, mSourceWeiboId, mSourceWeiboBean.getReward_price(), new RequestSuccessListener<MapResult>() {
//                                    @Override
//                                    public void onResponse(MapResult listBeanResult) {
//                                        ToastUtils.shangToast("X" + mSourceWeiboBean.getReward_price());
//                                        initData();
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }


        } else if (i == R.id.fl_video) {//播放视频
            if (!StringUtils.equalsNull(mSourceWeiboBean.getVideo_bofang_url())) {
                JumpAppPageUtil.jumpVideoPlayPage(mContext, mSourceWeiboBean.getVideo_bofang_url(), Tag);
//                    if (!StringUtils.equalsNull(mSourceWeiboBean.getBest_type()) && mSourceWeiboBean.getBest_type().equals(CommonConstants.TRUE_VALUE) && !StringUtils.equalsNull(mSourceWeiboBean.getIs_pay()) && !mSourceWeiboBean.getIs_pay().equals(CommonConstants.TRUE_VALUE)) {
//                        DialogUtil.payGoodVideoDialog(mContext, mSourceWeiboBean.getReward_price(), new DialogUtil.ConfirmCallBackInf() {
//                            @Override
//                            public void onConfirmClick(String content) {
//                                RequestUtils.RepostPayWeiboRequest(WeiboContentActivity.this, mSourceWeiboId, mSourceWeiboBean.getReward_price(), new RequestSuccessListener<MapResult>() {
//                                    @Override
//                                    public void onResponse(MapResult listBeanResult) {
//                                        ToastUtils.shangToast("X" + mSourceWeiboBean.getReward_price());
//                                        initData();
//                                    }
//                                });
//                            }
//                        });
//                    } else
//                        RequestUtils.GetVideoStatusRequest(WeiboContentActivity.this, mSourceWeiboId, new RequestSuccessListener<MapResult>() {
//                            @Override
//                            public void onResponse(MapResult mapResult) {
//                                JumpAppPageUtil.jumpVideoPlayPage(mContext, mSourceWeiboBean.getVideo_bofang_url(), Tag);
//                            }
//                        });
            }

//                else
//                    PageJumpAppInUtil.jumpYoukuVideoPlayPage(mContext, mSourceWeiboBean.getVid(), Tag);


        } else if (i == R.id.iv_zan) {
            UiUtils.changeZanBig(mZanIv, new UiUtils.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {

                    OneCommunityHelper.likeOrDislikeArticle(mSourceWeiboBean.getId(), CommonConstants.WEIBO_ZAN_TYPE_LIKE, mZanType, mSourceWeiboBean.getGroup_uid(), new RequestSuccessListener<ZanResult>() {
                        @Override
                        public void onResponse(ZanResult zanResult) {
                            if (zanResult != null) {

                                WeiboCatchModel.getZanMap().put(mSourceWeiboBean.getId(), zanResult);
                                mSourceWeiboBean.setIs_like(zanResult.getIs_like());
                                mSourceWeiboBean.setLikes_count(zanResult.getLikes_count());
                                mSourceWeiboBean.setDislikes_count(zanResult.getDislikes_count());
                                //判断是否赞过
                                bindLikeStatus();
                            }
                        }
                    });
                }
            });

        } else if (i == R.id.iv_cai) {
            UiUtils.changeZanBig(mZanIv, new UiUtils.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {

                    OneCommunityHelper.likeOrDislikeArticle(mSourceWeiboBean.getId(), CommonConstants.WEIBO_ZAN_TYPE_DISLIKE, mCaiType, mSourceWeiboBean.getGroup_uid(), new RequestSuccessListener<ZanResult>() {
                        @Override
                        public void onResponse(ZanResult zanResult) {
                            if (zanResult != null) {

                                WeiboCatchModel.getZanMap().put(mSourceWeiboBean.getId(), zanResult);
                                mSourceWeiboBean.setIs_like(zanResult.getIs_like());
                                mSourceWeiboBean.setLikes_count(zanResult.getLikes_count());
                                mSourceWeiboBean.setDislikes_count(zanResult.getDislikes_count());
                                //判断是否赞过
                                bindLikeStatus();
                            }
                        }
                    });
                }
            });

        } else if (i == R.id.tv_comment_num) {//看评论列表
            mCommentNumTv.setCompoundDrawables(null, null, null, bottomLine);
            mCommentNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));

            mShangNumTv.setCompoundDrawables(null, null, null, null);
            mShangNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
            mReplyLv.setAdapter(mCommentAdapter);

        } else if (i == R.id.tv_shang_num) {//看赞赏列表
            mShangNumTv.setCompoundDrawables(null, null, null, bottomLine);
            mShangNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
            mCommentNumTv.setTextColor(getResources().getColor(R.color.base_text_gray));
            mCommentNumTv.setCompoundDrawables(null, null, null, null);

            mReplyLv.setAdapter(mZanAdapter);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_PAY_WEIBO:
                if (resultCode == RESULT_OK) {
                    initData();
                }
                break;
        }
    }

}
