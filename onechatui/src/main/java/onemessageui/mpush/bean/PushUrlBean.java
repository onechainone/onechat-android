package onemessageui.mpush.bean;

import java.io.Serializable;

public class PushUrlBean implements Serializable {
    private static final long serialVersionUID = 123456L;

    private String title;
    private String img_url;
    private String message;
    private String web_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }
}
