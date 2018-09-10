package pad.com.haidiyun.www.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.FileCallBack;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.TimeUtil;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;

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
                    NewDataToast.makeText("同步完成");
                    //清除一些数据
                    sharedUtils.clear("table_name");
                    sharedUtils.clear("table_code");
                /*Intent intent = new Intent();
                //FlipNextActivity中定义的广播action
				intent.setAction("app.data.updata");
				context.sendBroadcast(intent);*/
                    Common.LAST_PAGE = 0;
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

    private SharedUtils sharedUtils;

    public CommonSnyDataPop(Context context, String msg) {
        this.context = context;
        this.msg = msg;
        sharedUtils = new SharedUtils(Common.CONFIG);
//        sharedUtils.setStringValue("IP", "192.168.1.118");
    }

    private Handler tableHandler = new Handler();
    private Runnable tableRunnable = new Runnable() {
        public void run() {
            Intent intent = new Intent();
            intent.setAction(Common.TOUCH_DOWN);
            context.sendBroadcast(intent);
            tableHandler.postDelayed(this, 5000);

        }
    };

    public synchronized Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.flip_loading, null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText("同步数据");
        load_img = (ImageView) layout.findViewById(R.id.load_img);
        load_close = (ImageView) layout.findViewById(R.id.load_close);
        load();
        tableHandler.post(tableRunnable);
        load_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
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
            if (requestImageCall != null) {
                requestImageCall.cancel();
            }
        }
    }

    private RequestCall requestCall;

    private void load() {
        String ip = sharedUtils.getStringValue("IP");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0},{\"TabName\":\"NTORestSite\",\"Timestamp\":0},{\"TabName\":\"NTOSuitMenuDetail\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuCook\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuCookCls\",\"Timestamp\":0},{\"TabName\":\"PadMenuCoordinate\",\"Timestamp\":0},{\"TabName\":\"NTOMenuGroup\",\"Timestamp\":0},{\"TabName\":\"NTOMenuGroupDetail\",\"Timestamp\":0},{\"TabName\":\"NTOSYSCODE\",\"Timestamp\":0},{\"TabName\":\"NTODiscountRule\",\"Timestamp\":0}]");
            String url = U.VISTER(ip, U.URL_DOWNLOAD_DATA);
            requestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_DOWNLOAD_DATA))
