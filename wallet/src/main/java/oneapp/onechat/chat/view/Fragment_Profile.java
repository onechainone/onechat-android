package oneapp.onechat.chat.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.chat.view.TitleMenu.TitlePopup;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.AccountDetails;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsApplication;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import onemessageui.view.fragment.MainImmersionFragment;
import onewalletui.util.ImageUtils;


//我
public class Fragment_Profile extends MainImmersionFragment implements OnClickListener {
    private Activity ctx;

    private boolean ifLoadView = false;
    private View layout, mTitleView, mTempView;
    private TextView mTitleTv;
    private ImageView img_right;
    private TitlePopup titlePopup;

    private View mUserInfoView;
    private TextView tvname, tv_accout, mIntroTv, mInvitationCodeTv;
    private ImageView mUserheadIv, mUserSexIv;

    private TextView mGoodAssetTv, mBadAssetTv, mChatAssetTv;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private LinearLayout mViewServiceNode;
    private LinearLayout mViewVideo;
    private View mUnreadAnnouncementView;
    private View mSwitchEth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_profile_new,
                    null);
            initViews();
            initData();
            updateView();

            setOnListener();
            try {
                IF_BAR_DARK_FONT = false;
                initImmersionBar();
                mImmersionBar.titleBar(layout.findViewById(R.id.layout_title_view));
                mImmersionBar.init();
            } catch (Exception e) {

            }
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		ctx = this.getActivity();
//		layout = ctx.getLayoutInflater().inflate(R.layout.fragment_profile,
//				null);
//		//XXX初始化view的各控件
//		isPrepared = true;
//		lazyLoad();
//		return layout;
//	}

    private void initViews() {
        mTitleView = layout.findViewById(R.id.layout_bar);
        mTitleView.setBackgroundResource(R.color.toumin);
        mTempView = layout.findViewById(R.id.temp_view);
        mTempView.setBackgroundResource(R.color.toumin);

        mTitleTv = (TextView) layout.findViewById(R.id.txt_title);
        mTitleTv.setText("");
        img_right = (ImageView) layout.findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.icon_add_white);
        img_right.setVisibility(View.GONE);
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(getActivity(), ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
//        tvname = (TextView) layout.findViewById(R.id.tvname);
//        tv_accout = (TextView) layout.findViewById(R.id.tvmsg);
//        String id = ZanUtils.getValue(getActivity(), Constants.User_ID);
//        tv_accout.setText(getString(R.string.wechat_id) + "：" + id);
//        if (GloableParams.UserInfos != null) {
//            String name = UserUtils.getUserName(ctx);
//            if (name != null && !TextUtils.isEmpty(name))
//                tvname.setText(name);
//        }

        tvname = (TextView) layout.findViewById(R.id.tvname);
        tv_accout = (TextView) layout.findViewById(R.id.tv_id);
        mIntroTv = (TextView) layout.findViewById(R.id.tv_intro);
        mInvitationCodeTv = (TextView) layout.findViewById(R.id.tv_invitation_code);
        mUserheadIv = (ImageView) layout.findViewById(R.id.iv_user_head);
        mUserInfoView = layout.findViewById(R.id.view_user_info);
        mUserSexIv = (ImageView) layout.findViewById(R.id.iv_user_sex);
        mGoodAssetTv = (TextView) layout.findViewById(R.id.tv_good_asset);
        mBadAssetTv = (TextView) layout.findViewById(R.id.tv_bad_asset);
        mChatAssetTv = (TextView) layout.findViewById(R.id.tv_message_asset);
        mViewServiceNode = (LinearLayout) layout.findViewById(R.id.view_service_node);
        mViewVideo = (LinearLayout) layout.findViewById(R.id.view_video);
        mSwitchEth = layout.findViewById(R.id.txt_switch_eth);
        mUnreadAnnouncementView = layout.findViewById(R.id.iv_unread_announcement);
        ifLoadView = true;
    }

    private void setOnListener() {
        layout.findViewById(R.id.ll_transaction_account).setOnClickListener(this);
        layout.findViewById(R.id.ll_withdraw).setOnClickListener(this);
        layout.findViewById(R.id.ll_recharge).setOnClickListener(this);
        layout.findViewById(R.id.ll_transfer).setOnClickListener(this);
        layout.findViewById(R.id.txt_backup_seed).setOnClickListener(this);
        layout.findViewById(R.id.txt_restore_wallet).setOnClickListener(this);
        layout.findViewById(R.id.txt_exchange_rates).setOnClickListener(this);
        layout.findViewById(R.id.txt_transaction_fees).setOnClickListener(this);
        layout.findViewById(R.id.txt_change_lock_psw).setOnClickListener(this);
        layout.findViewById(R.id.txt_setting).setOnClickListener(this);
        layout.findViewById(R.id.iv_my_qr_code).setOnClickListener(this);
        layout.findViewById(R.id.txt_red_packet_account).setOnClickListener(this);
        layout.findViewById(R.id.txt_social_red_packet).setOnClickListener(this);
        layout.findViewById(R.id.txt_transfer_fee).setOnClickListener(this);
        layout.findViewById(R.id.view_more).setOnClickListener(this);
        layout.findViewById(R.id.view_dapp).setOnClickListener(this);
        layout.findViewById(R.id.txt_danbao_trade).setOnClickListener(this);
        layout.findViewById(R.id.view_mining).setOnClickListener(this);
        layout.findViewById(R.id.view_test_node).setOnClickListener(this);
        layout.findViewById(R.id.view_newton_plan).setOnClickListener(this);
        layout.findViewById(R.id.view_task).setOnClickListener(this);

//        layout.findViewById(R.id.view_face).setOnClickListener(this);

        layout.findViewById(R.id.view_trade_dividend).setOnClickListener(this);
        layout.findViewById(R.id.view_trade_mining).setOnClickListener(this);
        layout.findViewById(R.id.view_announcement).setOnClickListener(this);

        layout.findViewById(R.id.view_news).setOnClickListener(this);
        layout.findViewById(R.id.view_quotation).setOnClickListener(this);
        layout.findViewById(R.id.view_news_flash).setOnClickListener(this);
        img_right.setOnClickListener(this);
        //新加入功能控件
        layout.findViewById(R.id.view_ensure).setOnClickListener(this);
        mViewVideo.setOnClickListener(this);
        mUserInfoView.setOnClickListener(this);
        mViewServiceNode.setOnClickListener(this);
        mSwitchEth.setOnClickListener(this);
    }

    private void initData() {
        // TODO Auto-generated method stub
        OneAccountHelper.RequestMyInfo(new RequestSuccessListener<UserInfoBean>() {
            @Override
            public void onResponse(UserInfoBean userInfoBeanMapResult) {
                updateView();

                String mobile = UserInfoUtils.getUserInfo().getMobile();

                if (!StringUtils.equalsNull(UserInfoUtils.getUserInfo().getAccount_name()) && StringUtils.equalsNull(mobile)) {
                    if (!SharePreferenceUtils.contains(SharePreferenceUtils.SP_IF_TIP_SET_PHONE)) {
                        SharePreferenceUtils.putObject(SharePreferenceUtils.SP_IF_TIP_SET_PHONE, true);
//                        DialogUtil.simpleDialog(getActivity(), getString(R.string.no_regist_phone), getString(R.string.now_bangding),
//                                getString(R.string.button_cancel), new DialogUtil.ConfirmCallBackInf() {
//                                    @Override
//                                    public void onConfirmClick(String content) {
//                                        JumpAppPageUtil.jumpBindPhonePage(getContext());
//                                    }
//                                });
                    }
                }

            }
        });

        mUnreadAnnouncementView.setVisibility(View.GONE);
    }

    public void updateView() {
        if (!ifLoadView)
            return;
        AccountDetails tempRet = BtsHelper.getDefaultAccount();
        if (tempRet != null && tempRet.AccountAssets == null) {
            BtsApplication.getInstance().updataBalance();
        }
        String accountName = BtsHelper.mMeAccountName;

        tv_accout.setText(getString(R.string.onechat_id) + accountName);

        if (!StringUtils.equalsNull(UserInfoUtils.getUserInfo().getIntro()))
            mIntroTv.setText(UserInfoUtils.getUserInfo().getIntro());
        else
            mIntroTv.setText(getString(R.string.default_user_intro));

        tvname.setText(UserInfoUtils.getUserInfo().getNickname());
        ImageUtils.displayAvatarNetImage(getContext(), UserInfoUtils.getUserAvatar(), mUserheadIv, UserInfoUtils.getUserInfo().getSex());

        if (!StringUtils.equalsNull(UserInfoUtils.getUserInfo().getSex()))
            switch (UserInfoUtils.getUserInfo().getSex()) {
                case UserInfoUtils.USER_SEX_MAN:
                    mUserSexIv.setImageResource(R.drawable.sex_man_icon);
//                    mUserSexIv.setVisibility(View.VISIBLE);
                    break;
                case UserInfoUtils.USER_SEX_WOMAN:
                    mUserSexIv.setImageResource(R.drawable.sex_women_icon);
//                    mUserSexIv.setVisibility(View.VISIBLE);
                    break;
                default:
                    mUserSexIv.setVisibility(View.GONE);
                    break;
            }
        // FIXME: 2017/7/31
//        if (GloableParams.UserInfos != null) {
//            String name = UserUtils.getUserName(ctx);
//            if (name != null && !TextUtils.isEmpty(name))
//                tvname.setText(name);
//        }

        long goodAssetBalance = (long) (BtsHelper.powerInDouble(BtsHelper.getAccountAssets(CommonConstants.ONEGOOD_ASSET_SYMBOL).precision, BtsHelper.getAccountAssets(CommonConstants.ONEGOOD_ASSET_SYMBOL).ammount) * CommonConstants.ONE_GOOD_BAD_FIX);
        long badAssetBalance = (long) BtsHelper.powerInDouble(BtsHelper.getAccountAssets(CommonConstants.ONEBAD_ASSET_SYMBOL).precision, BtsHelper.getAccountAssets(CommonConstants.ONEBAD_ASSET_SYMBOL).ammount) * CommonConstants.ONE_GOOD_BAD_FIX;
        double chatAssetBalance = BtsHelper.powerInDouble(BtsHelper.getAccountAssets(CommonConstants.CHAT_ASSET_SYMBOL).precision, BtsHelper.getAccountAssets(CommonConstants.CHAT_ASSET_SYMBOL).ammount);
        mGoodAssetTv.setText(String.valueOf(goodAssetBalance));
        mBadAssetTv.setText(String.valueOf(badAssetBalance));
        int chatMsgNum = (int) (chatAssetBalance / (Double.parseDouble(CommonConstants.CHAT_NUMBER_AMOUNT)));
        mChatAssetTv.setText(String.valueOf(chatMsgNum));

        String inviteCode = OneAccountHelper.getInviteCode();
        if (ConfigConstants.DEBUG) {
            switch (ConfigConstants.CURRENT_SERVICE_TYPE) {
                case ConfigConstants.SERVICE_TYPE_RELEASE://正式服务
                    inviteCode += "-正式版";
                    break;
                case ConfigConstants.SERVICE_TYPE_GRAY_TEST://灰度测试服务
                    inviteCode += "-灰度测试版";
                    break;
                case ConfigConstants.SERVICE_TYPE_TEST://开发测试服务
                    inviteCode += "-测试版";
                    break;
            }
        }

        mInvitationCodeTv.setText(getString(R.string.invitation_code) + inviteCode);
    }

    @Override
    public void onResume() {
        try {
            super.onResume();

            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.view_user_info:
                //修改信息
                JumpAppPageUtil.jumpSetUserInfoPage(getContext());
                break;
            case R.id.txt_backup_seed:
                JumpAppPageUtil.jumpShowSeedPage(getContext(), false);
                break;
            case R.id.txt_restore_wallet:
                JumpAppPageUtil.jumpAccountRestorePage(getActivity());
                break;
            case R.id.iv_my_qr_code: //我的二维码
                JumpAppPageUtil.jumpMyQrCodePage(getActivity());
                break;
            case R.id.txt_red_packet_account:  //红包账户
                JumpAppPageUtil.jumpNativeWebView(getContext(), ServiceConstants.GetRedPacketAccountUrl().getHost_url(), "", CommonConstants.H5_TYPE_RED_PACKET);
                break;
            case R.id.view_test_node:
                JumpAppPageUtil.jumpSetServiceNodePage(getContext());
                break;
            case R.id.txt_setting:
                JumpAppPageUtil.jumpSettingPage(getActivity());
                break;
            case R.id.view_announcement:
                //公告
                JumpAppPageUtil.jumpNativeWebView(getContext(), ServiceConstants.GetPublicNoticeUrl().getHost_url(), "", CommonConstants.H5_TYPE_SIMPLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}