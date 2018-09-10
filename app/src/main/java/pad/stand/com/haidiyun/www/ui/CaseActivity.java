package pad.stand.com.haidiyun.www.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;


public abstract class CaseActivity extends Activity{
	SharedUtils sharedUtils ;
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		sharedUtils = new SharedUtils(Common.CONFIG);
		if(sharedUtils.getBooleanValue("screen_keep")){

			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}else{

		}
		AppManager.getAppManager().addActivity(this);


	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		AppManager.getAppManager().finishActivity(this);
	}

}
