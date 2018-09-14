package oneapp.onechat.chat.utils;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.view.BaseActivity;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.*;
import oneapp.onechat.oneandroid.onewallet.util.download.DownloadUtils;
import oneapp.onechat.oneandroid.onewallet.util.download.OnDownloadListener;
import onemessageui.widght.ProgressWheel;
import oneapp.onechat.chat.WalletApplication;
import onewalletui.util.jump.JumpAppOutUtil;

import static onemessageui.dialog.DialogUtil.PHOTO_REQUEST_CAMERA;
import static onemessageui.dialog.DialogUtil.PHOTO_REQUEST_GALLERY;

/**
 * Created by 何帅 on 2018/8/29.
 */

public class DialogUtil {

    /**
     * 提交推荐码对话框
     *
     * @return
     */
    private static Context mContext;
    public static final float DEFAULT_DIALOG_DIMAMOUNT = 0.5f;// 结果
    /**
     * 版本检查对话框
     *
     * @param context
     * @return
     */
    public static Dialog upgradeDialog(final Context context, String msg, final String downloadPath) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_upgrade, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView msgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        msgTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        msgTv.setText(msg);

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);

        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null) {
                    dialog.dismiss();
                }

                if (ConfigConstants.IF_GOOGLE_PLAY_VERSION) {
                    JumpAppOutUtil.jumpOutBrowser(mContext, ServiceConstants.GOOGLE_PLAY_URL);
                } else {
                    JumpAppOutUtil.jumpDownload(mContext, downloadPath);
                    ToastUtils.simpleToast(R.string.downloading_new_apk);
                }
            }
        });
        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });

        final TextView browserBt = (TextView) layout.findViewById(R.id.btn_browser);

        browserBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (ConfigConstants.IF_GOOGLE_PLAY_VERSION) {
                    JumpAppOutUtil.jumpOutBrowser(mContext, ServiceConstants.GOOGLE_PLAY_URL);
                } else {
                    JumpAppOutUtil.jumpOutBrowser(mContext, downloadPath);
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dimen_260);
        lp.height = UiUtils.dip2px(context, 200 + msg.length());
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;
        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 版本检查对话框(强制更新)
     *
     * @param context
     * @return
     */
    public static Dialog upgradeMustDialog(final BaseActivity context, String msg, final String downloadPath) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_upgrade_must, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView msgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        msgTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        msgTv.setText(msg);

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);

        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfigConstants.IF_GOOGLE_PLAY_VERSION) {
                    JumpAppOutUtil.jumpOutBrowser(mContext, ServiceConstants.GOOGLE_PLAY_URL);
                } else {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    downloadDialog(context, downloadPath);
                }
            }
        });

        final TextView browserBt = (TextView) layout.findViewById(R.id.btn_browser);

        browserBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfigConstants.IF_GOOGLE_PLAY_VERSION) {
                    JumpAppOutUtil.jumpOutBrowser(mContext, ServiceConstants.GOOGLE_PLAY_URL);
                } else {
                    JumpAppOutUtil.jumpOutBrowser(mContext, downloadPath);
                }
            }
        });


        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                    WalletApplication.getInstance().exitApp();
//                    System.exit(0);
                    return false;
                } else {
                    return true;
                }
            }
        });


        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dimen_260);
        lp.height = UiUtils.dip2px(context, 200 + msg.length());
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }


    /**
     * 下载对话框
     *
     * @param context
     * @return
     */
    public static Dialog downloadDialog(final Context context, final String downloadPath) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_download, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView statusTv = (TextView) layout.findViewById(R.id.tv_download_status);

        final ProgressWheel progressWheel = (ProgressWheel) layout.findViewById(R.id.download_progress);

        final OnDownloadListener onDownloadListener = new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {

                JumpAppOutUtil.jumpInstallAPK(context, file, downloadPath);
                dialog.dismiss();
                WalletApplication.getInstance().exitApp();
            }

            @Override
            public void onDownloading(int progress) {
                progressWheel.setProgress(progress * 3.6f);
                progressWheel.setText(progress + "%");
                statusTv.setClickable(false);
            }

            @Override
            public void onDownloadFailed() {
                statusTv.setText(context.getString(R.string.download_error_retry));
                statusTv.setClickable(true);
                JumpAppOutUtil.jumpOutBrowser(context, downloadPath);
            }

            @Override
            public void onStartDownload() {
                statusTv.setClickable(false);
                statusTv.setText(context.getString(R.string.downloading_new_apk));
            }
        };

        DownloadUtils.download(downloadPath, false, onDownloadListener);

        statusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadUtils.download(downloadPath, false, onDownloadListener);
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                    WalletApplication.getInstance().exitApp();
//                    System.exit(0);
                    return false;
                } else {
                    return true;
                }
            }
        });

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.simple_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.simple_dialog_height);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 选择单张图片对话框
     *
     * @param mActivity
     * @return
     */
    public static Dialog chooceAndCropImageDialog(final BaseActivity mActivity, final String tempImgSaveFile) {

        final LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(sdk.android.onechatui.R.layout.dialog_chooce_pic, null,
                false);
        final Dialog dialog = new Dialog(mActivity, sdk.android.onechatui.R.style.ActionSheetDialogStyle);
        mContext = mActivity;
        dialog.setContentView(layout);
        final TextView galleryBt = (TextView) layout.findViewById(sdk.android.onechatui.R.id.btn_gallery);

        galleryBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                        @Override
                        public void superPermission() {
                            // 激活系统图库，选择一张图片
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            mActivity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                        }
                    }, sdk.android.onechatui.R.string.gallery, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });
        final TextView cameraBt = (TextView) layout.findViewById(sdk.android.onechatui.R.id.btn_camera);
        cameraBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    mActivity.checkPermission(new BaseActivity.CheckPermListener() {
                        @Override
                        public void superPermission() {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            // 判断存储卡是否可以用，可用进行存储
                            if (oneapp.onechat.oneandroid.onewallet.util.BaseUtils.isMounted()) {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        oneapp.onechat.oneandroid.onewallet.util.BaseUtils.getUriForFile(mActivity, new File(tempImgSaveFile)));
                            }
                            mActivity.startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
                        }
                    }, sdk.android.onechatui.R.string.camera, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(sdk.android.onechatui.R.id.tv_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = mActivity.getResources().getDimensionPixelSize(sdk.android.onechatui.R.dimen.choose_pic_dialog_height);
        lp.height = mActivity.getResources().getDimensionPixelSize(sdk.android.onechatui.R.dimen.choose_pic_dialog_width);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(sdk.android.onechatui.R.color.toumin);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }
}
