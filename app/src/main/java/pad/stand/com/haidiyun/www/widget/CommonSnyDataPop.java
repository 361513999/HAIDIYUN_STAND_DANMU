package pad.stand.com.haidiyun.www.widget;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.utils.CopyFile;

/**
 * 同步资源
 *
 * @author Administrator
 */
public class CommonSnyDataPop {
    private Context context;
    private TextView load_tv;
    private IDialog dlg;
    private String msg;
    private ImageView load_img, load_close;
    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    NewDataToast.makeText("请检查与主机的连接配置");
                    cancle();
                    break;
                case -2:
                    NewDataToast.makeText("FTP登录失败,请检查FTP配置");
                    cancle();
                    break;
                case -3:
                    NewDataToast.makeText("取消同步");
                    cancle();
                    break;
                case -4:
                    NewDataToast.makeText("更新文件寻找异常");
                    cancle();
                    break;
                case -5:
                    NewDataToast.makeText("解压出错");
                    cancle();
                    break;
                case 0:
                    NewDataToast.makeText((String) msg.obj);
                    cancle();
                    break;
                case 1:
                    loadTip();
                    break;
                case 3:
                    NewDataToast.makeText("同步完成");
                    //清除一些数据
                    sharedUtils.clear("table_name");
                    sharedUtils.clear("table_code");
                    cancle();
                    BaseApplication.application.resetApplicationAll();
                    break;
                case 2:
                    if (load_tv != null) {
                        load_tv.setText((String) msg.obj);
                    }
                    break;
            }
        }

        ;
    };
    private RequestCall getCall;

    //获取跑马灯资源
    private void loadTip() {
        String regCode = sharedUtils.getStringValue("regCode");
        getCall = OkHttpUtils.post().url(U.URL_TIP).addParams("siteCode", regCode).build();
        getCall.execute(getCallback);
    }


    private StringCallback getCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            try {
                P.c("跑马灯:" + response);
                sharedUtils.clear("tip");
                sharedUtils.clear("tip_en");
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("isok")) {
                    JSONObject objectData = object.getJSONArray("obj").getJSONObject(0);
                    sharedUtils.setStringValue("tip", objectData.getString("Tip"));
                    sharedUtils.setStringValue("tip_en", objectData.getString("Tip_en"));
                }
                handler.sendEmptyMessage(3);
            } catch (JSONException e) {

                P.c("跑马灯解析出错");
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 0;
                msg.obj = "资源格式出错!";
                handler.sendMessage(msg);
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
        }
    };

    private SharedUtils sharedUtils;

    public CommonSnyDataPop(Context context, String msg) {
        this.context = context;
        this.msg = msg;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }


    public synchronized Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.loading, null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText("同步数据");
        load_img = (ImageView) layout.findViewById(R.id.load_img);
        load_close = (ImageView) layout.findViewById(R.id.load_close);
        load();
        load_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handler.sendEmptyMessage(-3);
            }
        });
        // 动画
        Animation operatingAnim = AnimationUtils.loadAnimation(context,
                R.anim.anim_load);
        // 匀速旋转
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        load_img.startAnimation(operatingAnim);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private void cancle() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
            if (requestCall != null) {
                requestCall.cancel();
            }
            if (getCall != null) {
                getCall.cancel();
            }
        }
    }

    private RequestCall requestCall;

    private void load() {
        String ip = sharedUtils.getStringValue("IP");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0},{\"TabName\":\"NTORestSite\",\"Timestamp\":0},{\"TabName\":\"NTOSuitMenuDetail\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuLimited\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuCook\",\"Timestamp\":0},{\"TabName\":\"NTOMenuGroup\",\"Timestamp\":0},{\"TabName\":\"NTOMenuGroupDetail\",\"Timestamp\":0},{\"TabName\":\"NTOSYSCODE\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuCookCls\",\"Timestamp\":0},{\"TabName\":\"NTOSystemParm\",\"Timestamp\":0}]");
            requestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_DOWNLOAD_DATA))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            requestCall.execute(snyCallback);
            P.c(TimeUtil.getTime(System.currentTimeMillis()));
        } catch (JSONException e) {

            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    private void sendMsg(String tip) {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = tip;
        handler.sendMessage(msg);
    }

    private StringCallback snyCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

            //  P.c("获得数据"+TimeUtil.getTime(System.currentTimeMillis()));
            //P.c(response);
            /*try {
                FileUtils.write(Common.SD+"12.txt", false, FileUtils.formatJson(response));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}*/
            new Thread() {
                public void run() {
                    //开始解析数据和装载数据库
                    try {
                        DB.getInstance().clearAll();
                        sharedUtils.clear("user");
                        sharedUtils.clear("userName");
                        sharedUtils.setIntValue("versionOld", BaseApplication.application.getVersionCode());

                        P.c(FileUtils.formatJson(response));
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            //成功
                            JSONObject data = jsonObject.getJSONObject("Data");
                            if (data.has("PadParamSet")) {
                                P.c("配置信息:" + data.get("PadParamSet").toString());
                                //解析配置文件
                                try {
                                    JSONObject conJson = new JSONObject(data.getString("PadParamSet"));
                                    sharedUtils.setBooleanValue("is_waite", getBool(conJson, "SendOrderModel"));
                                    sharedUtils.setBooleanValue("is_dish", getBool(conJson, "AddMenuConfrim"));
                                    sharedUtils.setBooleanValue("is_table", getBool(conJson, "OpenTab"));
                                    sharedUtils.setBooleanValue("screen_keep", getBool(conJson, "SystemCloseModel"));
//                                    sharedUtils.setBooleanValue("is_ps", getBool(conJson, "SendOrderModel"));
//                                    sharedUtils.setBooleanValue("is_print", getBool(conJson, "SendOrderModel"));
                                    sharedUtils.setBooleanValue("is_price", getBool(conJson, "HideAmount"));
                                    sharedUtils.setBooleanValue("is_room", getBool(conJson, "HideMenu"));
                                    sharedUtils.setBooleanValue("is_giv", getBool(conJson, "RowFourMenu"));
                                    sharedUtils.setBooleanValue("is_limit", getBool(conJson, "CancelShowQuantity"));
                                    sharedUtils.setBooleanValue("is_call", getBool(conJson, "CloseServer"));
                                    sharedUtils.setBooleanValue("is_cy", getBool(conJson, "Interface"));
                                    sharedUtils.setBooleanValue("is_print", getBool(conJson, "Notice"));
                                    sharedUtils.setBooleanValue("is_prepay", getBool(conJson, "Payment"));
                                    sharedUtils.setBooleanValue("is_ps", !getBool(conJson, "Password"));
                                    sharedUtils.setBooleanValue("is_reas", getBool(conJson, "Order"));
                                    sharedUtils.setBooleanValue("is_hall", getBool(conJson, "Hall"));
                                    sharedUtils.setBooleanValue("is_txt", getBool(conJson, "Schema"));
                                    sharedUtils.setBooleanValue("is_advert", getBool(conJson, "Meal"));
                                    sharedUtils.setBooleanValue("is_card", getBool(conJson, "Disable"));
                                    sharedUtils.setBooleanValue("is_time", getBool(conJson, "Supply"));
                                    sharedUtils.setBooleanValue("is_tip", getBool(conJson, "Prompt"));
                                    sharedUtils.setBooleanValue("is_edit_price", getBool(conJson, "Temporary"));
                                    sharedUtils.setBooleanValue("is_cd", getBool(conJson, "CD"));
                                    sharedUtils.setBooleanValue("is_gua", getBool(conJson, "GD"));
//                                    sharedUtils.setBooleanValue("is_lan", getBool(conJson, "DishTip"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            JSONArray cateArray = data.getJSONArray("NTORestMenuClass");
                            sendMsg("解析分类数据");
                            Map<String, String> mes = DB.getInstance().addCate(cateArray);
                            //--
                            JSONArray dishArray = data.getJSONArray("NTORestMenu");
                            sendMsg("解析菜品数据");
                            DB.getInstance().addDish(dishArray, mes);
                            //---
                            JSONArray userArray = data.getJSONArray("NTOUsers");
                            sendMsg("解析用户数据");
                            DB.getInstance().addUser(userArray);
                            //---
                            JSONArray areaArray = data.getJSONArray("NTORestArea");
                            sendMsg("解析区域数据");
                            DB.getInstance().addArea(areaArray);
                            //---
                            JSONArray tableArray = data.getJSONArray("NTORestTable");
                            sendMsg("解析餐台数据");
                            DB.getInstance().addBoard(tableArray);
                            JSONArray siteArray = data.getJSONArray("NTORestSite");
                            sendMsg("解析营业点数据");
                            DB.getInstance().addSite(siteArray);
                            //---
                            JSONArray cookClsArray = data.getJSONArray("NTORestCookCls");
                            sendMsg("解析做法类别数据");
                            DB.getInstance().addCookCls(cookClsArray);
                            //---
                            JSONArray cookArray = data.getJSONArray("NTORestCook");
                            sendMsg("解析做法数据");
                            DB.getInstance().addCook(cookArray);
                            //---
                            JSONArray mcArray = data.getJSONArray("NTORestMenuCook");
                            sendMsg("解析菜品做法关系数据");
                            DB.getInstance().addMenuCook(mcArray);
                            //---
                            JSONArray imagesArray = data.getJSONArray("NTORestMenuImage");
                            sendMsg("解析图片组数据");
                            DB.getInstance().addImages(imagesArray);
                            //---
                            JSONArray detailArray = data.getJSONArray("NTOSuitMenuDetail");
                            sendMsg("解析套餐数据");
                            DB.getInstance().addDetail(detailArray);
                            JSONArray limitArray = data.getJSONArray("NTORestMenuLimited");
                            sendMsg("解析限制关系");
                            DB.getInstance().addLimit(limitArray);
                            //---
                            JSONArray remarkArray = data.getJSONArray("NTORestRemark");
                            sendMsg("解析口味数据");
                            DB.getInstance().addRemark(remarkArray);

                            JSONArray groupArray = data.getJSONArray("NTOMenuGroup");
                            sendMsg("解析组合方案");
                            DB.getInstance().addMgroup(groupArray);

                            JSONArray gDetailArray = data.getJSONArray("NTOMenuGroupDetail");
                            sendMsg("解析组合明细");
                            DB.getInstance().addMgDetail(gDetailArray);
                            JSONArray rcArray = data.getJSONArray("NTOSYSCODE");
                            //192.168.254.52
                            sendMsg("解析退菜理由");
                            DB.getInstance().addRc(rcArray);
                            DB.getInstance().addTableType(rcArray);
                            //解析分类口味
                            JSONArray clsArray = data.getJSONArray("NTORestMenuCookCls");
                            sendMsg("解析口味限制");
                            DB.getInstance().addCookLit(clsArray);
                            //解析配置参数
                            if (data.has("NTOSystemParm")) {
                                JSONArray paramArray = data.getJSONArray("NTOSystemParm");
                                sendMsg("解析配置参数");
                                sharedUtils.setBooleanValue("is_ps", false);
                                for (int i = 0; i < paramArray.length(); i++) {
                                    JSONObject objectParam = paramArray.getJSONObject(i);
                                    if (objectParam.getString("Code").equals("EnablePadLogin")) {
                                        P.c("解析服务员参数:" + objectParam.getString("ParmValue"));
                                        sharedUtils.setStringValue("ParmValue", objectParam.getString("ParmValue"));
                                    }
                                }
                            }
                            //--
                            File dir = new File(Common.ZIP);
                            File[] files = dir.listFiles();
                            if (files != null && files.length == 1) {
                                File par = files[0].getAbsoluteFile();
                                if (par.isDirectory()) {
                                    File dd[] = par.listFiles();
                                    if (dd != null && dd.length != 0) {
                                        sendMsg("整理图片资料");
                                    }
                                    for (int i = 0; i < dd.length; i++) {
                                        String string = dd[i].getAbsolutePath();
                                        String ss = string.substring(0, string.lastIndexOf("/"));
                                        DB.getInstance().changeImage(getImageWidthHeight(dd[i].getAbsolutePath()), string.substring(ss.lastIndexOf("/"), string.length()));
                                        string = null;
                                        ss = null;
                                    }
                                }
                            }
                            sendMsg("解析广播数据");
                            handler.sendEmptyMessage(1);
                            //	copy();

                        } else {
                            //失败
                            handler.sendEmptyMessage(-4);
                        }
                    } catch (final JSONException e) {
                        new Thread() {
                            public void run() {
                                try {
                                    FileUtils.writeLog(
                                            e.toString(), "下菜结果");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    FileUtils.writeLog("写入异常", "下菜结果");
                                }
                            }
                        }.start();
                        P.c("解析出错");
                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "数据格式出错!";
                        handler.sendMessage(msg);
                    }
                }
            }.start();
        }

        @Override
        public void onError(Call call, Exception e, int id) {

            P.c(e.getMessage() + "------" + e.getLocalizedMessage());
            handler.sendEmptyMessage(-1);
        }
    };

    private void copy() {
        CopyFile cf = new CopyFile();

        cf.copyFile("data/data/pad.stand.com.haidiyun.www/" + Common.DB_NAME, Common.BASE_DIR + "/droid4xShare/2.db");
    }

    private boolean getBool(JSONObject object, String var) {
        if (object.has(var)) {
            try {
                return object.getBoolean(var);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private int[] getImageWidthHeight(String path) {
        int[] wh = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        wh[0] = options.outWidth;
        wh[1] = options.outHeight;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        return wh;
    }


}
