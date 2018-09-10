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

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
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
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;

/**
 * 同步资源
 *
 * @author Administrator
 */
public class CommonSnyImagePop {
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
                case 3:
                    load_tv.setText("整理图片资料");
                    break;
                case 1:
                    NewDataToast.makeText("同步完成");
                    //清除一些数据
                /*Intent intent = new Intent();
				//FlipNextActivity中定义的广播action
				intent.setAction("app.data.updata");
				context.sendBroadcast(intent);*/
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

    public CommonSnyImagePop(Context context, String msg) {
        this.context = context;
        this.msg = msg;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }
    private void simulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }
    private Handler hfd = new Handler();
    public synchronized Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.loading, null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText("同步数据");
        simulateClick(load_tv,0,0);
        hfd.postDelayed(new Runnable() {
            @Override
            public void run() {
                load_tv.performClick();
                hfd.postDelayed(this,3000);
            }
        },3000);


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
        }
    }

    private RequestCall requestCall;

    private void load() {
        File file = new File(Common.SD + FILE);
        if (file.exists() && file.isFile()) {
            P.c("删除图片源文件");
            file.delete();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", "0");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        String ip = sharedUtils.getStringValue("IP");
        requestCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_DOWNLOAD_IMAGES))
//        .addParams("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString())
                .build();
        requestCall.execute(callBack);
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

    private String FILE = "Imagesdata.zip";
    private FileCallBack callBack = new FileCallBack(Common.SD, FILE) {

        @Override
        public void onResponse(final File response, int id) {
            Glide.get(context).clearMemory();


            if (response == null) {
                sendMsg(0, "无最新图片资源");
            } else {
                sendMsg(2, "解压图片数据");
                P.c(Thread.currentThread().getName());
                new Thread() {
                    public void run() {
                        Glide.get(context).clearDiskCache();
                        ImageLoader.getInstance().clearDiscCache();
                        ImageLoader.getInstance().clearMemoryCache();
                        ImageLoader.getInstance().clearDiskCache();

                        try {
                            FileUtils.unZipFile(response.getAbsolutePath(), Common.ZIP);
                            //解压数据
                            File dir = new File(Common.ZIP);
                            File[] files = dir.listFiles();
                            if (files.length == 1) {
                                File par = files[0].getAbsoluteFile();
                                if (par.isDirectory()) {
                                    File dd[] = par.listFiles();
                                    handler.sendEmptyMessage(3);
                                    for (int i = 0; i < dd.length; i++) {
                                        String string = dd[i].getAbsolutePath();
                                        String ss = string.substring(0, string.lastIndexOf("/"));
                                        DB.getInstance().changeImage(getImageWidthHeight(dd[i].getAbsolutePath()), string.substring(ss.lastIndexOf("/"), string.length()));
                                        string = null;
                                        ss = null;
                                    }
                                }
                            }

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

            super.inProgress(progress, total, id);
			/*Message msg = new Message();
			msg.what = 2;
			msg.obj = FormetFileSize(progress)+"/"+FormetFileSize(total);
			handler.sendMessage(msg);*/
            sendMsg(2, FormetFileSize(progress) + "/" + FormetFileSize(total));
        }

        @Override
        public void onError(Call call, Exception e, int id) {

            P.c(e.getLocalizedMessage() + "下载问题");
            handler.sendEmptyMessage(-4);
        }

    };

    private StringCallback snyCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

            //P.c(response);
            try {
                FileUtils.write(Common.SD + "2.txt", false, FileUtils.formatJson(response));
            } catch (JSONException e1) {

                e1.printStackTrace();
            }
            new Thread() {
                public void run() {
                    //开始解析数据和装载数据库
                    try {
                        JSONArray jsonArray = new JSONArray(FileUtils.formatJson(response));
                        int len = jsonArray.length();
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < len; i++) {
                            builder.append(jsonArray.getString(i));
                        }
                        FileUtils.getFile(builder.toString().getBytes(), Common.SD, "3");
                        handler.sendEmptyMessage(1);

                    } catch (JSONException e) {

                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = "数据格式出错!";
                        handler.sendMessage(msg);
                    }
//					DownloadImgZip

                }

                ;
            }.start();
			
			
		/*	try {
				final JSONObject jsonObject = new JSONObject(response);
				int status = jsonObject.getInt("Code");
				switch (status) {
				case 1:
					//需要更新
					//主线程进行解压操作
					new Thread(){
						public void run() {
							try {
								loadZip(jsonObject.getString("Ver"));
							} catch (JSONException e) {

								e.printStackTrace();
							}
						};
					}.start();
					
					//
					break;
				case 0:
					Message msg = new Message();
					msg.what = 0;
					msg.obj = jsonObject.getString("Detail");
					handler.sendMessage(msg);
					//失败或者不需要更新
					break;
				default:
					break;
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}*/
        }

        @Override
        public void onError(Call call, Exception e, int id) {

//			 P.c(e.getMessage()+""+e.getLocalizedMessage());
            handler.sendEmptyMessage(-1);
        }
    };

}
