package pad.stand.com.haidiyun.www.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.UsbSupply;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.widget.NewDataToast;
import pad.stand.com.haidiyun.www.wifi.WifiConnect;

/**
 * Created by zed on 18-5-24.
 */
public class NohandlerSevice extends Service {

    SharedUtils sharedUtils = new SharedUtils(Common.SHARED_WIFI);
    SharedUtils config = new SharedUtils(Common.CONFIG);
    private boolean WIFI_NOT_COND = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler handler = new Handler();
    Runnable ledteston = new Runnable() {
        @Override
        public void run() {
            try {

                UsbSupply.LedControlSet(1);
            } catch (Exception e) {
                // TODO: handle exception
            }
            P.c("led---on");
            handler.removeCallbacks(ledteston);
            handler.postDelayed(ledtestoff, 1000);
        }
    };
    Runnable ledtestoff = new Runnable() {
        @Override
        public void run() {
            try {
                UsbSupply.LedControlSet(2);
                UsbSupply.LedControlSet(4);
                UsbSupply.LedControlSet(6);
            } catch (Exception e) {
                // TODO: handle exception
            }
            P.c("led---off");

            handler.removeCallbacks(ledtestoff);
            handler.postDelayed(ledteston, 1000);
        }
    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        P.c("创建了NohandlerSevice");
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Common.FOC_CHANGE);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction("pad.tuch.screen");
        mIntentFilter.addAction("pad.haidiyun.command");
        mIntentFilter.addAction("pad.tuch.screen.pause");
        mIntentFilter.addAction("org.wifi.reset");
        mIntentFilter.addAction("com.zed.Usb.ReturnQRcode");


        /**
         *  <action android:name="org.wifi.reset" />
         <action android:name="pad.tuch.screen" />
         <action android:name="pad.tuch.screen.pause" />
         <action android:name="android.net.wifi.STATE_CHANGE" />
         <action android:name="pad.haidiyun.command" />
         */

        registerReceiver(NoHandlerReciver, mIntentFilter);

        UsbSupply.LedControlOpen();
        //  handler.postDelayed(ledteston, 1000) ;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(NoHandlerReciver);
        handler.removeCallbacks(ledteston);
        handler.removeCallbacks(ledtestoff);
        super.onDestroy();
    }

    public BroadcastReceiver NoHandlerReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Common.FOC_CHANGE)) {
                //切换WIFI
                WifiConnect connect = new WifiConnect(context);
                String wifiName = sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME);
                String wifiPass = sharedUtils.getStringValue(Common.LOCKED_WIFI_PASS);

                if (wifiName.length() != 0 && wifiPass.length() != 0) {
                    connect.reset(context, wifiName, wifiPass, null, null);
                } else {
                    NewDataToast.makeText(context.getResources().getString(R.string.config_wifi));
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                //WIFI状态
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelableExtra != null) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    System.out.println(wifiState + "-------->" + state.name() + "---" + networkInfo.isConnected());
                    if (state.name().equals("DISCONNECTED")) {
                        System.out.println(state.name());
                        // NewDataToast.Text(context, "断开WIFI",100).show();

                   /* try {
                        Led.reset(1);
                    } catch (UnsatisfiedLinkError e) {
                        e.printStackTrace();
                        P.c("灯光异常");
                    }*/
                        WIFI_NOT_COND = true;
                        //shandeng();
                        handler.removeCallbacks(ledteston);
                        handler.removeCallbacks(ledtestoff);
                        handler.postDelayed(ledteston, 1000);

                    } else if (state.name().equals("CONNECTING")) {
                        System.out.println(state.name());
                        // NewDataToast.Text(context, "尝试连接WIFI",100).show();
                        WIFI_NOT_COND = false;
                        try {
                            UsbSupply.LedControlSet(2);
                            UsbSupply.LedControlSet(4);
                            UsbSupply.LedControlSet(6);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                            P.c("灯光异常");
                        }
                        handler.removeCallbacks(ledteston);
                        handler.removeCallbacks(ledtestoff);
                    } else if (state.name().equals("CONNECTED") && networkInfo.isConnected()) {
                        // NewDataToast.Text(context, "WIFI连接成功",100).show();
                        System.out.println(state.name());
                    }
                }


            } else if ("pad.tuch.screen".equals(intent.getAction())) {
                P.c("-------------------点击----------------------");
                //如果是点击屏幕操作
                // if (conTimer != null) {
                //  conTimer.cancel();
                //  }

            } else if ("pad.tuch.screen.pause".equals(intent.getAction())) {
                P.c("-------------------暂停----------------------");
                //  if (conTimer != null) {
                //  conTimer.cancel();
                //   }
            } else if ("pad.haidiyun.command".equals(intent.getAction())) {
                P.c("-------------------命令==command----------------------");
                if (intent.hasExtra("basedir") && intent.hasExtra("from")) {
                    Intent it = new Intent();
                    P.c("from-----" + intent.getStringExtra("from"));
                    it.setAction(intent.getStringExtra("from"));
                    it.putExtra("basedir", Common.SD);
                    context.sendBroadcast(it);


                    Intent intentService = new Intent();
                    intentService.setAction("com.zed.play.start");
                    context.sendBroadcast(intentService);

                }
            } else if ("com.zed.Usb.ReturnQRcode".equals(intent.getAction())) {
                String data = intent.getStringExtra("RetQRcode");
                if (!data.equals("SocKet is busy") && !data.equals("")) {
                    saveUrlDiscount(data);
                }
            }
        }
    };

    //    打折地址:{"Remark":"http://watch.haidiyun.top/WxQrcodeImg/H800200001745001763.jpg","isok":true,"obj":{"isShow":1,"isDiscount":0},"rows":null,"total":0}
    private void saveUrlDiscount(String data) {
        try {
            P.c("打折二维码地址:" + data);
//            {"Remark":null,"isok":false,"obj":{"isDiscount":0},"rows":null,"total":0}
            JSONObject object = new JSONObject(data);
            if (object.getBoolean("isok")) {
                JSONObject objDisc = object.getJSONObject("obj");
                if (!objDisc.has("isShow")) {
                    return;
                }
                if (object.getBoolean("isok") && objDisc.getInt("isShow") == 1) {
                    String urlDiscount = object.getString("Remark");
                    if (urlDiscount.contains("d_")) {
                        config.setStringValue("urlDiscount", urlDiscount);
                        config.setBooleanValue("canDiscount", true);
                        P.c("打折二维码解析后地址:" + urlDiscount);
                    }
                    if(objDisc.has("Tip")){
                        config.setStringValue("Tip",objDisc.getString("Tip"));
                    }
                    if(objDisc.has("Tip_en")){
                        config.setStringValue("Tip_en",objDisc.getString("Tip_en"));
                    }
                    if(objDisc.has("BgUrl")){
                        config.setStringValue("BgUrl",objDisc.getString("BgUrl"));
                    }
                    if (objDisc.getInt("isDiscount") == 1) {
                        config.setBooleanValue("canShowDiscount", true);
                        P.c("允许折扣显示");
                    } else {
                        config.setBooleanValue("canShowDiscount", false);
                    }
                } else {
                    config.setBooleanValue("canDiscount", false);
                }
            } else {
                config.setBooleanValue("canDiscount", false);
                config.setBooleanValue("canShowDiscount", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
