package onemessageui.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import sdk.android.onechatui.R;
import onewalletui.ui.BaseActivity;

public class WaitReviewActivity extends BaseActivity implements View.OnClickListener {

    private ImageView img_back;
    private ListView mReviewList;
    private TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_review);
        initView();
        setListener();
    }

    private void setListener() {
        img_back.setOnClickListener(this);
    }

    private void initView() {
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        mReviewList = (ListView) findViewById(R.id.review_list);

        img_back.setVisibility(View.VISIBLE);
        txt_title.setText("等待审核成员");

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_back) {
            finish();

        }
    }
}
