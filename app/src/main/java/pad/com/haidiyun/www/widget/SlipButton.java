package pad.com.haidiyun.www.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import pad.stand.com.haidiyun.www.R;

public class SlipButton extends View implements OnTouchListener {
	private boolean NowChoose = false;// 璁板綍褰撳墠鎸夐挳鏄惁鎵撳紑,true涓烘墦锟?flase涓哄叧锟?

	private boolean isChecked;

	private boolean OnSlip = false;// 璁板綍鐢ㄦ埛鏄惁鍦ㄦ粦鍔ㄧ殑鍙橀噺

	private float DownX, NowX;// 鎸変笅鏃剁殑x,褰撳墠鐨剎

	private Rect Btn_On, Btn_Off;// 鎵撳紑鍜屽叧闂姸鎬佷笅,娓告爣鐨凴ect .

	private boolean isChgLsnOn = false;

	private OnChangedListener ChgLsn;

	private Bitmap bg_on, bg_off, slip_btn;

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {// 鍒濆锟?

		bg_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.split_left_1);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.split_right_1);
		slip_btn = BitmapFactory.decodeResource(getResources(),
				R.drawable.split_1);
		Btn_On = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		Btn_Off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
				bg_off.getWidth(), slip_btn.getHeight());
		setOnTouchListener(this);// 璁剧疆鐩戝惉锟?涔熷彲浠ョ洿鎺ュ鍐橭nTouchEvent
	}

	@Override
	protected void onDraw(Canvas canvas) {// 缁樺浘鍑芥暟

		super.onDraw(canvas);

		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;

		if (NowX < (bg_on.getWidth() / 2))// 婊戝姩鍒板墠鍗婃涓庡悗鍗婃鐨勮儗鏅笉锟?鍦ㄦ鍋氬垽锟?
		{
			x = NowX - slip_btn.getWidth() / 2;
			canvas.drawBitmap(bg_off, matrix, paint);// 鐢诲嚭鍏抽棴鏃剁殑鑳屾櫙
		}

		else {
			x = bg_on.getWidth() - slip_btn.getWidth() / 2;
			canvas.drawBitmap(bg_on, matrix, paint);// 鐢诲嚭鎵撳紑鏃剁殑鑳屾櫙
		}

		if (OnSlip)// 鏄惁鏄湪婊戝姩鐘讹拷?,

		{
			if (NowX >= bg_on.getWidth())// 鏄惁鍒掑嚭鎸囧畾鑼冨洿,涓嶈兘璁╂父鏍囪窇鍒板锟?蹇呴』鍋氳繖涓垽锟?

				x = bg_on.getWidth() - slip_btn.getWidth() / 2;// 鍑忓幓娓告爣1/2鐨勯暱锟?..

			else if (NowX < 0) {
				x = 0;
			} else {
				x = NowX - slip_btn.getWidth() / 2;
			}
		} else {// 闈炴粦鍔ㄧ姸锟?

			if (NowChoose)// 鏍规嵁鐜板湪鐨勫紑鍏崇姸鎬佽缃敾娓告爣鐨勪綅锟?
			{
				x = Btn_Off.left;
				canvas.drawBitmap(bg_on, matrix, paint);// 鍒濆鐘讹拷?涓簍rue鏃跺簲璇ョ敾鍑烘墦锟?锟斤拷鎬佸浘锟?
			} else
				x = Btn_On.left;
		}
		if (isChecked) {
			canvas.drawBitmap(bg_on, matrix, paint);
			x = Btn_Off.left;
			isChecked = !isChecked;
		}

		if (x < 0)// 瀵规父鏍囦綅缃繘琛屽紓甯稿垽锟?..
			x = 0;
		else if (x > bg_on.getWidth() - slip_btn.getWidth())
			x = bg_on.getWidth() - slip_btn.getWidth();
		canvas.drawBitmap(slip_btn, x, 0, paint);// 鐢诲嚭娓告爣.

	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction())
		// 鏍规嵁鍔ㄤ綔鏉ユ墽琛屼唬锟?

		{
		case MotionEvent.ACTION_MOVE:// 婊戝姩
			NowX = event.getX();
			break;

		case MotionEvent.ACTION_DOWN:// 鎸変笅

			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			OnSlip = true;
			DownX = event.getX();
			NowX = DownX;
			break;

		case MotionEvent.ACTION_CANCEL: // 绉诲埌鎺т欢澶栭儴

			OnSlip = false;
			boolean choose = NowChoose;
			if (NowX >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			} else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}
			if (isChgLsnOn && (choose != NowChoose)) // 濡傛灉璁剧疆浜嗙洃鍚櫒,灏辫皟鐢ㄥ叾鏂规硶..
				ChgLsn.OnChanged(NowChoose);
			break;
		case MotionEvent.ACTION_UP:// 鏉惧紑

			OnSlip = false;
			boolean LastChoose = NowChoose;

			if (event.getX() >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			}

			else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}

			if (isChgLsnOn && (LastChoose != NowChoose)) // 濡傛灉璁剧疆浜嗙洃鍚櫒,灏辫皟鐢ㄥ叾鏂规硶..

				ChgLsn.OnChanged(NowChoose);
			break;
		default:
		}
		invalidate();// 閲嶇敾鎺т欢
		return true;
	}

	public void SetOnChangedListener(OnChangedListener l) {// 璁剧疆鐩戝惉锟?褰撶姸鎬佷慨鏀圭殑鏃讹拷?
		isChgLsnOn = true;
		ChgLsn = l;
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}
	
	public void setCheck(boolean isChecked) {
		this.isChecked = isChecked;
		NowChoose = isChecked;
	}
	public boolean isCheck(){
		return NowChoose;
	}
}
