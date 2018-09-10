package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.JellyToggleButton.OnStateChangeListener;
import com.nightonke.jellytogglebutton.State;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.Mail;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.inter.Remove;
import pad.stand.com.haidiyun.www.inter.UpdateIp;
import pad.stand.com.haidiyun.www.inter.UpdateMac;
import pad.stand.com.haidiyun.www.ui.CardValActivity;
import pad.stand.com.haidiyun.www.ui.OrderActivity;
import pad.stand.com.haidiyun.www.ui.ShowImages;
import pad.stand.com.haidiyun.www.utils.CopyFile;

public class CommonConfigPop {

    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case -1:
                    NewDataToast.makeText("发送失败");
                    break;
                case 0:
                    NewDataToast.makeText("反馈成功");
                    break;
                case 1:
                    if (dlg != null && dlg.isShowing()) {
                        dlg.dismiss();
                    }
                    break;
                case 2:
                    NewDataToast.makeText("数据清除完毕");
                    BaseApplication.application.resetApplication();
                    break;
                case 3:
                    NewDataToast.makeText("数据清除完毕");
                    BaseApplication.application.resetApplicationL();
                    break;
                case 4:
                    P.c("清楚完毕==============");
                    NewDataToast.makeText("数据清除完毕");
                    handler.sendEmptyMessage(5);
                    break;
                case 5:
                    BaseApplication.application.resetApplicationAll();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Context context;
    /**
     * 删除弹出框
     */
    private TextView login, wifi, con, con_img, con_video, close, delete,
            delete_img, waite_view, ps_view, prepay_view, screen_view, delete_video, update, cy_view,
            delete_adver, con_adver, change_skin, price_view, room_view, broad_view,change,
            giv_view, txt_view, limt_view, call_view, send_dev, fspace, dish_view, table_view, card_view, advert_view, reas_view, hall_view, time_view, tip_view, tv_language, tv_line, edit_price_view,gua_view,cd_view;
    private JellyToggleButton waite_btn, ps_btn, prepay_btn, screen_btn, price_btn, room_btn, cy_btn,
            giv_btn, txt_btn, limt_btn, call_btn, dish_btn, table_btn, card_btn, advert_btn, reas_btn, hall_btn, time_btn, tip_btn, btn_line, btn_language, broad_btn, edit_price_btn,gua_btn,cd_btn;
    private TDialog dlg;
    private long exitTime;
    private SharedUtils utils;

    public CommonConfigPop(Context context) {
        this.context = context;
        utils = new SharedUtils(Common.CONFIG);
    }

    private CommonSnyDataPop dataPop;
    private CommonSnyImagePop imagePop;
    private CommonSnyAdverPop videoPop;
    protected long lastClick;
    private Remove remove;

    public void setRem(Remove rem) {
        this.remove = rem;
    }

