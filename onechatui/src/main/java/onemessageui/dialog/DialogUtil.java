package onemessageui.dialog;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.chatsdk.OneCommunityHelper;
import oneapp.onechat.oneandroid.chatsdk.OneGroupHelper;
import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.chatsdk.OneRedpacketHelper;
import oneapp.onechat.oneandroid.graphenechain.interfaces.ProgressRequestListener;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.graphenechain.models.UserContactItem;
import oneapp.onechat.oneandroid.graphenechain.models.UserGroupInfoItem;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onemessage.bean.RedPacketAssetBean;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.PathUtil;
import oneapp.onechat.oneandroid.onemessage.community.bean.CommentBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboBean;
import oneapp.onechat.oneandroid.onemessage.community.bean.WeiboCatchModel;
import oneapp.onechat.oneandroid.onewallet.modle.AssetInfo;
import oneapp.onechat.oneandroid.onewallet.modle.MapResult;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.Keyboard;
import oneapp.onechat.oneandroid.onewallet.util.ListUtils;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.TimeUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.adpter.SelectAssetAdapter;
import onemessageui.widght.FlippingImageView.FlippingImageView;
import onemessageui.widght.ProgressWheel;
import onewalletui.ui.BaseActivity;
import onewalletui.ui.DialogBuilder;
import onewalletui.ui.adaptors.SelectRedPacketAssetListAdapter;
import onewalletui.util.ImageUtils;
import onewalletui.util.jump.JumpAppOutUtil;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.qrcode.QrUtils;
import sdk.android.onechatui.R;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class DialogUtil {
    public static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3;// 结果

    public static final float DEFAULT_DIALOG_DIMAMOUNT = 0.5f;// 结果

    public static final String DELETE = "1";//
    public static final String REPLY = "2";//


    public static final String CALL_BACK_TYPE_DELETE = "delete";
    public static final String CALL_BACK_TYPE_ADD_JINGHUA = "add_jinghua";
    public static final String CALL_BACK_TYPE_AT_USER = "at_user";
    public static final String CALL_BACK_TYPE_ADD_ADMIN = "add_admin";

    /**
     * 提交推荐码对话框
     *
     * @return
     */
    private static Context mContext;

    /**
     * dialog中点击确认按钮的回调
     *
     * @author heshuai
     */
    public interface ConfirmCallBackInf {
        void onConfirmClick(String content);
    }

    public interface ConfirmCallBackObject<T> {
        void onConfirmClick(T t);
    }

    /**
     * dialog中点击取消按钮的回调
     *
     * @author heshuai
     */
    public interface CancelCallBackInf {
        void onCancelClick(String content);
    }

    /**
     * dialog中点击不再提醒按钮的回调
     *
     * @author zengyuxin
     */
    public interface RemindCallBackInf {
        void onRemindClick(String content);
    }

    /**
     * 有一个按钮仅提示的对话框
     *
     * @param context
     * @param msg     提示信息
     * @return
     */
    public static Dialog tipDialog(Context context, String msg, boolean ifCanCancel) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_tip, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        TextView mMsgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        if (!StringUtils.equalsNull(msg)) {
            mMsgTv.setText(msg);
        }

        final TextView okBt = (TextView) layout.findViewById(R.id.btn_commit);
        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(ifCanCancel);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.simple_dialog_width);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 有一个按钮仅提示的对话框
     *
     * @param context
     * @param msg     提示信息
     * @return
     */
    public static Dialog tipDialog(Context context, String msg) {

        return tipDialog(context, msg, false);
    }

    public static Dialog simpleDialog(Context context, String msg, ConfirmCallBackInf callBack) {
        return simpleDialog(context, msg, callBack, null);
    }

    /**
     * 有两个按钮的对话框
     *
     * @param context
     * @param msg      提示信息
     * @param callBack
     * @return
     */
    public static Dialog simpleDialog(Context context, String msg, final ConfirmCallBackInf callBack, final CancelCallBackInf cancelCallBack) {
        return simpleDialog(context, msg, null, null, callBack, cancelCallBack);
    }

    /**
     * 有两个按钮的对话框
     *
     * @param context
     * @param msg      提示信息
     * @param callBack
     * @return
     */
    public static Dialog simpleDialog(Context context, String msg, String confirText, String cancelText, final ConfirmCallBackInf callBack) {
        return simpleDialog(context, msg, confirText, cancelText, callBack, null);
    }

    /**
     * 对话框
     *
     * @param context
     * @param msg      提示信息
     * @param callBack
     * @return
     */
    public static Dialog simpleDialog(Context context, String msg, String confirText, String cancelText, final ConfirmCallBackInf callBack, final CancelCallBackInf cancelCallBack) {
        if (context == null) {
            return null;
        }
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_simple, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        if (!StringUtils.equalsNull(confirText)) {
            confirmBt.setText(confirText);
        }
        TextView mMsgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        if (!StringUtils.equalsNull(msg)) {
            mMsgTv.setText(msg);
        }
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (callBack != null) {
                    callBack.onConfirmClick("");
                }
            }
        });
        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        if (!StringUtils.equalsNull(cancelText)) {
            cancelBt.setText(cancelText);
        }
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (cancelCallBack != null) {
                    cancelCallBack.onCancelClick("");
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.simple_dialog_width);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 有三个按钮的对话框
     *
     * @param context
     * @param msg
     * @param confirText
     * @param remindText
     * @param cancelText
     * @param callBack
     * @param remindCallBack
     * @return
     */
    public static Dialog threeBtnDialog(Context context, String msg, String confirText, String remindText, String cancelText, final ConfirmCallBackInf callBack, final RemindCallBackInf remindCallBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_triple, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        confirmBt.setText(confirText);
        TextView mMsgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        if (!StringUtils.equalsNull(msg)) {
            mMsgTv.setText(msg);
        }
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.onConfirmClick("");
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });
        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setText(cancelText);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        TextView remindBt = (TextView) layout.findViewById(R.id.btn_remind);
        if (!StringUtils.equalsNull(remindText)) {
            remindBt.setText(remindText);
            remindBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (remindCallBack != null) {
                        remindCallBack.onRemindClick("");
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }
            });
        } else {
            remindBt.setVisibility(View.GONE);
        }

