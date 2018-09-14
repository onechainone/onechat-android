package onemessageui.community.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shizhefei.view.largeimage.LargeImageView;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.dialog.DialogUtil;

/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends Fragment {
    private Activity activity;
    private String mImageUrl;
    private ImageView mSaveImgIv;
    private LargeImageView image;

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_show_big_image, container, false);
        image = (LargeImageView) v.findViewById(R.id.image);
        mSaveImgIv = (ImageView) v.findViewById(R.id.iv_save_img);

        ImageLoader.getInstance().loadImage(ServiceConstants.GetWeiboImgConfigServer() + mImageUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                image.setImage(loadedImage);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activity != null)
                            activity.finish();
                    }
                });
                mSaveImgIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BaseUtils.saveBitmapFile(loadedImage, PathUtil.getInstance().getImagePath(), true);
                        ToastUtils.simpleToast(R.string.save_to_phone_success);
                    }
                });
                image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (activity != null)
                            DialogUtil.longClickBigImageDialog(activity, null, loadedImage);
                        return false;
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return v;
    }
}
