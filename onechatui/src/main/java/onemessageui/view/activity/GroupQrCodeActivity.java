package onemessageui.view.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.onewallet.Constants;
import onewalletui.onekeyshare.ShareUtils;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import onewalletui.util.ImageUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onewalletui.util.qrcode.QrUtils;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpParamsContants;

public class GroupQrCodeActivity extends BaseActivity implements View.OnClickListener {

    private TextView txt_title;
    private ImageView img_back;
    private String groupId;
    private UserGroupInfoItem groupInfo;
    private ImageView mIvGroupAvatar;
    private TextView mTvGroupName;
    private TextView mTvGroupId;
    private ImageView groupQrCode;
    private String groupUrl;
    private Bitmap logoBitmap;
    private LinearLayout mLlGroupQrcode;
    private TextView mTvLongPress;
    private TextView mTvShareQrcode;
    private TextView mTvGroupDescription;
    private String id;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_qr_code);

        initView();
        initData();
        setListener();
    }

    private void initData() {
        readArguments();
        groupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId, false);
        mTvGroupName.setText(groupInfo.group_name);
        mTvGroupId.setText(getResources().getString(R.string.group_id) + ": " + groupInfo.group_uid);
        ImageUtils.displayCircleNetImage(this, groupInfo.getGroupAvatarUrl(), mIvGroupAvatar, R.drawable.default_group);
        logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_group);
        QrUtils.setQr(groupQrCode, getResources(), groupUrl, logoBitmap);
        mTvGroupId.setText(getResources().getString(R.string.group_id) + ": " + id);
        mTvGroupDescription.setText(description);

    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            this.groupId = (String) paramMap.get(JumpParamsContants.INTENT_GROUP_ID);
            this.groupUrl = (String) paramMap.get(JumpParamsContants.INTENT_ADD_GROUP_URL);
            this.id = (String) paramMap.get(JumpParamsContants.INTENT_SINGLE_GROUP_ID);
            this.description = (String) paramMap.get(JumpParamsContants.INTENT_GROUP_DESCRIPTION);
        }
    }

    private void setListener() {
        img_back.setOnClickListener(this);
        mTvShareQrcode.setOnClickListener(this);
        mTvLongPress.setOnClickListener(this);
//        final Bitmap loadedImage = ((BitmapDrawable)groupQrCode.getDrawable()).getBitmap();
    }

    private void initView() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.group_qr_code));
        txt_title.setVisibility(View.VISIBLE);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        mIvGroupAvatar = (ImageView) findViewById(R.id.iv_group_avatar);
        mTvGroupName = (TextView) findViewById(R.id.tv_group_name);
        mTvGroupId = (TextView) findViewById(R.id.tv_group_id);
        groupQrCode = (ImageView) findViewById(R.id.iv_group_qrcode);
        mLlGroupQrcode = (LinearLayout) findViewById(R.id.ll_group_qr_code);
        mTvLongPress = (TextView) findViewById(R.id.tv_long_press);
        mTvShareQrcode = (TextView) findViewById(R.id.tv_share_qrcode);
        mTvGroupDescription = (TextView) findViewById(R.id.tv_group_description);
    }

    @Override
    public void onClick(View v) {

        mLlGroupQrcode.setDrawingCacheEnabled(true);
        mLlGroupQrcode.buildDrawingCache();  //启用DrawingCache并创建位图
        final Bitmap loadedImage = Bitmap.createBitmap(mLlGroupQrcode.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        mLlGroupQrcode.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能

        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.tv_long_press) {
            checkPermission(new CheckPermListener() {
                @Override
                public void superPermission() {

                    File file = BaseUtils.saveBitmapFile(loadedImage, TimeUtils.getTrueTime() + Constants.SAVE_GROUP_QRCODE_IMG_NAME, true);
                    if (file.exists()) {
                        ToastUtils.simpleToast(getResources().getString(R.string.image_save_success));
                    } else {
                        ToastUtils.simpleToast(getResources().getString(R.string.image_save_fail));
                    }

                }
            }, R.string.gallery, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        } else if (i == R.id.tv_share_qrcode) {
            context.checkPermission(new CheckPermListener() {
                @Override
                public void superPermission() {
                    File file = BaseUtils.saveBitmapFile(loadedImage, TimeUtils.getTrueTime() + ImageUtils.DEFAULT_IMAGE_FORMAT);
                    ShareUtils.showShare(context, "", "", file.getPath(), null, "", null, null);
                }
            }, R.string.file, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
    }
}