//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.simple_dialog_width);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog editNameDialog(final Context context, String title, String name, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_edit_text, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        TextView mTitleTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        mTitleTv.setText(title);

        final EditText mEditText = (EditText) layout.findViewById(R.id.dialog_edit_et);
        mEditText.setText(name);
        mEditText.setLines(1);
        if (!StringUtils.equalsNull(name))
            mEditText.setSelection(name.length());

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                String text = mEditText.getText().toString();
                if (!StringUtils.equalsNull(text) && callBack != null) {
                    callBack.onConfirmClick(text);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.edit_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.edit_dialog_height);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog editInputDialog(final Context context, String title, String defaultContent, String editHint, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_edit_text, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        TextView mTitleTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        mTitleTv.setText(title);

        final EditText mEditText = (EditText) layout.findViewById(R.id.dialog_edit_et);
        if (!StringUtils.equalsNull(editHint)) {
            mEditText.setHint(editHint);
        }
        if (!StringUtils.equalsNull(defaultContent)) {
            mEditText.setText(defaultContent);
            mEditText.setSelection(defaultContent.length());
        }

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                String text = mEditText.getText().toString();
                if (!StringUtils.equalsNull(text) && callBack != null) {
                    callBack.onConfirmClick(text);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.edit_dialog_width);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 输入密码对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog inputPswDialog(final Context context, String title, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_input_password, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        TextView mTitleTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        mTitleTv.setText(title);

        TextView mForgetPswTv = (TextView) layout.findViewById(R.id.forget_psw);
        mForgetPswTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle(R.string.override_wallet_warning_title)
                        .setMessage(R.string.override_new_wallet_warning_message)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OneAccountHelper.deleteWallet();
                            }
                        }).create().show();
