package onewalletui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import sdk.android.onechatui.R;

/**
 * Created by chunzhengwang on 2017/9/29.
 */

public class DecimalEditText extends TextView {
    private static final int DEFAULT_DECIMAL_NUMBER = 8;
    /**
     * 保留小数点后多少位
     */
    private int mDecimalNumber = DEFAULT_DECIMAL_NUMBER;

    public DecimalEditText(Context context) {
        this(context, null, R.attr.editTextStyle);
    }

    public DecimalEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public DecimalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DecimalEditText);
        mDecimalNumber = typedArray.getInt(R.styleable.DecimalEditText_decimalNumber, DEFAULT_DECIMAL_NUMBER);
        typedArray.recycle();
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String lastInputContent = dest.toString();
//                Log.e("TAG", "+++:"+start+"  "+end+"  "+dstart+"  "+dend+"  len:"+lastInputContent.length());

                if (lastInputContent.contains(".")) {
                    int index = lastInputContent.indexOf(".");
                    if (((lastInputContent.length() - index) >= (mDecimalNumber + 1)) && dstart >= (index + 1)) {
                        return "";
                    }
                }

                return null;
            }
        }});
    }

    public int getDecimalNumber() {
        return mDecimalNumber;
    }

    public void setDecimalNumber(int decimalNumber) {
        mDecimalNumber = decimalNumber;
    }


}
