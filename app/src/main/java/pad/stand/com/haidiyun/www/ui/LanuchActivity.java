package pad.stand.com.haidiyun.www.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.android.settings.MessageBean;
import com.android.settings.OrderManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.widget.T_Image;
import pad.stand.com.haidiyun.www.widget.VideoView;

public class LanuchActivity extends BaseActivity {
    private VideoView video;
    private ImageView lanucher_bg, no_video;
    private ImageSwitcher slider;
    private SharedUtils sharedUtils, util;
    private TextView enter;
    private File[] files, videoFiles;
    private View fouc;
    private OrderManager orderManager;
    ArrayList<MessageBean> advertOuts = new ArrayList<MessageBean>();
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            orderManager = OrderManager.Stub.asInterface(service);
            List<MessageBean> messageBeans = null;
            try {
                messageBeans = orderManager.getDemandlist();

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (messageBeans != null) {
                advertOuts.clear();
                for (int i = 0; i < messageBeans.size(); i++) {
                    if (messageBeans.get(i).getType().equals("3")) {
                        advertOuts.add(messageBeans.get(i));
                    }
                }
                if (advertOuts.size() != 0) {
                    files = new File[advertOuts.size()];
                    for (int i = 0; i < advertOuts.size(); i++) {
                        files[i] = new File(advertOuts.get(i).getFilepath());
                    }
                    handler.sendEmptyMessage(1);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:

                    playImage();
                    break;

            }
        }
    };

    //	private  Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(8);
        setLanguage();
        setContentView(R.layout.lanucher_layout);
        Intent intent = new Intent();
        intent.setAction("com.zed.play.orderaidl");
        //从 Android 5.0开始 隐式Intent绑定服务的方式已不能使用,所以这里需要设置Service所在服务端的包名
        intent.setPackage("com.zed.play");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        File dir = new File(Common.SD+"123");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Intent intentService = new Intent();
        intentService.setAction("pad.com.invisible");
        sendBroadcast(intentService);
        //tf = Typeface.createFromAsset(getAssets(), "font/mb.ttf");
        video = (VideoView) findViewById(R.id.video);
        video.setOnCompletionListener(listener);
        fouc = findViewById(R.id.fouc);
        enter = (TextView) findViewById(R.id.enter);
        //enter.setTypeface(tf);
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

                stopImage();
                if (video != null) {
                    video.stopPlayback();
                }
                Intent intent = new Intent(LanuchActivity.this, MainFragmentActivity.class);
                intent.putExtra("key", KeyEvent.KEYCODE_CAMERA);
                startActivity(intent);
                /*Intent intent = new Intent();
                intent.setAction(Common.ACTION_ADVERT);
				startActivity(intent);*/

            }
        });
    }

    private void setLanguage() {
        util = new SharedUtils(Common.CONFIG);
        Resources resources = getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        if (util.getBooleanValue("is_lan")) {
            //变为英文
            config.locale = Locale.ENGLISH; //英文
            resources.updateConfiguration(config, dm);
        } else {
            config.locale = Locale.CHINA;
            resources.updateConfiguration(config, dm);
        }
        //////////////
    }

    private int detly = 6000;
    private int play;
    int laster = 0;
    private OnCompletionListener listener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer arg0) {

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


    @Override
    public void keyEvent(KeyEvent event) {
        Intent intent = new Intent(LanuchActivity.this, MainFragmentActivity.class);
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


    protected void onPause() {
        super.onPause();
        if (video != null) {
            video.stopPlayback();
        }
    }

    ;
    int imgLen, videoLen;
    private Timer timer;

    @Override
    protected void onResume() {

        super.onResume();
        defaultView();
        Common.DOING = -1;
        fouc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


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
    protected void onDestroy() {

        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onStop() {

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
        Glide.with(BaseApplication.application).load(R.drawable.lanucher_t).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(no_video);

//        Glide.with(BaseApplication.application).load(R.drawable.lanucher_t).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(no_video);
    }

}
