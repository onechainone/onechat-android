package onemessageui.view;

import android.os.Bundle;

import onewalletui.ui.BaseActivity;


public abstract class OneBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
        initControl();
        initView();
        initData();
        setListener();
    }

    /**
     * init
     */
    protected abstract void start();

    /**
     * 绑定控件id
     */
    protected abstract void initControl();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置监听
     */
    protected abstract void setListener();

}