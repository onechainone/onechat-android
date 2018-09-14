package onemessageui.mpush.bean;

import java.io.Serializable;

import oneapp.onechat.oneandroid.onewallet.network.ServiceConstants;

public class PushAddUserBean implements Serializable {
    private static final long serialVersionUID = 123456L;

    /**
     *
     */
    private String account_id;
    private String account_name;
    private String avatar_url;
    private String nickname;
    private String remark;

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAvatar_url() {
        return ServiceConstants.GetAvatarConfigServer() + avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
