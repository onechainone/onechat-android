package onemessageui.utils.skin;


import oneapp.onechat.oneandroid.onewallet.modle.BaseBean;

public class SkinBean extends BaseBean {

    private String name;
    private int stringId;
    private boolean ifDarkBar;

    public SkinBean(String name, int stringId, boolean ifDarkBar) {
        this.name = name;
        this.stringId = stringId;
        this.ifDarkBar = ifDarkBar;
    }

    public boolean isIfDarkBar() {
        return ifDarkBar;
    }

    public void setIfDarkBar(boolean ifDarkBar) {
        this.ifDarkBar = ifDarkBar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(int stringId) {
        this.stringId = stringId;
    }
}
