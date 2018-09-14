package onemessageui.widght;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.common.Utils;

public class SideBar extends View {
    private List<Character> charList;
    private SectionIndexer sectionIndexter = null;
    private ListView list;
    private TextView mDialogText;
    private int m_nItemHeight = Utils.dipToPixel(getContext(), 12);

    public SideBar(Context context) {
        super(context);
        init();
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCharList(List<Character> charList) {
        if (charList == null) {
            return;
        }
        this.charList = charList;
        invalidate();
        requestLayout();
    }

    private void init() {
        charList = new ArrayList<>();
//        charList = ImmutableList.of('#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
//                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
//                'W', 'X', 'Y', 'Z');
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setListView(ListView _list) {
        list = _list;
    }

    public void setTextView(TextView mDialogText) {
        this.mDialogText = mDialogText;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();

        if (charList.size() <= 0) {
            return true;
        }

        int idx = i / m_nItemHeight;

        if (idx >= charList.size()) {
            idx = charList.size() - 1;
        } else if (idx < 0) {
            idx = 0;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE) {
            mDialogText.setVisibility(View.VISIBLE);
            mDialogText.setText("" + charList.get(idx));
            if (sectionIndexter == null) {
                if (list.getAdapter() instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter ha = (HeaderViewListAdapter) list
                            .getAdapter();
                    sectionIndexter = (SectionIndexer) ha.getWrappedAdapter();
                } else {
                    sectionIndexter = (SectionIndexer) list.getAdapter();
                }
            }
            if (sectionIndexter != null) {
                int position = sectionIndexter.getPositionForSection(charList.get(idx));
                if (position == -1) {
                    return true;
                }
                list.setSelection(position);
            }
        } else {
            mDialogText.setVisibility(View.INVISIBLE);
        }
        return true;
    }


    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.base_text_color_level2));
        paint.setTextSize(Utils.dipToPixel(getContext(), 10));
        // paint.setTextSize(20);
        // paint.setColor(0xff595c61);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < charList.size(); i++) {
            canvas.drawText(String.valueOf(charList.get(i)), widthCenter, m_nItemHeight
                    + (i * m_nItemHeight), paint);
        }
        super.onDraw(canvas);
    }
}
