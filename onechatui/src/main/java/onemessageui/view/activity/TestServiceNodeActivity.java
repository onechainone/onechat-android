package onemessageui.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.modle.ServiceBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import onemessageui.adpter.SelectServiceNodeAdapter;
import onemessageui.view.OneBaseActivity;
import pl.droidsonroids.gif.GifDrawable;
import sdk.android.onechatui.R;

public class TestServiceNodeActivity extends OneBaseActivity implements View.OnClickListener {
    private TextView mTipTv, mJumpPassTv;
    ImageView gifImageView;
    GifDrawable gifDrawable;
    private ListView mlistview;
    List<ServiceBean> serviceBeanList;
    private SelectServiceNodeAdapter mAdapter;

    private final MyHandler handler = new MyHandler();
    private static final int CHECK_SERVICES_STATUS = 1;
    private TextView switch_service_node;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void start() {
        setContentView(R.layout.activity_test_service);
    }

    @Override
    protected void initControl() {

        mTipTv = (TextView) findViewById(R.id.txt_tip);
        mJumpPassTv = (TextView) findViewById(R.id.txt_jump_pass);

        gifImageView = (ImageView) findViewById(R.id.loading_gif_icon);
        mlistview = (ListView) findViewById(R.id.listview);
        switch_service_node = (TextView) findViewById(R.id.switch_service_node);
    }

    @Override
    protected void initView() {
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.loading_test_service);
            gifImageView.setImageDrawable(gifDrawable);
            gifDrawable.setLoopCount(Character.MAX_VALUE);
            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        mTipTv.setText(getString(R.string.config_service_node_ing));
        mJumpPassTv.setVisibility(View.INVISIBLE);
        gifImageView.setVisibility(View.VISIBLE);

        serviceBeanList = ServiceConstants.GetAllServiceList();
        if (mAdapter == null) {
            mAdapter = new SelectServiceNodeAdapter(context, serviceBeanList, true);
            mlistview.setAdapter(mAdapter);
        } else {
            mAdapter.refresh(serviceBeanList, true);
        }
        switch_service_node.setVisibility(View.GONE);

        OneOpenHelper.GetServiceRequest(true, new RequestSuccessListener<Integer>() {
            @Override
            public void onResponse(final Integer serviceResultType) {
                serviceBeanList = ServiceConstants.GetAllServiceList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.refresh(serviceBeanList, false);
                        if (serviceResultType == ServiceConstants.TEST_ALL_SERVICE_OVER) {
                            handler.sendEmptyMessage(CHECK_SERVICES_STATUS);
//                    if (ServiceConstants.IfAllServiceSuccess(serviceBeanList)) {
//                        if (!isFinishing()) {
//                            ServiceConstants.initPublicInfo();
//                            finish();
//                        }
//                    } else {
//                        mTipTv.setText(getString(R.string.test_service_fail_tip));
//                        mJumpPassTv.setVisibility(View.VISIBLE);
//                        gifImageView.setVisibility(View.INVISIBLE);
//                    }
                        }
                    }
                });

            }
        });

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    handler.sendEmptyMessage(CHECK_SERVICES_STATUS);
                }
            }
        };
        timer.schedule(task, ServiceConstants.HTTP_TIMEOUT_SECONDS * 1000);
    }


    private class MyHandler extends Handler {
        public MyHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_SERVICES_STATUS:
                    try {
                        if (ServiceConstants.IfAllServiceSuccess(serviceBeanList)) {
                            if (!isFinishing()) {
                                ServiceConstants.initPublicInfo();
                                finish();
                            }
                        } else {
                            mTipTv.setText(getString(R.string.test_service_fail_tip_choose));
                            mJumpPassTv.setVisibility(View.VISIBLE);
                            gifImageView.setVisibility(View.INVISIBLE);
                        }

                        switch_service_node.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }
                    break;
            }
        }
    }

    @Override
    protected void setListener() {
        mJumpPassTv.setOnClickListener(this);
        switch_service_node.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.txt_jump_pass) {
            Utils.finish(TestServiceNodeActivity.this);
            ServiceConstants.initPublicInfo();

        } else if (i == R.id.switch_service_node) {//                ToastUtils.simpleToast("刷新成功！");
            initData();

        }
    }
}
