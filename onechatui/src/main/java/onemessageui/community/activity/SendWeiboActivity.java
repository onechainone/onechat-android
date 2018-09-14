package onemessageui.community.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.ImageUtils;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import onemessageui.widght.GridViewForScrollView;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import onewalletui.ui.widget.flowtag.FlowTagView;
import oneapp.onechat.oneandroid.onewallet.util.AssetInfoUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.adpter.InputKeywordAdapter;
import onemessageui.community.adapter.ShowImgSimpleAdapter;
import onemessageui.dialog.DialogUtil;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;

public class SendWeiboActivity extends BaseActivity implements View.OnClickListener {

    private final String Tag = "SendWeiboActivity";

    private Context mContext;

    private EditText mWeiboContentTv;
    private ImageView mGalleryIv, mVideoIv;
    private GridViewForScrollView mImgGv;
    List<ImageItem> imageList = new ArrayList<>();
    private ShowImgSimpleAdapter mImgAdapter;
    private TextView mSendWeiboTv;
    //视频缩略图
    private ImageView mVideoThumbIv;
    private ImageView mDeleteVideoIv;
    private TextView mVideoTimeTv;
    private TextView mVideoSizeTv;
    private String videoPatch;
    private FrameLayout mVideoFl;

    private String mGroupId;
    private ImageView img_back;
    private LinearLayout mInputKeyword;
    private LinearLayout mChargeContent;
    private FlowTagView selectKeywordFlowView;
    private List<String> keywordList = new ArrayList<>(); //关键词列表
    private InputKeywordAdapter keywordAdapter;

