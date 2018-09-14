package onemessageui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import sdk.android.onechatui.R;


public class WarnTipDialog extends BaseDialog implements
        View.OnClickListener {
    private TextView btn_cancel, btn_ok;
    private TextView mHtvText;
    private String mText;
    private static OnClickListener mOnClickListener;
    private static BaseDialog mBaseDialog;// 当前的对话框

    public WarnTipDialog(Context context, String text) {
        super(context);
        mText = text;
        mBaseDialog = new BaseDialog(context);
        init();
    }

    private void init() {
        setContentView(R.layout.layout_dialog_warntip);
        mHtvText = (TextView) findViewById(R.id.dialog_generic_htv_message);
        mHtvText.setText(mText);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    public void setText(String text) {
        if (text == null) {
            mHtvText.setVisibility(View.GONE);
        } else {
            mText = text;
            mHtvText.setText(mText);
        }
    }

    public void setBtnOkLinstener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            if (isShowing()) {
                super.dismiss();
            }

        } else if (i == R.id.btn_ok) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(mBaseDialog, 1);
            }

        }
    }
}
