package onemessageui.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import oneapp.onechat.oneandroid.chatsdk.OneOpenHelper;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;
import oneapp.onechat.oneandroid.onewallet.modle.ServiceBean;
import oneapp.onechat.oneandroid.graphenechain.interfaces.RequestSuccessListener;
import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onemessageui.adpter.SelectServiceNodeAdapter;
import onemessageui.view.OneBaseActivity;
import pl.droidsonroids.gif.GifDrawable;

public class SwitchServiceNodeActivity extends OneBaseActivity implements View.OnClickListener {
    private TextView txt_title, txt_refresh;
    private ImageView img_back;
    private ListView mlistview;
    private ImageView loadImg;
    GifDrawable gifDrawable;
    List<ServiceBean> serviceBeanList;
    private SelectServiceNodeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    protected void start() {
        setContentView(R.layout.activity_switch_node);
    }

    @Override
    protected void initControl() {
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_title.setText(getString(R.string.switch_service_node));
        txt_refresh = (TextView) findViewById(R.id.txt_right);
        txt_refresh.setVisibility(View.VISIBLE);
        txt_refresh.setText(getString(R.string.action_refresh));

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setVisibility(View.VISIBLE);
        mlistview = (ListView) findViewById(R.id.listview);

        loadImg = (ImageView) findViewById(R.id.loading_gif_icon);
    }

    @Override
    protected void initView() {
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.loading_test_service);
            loadImg.setImageDrawable(gifDrawable);
            gifDrawable.setLoopCount(Character.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        txt_refresh.setVisibility(View.GONE);
        gifDrawable.start();
        loadImg.setVisibility(View.VISIBLE);

        serviceBeanList = ServiceConstants.GetAllServiceList();
        if (mAdapter == null) {
            mAdapter = new SelectServiceNodeAdapter(context, serviceBeanList, true);
            mlistview.setAdapter(mAdapter);
        } else {
            mAdapter.refresh(serviceBeanList, true);
        }
        OneOpenHelper.GetServiceRequest(true, new RequestSuccessListener<Integer>() {
            @Override
            public void onResponse(final Integer serviceResultType) {
                serviceBeanList = ServiceConstants.GetAllServiceList();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.refresh(serviceBeanList, false);
                        if (serviceResultType == ServiceConstants.TEST_ALL_SERVICE_OVER) {
                            if (!isFinishing()) {
                                ToastUtils.simpleToast(R.string.test_service_node_done);
                                gifDrawable.stop();
                                loadImg.setVisibility(View.GONE);
                                txt_refresh.setVisibility(View.VISIBLE);
                            }
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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ToastUtils.simpleToast(R.string.test_service_node_done);
                                gifDrawable.stop();
                                loadImg.setVisibility(View.GONE);
                                txt_refresh.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(task, ServiceConstants.HTTP_TIMEOUT_SECONDS * 1000);

    }


    @Override
    protected void setListener() {
        img_back.setOnClickListener(this);
        txt_refresh.setOnClickListener(this);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            Utils.finish(SwitchServiceNodeActivity.this);

        } else if (i == R.id.txt_right) {
            initData();

        }
    }

}