    private String is_pay = CommonConstants.NO_VALUE;
    private String payValue = "", payAssetCode = "";
    private TextView mSetPayStatusTv;
    private TextView txt_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ifOpenRightSlideBack = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_weibo);
        mContext = this;
        readArguments();
        initView();
        initData();
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        if (intent != null) {
            this.mGroupId = intent.getStringExtra(JumpParamsContants.INTENT_GROUP_ID);
        }
    }

    /***
     * 载入视图
     */
    private void initView() {

        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setVisibility(View.VISIBLE);
        txt_title.setText(getResources().getString(R.string.release_invitation));
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        img_back.setOnClickListener(this);
        mInputKeyword = (LinearLayout) findViewById(R.id.ll_input_keyword);
        mInputKeyword.setOnClickListener(this);
        mChargeContent = (LinearLayout) findViewById(R.id.ll_charge_content);
        mChargeContent.setOnClickListener(this);
        selectKeywordFlowView = (FlowTagView) findViewById(R.id.keyword_select);

        mWeiboContentTv = (EditText) findViewById(R.id.weibo_content_et);
        mSendWeiboTv = (TextView) findViewById(R.id.txt_right);
//        mSendWeiboTv.setVisibility(View.VISIBLE);
//        mSendWeiboTv.setText(R.string.send);
//        mSendWeiboTv.setOnClickListener(this);
        mGalleryIv = (ImageView) findViewById(R.id.iv_gallery);
        mGalleryIv.setOnClickListener(this);
        mVideoIv = (ImageView) findViewById(R.id.iv_video);
        mVideoIv.setOnClickListener(this);
        mDeleteVideoIv = (ImageView) findViewById(R.id.iv_delete_video);
        mDeleteVideoIv.setOnClickListener(this);

        mVideoFl = (FrameLayout) findViewById(R.id.fl_video);
        mVideoThumbIv = (ImageView) findViewById(R.id.iv_video_thumb);
        mVideoThumbIv.setOnClickListener(this);
        mVideoTimeTv = (TextView) findViewById(R.id.tv_video_time);
        mVideoSizeTv = (TextView) findViewById(R.id.tv_video_size);

        mImgGv = (GridViewForScrollView) findViewById(R.id.weibo_img_gv);

        mImgAdapter = new ShowImgSimpleAdapter(this, imageList);
        mImgGv.setAdapter(mImgAdapter);
        mImgAdapter.setDeleteImgInf(new ShowImgSimpleAdapter.DeleteImgInf() {
            @Override
            public void onDeleteImgClick(int position) {
                if (imageList.size() > position) {
                    imageList.remove(position);
                    mImgAdapter.update(imageList);
                }
            }
        });
        mSetPayStatusTv = (TextView) findViewById(R.id.tv_set_pay_value);
        initIsPay();
        mSendWeiboTv.setVisibility(View.VISIBLE);
        mSendWeiboTv.setText(getResources().getString(R.string.publish));
        mSendWeiboTv.setOnClickListener(this);
    }

    private void initIsPay() {
        if (is_pay.equals(CommonConstants.TRUE_VALUE)) {
            AssetInfo assetInfo = AssetInfoUtils.getAssetInfoBySymbol(payAssetCode);
            if (assetInfo != null) {
                mSetPayStatusTv.setText(payValue + assetInfo.getShort_name());
            }
        } else {
            mSetPayStatusTv.setText("");
        }
    }

    private void initData() {
        keywordAdapter = new InputKeywordAdapter(context, keywordList);
        selectKeywordFlowView.setAdapter(keywordAdapter);
        keywordAdapter.setOnItemClickListener(new InputKeywordAdapter.OnItemClickListener() {
            @Override
            public void setItemClick(int position) {
                keywordList.remove(position);
                keywordAdapter.refresh();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_gallery) {
            if (!StringUtils.equalsNull(videoPatch)) {
                return;
            }
            //淡出选择图片
            ImagePicker.getInstance().setSelectLimit(CommonConstants.MAX_SEND_WEIBO_IMG - imageList.size());
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PIC);
//                DialogUtil.selectImgDialog(SendWeiboActivity.this);

        } else if (i == R.id.iv_video) {
            if (imageList.size() > 0) {
                return;
            }
            Intent openVideoIntent = new Intent();
            openVideoIntent.setAction(Intent.ACTION_GET_CONTENT);
            openVideoIntent.setType("video/*");
                    /* 取得相片后返回本画面 */
            startActivityForResult(openVideoIntent, Constants.REQUEST_CODE_TAKE_VIDEO);

        } else if (i == R.id.iv_delete_video) {
            mVideoFl.setVisibility(View.GONE);
            videoPatch = "";

        } else if (i == R.id.iv_video_thumb) {
            Uri uri = Uri.parse(videoPatch);
            //调用系统自带的播放器
            Intent videoIntent = new Intent(Intent.ACTION_VIEW);
            videoIntent.setDataAndType(uri, "video/mp4");
            startActivity(videoIntent);

        } else if (i == R.id.txt_right) {
            String keyWord = Joiner.on(",").join(keywordList);
            //发送
            if (imageList.size() > 0) {
                showLoadingDialog("");
                OneCommunityHelper.createArticle(mGroupId, mWeiboContentTv.getText().toString(), imageList, CommonConstants.WEIBO_TYPE_IMAGE, keyWord, is_pay, payAssetCode, payValue, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult mapResult) {
                        hideLoadingDialog();
//                            EventBus.getDefault().post(new SendWeiboEvent());
                        if (mapResult != null) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtils.simpleToast(R.string.send_fail);
                        }
                    }
                });
            } else if (!StringUtils.equalsNull(videoPatch)) {
                //上传视频
                DialogUtil.uploadDialog(mContext, mGroupId, mWeiboContentTv.getText().toString(), videoPatch, keyWord, is_pay, payAssetCode, payValue, new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            } else if (!StringUtils.equalsNull(mWeiboContentTv.getText().toString())) {
                showLoadingDialog("");
                OneCommunityHelper.createArticle(mGroupId, mWeiboContentTv.getText().toString(), CommonConstants.WEIBO_TYPE_FEED, keyWord, is_pay, payAssetCode, payValue, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult weiboBean) {
                        hideLoadingDialog();
//                            EventBus.getDefault().post(new SendWeiboEvent());
                        if (weiboBean != null) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtils.simpleToast(R.string.send_fail);
                        }
                    }
                });
            }

        } else if (i == R.id.img_back) {//点击返回键
            backPress();


        } else if (i == R.id.ll_input_keyword) {
            DialogUtil.inputKeywordDialog(context, new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    keywordList.add(content);
                    keywordAdapter.refresh();
                }
            });

        } else if (i == R.id.ll_charge_content) {
            JumpAppPageUtil.jumpSetWeiboPayPage(context, payAssetCode, payValue);

        }
    }


    void backPress() {
        //点击返回键
        if (!StringUtils.equalsNull(videoPatch) || imageList.size() > 0 || !StringUtils.equalsNull(mWeiboContentTv.getText().toString())) {
            DialogUtil.simpleDialog(this, getResources().getString(R.string.is_sure_to_forgive_this), new DialogUtil.ConfirmCallBackInf() {
                @Override
                public void onConfirmClick(String content) {
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ImagePicker.getInstance().clear();
        } catch (Exception e) {

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backPress();
        }
        return false;
    }

    @Override
    protected void onResume() {
        if (mImgAdapter != null)
            mImgAdapter.notifyDataSetChanged();

        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_TAKE_PIC:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            if (imageList.size() + images.size() <= CommonConstants.MAX_SEND_WEIBO_IMG) {
                                imageList.addAll(images);
                                mImgAdapter.update(imageList);
                            } else {

                            }
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case Constants.REQUEST_CODE_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    videoPatch = PathUtil.videoUri2FilePath(SendWeiboActivity.this, uri);
                    long videoSize = PathUtil.getFileOrFilesSize(videoPatch);
                    if (videoSize > CommonConstants.MAX_WEIBO_VIDEO_SIZE) {
                        videoPatch = null;
                        DialogUtil.tipDialog(this, getString(R.string.weibo_video_max_tip));
                        return;
                    }
                    mVideoFl.setVisibility(View.VISIBLE);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mVideoSizeTv.setText(PathUtil.FormatFileSize(videoSize));
                    try {
                        mediaPlayer.setDataSource(videoPatch);
                        mediaPlayer.prepare();
                        mVideoTimeTv.setText(new SimpleDateFormat("mm:ss").format(mediaPlayer.getDuration()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mVideoThumbIv.setImageBitmap(ImageUtils.getVideoThumbnail(videoPatch));

                }
                break;
            case Constants.REQUEST_CODE_SET_WEIBO_PAY:
                if (data != null) {
                    is_pay = CommonConstants.TRUE_VALUE;
                    payValue = data.getStringExtra(JumpParamsContants.INTENT_VALUE);
                    payAssetCode = data.getStringExtra(JumpParamsContants.INTENT_ASSET_CODE);
                } else {
                    is_pay = CommonConstants.NO_VALUE;
                    payValue = "";
                    payAssetCode = "";
                }
                initIsPay();
                break;

        }
    }


}
