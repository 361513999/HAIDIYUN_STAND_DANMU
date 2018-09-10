package pad.stand.com.haidiyun.www.inter;

import pad.stand.com.haidiyun.www.bean.Dish;
import pad.stand.com.haidiyun.www.bean.DishTableBean;

public interface BuyClick {
	public void add(DishTableBean obj);
	public void delete(DishTableBean obj);
	public void remove(DishTableBean obj);
	public void res(DishTableBean obj);
	public void person();
	public void person(LoadBuy buy, String optName);
	public void num(DishTableBean obj);
	public void price(DishTableBean obj);
	/**
	 * 退增
	 * @param obj
	 */
	public void tz(DishTableBean obj,int INDEX);
}
