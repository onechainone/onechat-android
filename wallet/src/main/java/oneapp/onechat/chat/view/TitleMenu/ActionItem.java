package oneapp.onechat.chat.view.TitleMenu;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 功能描述：弹窗内部子类项（绘制标题和图标）
 */
public class ActionItem {
    // 定义图片对象
    public Drawable mDrawable;
    // 定义文本对象
    public CharSequence mTitle;

    // 定义文本对象
    public int mCmdId;

    public ActionItem(Drawable drawable, CharSequence title) {
        this.mDrawable = drawable;
        this.mTitle = title;
    }

    public ActionItem(Context context, int titleId) {
        this.mTitle = context.getResources().getText(titleId);
        this.mDrawable = null;
    }

    public ActionItem(Context context, int titleId, int cmdId, int drawableId) {
        this.mTitle = context.getResources().getText(titleId);
        if (drawableId <= 0) {
            this.mDrawable = null;
        } else {
            this.mDrawable = context.getResources().getDrawable(drawableId);
        }
        this.mCmdId = cmdId;
    }

    public ActionItem(Context context, int titleId, int drawableId) {
        this.mTitle = context.getResources().getText(titleId);
        if (drawableId <= 0) {
            this.mDrawable = null;
        } else {
            this.mDrawable = context.getResources().getDrawable(drawableId);
        }
    }

    public ActionItem(Context context, CharSequence title, int drawableId) {
        this.mTitle = title;
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }

    public ActionItem(Context context, CharSequence title) {
        this.mTitle = title;
        this.mDrawable = null;
    }

    public int getCmdId() {
        return mCmdId;
    }
}
