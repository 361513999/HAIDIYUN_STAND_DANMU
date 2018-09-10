package pad.com.haidiyun.www.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.android.settings.IBackService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.utils.FTPDownloadFile;
import pad.com.haidiyun.www.widget.CommonMovePop;
import pad.com.haidiyun.www.widget.CommonSnyDataPop;
import pad.com.haidiyun.www.widget.T_Image;
import pad.com.haidiyun.www.widget.VideoView;
import pad.stand.com.haidiyun.www.ui.BaseActivity;

public class LanuchActivity extends BaseActivity {
    private VideoView video;
    private ImageView lanucher_bg, no_video;
    private ImageSwitcher slider;
    private SharedUtils sharedUtils;
    private TextView enter;
    private File[] files, videoFiles;
    private View fouc;
    private Typeface tf;
    private CommonMovePop move;
    private DataReceiver dataReceiver;
    SharedUtils configUtils;

    /**
     * 发送一次点击广播
     */
    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        sendBroadcast(intent);
    }

	/*@Override
    public void keyEvent(KeyEvent event) {
		Intent intent = new Intent(LanuchActivity.this,MainFragmentActivity.class);
		switch (event.getKeyCode()){
			case KeyEvent.KEYCODE_CAMERA:
				intent.putExtra("key",KeyEvent.KEYCODE_CAMERA);
				break;
			case KeyEvent.KEYCODE_BACK:
				intent.putExtra("key",KeyEvent.KEYCODE_BACK);
				break;
			case KeyEvent.KEYCODE_VOLUME_UP :
				intent.putExtra("key",KeyEvent.KEYCODE_VOLUME_UP);
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				intent.putExtra("key",KeyEvent.KEYCODE_VOLUME_DOWN);
				break;
		}
		startActivity(intent);
	}*/

    class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("KeyEvent.KEYCODE_BACK_Laucher")) {
                //广告切换到首页
                Intent it = new Intent(LanuchActivity.this, MainFragmentActivity.class);
                it.putExtra("key", KeyEvent.KEYCODE_BACK);
                startActivity(it);
                P.c("==========111");
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
        }
    }

    @Override
    public void keyEvent(KeyEvent event) {
        Intent intent = new Intent(LanuchActivity.this, pad.stand.com.haidiyun.www.ui.MainFragmentActivity.class);
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_CAMERA:
                intent.putExtra("key", KeyEvent.KEYCODE_CAMERA);
                break;
            case KeyEvent.KEYCODE_BACK:
                intent.putExtra("key", KeyEvent.KEYCODE_BACK);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                intent.putExtra("key", KeyEvent.KEYCODE_VOLUME_UP);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                intent.putExtra("key", KeyEvent.KEYCODE_VOLUME_DOWN);

//				CommonUserPop userPop = new CommonUserPop(LanuchActivity.this,new Handler());
//				userPop.showSheet();
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        P.c("--------------onCreate");
        // TODO Auto-generated method stub
        getWindow().getDecorView().setSystemUiVisibility(8);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flip_lanucher_layout);
        Intent intentService = new Intent();
        intentService.setAction("flip.pad.com.invisible");
        sendBroadcast(intentService);
