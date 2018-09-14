package onewalletui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;

import onemessageui.dialog.FlippingLoadingDialog;


/**
 * @author John L. Jegutanis
 */
public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected FlippingLoadingDialog mLoadingDialog;

    protected ProgressDialog mProgressLoadingDialog;

    private boolean ifNativeDialog = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    public void showLoadingDialog(String msg) {
        if (ifNativeDialog) {
            showDialog("", msg);
        } else {
            if (mLoadingDialog != null) {
                if (!mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                } else {
                    mLoadingDialog.setLoadingStatusText(msg);
                }
            } else {
                mLoadingDialog = new FlippingLoadingDialog(getActivity(), msg);

                mLoadingDialog.setLoadingStatusText(msg);
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.show();
            }
        }
    }


    public void showLoadingDialog() {
        showLoadingDialog("");
    }

    public void hideLoadingDialog() {
        if (ifNativeDialog) {
            hideDialog();
        } else {
            try {
                if (mLoadingDialog == null)
                    return;
                mLoadingDialog.dismiss();
            } catch (Exception e) {
            }
        }
    }

    protected void showDialog(String title, String msg) {
        try {
            if (mProgressLoadingDialog == null) {
                mProgressLoadingDialog = new ProgressDialog(getActivity());
            }

            if (!mProgressLoadingDialog.isShowing()) {
                mProgressLoadingDialog.setTitle(title);
                mProgressLoadingDialog.setMessage(msg);
                mProgressLoadingDialog.show();
            } else {
                mProgressLoadingDialog.setTitle(title);
                mProgressLoadingDialog.setMessage(msg);
            }

        } catch (Exception e) {

        }
    }

    protected void hideDialog() {

        if (mProgressLoadingDialog != null) {
            if (mProgressLoadingDialog.isShowing()) {
                mProgressLoadingDialog.cancel();
            }
        }

    }
}
