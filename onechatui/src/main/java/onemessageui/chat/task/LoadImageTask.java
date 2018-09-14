package onemessageui.chat.task;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage;
import oneapp.onechat.oneandroid.onemessage.beanchat.chat.ItemMessage.ChatType;
import oneapp.onechat.oneandroid.onemessage.beanchat.util.ImageUtils;
import onemessageui.chat.utils.ImageCache;
import onemessageui.chat.ShowBigImage;

public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
    private ImageView iv = null;
    String localFullSizePath = null;
    String thumbnailPath = null;
    String remotePath = null;
    ItemMessage message = null;
    ChatType chatType;
    Activity activity;
    private TextView statusTv;

    @Override
    protected Bitmap doInBackground(Object... args) {
        thumbnailPath = (String) args[0];
        localFullSizePath = (String) args[1];
        remotePath = (String) args[2];
        chatType = (ChatType) args[3];
        iv = (ImageView) args[4];
        // if(args[2] != null) {
        activity = (Activity) args[5];
        // }
        message = (ItemMessage) args[6];
        statusTv = (TextView) args[7];
        File file = new File(thumbnailPath);
        if (file.exists()) {
            return ImageUtils.decodeScaleImage(thumbnailPath, 320, 320, 15f);
        } else {
            if (message.direct == ItemMessage.Direct.SEND) {
                return ImageUtils.decodeScaleImage(localFullSizePath, ImageUtils.SCALE_IMAGE_WIDTH, ImageUtils.SCALE_IMAGE_HEIGHT);
            } else {
                return null;
            }
        }

    }

    protected void onPostExecute(Bitmap image) {
        if (image != null) {
            iv.setImageBitmap(image);
            statusTv.setVisibility(View.GONE);
            ImageCache.getInstance().put(thumbnailPath, image);
            iv.setClickable(true);
            iv.setTag(thumbnailPath);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (thumbnailPath != null) {
                        // TODO 查看大图
                        Intent intent = new Intent(activity,
                                ShowBigImage.class);
                        File file = new File(localFullSizePath);
                        if (file.exists()) {
                            Uri uri = Uri.fromFile(file);
                            intent.putExtra("uri", uri);
                        } else {
                            intent.putExtra("remotepath", remotePath);
                        }
                        activity.startActivity(intent);
                    }
                }
            });
        } else {
            statusTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
