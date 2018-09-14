package onewalletui.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.acra.ACRA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.LogUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onecore.graphenej.models.OrderBook;
import onemessageui.widght.VerticalImageSpan;
import sdk.android.onechatui.R;

/**
 * @author John L. Jegutanis
 */
public class UiUtils {
    private static final Logger log = LoggerFactory.getLogger(UiUtils.class);
    public static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3;// 结果

    //获取屏幕高度
    public static int getWindowHeight(Context mContext) {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();//屏幕高度
        return height;
    }

    //获取屏幕宽度
    public static int getWindowWidth(Context mContext) {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        return width;
    }

    static public void share(Activity activity, String text) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(text);

        activity.startActivity(Intent.createChooser(
                builder.getIntent(),
                activity.getString(R.string.action_share)));
    }

    public static void copy(Context context, String string) {
        if (StringUtils.equalsNull(string)) {
            return;
        }
        Object clipboardService = context.getSystemService(Context.CLIPBOARD_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipboardManager clipboard = (ClipboardManager) clipboardService;
                clipboard.setPrimaryClip(ClipData.newPlainText("simple text", string));
            } else {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) clipboardService;
                clipboard.setText(string);
            }
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Should not normally happen
            ACRA.getErrorReporter().handleSilentException(e);
            Toast.makeText(context, R.string.error_generic, Toast.LENGTH_LONG).show();
        }
    }

    public static String getPasteString(Context context) {
        Object clipboardService = context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData abc;
        String text = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipboardManager clipboard = (ClipboardManager) clipboardService;
                if (clipboard != null) {
                    abc = clipboard.getPrimaryClip();
                    ClipData.Item item = abc.getItemAt(0);
                    text = item.getText().toString();
                }
            } else {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) clipboardService;
                if (clipboard != null)
                    text = clipboard.getText().toString();
            }
        } catch (Exception e) {

        }
        return text;
    }

    public static void setVisible(View view) {
        setVisibility(view, View.VISIBLE);
    }

    public static void setInvisible(View view) {
        setVisibility(view, View.INVISIBLE);
    }

    public static void setGone(View view) {
        setVisibility(view, View.GONE);
    }

    public static void setVisibility(View view, int visibility) {
        if (view.getVisibility() != visibility) view.setVisibility(visibility);
    }

    public static ActionMode startActionMode(final Activity activity, final ActionMode.Callback callback) {
        if (activity == null || !(activity instanceof AppCompatActivity)) {
            log.warn("To show action mode, your activity must extend " + AppCompatActivity.class);
            return null;
        }
        return ((AppCompatActivity) activity).startSupportActionMode(callback);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 剪切图片
     *
     * @param uri
     * @function:
     * @author:Jerry
     * @date:2013-12-30
     */
    public static Uri crop(Uri uri, Activity mActivity) {

        int dp = 500;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);//输出是X方向的比例
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", dp);//输出X方向的像素
        intent.putExtra("outputY", dp);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);


        Uri userImageUri = BaseUtils.createImagePathUri(mActivity);

