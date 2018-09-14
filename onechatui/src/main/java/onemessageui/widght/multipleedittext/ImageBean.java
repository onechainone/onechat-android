package onemessageui.widght.multipleedittext;

import android.graphics.Bitmap;

import java.io.Serializable;


public class ImageBean implements Serializable {
    //图片所在位置
    public int position;
    public String imageUrl;
    public String imagePath;
    private Bitmap bitmap;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
