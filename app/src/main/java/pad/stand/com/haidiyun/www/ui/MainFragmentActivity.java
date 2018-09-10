package pad.stand.com.haidiyun.www.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.com.haidiyun.www.ui.FlipNextActivity;
import pad.stand.com.haidiyun.cy.ui.CyMainActivity;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.Remove;
import pad.stand.com.haidiyun.www.service.FloatService;
import pad.stand.com.haidiyun.www.widget.CommonCodePop;
import pad.stand.com.haidiyun.www.widget.CommonConfigPop;
import pad.stand.com.haidiyun.www.widget.CommonUserStandPop;
import pad.stand.com.haidiyun.www.widget.NewDataToast;

/**
 * Created by Administrator on 2017/9/6/006.
 */

public class MainFragmentActivity extends BaseFragmentActivity {
    private static final String TAG = "MainFragmentActivity";
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

    private int mode = 0;
    Remove remove = new Remove() {
        @Override
        public void remove() {
            if (commonConfigPop != null) {
                commonConfigPop = null;
            }
        }
    };
    CommonConfigPop commonConfigPop;
    pad.com.haidiyun.www.widget.CommonConfigPop  com;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = 1;
                    break;
                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode -= 1;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    mode += 1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == 3) {
                        if ( sharedUtils.getBooleanValue("flip")) {
                            if(com==null){
                                com    = new  pad.com.haidiyun.www.widget.CommonConfigPop (MainFragmentActivity.this);
                                com.setRem(new Remove() {
                                    @Override
                                    public void remove() {
                                        if(com!=null){
                                            com = null;
                                        }
                                    }
                                });
                                com.showSheet();
                            }

                        }else{
                            if (commonConfigPop == null) {
                                commonConfigPop = new CommonConfigPop(MainFragmentActivity.this);
                                commonConfigPop.setRem(remove);
                                commonConfigPop.showSheet();
                            }
                        }

                    }
                    break;
            }



        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
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
        //add
        //  P.c("FloatService-->"+isWorked("pad.stand.com.haidiyun.www.service.FloatService"));
    Class c = null;
        if(sharedUtils.getBooleanValue("flip")){
           c = pad.com.haidiyun.www.service.FloatService.class;
        }else{
            c = FloatService.class;
        }

        if (!isWorked("pad.stand.com.haidiyun.www.service.FloatService")) {
            startFloat(c);
        }
    }

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

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
                case 1:
                    Intent intent = new Intent();
                    intent.setAction(Common.SERVICE_ACTION);
                    intent.putExtra("open_table", "");
                    startService(intent);
                    sharedUtils.clear("order_time");
                    DB.getInstance().clear();
                    NewDataToast.makeTextL(getResources().getString(R.string.ordersuccess), 2000);
//                    loadBuy.success(null);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
        }
        if (orderFinishRunnable != null) {
            orderFinishHandler.removeCallbacks(orderFinishRunnable);
        }
