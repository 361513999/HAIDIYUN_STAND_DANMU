package pad.stand.com.haidiyun.www.service;


import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.Led;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.widget.CommonConfigPop;
import pad.stand.com.haidiyun.www.widget.NewDataToast;
import pad.stand.com.haidiyun.www.widget.ProgressWheel;
import pad.stand.com.haidiyun.www.widget.SettingMM1Dialog;

public class FloatService extends Service {

    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private View float_service;
    private TextView wifi_status, touch_view;
    private View fs;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private int state;
    private float StartX;
    private float StartY;
    private int delaytime = 10000;
    private int tableDelay = 10000;
    //	private int tableDelay = 2000;
    private SharedUtils sharedUtils, configUtils;
    private LinearLayout move_layout;
    private WifiManager mWifiManager;
    private ProgressWheel home_progress_bar;
    private BatteryReceiver batteryReceiver;
    //	private Typeface tf;
    private ViewHandler viewHandler;
    private ImageView home_bg_logo;
    private ServiceReceiver servicereceiver;
    private checkActivityThread McheckActivityThread;
//    private LinearLayout fl_service;

    @Override
    public void onCreate() {
        super.onCreate();

        P.c("创建了FloatService");
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        servicereceiver = new ServiceReceiver();
        IntentFilter filterO = new IntentFilter();
        filterO.addAction("pad.com.invisible");
        filterO.addAction("pad.com.visible");
        filterO.addAction("pad.com.change.vis");
        registerReceiver(servicereceiver, filterO);
        //-----------------------------
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        float_service = LayoutInflater.from(this).inflate(R.layout.float_service, null);
        P.c("这里=============bug4");
        createView();
        P.c("这里=============bug3");
        viewHandler = new ViewHandler(this);
        wifi_status = (TextView) float_service.findViewById(R.id.wifi_status);
//        fl_service = (LinearLayout) float_service.findViewById(R.id.fl_service);
        home_bg_logo = (ImageView) float_service.findViewById(R.id.home_bg_logo);
        home_progress_bar = (ProgressWheel) float_service.findViewById(R.id.home_progress_bar);
        touch_view = (TextView) float_service.findViewById(R.id.touch_view);
        //tf = Typeface.createFromAsset(getAssets(), "font/mb.ttf");
        P.c("这里=============bug");
        sharedUtils = new SharedUtils(Common.SHARED_WIFI);
        configUtils = new SharedUtils(Common.CONFIG);
        P.c("这里=============bug1");
        handler.postDelayed(task, delaytime);
        tableHandler.post(tableRunnable);
        batteryReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.shop_icon,
                home_bg_logo,
                options);
      /*  if (configUtils.getBooleanValue("is_cy")) {
            float_service.setVisibility(View.GONE);
        }*/
        float_service.setVisibility(View.GONE);
    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if ("pad.com.invisible".equals(intent.getAction())) {
                //做隐藏操作
                P.c("隐藏图标1");

                float_service.setVisibility(View.GONE);
            } else if ("pad.com.visible".equals(intent.getAction())) {
                //做隐藏操作
                P.c("显示图标");
                //
                float_service.setVisibility(View.GONE);
             /*   if (configUtils.getBooleanValue("is_cy")) {
                    float_service.setVisibility(View.GONE);
                } else {
                    float_service.setVisibility(View.VISIBLE);
                }*/

            } else if ("pad.com.change.vis".equals(intent.getAction())) {
                if (float_service != null) {
                    /**
                     * 耗时操作用线程执行
                     */
                    P.c("显示图标-->checkAc");
                    new checkActivityThread().start();
                }

            }

        }
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
                int status = intent
                        .getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL;
                if (status == 2) {
                    viewHandler.sendEmptyMessage(10);
                } else {
                    viewHandler.sendEmptyMessage(9);
                }
                int currPower = (progress * 36 / 10);
                home_progress_bar.setProgress(currPower);
                if (isCharging) {// 充电
                    viewHandler.sendEmptyMessage(0);
                } else {
                    if (progress >= 80) {// 绿色#00FF00
                        viewHandler.sendEmptyMessage(1);
                    }
                    if (progress > 40 && progress < 80) {// 黄色#FFFF00
                        viewHandler.sendEmptyMessage(2);
                    }
                    if (progress <= 40) {// 红色#EC6841
                        viewHandler.sendEmptyMessage(3);
                    }
                }
            }
        }
    }

    /**
     * 透明事件穿透模式
     */
    private long lastClick;

    private void createView() {
        // 获取WindowManager
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        wmParams = (BaseApplication.application).getMywmParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        wmParams.format = PixelFormat.RGBA_8888;
//					wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wm.addView(float_service, wmParams);

        float_service.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (System.currentTimeMillis() - lastClick > 1000) {
                    lastClick = System.currentTimeMillis();
                } else {


                    final SettingMM1Dialog mmDialog = new SettingMM1Dialog(FloatService.this);
                    mmDialog.getSureBtn().setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (mmDialog.getPassword().equals(Common.COMMON_PASS)) {
                                mmDialog.dismiss();
                                CommonConfigPop configPop = new CommonConfigPop(FloatService.this);
                                configPop.showSheet();
                                //
                                //showSetTableDialog();
                            } else {
                                NewDataToast.makeText(getResources().getString(R.string.wrongnum));
                            }
                        }
                    });
                    mmDialog.show();
                }
            }
        });

		/*float_service.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
				// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				y = event.getRawY() - 25; // 25是系统状态栏的高度
				Log.i("currP", "currX" + x + "====currY" + y);// 调试信息
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);// 调试信息
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;
					updateViewPosition();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("recy_table")) {
            P.c("切换桌台");
            find();
        } else if (intent != null && intent.hasExtra("open_table")) {
            cal();
            try {
                Led.reset(5);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();

            }
            touch_view.setVisibility(View.VISIBLE);
            touch_view.setText(getResources().getString(R.string.repast));
            touch_view.setTextColor(getResources().getColor(R.color.red));
        } else if (intent != null && intent.hasExtra("free_table")) {
            cal();
            try {
                Led.reset(3);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();

            }
            touch_view.setVisibility(View.VISIBLE);
            touch_view.setText(getResources().getString(R.string.free));
            touch_view.setTextColor(getResources().getColor(R.color.green));
        } else if (intent != null && intent.hasExtra("cancel_table")) {
            if (tableRequestCall != null) {
                tableRequestCall.cancel();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void cal() {
        if (tableRequestCall != null) {
            tableRequestCall.cancel();
            tableRequestCall = null;
        }
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            dataRefresh();
            handler.postDelayed(this, delaytime);
            wm.updateViewLayout(float_service, wmParams);
            //在这里进行电量的控制变化
            if (float_service != null) {
                /**
                 * 耗时操作用线程执行
                 */
                P.c("显示图标-->checkAc-->电量");
                new checkActivityThread().start();
            }

        }
    };
    private Handler tableHandler = new Handler();
    private Runnable tableRunnable = new Runnable() {
        public void run() {
//			test();
            find();
            /*String ip = configUtils.getStringValue("IP");
            if (ip.length() != 0) {
				gu(ip);
			}*/
            tableHandler.postDelayed(this, tableDelay);
        }
    };

    public void dataRefresh() {
        //
        if (wifi_status != null) {
            if (sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME).length() != 0) {
                //证明有数据存在
                try {
                    WifiInfo info = mWifiManager.getConnectionInfo();
                    wifi_status.setText(info.getSSID().replace("\"", ""));
                    wifi_status.setVisibility(View.VISIBLE);
                    if (!info.getBSSID().equals("00:00:00:00:00:00")) {
                        if (!info.getSSID().replace("\"", "").equals(sharedUtils.getStringValue(Common.LOCKED_WIFI_NAME))) {
                            //切换网络

                        }
                    }
                } catch (Exception e) {
                    wifi_status.setText(getResources().getString(R.string.wifi_wrong));
                    wifi_status.setVisibility(View.VISIBLE);
                }
            } else {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                P.c("SSID" + wifiInfo.getSSID());
                String systemWifi = wifiInfo.getSSID();
                wifi_status.setVisibility(View.VISIBLE);
                if (!systemWifi.equals("0x")) {
                    wifi_status.setText(systemWifi);
                } else {
                    wifi_status.setText(getResources().getString(R.string.config_wifi));
                }
            }
        }

        //查询桌台

    }

    /**
     * 沽清
     */
    private RequestCall requestCall;

    private void gu(String ip) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "[{\"TabName\":\"NTORestSaleOutMenu\",\"Timestamp\":0}]");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        requestCall = OkHttpUtils
                .postString()
                .url(U.VISTER(ip, U.URL_DOWNLOAD_DATA))
                // .addParams("data",
                // "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        requestCall.execute(guCallback);
        P.c("服务提交沽清查询");
    }

    private final StringCallback guCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                FileUtils.formatJson(response), "服务沽清结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "服务沽清结果");
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(
                                FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            Common.guKeys.clear();
                            JSONObject json = jsonObject.getJSONObject("Data");
                            JSONArray array = json.getJSONArray("NTORestSaleOutMenu");
                            int len = array.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Common.guKeys.put(obj.getString("MenuCode"), 0);
                            }
                        }

                    } catch (JSONException e) {
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

        }
    };
    RequestCall tableRequestCall;

    /**
     * 查找更新数据
     */
    private void find() {
        //{'CMD':'TB','CONTENT':{'MhtTableArea':[{"TableAreaId":1},…]
        if (configUtils.getStringValue("table_code").length() != 0 && configUtils.getStringValue("IP").length() != 0) {
            P.c("--查询状态--");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("data", configUtils.getStringValue("table_code"));
                String ip = configUtils.getStringValue("IP");
                cal();
                tableRequestCall = OkHttpUtils.postString()
                        .url(U.VISTER(ip, U.URL_REFREF_TABLE_STATUS))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonObject.toString())
                        .build();
                if (tableRequestCall != null) {
                    tableRequestCall.execute(tableCallback);
                }

                P.c(TimeUtil.getTime(System.currentTimeMillis()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            //什么都没有那么就把桌台状态清掉
            configUtils.clear("person");
            configUtils.clear("billId");
            viewHandler.sendEmptyMessage(33);
        }
    }


    private StringCallback tableCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

        /*	try {
                P.c(FileUtils.formatJson(response)			);
			} catch (JSONException e) {

				e.printStackTrace();
			}*/
            new Thread() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            //成功
                            JSONArray array = jsonObject.getJSONArray("Data");
                            int len = array.length();
                            if (len != 0 && len == 1) {
                                JSONObject obj = array.getJSONObject(0);
                                String state = obj.getString("State");
                                //P打印状态
                                if (state.equals("F")) {
                                    //空台
                                    configUtils.clear("billId");
                                    //结账清人数
//                                    configUtils.clear("person");
                                    viewHandler.sendEmptyMessage(11);
                                } else if (state.equals("O")) {
                                    //就餐中
                                    configUtils.setIntValue("person", obj.getInt("GstCount"));
                                    //这里有变化需要改变
                                    configUtils.setStringValue("billId", obj.getString("BillNo"));
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

                        e.printStackTrace();
                    }
                }

                ;
            }.start();
            cal();
        }

        @Override
        public void onError(Call call, Exception e, int id) {

//			P.c(e.getMessage());
        }
    };

    private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(0).showImageOnFail(0)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();

    private class ViewHandler extends Handler {
        WeakReference<FloatService> mLeakActivityRef;

        public ViewHandler(FloatService leakActivity) {
            mLeakActivityRef = new WeakReference<FloatService>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {

            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {

                switch (msg.what) {
                    case 9:
                        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.shop_icon,
                                home_bg_logo,
                                options);
                        break;
                    case 10:
//    				home_bg_logo
                        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.charge,
                                home_bg_logo,
                                options);
                        break;
                    //1=空闲,2=就餐,3=预订,4=待清台
                    case 11:

                        try {
                            Led.reset(3);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                        }
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText(getResources().getString(R.string.free));
                        touch_view.setTextColor(getResources().getColor(R.color.green));
                        sendBroadcast(new Intent("buy.free"));

                        break;
                    case 22:
                        try {
                            Led.reset(5);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                            P.c("灯光异常");
                        }
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText(getResources().getString(R.string.repast));
                        touch_view.setTextColor(getResources().getColor(R.color.red));
                        sendBroadcast(new Intent("buy.doing"));
                        break;
                    case 23:
                        try {
                            Led.reset(5);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                            P.c("灯光异常");
                        }
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText(getResources().getString(R.string.lock));
                        touch_view.setTextColor(getResources().getColor(R.color.je_left));
                        sendBroadcast(new Intent("buy.end"));
                        break;
                    case 33:
                        touch_view.setVisibility(View.VISIBLE);
                        touch_view.setText(getResources().getString(R.string.unname_table));
                        touch_view.setTextColor(getResources().getColor(R.color.grey));
                        sendBroadcast(new Intent("buy.un"));
                        break;
                    case 44:
                        try {
                            Led.reset(1);
                        } catch (UnsatisfiedLinkError e) {
                            e.printStackTrace();
                            P.c("灯光异常");
                        }
                        break;
                    case 0:
                        // 绿色00FF7F
                        home_progress_bar.setBarColor(Color.parseColor("#00FF7F"));
                        break;
                    case 1:
                        // 大于80黄绿色99FF00
                        home_progress_bar.setBarColor(Color.parseColor("#99FF00"));
                        break;
                    case 2:
                        // 大于40黄色FFFF00
                        home_progress_bar.setBarColor(Color.parseColor("#FFFF00"));
                        break;
                    case 3:
                        // 大于0橘红色FF6600
                        home_progress_bar.setBarColor(Color.parseColor("#FFCC00"));
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


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
            P.c((BaseApplication.application.packgeName==null)+"是吗"+(result==null));
            if (result!=null&&result.equals(BaseApplication.application.packgeName)) {
                sendBroadcast(new Intent("pad.com.visible"));
            } else {
                P.c("隐藏图标checkActivityThread");
                sendBroadcast(new Intent("pad.com.invisible"));
            }
        }
    }

}
