package pad.stand.com.haidiyun.www.widget;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.inter.LoadBuy;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CommonTipPop {
	 private Context context;
	/**
	 * 下单提示
	 */
	private TextView tip,txt,cancle,sure;
	private   IDialog dlg;
	private LoadBuy sureBuy;
	private SharedUtils sharedUtils;
	private Handler fHandler;
	public CommonTipPop(Context context,LoadBuy sureBuy,Handler fHandler) {
		this.context = context;
		this.sureBuy = sureBuy;
		this.fHandler  = fHandler;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}
	public  Dialog showSheet() {
		  dlg = new IDialog(context, R.style.config_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.common_tip, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		tip = (TextView) layout.findViewById(R.id.tip);
		txt = (TextView) layout.findViewById(R.id.txt);
		cancle = (TextView) layout.findViewById(R.id.cancle);
		sure = (TextView) layout.findViewById(R.id.sure);
		sure.setText("确定");
		tip.setText("温馨提示");
		String str = "您选择的菜品下单后无法更改,是否确认下单";
		SpannableStringBuilder sbBuilder=new SpannableStringBuilder(str); 
		sbBuilder.setSpan
        (new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
		txt.setText(sbBuilder);
		txt.setTextSize(20);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlg.cancel();
			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//下单,自助下单
				//确定下单
				if(fHandler==null){
					P.c("这是什么意思");
					String optName = sharedUtils.getStringValue("optName");
					sureBuy.success(optName.length()==0?"":optName);
					dlg.cancel();
				}else{
					//真的就关闭
					dlg.cancel();
					fHandler.sendEmptyMessage(11);
				}
				
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
