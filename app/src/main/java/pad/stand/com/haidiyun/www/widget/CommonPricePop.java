package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.ui.PriceActivity;

public class CommonPricePop {
    private Context context;
    /**
     * 删除弹出框
     */
    private DishTableBean va;
    private SharedUtils sharedUtils;

    public CommonPricePop(Context context, DishTableBean va) {
        this.context = context;
        this.va = va;
        this.sharedUtils = new SharedUtils(Common.CONFIG);
    }

    private SharedUtils utils;
    private IDialog dlg;
    private CommonLoginSendPop loginSendPop;
    private TextView price_save, price_cancle;
    private EditText txt, txt_name;
    private TextView tip;
    private LinearLayout ll_name;

    public void showSheet() {
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.modify_price_pop, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        //----修改时价
        price_save = (TextView) layout.findViewById(R.id.price_sure);
        price_cancle = (TextView) layout.findViewById(R.id.price_cancle);
        txt = (EditText) layout.findViewById(R.id.txt);
        txt_name = (EditText) layout.findViewById(R.id.txt_name);
        ll_name = (LinearLayout) layout.findViewById(R.id.ll_name);
        if (va.isTemp()) {
            ll_name.setVisibility(View.VISIBLE);
        } else {
            ll_name.setVisibility(View.GONE);
        }
        tip = (TextView) layout.findViewById(R.id.tip);
        if (sharedUtils.getBooleanValue("is_lan")) {
            //变为英文
            tip.setText(va.getNameEn() + "【origin price】" + va.getPrice());
        } else {
            tip.setText(va.getName() + "【原价】" + va.getPrice());
        }
        utils = new SharedUtils(Common.CONFIG);
        /*if (utils.getStringValue("pass").length() != 0) {
            pass.setText(utils.getStringValue("pass"));
		}*/

        price_cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                close();
            }
        });
        price_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (txt.getText().toString().length() != 0) {

                    new Thread() {
                        public void run() {
                            va.setPrice(Double.parseDouble(txt.getText().toString()));
                            va.setEditPrice(true);
                            DB.getInstance().changePrice(va);
                            handler.sendEmptyMessage(1);
                        }
                    }.start();


                }
                if (txt_name.getText().toString().length() != 0) {

                    new Thread() {
                        public void run() {
                            if (sharedUtils.getBooleanValue("is_lan")) {
                                //变为英文
                                va.setNameEn(txt_name.getText().toString());
                                DB.getInstance().changeNameEn(va);
                            } else {
                                va.setName(txt_name.getText().toString());
                                DB.getInstance().changeName(va);
                            }
                            handler.sendEmptyMessage(1);
                        }
                    }.start();


                }
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    AppManager.getAppManager().finishActivity(PriceActivity.class);
                } catch (Exception e) {

                }
            }
        });
        dlg.setContentView(layout);
        dlg.show();

    }

    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    close();
                    Intent intent = new Intent();
                    intent.setAction("pad.haidiyun.www.change_price");
                    context.sendBroadcast(intent);
                    break;

                default:
                    break;
            }
        }

        ;
    };

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            try {
                AppManager.getAppManager().finishActivity(PriceActivity.class);
            } catch (Exception e) {

            }
            dlg.cancel();
            dlg = null;
        }
    }
}
