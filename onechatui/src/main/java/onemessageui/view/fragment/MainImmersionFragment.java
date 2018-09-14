package onemessageui.view.fragment;

import com.gyf.barlibrary.ImmersionBar;

import oneapp.onechat.oneandroid.chatsdk.ConfigConstants;
import onewalletui.ui.BaseFragment;
import sdk.android.onechatui.R;

/**
 * 当以show()和hide()方法形式加载Fragment，沉浸式的使用
 * Created by geyifeng on 2017/4/7.
 */
public abstract class MainImmersionFragment extends BaseFragment {

    protected ImmersionBar mImmersionBar;

    //状态栏是否深色主题
    protected boolean IF_BAR_DARK_FONT = ConfigConstants.DEFAULT_BAR_DARK;
    //导航栏是否深色主题
    protected boolean IF_NAVIGATION_BAR_DARK_FONT = ConfigConstants.DEFAULT_NAVIGATION_BAR_DARK;

    protected boolean IF_KEYBOARD_ENABLE = true;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mImmersionBar != null)
            mImmersionBar.init();
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        try {
            mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.statusBarDarkFont(false, 0.2f); //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
            mImmersionBar.statusBarDarkFont(IF_BAR_DARK_FONT, 0.2f); //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
            if (IF_KEYBOARD_ENABLE) {
                mImmersionBar.keyboardEnable(true);
            }
            mImmersionBar.navigationBarWithKitkatEnable(false);
            mImmersionBar.navigationBarEnable(!IF_NAVIGATION_BAR_DARK_FONT);
            if (IF_NAVIGATION_BAR_DARK_FONT) {
                mImmersionBar.navigationBarColor(R.color.base_bg_color_level1);
            } else {
//                mImmersionBar.navigationBarColor(R.color.base_bg_color_level1_blackskin);
            }

//            mImmersionBar.navigationBarAlpha(0.1f);
//        mImmersionBar.statusBarColor(R.color.base_title_bar_color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
