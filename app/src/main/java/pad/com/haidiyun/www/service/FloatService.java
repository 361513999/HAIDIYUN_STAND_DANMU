package pad.com.haidiyun.www.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.Led;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.utils.Tip_Utils;
import pad.com.haidiyun.www.widget.CommonConfigPop;

public class FloatService extends Service {

    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private View float_service;
    private TextView wifi_status, touch, touch_tip, touch_view;
    private View fs;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private int state;
    private float StartX;
    private float StartY;
    private int delaytime = 3000;
    private int tableDelay = 10000;
    private SharedUtils sharedUtils, configUtils;
    private Tip_Utils tip_Utils;
    private LinearLayout move_layout;
    private WifiManager mWifiManager;
    private ProgressBar pw_spinner;
    private ViewHandler viewHandler;
    private BatteryReceiver batteryReceiver;
    private Typeface tf;
    FrameLayout fl_service;
    private ServiceReceiver servicereceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        servicereceiver = new ServiceReceiver();
        IntentFilter filterO = new IntentFilter();
        filterO.addAction("flip.pad.com.invisible");
        filterO.addAction("flip.pad.com.visible");
        registerReceiver(servicereceiver, filterO);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        viewHandler = new ViewHandler(this);
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        float_service = LayoutInflater.from(this).inflate(
                R.layout.flip_float_service, null);
        configUtils = new SharedUtils(Common.CONFIG);
        createView();
        pw_spinner = (ProgressBar) float_service.findViewById(R.id.pw_spinner);
        fl_service = (FrameLayout) float_service.findViewById(R.id.fl_service);
        wifi_status = (TextView) float_service.findViewById(R.id.wifi_status);
        touch = (TextView) float_service.findViewById(R.id.touch);
        touch_view = (TextView) float_service.findViewById(R.id.touch_view);
        tf = Typeface.createFromAsset(getAssets(), "font/mb.ttf");
        touch_tip = (TextView) float_service.findViewById(R.id.touch_tip);
        touch_tip.setTypeface(tf);
        tip_Utils = new Tip_Utils();
        sharedUtils = new SharedUtils(Common.SHARED_WIFI);
        handler.post(task);
        tableHandler.post(tableRunnable);
        shandler.postDelayed(stask, sdelaytime);
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
        fl_service.setVisibility(View.GONE);
    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 获取当前电量
                int level = intent.getIntExtra("level", 0);
                // 电量的总刻度
                int scale = intent.getIntExtra("scale", 100);
                // 设置当前电量
                int progress = (level * 100) / scale;

