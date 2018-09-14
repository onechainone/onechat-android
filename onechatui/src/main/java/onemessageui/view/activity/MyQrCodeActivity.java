package onemessageui.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import onewalletui.onekeyshare.ShareUtils;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import onewalletui.util.qrcode.QrUtils;
import onemessageui.view.OneBaseActivity;

//import oneapp.onemessage.GloableParams;
//import oneapp.onemessage.bean.User;

public class MyQrCodeActivity extends OneBaseActivity implements OnClickListener {

    private TextView txt_title, txt_right, tvname, tv_accout, tv_share;
    private ImageView img_back, img_avatar;
    private TextView mUserInviteUrl;

    private String mInviteUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_my_qr_code);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(MyQrCodeActivity.this);

        } else if (i == R.id.view_copy_invite_url) {//复制邀请链接
            UiUtils.copy(context, getString(R.string.user_invite_url_start) + mInviteUrl);

        } else if (i == R.id.txt_right) {//分享邀请链接
            ShareUtils.showShareWebUrl(context, mInviteUrl, getString(R.string.user_invite_url_start) + mInviteUrl, mInviteUrl);

        } else {
        }
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getString(R.string.my_qr_code));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        tv_share = (TextView) findViewById(R.id.txt_right);
        tv_share.setVisibility(View.VISIBLE);
        tvname = (TextView) findViewById(R.id.tvname);
        tv_accout = (TextView) findViewById(R.id.tvmsg);
        img_avatar = (ImageView) findViewById(R.id.iv_head);
        mUserInviteUrl = (TextView) findViewById(R.id.tv_user_invite_url);
    }

    @Override
    protected void initView() {
        tv_share.setText(getString(R.string.action_share));
    }

    @Override
    protected void initData() {
        tv_accout.setText(getString(R.string.onechat_id) + OneAccountHelper.getMeAccountName());
        ImageView imageView = (ImageView) findViewById(R.id.img_code);
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_head_man);
        QrUtils.setQr(imageView, getResources(), getUri(), logoBitmap);
        tvname.setText(UserInfoUtils.getUserInfo().getNickname());
        ImageUtils.displayAvatarNetImage(this, UserInfoUtils.getUserAvatar(), img_avatar, UserInfoUtils.getUserInfo().getSex());

        mInviteUrl = ServiceConstants.GetShareUserUrl(OneAccountHelper.getMeAccountName());
        mUserInviteUrl.setText(mInviteUrl);
    }

    private String getUri() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.ACTION_INTENT_KEY_ACTION, Constants.INTENT_ACTION_TYPE_ADD_FRIEND);
        params.put(Constants.ACTION_INTENT_KEY_ACCOUNT_NAME, OneAccountHelper.getMeAccountName());

        String qrUri = QrUtils.formatQrUri(params);
        return qrUri;
    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        findViewById(R.id.view_copy_invite_url).setOnClickListener(this);
    }

}
