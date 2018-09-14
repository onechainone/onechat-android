package onewalletui.util.jump;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author wxf
 * @Description: 跳转参数封装基类。存放如from，back字段等
 */
public class BaseJumpBean implements Serializable {
    private static final long serialVersionUID = -4566842394474955747L;
    /**
     * 从哪儿跳转过来
     */
    private String from;
    /**
     * 上个应用进程id
     */
    private String processId;
    /**
     * 跳转到哪个Activity对应的class ， 默认首页
     */
    protected Class<?> whichActivity = null;
    /**
     * 是否来自打洞。后面用于处理打洞逻辑LetvBackActivity 。默认值false，内跳
     */
    private boolean isFromOutJumpIn = false;
    /**
     * intent的flags，如果这个值不为null，就会add到intent里边
     */
    protected Integer intentFlags;
    /**
     * activity跳转所带的参数
     **/
    private HashMap<String, Object> paramsMap;

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Class<?> getWhichActivity() {
        return this.whichActivity;
    }

    public void setWhichActivity(Class<?> whichActivity) {
        this.whichActivity = whichActivity;
    }

    public boolean isFromOutJumpIn() {
        return this.isFromOutJumpIn;
    }

    public void setFromOutJumpIn(boolean fromJump) {
        this.isFromOutJumpIn = fromJump;
    }

    public String getProcessId() {
        return this.processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getIntentFlags() {
        return this.intentFlags;
    }

    public void setIntentFlags(Integer intentFlags) {
        this.intentFlags = intentFlags;
    }

    public HashMap<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(HashMap<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }

}
