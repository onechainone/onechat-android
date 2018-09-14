package onemessageui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Splitter;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

import oneapp.onechat.oneandroid.onewallet.util.WalletUtils;
import onemessageui.widght.SideBar;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import oneapp.onechat.oneandroid.onewallet.Constants;
import onewalletui.ui.widget.flowtag.FlowTagView;
import oneapp.onechat.oneandroid.onewallet.util.Keyboard;
import oneapp.onechat.oneandroid.onewallet.util.ListUtils;
import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import oneapp.onechat.oneandroid.onewallet.util.UiUtils;
import onemessageui.adpter.InputSeedWordAdapter;
import onemessageui.adpter.SeedWordAdapter;
import onemessageui.view.OneBaseActivity;
import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;


public class InputSeedActivity extends OneBaseActivity implements OnClickListener {
    private ImageView img_back;
    private TextView txt_title, txt_right;

    private TextView mPastTv;

    private EditText et_search;
    private SideBar indexBar;
    private TextView mDialogText;
    private WindowManager mWindowManager;

    private InputSeedWordAdapter selectWordsAdapter;
    private List<String> selectWordsList = new ArrayList<>();
    private FlowTagView selectSeedFlowView;

    private SeedWordAdapter seedWordAdapter;
    private List<String> allWordList;// 助记词列表
    private ListView listView;
    private boolean ifForResult;
    private String intentSeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void start() {
        setContentView(R.layout.activity_input_seed);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mDialogText);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(R.string.choose_seed);
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_right.setText(R.string.action_ok);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        et_search = (EditText) this.findViewById(R.id.et_search);
        listView = (ListView) findViewById(R.id.list);
        mDialogText = (TextView) LayoutInflater.from(this).inflate(
                R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        indexBar = (SideBar) findViewById(R.id.sideBar);
        indexBar.setListView(listView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
        indexBar.setVisibility(View.GONE);

        selectSeedFlowView = (FlowTagView) findViewById(R.id.seed_select);

        mPastTv = (TextView) findViewById(R.id.tv_paste);
    }

    @Override
    protected void initView() {
        if (ConfigConstants.DEBUG) {
            mPastTv.setVisibility(View.VISIBLE);
        } else {
            mPastTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {

        allWordList = MnemonicCode.INSTANCE.getWordList();

        seedWordAdapter = new SeedWordAdapter(InputSeedActivity.this,
                allWordList);

        listView.setAdapter(seedWordAdapter);

        selectWordsAdapter = new InputSeedWordAdapter(context);
        selectSeedFlowView.setAdapter(selectWordsAdapter);
//        SharedPreferences usersign = getSharedPreferences("usersign", MODE_PRIVATE);
//        sign = usersign.getBoolean("sign", true);
        if (getIntent() != null) {
            ifForResult = getIntent().getBooleanExtra(JumpParamsContants.INTENT_IF_FOR_RESULT, false);
            intentSeed = getIntent().getStringExtra(JumpParamsContants.INTENT_INPUT_SEED);
        }

        if (!StringUtils.equalsNull(intentSeed)) {
            List<String> strings = Splitter.on(" ").trimResults().splitToList(intentSeed);
            selectWordsList.addAll(strings);
            selectWordsAdapter.setItems(selectWordsList);
        }

    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        txt_right.setOnClickListener(this);
        findViewById(R.id.tv_clear).setOnClickListener(this);
        mPastTv.setOnClickListener(this);
        selectSeedFlowView.setOnClickListener(this);

        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim().toLowerCase();
                    List<String> seeds_temp = ListUtils.searchStringList(allWordList, str_s);

                    seedWordAdapter = new SeedWordAdapter(
                            InputSeedActivity.this, seeds_temp, str_s);
                    listView.setAdapter(seedWordAdapter);
                } else {
                    seedWordAdapter = new SeedWordAdapter(
                            InputSeedActivity.this, allWordList);
                    listView.setAdapter(seedWordAdapter);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectWordsList.size() >= Constants.SEED_WORD_NUM2) {
                    ToastUtils.simpleToast(R.string.over_seed_word_num_two);
                } else {
                    String seedWord = seedWordAdapter.getItem(i);
                    selectWordsList.add(seedWord);
                    selectWordsAdapter.setItems(selectWordsList);
                }
                et_search.setText("");
            }
        });

        selectSeedFlowView.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                String seedWord = selectWordsAdapter.getItem(position);
                selectWordsList.remove(seedWord);
                selectWordsAdapter.setItems(selectWordsList);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            Utils.finish(InputSeedActivity.this);

        } else if (i == R.id.txt_right) {//确定
            if (selectWordsList.size() != Constants.DEFAULT_SEED_WORD_NUM && selectWordsList.size() != Constants.SEED_WORD_NUM2 && selectWordsList.size() != Constants.SEED_WORD_NUM3) {
                ToastUtils.simpleToast(R.string.over_seed_word_num_two);
            } else {
                String seed = WalletUtils.mnemonicToString(selectWordsList);
                if (checkSeed(seed)) {
                    if (ifForResult) {
                        setResult(Constants.RESULT_CODE_SEED_WORD,
                                new Intent().putExtra(JumpParamsContants.INTENT_SEED, seed));

                    } else {
                        JumpAppPageUtil.jumpCreateSeedPage(context, seed, true);
                    }

                    finish();
//                    Helper.storeBoolianSharePref(context, CommonConstants.PREF_IF_SAVE_WALLET_SEED, true);
                }
            }

        } else if (i == R.id.tv_clear) {
            selectWordsList.clear();
            selectWordsAdapter.setItems(selectWordsList);

        } else if (i == R.id.tv_paste) {//粘贴
            String pasteString = UiUtils.getPasteString(context);
            if (!StringUtils.equalsNull(pasteString)) {
//                    if (!MnemonicCode.INSTANCE.check(pasteString)) {
                List<String> pasteWordsList = Splitter.on(" ").trimResults().splitToList(pasteString);
                if (pasteWordsList != null && pasteWordsList.size() > 1) {
                    selectWordsList.clear();
                    selectWordsList.addAll(Splitter.on(" ").trimResults().splitToList(pasteString));
                    selectWordsAdapter.setItems(selectWordsList);
                }
//                    }
            } else {
                ToastUtils.simpleToast(R.string.pasteboard_is_null);
            }

        } else if (i == R.id.seed_select) {
            Keyboard.changeKeyboard(this);

        } else {
        }
    }

    /**
     * 检查seed
     *
     * @return
     */
    private boolean checkSeed(String seed) {
        String errorString = null;
        if (StringUtils.equalsNull(seed)) {
            errorString = getString(R.string.please_enter_brainkey);
        } else if (!MnemonicCode.INSTANCE.check(seed)) {
            errorString = getString(R.string.error_invalid_account);
        }
        if (!StringUtils.equalsNull(errorString)) {
            ToastUtils.simpleToast(errorString);
            return false;
        } else {
            return true;
        }
    }

}
