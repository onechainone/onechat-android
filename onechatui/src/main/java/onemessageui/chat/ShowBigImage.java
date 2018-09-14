package onemessageui.chat;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

import java.io.File;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.dialog.DialogUtil;
import onewalletui.ui.BaseActivity;
import onewalletui.util.jump.JumpParamsContants;

/**
 * 下载显示大图
 */
public class ShowBigImage extends BaseActivity {

    private ProgressDialog pd;
    private ImageView mSaveImgIv;
    private LargeImageView image;
    private ProgressBar loadLocalPb;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (LargeImageView) findViewById(R.id.image);
        mSaveImgIv = (ImageView) findViewById(R.id.iv_save_img);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        uri = getIntent().getParcelableExtra("uri");
        String imgUrl = getIntent().getStringExtra(JumpParamsContants.INTENT_IMG_URL);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // 本地存在，直接显示本地的图片
        if (uri != null && new File(uri.getPath()).exists()) {

            image.setImage(new FileBitmapDecoderFactory(uri.getPath()));

            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mSaveImgIv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseUtils.inertImageToPhone(new File(uri.getPath()));
                    ToastUtils.simpleToast(R.string.save_to_phone_success);
                }
            });
            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogUtil.longClickBigImageDialog(context, uri, null);
                    return false;
                }
            });
        } else {
            if (!StringUtils.equalsNull(imgUrl)) {
                ImageLoader.getInstance().loadImage(imgUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                        image.setImage(loadedImage);

                        image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        mSaveImgIv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BaseUtils.inertImageToPhone(new File(uri.getPath()));
                                ToastUtils.simpleToast(R.string.save_to_phone_success);
                            }
                        });

                        image.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                DialogUtil.longClickBigImageDialog(context, uri, loadedImage);
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
    }

}
