package onemessageui.widght;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BaseSoftInputLayout extends LinearLayout implements View.OnClickListener {

    public final static int SHOW_KEYBOARD = 0X1;
    public final static int SHOW_EMOTION = 0X10;
    public final static int SHOW_OTHER = 0X11;
    Context context;
    private View rootView;
    private boolean mIsKeyboardShow;
    private View btnKeyBoard, btnEmotion, btnOther;
    // emotionView,otherView容器
    private View container;
    private int showWhat;
    private int keyboardHeight;
    private int minOtherBoardHeight = 400;
    private List<View> showViewList;
    private Map<View, ViewHolder> viewMapping;
    private View frame;
    private EditText editText;
    private int mNavigationBarHeight = -1;
    private int mHiddenHeight;
    private int mShownHeight;
    private int mLastCoverHeight;
    private int mLastHitBottom;

    public BaseSoftInputLayout(Context context) {
        super(context);
    }

    public BaseSoftInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BaseSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static class ViewHolder {
        private int SHOW_TYPE;
        private View showView;

        public ViewHolder(int SHOW_TYPE, View showView) {
            this.SHOW_TYPE = SHOW_TYPE;
            this.showView = showView;
        }

        public int getSHOW_TYPE() {
            return SHOW_TYPE;
        }

        public View getShowView() {
            return showView;
        }
    }

//    public BaseSoftInputLayout(Context context) {
//        super(context);
////        init();
//    }
//
//    public BaseSoftInputLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
////        init();
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public BaseSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
////        init();
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public BaseSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
////        init();
//    }

    public void init(Context context, View containers, View frame, EditText editText, View btnOther, View btnKeyBoard, View otherView, View btnEmotion, View emotionView) {
        if (mNavigationBarHeight == -1) {
//            frame.getLayoutParams().height = getMeasuredHeight();
            mNavigationBarHeight = getNavigationBarHeight(context);
        }
        this.context = context;
        viewMapping = new HashMap<>();
        showViewList = new ArrayList<>();

        this.editText = editText;
        this.editText.setOnClickListener(this);
        this.container = containers;
        this.frame = frame;
//        inflateView();
        if (context instanceof AppCompatActivity) {
            rootView = ((AppCompatActivity) context).getWindow().getDecorView();
            rootView = this.frame;
        } else {
            rootView = this.frame;
        }
        this.btnOther = btnOther;
        this.btnOther.setOnClickListener(this);
        this.btnEmotion = btnEmotion;
        this.btnEmotion.setOnClickListener(this);
        this.btnKeyBoard = btnKeyBoard;
        this.btnKeyBoard.setOnClickListener(this);

        add2ShowViewList(otherView);
        add2MappingMap(btnOther, SHOW_OTHER, otherView);
        add2ShowViewList(emotionView);
        add2MappingMap(btnEmotion, SHOW_EMOTION, emotionView);// btnEmotion-(SHOW_EMOTION-emotionView)
//        add2ShowViewList(btnKeyBoard);
//        add2MappingMap(btnKeyBoard, SHOW_KEYBOARD, null);


        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                detectKeyBoardState();
                if (mIsKeyboardShow) {
                    if (showWhat == SHOW_KEYBOARD) {
                        hideAllViewExceptKeyBoard();
                    }
                    showView(container);
                } else {
                    if (showWhat == 0) {
                        hideView(container);
                    } else {
                        showView(container);
                    }
                }
            }
        });
    }


    /**
     * 渲染自定义布局
     */
//    protected abstract void inflateView();
//
//    protected abstract View getContainer();
//
//    protected abstract View getFrame();
//
//    public abstract EditText getEditText();

    /**
     * @return 返回键盘按键, 若无则返回null
     */
