package pad.stand.com.haidiyun.www.widget;

import pad.stand.com.haidiyun.www.R;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CommonAdvertPop {
	 private Context context;
	/**
	 * 下单提示
	 */
	private TextView  cancle;
	private   IDialog dlg;
	public CommonAdvertPop(Context context ) {
		this.context = context;
		 
	}
	public  Dialog showSheet() {
		  dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.pop_advert_layout, null);
		final int cFullFillWidth = 615;
		layout.setMinimumWidth(cFullFillWidth);
		CountDownTimer downTimer = new CountDownTimer(3000,1000) {
			
			@Override
			public void onTick(long arg0) {

				
			}
			
			@Override
			public void onFinish() {

				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}
			}
		};
		downTimer.start();
//		cancle = (TextView) layout.findViewById(R.id.cancle);
//		cancle.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				dlg.cancel();
//			}
//		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
