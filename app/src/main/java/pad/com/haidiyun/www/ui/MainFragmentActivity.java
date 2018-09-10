package pad.com.haidiyun.www.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.widget.CommonUserStandPop;
import pad.stand.com.haidiyun.www.ui.BaseFragmentActivity;

/**
 * Created by Administrator on 2017/9/6/006.
 */

public class MainFragmentActivity extends BaseFragmentActivity {
    private FragmentManager fragmentManager;
    private int CONTENT = R.id.main_content;
    private DataReceiver dataReceiver;
    public static MainFragmentActivity instance;

    /**
     * 发送一次点击广播
     */
    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        sendBroadcast(intent);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
//			toBack(true);
                down();
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);

    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
        if (!isWorked("pad.stand.com.haidiyun.www.service.FloatService")) {
            startFloat();
        }
    }

    private void startFloat() {
        Intent intent = new Intent(this, FloatService.class);
        startService(intent);
    }

    public boolean isWorked(String ServiceName) {
        ActivityManager myManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().contains(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("app.data.updata")) {
                //数据变化

            } else if (intent.getAction().equals("app.fc.ud")) {


            } else if (intent.getAction().equals("app.fc.gr")) {
                //翻转模式

            }
//            else if (intent.getAction().equals("KeyEvent.KEYCODE_BACK")) {
//                //切换到
//                P.c("==========222");
//                select(KeyEvent.KEYCODE_BACK);
//            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 2:
                    select(KeyEvent.KEYCODE_CAMERA);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
        }
        if (!isApplicationBroughtToBackground(this)) {
            Intent intentService = new Intent();
            intentService.setAction("flip.pad.com.invisible");
            sendBroadcast(intentService);
        }
    }

    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    private FlipNextActivity flipNextActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(8);
        setContentView(R.layout.flip_main_layout);
        fragmentManager = getSupportFragmentManager();
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("app.data.updata");
        //模式
        filter.addAction("app.fc.gr");
        filter.addAction("KeyEvent.KEYCODE_BACK");
        //上下
        filter.addAction("app.fc.ud");
        registerReceiver(dataReceiver, filter);
        Intent intent = getIntent();
        to(intent);
    }

    private void to(Intent intent) {//点服务
        if (intent.hasExtra("key")) {
            if (intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA) == KeyEvent.KEYCODE_BACK || intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA) == KeyEvent.KEYCODE_VOLUME_UP) {

            } else {
                select(intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA));
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        to(intent);
    }


    public void select(int KEYCODE) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        down();
        switch (KEYCODE) {
            case KeyEvent.KEYCODE_CAMERA://点餐
                flipNextActivity = new FlipNextActivity(MainFragmentActivity.this, handler);
                transaction.replace(CONTENT, flipNextActivity);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN://支付
                flipNextActivity = new FlipNextActivity(MainFragmentActivity.this, handler);
                transaction.replace(CONTENT, flipNextActivity);
//                CommonUserPop userPop = new CommonUserPop(MainFragmentActivity.this, new Handler());
                CommonUserStandPop userPop = new CommonUserStandPop(MainFragmentActivity.this, new Handler());
                userPop.showSheet();
                break;
        }
        INDEX = KEYCODE;
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    int INDEX = -1;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 自己记录fragment的位置,防止activity被系统回收时，fragment错乱的问题
        // super.onSaveInstanceState(outState);
        outState.putInt("index", INDEX);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        INDEX = savedInstanceState.getInt("index");
    }

    /* @Override
     public void keyEvent(KeyEvent event) {
         select(event.getKeyCode());
     }
 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0
                    || index >= fm.getFragments().size()) {

                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {

            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }

    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }
}
