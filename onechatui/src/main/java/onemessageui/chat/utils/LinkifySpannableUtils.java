package onemessageui.chat.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;

import java.util.regex.Pattern;

import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import sdk.android.onechatui.R;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.activity.SimpleWebViewActivity;
import onewalletui.ui.BaseActivity;

/**
 * Created by 何帅 on 2017/11/11.
 */

public class LinkifySpannableUtils {

    public static LinkifySpannableUtils mInstance;

    private Context mContext;
    private TextView mTextView;
    private SpannableStringBuilder mSpannableStringBuilder;

    private LinkifySpannableUtils() {
    }

    public static LinkifySpannableUtils getInstance() {
        if (mInstance == null) {
            mInstance = new LinkifySpannableUtils();
        }
        return mInstance;
    }

    public void setSpan(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;
        addLinks();
    }

    private void addLinks() {

        Linkify.addLinks(mTextView, WEB_PATTERN, null);
        Linkify.addLinks(mTextView, EMAIL_ADDRESS, null);
        Linkify.addLinks(mTextView, PHONE, null);

        CharSequence cSequence = mTextView.getText();
        if (cSequence instanceof Spannable) {
            int end = mTextView.getText().length();
            Spannable sp = (Spannable) mTextView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            mSpannableStringBuilder = new SpannableStringBuilder(sp);
//            mSpannableStringBuilder.clearSpans();

            for (URLSpan url : urls) {
                String urlString = url.getURL();
                PatternURLSpan patternURLSpan = new PatternURLSpan(urlString);
                if (urlString != null && urlString.length() > 0) {
                    int _start = sp.getSpanStart(url);
                    int _end = sp.getSpanEnd(url);
                    try {
                        mSpannableStringBuilder.setSpan(patternURLSpan, _start, _end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            mTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.link_line_color));
            mTextView.setHighlightColor(ContextCompat.getColor(mContext, R.color.link_line_color));
            mTextView.setText(mSpannableStringBuilder, TextView.BufferType.SPANNABLE);
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    private class PatternURLSpan extends ClickableSpan {

        private String mString;

        PatternURLSpan(String str) {
            this.mString = str;
        }

        @Override
        public void onClick(View widget) {
            if (EMAIL_ADDRESS.matcher(mString).find()) {
                sendEmail(mString);
            } else if (WEB_PATTERN.matcher(mString).find()) {
                openUrl(mString);
            } else if (PHONE.matcher(mString).find()) {
                dialNum(mString);
            } else {
                if (mString.contains(".")) {
                    if (mString.startsWith("http")) {
                        openUrl(mString);
                    } else {
                        openUrl("http://" + mString);
                    }
                }
            }
        }
    }


    /**
     * 打开系统浏览器
     *
     * @param url
     */
    private void openUrl(String url) {
        Utils.start_Activity(
                mContext,
                SimpleWebViewActivity.class,
                new BasicNameValuePair(
                        Constants.URL,
                        url));

    }


    /**
     * 拨打电话
     *
     * @param num
     */
    private void dialNum(final String num) {
        if (num != null && num.length() > 0) {
            call(num, mContext);
        }
    }

    /**
     * 调用邮箱
     *
     * @param address
     */
    private void sendEmail(String address) {
        String[] receive = new String[]{address};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, receive);
        mContext.startActivity(Intent.createChooser(intent, ""));
    }


    private void call(final String mobile, final Context activity) {
        if (mobile == null || mobile.length() == 0) {
            Toast.makeText(activity, "电话号码为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = mobile.toLowerCase();
        if (!phone.startsWith("tel:")) {
            phone = "tel:" + mobile;
        }
        final String callMobile = phone;

        //适配6.0系统，申请权限
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            if (mContext instanceof BaseActivity) {
                BaseActivity mActivity = (BaseActivity) mContext;
                mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                    @Override
                    public void superPermission() {
                        callPhone(activity, callMobile);
                    }
                }, R.string.call, Manifest.permission.CALL_PHONE);
            }
        } else {
            callPhone(activity, callMobile);
        }

    }

    public static void callPhone(final Context activity, final String callMobile) {
        DialogUtil.simpleDialog(activity, activity.getString(R.string.make_sure_call_to) + callMobile.replace("tel:", ""), new DialogUtil.ConfirmCallBackInf() {
            @Override
            public void onConfirmClick(String content) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(callMobile));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                activity.startActivity(intent);
            }
        });

    }


