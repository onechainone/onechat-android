package onemessageui.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;

//浏览器
public class NativeWebViewActivity extends OneBaseActivity {
    private ImageView img_back;
    private boolean ifShowRefresh = false;
    private boolean ifShowTitleTxt = false;
    private boolean ifCanBack = false;
    private final int WHITE_STYLE = 1;
    private final int BLACK_STYLE = 2;
    private final int RED_PACKET_STYLE = 3;
    private int titleColorStyle = WHITE_STYLE;
    private int titleBgRes = R.color.base_bg_color_level1;
    private TextView txt_back, txt_title, txt_close, txt_refresh;
    private View mTitleView, mShadowLine;
    private LinearLayout webMainView;
    private WebView mWebView;
    private String strurl = "";
    private String title = "";
    private int type = CommonConstants.H5_TYPE_SIMPLE;
    private String groupId;

    private boolean ifInitLoadUrl = false, ifSHowShadowLine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        readArguments();
        initStyle();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_native_webview);
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            this.strurl = (String) paramMap.get(JumpParamsContants.INTENT_WEB_URL);
            this.title = (String) paramMap.get(JumpParamsContants.INTENT_TITLE);
            this.type = (int) paramMap.get(JumpParamsContants.INTENT_TYPE);
            this.groupId = (String) paramMap.get(JumpParamsContants.INTENT_GROUP_ID);
        }
    }

    private void initStyle() {
        switch (type) {
            case CommonConstants.H5_TYPE_RED_PACKET:
                titleColorStyle = RED_PACKET_STYLE;
                titleBgRes = R.color.red_packet_color;
                ifCanBack = true;
                ifShowRefresh = true;
                ifSHowShadowLine = false;
                ifShowTitleTxt = true;
                break;
            case CommonConstants.H5_TYPE_MINING_COMMUNITY:
                titleColorStyle = WHITE_STYLE;
                titleBgRes = R.drawable.title_bar_mining_community;
                ifSHowShadowLine = false;
                ifCanBack = false;
                ifShowRefresh = true;
                ifShowTitleTxt = false;
                break;
            case CommonConstants.H5_TYPE_MINING_POWER:
                titleColorStyle = BLACK_STYLE;
                ifCanBack = false;
                ifShowRefresh = true;
                ifShowTitleTxt = true;
                break;
            case CommonConstants.H5_TYPE_GROUP:
                titleColorStyle = BLACK_STYLE;
                ifCanBack = true;
                ifShowRefresh = true;
                ifShowTitleTxt = true;
                break;
            case CommonConstants.H5_TYPE_TRADE_DIVIDEND:
                titleColorStyle = WHITE_STYLE;
                titleBgRes = R.drawable.title_trade_dividend;
                ifSHowShadowLine = false;
                ifCanBack = true;
                ifShowRefresh = true;
                ifShowTitleTxt = true;
                break;
            case CommonConstants.H5_TYPE_TRADE_MINING:
                titleColorStyle = WHITE_STYLE;
                titleBgRes = R.drawable.title_trade_mining;
                ifSHowShadowLine = false;
                ifCanBack = true;
                ifShowRefresh = true;
                ifShowTitleTxt = true;
                break;
            default:
                ifSHowShadowLine = true;
                titleColorStyle = BLACK_STYLE;
                ifCanBack = true;
                ifShowRefresh = true;
                ifShowTitleTxt = true;
                break;
        }

            titleColorStyle = BLACK_STYLE;
        switch (titleColorStyle) {
            case WHITE_STYLE:
                break;
            case BLACK_STYLE:
                break;
            case RED_PACKET_STYLE:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            if (ifInitLoadUrl) {
                mWebView.reload();
            } else {
                mWebView.loadUrl(getDetailUrl());

                ifInitLoadUrl = true;
            }
        }
//        String token = BaseUtils.getUrlValueByName(strurl, ServiceConstants.KEY_TOKEN);
//        if (!StringUtils.equalsNull(token) && !token.equals(BtsHelper.getUserToken())) {
//            strurl.replaceAll(token, BtsHelper.getUserToken());
//        }
//
//        mWebView.loadUrl(strurl);
//        mWebView.loadUrl(getDetailUrl());
//        try {
//            mWebView.getClass().getMethod("onResume")
//                    .invoke(mWebView, (Object[]) null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String getDetailUrl() {
        if (type == CommonConstants.H5_TYPE_GROUP) {
            HashMap<String, String> webviewPublicParamsMap = ServiceConstants.GetWebviewPublicParams(true);
            webviewPublicParamsMap.put(ServiceConstants.KEY_GROUP_UID, groupId);
            webviewPublicParamsMap.put(ServiceConstants.KEY_ACCOUNT_ID, OneAccountHelper.getAccountId());
            return BaseUtils.buildUrl(strurl, webviewPublicParamsMap);
        } else {
            return BaseUtils.buildUrl(strurl, ServiceConstants.GetWebviewPublicParams(true));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        try {
//            mWebView.getClass().getMethod("onPause")
//                    .invoke(mWebView, (Object[]) null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void initControl() {
        mTitleView = findViewById(R.id.layout_title);
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_back = (TextView) findViewById(R.id.txt_back);
        webMainView = (LinearLayout) findViewById(R.id.view_web);
        mWebView = (WebView) findViewById(R.id.mwebview);
        mShadowLine = findViewById(R.id.shadow_line);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void initView() {
        txt_title.setText(title);

        txt_close = (TextView) findViewById(R.id.txt_left);
        txt_close.setText(R.string.string_close);

        txt_refresh = (TextView) findViewById(R.id.txt_right);
        txt_refresh.setText(R.string.action_refresh);

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
//                禁止长按
                return true;
            }
        });

        mWebView.clearCache(true);


        mTitleView.setBackgroundResource(titleBgRes);

        if (ifSHowShadowLine) {
            mShadowLine.setVisibility(View.VISIBLE);
        } else {
            mShadowLine.setVisibility(View.GONE);
        }
        if (ifShowRefresh) {
            txt_refresh.setVisibility(View.VISIBLE);
        }

        if (ifCanBack) {
            txt_back.setVisibility(View.VISIBLE);
            txt_close.setVisibility(View.VISIBLE);
        }

        if (ifShowTitleTxt) {
            txt_title.setVisibility(View.VISIBLE);
        } else {
            txt_title.setVisibility(View.INVISIBLE);
        }

        int textColor = R.color.base_bg_color_level1;

        switch (titleColorStyle) {
            case WHITE_STYLE:
                img_back.setImageResource(R.drawable.icon_back_white);
                textColor = R.color.base_bg_color_level1;
//                txt_title.setTextColor(ContextCompat.getColor(context, R.color.base_bg_color_level1));
//                txt_refresh.setTextColor(ContextCompat.getColor(context, R.color.base_bg_color_level1));
//                txt_back.setTextColor(ContextCompat.getColor(context, R.color.base_bg_color_level1));
//                txt_close.setTextColor(ContextCompat.getColor(context, R.color.base_bg_color_level1));
                break;
            case BLACK_STYLE:
                img_back.setImageResource(R.drawable.icon_back);
                textColor = R.color.base_text_color_level1;
//                txt_title.setTextColor(ContextCompat.getColor(context, R.color.default_title_color));
//                txt_refresh.setTextColor(ContextCompat.getColor(context, R.color.default_title_color));
//                txt_back.setTextColor(ContextCompat.getColor(context, R.color.default_title_color));
//                txt_close.setTextColor(ContextCompat.getColor(context, R.color.default_title_color));
                break;
            case RED_PACKET_STYLE:
                img_back.setImageResource(R.drawable.icon_back_red_packet);
                textColor = R.color.red_packet_text;
//                txt_title.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
//                txt_back.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
//                txt_close.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
//                txt_refresh.setTextColor(ContextCompat.getColor(context, R.color.red_packet_text));
                break;
        }

        txt_title.setTextColor(textColor);
        txt_back.setTextColor(textColor);
        txt_close.setTextColor(textColor);
        txt_refresh.setTextColor(textColor);

        if (!TextUtils.isEmpty(strurl)) {
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new MyWebViewClient());
            //自适应屏幕
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
            }

            mWebView.getSettings().setJavaScriptEnabled(true);
