package onewalletui.ui.widget.flowtag;

/**
 * Created by 何帅 on 2017/12/25.
 */

public class Evaluate {
    private String name;
    private boolean is_choosed;

    public Evaluate(String name, boolean is_choosed) {
        this.name = name;
        this.is_choosed = is_choosed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_choosed() {
        return is_choosed;
    }

    public void setIs_choosed(boolean is_choosed) {
        this.is_choosed = is_choosed;
    }
}