//                JumpAppPageUtil.jumpRegisterGuidePage(context, Constants.FROM_OTHER);
            }
        });

        final EditText mEditText = (EditText) layout.findViewById(R.id.dialog_edit_et);

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                if (callBack != null) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    callBack.onConfirmClick(mEditText.getText().toString());
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.edit_dialog_width);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }


    /**
     * 检查密码对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog checkPswDialog(final Context context, final String title, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_input_password, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        TextView mTitleTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        mTitleTv.setText(title);

        TextView mForgetPswTv = (TextView) layout.findViewById(R.id.forget_psw);
        mForgetPswTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle(R.string.override_wallet_warning_title)
                        .setMessage(R.string.override_new_wallet_warning_message)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OneAccountHelper.deleteWallet();
                            }
                        }).create().show();
//                JumpAppPageUtil.jumpRegisterGuidePage(context, Constants.FROM_OTHER);
            }
        });

        final EditText mEditText = (EditText) layout.findViewById(R.id.dialog_edit_et);

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                String password = mEditText.getText().toString();
                if (callBack != null && !StringUtils.equalsNull(password)) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (OneAccountHelper.checkPassword(password)) {
                        callBack.onConfirmClick(password);
                    } else if (StringUtils.equalsNull(OneAccountHelper.getMePasswordBackend())) {
                        RpcCallProxy.getInstance().savePassword(password);
                    } else {
                        DialogBuilder.warn(context, R.string.unlocking_wallet_error_title)
                                .setMessage(R.string.unlocking_wallet_error_detail)
                                .setNegativeButton(R.string.button_cancel, null)
                                .setPositiveButton(R.string.button_retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkPswDialog(context, title, callBack);
                                    }
                                }).create().show();
                    }
                }
                mEditText.setText("");
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().

                getDimensionPixelSize(R.dimen.edit_dialog_width);

        lp.height = context.getResources().

                getDimensionPixelSize(R.dimen.edit_dialog_height);

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
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_chooce_pic, null,
                false);
        final Dialog dialog = new Dialog(mActivity, R.style.ActionSheetDialogStyle);
        mContext = mActivity;
        dialog.setContentView(layout);
        final TextView galleryBt = (TextView) layout.findViewById(R.id.btn_gallery);

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
                    }, R.string.gallery, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });
        final TextView cameraBt = (TextView) layout.findViewById(R.id.btn_camera);
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
                            if (BaseUtils.isMounted()) {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        BaseUtils.getUriForFile(mActivity, new File(tempImgSaveFile)));
                            }
                            mActivity.startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
                        }
                    }, R.string.camera, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.tv_cancel);
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
        lp.width = mActivity.getResources().getDimensionPixelSize(R.dimen.choose_pic_dialog_height);
        lp.height = mActivity.getResources().getDimensionPixelSize(R.dimen.choose_pic_dialog_width);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * webvie菜单
     *
     * @param mActivity
     * @return
     */
    public static Dialog webviewMeuDialog(final BaseActivity mActivity, final WebView mWebView) {

        final LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_webview_menu, null,
                false);
        final Dialog dialog = new Dialog(mActivity, R.style.ActionSheetDialogStyle);
        mContext = mActivity;
        dialog.setContentView(layout);
        final String webUrl = mWebView.getUrl();
        final TextView openOutBrowserTv = (TextView) layout.findViewById(R.id.btn_open_out_browser);
        openOutBrowserTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                JumpAppOutUtil.jumpOutBrowser(mContext, webUrl);
            }
        });
        final TextView copyUrlTv = (TextView) layout.findViewById(R.id.btn_copy_url);
        copyUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    UiUtils.copy(mContext, webUrl);
                }
            }
        });

        final TextView refreshTv = (TextView) layout.findViewById(R.id.btn_refresh);
        refreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                mWebView.reload();
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.tv_cancel);
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
        lp.width = mActivity.getResources().getDimensionPixelSize(R.dimen.webview_dialog_height);
        lp.height = mActivity.getResources().getDimensionPixelSize(R.dimen.webview_dialog_width);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }


    /**
     * dialog消息，并且可以隐藏键盘
     *
     * @param dialog
     * @param keyBoardView
     */
    private static void dimissDialog(Dialog dialog, View keyBoardView) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (keyBoardView != null) {
            InputMethodManager manager = (InputMethodManager) mContext.getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(keyBoardView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    /**
     * 重要提示对话框
     *
     * @param context
     * @param msg      提示信息
     * @param callBack
     * @return
     */
    public static Dialog importentTipDialog(Context context, String msg, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_simple, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        TextView mMsgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        if (!StringUtils.equalsNull(msg)) {
            mMsgTv.setText(msg);
            mMsgTv.setTextColor(ContextCompat.getColor(context, R.color.base_color));
        }

        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.onConfirmClick("");
                    if (dialog != null) {
                        dialog.dismiss();
                    }
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
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.simple_dialog_width);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }



    /**
     * 红包对话框
     *
     * @param context
     * @param redPacketId 红包id
     * @param callBack
     * @return
     */
    public static Dialog redPacketDialog(final Context context, String redPacketSenderId, final String redPacketId, final String redPacketMsg, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_red_packet, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        TextView mRedPacketMsgTv = (TextView) layout.findViewById(R.id.tv_red_packet_msg);
        if (!StringUtils.equalsNull(redPacketMsg)) {
            mRedPacketMsgTv.setText(redPacketMsg);
        }
        UserContactItem sender = OneAccountHelper.getDatabase().getUserContactItemById(redPacketSenderId);

        TextView senderName = (TextView) layout.findViewById(R.id.tv_sender_name);
        ImageView senderAvatar = (ImageView) layout.findViewById(R.id.iv_sender_avatar);

        if (sender != null) {
            senderName.setText(sender.getUserName());
            ImageUtils.displayAvatarNetImage(context, sender.getAvatar(), senderAvatar, sender.getSex());
        }

        final FlippingImageView imageView = (FlippingImageView) layout.findViewById(R.id.iv_chai);

        final View redPacketView = layout.findViewById(R.id.view_red_packet);
        redPacketView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    redPacketView.setClickable(false);
                    imageView.startAnimation();
                    OneRedpacketHelper.clickRedpacket(context, redPacketId, new RequestSuccessListener<Boolean>() {
                        @Override
                        public void onResponse(Boolean mapResult) {
                            if (!mapResult) {
                                redPacketView.setClickable(true);
                                imageView.clearRotateAnimation();
                            } else {
                                JumpAppPageUtil.jumpRedPacketInfoPage(context, redPacketId);
                                callBack.onConfirmClick("");
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    });

                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                layout.setBackgroundResource(0);
                imageView.clearRotateAnimation();
                imageView.setImageResource(0);
                System.gc();
            }
        });

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.red_packet_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.red_packet_dialog_height);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 选择红包资产类型对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog selectRedPacketAssetDialog(final Context context, List<RedPacketAssetBean> assetBeanList, final ConfirmCallBackObject<String> callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_select_asset, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final ListView assetLv = (ListView) layout.findViewById(R.id.dialog_asset_lv);

        if (assetBeanList != null) {
            final SelectRedPacketAssetListAdapter mAdapter = new SelectRedPacketAssetListAdapter(context, assetBeanList);
            assetLv.setAdapter(mAdapter);
            assetLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (callBack != null) {
                        callBack.onConfirmClick(mAdapter.getItem(i).getAsset_code());
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }
            });
        }
        TextView assetListTitle = (TextView) layout.findViewById(R.id.tv_asset_list_title);
        assetListTitle.setText(context.getString(R.string.select_coin_type));

        final TextView rechargeBt = (TextView) layout.findViewById(R.id.tv_recharge);
        rechargeBt.setVisibility(View.GONE);
        rechargeBt.setOnClickListener(new View.OnClickListener() {
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
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.select_asset_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.select_asset_dialog_height);
        lp.dimAmount = 0;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }


    /**
     * 重要提示对话框
     *
     * @param context
     * @param msg      禁止截图
     * @param callBack
     * @return
     */
    public static Dialog noScreenshotDialog(Context context, String msg, String hint, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_screenshot, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        TextView mMsgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        TextView mHintTv = (TextView) layout.findViewById(R.id.dialog_msg_hint);
        if (!StringUtils.equalsNull(msg)) {
            mMsgTv.setText(msg);
        }
        if (!StringUtils.equalsNull(hint)) {
            mHintTv.setText(hint);
        }

        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.onConfirmClick("");
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.screenshot_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.screenshot_height);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 展示发送的图片
     *
     * @param context
     * @return
     */
    public static Dialog showSendImgDialog(BaseActivity context, String imgFile, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_show_send_img, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        final LargeImageView showSendIv = (LargeImageView) layout.findViewById(R.id.iv_send_img);

        Luban.with(context)
                .load(imgFile)                                   // 传人要压缩的图片列表
                .ignoreBy(ImageUtils.MAX_UPLOAD_IMG_SIZE)        // 忽略不压缩图片的大小
                .setTargetDir(PathUtil.getInstance().getImagePath())     // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(final File file) {
                        if (file != null) {
                            final String filePath = file.getAbsolutePath();
                            TextView sendTv = (TextView) layout.findViewById(R.id.tv_send);
                            sendTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    if (callBack != null) {
                                        callBack.onConfirmClick(filePath);
                                    }
                                }
                            });
                            showSendIv.setImage(new FileBitmapDecoderFactory(filePath));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();

        TextView cancelTv = (TextView) layout.findViewById(R.id.tv_cancel);

        cancelTv.setOnClickListener(new View.OnClickListener() {
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
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 1;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 上传视频到服务器进度对话框
     *
     * @param callBack
     * @return
     */
    public static Dialog uploadDialog(final Context context, String groupId, String text, String videoPatch, String keyword, final String is_pay, final String asset_code, final String reward_price, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_upload, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final ProgressWheel mCirclePercentView = (ProgressWheel) layout.findViewById(R.id.upload_progress);
        final TextView mUploadTipTv = (TextView) layout.findViewById(R.id.tv_upload_tip_tv);
        //开始上传
        OneCommunityHelper.createArticle(groupId, text, keyword, videoPatch, is_pay, asset_code, reward_price, new ProgressRequestListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {

                int uploadPercent = (int) ((100 * bytesWritten) / contentLength);
//                if (percent[0] < uploadPercent) {
                //ui层回调
                try {
                    mCirclePercentView.setProgress(uploadPercent * 3.6f);
                    mCirclePercentView.setText(uploadPercent + "%");
//                        percent[0] = uploadPercent;
                    if (uploadPercent == 100) {
//                        callBack.onConfirmClick("");
//                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                }
            }
        }, new RequestSuccessListener<MapResult>() {
            @Override
            public void onResponse(MapResult videoBeanResult) {
                mCirclePercentView.setVisibility(View.GONE);
                mUploadTipTv.setText(mContext.getString(R.string.video_upload_ok));
                mUploadTipTv.setTextColor(mContext.getResources().getColor(R.color.base_color));
                callBack.onConfirmClick("");
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    DialogUtil.simpleDialog(context, context.getString(R.string.is_sure_to_forgive_this), new ConfirmCallBackInf() {
                        @Override
                        public void onConfirmClick(String content) {
                            OneOpenHelper.cancelAllHttp();
                            dialog.dismiss();
                        }
                    });
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

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dimen_100);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.dimen_100);
        lp.dimAmount = 0.5f;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 第一次打赏提示对话框
     *
     * @param context
     * @return
     */
    public static Dialog shangTipDialog(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_shang_tip, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        SharePreferenceUtils.putObject(mContext, SharePreferenceUtils.SP_IF_FIRST_SHANG, false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dimen_200);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.dimen_110);
        lp.dimAmount = 0.5f;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 回复对话框
     *
     * @return
     */
    public static Dialog replyWeiboDialog(final BaseActivity context, final CommentBean commentBean, final String sourceWeiboId, final String weiboId, final String groupId, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_reply, null, false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        final EditText mZhuanfaEt = (EditText) layout.findViewById(R.id.zhuanfa_et);
        mZhuanfaEt.setHint(context.getString(R.string.reply) + commentBean.getNickname());
        Keyboard.changeKeyboard(context);
        mZhuanfaEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //点击发送
                    context.showLoadingDialog();
                    OneCommunityHelper.commentToArticleComment(mZhuanfaEt.getText().toString(), weiboId, commentBean.getId(), commentBean.getAccount_name(), groupId, new RequestSuccessListener<MapResult>() {
                        @Override
                        public void onResponse(MapResult mapResult) {
                            dialog.dismiss();
                            mZhuanfaEt.setText("");
                            callBack.onConfirmClick(commentBean.getId());
                        }
                    });
                    return true;
                }
                return false;

            }
        });
        View mSendTv = layout.findViewById(R.id.tv_send);
        mSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击发送
                context.showLoadingDialog();
                OneCommunityHelper.commentToArticleComment(mZhuanfaEt.getText().toString(), weiboId, commentBean.getId(), commentBean.getAccount_name(), groupId,
                        new RequestSuccessListener<MapResult>() {
                            @Override
                            public void onResponse(MapResult mapResult) {
                                dialog.dismiss();
                                mZhuanfaEt.setText("");
                                callBack.onConfirmClick(commentBean.getId());
                            }
                        });
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callBack.onConfirmClick("");
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.5f;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 支付精品课对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog payGoodVideoDialog(final Context context, final String money, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_pay_weibo, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;

        dialog.setContentView(layout);
        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        TextView mMsgTv = (TextView) layout.findViewById(R.id.dialog_msg_tv);
        TextView mTitleTv = (TextView) layout.findViewById(R.id.dialog_title_tv);
        final EditText mInputMoneyEt = (EditText) layout.findViewById(R.id.et_input_money);
        TextView mTipTv = (TextView) layout.findViewById(R.id.dialog_tip_tv);

        confirmBt.setText(context.getString(R.string.pay));
        mTitleTv.setText("支付" + money + "元");

//        mMsgTv.setText(context.getString(R.string.look_good_vieo));
        mTipTv.setVisibility(View.GONE);
        mInputMoneyEt.setVisibility(View.GONE);

        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.onConfirmClick(money + "");

                    if (dialog != null) {
                        dialog.dismiss();
                    }
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
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.drawable.tuoyuan_bg);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.pay_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.pay_dialog_height);

        lp.dimAmount = 0.5f;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 微博评论操作对话框
     *
     * @param context
     * @return
     */
    public static Dialog weiboReplyDetailDialog(final BaseActivity context, final String groupId, final String weiboId, final CommentBean commentBean, final String mHxid, final ConfirmCallBackInf confirmCallBackInf) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_weibo_reply_detail, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        final TextView replyBt = (TextView) layout.findViewById(R.id.tv_reply);

        replyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!StringUtils.equalsNull(commentBean.getId())) {
                    dialog.dismiss();
                    confirmCallBackInf.onConfirmClick(DialogUtil.REPLY);
                }
            }
        });

        final TextView copyBt = (TextView) layout.findViewById(R.id.tv_copy);

        copyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.equalsNull(commentBean.getContent()))
                    UiUtils.copy(mContext, commentBean.getContent());
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final TextView deleteBt = (TextView) layout.findViewById(R.id.tv_delete);
        if (commentBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()) || mHxid.equals(OneAccountHelper.getMeAccountName())) {
            deleteBt.setText(context.getString(R.string.delete));
            deleteBt.setVisibility(View.VISIBLE);
            copyBt.setBackgroundResource(R.drawable.base_click_bg_white);
        } else {
            deleteBt.setText(context.getString(R.string.report));
            deleteBt.setVisibility(View.GONE);
            copyBt.setBackgroundResource(R.drawable.tuoyuan_bottom_bg);
        }

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentBean.getAccount_name().equals(OneAccountHelper.getMeAccountName()) || mHxid.equals(OneAccountHelper.getMeAccountName())) {
                    context.showLoadingDialog();
                    OneCommunityHelper.deleteComment(groupId, weiboId, commentBean.getId(), new RequestSuccessListener<MapResult>() {
                        @Override
                        public void onResponse(MapResult mapResult) {
                            confirmCallBackInf.onConfirmClick(DialogUtil.DELETE);
                        }
                    });
                } else {
                    context.showLoadingDialog();
                    OneCommunityHelper.reportArticle("", commentBean.getId(), groupId, new RequestSuccessListener<MapResult>() {
                        @Override
                        public void onResponse(MapResult mapResult) {

                        }
                    });
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.tv_cancel);
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
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dimen_300);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.5f;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 微博更多操作对话框
     *
     * @param context
     * @return
     */
    public static Dialog weiboMoreDialog(final Context context, final String mWeibiId, final String mSourceWeibiId, final WeiboBean mWeibiBean, final boolean ifFinishPage, final String groupId, final ConfirmCallBackInf confirmCallBackInf) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_weibo_more, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        boolean isAdmin = false;

        final TextView shoucangBt = (TextView) layout.findViewById(R.id.tv_shoucang);
        final TextView jubaoBt = (TextView) layout.findViewById(R.id.tv_jubao);
        final TextView deleteBt = (TextView) layout.findViewById(R.id.tv_delete);
        final TextView bannedBt = (TextView) layout.findViewById(R.id.tv_banned);
        shoucangBt.setText(context.getString(R.string.article_set_essence));
        deleteBt.setText(context.getString(R.string.delete));
        jubaoBt.setText(context.getString(R.string.report));

        if (!StringUtils.equalsNull(mWeibiBean.getAccount_name()) && mWeibiBean.getAccount_name().equals(OneAccountHelper.getMeAccountName())) {
            jubaoBt.setVisibility(View.GONE);
        } else {
            deleteBt.setVisibility(View.GONE);
        }

        if (groupId != null) {
            final UserGroupInfoItem mGroupInfo = OneAccountHelper.getDatabase().getUserGroupInfoItemById(groupId, false);
            if (mGroupInfo.getGroupAdminMap() != null && mGroupInfo.getGroupAdminMap().containsKey(OneAccountHelper.getAccountId())) {
                isAdmin = true;
            } else {
                isAdmin = false;
            }

            if (StringUtils.equals(mGroupInfo.owner, OneAccountHelper.getAccountId())) {
                if (!StringUtils.equalsNull(mWeibiBean.getAccount_name()) && mWeibiBean.getAccount_name().equals(OneAccountHelper.getMeAccountName())) {
                    jubaoBt.setVisibility(View.GONE);
                    bannedBt.setVisibility(View.GONE);
                } else {
                    jubaoBt.setVisibility(View.VISIBLE);
                    bannedBt.setVisibility(View.VISIBLE);
                }
                shoucangBt.setVisibility(View.VISIBLE);
                deleteBt.setVisibility(View.VISIBLE);
            } else if (isAdmin) {
                if (!StringUtils.equalsNull(mWeibiBean.getAccount_name()) && mWeibiBean.getAccount_name().equals(OneAccountHelper.getMeAccountName())) {
                    jubaoBt.setVisibility(View.GONE);
                    bannedBt.setVisibility(View.GONE);
                } else {
                    jubaoBt.setVisibility(View.VISIBLE);
                    bannedBt.setVisibility(View.VISIBLE);
                }
                shoucangBt.setVisibility(View.VISIBLE);
                deleteBt.setVisibility(View.VISIBLE);
            } else {
                bannedBt.setVisibility(View.GONE);
                shoucangBt.setVisibility(View.GONE);
                jubaoBt.setBackgroundResource(R.drawable.tuoyuan_bg);
                deleteBt.setBackgroundResource(R.drawable.tuoyuan_bg);
            }
        }

        bannedBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneGroupHelper.muteMember(mWeibiBean.getGroup_uid(), mWeibiBean.getAccount_id(), new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (result != null) {
                            ToastUtils.simpleToast(R.string.mute_member_success);
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final TextView zhuanfaBt = (TextView) layout.findViewById(R.id.tv_zhuanfa);

        zhuanfaBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!StringUtils.equalsNull(mSourceWeibiId)) {
                    dialog.dismiss();
//                    DialogUtil.showZhuanDialog((BaseActivity) context, mSourceWeibiId, mWeibiId, new DialogUtil.ConfirmCallBackInf() {
//                        @Override
//                        public void onConfirmClick(String content) {
//                            confirmCallBackInf.onConfirmClick("");
//                        }
//                    });
                }
            }
        });

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneCommunityHelper.deleteArticle(mWeibiId, mWeibiBean.getGroup_uid(), new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult mapResult) {
//                            EventBus.getDefault().post(new DeleteWeiboEvent());
                        if (mapResult != null) {
                            if (ifFinishPage)
                                ((BaseActivity) context).finish();
                            WeiboCatchModel.getDeleteWeiboMap().put(mWeibiId, mWeibiId);
                            confirmCallBackInf.onConfirmClick(CALL_BACK_TYPE_DELETE);
                        }
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        jubaoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneCommunityHelper.reportArticle("", mWeibiId, groupId, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult mapResult) {
                        if (mapResult != null) {
                            ToastUtils.simpleToast(R.string.article_report_success);
                        }
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        shoucangBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OneCommunityHelper.essenceArticle(mWeibiId, groupId, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (result != null) {
                            WeiboCatchModel.getJinghuaWeiboMap().put(mWeibiId, mWeibiId);
                            if (confirmCallBackInf != null)
                                confirmCallBackInf.onConfirmClick(CALL_BACK_TYPE_ADD_JINGHUA);
                            mWeibiBean.setWeibo_jinghua(CommonConstants.TRUE_VALUE);
                            ToastUtils.simpleToast(context.getString(R.string.article_essence_success));
                        }
                    }
                });

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final TextView shareBt = (TextView) layout.findViewById(R.id.tv_share);

        if (!StringUtils.equalsNull(mWeibiBean.getType()) && mWeibiBean.getType().equals(CommonConstants.WEIBO_TYPE_VIDEO)) {
//            shareBt.setVisibility(View.VISIBLE);
            shareBt.setVisibility(View.GONE);
        } else {
            shareBt.setVisibility(View.GONE);
        }
        shareBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null) {
                    dialog.dismiss();
                    //分享
                    String title = "【" + mWeibiBean.getNickname() + "】";
//                    if (StringUtils.equalsNull(mWeibiBean.getContent()))
//                        title = title + context.getString(R.string.share_video_title);
//                    else
//                        title = title + mWeibiBean.getContent();
//                    ShareUtils.shareVideo((Activity) context, NetConstants.SHARE_VIDEO_URL + mSourceWeibiId, title);

                }
            }
        });

        final TextView copyBt = (TextView) layout.findViewById(R.id.tv_copy);

        if (StringUtils.equalsNull(mWeibiBean.getContent()))
            copyBt.setVisibility(View.GONE);
        else
            copyBt.setVisibility(View.VISIBLE);
        copyBt.setVisibility(View.GONE);

        copyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.equalsNull(mWeibiBean.getContent()))
                    UiUtils.copy(mContext, mWeibiBean.getContent());
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.tv_cancel);
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
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dimen_300);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.5f;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 点击头像弹出对话框
     *
     * @return
     */
    public static Dialog chooceGroupUserAvatarDialog(final Context context, final String otherAccountId, final String groupUid, boolean isAdmin, final DialogUtil.ConfirmCallBackInf listener) {
        final String groupAdmin = "ADMIN";

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_group_option_menu, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        final TextView setAsManagerBt = (TextView) layout.findViewById(R.id.tv_set_as_manager);
        final TextView bannedToPostBt = (TextView) layout.findViewById(R.id.tv_banned_to_post);
        final TextView checkCardBt = (TextView) layout.findViewById(R.id.tv_check_card);
        final TextView informOthersBt = (TextView) layout.findViewById(R.id.tv_inform_others);
        final TextView groupDeleteBt = (TextView) layout.findViewById(R.id.tv_group_delete);
        TextView addBlackList = (TextView) layout.findViewById(R.id.tv_add_blacklist);
        View lineOne = layout.findViewById(R.id.view_one);
        View lineTwo = layout.findViewById(R.id.view_two);
        View lineThree = layout.findViewById(R.id.view_three);
        informOthersBt.setText("@TA");


        if (!isAdmin) {
            setAsManagerBt.setVisibility(View.GONE);
            bannedToPostBt.setVisibility(View.GONE);
            groupDeleteBt.setVisibility(View.GONE);
            addBlackList.setVisibility(View.GONE);
            lineOne.setVisibility(View.GONE);
            lineTwo.setVisibility(View.GONE);
            lineThree.setVisibility(View.GONE);
        }

        informOthersBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirmClick(CALL_BACK_TYPE_AT_USER);
                    Keyboard.changeKeyboard((BaseActivity) context);
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        setAsManagerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneGroupHelper.addMemberToAdminList(groupUid, groupAdmin, otherAccountId, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (result != null) {
                            if (listener != null) {
                                listener.onConfirmClick(CALL_BACK_TYPE_ADD_ADMIN);
                            }
                            ToastUtils.simpleToast(R.string.set_admin_success);
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        bannedToPostBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneGroupHelper.muteMember(groupUid, otherAccountId, new RequestSuccessListener<MapResult>() {
                    @Override
                    public void onResponse(MapResult result) {
                        if (result != null) {
                            ToastUtils.simpleToast(R.string.mute_member_success);
                        } else {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        checkCardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpAppPageUtil.jumpOtherUserInfoPage(context, otherAccountId);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        groupDeleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.simpleDialog(context, context.getString(R.string.sure_delete_user), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        List<String> deleteMemberIds = new ArrayList<>();
                        deleteMemberIds.add(otherAccountId);

                        //更新群聊请求
                        OneGroupHelper.removeOccupants(groupUid, deleteMemberIds, new RequestSuccessListener<Boolean>() {
                            @Override
                            public void onResponse(Boolean result) {
                                if (result) {
                                    ToastUtils.simpleToast(R.string.delete_success);
                                } else {
                                    ToastUtils.simpleToast(R.string.delete_failed);
                                }
                            }
                        });
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        addBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.simpleDialog(context, context.getString(R.string.sure_delete_user), new DialogUtil.ConfirmCallBackInf() {
                    @Override
                    public void onConfirmClick(String content) {
                        List<String> deleteMemberIds = new ArrayList<>();
                        deleteMemberIds.add(otherAccountId);

                        //更新群聊请求
                        OneGroupHelper.addMemberToBlackList(groupUid, deleteMemberIds, new RequestSuccessListener<Boolean>() {
                            @Override
                            public void onResponse(Boolean result) {
                                if (result) {
                                    ToastUtils.simpleToast(R.string.add_member_to_blacklist_success);
                                } else {
                                    ToastUtils.simpleToast(R.string.delete_failed);
                                }
                            }
                        });
                    }
                });
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        final TextView cancelBt = (TextView) layout.findViewById(R.id.tv_cancel);
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
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 选择资产类型对话框
     * 输入密码对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog selectAssetDialog(final Context context, final List<AssetInfo> assetInfoList, final ConfirmCallBackObject<String> callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_select_asset, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);
        layout.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
        final ListView assetLv = (ListView) layout.findViewById(R.id.dialog_asset_lv);
        final EditText mSearchEt = (EditText) layout.findViewById(R.id.et_search);
        layout.findViewById(R.id.tv_recharge).setVisibility(View.GONE);
        mSearchEt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                final SelectAssetAdapter adapter;
                if (s.length() > 0) {
                    String str_s = mSearchEt.getText().toString().trim().toLowerCase();
                    List<AssetInfo> tempCoinList = ListUtils.searchAssetInfoList(assetInfoList, str_s);

                    adapter = new SelectAssetAdapter(context, tempCoinList, str_s);
                    assetLv.setAdapter(adapter);
                } else {
                    adapter = new SelectAssetAdapter(context, assetInfoList, "");
                    assetLv.setAdapter(adapter);
                }
                assetLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (callBack != null) {
                            callBack.onConfirmClick(adapter.getItem(i).getAsset_code());
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        if (assetInfoList != null) {
            final SelectAssetAdapter mAdapter = new SelectAssetAdapter(context, assetInfoList, "");
            assetLv.setAdapter(mAdapter);
            assetLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (callBack != null) {
                        callBack.onConfirmClick(mAdapter.getItem(i).getAsset_code());
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }
            });
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.select_asset_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.select_asset_dialog_height);
        lp.dimAmount = 0;
        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 输入关键词对话框
     *
     * @param context
     * @param callBack
     * @return
     */
    public static Dialog inputKeywordDialog(final Context context, final ConfirmCallBackInf callBack) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_input_keyword, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionToastDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        final EditText mEditText = (EditText) layout.findViewById(R.id.dialog_edit_et);

        final TextView confirmBt = (TextView) layout.findViewById(R.id.btn_commit);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.equalsNull(mEditText.getText().toString().trim())) {
                    UiUtils.closeKeybord(mEditText, context);
                    if (callBack != null) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        callBack.onConfirmClick(mEditText.getText().toString());
                    }
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.btn_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.closeKeybord(mEditText, context);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        final Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        lp.width = context.getResources().getDimensionPixelSize(R.dimen.edit_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.edit_dialog_height);
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    /**
     * 长摁大图提示框
     *
     * @return
     */
    public static Dialog longClickBigImageDialog(final Activity context, final Uri uri, final Bitmap mBitmap) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_big_image, null,
                false);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        mContext = context;
        dialog.setContentView(layout);

        TextView headingCodeBt = (TextView) layout.findViewById(R.id.tv_heading_code);
        headingCodeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                        QrUtils.analyzeBitmap(bitmap, new QrUtils.AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                if (result != null) {
                                    JumpAppPageUtil.detailIntentDataJump((BaseActivity) context, result, true);
                                }

                            }

                            @Override
                            public void onAnalyzeFailed() {
                                ToastUtils.simpleToast(R.string.erro);
                            }
                        });
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (mBitmap != null) {
                    QrUtils.analyzeBitmap(mBitmap, new QrUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            if (result != null) {
                                JumpAppPageUtil.detailIntentDataJump((BaseActivity) context, result, true);
                            }
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            ToastUtils.simpleToast(R.string.erro);
                        }
                    });
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });

        TextView saveBt = (TextView) layout.findViewById(R.id.tv_save);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null && new File(uri.getPath()).exists()) {
                    BaseUtils.inertImageToPhone(new File(uri.getPath()));
                    ToastUtils.simpleToast(R.string.save_to_phone_success);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    File file = BaseUtils.saveBitmapFile(mBitmap, TimeUtils.getTrueTime() + ImageUtils.DEFAULT_IMAGE_FORMAT);
                    BaseUtils.inertImageToPhone(file);
                    ToastUtils.simpleToast(R.string.save_to_phone_success);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });

        final TextView cancelBt = (TextView) layout.findViewById(R.id.tv_cancel);
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
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = DEFAULT_DIALOG_DIMAMOUNT;

        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.color.toumin);

        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

}

