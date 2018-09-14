package onewalletui.util.qrcode;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;

/**
 * @author John L. Jegutanis
 */
public class QrUtils {

    private final static QRCodeWriter QR_CODE_WRITER = new QRCodeWriter();

    private static final Logger log = LoggerFactory.getLogger(QrUtils.class);
    private static final ErrorCorrectionLevel ERROR_CORRECTION_LEVEL = ErrorCorrectionLevel.M;

    private static final int DARK_COLOR = 0xdd000000;
    private static final int LIGHT_COLOR = 0;

    public static boolean setQr(ImageView view, Resources res, String content) {
        return setQr(view, res, content, R.dimen.qr_code_size, R.dimen.qr_code_quite_zone_pixels, null);
    }

    public static boolean setQr(ImageView view, Resources res, String content, Bitmap logoBitmap) {
        return setQr(view, res, content, R.dimen.qr_code_size, R.dimen.qr_code_quite_zone_pixels, logoBitmap);
    }

    private static boolean setQr(ImageView view, Resources res, String content,
                                 int viewSizeResId, int qrQuiteZoneResId, Bitmap logoBitmap) {

        int qrCodeViewSize = res.getDimensionPixelSize(viewSizeResId);
        int qrQuiteZone = (int) res.getDimension(qrQuiteZoneResId);

        Bitmap bitmap = create(content, qrQuiteZone);
        if (bitmap == null) {
            return false;
        }

        BitmapDrawable qr = new BitmapDrawable(res, bitmap);
        qr.setFilterBitmap(false);
        int qrSize = (qrCodeViewSize / qr.getIntrinsicHeight()) * qr.getIntrinsicHeight();
        view.getLayoutParams().height = qrSize;
        view.getLayoutParams().width = qrSize;
        view.requestLayout();
        view.setImageDrawable(qr);

        return true;
    }

    public static Bitmap create(final String content, final int marginSize) {
        return create(content, DARK_COLOR, LIGHT_COLOR, marginSize);
    }

    public static Bitmap create(final String content, final int darkColor, final int lightColor,
                                final int marginSize) {
        try {
            QRCode code = Encoder.encode(content, ERROR_CORRECTION_LEVEL, null);
            int size = code.getMatrix().getWidth();

            final Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.MARGIN, marginSize);
            hints.put(EncodeHintType.ERROR_CORRECTION, ERROR_CORRECTION_LEVEL);
            final BitMatrix result =
                    QR_CODE_WRITER.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            final int width = result.getWidth();
            final int height = result.getHeight();
            final int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                final int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? darkColor : lightColor;
                }
            }

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (final WriterException x) {
            log.info("Could not create qr code", x);
            return null;
        }
    }


    /**
     * 格式化
     *
     * @param qrParams
     * @return
     */
    public static String formatQrUri(HashMap<String, String> qrParams) {
        String qrUri = BaseUtils.buildUrl(Constants.ACTION_INTENT_STRING_START, qrParams);
        return qrUri;
    }


    /**
     * 解析二维码图片工具类
     *
     * @param analyzeCallback
     */
    public static void analyzeBitmap(Bitmap mBitmap, AnalyzeCallback analyzeCallback) {
        String qrCode = scanBitmap(mBitmap);
        if (!StringUtils.equalsNull(qrCode)) {
            analyzeCallback.onAnalyzeSuccess(mBitmap, qrCode);
        } else {
            analyzeCallback.onAnalyzeFailed();
        }
    }

    /**
     * 解析二维码结果
     */
    public interface AnalyzeCallback {

        void onAnalyzeSuccess(Bitmap mBitmap, String result);

        void onAnalyzeFailed();
    }

    public interface ScanCall {
        void getCode(String code);
    }

    /**
     * 扫描当前View上的二维码
     *
     * @param mView
     * @param mScanCall
     */
    public static void scanCode(View mView, ScanCall mScanCall) {
        Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.rgb(0xf1, 0xf1, 0xf1));
        mView.draw(canvas);
        if (bitmap != null) {
            //todo:调用扫描
            String code = scanBitmap(bitmap);
            mScanCall.getCode(code);
        }
    }

    /**
     * 解析二维码
     *
     * @param bitmap
     * @return
     */
    public static String scanBitmap(Bitmap bitmap) {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        //将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        //初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        //开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (result != null) {
            return result.getText();
        } else {
            return "";
        }

    }
}