    private void copyFile() {
        CopyFile cf = new CopyFile();
        try {
            File file = new File(Common.SOURCE_SKIN);
            if (!file.exists()) {
                file.mkdirs();
            }
            String sts[] = context.getAssets().list("skins");
            for (int i = 0; i < sts.length; i++) {
                cf.copyFile(context.getAssets().open("skins/" + sts[i]),
                        Common.SOURCE_SKIN + sts[i]);
                P.c("移动成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
            P.c("移动失败");
        }
    }

    private void closeInit() {
        System.out.println("关闭了");
        try {

            AppManager.getAppManager().finishActivity(OrderActivity.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            AppManager.getAppManager().finishActivity(CardValActivity.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        try {
            AppManager.getAppManager().finishActivity(ShowImages.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public Dialog showSheet() {
      //  copyFile();
        dlg = new TDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.setting_layout, null);
        final int cFullFillWidth = 600;
        layout.setMinimumWidth(cFullFillWidth);
        login = (TextView) layout.findViewById(R.id.login);
        waite_btn = (JellyToggleButton) layout.findViewById(R.id.waite_btn);
        ps_btn = (JellyToggleButton) layout.findViewById(R.id.ps_btn);
        prepay_btn = (JellyToggleButton) layout.findViewById(R.id.prepay_btn);
        waite_view = (TextView) layout.findViewById(R.id.waite_view);
        ps_view = (TextView) layout.findViewById(R.id.ps_view);
        broad_view = (TextView) layout.findViewById(R.id.broad_view);
        prepay_view = (TextView) layout.findViewById(R.id.prepay_view);
        change = (TextView) layout.findViewById(R.id.change);
        dish_btn = (JellyToggleButton) layout.findViewById(R.id.dish_btn);
        dish_view = (TextView) layout.findViewById(R.id.dish_view);

        table_btn = (JellyToggleButton) layout.findViewById(R.id.table_btn);
        broad_btn = (JellyToggleButton) layout.findViewById(R.id.broad_btn);
        table_view = (TextView) layout.findViewById(R.id.table_view);
        edit_price_view = (TextView) layout.findViewById(R.id.edit_price_view);
        edit_price_btn = (JellyToggleButton) layout.findViewById(R.id.edit_price_btn);
        cy_view = (TextView) layout.findViewById(R.id.cy_view);
        cy_btn = (JellyToggleButton) layout.findViewById(R.id.cy_btn);
        cd_view = (TextView) layout.findViewById(R.id.cd_view);
        cd_btn  = (JellyToggleButton) layout.findViewById(R.id.cd_btn);

        reas_view = (TextView) layout.findViewById(R.id.reas_view);
        reas_btn = (JellyToggleButton) layout.findViewById(R.id.reas_btn);
        card_view = (TextView) layout.findViewById(R.id.card_view);
        card_btn = (JellyToggleButton) layout.findViewById(R.id.card_btn);
        advert_view = (TextView) layout.findViewById(R.id.advert_view);
        advert_btn = (JellyToggleButton) layout.findViewById(R.id.advert_btn);
        screen_view = (TextView) layout.findViewById(R.id.screen_view);
        tv_line = (TextView) layout.findViewById(R.id.tv_line);
        screen_btn = (JellyToggleButton) layout.findViewById(R.id.screen_btn);
        btn_line = (JellyToggleButton) layout.findViewById(R.id.btn_line);
        price_btn = (JellyToggleButton) layout.findViewById(R.id.price_btn);
        price_view = (TextView) layout.findViewById(R.id.price_view);
        limt_btn = (JellyToggleButton) layout.findViewById(R.id.limt_btn);
        limt_view = (TextView) layout.findViewById(R.id.limt_view);
        hall_btn = (JellyToggleButton) layout.findViewById(R.id.hall_btn);
        hall_view = (TextView) layout.findViewById(R.id.hall_view);
        time_btn = (JellyToggleButton) layout.findViewById(R.id.time_btn);
        time_view = (TextView) layout.findViewById(R.id.time_view);
        tip_btn = (JellyToggleButton) layout.findViewById(R.id.tip_btn);
        btn_language = (JellyToggleButton) layout.findViewById(R.id.btn_language);
        tip_view = (TextView) layout.findViewById(R.id.tip_view);
        tv_language = (TextView) layout.findViewById(R.id.tv_language);
        gua_view  = (TextView) layout.findViewById(R.id.gua_view);
        gua_btn  = (JellyToggleButton) layout.findViewById(R.id.gua_btn);
        room_btn = (JellyToggleButton) layout.findViewById(R.id.room_btn);
        room_view = (TextView) layout.findViewById(R.id.room_view);
        send_dev = (TextView) layout.findViewById(R.id.send_dev);
        fspace = (TextView) layout.findViewById(R.id.fspace);
        call_btn = (JellyToggleButton) layout.findViewById(R.id.call_btn);
        call_view = (TextView) layout.findViewById(R.id.call_view);
        giv_btn = (JellyToggleButton) layout.findViewById(R.id.giv_btn);
        txt_btn = (JellyToggleButton) layout.findViewById(R.id.txt_btn);
        giv_view = (TextView) layout.findViewById(R.id.giv_view);
        txt_view = (TextView) layout.findViewById(R.id.txt_view);
        change_skin = (TextView) layout.findViewById(R.id.change_skin);
        wifi = (TextView) layout.findViewById(R.id.wifi);
        String ddd = FileUtils.getDeviceIpStr();
        if (!ddd.equals("")) {
            wifi.setText("选择合适的wifi" + "(" + FileUtils.getDeviceIpStr() + ")");
        }
        con = (TextView) layout.findViewById(R.id.con);
        String ip = utils.getStringValue("IP");
        if (!ip.equals("")) {
            con.setText("同步菜谱数据" + "(" + ip + ")");
        }
        con_img = (TextView) layout.findViewById(R.id.con_img);
        delete_img = (TextView) layout.findViewById(R.id.delete_img);
        con_video = (TextView) layout.findViewById(R.id.con_video);
        con_adver = (TextView) layout.findViewById(R.id.con_adver);
        close = (TextView) layout.findViewById(R.id.close);
        delete = (TextView) layout.findViewById(R.id.delete);
        delete_adver = (TextView) layout.findViewById(R.id.delete_adver);
        update = (TextView) layout.findViewById(R.id.update);
        update.setText(update.getText() + "[版本号:"
                + BaseApplication.application.getVersion() + "]" + "     "
                + FileUtils.getDeviceId());
        delete_video = (TextView) layout.findViewById(R.id.delete_video);
        change_skin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (utils.getBooleanValue("is_cy")) {
                    NewDataToast.makeText("请先恢复到默认界面");
                    return;
                }
                CommonSkinPop skinPop = new CommonSkinPop(context);
                skinPop.showSheet();
            }
        });
        if (utils.getBooleanValue("is_edit_price")) {
            edit_price_btn.setChecked(true);
            edit_price_view.setText("允许临时菜改价");
        } else {
            edit_price_btn.setChecked(false);
            edit_price_view.setText("不允许临时菜改价");
        }
        if (utils.getBooleanValue("is_advert")) {
            advert_view.setText("广告模式");
            advert_btn.setChecked(true);
        } else {
            advert_view.setText("正常点餐");
            advert_btn.setChecked(false);
        }
        if (utils.getBooleanValue("is_cy")) {
            cy_btn.setChecked(true);
            cy_view.setText("三联界面");

        } else {
            cy_btn.setChecked(false);
            cy_view.setText("默认界面");
        }
        if (utils.getBooleanValue("is_card")) {
            card_view.setText("启用电子支付");
            card_btn.setChecked(true);
        } else {
            card_view.setText("禁用电子支付");
            card_btn.setChecked(false);
        }

        if (utils.getBooleanValue("is_waite")) {
            //
            waite_btn.setChecked(true);
            dish_btn.setEnabled(true);
            waite_view.setText("服务员再次确认点餐模式");
        } else {
            dish_btn.setEnabled(false);
            waite_btn.setChecked(false);
            waite_view.setText("顾客自主点餐模式");
        }
        if (utils.getBooleanValue("is_ps")) {
            ps_btn.setChecked(true);
            ps_view.setText("餐台选择必选密码模式");
        } else {
            ps_btn.setChecked(false);
            ps_view.setText("餐台选择无密码模式");
        }

        if(utils.getBooleanValue("is_gua")){
            gua_view.setText("开启挂单");
            gua_btn.setChecked(true);
        }else{
            gua_view.setText("关闭挂单");
            gua_btn.setChecked(false);
        }
        if(utils.getBooleanValue("is_cd")){
            cd_view.setText("开启补打");
            cd_btn.setChecked(true);
        }else{
            cd_view.setText("关闭补打");
            cd_btn.setChecked(false);
        }

        if (utils.getBooleanValue("is_print")) {
            broad_btn.setChecked(true);
            broad_view.setText("显示通知结账");
        } else {
            broad_btn.setChecked(false);
            broad_view.setText("隐藏通知结账");
        }
        if (utils.getBooleanValue("is_prepay")) {
            prepay_btn.setChecked(true);
            prepay_view.setText("先付费模式");
        } else {
            prepay_btn.setChecked(false);
            prepay_view.setText("后付费模式");
        }
        if (utils.getBooleanValue("is_dish")) {
            //
            dish_btn.setChecked(true);
            dish_view.setText("自助加菜");
        } else {
            dish_btn.setChecked(false);
            dish_view.setText("加菜确认");
        }
        if (utils.getBooleanValue("is_reas")) {
            reas_btn.setChecked(true);
            reas_view.setText("口味必选");


        } else {
            reas_btn.setChecked(false);
            reas_view.setText("直接点餐");
        }
        if (utils.getBooleanValue("is_table")) {
            //
            table_btn.setChecked(true);
            table_view.setText("显示开台");

        } else {
            table_btn.setChecked(false);
            table_view.setText("隐藏开台");
        }
        change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.setBooleanValue("flip",true);
                BaseApplication.application.resetApplicationAll();
            }
        });
        if (utils.getBooleanValue("screen_keep")) {
            screen_btn.setChecked(true);
            screen_view.setText("屏幕常亮模式");
        } else {
            screen_btn.setChecked(false);
            screen_view.setText("系统熄灭策略");
        }

        if (utils.getBooleanValue("isLine")) {
            btn_line.setChecked(true);
            tv_line.setText("恢复正常点餐");
        } else {
            btn_line.setChecked(false);
            tv_line.setText("切换排队点餐");
        }

        if (utils.getBooleanValue("is_price")) {
            price_btn.setChecked(true);
            price_view.setText("显示总价");
        } else {
            price_btn.setChecked(false);
            price_view.setText("隐藏总价");
        }

        if (utils.getBooleanValue("is_room")) {
            room_btn.setChecked(true);
            room_view.setText("显示包房菜");
        } else {
            room_btn.setChecked(false);
            room_view.setText("隐藏包房菜");
        }
        if (utils.getBooleanValue("is_hall")) {
            hall_btn.setChecked(true);
            hall_view.setText("隐藏大厅菜");
        } else {
            hall_btn.setChecked(false);
            hall_view.setText("显示大厅菜");
        }
        if (utils.getBooleanValue("is_time")) {
            time_btn.setChecked(true);
            time_view.setText("开启供应时间");
        } else {
            time_btn.setChecked(false);
            time_view.setText("关闭供应时间");
        }
        if (utils.getBooleanValue("is_tip")) {
            tip_btn.setChecked(true);
            tip_view.setText("开启下单提示");
        } else {
            tip_btn.setChecked(false);
            tip_view.setText("关闭下单提示");
        }
        if (utils.getBooleanValue("is_lan")) {
            btn_language.setChecked(true);
            tv_language.setText("切换中文");
        } else {
            btn_language.setChecked(false);
            tv_language.setText("切换英文");
        }

        if (utils.getBooleanValue("is_limit")) {
            limt_btn.setChecked(true);
            limt_view.setText("开启限制数量");
        } else {
            limt_btn.setChecked(false);
            limt_view.setText("取消限制数量");
        }
        if (utils.getBooleanValue("is_giv")) {
            giv_btn.setChecked(true);
            giv_view.setText("每行三菜");
        } else {

            giv_btn.setChecked(false);
            giv_view.setText("每行四菜");
        }
        if (utils.getBooleanValue("is_txt")) {
            txt_btn.setChecked(true);
            txt_view.setText("文字模式");
        } else {
            txt_btn.setChecked(false);
            txt_view.setText("图文模式");
        }

        if (utils.getBooleanValue("is_call")) {
            call_btn.setChecked(true);
            call_view.setText("关闭呼叫服务");
        } else {
            call_btn.setChecked(false);
            call_view.setText("开启呼叫服务");
        }
        File file = new File(Common.APK_LOG
                + TimeUtil.getTimeLog(System.currentTimeMillis()) + ".txt");

        fspace.setText("日志大小"
                + ((file != null && file.exists()) ? FormetFileSize(file
                .length()) : "0kb"));
        send_dev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new Thread() {
                    @Override
                    public void run() {

                        super.run();
                        try {
//                            Mail.send(handler, "yanghongyu20130603@163.com", "yanghongyu20130603@163.com", "smtp", "smtp.163.com", "yangweibo851003");
                            Mail.send(handler, "bryant_liu24@126.com", "bryant_liu24@126.com", "smtp", "smtp.126.com", "liuzheng666");
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }.start();
            }
        });
        cy_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_cy", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        cy_view.setText("默认界面");
                    }
                    if (state.equals(State.RIGHT)) {
                        cy_view.setText("三联界面");
                    }
                }

            }
        });
        gua_btn.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {

                    utils.setBooleanValue("is_gua", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        gua_view.setText("关闭挂单");
                    }
                    if (state.equals(State.RIGHT)) {
                        gua_view.setText("开启挂单");
                    }

                }
            }
        });

        cd_btn.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {

                    utils.setBooleanValue("is_cd", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        cd_view.setText("关闭补打");
                    }
                    if (state.equals(State.RIGHT)) {
                        cd_view.setText("开启补打");
                    }

                }
            }
        });

        call_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {


                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {

                    utils.setBooleanValue("is_call", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        call_view.setText("开启呼叫服务");
                    }
                    if (state.equals(State.RIGHT)) {
                        call_view.setText("关闭呼叫服务");
                    }
                    closeInit();
                }
            }
        });

        reas_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {


                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {

                    utils.setBooleanValue("is_reas", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        reas_view.setText("直接点餐");
                    }
                    if (state.equals(State.RIGHT)) {
                        reas_view.setText("口味必选");
                    }
                }
            }
        });
        edit_price_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {


                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {

                    utils.setBooleanValue("is_edit_price", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        edit_price_view.setText("不允许临时菜改价");
                    }
                    if (state.equals(State.RIGHT)) {
                        edit_price_view.setText("允许临时菜改价");
                    }
                }
            }
        });
        advert_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {


                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {

                    utils.setBooleanValue("is_advert", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        advert_view.setText("正常点餐");
                    }
                    if (state.equals(State.RIGHT)) {
                        advert_view.setText("广告模式");
                    }
                    closeInit();
                }
            }
        });

        card_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_card", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        card_view.setText("禁用电子支付");
                    }
                    if (state.equals(State.RIGHT)) {
                        card_view.setText("启用电子支付");
                    }
                    closeInit();

                }
            }
        });
        giv_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_giv", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        giv_view.setText("每行四菜");
                    }
                    if (state.equals(State.RIGHT)) {
                        giv_view.setText("每行三菜");
                    }
                    closeInit();
                }

            }
        });
        txt_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_txt", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        txt_view.setText("图文模式");
                    }
                    if (state.equals(State.RIGHT)) {
                        txt_view.setText("文字模式");
                    }
                    closeInit();
                }

            }
        });
        room_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_room", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        room_view.setText("隐藏包房菜");
                    }
                    if (state.equals(State.RIGHT)) {
                        room_view.setText("显示包房菜");
                    }
                    closeInit();
                }

            }
        });

        hall_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_hall", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        hall_view.setText("显示大厅菜");
                    }
                    if (state.equals(State.RIGHT)) {
                        hall_view.setText("隐藏大厅菜");
                    }
                    closeInit();
                }

            }
        });
        time_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_time", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        time_view.setText("关闭供应时间");
                    }
                    if (state.equals(State.RIGHT)) {
                        time_view.setText("开启供应时间");
                    }
                    closeInit();
                }

            }
        });
        tip_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_tip", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        tip_view.setText("关闭下单提示");
                    }
                    if (state.equals(State.RIGHT)) {
                        tip_view.setText("开启下单提示");
                    }

                }

            }
        });
        btn_language.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_lan", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        tv_language.setText("切换英文");
                        BaseApplication.application.resetApplicationAll();
                    }
                    if (state.equals(State.RIGHT)) {
                        tv_language.setText("切换中文");
                        BaseApplication.application.resetApplicationAll();
                    }

                }

            }
        });
        limt_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_limit", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        limt_view.setText("关闭限制数量");
                    }
                    if (state.equals(State.RIGHT)) {
                        limt_view.setText("开启限制数量");
                    }
                }

            }
        });

        price_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_price", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        price_view.setText("隐藏总价");
                    }
                    if (state.equals(State.RIGHT)) {
                        price_view.setText("显示总价");
                    }
                }

            }
        });
        screen_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("screen_keep", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        screen_view.setText("系统熄灭策略");
                    }
                    if (state.equals(State.RIGHT)) {
                        screen_view.setText("屏幕常亮模式");
                    }
                    handler.sendEmptyMessage(5);
                }

            }
        });
        btn_line.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("isLine", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        tv_line.setText("切换排队点餐");
                    }
                    if (state.equals(State.RIGHT)) {
                        tv_line.setText("恢复正常点餐");
                    }
