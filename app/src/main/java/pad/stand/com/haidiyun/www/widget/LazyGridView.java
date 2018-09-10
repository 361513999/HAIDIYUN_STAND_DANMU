package pad.stand.com.haidiyun.www.widget;

import pad.stand.com.haidiyun.www.common.P;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

/**
 * 滚动到底部自动刷新
 */
public class LazyGridView extends GridView implements OnScrollListener {

	public LazyGridView(Context context) {
		super(context);
	}

	public LazyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LazyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	/**
	 * 列表视图滚动
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			P.c("停止");
			// 判断滚动到底部
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				P.c("开始刷新");
				if (listener != null) {
					listener.onScrollBottom();
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			P.c("开始滚动");
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			P.c("正在滚动");
			break;
		}

	}

	private OnScrollBottomListener listener;

	public void setOnScrollBottomListener(OnScrollBottomListener listener) {
		this.setOnScrollListener(this);
		this.listener = listener;
	}

	public void removeOnScrollBottomListener() {
		listener = null;
		System.out.println("removeOnScrollBottomListener");
	}

	/**
	 * 列表视图滚动到底部监听器
	 * 
	 * @author yinghui.hong
	 * 
	 */
	public interface OnScrollBottomListener {
		/**
		 * 列表视图滚动到底部时响应
		 */
		public void onScrollBottom();
	}
}