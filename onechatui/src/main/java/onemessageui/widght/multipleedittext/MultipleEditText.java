package onemessageui.widght.multipleedittext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import onewalletui.util.UiUtils;
import sdk.android.onechatui.R;
import oneapp.onechat.oneandroid.onemessage.CommonConstants;
import oneapp.onechat.oneandroid.onewallet.util.LogUtils;

/**
 * 当前类注释: EditText 实现图文混排
 * 项目名：FastDevTest
 * 包名：com.jwenfeng.fastdev.view.htmledittext
 * 作者：jinwenfeng on 16/1/27 10:21
 * 邮箱：823546371@qq.com
 * QQ： 823546371
 * 公司：南京穆尊信息科技有限公司
 * © 2016 jinwenfeng
 * ©版权所有，未经允许不得传播
 */
public class MultipleEditText extends LinearLayout {


    public MultipleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MultipleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultipleEditText(Context context) {
        super(context);
        init(context);
    }

    private Context context;
    private EditText et;
    private int width;
    private HashMap<String, ImageBean> mImgsMap;

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.multiple_edit, this);
        et = (EditText) this.findViewById(R.id.et);
        mImgsMap = new HashMap<>();
        ViewTreeObserver vto = et.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                width = et.getWidth();
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() >= start + count && mImgsMap.containsKey(s.toString().substring(start, start + count))) {
                    //ToastUtils.tishiToast(s.toString().substring(start, start + count));
                    mImgsMap.remove(s.toString().substring(start, start + count));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ToastUtils.tishiToast(s.toString()+"*"+start + "*" + count + "*" + before);

                if (et.getText().toString().length() > start + count + 6) {
                    //if (et.getText().toString().substring(start + count, start + count + 5).equals("[img="))
                    //et.getEditableText().insert(start+count, "\n");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 设置图片宽度，默认为控件的宽度
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 设置文字
     *
     * @param text
     */
    public void setText(String text) {

        UiUtils.formatTextView(context, et, text, width);

    }


    public EditText getEditText() {
        return et;
    }


    /**
     * 获取图片列表
     */
    public List<ImageBean> getImgsList() {

        List<ImageBean> itemList = new ArrayList<>();
        for (ImageBean item : mImgsMap.values()) {
            itemList.add(item);
        }
        return itemList;
    }


    public void insertImageSpan(Bitmap loadedImage, String fileName) {
        et.getEditableText().insert(et.getSelectionStart(), "\n");
        // 根据Bitmap对象创建ImageSpan对象
        float scaleWidth = ((float) width) / loadedImage.getWidth();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        try {
            loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix,
                    true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ImageSpan imageSpan = new ImageSpan(context, loadedImage);
        // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = CommonConstants.IMG_PATTERN_LEFT + fileName + CommonConstants.IMG_PATTERN_RIGHT;

        //添加到map
        ImageBean item = new ImageBean();
        item.setBitmap(loadedImage);
        item.setImagePath(fileName);
        mImgsMap.put(tempUrl, item);

        SpannableString spannableString = new SpannableString(tempUrl);
        // 用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将选择的图片追加到EditText中光标所在位置
        int index = et.getSelectionStart(); // 获取光标所在位置
        Editable edit_text = et.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            edit_text.insert(index, spannableString);
        }
        edit_text.insert(index + spannableString.length(), "\n");
        LogUtils.d("插入的图片：" + spannableString.toString());

    }

}
