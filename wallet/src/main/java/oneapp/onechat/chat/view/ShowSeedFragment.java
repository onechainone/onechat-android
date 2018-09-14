package oneapp.onechat.chat.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oneapp.onechat.androidapp.R;
import oneapp.onechat.chat.utils.jump.JumpAppPageUtil;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.chatsdk.OneAccountHelper;
import oneapp.onechat.oneandroid.graphenechain.utils.BtsHelper;
import oneapp.onechat.oneandroid.onemessage.RpcCallProxy;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onewallet.Constants;
import oneapp.onechat.oneandroid.onewallet.util.BaseUtils;
import oneapp.onechat.oneandroid.onewallet.util.Fonts;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import oneapp.onechat.oneandroid.onewallet.util.WeakHandler;
import oneapp.onecore.graphenej.Util;
import onewalletui.ui.BaseActivity;
import onewalletui.ui.DialogBuilder;
import onewalletui.ui.Dialogs;
import onewalletui.ui.UnlockWalletDialog;
import onewalletui.util.qrcode.QrUtils;

/**
 * @author John L. Jegutanis
 */
public class ShowSeedFragment extends Fragment implements View.OnClickListener {
    private static final Logger log = LoggerFactory.getLogger(ShowSeedFragment.class);

    private static final int UPDATE_VIEW = 0;
    private static final int SET_PASSWORD = 1;
    private static final int SET_SEED = 2;

    private static final String SEED_PROCESSING_DIALOG_TAG = "seed_processing_dialog_tag";
    private static final String PASSWORD_DIALOG_TAG = "password_dialog_tag";

    private View seedLayout;
    private View seedEncryptedLayout;
    private TextView seedView;
    private TextView onePrivateKeyView;
    private View seedPasswordProtectedView;
    private ImageView qrView;

    private Button btnCopySave;
    Boolean isCopyKey = false;
    Boolean isBackupKey = false;

    private ImageView mBackIv;
    private TextView mTitleTv;

    private Listener listener;

    private CharSequence password;
    private SeedInfo seedInfo;

    private final Handler handler = new MyHandler(this);

    //是否第一次进入
    public boolean ifFirstCreate = false;
    private TextView brainkey_recover;
    private TextView pass_brainkey_recover;
    private View brainkey_recover_view, pass_brainkey_recover_view;
    private LinearLayout encrypt_brainkey_ll;
    private ScrollView brainkey_sv;
    private LinearLayout title_brainkey;

    private TextView mSaveFileTv, mSavePathTv;
    private BaseActivity activity;

    public static ShowSeedFragment newInstance(boolean ifFirstCreate) {
        ShowSeedFragment fragment = new ShowSeedFragment();
        fragment.ifFirstCreate = ifFirstCreate;
        return fragment;
    }

    private static class MyHandler extends WeakHandler<ShowSeedFragment> {
        public MyHandler(ShowSeedFragment ref) {
            super(ref);
        }

        @Override
        protected void weakHandleMessage(ShowSeedFragment ref, Message msg) {
            switch (msg.what) {
                case SET_SEED:
                    ref.seedInfo = (SeedInfo) msg.obj;
                    ref.updateView();
                    break;
                case SET_PASSWORD:
                    ref.setPassword((CharSequence) msg.obj);
                    break;
                case UPDATE_VIEW:
                    ref.updateView();
                    break;
            }
        }
    }

    public void setPassword(CharSequence password) {
        this.password = password;
        if (OneAccountHelper.checkPassword(password.toString())) {
            seedInfo = new SeedInfo();
            seedInfo.seedString = OneAccountHelper.getBrainKey();
            seedInfo.isSeedPasswordProtected = false;

            updateView();
        } else {
            DialogBuilder.warn(getContext(), sdk.android.onechatui.R.string.unlocking_wallet_error_title)
                    .setMessage(sdk.android.onechatui.R.string.unlocking_wallet_error_detail)
                    .setNegativeButton(sdk.android.onechatui.R.string.button_cancel, null)
                    .setPositiveButton(sdk.android.onechatui.R.string.button_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showUnlockDialog();
                        }
                    }).create().show();
        }
        RpcCallProxy.getInstance().savePassword(password.toString());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true); // for the async task
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_seed, container, false);
        activity = (BaseActivity) getActivity();
        seedLayout = view.findViewById(R.id.show_seed_layout);
        seedEncryptedLayout = view.findViewById(R.id.seed_encrypted_layout);
