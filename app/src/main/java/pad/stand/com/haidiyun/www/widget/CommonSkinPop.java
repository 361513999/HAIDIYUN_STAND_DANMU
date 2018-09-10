package pad.stand.com.haidiyun.www.widget;

import java.util.ArrayList;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.SkinListAdapter;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.bean.Skin;
import pad.stand.com.haidiyun.www.ui.CardValActivity;
import pad.stand.com.haidiyun.www.ui.OrderActivity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;



public class CommonSkinPop {
	 private Context context;
	/**
	 * 删除弹出框
	 */
	private ListView wifiView;
	private SkinListAdapter wifiListAdapter;
	private   IDialog dlg;
	private ArrayList<Skin> skins;
	public CommonSkinPop(Context context) {
		this.context = context;
	}
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				NewDataToast.makeText("更换完成");
				close();
				closeInit();
				break;
			case 0:
				NewDataToast.makeText("更换失败");
				break;
			default:
				break;
			}
		};
	};
	private void init(){
		Skin skin0 = new Skin();
		skin0.setName("经典");
		skin0.setPkg("");
		Skin skin1 = new Skin();
		skin1.setName("青花瓷");
		skin1.setApk("HAIDIYUN_STAND_PLUG_QHC.apk");
		skin1.setPkg("pad.stand.com.haidiyun.plug.qhc");
		Skin skin2 = new Skin();
		skin2.setName("皮革");
		skin2.setApk("HAIDIYUN_STAND_PLUG_PG.apk");
		skin2.setPkg("pad.stand.com.haidiyun.plug.pg");
		
		skins.add(skin0);
		skins.add(skin1);
		skins.add(skin2);
	}
	public  Dialog showSheet() {
		  dlg = new IDialog(context,R.style.config_pop_style);
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
//		  d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY); 
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.setting_skin, null);
		final int cFullFillWidth = 600;
		layout.setMinimumWidth(cFullFillWidth);
		wifiView = (ListView) layout.findViewById(R.id.wifi_list);
		  skins = new ArrayList<Skin>();
		  init();
		wifiListAdapter = new SkinListAdapter(context, skins,handler);
		wifiView.setAdapter(wifiListAdapter);
		wifiView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				 
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	private void close() {
		if (dlg != null && dlg.isShowing()) {
			dlg.cancel();
			dlg = null;
		}
	}
	private void closeInit(){
		try {
			
			AppManager.getAppManager().finishActivity(OrderActivity.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			
			AppManager.getAppManager().finishActivity(CardValActivity.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
