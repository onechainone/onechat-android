package onewalletui.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.util.RoundedCenterBitmapDisplayer;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.UserInfoUtils;

/**
 * @说 明：
 * @项目名称：Sz1504
 * @包 名： com.qianfeng.zhangwei.day26.utils
 * @类 名： ImageLoaderHelper
 * @创 建人： zhangwei
 * @创建时间：2015-07-24 15:31
 * @版 本：v1.0
 * @修 改人：
 * @修改时间：
 * @修改备注：
 */
public class ImageLoaderHelper {
    private static ImageLoaderHelper helper;

    private ImageLoaderHelper() {
    }


    public static ImageLoaderHelper getInstance() {
        if (helper == null) {
            helper = new ImageLoaderHelper();
        }
        return helper;
    }

    public static void initImageLoader(Context mContext) {
        HttpParams params = new BasicHttpParams();
        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        // Default connection and socket timeout of 10 seconds. Tweak to taste.
        HttpConnectionParams.setConnectionTimeout(params, 100 * 1000);
        HttpConnectionParams.setSoTimeout(params, 100 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        // Don't handle redirects -- return them to the caller. Our code
        // often wants to re-POST after a redirect, which we must do ourselves.
        HttpClientParams.setRedirecting(params, false);
        // Set the specified user agent and register standard protocols.
        HttpProtocolParams.setUserAgent(params, "some_randome_user_agent");
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration
                        .Builder(mContext)
                        .threadPoolSize(3)
                        .threadPriority(Thread.NORM_PRIORITY - 2)  //降低线程的优先级保证主UI线程不受太大影响
                        .diskCacheFileCount(100) //缓存的File数量
                        .diskCacheSize(50 * 1024 * 1024)//缓存上限
                        //.memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                        .memoryCache(new WeakMemoryCache())
                        .memoryCacheSize(10 * 1024 * 1024)
                        .imageDownloader(new HttpClientImageDownloader(mContext, new DefaultHttpClient(manager, params)))
                        .build();

        ImageLoader.getInstance().init(config.createDefault(OneAccountHelper.getContext()));
    }


    //查看大图用的
    public DisplayImageOptions getDisplayBigImageOptions() {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        //启用内存缓存
        builder.cacheInMemory(true);
        builder.cacheOnDisk(false);
        builder.imageScaleType(ImageScaleType.NONE);
        builder.showImageOnFail(R.drawable.default_image);
        builder.showImageForEmptyUri(R.drawable.default_image);
        return builder.build();
    }

    public DisplayImageOptions getDisplayImageOptions(int resFailId, int resLoadingId, int round) {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        //启用内存缓存
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.showImageOnLoading(resLoadingId);
        builder.showImageOnFail(resFailId);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        if (round > 0)
            builder.displayer(new RoundedCenterBitmapDisplayer(round, 1));//是否设置为圆角，弧度为多少,这个会影响imagview的ScareType
        builder.imageScaleType(ImageScaleType.EXACTLY);
        builder.showImageForEmptyUri(resFailId);

        return builder.build();
    }

    public DisplayImageOptions getDisplayImageOptions(int resFailId, int resLoadingId, int round, float ratio) {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        //启用内存缓存
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.showImageOnLoading(resLoadingId);
        builder.showImageOnFail(resFailId);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        if (round > 0)
            builder.displayer(new RoundedCenterBitmapDisplayer(round, ratio));//是否设置为圆角，弧度为多少,这个会影响imagview的ScareType
        builder.imageScaleType(ImageScaleType.EXACTLY);
        builder.showImageForEmptyUri(resFailId);

        return builder.build();
    }


    //普通图片配置
    public DisplayImageOptions getSimpleDisplayImageOptions() {

        return getDisplayImageOptions(R.drawable.default_image, R.drawable.default_image, 0);

    }

    //普通圆角图片配置
    public DisplayImageOptions getSimpleDisplayImageOptions2() {

        return getDisplayImageOptions(R.drawable.default_image, R.drawable.default_image, 6, 1.5f);

    }

    //普通圆角图片配置
    public DisplayImageOptions getSimpleDisplayImageOptions2(float f) {

        return getDisplayImageOptions(R.drawable.default_image, R.drawable.default_image, 6, f);

    }

    //圆形图片配置
    public DisplayImageOptions getCircleDisplayImageOptions() {
        return getDisplayImageOptions(R.drawable.default_image, R.drawable.default_image, 360);
    }

    //圆形图片配置
    public DisplayImageOptions getCircleDisplayImageOptions(int defaultImg) {
        return getDisplayImageOptions(defaultImg, defaultImg, 360);
    }

    //圆形头像配置
    public DisplayImageOptions getAvatarDisplayImageOptions(String sex) {
        int resLoadingId = R.drawable.default_my_head;
        if (!StringUtils.equalsNull(sex))
            switch (sex) {
                case UserInfoUtils.USER_SEX_MAN:
                    resLoadingId = R.drawable.default_head_man;
                    break;
                case UserInfoUtils.USER_SEX_WOMAN:
                    resLoadingId = R.drawable.default_head_women;
                    break;
                default:
                    resLoadingId = R.drawable.default_my_head;
                    break;
            }
        return getDisplayImageOptions(resLoadingId, resLoadingId, 360);
    }
}
