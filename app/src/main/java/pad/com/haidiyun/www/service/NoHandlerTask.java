package pad.com.haidiyun.www.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.Led;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.widget.NewDataToast;
import pad.com.haidiyun.www.wifi.WifiConnect;

public class NoHandlerTask extends BroadcastReceiver {
    private static CountDownTimer conTimer;
    private static final String TAG = "NoHandlerTask";
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
        // TODO Auto-generated method stub

        if (intent.getAction().equals(Common.FOC_CHANGE)) {
            //切换WIFI
            WifiConnect connect = new WifiConnect(context);
            SharedUtils sharedUtils = new SharedUtils(Common.SHARED_WIFI);
            String wifiName = sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME);
            String wifiPass = sharedUtils.getStringValue(Common.LOCKED_WIFI_PASS);

            if (wifiName.length() != 0 && wifiPass.length() != 0) {
//                connect.reset(context, wifiName, wifiPass, null);
            } else {
                NewDataToast.makeText("请先配置WIFI");
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
                    shandeng();
                } else if (state.name().equals("CONNECTING")) {
                    System.out.println(state.name());
                    // NewDataToast.Text(context, "尝试连接WIFI",100).show();
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
                conTimer = null;
            }
            /*if (conTimer == null) {
                conTimer = new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long arg0) {
                        // TODO Auto-generated method stub

//                        P.c("倒计时" + arg0);
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub

                        if (config.getBooleanValue("is_advert")) {
                            try {
                                Intent it = new Intent();
                                it.setAction(Common.ACTION_ADVERT);
                                it.putExtra("playaction", "free");//空闲时
                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(it);
                                AppManager.getAppManager().finishAllActivity();
                                android.os.Process.killProcess(android.os.Process.myPid());
                                Log.e(TAG, "广告页面is_advert");
                            } catch (Exception e) {
                                // TODO: handle exception
                                //NewDataToast.makeText("没有检测到");
                            }
                        }
                        try {
                            //
                            Common.LAST_PAGE = 0;
                            AppManager.getAppManager().finishActivity(MainFragmentActivity.class);
                            Log.e(TAG, "广告页面is_advert_no");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                };
            }
            conTimer.start();*/

        } else if ("pad.tuch.screen.pause".equals(intent.getAction())) {
            P.c("-------------------暂停----------------------");
        /*	if(conTimer!=null){
				 conTimer.cancel();
			 }*/
        } else if ("pad.com.haidiyun.www.val".equals(intent.getAction())) {

            Intent mIntent = new Intent();
            mIntent.setAction("pad.com.haidiyun.www.val");
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        } else if ("pad.haidiyun.command".equals(intent.getAction())) {
            Log.e(TAG, "显示广告页面");

            //
            if (intent.hasExtra("basedir") && intent.hasExtra("from")) {
                Intent it = new Intent();
                String code = intent.getStringExtra("from");
                it.setAction(intent.getStringExtra("from"));
                it.putExtra("basedir", Common.SD);
                context.sendBroadcast(it);


                Intent intentService = new Intent();
                intentService.setAction("flip.pad.com.invisible");
                context.sendBroadcast(intentService);
            }
        }
    }

}
