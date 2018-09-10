package pad.com.haidiyun.www.bean;

import java.io.Serializable;

public class ResonMenuBean implements Serializable {
    private String name;
    private String code;
    private boolean MultySelect;
    private boolean mustSelect;

    public boolean isMustSelect() {
        return mustSelect;
    }

    public void setMustSelect(boolean mustSelect) {
        this.mustSelect = mustSelect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isMultySelect() {
        return MultySelect;
    }

    public void setMultySelect(boolean multySelect) {
        MultySelect = multySelect;
    }

    @Override
    public String toString() {
        return "ResonMenuBean{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", MultySelect=" + MultySelect +
                ", mustSelect=" + mustSelect +
                '}';
    }
}
