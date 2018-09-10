package pad.stand.com.haidiyun.www.inter;

import pad.stand.com.haidiyun.www.bean.TableBean;

public interface SelectTable {
	public void select(TableBean bean, String optName, int person);
	public void isLocked();
	
}