//            mWebView.addJavascriptInterface(new Object() {
//                @JavascriptInterface
//                public void setParam(final String str) {
//                    setParamFromJS(str);
//                }
//            }, "JSInterface");

            if (Build.VERSION.SDK_INT >= 19) {
                mWebView.getSettings().setLoadsImagesAutomatically(true);
            } else {
                mWebView.getSettings().setLoadsImagesAutomatically(false);
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }

        }
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(Constants.ACTION_INTENT_STRING_START)||url.startsWith(Constants.ACTION_INTENT_STRING_START2)) {
                JumpAppPageUtil.detailIntentDataJump(context, url, false);
                return true;
            } else
                return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {

            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            // TODO 显示错误404
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            if (!isLoadUrl && url.startsWith(Constants.ACTION_INTENT_STRING_START)) {
//                JumpAppPageUtil.detailIntentDataJump(context, url, false);
//            }
//
//            isLoadUrl = true;
        }

        //        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            isLoadUrl = false;
            if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                mWebView.getSettings().setLoadsImagesAutomatically(true);
            }
            if (ifShowTitleTxt && StringUtils.equalsNull(title)) {
                String webTitle = view.getTitle();
                if (url != null && webTitle != null && !url.endsWith(webTitle)) {
                    txt_title.setText(webTitle);
                } else {
                    txt_title.setText("");
                }
            }

//            mWebView.evaluateJavascript("document.getElementsByClassName('jump')[0].remove();document.getElementsByClassName('my-notice')[0].remove()", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    Toast.makeText(context,value,Toast.LENGTH_LONG).show();
//                }
//            });
        }

    }


    @Override
    protected void initData() {

    }


    @Override
    protected void setListener() {
        img_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txt_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txt_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txt_refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView != null) {
                    mWebView.reload();
                }
            }
        });

        if (ConfigConstants.DEBUG) {
            mTitleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    UiUtils.copy(context, mWebView.getUrl());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        if (ifCanBack && mWebView.canGoBack())
            mWebView.goBack();
        else
            Utils.finish(NativeWebViewActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 先从父控件中移除WebView
        webMainView.removeView(mWebView);
        mWebView.stopLoading();
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();
    }
}
