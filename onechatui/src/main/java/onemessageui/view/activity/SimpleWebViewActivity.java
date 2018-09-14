package onemessageui.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import oneapp.onechat.oneandroid.onemessage.Constants;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import onemessageui.dialog.DialogUtil;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import sdk.android.onechatui.R;

//浏览器
public class SimpleWebViewActivity extends OneBaseActivity {
    private ImageView img_back;
    private TextView txt_close;
    private TextView txt_title;
    private TextView txt_back;
    private ImageView img_menu;
    private RelativeLayout webMainView;
    private WebView mWebView;
    private ProgressBar progressbar;
    private String strurl = "";
    private MyTimer mTimer;
    private int progress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_web);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mWebView.getClass().getMethod("onResume")
                    .invoke(mWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mWebView.getClass().getMethod("onPause")
                    .invoke(mWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_close = (TextView) findViewById(R.id.txt_left);
        txt_close.setVisibility(View.VISIBLE);
        txt_close.setText(R.string.string_close);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        txt_back = (TextView) findViewById(R.id.txt_back);
        txt_back.setVisibility(View.VISIBLE);

        img_menu = (ImageView) findViewById(R.id.img_right);
        img_menu.setVisibility(View.VISIBLE);
        img_menu.setImageResource(R.drawable.icon_webview_menu);
        webMainView = (RelativeLayout) findViewById(R.id.view_web);
        mWebView = (WebView) findViewById(R.id.mwebview);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(Constants.URL) != null) {
            strurl = "";
            strurl = bundle.getString(Constants.URL);
        }
        if (bundle != null && bundle.getString(Constants.Title) != null) {
            txt_title.setText(bundle.getString(Constants.Title));
        } else
            txt_title.setText("");

//        mWebView.clearCache(true);
        if (!TextUtils.isEmpty(strurl)) {
            mWebView.setWebViewClient(new MyWebViewClient());
            mWebView.setWebChromeClient(new MyWebChromeClient());
            //自适应屏幕
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setSupportZoom(true);// 设置可以支持缩放
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.getSettings().setDisplayZoomControls(false);//不显示webview缩放按钮
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
            }
            mWebView.getSettings().setJavaScriptEnabled(true);
            if (Build.VERSION.SDK_INT >= 19) {
                mWebView.getSettings().setLoadsImagesAutomatically(true);
            } else {
                mWebView.getSettings().setLoadsImagesAutomatically(false);
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }

            mWebView.loadUrl(strurl);

        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(oneapp.onechat.oneandroid.onewallet.Constants.ACTION_INTENT_STRING_START) || url.startsWith(oneapp.onechat.oneandroid.onewallet.Constants.ACTION_INTENT_STRING_START2)) {
                JumpAppPageUtil.detailIntentDataJump(context, url, false);
                return true;
            } else
                return false;
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
            if (mTimer == null) {
                mTimer = new MyTimer(15000, 50);
            }
            mTimer.start();
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                mWebView.getSettings().setLoadsImagesAutomatically(true);
            }
            if (mTimer != null) {
                mTimer.cancel();
            }
            progress = 0;
            progressbar.setProgress(100);
            progressbar.setVisibility(View.GONE);
            String webTitle = view.getTitle();
            if (webTitle != null) {
                txt_title.setText(view.getTitle());
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

//            txt_title.setText(title);
        }
    }


    @Override
    protected void initData() {

    }

    /* 定义一个倒计时的内部类 */
    private class MyTimer extends CountDownTimer {
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (progressbar == null) {
                return;
            }
            progress = 100;
            progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (progressbar == null) {
                return;
            }
            if (progress == 100) {
                progressbar.setVisibility(View.GONE);
            } else {
                progressbar.setProgress(progress++);
            }
        }

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

        img_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.webviewMeuDialog(context, mWebView);
            }
        });
        txt_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack())
            mWebView.goBack();
        else
            Utils.finish(SimpleWebViewActivity.this);
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
