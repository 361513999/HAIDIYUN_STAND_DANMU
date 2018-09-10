package pad.stand.com.haidiyun.www.inter;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.widget.T_Image;

/**
 * Created by Administrator on 2017/6/24/024.
 */

public interface NumSel {
    public void change(String o, Object object);

    public void changeWeigh(String o, Object object, ArrayList<ReasonBean> resons, String remark, T_Image im);
}