//        getAidlArg();
//        getFwContent("");
        down();
        tf = Typeface.createFromAsset(getAssets(), "font/mb.ttf");
        video = (VideoView) findViewById(R.id.video);
        video.setOnCompletionListener(listener);
        fouc = findViewById(R.id.fouc);
        enter = (TextView) findViewById(R.id.enter);
        enter.setTypeface(tf);
        lanucher_bg = (ImageView) findViewById(R.id.lanucher_bg);
        no_video = (ImageView) findViewById(R.id.no_video);
        slider = (ImageSwitcher) findViewById(R.id.slider);
        slider.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.t_fade_in));
        slider.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.t_fade_out));
        slider.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                T_Image i = new T_Image(LanuchActivity.this);
                i.setScaleType(ImageView.ScaleType.CENTER_CROP);
                i.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                return i;
            }
        });
        sharedUtils = new SharedUtils(Common.VIDEO_TAG);
        Glide.with(BaseApplication.application).load(R.drawable.lanuch_bg).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(lanucher_bg);
        enter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                stopImage();
                if (video != null) {
                    video.stopPlayback();
                }
                dataHandler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(LanuchActivity.this, MainFragmentActivity.class);
                intent.putExtra("key", KeyEvent.KEYCODE_CAMERA);
                startActivity(intent);

            }
        });
        /*new Thread(){
            public void run() {
				P.c("---------------首次检测更新-------------------");
				checkUpdata();
			};
		}.start();*/
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("KeyEvent.KEYCODE_BACK_Laucher");
        registerReceiver(dataReceiver, filter);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBackService backService = IBackService.Stub.asInterface(service);
            try {
                String regCode = backService.getdeviceInfoString("ServiceInfo", 1);
                String sn = backService.getdeviceInfoString("ServiceInfo", 2);
                String callingServer = backService.getdeviceInfoString("ServiceInfo", 19);
                String localServer = backService.getdeviceInfoString("ServiceInfo", 6);
                if (!"".equals(regCode)) {
                    configUtils.setStringValue("regCode", regCode);
                    configUtils.setStringValue("sn", sn);
                    configUtils.setStringValue("callingServer", callingServer);
                    //这里存储aidl获取的ip替代原来手动配置
                    localServer = "192.168.1.118";
                    configUtils.setStringValue("IP", localServer);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("", "断开连接");
        }
    };

    private void getAidlArg() {
        configUtils = new SharedUtils(Common.CONFIG);
        Intent mServiceIntent = new Intent("sys.update.time");
        bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
    }

    private int detly = 6000;
    private int play;
    int laster = 0;
    private OnCompletionListener listener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer arg0) {
            // TODO Auto-generated method stub
            video.stopPlayback();
            P.c("图片数量" + imgLen);
            int index = sharedUtils.getIntValue("video_index");
            if (videoFiles.length == 1) {
                index = 0;
                //
                if (imgLen != 0) {
                    playImage();

                } else {
                    playVideo();

                }
            } else {
                index = index + 1;
                P.c("索引" + index);
                sharedUtils.setIntValue("video_index", index);
                if (index == videoFiles.length) {
                    index = 0;
                    sharedUtils.setIntValue("video_index", 0);
                    //在此将视频停止


                    if (imgLen != 0) {
                        playImage();
                    } else {
                        playVideo();
                    }

                } else {
                    playVideo();
                }
            }
            /*no_video.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
			slider.setVisibility(View.GONE);
			P.c("播放"+videoFiles[index].getAbsolutePath());
			video.setVideoPath(videoFiles[index].getAbsolutePath());
			video.start();*/


        }
    };

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

    private void checkUpdata() {
        FTPDownloadFile downloadFile = new FTPDownloadFile(null, null);
        if (downloadFile.isUpdata()) {
            //如果是true就有更新。
            File file = new File(Common.SD + Common.json);
            if (file.exists()) {
                file.delete();
            }
            deleteDir(new File(Common.SOURCE));
            DB.getInstance().clearDish();
            Glide.get(BaseApplication.application).clearDiskCache();
            //清除，再更新
            downHandler.sendEmptyMessage(1);
        }
    }

    private Handler downHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    CommonSnyDataPop sny = new CommonSnyDataPop(LanuchActivity.this, "自动检测有数据更新");
                    sny.showSheet();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    protected void onPause() {
        super.onPause();
        P.c("--------------onPause");
        if (video != null) {
            video.stopPlayback();
        }
        dataHandler.removeCallbacksAndMessages(null);
    }

    ;

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        P.c("--------------onRestart");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        P.c("--------------onStart");
    }

    int imgLen, videoLen;
    private Timer timer;
    private Handler dataHandler = new Handler();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            P.c("-----------自动检测更新1------------");
            new Thread() {
                public void run() {
                    P.c("-----------自动检测更新2------------");
                    checkUpdata();
                }

                ;
            }.start();
            P.c("-----------自动检测更新3------------");
            dataHandler.postDelayed(this, 60000);
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        P.c("--------------onResume");

		/*Intent intent = new Intent();
        intent.setAction(Common.SERVICE_ACTION);
		intent.putExtra("open_buy", "1");
		startService(intent);*/