    public final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL =
            "(?:"
                    + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
                    + "|(?:biz|b[abdefghijmnorstvwyz])"
                    + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
                    + "|d[ejkmoz]"
                    + "|(?:edu|e[cegrstu])"
                    + "|f[ijkmor]"
                    + "|(?:gov|g[abdefghilmnpqrstuwy])"
                    + "|h[kmnrtu]"
                    + "|(?:info|int|i[delmnoqrst])"
                    + "|(?:jobs|j[emop])"
                    + "|k[eghimnprwyz]"
                    + "|l[abcikrstuvy]"
                    + "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
                    + "|(?:name|net|n[acefgilopruz])"
                    + "|(?:org|om)"
                    + "|(?:pro|p[aefghklmnrstwy])"
                    + "|qa"
                    + "|r[eosuw]"
                    + "|s[abcdeghijklmnortuvyz]"
                    + "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
                    + "|u[agksyz]"
                    + "|v[aceginu]"
                    + "|w[fs]"
                    + "|(?:\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae|\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435|\u0440\u0444|\u0441\u0440\u0431|\u05d8\u05e2\u05e1\u05d8|\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc|\u0625\u062e\u062a\u0628\u0627\u0631|\u0627\u0644\u0627\u0631\u062f\u0646|\u0627\u0644\u062c\u0632\u0627\u0626\u0631|\u0627\u0644\u0633\u0639\u0648\u062f\u064a\u0629|\u0627\u0644\u0645\u063a\u0631\u0628|\u0627\u0645\u0627\u0631\u0627\u062a|\u0628\u06be\u0627\u0631\u062a|\u062a\u0648\u0646\u0633|\u0633\u0648\u0631\u064a\u0629|\u0641\u0644\u0633\u0637\u064a\u0646|\u0642\u0637\u0631|\u0645\u0635\u0631|\u092a\u0930\u0940\u0915\u094d\u0937\u093e|\u092d\u093e\u0930\u0924|\u09ad\u09be\u09b0\u09a4|\u0a2d\u0a3e\u0a30\u0a24|\u0aad\u0abe\u0ab0\u0aa4|\u0b87\u0ba8\u0bcd\u0ba4\u0bbf\u0baf\u0bbe|\u0b87\u0bb2\u0b99\u0bcd\u0b95\u0bc8|\u0b9a\u0bbf\u0b99\u0bcd\u0b95\u0baa\u0bcd\u0baa\u0bc2\u0bb0\u0bcd|\u0baa\u0bb0\u0bbf\u0b9f\u0bcd\u0b9a\u0bc8|\u0c2d\u0c3e\u0c30\u0c24\u0c4d|\u0dbd\u0d82\u0d9a\u0dcf|\u0e44\u0e17\u0e22|\u30c6\u30b9\u30c8|\u4e2d\u56fd|\u4e2d\u570b|\u53f0\u6e7e|\u53f0\u7063|\u65b0\u52a0\u5761|\u6d4b\u8bd5|\u6e2c\u8a66|\u9999\u6e2f|\ud14c\uc2a4\ud2b8|\ud55c\uad6d|xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-3e0b707e|xn\\-\\-45brj9c|xn\\-\\-80akhbyknj4f|xn\\-\\-90a3ac|xn\\-\\-9t4b11yi5a|xn\\-\\-clchc0ea0b2g2a9gcd|xn\\-\\-deba0ad|xn\\-\\-fiqs8s|xn\\-\\-fiqz9s|xn\\-\\-fpcrj9c3d|xn\\-\\-fzc2c9e2c|xn\\-\\-g6w251d|xn\\-\\-gecrj9c|xn\\-\\-h2brj9c|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-j6w193g|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-kprw13d|xn\\-\\-kpry57d|xn\\-\\-lgbbat1ad8j|xn\\-\\-mgbaam7a8h|xn\\-\\-mgbayh7gpa|xn\\-\\-mgbbh1a71e|xn\\-\\-mgbc0a9azcg|xn\\-\\-mgberp4a5d4ar|xn\\-\\-o3cw4h|xn\\-\\-ogbpf8fl|xn\\-\\-p1ai|xn\\-\\-pgbs0dh|xn\\-\\-s9brj9c|xn\\-\\-wgbh1c|xn\\-\\-wgbl6a|xn\\-\\-xkc2al3hye2a|xn\\-\\-xkc2dl3a5ee0h|xn\\-\\-yfro4i67o|xn\\-\\-ygbi2ammx|xn\\-\\-zckzah|xxx)"
                    + "|y[et]" + "|z[amw]))";

    public final String GOOD_IRI_CHAR = "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";


    public static final Pattern WEB_URL = Pattern
            .compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");


    public static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}");
    public static final Pattern WEB_PATTERN =
            Pattern.compile("(((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(.(html|htm|asp|php|jsp|shtml|nsp))");

    public static final Pattern PHONE = Pattern.compile( // sdd = space, dot, or dash
            "(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
                    + "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
                    + "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])");
}