//        seedEncryptedLayout.setVisibility(View.GONE);
        // Hide layout as maybe we have to show the password dialog
        seedLayout.setVisibility(View.GONE);

        seedView = (TextView) view.findViewById(R.id.seed);
        seedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCopy();
            }
        });

        onePrivateKeyView = (TextView) view.findViewById(R.id.one_private_key);

        seedPasswordProtectedView = view.findViewById(R.id.seed_password_protected);
        Fonts.setTypeface(view.findViewById(R.id.seed_password_protected_lock), Fonts.Font.ONEAPP_FONT_ICONS);
        qrView = (ImageView) view.findViewById(R.id.qr_code_seed);

        btnCopySave = (Button) view.findViewById(R.id.btn_copy_save);
        btnCopySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifFirstCreate) {
                    // 更新所有未读消息为已读
                    OneAccountHelper.getDatabase().cleanAllUserChatStatusRead(
                            ItemMessage.StatusListen.READ.ordinal());
                    OneAccountHelper.getDatabase().clearAllUserConversationUnreadNum();

//                    if (Helper.fetchBoolianSharePref(getActivity(), CommonConstants.PREF_IF_SAVE_WALLET_SEED)) {
                    JumpAppPageUtil.jumpMainPage(getActivity());
//                    } else {
//                        DialogUtil.simpleDialog(getActivity(), getString(R.string.make_sure_save_seed), new DialogUtil.ConfirmCallBackInf() {
//                            @Override
//                            public void onConfirmClick(String content) {
//                                JumpAppPageUtil.jumpMakeSureSeedPage(getActivity(), seedInfo.seedString);
////                            getActivity().finish();
//                                onSave();
//                            }
//                        });
//                    }
                } else {
//                    JumpAppPageUtil.jumpMakeSureSeedPage(getActivity(), seedInfo.seedString);
                    getActivity().finish();
                }
            }
        });


        TextView lockIcon = (TextView) view.findViewById(R.id.lock_icon);