                // 是否在充电
                // int status = intent
                // .getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                // boolean isCharging = status ==
                // BatteryManager.BATTERY_STATUS_CHARGING
                // || status == BatteryManager.BATTERY_STATUS_FULL;
                // int currPower = (progress * 36 / 10);
                P.c(level + "电量" + progress + "--" + scale);
                pw_spinner.setProgress(level);
                /*
                 * if (isCharging) {// 充电
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsCharging); }
				 * else { if (progress >= 80) {// 绿色#00FF00
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsChange80); } if
				 * (progress > 40 && progress < 80) {// 黄色#FFFF00
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsChange40); } if
				 * (progress <= 40) {// 红色#EC6841
				 * batteryHandler.sendEmptyMessage(Msg_Battery_IsChange00); } }
				 */
            }
        }
    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("flip.pad.com.invisible".equals(intent.getAction())) {
                //做隐藏操作
                fl_service.setVisibility(View.GONE);
            } else if ("flip.pad.com.visible".equals(intent.getAction())) {
                //做隐藏操作
//                fl_service.setVisibility(View.VISIBLE);
                fl_service.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 透明事件穿透模式
     */
    private long lastClick;

    private void createView() {
        // 获取WindowManager
        wm = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        wmParams = (BaseApplication.application).getMywmParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.format = PixelFormat.RGBA_8888;
        // wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        String flag = configUtils.getStringValue("isOldVersion");
        if (flag.equals("rk312x")) {
            wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
        } else {
            wmParams.gravity = Gravity.RIGHT | Gravity.TOP; // 调整悬浮窗口至左上角
        }
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wm.addView(float_service, wmParams);
        float_service.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                CommonConfigPop configPop = new CommonConfigPop(
                        FloatService.this);
                configPop.showSheet();
                return true;
            }
        });
        /*float_service.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (System.currentTimeMillis() - lastClick <= 1000) {
                    return;
                }
                lastClick = System.currentTimeMillis();
                if (AppManager.getAppManager().currentActivity() instanceof LanuchActivity) {
                    fl_service.setVisibility(View.GONE);
                    Intent intent = new Intent();
                    intent.setAction("KeyEvent.KEYCODE_BACK_Laucher");
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("KeyEvent.KEYCODE_BACK");
                    sendBroadcast(intent);
                }


                // 发一个时间更改的通知

            }
        });*/

		/*
         * float_service.setOnTouchListener(new OnTouchListener() { public
		 * boolean onTouch(View v, MotionEvent event) { // 获取相对屏幕的坐标，即以屏幕左上角为原点
		 * x = event.getRawX(); y = event.getRawY() - 25; // 25是系统状态栏的高度
		 * Log.i("currP", "currX" + x + "====currY" + y);// 调试信息 switch
		 * (event.getAction()) { case MotionEvent.ACTION_DOWN: state =
		 * MotionEvent.ACTION_DOWN; StartX = x; StartY = y; //
		 * 获取相对View的坐标，即以此View左上角为原点 mTouchStartX = event.getX(); mTouchStartY =
		 * event.getY(); Log.i("startP", "startX" + mTouchStartX + "====startY"
		 * + mTouchStartY);// 调试信息 break; case MotionEvent.ACTION_MOVE: state =
		 * MotionEvent.ACTION_MOVE; updateViewPosition(); break;
		 *
		 * case MotionEvent.ACTION_UP: state = MotionEvent.ACTION_UP;
		 * updateViewPosition(); mTouchStartX = mTouchStartY = 0; break; }
		 * return true; } });
		 */

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("recy_table")) {
            P.c("切换桌台");
            find();
        } else if (intent != null && intent.hasExtra("open_table")) {
            viewHandler.sendEmptyMessage(22);
        } else if (intent != null && intent.hasExtra("open_buy")) {
            String view = intent.getStringExtra("open_buy");
            if (float_service != null) {

               /* if (view.equals("0")) {
                    wmParams.gravity = Gravity.LEFT | Gravity.TOP;
                } else {
                    wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
                }*/
//                String flag = configUtils.getStringValue("isOldVersion");
//                if (flag.equals("1")) {
//                    wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
//                } else {
//                    wmParams.gravity = Gravity.RIGHT | Gravity.TOP; // 调整悬浮窗口至左上角
//                }
//                wm.updateViewLayout(float_service, wmParams);
//				float_service.setVisibility(view.equals("0")?View.GONE:View.VISIBLE);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            dataRefresh();
            handler.postDelayed(this, delaytime);
            wm.updateViewLayout(float_service, wmParams);
            // 在这里进行电量的控制变化
            if (float_service != null) {
                /**
                 * 耗时操作用线程执行
                 */
               // pad.stand.com.haidiyun.www.common.P.c("显示图标-->checkAc-->电量");
                new checkActivityThread().start();
            }

        }
    };
    private Handler tableHandler = new Handler();
    private Runnable tableRunnable = new Runnable() {
        public void run() {
            find();
            // 获取沽清
            String ip = configUtils.getStringValue("IP");
            if (ip.length() != 0) {
                gu(ip);
            }
            // HQGQ
            tableHandler.postDelayed(this, tableDelay);

        }
    };
    int count = 0;

    public void dataRefresh() {
        if (wifi_status != null) {
            if (sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME).length() != 0) {
                // 证明有数据存在
                try {
                    WifiInfo info = mWifiManager.getConnectionInfo();
                    // P.c(info.getBSSID()+"=="+info.getSSID());
                    wifi_status.setText(info.getSSID().replace("\"", ""));
                    wifi_status.setVisibility(View.VISIBLE);
                    // P.c(info.getBSSID()+"=="+info.getSSID());
                    if (!info.getBSSID().equals("00:00:00:00:00:00")) {
                        if (!info
                                .getSSID()
                                .replace("\"", "")
                                .equals(sharedUtils
                                        .getStringValue(Common.LOCKED_WIFI_NAME))) {
                            // 切换网络

                        }
                        count = 0;
                    } else {
                        count++;
                        if (count > 10) {
                            //sharedUtils.clear();
                        }
                    }
                } catch (Exception e) {
                    wifi_status.setText("WIFI获取有误");
                    wifi_status.setVisibility(View.VISIBLE);
                }
            } else {
                wifi_status.setText("请先配置WIFI");
                wifi_status.setVisibility(View.VISIBLE);
//                String ssid = mWifiManager.getConnectionInfo().getSSID();
//                wifi_status.setText(ssid);
            }
        }
        // 进行桌台号的选取
        if (touch != null) {
            if (configUtils.getStringValue("table_name").length() != 0) {
                /*
                 * if(!touch.getText().toString().equals(configUtils.getStringValue
				 * ("table_name"))){ FrameLayout.LayoutParams params = new
				 * FrameLayout
				 * .LayoutParams(float_service.getMeasuredWidth()-44,4);
				 * params.setMargins(0, 2, 0, 0); params.gravity =
				 * Gravity.CENTER_HORIZONTAL;
				 * pw_spinner.setLayoutParams(params);
				 * pw_spinner.setVisibility(View.VISIBLE);
				 *
				 * }
				 */
                touch.setText(configUtils.getStringValue("table_name"));

            } else {
                touch.setText("请配置桌台号");
            }

        }
        // 查询桌台

    }

    /**
     * 查找更新数据
     */
    private void find() {
        // {'CMD':'TB','CONTENT':{'MhtTableArea':[{"TableAreaId":1},…]
        if (configUtils.getStringValue("table_code").length() != 0
                && configUtils.getStringValue("IP").length() != 0) {
            P.c("--查询状态--");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject
                        .put("data", configUtils.getStringValue("table_code"));
                String ip = configUtils.getStringValue("IP");
                OkHttpUtils
                        .postString()
                        .url(U.VISTER(ip, U.URL_REFREF_TABLE_STATUS))
                        .mediaType(
                                MediaType
                                        .parse("application/json; charset=utf-8"))
                        .content(jsonObject.toString()).build()
                        .execute(tableCallback);
//				P.c(TimeUtil.getTime(System.currentTimeMillis()));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            // 什么都没有那么就把桌台状态清掉
            configUtils.clear("person");
            configUtils.clear("billId");
            viewHandler.sendEmptyMessage(33);
//			P.c("未知桌台");
        }
    }

    private StringCallback tableCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            // TODO Auto-generated method stub
            try {
                JSONObject jsonObject = new JSONObject(
                        FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    // 成功
                    JSONArray array = jsonObject.getJSONArray("Data");
                    int len = array.length();
                    if (len != 0 && len == 1) {
                        JSONObject obj = array.getJSONObject(0);
                        String state = obj.getString("State");
                        if (state.equals("F")) {
                            // 空台
                            configUtils.clear("person");
                            configUtils.clear("billId");
                            viewHandler.sendEmptyMessage(11);
                        } else if (state.equals("O")) {
                            // 就餐中
                            configUtils.setIntValue("person",
                                    obj.getInt("GstCount"));
                            configUtils.setStringValue("billId",
                                    obj.getString("BillNo"));
                            viewHandler.sendEmptyMessage(22);
                        } else if (state.equals("P")) {
                            //就餐中
                            configUtils.setIntValue("person", obj.getInt("GstCount"));
                            //这里有变化需要改变
                            configUtils.setStringValue("billId", obj.getString("BillNo"));
                            viewHandler.sendEmptyMessage(23);
                        }

                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            // TODO Auto-generated method stub
//			P.c(e.getMessage());
        }
    };
    /**
     * 沽清
     */
    private RequestCall requestCall;

    private void gu(String ip) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "[{\"TabName\":\"GetSaleOutMenus\",\"Timestamp\":0}]");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        requestCall = OkHttpUtils
                .post()
                .url(U.VISTER(ip, U.URL_GU))
                //.mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build();
        requestCall.execute(guCallback);
        //P.c("提交沽清查询");
    }

    private StringCallback guCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
            // TODO Auto-generated method stub

            new Thread() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(
                                FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            Common.guKeys.clear();
                            JSONArray json = jsonObject.getJSONArray("Data");

                            int len = json.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject obj = json.getJSONObject(i);
                                Common.guKeys.put(obj.getString("MenuCode"), obj.getInt("LeftQty"));
                            }
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        if (requestCall != null) {
                            requestCall.cancel();
                            requestCall = null;
                        }
                    }
                }

                ;
            }.start();


        }

        @Override
        public void onError(Call call, Exception e, int id) {
            // TODO Auto-generated method stub

        }
    };

    private class ViewHandler extends Handler {
        WeakReference<FloatService> mLeakActivityRef;

        public ViewHandler(FloatService leakActivity) {
            mLeakActivityRef = new WeakReference<FloatService>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {

                switch (msg.what) {

                    case 11:
                        try {
                            Led.reset(3);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                        }
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText("空闲");
                        // touch_view.setTextColor(getResources().getColor(R.color.green));
                        if (tip_Utils.getBuy()) {
                            tip_Utils.setFirst(false);
                            tip_Utils.setWel(false);
                            tip_Utils.setBuy(false);
                        }
                        Intent intentService = new Intent();
                        intentService.setAction("pad.haidiyun.www.change_price");
                        intentService.putExtra("refreshData", true);
                        sendBroadcast(intentService);
                        break;
                    case 22:
                        try {
                            Led.reset(5);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                            P.c("灯光异常");
                        }
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText("就餐中");
                        // touch_view.setTextColor(getResources().getColor(R.color.red));
                        break;
                    case 23:
                        try {
                            Led.reset(5);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                            P.c("灯光异常");
                        }
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText("已打单");
                        break;
                    case 33:
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText("未知桌台");
                        // touch_view.setTextColor(getResources().getColor(R.color.grey));
                        break;
                    default:
                        break;

                }
            }

        }
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wm.updateViewLayout(float_service, wmParams);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onDestroy() {
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
        if (servicereceiver != null) {
            unregisterReceiver(servicereceiver);
        }
        handler.removeCallbacks(task);
        tableHandler.removeCallbacks(tableRunnable);
        wm.removeView(float_service);
        super.onDestroy();
    }

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        FloatService getService() {
            return FloatService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // ------------------------提交数据到服务器
    private String serverURL = "http://www.haidiyun.top";

    public String getDeviceId() {
        // 根据Wifi信息获取本地Mac
        String ANDROID = "";
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            ANDROID = info.getMacAddress();
        }
        return ANDROID;
    }

    public boolean isNec(String urlString) throws IOException {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        String is = pm.isScreenOn() ? "1" : "0";
        String mac = getDeviceId();
        URL url = new URL(urlString
                + "/WebService/WebService.asmx/DeviceStatus?lcd_On=" + is
                + "&mac=" + mac);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.connect();
        return httpUrlConnection.getResponseCode() == 200 ? true : false;
    }

    private int sdelaytime = 1000 * 60 * 5;
    private Handler shandler = new Handler();
    private Runnable stask = new Runnable() {
        public void run() {
            dataRefresh();

            shandler.postDelayed(this, sdelaytime);
            // 在这里进行电量的控制变化

        }

        private void dataRefresh() {
            // TODO Auto-generated method stub

            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    try {
                        // 没有“/”形式的http://域名
                        for (int i = 0; i < 3; i++)
                            if (isNec(serverURL)) {
                                sdelaytime = 1000 * 60 * 60;

                                break;
                            } else {
                                sdelaytime = 1000 * 60 * 5;
                            }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    };
    private ActivityManager am;

    private String getTopActivity() {


        if (Build.VERSION.SDK_INT <= 20) {
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            if (tasks != null && !tasks.isEmpty()) {
                ComponentName componentName = tasks.get(0).topActivity;
                if (componentName != null) {
                    return componentName.getPackageName();
                }
            }
        } else {
            /*if(Build.VERSION.SDK_INT==24){


					BufferedReader cmdlineReader = null;
					try {
						cmdlineReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + android.os.Process.myPid() + "/cmdline"), "iso-8859-1"));
						int c;
						StringBuilder processName = new StringBuilder();
						while ((c = cmdlineReader.read()) > 0) {
							processName.append((char) c);
						}
						return processName.toString();
					} catch (Exception ignore) {
					} finally {
						try {
							if (cmdlineReader != null) {
								cmdlineReader.close();
							}
						} catch (IOException ignore) {
						}
					}
					return "";

			}else{*/
            ActivityManager.RunningAppProcessInfo currentInfo = null;
            Field field = null;
            int START_TASK_TO_FRONT = 2;
            String pkgName = null;
            try {
                field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (Exception e) {
                return null;
            }
            List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
            if (appList == null || appList.isEmpty()) {
                return null;
            }
            for (ActivityManager.RunningAppProcessInfo app : appList) {
                if (app != null && app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Integer state = null;
                    try {
                        state = field.getInt(app);
                    } catch (Exception e) {
                        return null;
                    }
                    if (state != null && state == START_TASK_TO_FRONT) {
                        currentInfo = app;
                        break;
                    }
                }
            }
            if (currentInfo != null) {
                pkgName = currentInfo.processName;
            }
            return pkgName;
        }


//		}
        return null;
    }

    class checkActivityThread extends Thread {
        @Override
        public void run() {
            String result = getTopActivity();
            P.c("监测--"+result);
            if (result.equals(BaseApplication.application.packgeName)) {
                sendBroadcast(new Intent("flip.pad.com.visible"));
            } else {
                pad.stand.com.haidiyun.www.common.P.c("隐藏图标checkActivityThread");
                sendBroadcast(new Intent("flip.pad.com.invisible"));
            }

        }
    }

}
