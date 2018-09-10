package pad.com.haidiyun.www.inter;

import pad.com.haidiyun.www.bean.DishTableBean;


public interface BuyClick {
	public void add(DishTableBean obj);
	public void delete(DishTableBean obj);
	public void remove(DishTableBean obj);
	public void res(DishTableBean obj);
	public void person();
	public void price(DishTableBean obj);
	public void person(LoadBuy buy, String optName);
}
