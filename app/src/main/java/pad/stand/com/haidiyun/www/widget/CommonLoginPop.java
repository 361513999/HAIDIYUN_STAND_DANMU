package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
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
import pad.stand.com.haidiyun.www.inter.LoginS;
import pad.stand.com.haidiyun.www.inter.UpdateIp;

public class CommonLoginPop {
    private Context context;
    private UpdateIp updateIp;

    /**
     * 删除弹出框
     */

    public CommonLoginPop(Context context, UpdateIp updateIp) {
        this.context = context;
        this.updateIp = updateIp;
    }

    private SharedUtils utils;
    private IDialog dlg;
    private CommonLoginSendPop loginSendPop;
    private TextView save, cancle;
    private EditText ip;

    public void showSheet() {
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.setting_login, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        ip = (EditText) layout.findViewById(R.id.ip);

        save = (TextView) layout.findViewById(R.id.save);
        cancle = (TextView) layout.findViewById(R.id.cancle);

        utils = new SharedUtils(Common.CONFIG);
        if (utils.getStringValue("IP").length() != 0) {
            ip.setText(utils.getStringValue("IP"));
        }
        ip.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {


            }

            @Override
            public void afterTextChanged(Editable arg0) {

                // 只要EditText变化 就将桌台变掉

            }
        });
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                close();
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip.getText().toString().length() != 0) {
                    loginSendPop = new CommonLoginSendPop(context, "正在登录", ip.getText().toString(), loginS,updateIp);
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
            close();
            if (loginSendPop != null) {
                loginSendPop = null;
            }
        }
    };

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }
}
