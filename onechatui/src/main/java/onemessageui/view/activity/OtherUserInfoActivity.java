package onemessageui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneChatHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import oneapp.onecore.graphenej.models.AssetBalance;
import oneapp.onecore.graphenej.models.WitnessResponse;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;


public class OtherUserInfoActivity extends OneBaseActivity implements View.OnClickListener {

    private final String TAG = "OtherUserInfoActivity";

    UserContactItem userContactItem;

    private String mUserAccountId = "", mUserAccountName = "";
    private TextView mTitleTv;
    private ImageView mBackIv;

    private Context mContext;

    private TextView tvname, tv_accout, mIntroTv, mInvitationCodeTv;
    private ImageView mUserheadIv, mUserSexIv;

    private TextView mSendMsgTv;

    private View mChangeLocalNameView;
    private TextView mLocalNameTv;
    private TextView mClearMsgTv;

    private TextView mGoodAssetTv, mBadAssetTv;

    private TextView mAddFriendTv;

    private TextView mDeleteFriendTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void start() {
        mContext = this;
        setContentView(R.layout.activity_other_user_info);
        readArguments();
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, String> paramMap = (HashMap<String, String>) sear;
            this.mUserAccountId = paramMap.get(JumpParamsContants.INTENT_USER_ACCOUNT_ID);
            this.mUserAccountName = paramMap.get(JumpParamsContants.INTENT_ACCOUNT_NAME);
        }
    }

    @Override
    protected void initControl() {

    }

    /***
     * 载入视图
     */
    protected void initView() {
        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv = (TextView) findViewById(R.id.txt_title);
        tvname = (TextView) findViewById(R.id.tvname);
        tv_accout = (TextView) findViewById(R.id.tvmsg);
        mIntroTv = (TextView) findViewById(R.id.tv_intro);
        mInvitationCodeTv = (TextView) findViewById(R.id.tv_invitation_code);
        mUserheadIv = (ImageView) findViewById(R.id.iv_user_head);
        mUserSexIv = (ImageView) findViewById(R.id.iv_user_sex);
        mSendMsgTv = (TextView) findViewById(R.id.tv_send_msg);
        mChangeLocalNameView = findViewById(R.id.view_change_local_name);
        mLocalNameTv = (TextView) findViewById(R.id.txt_local_name);
        mClearMsgTv = (TextView) findViewById(R.id.tv_clear_message);
        mGoodAssetTv = (TextView) findViewById(R.id.tv_good_asset);
        mBadAssetTv = (TextView) findViewById(R.id.tv_bad_asset);
        mAddFriendTv = (TextView) findViewById(R.id.tv_add_friend);
        mDeleteFriendTv = (TextView) findViewById(R.id.tv_delete_friend);

        mTitleTv.setText(getString(R.string.user_info));
    }

    @Override
    protected void initData() {

        updateView();

        if (!StringUtils.equalsNull(mUserAccountId)) {
            OneChatHelper.RequestUserInfoById(mUserAccountId, new RequestSuccessListener<UserInfoBean>() {
                @Override
                public void onResponse(UserInfoBean userInfoBeanMapResult) {
                    bindUserInfoBean(userInfoBeanMapResult);
                }
            });
        } else {
            OneChatHelper.RequestUserInfoByName(mUserAccountName, new RequestSuccessListener<UserInfoBean>() {
                @Override
                public void onResponse(UserInfoBean userInfoBeanMapResult) {
                    bindUserInfoBean(userInfoBeanMapResult);
                }
            });
        }

    }

    void bindUserInfoBean(UserInfoBean userInfoBeanMapResult) {

        if (userInfoBeanMapResult == null) {
            ToastUtils.simpleToast(R.string.account_name_not_exist);
            finish();
            return;
        }

        updateView();

        if (!StringUtils.equalsNull(userInfoBeanMapResult.getIntro()))
            mIntroTv.setText(userInfoBeanMapResult.getIntro());
        else
            mIntroTv.setText(getString(R.string.default_user_intro));


        ImageUtils.displayAvatarNetImage(context, userInfoBeanMapResult.getAvatar_url(), mUserheadIv, userInfoBeanMapResult.getSex());

        if (!StringUtils.equalsNull(userInfoBeanMapResult.getSex()))
            switch (userInfoBeanMapResult.getSex())

            {
                case UserInfoUtils.USER_SEX_MAN:
                    mUserSexIv.setImageResource(R.drawable.sex_man_icon);
                    mUserSexIv.setVisibility(View.VISIBLE);
                    break;
                case UserInfoUtils.USER_SEX_WOMAN:
                    mUserSexIv.setImageResource(R.drawable.sex_women_icon);
                    mUserSexIv.setVisibility(View.VISIBLE);
                    break;
                default:
                    mUserSexIv.setVisibility(View.GONE);
                    break;
            }

        getOtherUserAccountBalance(userInfoBeanMapResult.getAccount_name());

    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateView();
    }

    void updateView() {
        if (!StringUtils.equalsNull(mUserAccountId)) {
            userContactItem = OneAccountHelper.getDatabase().getUserContactItemById(mUserAccountId);
        } else {
            userContactItem = OneAccountHelper.getDatabase().getUserContactItemByName(mUserAccountName);
        }
        if (userContactItem != null) {
            mUserAccountId = userContactItem.getId();
            mUserAccountName = userContactItem.getAccountName();
            tv_accout.setText(getString(R.string.onechat_id) + mUserAccountName);
            tvname.setText(userContactItem.getNickname());
            mLocalNameTv.setText(userContactItem.getUserName());
            ImageUtils.displayAvatarNetImage(context, userContactItem.getAvatar(), mUserheadIv, userContactItem.getSex());
            if (userContactItem.getStatusFriend() == UserContactItem.StatusFriend.FRIEND.ordinal()) {
                mSendMsgTv.setVisibility(View.VISIBLE);
                mDeleteFriendTv.setVisibility(View.VISIBLE);
                mAddFriendTv.setVisibility(View.GONE);
            } else {
                mSendMsgTv.setVisibility(View.GONE);
                mDeleteFriendTv.setVisibility(View.GONE);
                mAddFriendTv.setVisibility(View.VISIBLE);
            }

            mInvitationCodeTv.setText(getString(R.string.invitation_code) + OneAccountHelper.getInviteCodeById(mUserAccountId));
        }

    }

    @Override
    protected void setListener() {
        mBackIv.setOnClickListener(this);
        mSendMsgTv.setOnClickListener(this);
        mChangeLocalNameView.setOnClickListener(this);
        mClearMsgTv.setOnClickListener(this);
        mAddFriendTv.setOnClickListener(this);
        mDeleteFriendTv.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        int i = v.getId();
        if (i == R.id.iv_user_head) {//点击头像

        } else if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.tv_send_msg) {
            if (userContactItem == null) {
                return;
            }
            //进入聊天页面
            JumpAppPageUtil.jumpSingleChatPage(context, mUserAccountId);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);

        } else if (i == R.id.view_change_local_name) {
            if (userContactItem == null) {
                return;
            }
            //修改备注
            DialogUtil.editNameDialog(context, getString(R.string.change_user_local_name), userContactItem.getUserName(), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    OneAccountHelper.changeFriendRemark(userContactItem.getAccountName(), content, new RequestSuccessListener<MapResult>() {
                        @Override
                        public void onResponse(MapResult result) {
                            if (result != null) {
                                ToastUtils.simpleToast(R.string.success);
                            } else {
                                ToastUtils.simpleToast(R.string.erro);
                            }
                        }
                    });
                    userContactItem.setLocalname(content);
                    OneAccountHelper.getDatabase().userContactInsertOrUpdate(userContactItem);
                    updateView();
                }
            });

        } else if (i == R.id.tv_clear_message) {
            DialogUtil.simpleDialog(context, getString(R.string.clear_mesage_tip), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    OneAccountHelper.getDatabase().deleteMessageByToid(mUserAccountId);
                    ToastUtils.simpleToast(R.string.clear_mesage_success);
                }
            });

        } else if (i == R.id.tv_add_friend) {
            if (userContactItem == null) {
                return;
            }
            //申请好友
            DialogUtil.editInputDialog(this, getString(R.string.input_apply_msg), getString(R.string.i_am) + UserInfoUtils.getUserNick(), null, new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String message) {
                    showLoadingDialog();
                    OneAccountHelper.addFriend(userContactItem.getAccountName(), message,
                            new RequestSuccessListener<MapResult>() {
                                @Override
                                public void onResponse(MapResult mapResult) {
                                    hideLoadingDialog();
                                    if (OneOpenHelper.checkResultCode(mapResult)) {
                                        ToastUtils.simpleToast(R.string.send_add_success);
                                        mAddFriendTv.setVisibility(View.GONE);

                                    } else {
                                        ToastUtils.simpleToast(R.string.erro);
                                    }
                                }
                            });
                }
            });


        } else if (i == R.id.tv_delete_friend) {
            if (userContactItem == null) {
                return;
            }
            //删除好友
            DialogUtil.simpleDialog(this, getString(R.string.make_sure_delete_tip), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    showLoadingDialog();
                    OneAccountHelper.deleteFriend(userContactItem.getAccountName(), new RequestSuccessListener<MapResult>() {
                        @Override
                        public void onResponse(MapResult mapResult) {
                            hideLoadingDialog();
                            if (mapResult != null) {
                                userContactItem.setStatusFriend(UserContactItem.StatusFriend.GUEST.ordinal());
                                OneAccountHelper.getDatabase().userContactInsertOrUpdate(userContactItem);
                                ToastUtils.simpleToast(R.string.delete_success);
                                updateView();
                            } else {
                                ToastUtils.simpleToast(R.string.delete_failed);
                            }
                        }
                    });
                }
            });


        }
    }

    /**
     * Retrieves the id for the newly created account.
     *
     * @param accountName: Account name
     */
    private void getOtherUserAccountBalance(final String accountName) {
        OneAccountHelper.getOtherUserAccountBalance(accountName, new RequestSuccessListener<WitnessResponse>() {
            @Override
            public void onResponse(WitnessResponse response) {
                try {
                    if (response == null || response.result == null) {
                        return;
                    }

                    final ArrayList<AssetBalance> tempList = (ArrayList<AssetBalance>) response.result;

                    OtherUserInfoActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AssetBalance tempTemp = OneAccountHelper.getAssetBalance(tempList, CommonConstants.ONEGOOD_ASSET_ID);
                            if (tempTemp != null) {
                                AssetInfo goodAsset = AssetInfoUtils.getAssetInfoBySymbol(CommonConstants.ONEGOOD_ASSET_SYMBOL);
                                long goodAssetBalance = 0;
                                if (goodAsset != null) {
                                    goodAssetBalance = (long) OneAccountHelper.powerInDouble(goodAsset.getPrecision() + "", tempTemp.amount) * CommonConstants.ONE_GOOD_BAD_FIX;
                                }
                                mGoodAssetTv.setText(String.valueOf(goodAssetBalance));
                            }

                            tempTemp = OneAccountHelper.getAssetBalance(tempList, CommonConstants.ONEBAD_ASSET_ID);
                            if (tempTemp != null) {
                                AssetInfo badAsset = AssetInfoUtils.getAssetInfoBySymbol(CommonConstants.ONEBAD_ASSET_SYMBOL);
                                long badAssetBalance = 0;
                                if (badAsset != null) {
                                    badAssetBalance = (long) OneAccountHelper.powerInDouble(badAsset.getPrecision() + "", tempTemp.amount) * CommonConstants.ONE_GOOD_BAD_FIX;
                                }
                                mBadAssetTv.setText(String.valueOf(badAssetBalance));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}