//        Fonts.setTypeface(lockIcon, Fonts.Font.ONEAPP_FONT_ICONS);
        lockIcon.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                showUnlockDialog();
            }
        });

        updateView();

        mBackIv = (ImageView) view.findViewById(R.id.img_back);
        if (!ifFirstCreate)
            mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        mTitleTv = (TextView) view.findViewById(R.id.txt_title);
        mTitleTv.setText(getString(R.string.title_activity_backup_seed));

        //验证控件
        brainkey_recover = (TextView) view.findViewById(R.id.brainkey_recover);
        pass_brainkey_recover = (TextView) view.findViewById(R.id.pass_brainkey_recover);
        encrypt_brainkey_ll = (LinearLayout) view.findViewById(R.id.encrypt_brainkey_ll);
        brainkey_sv = (ScrollView) view.findViewById(R.id.brainkey_sv);
        brainkey_recover_view = view.findViewById(R.id.view_brainkey_recover);
        pass_brainkey_recover_view = view.findViewById(R.id.view_pass_brainkey_recover);

        //加密验证新控件
        brainkey_recover.setOnClickListener(this);
        pass_brainkey_recover.setOnClickListener(this);

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                onCopy();
                ToastUtils.simpleToast(R.string.copied_to_clipboard);
                return super.onDoubleTap(e);
            }
        });

        title_brainkey = (LinearLayout) view.findViewById(R.id.title_brainkey);
        title_brainkey.setVisibility(View.GONE);
        mSaveFileTv = (TextView) view.findViewById(R.id.tv_save_seed_file);
        mSaveFileTv.setOnClickListener(this);
        mSavePathTv = (TextView) view.findViewById(R.id.tv_seed_path);
        mSavePathTv.setText(Constants.SAVE_FILE_NAME + BtsHelper.mMeAccountName + Constants.SAVE_SEED_FILE_NAME);

        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.brainkey_recover) {
            brainkey_recover_view.setVisibility(View.VISIBLE);
            pass_brainkey_recover_view.setVisibility(View.GONE);
            pass_brainkey_recover.setAlpha(0.7f);
            pass_brainkey_recover.setBackgroundResource(R.color.mnemonic_recovery_background);
            brainkey_recover.setAlpha(1f);
            brainkey_recover.setBackgroundResource(R.color.base_bg_color_level1);
            brainkey_sv.setVisibility(View.VISIBLE);
            encrypt_brainkey_ll.setVisibility(View.GONE);

        } else if (i == R.id.pass_brainkey_recover) {
            brainkey_recover_view.setVisibility(View.GONE);
            pass_brainkey_recover_view.setVisibility(View.VISIBLE);
            brainkey_recover.setAlpha(0.7f);
            brainkey_recover.setBackgroundResource(R.color.mnemonic_recovery_background);
            pass_brainkey_recover.setAlpha(1f);
            pass_brainkey_recover.setBackgroundResource(R.color.base_bg_color_level1);
            brainkey_sv.setVisibility(View.GONE);
            encrypt_brainkey_ll.setVisibility(View.VISIBLE);

        } else if (i == R.id.tv_save_seed_file) {
            activity.checkPermission(new BaseActivity.CheckPermListener() {
                @Override
                public void superPermission() {
                    String encryptString = Util.bytesToHex(Util.encryptAES(BtsHelper.getDefaultAccount().brain_key.getBytes(Charsets.UTF_8), BtsHelper.getMePasswordBackend().getBytes(Charsets.UTF_8)));
                    BaseUtils.saveStringToSD(encryptString, BtsHelper.mMeAccountName + Constants.SAVE_SEED_FILE_NAME);

                    SharePreferenceUtils.putObject(SharePreferenceUtils.SP_DERIVE_BRAINKEY, false);
                    ToastUtils.simpleToast(R.string.export_seed_success);
                }
            }, R.string.file, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
    }

    private void onCopy() {
        try {
            if (ConfigConstants.DEBUG) {
                UiUtils.copy(getActivity(), seedInfo.seedString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSave() {
        try {
            if (!isBackupKey) {
                isBackupKey = true;
            }

            Toast.makeText(getActivity(), R.string.action_have_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUnlockDialog() {
        Dialogs.dismissAllowingStateLoss(getFragmentManager(), PASSWORD_DIALOG_TAG);
        UnlockWalletDialog.getInstance().show(getFragmentManager(), PASSWORD_DIALOG_TAG);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " + Listener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void updateView() {
        if (seedInfo != null) {
            title_brainkey.setVisibility(View.VISIBLE);
            seedLayout.setVisibility(View.VISIBLE);
            seedEncryptedLayout.setVisibility(View.GONE);
            seedView.setText(seedInfo.seedString);
            onePrivateKeyView.setText(BtsHelper.mMeWif);

            seedView.setEnabled(true);
            seedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCopy();
                }
            });
            QrUtils.setQr(qrView, getResources(), seedInfo.seedString);
            if (seedInfo.isSeedPasswordProtected) {
                seedPasswordProtectedView.setVisibility(View.VISIBLE);
            } else {
                seedPasswordProtectedView.setVisibility(View.GONE);
            }
        } else {
            showUnlockDialog();
        }
    }


    private static class SeedInfo {
        String seedString;
        boolean isSeedPasswordProtected;
    }

    public interface Listener extends UnlockWalletDialog.Listener {
        void onSeedNotAvailable();
    }
}
