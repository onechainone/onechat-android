package onemessageui.chat.task;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.ImageUtils;
import onemessageui.chat.utils.ImageCache;

public class LoadVideoImageTask extends AsyncTask<Object, Void, Bitmap> {

    private ImageView iv = null;
    String thumbnailPath = null;
    String thumbnailUrl = null;
    Activity activity;
    ItemMessage message;
    BaseAdapter adapter;

    @Override
    protected Bitmap doInBackground(Object... params) {
        thumbnailPath = (String) params[0];
        thumbnailUrl = (String) params[1];
        iv = (ImageView) params[2];
        activity = (Activity) params[3];
        message = (ItemMessage) params[4];
        adapter = (BaseAdapter) params[5];
        if (new File(thumbnailPath).exists()) {
            return ImageUtils.decodeScaleImage(thumbnailPath, 120, 120);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            iv.setImageBitmap(result);
            ImageCache.getInstance().put(thumbnailPath, result);
            iv.setClickable(true);
            iv.setTag(thumbnailPath);
            iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (thumbnailPath != null) {
                    }
                }
            });

        } else {
            if (message.status == ItemMessage.Status.FAIL
                    || message.direct == ItemMessage.Direct.RECEIVE) {

            }

        }
    }

}