//                    handler.sendEmptyMessage(5);
                }

            }
        });
        waite_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_waite", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        if (dish_btn.isChecked()) {
                            dish_btn.setChecked(false);
                            dish_view.setText("加菜确认");
                            dish_btn.setEnabled(false);
                            utils.setBooleanValue("is_dish", false);
                        }
                        waite_view.setText("顾客自主点餐模式");
                    }
                    if (state.equals(State.RIGHT)) {
                        dish_btn.setEnabled(true);
                        waite_view.setText("服务员再次确认点餐模式");
                    }
                }

            }
        });
        ps_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_ps", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        ps_view.setText("餐台选择无密码模式");
                    }
                    if (state.equals(State.RIGHT)) {
                        ps_view.setText("餐台选择必选密码模式");
                    }
                }

            }
        });
        broad_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_print", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        broad_view.setText("隐藏通知结账");
                    }
                    if (state.equals(State.RIGHT)) {
                        broad_view.setText("显示通知结账");
                    }
                }

            }
        });
        prepay_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {
                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_prepay", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        prepay_view.setText("后付费模式");
                    }
                    if (state.equals(State.RIGHT)) {
                        prepay_view.setText("先付费模式");
                    }
                }

            }
        });
        table_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_table", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        table_view.setText("隐藏开台");
                    } else if (state.equals(State.RIGHT)) {
                        table_view.setText("显示开台");
                    }
                    closeInit();
                }
            }
        });

        dish_btn.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public void onStateChange(float process, State state,
                                      JellyToggleButton jtb) {

                if (state.equals(State.RIGHT) || state.equals(State.LEFT)) {
                    utils.setBooleanValue("is_dish", jtb.isChecked());
                    if (state.equals(State.LEFT)) {
                        dish_view.setText("加菜确认");
                    }
                    if (state.equals(State.RIGHT)) {
                        dish_view.setText("自助加菜");
                        // if(!utils.getBooleanValue("is_waite")){
                        // utils.setBooleanValue("is_dish", false);
                        // dish_btn.setChecked(false);
                        // dish_view.setText("加菜确认");
                        // //NewDataToast.makeText("服务员模式才能开启此项");
                        // return;
                        // }

                    }
                }

            }
        });

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //配置ip及登录
                CommonLoginPop commonConfigPop = new CommonLoginPop(context, updateIp);
                commonConfigPop.showSheet();
            }
        });
        wifi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CommonWifiPop wifiPop = new CommonWifiPop(context, updateMac);
                wifiPop.showSheet();

            }
        });
        con.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (System.currentTimeMillis() - lastClick <= 2000) {
                    return;
                }
                lastClick = System.currentTimeMillis();
                if (dataPop == null) {
                    dataPop = new CommonSnyDataPop(context, "同步菜品资源");
                    dataPop.showSheet();
                    dataPop = null;
                }

            }
        });
        con_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (System.currentTimeMillis() - lastClick <= 2000) {
                    return;
                }
                lastClick = System.currentTimeMillis();
                if (imagePop == null) {
                    imagePop = new CommonSnyImagePop(context, "同步菜品图片资源");
                    imagePop.showSheet();
                    imagePop = null;
                }

            }
        });
        con_video.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (System.currentTimeMillis() - lastClick <= 2000) {
                    return;
                }
                lastClick = System.currentTimeMillis();
                // NewDataToast.makeText(context, "暂不可用");

                CommonSnyVideoPop videoPop = new CommonSnyVideoPop(context,
                        "同步视频资源");
                videoPop.showSheet();

            }
        });
        con_adver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (System.currentTimeMillis() - lastClick <= 2000) {
                    return;
                }
                lastClick = System.currentTimeMillis();
                if (videoPop == null) {
                    videoPop = new CommonSnyAdverPop(context, "同步推广资源");
                    videoPop.showSheet();
                    videoPop = null;
                }

            }
        });
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                double_dish_click();
            }
        });
        delete_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                double_img_click();
            }
        });

        delete_video.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                double_video_click();
            }
        });
        delete_adver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                double_adver_click();
            }
        });
        update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                CommonApkPop apkPop = new CommonApkPop(context, "在线升级");
                apkPop.showSheet();
            }
        });
        update.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                Intent intentService = new Intent();
                intentService.setAction("pad.com.invisible");
                context.sendBroadcast(intentService);
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dlg != null && dlg.isShowing()) {
                    dlg.cancel();
                }
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (remove != null) {
                    remove.remove();
                }
            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileS == 0) {
            return "0.00";
        }
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除文件
     *
     * @param dir
     * @return
     */
    private synchronized boolean deleteDir(File dir) {
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

    /**
     * 删除菜品
     */
    private synchronized void double_dish_click() {
        if ((System.currentTimeMillis() - exitTime) > 1000) // System.currentTimeMillis()无论何时调用，肯定大于2000
        {
            exitTime = System.currentTimeMillis();
            NewDataToast.makeTextL("再按一次清除数据", 400);
        } else {
            exitTime = 0;
            Glide.get(context).clearMemory();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // 删除数据
                    File file = new File(Common.DB_DIR + Common.DB_NAME);
                    if (file.exists()) {
                        file.delete();
                    }
                    deleteDir(new File(Common.ZIP));
                    Glide.get(context).clearDiskCache();
                    handler.sendEmptyMessage(3);
                }
            }.start();
        }

    }

    /**
     * 删除菜品图片
     */
    private synchronized void double_img_click() {
        if ((System.currentTimeMillis() - exitTime) > 1500) // System.currentTimeMillis()无论何时调用，肯定大于2000
        {
            exitTime = System.currentTimeMillis();
            NewDataToast.makeText("再按一次清除数据");
        } else {
            exitTime = 0;
            Glide.get(context).clearMemory();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // 删除数据
                    /*
                     * File file = new File(Common.DB_DIR+Common.DB_NAME);
					 * if(file.exists()){ file.delete(); }
					 */
                    deleteDir(new File(Common.ZIP));
                    Glide.get(context).clearDiskCache();
                    ImageLoader.getInstance().clearDiscCache();
                    ImageLoader.getInstance().clearMemoryCache();
                    ImageLoader.getInstance().clearDiskCache();

                    handler.sendEmptyMessage(4);
                }
            }.start();
        }

    }

    /**
     * 删除视频
     */
    private synchronized void double_video_click() {
        if ((System.currentTimeMillis() - exitTime) > 1500) // System.currentTimeMillis()无论何时调用，肯定大于2000
        {

            exitTime = System.currentTimeMillis();
            NewDataToast.makeText("再按一次清除数据");
        } else {
            exitTime = 0;
            new Thread() {
                @Override
                public void run() {

                    super.run();
                    // 删除数据
                    deleteDir(new File(Common.SOURCE_VIDEO));
                    Glide.get(context).clearDiskCache();
                    handler.sendEmptyMessage(3);
                }
            }.start();
        }

    }

    /**
     * 删除视频
     */
    private synchronized void double_adver_click() {
        if ((System.currentTimeMillis() - exitTime) > 1500)
        // System.currentTimeMillis()无论何时调用，肯定大于2000
        {
            exitTime = System.currentTimeMillis();
            NewDataToast.makeText("再按一次清除数据");
        } else {
            exitTime = 0;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // 删除数据
                    deleteDir(new File(Common.SOURCE_ADVER));
                    Glide.get(context).clearDiskCache();
                    handler.sendEmptyMessage(3);
                }
            }.start();
        }

    }

    private UpdateIp updateIp = new UpdateIp() {

        @Override
        public void change(String ip) {
            con.setText("同步菜谱数据" + "(" + ip + ")");
        }
    };

    private UpdateMac updateMac = new UpdateMac() {

        @Override
        public void change(String mac) {
            wifi.setText("选择合适的wifi" + "(" + mac + ")");
        }
    };


}
