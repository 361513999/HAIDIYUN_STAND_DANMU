package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RecyclerImageView extends ImageView
{

	public RecyclerImageView(Context context)
	{
		super(context);
	}

	public RecyclerImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RecyclerImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		// 閲嶅啓ImageView鐨刼nDetachedFromWindow鏂规硶锛屽湪瀹冧粠灞忓箷涓秷澶辨椂鍥炶皟锛屽幓鎺塪rawable寮曠敤锛岃兘鍔犲揩鍐呭瓨鐨勫洖鏀躲?
		setImageDrawable(null);
	}
}