//			        .addParams("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            requestCall.execute(snyCallback);
            P.c(TimeUtil.getTime(System.currentTimeMillis()));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    private String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileS == 0) {
            return "0.00";
        }
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    private void sendMsg(int what, String tip) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = tip;
        handler.sendMessage(msg);
    }

    private boolean deleteDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private boolean pocess() {
        Glide.get(context).clearMemory();
        return true;
    }

    private StringCallback snyCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
            // TODO Auto-generated method stub


            if (pocess()) {
                //开始执行
                new Thread() {
                    public void run() {
                        //开始解析数据和装载数据库
                        try {
                            //清空数据库
                            DB.getInstance().clearAll();
                            //清除图片资源
                            File image = new File(Common.SOURCE);
                            if (image.exists()) {
                                deleteDir(image);
                            }
                            Glide.get(context).clearDiskCache();

                            try {
                                pad.stand.com.haidiyun.www.common.FileUtils.write(pad.stand.com.haidiyun.www.common.Common.SD + "data.txt", false, pad.stand.com.haidiyun.www.common.FileUtils.formatJson(response));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //
                            JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                            //P.c("同步数据"+FileUtils.read(pad.stand.com.haidiyun.www.common.Common.SD + "data.txt"));

                            if (jsonObject.getBoolean("Success")) {
                                //成功
                                JSONObject data = jsonObject.getJSONObject("Data");

                                if (data.has("PadParamSet")) {
                                    if(data.getString("PadParamSet").length()!=0){
                                        JSONObject conJson = new JSONObject(data.getString("PadParamSet"));
                                        P.c("配置信息:" + data.get("PadParamSet").toString());
                                        sharedUtils.setBooleanValue("is_card", getBool(conJson, "Disable"));
                                    }

                                }

                                JSONArray cateArray = data.getJSONArray("NTORestMenuClass");
                                sendMsg(2, "解析分类数据");
                                DB.getInstance().addCate(cateArray);

                                //--
                                JSONArray dishArray = data.getJSONArray("NTORestMenu");
                                sendMsg(2, "解析菜品数据");
                                DB.getInstance().addDish(dishArray);
                                //---
                                JSONArray userArray = data.getJSONArray("NTOUsers");
                                sendMsg(2, "解析用户数据");

                                DB.getInstance().addUser(userArray);
                                //---
                                JSONArray areaArray = data.getJSONArray("NTORestArea");
                                sendMsg(2, "解析区域数据");
                                DB.getInstance().addArea(areaArray);
                                //---
                                JSONArray tableArray = data.getJSONArray("NTORestTable");
                                sendMsg(2, "解析餐台数据");
                                DB.getInstance().addBoard(tableArray);
                                //---
                                JSONArray siteArray = data.getJSONArray("NTORestSite");
                                sendMsg(2, "解析营业点数据");
                                DB.getInstance().addSite(siteArray);
                                //---
                                JSONArray cookClsArray = data.getJSONArray("NTORestCookCls");
                                sendMsg(2, "解析做法类别数据");

                                DB.getInstance().addCookCls(cookClsArray);
                                //---
                                JSONArray cookArray = data.getJSONArray("NTORestCook");
                                sendMsg(2, "解析做法数据");
                                DB.getInstance().addCook(cookArray);
                                //---
                                JSONArray mcArray = data.getJSONArray("NTORestMenuCook");
                                sendMsg(2, "解析菜品做法关系数据");
                                DB.getInstance().addMenuCook(mcArray);
                                //---
                                JSONArray mcclsArray = data.getJSONArray("NTORestMenuCookCls");
                                sendMsg(2, "解析菜品必选关系数据");
                                DB.getInstance().addMenuCookCls(mcclsArray);
                            /*JSONArray imagesArray = data.getJSONArray("NTORestMenuImage");
                            sendMsg(2,"解析图片组数据");
							DB.getInstance().addImages(imagesArray);*/
                                //---
                                JSONArray detailArray = data.getJSONArray("NTOSuitMenuDetail");
                                sendMsg(2, "解析套餐数据");
                                DB.getInstance().addDetail(detailArray);

                                //---
                                JSONArray remarkArray = data.getJSONArray("NTORestRemark");
                                sendMsg(2, "解析口味数据");
                                DB.getInstance().addRemark(remarkArray);
                                //--
                                JSONArray fgArray = data.getJSONArray("PadMenuCoordinate");
                                sendMsg(2, "解析菜品关系数据");
                                DB.getInstance().addFg(fgArray);

                                JSONArray groupArray = data.getJSONArray("NTOMenuGroup");
                                sendMsg(2, "解析组合方案");
                                DB.getInstance().addMgroup(groupArray);

                                JSONArray gDetailArray = data.getJSONArray("NTOMenuGroupDetail");
                                sendMsg(2, "解析组合明细");
                                DB.getInstance().addMgDetail(gDetailArray);
                                JSONArray rcArray = data.getJSONArray("NTOSYSCODE");
                                sendMsg(2, "解析退菜理由");
                                DB.getInstance().addRc(rcArray);


                                JSONArray dis =  data.getJSONArray("NTODiscountRule");

                                DB.getInstance().addDis(dis);

                                //下载图片数据
                                sendMsg(2, "准备下载图片资源");
                                loadImage();
                                //handler.sendEmptyMessage(1);
                            } else {
                                //失败
                                handler.sendEmptyMessage(-4);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            P.c(e.getLocalizedMessage());
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = "数据格式出错!";
                            handler.sendMessage(msg);
                        }
//					DownloadImgZip

                    }

                    ;
                }.start();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            // TODO Auto-generated method stub
            P.c(e.getMessage() + "------" + e.getLocalizedMessage());
            handler.sendEmptyMessage(-1);
        }
    };
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

    private RequestCall requestImageCall;
    private String FILE = "Imagesdata.zip";

    private void loadImage() {
        File file = new File(Common.SD + FILE);
        if (file.exists() && file.isFile()) {
            P.c("删除图片源文件");
            file.delete();
        }
        File file1 = new File(Common.SD + "IMAGES");
        if (file1.exists() && file1.isFile()) {
            P.c("删除图片源文件");
            file1.delete();
        }
        String ip = sharedUtils.getStringValue("IP");
        requestImageCall = OkHttpUtils.post()
                .url(U.VISTER(ip, U.URL_DOWNLOAD_IMAGES))
//        .addParams("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
//        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build();
        String url = U.VISTER(ip, U.URL_DOWNLOAD_IMAGES);
        requestImageCall.execute(callBack);
    }

    private FileCallBack callBack = new FileCallBack(Common.SD, FILE) {

        @Override
        public void onResponse(final File response, int id) {
            // TODO Auto-generated method stub
            if (response == null) {
                sendMsg(0, "无最新图片资源");
            } else {
                sendMsg(2, "解压图片数据");
                P.c(Thread.currentThread().getName());
                new Thread() {
                    public void run() {
                        try {
                            FileUtils.unZipFile(response.getAbsolutePath(), Common.SOURCE);
                            handler.sendEmptyMessage(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(-5);
                        }

                    }

                    ;
                }.start();
            }

            P.c("///////////////////////" + response);
        }

        @Override
        public void inProgress(long progress, long total, int id) {
            // TODO Auto-generated method stub
            super.inProgress(progress, total, id);
			/*Message msg = new Message();
			msg.what = 2;
			msg.obj = FormetFileSize(progress)+"/"+FormetFileSize(total);
			handler.sendMessage(msg);*/
            sendMsg(2, FormetFileSize(progress) + "/" + FormetFileSize(total));
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            // TODO Auto-generated method stub
            P.c(e.getLocalizedMessage() + "下载问题");
            handler.sendEmptyMessage(-4);
        }

    };

}
