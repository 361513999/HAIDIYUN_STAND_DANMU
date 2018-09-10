package pad.stand.com.haidiyun.www.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.Led;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.widget.NewDataToast;
import pad.stand.com.haidiyun.www.wifi.WifiConnect;

public class NoHandlerTask extends BroadcastReceiver {
    private static CountDownTimer conTimer;
    SharedUtils sharedUtils = new SharedUtils(Common.SHARED_WIFI);
    SharedUtils config = new SharedUtils(Common.CONFIG);
    private boolean WIFI_NOT_COND = true;

    private synchronized void shandeng() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (WIFI_NOT_COND) {
                    try {
                        Led.reset(1);
                    } catch (UnsatisfiedLinkError e) {
                        e.printStackTrace();
                        P.c("灯光异常");
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Led.close();
                    } catch (UnsatisfiedLinkError e) {
                        e.printStackTrace();
                        P.c("灯光异常");
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

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
                State state = networkInfo.getState();
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
                    shandeng();

                } else if (state.name().equals("CONNECTING")) {
                    System.out.println(state.name());
                    // NewDataToast.Text(context, "尝试连接WIFI",100).show();
                    WIFI_NOT_COND = false;
                } else if (state.name().equals("CONNECTED") && networkInfo.isConnected()) {
                    // NewDataToast.Text(context, "WIFI连接成功",100).show();
                    System.out.println(state.name());
                }
            }


        } else if ("pad.tuch.screen".equals(intent.getAction())) {
            P.c("-------------------点击----------------------");
            //如果是点击屏幕操作
            if (conTimer != null) {
                conTimer.cancel();
            }
            /*if (conTimer == null) {
                conTimer = new CountDownTimer(60000, 1000) {

                    @Override
                    public void onTick(long arg0) {


                    }

                    @Override
                    public void onFinish() {

                        if (Common.CAN_BACK) {
                            if (config.getBooleanValue("is_advert")) {
                                try {
                                    Intent it = new Intent();
                                    it.setAction(Common.ACTION_ADVERT);
                                    it.putExtra("playaction", "free");//空闲时
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(it);
                                    AppManager.getAppManager().finishAllActivity();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    //NewDataToast.makeText("没有检测到");
                                }
                            }


                            try {

                                AppManager.getAppManager().finishActivity(MainFragmentActivity.class);
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                            try {

                                AppManager.getAppManager().finishActivity(CardValActivity.class);
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                            try {

                                AppManager.getAppManager().finishActivity(ShowImages.class);
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                            try {

                                AppManager.getAppManager().finishActivity(PriceActivity.class);
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
            conTimer.start();*/

        } else if ("pad.tuch.screen.pause".equals(intent.getAction())) {
            P.c("-------------------暂停----------------------");
            if (conTimer != null) {
                conTimer.cancel();
            }
        } else if ("pad.haidiyun.command".equals(intent.getAction())) {
            //
            if (intent.hasExtra("basedir") && intent.hasExtra("from")) {
                Intent it = new Intent();
                it.setAction(intent.getStringExtra("from"));
                it.putExtra("basedir", Common.SD);
                context.sendBroadcast(it);
            }
        } else if ("com.zed.Usb.ReturnQRcode".equals(intent.getAction())) {
            String data = intent.getStringExtra("RetQRcode");
            if (!data.equals("SocKet is busy") && !data.equals("")) {
                saveUrlDiscount(data);
            }
        }
    }

    private void saveUrlDiscount(String data) {
        try {
            P.c("二维码系统返回地址:" + data);
            JSONObject object = new JSONObject(data);
            JSONObject objDisc = object.getJSONObject("obj");
            if (object.getBoolean("isok") && objDisc.getInt("isDiscount") == 1) {
                String urlDiscount = object.getString("Remark");
                if (urlDiscount.contains("d_")) {
                    config.setStringValue("urlDiscount", urlDiscount);
                    config.setBooleanValue("canDiscount", true);
                    P.c("打折二维码解析后地址:" + urlDiscount);
                }
            } else {
                config.setBooleanValue("canDiscount", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
