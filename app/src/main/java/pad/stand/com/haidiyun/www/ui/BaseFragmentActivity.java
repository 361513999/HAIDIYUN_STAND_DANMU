package pad.stand.com.haidiyun.www.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.zc.changeskin.SkinManager;

import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;


public abstract class BaseFragmentActivity extends FragmentActivity {
	SharedUtils sharedUtils ;
	/*@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		final int action = event.getAction();
		final boolean isDown = action == KeyEvent.ACTION_DOWN;
		if(isDown&&(event.getRepeatCount() == 0)){
			keyEvent(event);
		}
		return  true;
	}
	public abstract  void keyEvent(KeyEvent event);*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		sharedUtils = new SharedUtils(Common.CONFIG);
		SkinManager.getInstance().register(this);
		if(sharedUtils.getBooleanValue("screen_keep")){
			P.c("常亮模式");
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}else{
			P.c("系统熄屏策略");
		}
		AppManager.getAppManager().addActivity(this);
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		SkinManager.getInstance().unregister(this);
			AppManager.getAppManager().finishActivity(this);
	}
}
