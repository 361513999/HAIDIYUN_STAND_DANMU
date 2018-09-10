package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pad.com.haidiyun.www.bean.Dis;
import pad.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.inter.SetI;


public class CommonDiscountPop {
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    dlg.cancel();
                    dlg = null;
                    break;

                default:
                    break;
            }
        }
    };
    private Context context;
    private SharedUtils sharedUtils;
    /**
     * 八八折弹出框
     */
//    private TDialog dlg = null;
    private IDialog dlg;
    private SetI dis;
    public void dis(SetI dis){
        this.dis = dis;
    }
    public CommonDiscountPop(Context context) {
        this.context = context;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.config_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.common_discount, null);
//        final int cFullFillWidth = 500;
//        layout.setMinimumWidth(cFullFillWidth);
        ImageView tip_bg = (ImageView) layout.findViewById(R.id.tip_bg);
        ImageView iv_code = (ImageView) layout.findViewById(R.id.iv_code);
        Button btn_enter = (Button) layout.findViewById(R.id.btn_enter);
        TextView tv = (TextView) layout.findViewById(R.id.tip_view);
        Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("BgUrl")).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(tip_bg);
        if(!sharedUtils.getBooleanValue("is_lan")){
            tv.setText(sharedUtils.getStringValue("Tip"));
        }else{
            tv.setText(sharedUtils.getStringValue("Tip_en"));
        }
        Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("urlDiscount")).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_code);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                if (dlg != null && dlg.isShowing()) {
                    dlg.cancel();
                    if(dis!=null){
                        dis.click();
                    }
                    dlg = null;
                }

            }
        });
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        if (dlg != null) {
            try {
                dlg.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dlg;
    }

    public void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }
}

