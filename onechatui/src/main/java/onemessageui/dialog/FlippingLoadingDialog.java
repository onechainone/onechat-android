package onemessageui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import pl.droidsonroids.gif.GifImageView;

public class FlippingLoadingDialog extends BaseDialog {

    private GifImageView mGifIcon;
    private TextView mLoadingStatusText;
    private String mText;
    private Context context;

    public FlippingLoadingDialog(Context context, String text) {
        super(context);
        this.context = context;
        mText = text;
        init();
    }

    private void init() {
        setContentView(R.layout.common_flipping_loading_diloag);
        mGifIcon = (GifImageView) findViewById(R.id.loadingdialog_gif_icon);
        mLoadingStatusText = (TextView) findViewById(R.id.tv_loading_status);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        setCanceledOnTouchOutside(false);
        setCancelable(true);
        lp.dimAmount = 0.2f;

        dialogWindow.setAttributes(lp);
    }

    public void setLoadingStatusText(String text) {
        mText = text;
        if (!StringUtils.equalsNull(mText)) {
            mLoadingStatusText.setVisibility(View.VISIBLE);
            mLoadingStatusText.setText(mText);
        } else
            mLoadingStatusText.setVisibility(View.GONE);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
