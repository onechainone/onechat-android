package onemessageui.mpush.bean;

import java.io.Serializable;

public class OrderChangeBean implements Serializable {
    private static final long serialVersionUID = 123456L;

    /**
     *
     */
    private String id;
    private String uni_uuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUni_uuid() {
        return uni_uuid;
    }

    public void setUni_uuid(String uni_uuid) {
        this.uni_uuid = uni_uuid;
    }
}