//		dataHandler.post(runnable);
        defaultView();
        Common.DOING = -1;
        fouc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

            }
        });
        File file = new File(Common.SOURCE_ADVER);
        File videoFile = new File(Common.SOURCE_VIDEO);
        if (file.exists() || videoFile.exists()) {


            if (file.isDirectory() || videoFile.isDirectory()) {


                FileFilter filefilter = new FileFilter() {

                    public boolean accept(File file) {
                        // if the file extension is .txt return true, else false
                        if (file.getName().endsWith(".png")
                                || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                            return true;
                        }
                        return false;
                    }
                };
                FileFilter videofilefilter = new FileFilter() {

                    public boolean accept(File file) {
                        // if the file extension is .txt return true, else false
                        if (file.getName().endsWith(".mp4")
                                || file.getName().endsWith(".rmvb")) {
                            return true;
                        }
                        return false;
                    }
                };
                files = file.listFiles(filefilter);
                videoFiles = videoFile.listFiles(videofilefilter);
                if ((files != null && files.length != 0) || (videoFiles != null && videoFiles.length != 0)) {
                    if (files != null && (imgLen = files.length) != 0) {
                        //有图片先显示图片
                        //有数据存在
                        playImage();
                    } else if (videoFiles != null && (videoLen = videoFiles.length) != 0) {
                        //有视频就显示视频
                        playVideo();

                    } else {
                        defaultView();
                        return;
                    }
                } else {
                    defaultView();
                }
            }
        } else {
            defaultView();
        }

	/*	File file = new File( Common.SOURCE_VIDEO);
        if (file.exists()) {
			if (file.isDirectory()) {
				FileFilter filefilter = new FileFilter() {

					public boolean accept(File file) {
						// if the file extension is .txt return true, else false
						if (file.getName().endsWith(".mp4")
								|| file.getName().endsWith(".rmvb")) {
							return true;
						}
						return false;
					}
				};
				files = file.listFiles(filefilter);
				int len = files.length;
				if (len != 0) {
					no_video.setVisibility(View.GONE);
					video.setVisibility(View.VISIBLE);
					if (sharedUtils.getIntValue("video_index") < len) {
						// 当前播放的索引不能大于数组大小

						// if(video.getUri()==null){
						this.video.setVideoPath(files[sharedUtils.getIntValue(
								"video_index")].getAbsolutePath());

					} else {
						this.video.setVideoPath(files[0].getAbsolutePath());
					}
					this.video.start();
					int position = sharedUtils.getIntValue("video_position");
					if (position != 0) {
						this.video.seekTo(position);
					}
					sharedUtils.setIntValue("video_index",
							sharedUtils.getIntValue("video_position"));
					this.video.setOnCompletionListener(listener);
					//证明有视频在运行
					
				}else{
					//没有视频在运行
					no_video.setVisibility(View.VISIBLE);
					video.setVisibility(View.GONE);
					 Glide.with(BaseApplication.application).load(R.drawable.lanuch_bg).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(no_video);
						
				}

			}
		}else{
			no_video.setVisibility(View.VISIBLE);
			video.setVisibility(View.GONE);
			 Glide.with(BaseApplication.application).load(R.drawable.lanuch_bg).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(no_video);
				
		}*/
    }

    private void playImage() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        play = 0;
        startSlider();
        no_video.setVisibility(View.GONE);
        video.setVisibility(View.GONE);
        slider.setVisibility(View.VISIBLE);
    }

    public void stopImage() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void startSlider() {
        //Glide.with(BaseApplication.application).load(files[play]).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).animate( android.R.anim.slide_in_left).into(slider);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                // avoid exception:
                // "Only the original thread that created a view hierarchy can touch its views"
                runOnUiThread(new Runnable() {
                    public void run() {
//                        slider.setImageDrawable(BitmapDrawable.createFromPath(files[play].getAbsolutePath()));
                        if (play == files.length) {
                            if (videoFiles != null && videoFiles.length != 0) {
                                P.c("播放视频");
                                P.c("图片数量" + videoLen);

                                playVideo();
                            } else {
                                P.c("播放图片");
                                P.c("图片数量" + imgLen);
                                playImage();
                            }
                            return;
                        }
                        slider.showNext();
                        Glide.with(BaseApplication.application).load(files[play++]).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into((T_Image) slider.getCurrentView());

                    }
                });
            }

        }, 0, detly);
    }

    private void playVideo() {
        //有视频就显示视频
        if (sharedUtils.getIntValue("video_index") >= videoFiles.length) {
            // 当前播放的索引不能大于数组大小
            sharedUtils.setIntValue("video_index", 0);
            // if(video.getUri()==null){
        }
        this.video.setVideoPath(videoFiles[sharedUtils.getIntValue(
                "video_index")].getAbsolutePath());

        this.video.start();
        /*int position = sharedUtils.getIntValue("video_position");
        if (position != 0) {
			this.video.seekTo(position);
		}*/

        //证明有视频在运行
        P.c("播放" + videoFiles[sharedUtils.getIntValue(
                "video_index")].getAbsolutePath());
        no_video.setVisibility(View.GONE);
        video.setVisibility(View.VISIBLE);
        slider.setVisibility(View.GONE);


    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        stopImage();
        if (video != null) {
            video.stopPlayback();
        }
    }

    private void defaultView() {

        video.setVisibility(View.GONE);
        no_video.setVisibility(View.VISIBLE);
        slider.setVisibility(View.GONE);
        Glide.with(BaseApplication.application).load(R.drawable.lanucher_t).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(no_video);
    }

    private RequestCall getCall;
    String regCode;

    private void getFwContent(String ip) {
        JSONObject jsonObject = new JSONObject();
        try {
            regCode = configUtils.getStringValue("regCode");
            jsonObject.put("RegCode", regCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCall = OkHttpUtils.post().url("http://watch.haidiyun.top/ajax/Ajax_LoginAction?method=GetCallType").addParams("RegCode", "2AF10729BE69").build();
        getCall.execute(getCallback);
    }

    private StringCallback getCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            try {
                String info = response.toString();
                if (configUtils.getStringValue("info").equals(info)) {
                    return;
                } else {
                    configUtils.setStringValue("info", info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
        }
    };

}
