package pad.com.haidiyun.www.inter;

import android.graphics.RectF;

import pad.com.haidiyun.www.bean.FouceBean;

/**
 * 图片点击事件
 * @author Administrator
 *
 */
public interface AreaTouch {
	/**
	 * 区域点击接口
	 * @param bean
	 */
	public void init(RectF downF);
	public void click(FouceBean bean, RectF downF);
	public void up(float x, float y, boolean mIsFlipping);
	public void down();
	
}
