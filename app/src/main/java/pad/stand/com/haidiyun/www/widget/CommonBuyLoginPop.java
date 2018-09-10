package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.inter.LoadBuy;
import pad.stand.com.haidiyun.www.inter.LoginS;

public class CommonBuyLoginPop {
    private Context context;

    /**
     * 删除弹出框
     */

    public CommonBuyLoginPop(Context context, LoadBuy loginBuy, LoadBuy cancleBuy) {
        this.context = context;
        this.loginBuy = loginBuy;
        this.cancleBuy = cancleBuy;
    }

    private SharedUtils utils;
    private IDialog dlg;
    private CommonLoginSendPop loginSendPop;
    private TextView save, cancle;
    private EditText user, pass;
    private LoadBuy loginBuy;
    private LoadBuy cancleBuy;

    public void showSheet() {
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.buy_login, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        user = (EditText) layout.findViewById(R.id.user);
        pass = (EditText) layout.findViewById(R.id.pass);
        save = (TextView) layout.findViewById(R.id.save);
        cancle = (TextView) layout.findViewById(R.id.cancle);
        NewDataToast.makeTextTop("请联系您身边的服务员过来为您确认下单", 5000);
        utils = new SharedUtils(Common.CONFIG);
        /*
		if (utils.getStringValue("user").length() != 0) {
			user.setText(utils.getStringValue("user"));
		}
		if (utils.getStringValue("pass").length() != 0) {
			pass.setText(utils.getStringValue("pass"));
		}*/
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //取消操作
                cancleBuy.success(null);
                close();
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().length() != 0) {
//				utils.setStringValue("user", user.getText().toString());
//				utils.setStringValue("pass", pass.getText().toString());
                    String ip = utils.getStringValue("IP");
                    loginSendPop = new CommonLoginSendPop(context, "服务号校验", ip, loginS, null);
                    loginSendPop.showSheet();

                } else {
                    NewDataToast.makeText("请检查信息");
                }
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();

    }

    private LoginS loginS = new LoginS() {

        @Override
        public void success() {

            //登录成功
            loginBuy.success(null);
            close();
        }
    };

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }
}
