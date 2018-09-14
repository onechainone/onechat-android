package onewalletui.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;


public class ToastUtils {

    private static ToastUtils mInstance;
    private static Context mContext;
    private static Toast mToast;

    static {
        mContext = OneAccountHelper.getContext();
    }

    public static ToastUtils getInstance() {
        if (mInstance == null) {
            mInstance = new ToastUtils();
        }
        return mInstance;
    }

    public static void simpleToast(String string) {
        if (StringUtils.equalsNull(string))
            return;
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    public static void simpleToast(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }


    public static void simpleLongToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_LONG).show();
    }


    public static void clearToast() {
        if (mToast != null)
            mToast.cancel();
    }

    //打赏toast
    public static void shangToast(String string) {
        View layout = LayoutInflater.from(OneAccountHelper.getContext()).inflate(R.layout.toast_shang, null);
        TextView text = (TextView) layout.findViewById(R.id.tv_money);
        text.setText(string);

        if (mToast == null) {
            mToast = new Toast(OneAccountHelper.getContext());
        }
        mToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, UiUtils.dip2px(OneAccountHelper.getContext(), 60));
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(layout);
        mToast.show();
    }


}
