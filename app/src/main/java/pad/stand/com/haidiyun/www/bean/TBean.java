package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/4/004.
 */

public class TBean implements Serializable {
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
