package onemessageui.chat.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

/**
 * Created by msg55 on 2018/3/2.
 */

@SuppressLint("AppCompatCustomView")
public class MyChronometer extends TextView {

    /**
     * 回调，计时器增加时通知自己
     */
    public interface OnMyChronometerTickListener {

        /**
         * 通知计时改变
         */
        void onMyChronometerTick(int time);

    }

    public interface OnMyChronometerTimeListener {

        /**
         * 通知计时改变
         */
        void OnMyChronometerTimeListener(int time);

    }

    private OnMyChronometerTimeListener OnMyChronometerTimeListener;

    private long mBase;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private OnMyChronometerTickListener mOnMyChronometerTickListener;
    private long now_time;

    private static final int TICK_WHAT = 2;


    private static final String TAG = "MyChronometer";

    public MyChronometer(Context context) {
        this(context, null, 0);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateText(mBase);
    }

    /**
     * 设置定时计时器所参照的时间
     */
    public void setBase(long base) {
        mBase = base;
        updateText(SystemClock.elapsedRealtime());
    }

    /**
     * 当我的计时器改变时，设置监听器
     *
     * @param listener
     */
    public void setOnMyChronometerTickListener(OnMyChronometerTickListener listener) {
        mOnMyChronometerTickListener = listener;
    }

    public void setOnMyChronometerTimeListener(OnMyChronometerTimeListener listener) {
        OnMyChronometerTimeListener = listener;
    }

    /**
     * 开始视图显示
     */
    public void start() {
        mStarted = true;
        updateRunning();
    }

    /**
     * 结束视图显示
     */
    public void stop() {
        mStarted = false;
        updateRunning();
        now_time /= 10;
        if (OnMyChronometerTimeListener != null) {
            OnMyChronometerTimeListener.OnMyChronometerTimeListener((int) now_time);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateText(long now) {

        long seconds = now - mBase;
        seconds /= 10;
        now_time = seconds;

        int time_m = (int) (seconds / 100);
        if (mOnMyChronometerTickListener != null) {
            mOnMyChronometerTickListener.onMyChronometerTick(time_m);
        }
        int time_s = (int) (seconds % 100);
        setText(time_m + "");

    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());//设定系统时钟
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 1000);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MyChronometer.class.getName());
    }

    @SuppressLint("NewApi")
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MyChronometer.class.getName());
    }

}
