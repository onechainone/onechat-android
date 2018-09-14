package onemessageui.mpush.bean;

import java.io.Serializable;

public class PushMsgBean implements Serializable {
    private static final long serialVersionUID = 123456L;

    /**
     *
     */
    private String msgId;
    private String type;       // 类型
    private String content;    // json内容
    private String timestamp;
    private String classify_key;//筛选类型 群/资产
    private String classify_id;//筛选id
    private String show;


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getClassify_key() {
        return classify_key;
    }

    public void setClassify_key(String classify_key) {
        this.classify_key = classify_key;
    }

    public String getClassify_id() {
        return classify_id;
    }

    public void setClassify_id(String classify_id) {
        this.classify_id = classify_id;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }
}
