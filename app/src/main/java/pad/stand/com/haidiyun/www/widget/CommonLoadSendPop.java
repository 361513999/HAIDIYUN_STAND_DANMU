package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
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

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.inter.Remove;


/**
 * 公共加载
 *
 * @author Administrator
 */
public class CommonLoadSendPop {
    private Context context;
    private TextView load_tv;
    private IDialog dlg;
    private String msg;
    private ImageView load_img, load_close;
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {

                case -3:
                    NewDataToast.makeText("取消获取");
                    cancle();

                default:
                    break;
            }
        }

        ;
    };

    public CommonLoadSendPop(Context context, String msg) {
        this.context = context;
        this.msg = msg;

    }

    private Remove remove;

    public void setCloseLis(Remove remove) {
        this.remove = remove;
    }

    public Dialog showSheet(boolean flag) {
        dlg = new IDialog(context, R.style.buy_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.loading, null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText(msg);
        load_img = (ImageView) layout.findViewById(R.id.load_img);
        load_close = (ImageView) layout.findViewById(R.id.load_close);
        if (flag) {
            load_close.setVisibility(View.GONE);
        }

        load_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (remove != null) {
                    remove.remove();
                }
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


    public void cancle() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }

}
