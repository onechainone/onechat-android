package onemessageui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import onewalletui.util.jump.JumpAppPageUtil;
import onewalletui.util.jump.JumpParamsContants;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onewallet.Constants;
import onewalletui.ui.adaptors.SeedItemWordAdapter;
import onewalletui.ui.widget.flowtag.Evaluate;
import onewalletui.ui.widget.flowtag.FlowTagView;
import oneapp.onechat.oneandroid.onewallet.util.SharePreferenceUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.view.OneBaseActivity;


/**
 * @author John L. Jegutanis
 */
public class MakeSureSeedActivity extends OneBaseActivity implements View.OnClickListener {

    private String mMySeed;

    private TextView mSubmitTv;

    private TextView txt_title;
    private ImageView img_back;

    private FlowTagView mAllWordsView, mSelectWordsView;
    private SeedItemWordAdapter allWordsAdapter, selectWordsAdapter;
    private List<Evaluate> selectWordsList = new ArrayList<>();

    private List<Evaluate> allWordsList = new ArrayList<>();
    private TextView skip_tv;
    private ImageView iv_img;
    private LinearLayout ll_word;
    private boolean ifHaveSaveSeed = false;
    private boolean firstSeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ifCanScreenShot = false;
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_make_sure_seed);
        readArguments();
    }

    private void hideView() {
        if (!firstSeed && !ifHaveSaveSeed) {
            iv_img.setVisibility(View.GONE);
            skip_tv.setVisibility(View.GONE);
            ll_word.setVisibility(View.GONE);
        } else {
            iv_img.setVisibility(View.VISIBLE);
//            if (ConfigConstants.DEBUG) {
//                skip_tv.setVisibility(View.VISIBLE);
//            } else {
//                skip_tv.setVisibility(View.VISIBLE);
//            }
            skip_tv.setVisibility(View.VISIBLE);
            ll_word.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        mAllWordsView = (FlowTagView) this.findViewById(R.id.tag_all);
        mSelectWordsView = (FlowTagView) this.findViewById(R.id.tag_select);
        skip_tv = (TextView) findViewById(R.id.skip_tv);
        mSubmitTv = (TextView) findViewById(R.id.btn_submit);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        ll_word = (LinearLayout) findViewById(R.id.ll_word);
        if (SharePreferenceUtils.contains(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED)) {
            ifHaveSaveSeed = (boolean) SharePreferenceUtils.getObject(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED);
        }
    }

    @Override
    protected void initView() {

        hideView();
    }

    private void readArguments() {
        final Intent intent = this.getIntent();
        final Serializable sear = intent
                .getSerializableExtra(JumpParamsContants.INTENT_PARAMS_MAP);
        if (sear != null) {
            final HashMap<String, Object> paramMap = (HashMap<String, Object>) sear;
            this.mMySeed = (String) paramMap.get(JumpParamsContants.INTENT_SEED);
            firstSeed = (boolean) paramMap.get(JumpParamsContants.INTENT_FIRST_SEED);
        }
    }


    @Override
    protected void initData() {
        txt_title.setText(getString(R.string.make_sure_seed));

        img_back.setVisibility(View.VISIBLE);

        selectWordsAdapter = new SeedItemWordAdapter(this);
        selectWordsAdapter.setBackGround(R.drawable.tuoyuan_seed_choose_bg, R.drawable.tuoyuan_seed_choose_bg);
        selectWordsAdapter.setTextColor(R.color.base_color, R.color.base_color);
        mSelectWordsView.setAdapter(selectWordsAdapter);

        allWordsAdapter = new SeedItemWordAdapter(this);
        mAllWordsView.setAdapter(allWordsAdapter);

        String[] seedItems = mMySeed.split(" ");
        if (seedItems == null) {
            return;
        }
        for (String itemWord : seedItems) {
            allWordsList.add(new Evaluate(itemWord, false));
        }
        // 打乱顺序
        Collections.shuffle(allWordsList);
        allWordsAdapter.setItems(allWordsList);

    }

    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        skip_tv.setOnClickListener(this);
        mAllWordsView.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                Evaluate e = (Evaluate) allWordsAdapter.getItem(position);
                e.setIs_choosed(!e.isIs_choosed());
                if (e.isIs_choosed()) {
                    selectWordsList.add(e);
                } else {
                    selectWordsList.remove(e);
                }
                selectWordsAdapter.setItems(selectWordsList);
                allWordsAdapter.notifyDataSetChanged();
                if (selectWordsList.size() == allWordsList.size()) {
                    mSubmitTv.setAlpha(1);
                    mSubmitTv.setEnabled(true);
                } else {
                    mSubmitTv.setAlpha(Constants.DEFAULT_CAN_NOT_CLICK_ALPHA);
                    mSubmitTv.setEnabled(false);
                }
            }
        });

        mSelectWordsView.setItemClickListener(new FlowTagView.TagItemClickListener() {
            @Override
            public void itemClick(int position) {
                Evaluate e = (Evaluate) selectWordsAdapter.getItem(position);
                e.setIs_choosed(!e.isIs_choosed());
                if (e.isIs_choosed()) {
                    selectWordsList.add(e);
                } else {
                    selectWordsList.remove(e);
                }
                selectWordsAdapter.setItems(selectWordsList);
                allWordsAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.btn_submit) {
            if (checkSelectSeed()) {
                if (firstSeed == true) {
                    SharePreferenceUtils.putObject(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED, true);
//                        Helper.storeBoolianSharePref(WalletApplication.getInstance(), CommonConstants.PREF_IF_SAVE_WALLET_SEED, true);
                } else {
                    SharePreferenceUtils.putObject(SharePreferenceUtils.SP_IFHAVE_SAVE_SEED, true);
                    finish();
                }
            } else {
                ToastUtils.simpleToast(R.string.make_sure_seed_error_tip);
            }


        } else if (i == R.id.skip_tv) {
            firstSeed = false;
        }
    }

    private boolean checkSelectSeed() {
        String selectWords = "";
        for (Evaluate word : selectWordsList) {
            selectWords += word.getName();
        }
        return selectWords.trim().equals(mMySeed.replace(" ", ""));
    }
}
