package pad.stand.com.haidiyun.www.widget;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.inter.UpdateMac;
import pad.stand.com.haidiyun.www.inter.WifiC;
import pad.stand.com.haidiyun.www.wifi.WifiConnect;



public class CommonConnectWifiPop {
	 private Context context;
	/**
	 * 删除弹出框
	 */
	private EditText edit_pass;
	private TextView close,connect,wifi;
	private   IDialog dlg;
	private String wifi_name;
	private WifiC wifiC;
	private UpdateMac updateMac;
	public CommonConnectWifiPop(Context context,String wifi_name,WifiC wifiC,UpdateMac updateMac) {
		this.context = context;
		this.wifi_name = wifi_name;
		this.wifiC = wifiC;
		this.updateMac = updateMac;
	}
 
	public  void showSheet() {
		  dlg = new IDialog(context, R.style.config_pop_style);
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.setting_connect_wifi, null);
		final int cFullFillWidth = 600;
		layout.setMinimumWidth(cFullFillWidth);
		wifi = (TextView) layout.findViewById(R.id.wifi);
		edit_pass = (EditText) layout.findViewById(R.id.edit_pass);
		edit_pass.setText("hdy00001");
		close = (TextView) layout.findViewById(R.id.close);
		connect = (TextView) layout.findViewById(R.id.connect);
		wifi.setText("准备连接【"+wifi_name+"】");
		connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String pass = edit_pass.getText().toString();
				WifiConnect connect = new WifiConnect(context);
				boolean flag = connect.reset(context, wifi_name, pass, wifiC,updateMac);
				if(flag){
					if(dlg!=null&&dlg.isShowing()){
						dlg.cancel();
					}
				}
				
			}
		});
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				if(dlg!=null&&dlg.isShowing()){
					dlg.cancel();
				}
			}
		});
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
	}

}
