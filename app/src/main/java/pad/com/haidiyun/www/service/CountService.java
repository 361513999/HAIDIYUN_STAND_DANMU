package pad.com.haidiyun.www.service;


/**
 * Created by Administrator on 2017/10/20.
 */

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import pad.stand.com.haidiyun.www.base.BaseApplication;

/**
 * 注册验证码计时服务
 *
 * @author liu
 */
public class CountService extends Service {
    public static final String IN_RUNNING = "com.haidiyun.countdown.IN_RUNNING";
    public static final String END_RUNNING = "com.haidiyun.countdown.END_RUNNING";
    private static CountDownTimer mCodeTimer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int key = intent.getIntExtra("key", 66);
        // 第一个参数是总时间， 第二个参数是间隔
        mCodeTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 广播剩余时间
                broadcastUpdate(IN_RUNNING, millisUntilFinished / 1000 + "");
            }

            @Override
            public void onFinish() {
                // 广播倒计时结束,设值
                broadcastUpdate(END_RUNNING);
                // 停止服务
                stopSelf();
                //退出界面,马上进来计时器记住,计时完成再进来触发不了这操作,服务设值
                BaseApplication.clickMap.put(key, true);
            }
        };
        // 开始倒计时
        mCodeTimer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    // 发送广播
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    // 发送带有数据的广播
    private void broadcastUpdate(final String action, String time) {
        final Intent intent = new Intent(action);
        intent.putExtra("time", time);
        sendBroadcast(intent);
    }


}
