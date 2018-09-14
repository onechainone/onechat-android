package oneapp.onechat.chat.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.BaseUtils;
import oneapp.onechat.chat.utils.DialogUtil;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.onewallet.modle.UploadImgResultBean;
import oneapp.onechat.oneandroid.onewallet.modle.UserInfoBean;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;
import onewalletui.util.ImageUtils;
import onewalletui.util.UiUtils;

import static onemessageui.dialog.DialogUtil.PHOTO_REQUEST_CAMERA;
import static onemessageui.dialog.DialogUtil.PHOTO_REQUEST_CUT;
import static onemessageui.dialog.DialogUtil.PHOTO_REQUEST_GALLERY;


public class SetUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private final String Tag = "SetUserInfoActivity";

    private TextView mTitleTv;
    private ImageView mBackIv;
    private TextView mSaveTv;

    private BaseActivity mContext;

    //头像
    private ImageView mUserHeadIv;

    //昵称
    private EditText mNickNameEt, mUsernameEt;
    //简介
    private EditText mJianjieEt, mEmailCodeEt, mEmailEt, mTelCodeEt;

    private TextView mPhoneTv;

    private String mSex = UserInfoUtils.USER_SEX_UNKNOWN;

    private ImageView mManIv, mWomenIv;

    private TextView mGetEmailCodeTv;
    private View mGetTelCodeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ifUnlockThisActivity = false;

        mContext = this;
        setContentView(R.layout.activity_set_user_info);
        initView();
        initData();
        setListener();
    }


    /**
     * 载入视图
     */
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.txt_title);
        mTitleTv.setText(getString(R.string.user_info));

        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);

        mSaveTv = (TextView) findViewById(R.id.txt_right);
        mSaveTv.setText(getString(R.string.button_save));

        mUserHeadIv = (ImageView) findViewById(R.id.iv_user_head);

        mNickNameEt = (EditText) findViewById(R.id.et_my_nikename);

        mUsernameEt = (EditText) findViewById(R.id.et_my_username);
        mJianjieEt = (EditText) findViewById(R.id.et_jianjie);
        mEmailCodeEt = (EditText) findViewById(R.id.et_email_code);
        mEmailEt = (EditText) findViewById(R.id.et_email);
        mTelCodeEt = (EditText) findViewById(R.id.et_tel_code);
        mPhoneTv = (TextView) findViewById(R.id.tv_phone);

        mGetEmailCodeTv = (TextView) findViewById(R.id.tv_get_email_code);
        mGetTelCodeTv = findViewById(R.id.view_bind_phone);

        mManIv = (ImageView) findViewById(R.id.iv_man);
        mWomenIv = (ImageView) findViewById(R.id.iv_women);

    }

    protected void initData() {

        ImageUtils.displayAvatarNetImage(mContext, UserInfoUtils.getUserAvatar(), mUserHeadIv, UserInfoUtils.getUserInfo().getSex());
        mNickNameEt.setText(UserInfoUtils.getUserInfo().getNickname());

        mUsernameEt.setText(OneAccountHelper.getMeAccountName());

        if (!StringUtils.equalsNull(UserInfoUtils.getUserInfo().getIntro()))
            mJianjieEt.setText(UserInfoUtils.getUserInfo().getIntro());
        if (!StringUtils.equalsNull(UserInfoUtils.getUserInfo().getEmail()))
            mEmailEt.setText(UserInfoUtils.getUserInfo().getEmail());
        if (!StringUtils.equalsNull(UserInfoUtils.getUserInfo().getMobile())) {
            mPhoneTv.setText(UserInfoUtils.getUserInfo().getMobile());
        } else {
            mGetTelCodeTv.setOnClickListener(this);
        }
        mSex = UserInfoUtils.getUserInfo().getSex();
        if (!StringUtils.equalsNull(mSex))
            switch (mSex) {
                case UserInfoUtils.USER_SEX_MAN:
                    mManIv.setImageResource(R.drawable.rb_set_sex_selected);
                    mWomenIv.setImageResource(R.drawable.rb_set_sex_normal);
                    break;
                case UserInfoUtils.USER_SEX_WOMAN:
                    mWomenIv.setImageResource(R.drawable.rb_set_sex_selected);
                    mManIv.setImageResource(R.drawable.rb_set_sex_normal);
                    break;
                default:
                    mManIv.setImageResource(R.drawable.rb_set_sex_normal);
                    mWomenIv.setImageResource(R.drawable.rb_set_sex_normal);
                    break;
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OneAccountHelper.RequestMyInfo(new RequestSuccessListener<UserInfoBean>() {
            @Override
            public void onResponse(UserInfoBean userInfoBeanMapResult) {
                initData();
            }
        });
    }

    protected void setListener() {
        mBackIv.setOnClickListener(this);
        mUserHeadIv.setOnClickListener(this);
        mSaveTv.setOnClickListener(this);
        mWomenIv.setOnClickListener(this);
        mManIv.setOnClickListener(this);
        mGetEmailCodeTv.setOnClickListener(this);
        mUsernameEt.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_user_head:
                //修改头像
                tempSaveFilePath = BaseUtils.getSaveBitmapFile() + ImageUtils.createPhotoFileName();
                DialogUtil.chooceAndCropImageDialog(mContext, tempSaveFilePath);
                break;

            case R.id.txt_right:
                mSubmitInfo();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.et_my_username:
                //不能修改用户名
                ToastUtils.simpleToast(getString(R.string.user_name_can_not_edit));
                break;
            case R.id.iv_man:
                mManIv.setImageResource(R.drawable.rb_set_sex_selected);
                mWomenIv.setImageResource(R.drawable.rb_set_sex_normal);
                mSex = UserInfoUtils.USER_SEX_MAN;
                break;
            case R.id.iv_women:
                mWomenIv.setImageResource(R.drawable.rb_set_sex_selected);
                mManIv.setImageResource(R.drawable.rb_set_sex_normal);
                mSex = UserInfoUtils.USER_SEX_WOMAN;
                break;
            case R.id.tv_get_email_code:
                // FIXME: 2017/10/19 hs
                ToastUtils.simpleToast(R.string.feature_come_soon);
                break;

            case R.id.view_bind_phone:
                //同步通讯录
                BaseUtils.AddUserContacts(mContext, false, true);
//                JumpAppPageUtil.jumpBindPhonePage(context);
                break;
        }
    }

    private String tempSaveFilePath;
    Uri uri;

    /**
     * 获取照片
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_REQUEST_GALLERY) {
                if (data != null) {
                    // 得到图片的全路径
                    uri = data.getData();
                    Log.i("xxx", uri + "");
                    uri = UiUtils.crop(uri, SetUserInfoActivity.this);
                }

            } else if (requestCode == PHOTO_REQUEST_CAMERA) {
                if (BaseUtils.isMounted()) {
                    File tempFile = new File(tempSaveFilePath);
                    uri = BaseUtils.getImageUriForFile(context, tempFile);

                    uri = UiUtils.crop(uri, SetUserInfoActivity.this);

                } else {
                    Toast.makeText(SetUserInfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == PHOTO_REQUEST_CUT) {
                try {

                    File file = BaseUtils.uri2File(this, uri);

                    OneAccountHelper.UploadAvatarRequest(file, new RequestSuccessListener<MapResult<UploadImgResultBean>>() {
                        @Override
                        public void onResponse(MapResult<UploadImgResultBean> avatarBeanResult) {
                            UserInfoUtils.getUserInfo().setAvatar_url(avatarBeanResult.getData().getMap().getAvatar_url());
                            ImageUtils.displayAvatarNetImage(mContext, UserInfoUtils.getUserAvatar(), mUserHeadIv, UserInfoUtils.getUserInfo().getSex());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //提交
    private void mSubmitInfo() {
        OneAccountHelper.UpdateUserInfoRequest(mNickNameEt.getText().toString(), mSex, mJianjieEt.getText().toString(), new RequestSuccessListener<MapResult<UserInfoBean>>() {
            @Override
            public void onResponse(MapResult<UserInfoBean> userResult) {
                OneAccountHelper.RequestMyInfo(new RequestSuccessListener<UserInfoBean>() {
                    @Override
                    public void onResponse(UserInfoBean userBeanResult) {
                        finish();
                    }
                });
            }
        });
    }

}


