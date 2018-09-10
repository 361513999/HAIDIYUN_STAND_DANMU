package pad.stand.com.haidiyun.www.inter;

import java.util.ArrayList;
import java.util.HashMap;

import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.ReasonBean;

public interface ReasonI {
	public void select(ArrayList<ReasonBean> beans, DishTableBean bean,String num);
	public void insert(ArrayList<ReasonBean> beans, FoodsBean bean,String num);
	public void select(ArrayList<ReasonBean> beans, DishTableBean bean, HashMap<String, ArrayList<ReasonBean>> tcBeans, HashMap<String, String> tcListStr, int position, int num);

	public void init(DishTableBean bean,String num);
	public void init(FoodsBean bean,String num);
}
