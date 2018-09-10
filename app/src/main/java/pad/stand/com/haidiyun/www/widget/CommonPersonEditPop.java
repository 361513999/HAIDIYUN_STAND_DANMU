package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import pad.stand.com.haidiyun.www.ui.PriceActivity;

/**
 * Created by Administrator on 2018/3/26.
 */

public class CommonPersonEditPop {
    private Context context;
    /**
     * 删除弹出框
     */
    private DishTableBean va;
    private SharedUtils sharedUtils;
    private TextView table_people;

    public CommonPersonEditPop(Context context, TextView table_people) {
        this.context = context;
        this.table_people = table_people;
        this.sharedUtils = new SharedUtils(Common.CONFIG);
    }

    private SharedUtils utils;
    private IDialog dlg;
    private CommonLoginSendPop loginSendPop;
    private TextView save, cancle;
    private EditText user;

    public void showSheet() {
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.select_person_num, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        save = (TextView) layout.findViewById(R.id.save);
        cancle = (TextView) layout.findViewById(R.id.cancle);
        user = (EditText) layout.findViewById(R.id.user);
        if (sharedUtils.getBooleanValue("is_lan")) {
            //变为英文
//            tip.setText(va.getNameEn() + "【origin price】" + va.getPrice());
        } else {
//            tip.setText(va.getName() + "【原价】" + va.getPrice());
        }
        utils = new SharedUtils(Common.CONFIG);
        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                close();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (user.getText().toString().length() != 0 && !"0".equals(user.getText().toString())) {
                    sharedUtils.setIntValue("person", Integer.parseInt(user.getText().toString()));
                    table_people.setText(String.valueOf(sharedUtils.getIntValue("person")));
                    close();
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

