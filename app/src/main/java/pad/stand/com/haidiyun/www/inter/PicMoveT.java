package pad.stand.com.haidiyun.www.inter;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.PicPoint;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.widget.T_Image;


public interface PicMoveT {
    public void addP(PicPoint point);

    public void add(FoodsBean obj);

    public void mics(FoodsBean obj);

    //时价选择
    public void sj(FoodsBean obj);

    //口味选择下单
    public void rb(FoodsBean obj);

    public void gq();

    //套餐选择
    public void tc(FoodsBean obj);
    public void tc(FoodsBean obj,String num);

    public void cz(FoodsBean obj, ArrayList<ReasonBean> resons, String remark, T_Image im);

    public void view(FoodsBean obj, int index);
}