//    protected abstract View getBtnKeyBoard();

    /**
     * 检测键盘弹出状态
     */
    private void detectKeyBoardState() {
        Rect visibleRect = new Rect();
        rootView.getWindowVisibleDisplayFrame(visibleRect);
        Rect hitRect = new Rect();
        rootView.getHitRect(hitRect);
        int coverHeight = hitRect.bottom - visibleRect.bottom;
        if (mLastCoverHeight == coverHeight && mLastHitBottom == hitRect.bottom) { // fix魅族动态显示/隐藏navigationbar没有及时响应
            return;
        }
        mLastHitBottom = hitRect.bottom;
        int deltaCoverHeight = coverHeight - mLastCoverHeight;
        mLastCoverHeight = coverHeight;
        if (coverHeight > mNavigationBarHeight) {
            if ((deltaCoverHeight == mNavigationBarHeight || deltaCoverHeight == -mNavigationBarHeight) && mIsKeyboardShow) {
                // 华为显示/隐藏navigationBar
                mHiddenHeight += deltaCoverHeight;
            }
            mShownHeight = coverHeight - mHiddenHeight;
            int height = mShownHeight;
            int overMinHeight = 0;
            if (height < minOtherBoardHeight) {
                overMinHeight = minOtherBoardHeight - height;
                height = minOtherBoardHeight;
            }
            if (keyboardHeight != height) {
                keyboardHeight = height;
                container.getLayoutParams().height = height;
                container.requestLayout();
            }
            mIsKeyboardShow = true;
            showWhat = SHOW_KEYBOARD;
            refreshFrame(visibleRect.bottom + mShownHeight + overMinHeight);
        } else {
            if ((deltaCoverHeight == mNavigationBarHeight || deltaCoverHeight == -mNavigationBarHeight) && !mIsKeyboardShow) {
                // 华为显示/隐藏navigationBar
                mHiddenHeight += deltaCoverHeight;
            }
            if (coverHeight != mHiddenHeight) {
                mHiddenHeight = coverHeight;
            }
            refreshFrame(visibleRect.bottom);
            mIsKeyboardShow = false;
            if (showWhat == SHOW_KEYBOARD) {
                showWhat = 0;
            }
        }
    }

    /**
     * 刷新frame高度
     *
     * @param bottom
     */
    private void refreshFrame(int bottom) {
        Rect rect = new Rect();
        frame.getHitRect(rect);
        int[] location = new int[2];
        frame.getLocationInWindow(location);
        int height = bottom - rect.top - location[1];
        if (height != frame.getLayoutParams().height) {
            frame.getLayoutParams().height = height;
            frame.requestLayout();
        }
    }

    protected void add2MappingMap(View view, int SHOW_TYPE, View showView) {
        viewMapping.put(view, new ViewHolder(SHOW_TYPE, showView));
    }

    protected void add2ShowViewList(View view) {
        showViewList.add(view);
    }

    @Override
    public void onClick(View v) {
        if (v == btnKeyBoard) {

            // 点击键盘
            if (showWhat == SHOW_KEYBOARD) {
                showWhat = 0;
                hideSoftInput();
            } else if (showWhat == 0) {
                showWhat = SHOW_KEYBOARD;
                showSoftInput();
                showView(container);
            } else {
                showWhat = SHOW_KEYBOARD;
                hideAllViewExceptKeyBoard();
                showSoftInput();
            }
        } else if (v == this.editText) {
            showWhat = SHOW_KEYBOARD;
            btnKeyBoard.setVisibility(GONE);
            btnEmotion.setVisibility(VISIBLE);
        } else {
            ViewHolder viewHolder = viewMapping.get(v);
            if (viewHolder != null) {
                int show_type = viewHolder.getSHOW_TYPE();
                View showView = viewHolder.getShowView();
                // 点击表情
                if (showWhat == show_type) {
                    // 隐藏表情,隐藏layout
                    showWhat = 0;
                    hideView(showView);
                    hideView(container);
                } else if (showWhat == SHOW_KEYBOARD) {
                    showWhat = show_type;
                    hideSoftInput();
                    showView(showView);
                    showView(container);
                } else {
                    showWhat = show_type;
                    hideAllViewExceptKeyBoard();
                    showView(showView);
                    showView(container);
                }
            }

            if (v == this.btnEmotion) {
                btnKeyBoard.setVisibility(VISIBLE);
                btnEmotion.setVisibility(GONE);
            } else if (v == this.btnOther) {
                btnKeyBoard.setVisibility(GONE);
                btnEmotion.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void hideSoftInput() {
        if (editText == null) return;
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        btnKeyBoard.setVisibility(GONE);
        btnEmotion.setVisibility(VISIBLE);
    }

    private void showSoftInput() {
        if (editText == null) return;
        editText.requestFocus();
        ((InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(editText, 0);
        btnKeyBoard.setVisibility(GONE);
        btnEmotion.setVisibility(VISIBLE);
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.GONE)
            view.setVisibility(View.GONE);
    }

    /**
     * 隐藏除了键盘外的view
     */
    private void hideAllViewExceptKeyBoard() {
        for (int i = 0; i < showViewList.size(); i++) {
            hideView(showViewList.get(i));
        }
    }

    public void hideAllView() {
        if (showWhat == 0) {
            return;
        }
        if (showWhat == SHOW_KEYBOARD) {
            hideSoftInput();
        } else {
            hideView(container);
        }
        showWhat = 0;
    }

    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
            if (id > 0) {
                navigationBarHeight = rs.getDimensionPixelSize(id);
            }
        } catch (Exception e) {
            // default 0
        }
        return navigationBarHeight;
    }

    /**
     * 设置最小高度(除了键盘外的最小高度)
     *
     * @param minOtherBoardHeight
     */
    public void setMinOtherBoardHeight(int minOtherBoardHeight) {
        this.minOtherBoardHeight = minOtherBoardHeight;
    }

    public boolean ifHideAll() {
        return showWhat == 0;
    }
}