//        if (!isApplicationBroughtToBackground(this)) {
//            Intent intentService = new Intent();
//            intentService.setAction("pad.com.invisible");
//            sendBroadcast(intentService);
//        }
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

    private OrderActivity flipNextActivity;
    private FlipNextActivity nextActivity;
    private CyMainActivity cyMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(8);
        setContentView(R.layout.main_layout);
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
    public static Intent convertToExplicitIntent(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(intent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
    //addCode--windy
    private void startFloat(Class c) {
        Intent intent = new Intent(this, c);
        intent.setPackage(BaseApplication.packgeName);

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

    //end
    private void to(Intent intent) {//点服务
        if (intent.hasExtra("key")) {
            if (intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA) == KeyEvent.KEYCODE_BACK || intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA) == KeyEvent.KEYCODE_VOLUME_UP) {

            } else {
                select(intent.getIntExtra("key", KeyEvent.KEYCODE_CAMERA));
            }
        }

    }

  /*  @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        to(intent);
    }*/

    private Handler orderFinishHandler = new Handler();
    private int tableDelay = 3000;
    private Runnable orderFinishRunnable = new Runnable() {
        public void run() {
            findOrder();
            orderFinishHandler.postDelayed(this, tableDelay);
        }
    };
    private CommonCodePop codePop;

    public void select(int KEYCODE) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        down();
        switch (KEYCODE) {
            case KeyEvent.KEYCODE_CAMERA://点餐
                if(sharedUtils.getBooleanValue("flip")){
                    if(nextActivity==null){
                        nextActivity = new FlipNextActivity(MainFragmentActivity.this,handler);
                    }

                    transaction.replace(CONTENT, nextActivity);
                }else{
                    if (sharedUtils.getBooleanValue("is_cy")) {
                        if(cyMainActivity==null){
                            cyMainActivity = new CyMainActivity(MainFragmentActivity.this);
                        }

                        transaction.replace(CONTENT, cyMainActivity);
                    } else {
                        if(flipNextActivity==null){
                            flipNextActivity = new OrderActivity(MainFragmentActivity.this, handler);
                        }

                        transaction.replace(CONTENT, flipNextActivity);
                    }
                }


                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN://支付
             /*   flipNextActivity = new OrderActivity(MainFragmentActivity.this, handler);
                transaction.replace(CONTENT, flipNextActivity);*/
                if(sharedUtils.getBooleanValue("flip")){
                    if(nextActivity==null){
                        nextActivity = new FlipNextActivity(MainFragmentActivity.this,handler);
                    }

                    transaction.replace(CONTENT, nextActivity);
                }else{
                    if (sharedUtils.getBooleanValue("is_cy")) {
                        if(cyMainActivity==null){
                            cyMainActivity = new CyMainActivity(MainFragmentActivity.this);
                        }

                        transaction.replace(CONTENT, cyMainActivity);
                    } else {
                        if(flipNextActivity==null){
                            flipNextActivity = new OrderActivity(MainFragmentActivity.this, handler);
                        }

                        transaction.replace(CONTENT, flipNextActivity);
                    }

                }

                if (sharedUtils.getBooleanValue("is_prepay")) {
                    String payUrl = sharedUtils.getStringValue("payUrl");
                    codePop = new CommonCodePop(MainFragmentActivity.this, new Handler(), payUrl);
                    codePop.showSheet();
                    if (payUrl.contains("$")) {
                        orderFinishHandler.post(orderFinishRunnable);
                    }
                } else {
                    CommonUserStandPop userPop = new CommonUserStandPop(MainFragmentActivity.this, new Handler());
                    userPop.showSheet();
                }
                break;
        }
        INDEX = KEYCODE;
        transaction.commitAllowingStateLoss();
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
        INDEX = savedInstanceState.getInt("index");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
//            if (fm.getFragments() == null || index < 0
//                    || index >= fm.getFragments().size()) {
//
//                return;
//            }
            index=0;
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

    private RequestCall orderFinishRequestCall;

    /**
     * 查找先付费订单数据
     */
    private void findOrder() {
        JSONObject jsonObject = new JSONObject();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("{\"BillNo\":\"" + sharedUtils.getStringValue("billId") + "\",\"DeviceSerialNo\":\"" + sharedUtils.getStringValue("deviceSerialNo") + "\"}");
            jsonObject.put("data", builder.toString());
            String ip = sharedUtils.getStringValue("IP");
            cal();
            orderFinishRequestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_FINISH_STATUS))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            if (orderFinishRequestCall != null) {
                orderFinishRequestCall.execute(orderFinishCallback);
            }

            P.c(TimeUtil.getTime(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //定时查是否扫码支付
    private StringCallback orderFinishCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                FileUtils.formatJson(response), "先付费模式结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "先付费模式结果");
                    }
                }
            }.start();
            new Thread() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            //成功
                            JSONObject object = jsonObject.getJSONObject("Data");
                            if (object.getBoolean("PaySuccess")) {
                                //支付成功
                                codePop.close();
                                sharedUtils.clear("payUrl");
                                orderFinishHandler.removeCallbacks(orderFinishRunnable);
                                handler.sendEmptyMessage(1);
                            } else {
                                //继续定时查扫码支付
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            cal();
        }

        @Override
        public void onError(Call call, Exception e, int id) {
        }
    };

    private void cal() {
        if (orderFinishRequestCall != null) {
            orderFinishRequestCall.cancel();
            orderFinishRequestCall = null;
        }
    }
}
