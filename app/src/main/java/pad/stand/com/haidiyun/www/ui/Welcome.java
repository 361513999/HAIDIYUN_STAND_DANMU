package pad.stand.com.haidiyun.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.ui.LanuchActivity;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;

public class Welcome extends BaseActivity {
     SharedUtils sharedUtils ;

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
       sharedUtils = new SharedUtils(Common.CONFIG);
        if(sharedUtils.getBooleanValue("flip")){
            Intent intent = new Intent(Welcome.this,LanuchActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishActivity();
        }else{
            Intent intent = new Intent(Welcome.this, pad.stand.com.haidiyun.www.ui.LanuchActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishActivity();
        }
    }
}
