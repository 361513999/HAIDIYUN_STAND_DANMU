package pad.stand.com.haidiyun.www.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pad.stand.com.haidiyun.www.base.BaseApplication;

public class Common {
    public static String DEFAULT_NUM = "1";
    public static String COMMON_PASS = "8888";
    public static String BASE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static String SD = BASE_DIR + "/HAIDIYUN_STAND/";
    public static String ZIP = SD + "ZIP/";
    public final static String DB_DIR = "data/data/" + BaseApplication.application.getPackageName() + "/databases/";
    public static String SOURCE = SD + "IMAGES/";
    public static String SOURCE_VIDEO = SD + "VIDEOS/";
    public static String SOURCE_APK = SD + "APK/";
    public static String SOURCE_ADVER = SD + "ADVER/";
    public static String APK_LOG = SD + "LOG/";

    public static String ACTION_ADVERT = "pad.stand.com.haidiyun.www.start";

    //皮肤地址
    public static String SOURCE_SKIN = SD + "SKIN/";
    public static String json = "config.json";
    public static String tables = "tables.json";
    public static final String CONFIG = "config";
    //临时给一个营业点
    public static String SITE_CODE = "";
    //判断是否是切换到广告页
    public static int DOING = -1;
    public static final String LOCKED_WIFI_NAME = "wifi_lock_name";
    public static final String LOCKED_WIFI_PASS = "wifi_lock_pass";
    public static final String SHARED_WIFI = "wifi_lock_config";
    public static final String DB_NAME = "haidiyun_stand.db";
    public static final int DB_VERSION = 31;
    //---------------------
    public static final String FOC_CHANGE = "org.wifi.reset";
    //-----------------
    //引导点击
    public static final String TIP_CONFIG = "tip_config";
    public static final String tip_click = "tip_click";
    public static final String tip_first = "tip_first";
    public static final String tip_buy = "tip_buy";
    public static final String SERVICE_ACTION = "pad.stand.com.haidiyun.www.float";
    //视频索引
    public static final String VIDEO_TAG = "video_tag";
    //控制返回,默认是可以返回的
    public static boolean CAN_BACK = true;
    public static String TOUCH_DOWN = "pad.tuch.screen";
    public static String TOUCH_PAUSE = "pad.tuch.screen.pause";
    public static Map<String, Integer> guKeys = new HashMap<String, Integer>();
    public static Map<String, String> guSttxKeys = new HashMap<String, String>();
    //-------------NFC属性
    public static String[][] TECHLISTS;
    public static IntentFilter[] FILTERS;

    static {
        try {
            TECHLISTS = new String[][]{{IsoDep.class.getName()},
                    {NfcV.class.getName()}, {NfcF.class.getName()}, {MifareClassic.class.getName()}, {MifareUltralight.class.getName()}};

            //			FILTERS = new IntentFilter[] { new IntentFilter(
            //					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
            FILTERS = new IntentFilter[]{new IntentFilter(
                    NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
        } catch (Exception e) {
        }
    }

    /**
     * aidl 隐式转显式
     * @param context
     * @param implicitIntent
     * @return
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