//        Uri userImageUri = Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/" + DialogUtil.createPhotoFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, userImageUri);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);//设置为不返回数据
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        mActivity.startActivityForResult(intent, UiUtils.PHOTO_REQUEST_CUT);
        return userImageUri;
    }

    /*
    * 压缩图片
    *
    * */
    public static Bitmap getBitmapFormUri(Context ac, Uri uri) {
        try {
            InputStream input = null;

            input = ac.getContentResolver().openInputStream(uri);

            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;
            //图片分辨率以480x800为标准
            float hh = 800f;//这里设置高度为800f
            float ww = 480f;//这里设置宽度为480f
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (originalWidth / ww);
            } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (originalHeight / hh);
            }
            if (be <= 0)
                be = 1;
            //比例压缩
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = be;//设置缩放比例
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = ac.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();

            return compressImage(bitmap);//再进行质量压缩
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * * 质量压缩方法
     * *
     * * @param image
     * * @return
     *     
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static float getMaxAmount(List<OrderBook.Order> bids, List<OrderBook.Order> asks, int size) {
        float price = 1;
        try {
            boolean bSet = false;

            int length = bids.size() > size ? size : bids.size();
            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    price = Float.parseFloat(bids.get(0).quote);
                    bSet = true;
                } else {
                    if (price < Float.parseFloat(bids.get(i).quote)) {
                        price = Float.parseFloat(bids.get(i).quote);
                    }
                }
            }

            length = asks.size() > size ? size : asks.size();
            for (int i = 0; i < length; i++) {
                if (!bSet) {
                    if (i == 0) {
                        price = Float.parseFloat(asks.get(0).quote);
                        bSet = true;
                    }
                } else {
                    if (price < Float.parseFloat(asks.get(i).quote)) {
                        price = Float.parseFloat(asks.get(i).quote);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return price;
    }

    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    //通过定时器强制隐藏虚拟键盘
    public static void HideKeyboard(final View view) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
            }
        }, 400);
    }

    public static void SetSearchTextView(Context context, TextView textView, String baseStr, String searchStr) {
        try {
            searchStr = searchStr.toLowerCase();
            String baseStrLow = baseStr.toLowerCase();
            int index = baseStrLow.indexOf(searchStr);
            if (index >= 0 && !StringUtils.equalsNull(searchStr)) {
                SpannableString span = new SpannableString(baseStr);
                span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.base_color)), index, index + searchStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(baseStr);
                textView.setText(span);  //设置字体变颜色
            } else {
                textView.setText(baseStr);
            }
        } catch (Exception e) {
            textView.setText(baseStr);
        }
    }

    /**
     * 解决透明状态栏下，布局无法自动拉起的问题
     * 手动设置View的高度
     */
    public static void setAdaptInput(Activity activity, final View scrollView) {

        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = decorView.getRootView().getHeight();
                int heightDifferent = screenHeight - rect.bottom;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
                lp.setMargins(0, 0, 0, heightDifferent);
                scrollView.requestLayout();
            }
        });
    }

    /**
     * 显示图文混排
     */
    public static boolean formatTextView(final Context context, final TextView tv, String text) {
        return formatTextView(context, tv, text, tv.getWidth());
    }

    /**
     * 显示图文混排
     */
    public static boolean formatTextView(final Context context, final TextView tv, String text,
                                         final int width) {
        //是否含有图片
        boolean ifHaveImg = false;

        //图片前后加回车
        if (!StringUtils.equalsNull(text)) {
            text = detailText(context, text);

            tv.setText(text);
            final SpannableString spannableString = new SpannableString(text);
            Pattern p = Pattern.compile(CommonConstants.IMG_PATTERN_LEFT + "(.*?)" + CommonConstants.IMG_PATTERN_RIGHT);
            Matcher m = p.matcher(text);

            Bitmap bitmap = null;
            while (m.find()) {
                final String imgUrl = m.group(1);

                ifHaveImg = true;
                final int start = m.start();
                final int end = m.end();

                if (bitmap == null) {
                    //先用默认图片替换图片路径
                    bitmap = BitmapFactory
                            .decodeResource(context.getResources(), R.drawable.icon_launcher);
                    // 计算缩放比例
                    float scaleWidth = (float) width / (float) bitmap.getWidth();
                    // 取得想要缩放的matrix参数
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleWidth);
                    try {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                                true);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                ClickableSpan clickSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                        JumpAppPageUtil.jumpImagePage(context, imgUrl);
                    }
                };

                ImageSpan imageSpan = new ImageSpan(context, bitmap);

                // 用ImageSpan对象替换指定的字符串
                spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv.setText(spannableString);
                if (!StringUtils.equalsNull(imgUrl)) {
                    //加载网络图片
                    ImageLoader.getInstance()
                            .loadImage(imgUrl, ImageLoaderHelper.getInstance().getSimpleDisplayImageOptions(),
                                    new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            // 计算缩放比例
                                            float scaleWidth = (float) width / (float) loadedImage.getWidth();
                                            // 取得想要缩放的matrix参数
                                            Matrix matrix = new Matrix();
                                            matrix.postScale(scaleWidth, scaleWidth);
                                            try {
                                                loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
                                                        loadedImage.getHeight(), matrix,
                                                        true);
                                            } catch (Throwable e) {
                                                e.printStackTrace();
                                            }
                                            ImageSpan imageSpan = new ImageSpan(context, loadedImage);

                                            // 用ImageSpan对象替换你指定的字符串
                                            spannableString
                                                    .setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            tv.setText(spannableString);
                                            tv.setMovementMethod(LinkMovementMethod
                                                    .getInstance());
                                        }
                                    });
                }
            }
        }
        return ifHaveImg;
    }


    /***
     * 图片前后加回车
     */
    public static String detailText(Context context, String text) {
        Pattern p = Pattern.compile(CommonConstants.IMG_PATTERN_LEFT + "(.*?)" + CommonConstants.IMG_PATTERN_RIGHT);
        Matcher m = p.matcher(text);
        String mStr = "";
        int lastEnd = 0;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            mStr += text.substring(lastEnd, start);
            //if (StringUtils.equalsNull(text.substring(start - 1, start)) || !text.substring(start - 1, start).equals('\n'))
            mStr += '\n';
            mStr += text.substring(start, end);
            //if (StringUtils.equalsNull(text.substring(end, end + 1)) || !text.substring(end, end + 1).equals('\n'))
            mStr += '\n';

            lastEnd = end;
        }
        mStr = mStr + text.substring(lastEnd, text.length());

        mStr = mStr.replace("\\n", "\n");
        //mStr = Html.fromHtml(mStr).toString();
        LogUtils.d("mStr", mStr);
        return mStr;
    }
    /**
     * dialog中点击确认按钮的回调
     *
     * @author heshuai
     */
    public interface ConfirmCallBackInf {
        void onConfirmClick(String content);
    }
    //点赞变大动画
    public static void changeZanBig(ImageView mImg, final ConfirmCallBackInf callBackInf) {

        float x = mImg.getX();
        float y = mImg.getY();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mImg,
                "alpha", 1.0f, 0f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mImg,
                "scaleX", 1.0f, 1.5f, 1.0f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(mImg,
                "scaleY", 1.0f, 1.5f, 1.0f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.playTogether(anim1, anim2, anim3);

        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callBackInf.onConfirmClick("");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    public static void addGoodWeiboIcon(Context context, TextView tv, String text) {
        String imgSpanStr = "#";
        text = imgSpanStr + text;
        SpannableString spannableString = new SpannableString(text);
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.good_weibo_icon);
        VerticalImageSpan imageSpan = new VerticalImageSpan(context, R.drawable.good_weibo_icon);

        // 用ImageSpan对象替换指定的字符串
        spannableString.setSpan(imageSpan, 0, imgSpanStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(spannableString);
    }

    public static void scrollToPosition(final ListView listView, final int position) {
        int duration = Math.abs(listView.getSelectedItemPosition() - position) * 10;
        if (duration > 1000) {
            duration = 1000;
        }
        scrollToPosition(listView, position, duration);
    }

    public static void scrollToPosition(final ListView listView, final int position, int duration) {
        if (duration > 0) {
            listView.smoothScrollToPositionFromTop(position, 0, duration);
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(position);
                }
            }, duration + 50);
        } else {
            listView.setSelection(position);
        }
    }
}
