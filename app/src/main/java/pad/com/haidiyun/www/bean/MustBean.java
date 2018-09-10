package pad.com.haidiyun.www.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/26.
 */

public class MustBean implements Serializable {
    private int index;
    private List<String> listIndex;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getListIndex() {
        return listIndex;
    }

    public void setListIndex(List<String> listIndex) {
        this.listIndex = listIndex;
    }

    @Override
    public String toString() {
        return "MustBean{" +
                "index=" + index +
                ", listIndex=" + listIndex +
                '}';
    }
}
