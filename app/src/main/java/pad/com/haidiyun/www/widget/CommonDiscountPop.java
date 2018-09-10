package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;


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
    private TDialog dlg = null;

    public CommonDiscountPop(Context context) {
        this.context = context;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    public Dialog showSheet() {
        dlg = new TDialog(context, R.style.config_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.flip_common_discount, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        ImageView iv_code = (ImageView) layout.findViewById(R.id.iv_code);
        Button btn_enter = (Button) layout.findViewById(R.id.btn_enter);
        Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("urlDiscount")).asBitmap().placeholder(R.drawable.code_def).into(iv_code);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (dlg != null) {
                    dlg.cancel();
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

}

