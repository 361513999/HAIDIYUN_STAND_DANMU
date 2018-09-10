package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

import java.io.File;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.Mail;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.UpdateIp;
import pad.com.haidiyun.www.widget.SlipButton.OnChangedListener;
import pad.stand.com.haidiyun.www.inter.Remove;

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
    private TextView login, wifi, con, con_video, close, delete, swich_view, cook_view, waite_view, screen_view,
            delete_video, update, delete_adver, con_adver, send_dev, advert_view, price_view, edit_price_view,change;
    private SlipButton swich_btn, waite_btn, screen_btn, cook_btn, advert_btn, price_btn, edit_price_btn;
    private IDialog dlg;
    private long exitTime;
    private SharedUtils utils;

    public CommonConfigPop(Context context) {
        this.context = context;
        utils = new SharedUtils(Common.CONFIG);
    }
    private Remove remove;

    public void setRem(Remove rem) {
        this.remove = rem;
    }
    private CommonSnyDataPop dataPop;
    private CommonSnyAdverPop videoPop;
    protected long lastClick;

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.flip_setting_layout, null);
        final int cFullFillWidth = 600;
        layout.setMinimumWidth(cFullFillWidth);
        change = (TextView) layout.findViewById(R.id.change);
        login = (TextView) layout.findViewById(R.id.login);
        swich_view = (TextView) layout.findViewById(R.id.swich_view);
        swich_btn = (SlipButton) layout.findViewById(R.id.swich_btn);
        cook_view = (TextView) layout.findViewById(R.id.cook_view);
        advert_view = (TextView) layout.findViewById(R.id.advert_view);
        advert_btn = (SlipButton) layout.findViewById(R.id.advert_btn);
        price_view = (TextView) layout.findViewById(R.id.price_view);
        edit_price_view = (TextView) layout.findViewById(R.id.edit_price_view);
        price_btn = (SlipButton) layout.findViewById(R.id.price_btn);
        edit_price_btn = (SlipButton) layout.findViewById(R.id.edit_price_btn);
        waite_btn = (SlipButton) layout.findViewById(R.id.waite_btn);
        cook_btn = (SlipButton) layout.findViewById(R.id.cook_btn);
        waite_view = (TextView) layout.findViewById(R.id.waite_view);
        screen_view = (TextView) layout.findViewById(R.id.screen_view);
        screen_btn = (SlipButton) layout.findViewById(R.id.screen_btn);
        send_dev = (TextView) layout.findViewById(R.id.send_dev);
        wifi = (TextView) layout.findViewById(R.id.wifi);
        con = (TextView) layout.findViewById(R.id.con);
        String ip = utils.getStringValue("IP");
        if (!ip.equals("")) {
            con.setText("同步菜谱数据" + "(" + ip + ")");
        }
        con_video = (TextView) layout.findViewById(R.id.con_video);
        con_adver = (TextView) layout.findViewById(R.id.con_adver);
        close = (TextView) layout.findViewById(R.id.close);
        delete = (TextView) layout.findViewById(R.id.delete);
        delete_adver = (TextView) layout.findViewById(R.id.delete_adver);
        update = (TextView) layout.findViewById(R.id.update);
        update.setText(update.getText() + "[版本号:"
                + BaseApplication.application.getVersion() + "]");
        delete_video = (TextView) layout.findViewById(R.id.delete_video);

        if (utils.getBooleanValue("is_advert")) {
            advert_view.setText("广告模式");
            advert_btn.setCheck(true);
        } else {
            advert_view.setText("正常点餐");
            advert_btn.setCheck(false);
        }
        if (utils.getBooleanValue("isflip")) {
            swich_btn.setCheck(true);
            swich_view.setText("上下翻转模式");
        } else {
            swich_btn.setCheck(false);
            swich_view.setText("左右翻转模式");
        }
        if (utils.getBooleanValue("is_price")) {
            price_btn.setCheck(true);
            price_view.setText("显示总价");
        } else {
            price_btn.setCheck(false);
            price_view.setText("隐藏总价");
        }
        if (utils.getBooleanValue("is_edit_price")) {
            edit_price_btn.setCheck(true);
            edit_price_view.setText("允许临时菜改价");
        } else {
            edit_price_btn.setCheck(false);
            edit_price_view.setText("不允许临时菜改价");
        }
        if (utils.getBooleanValue("isCook")) {
            cook_btn.setCheck(true);
            cook_view.setText("口味必选");
        } else {
            cook_btn.setCheck(false);
            cook_view.setText("直接点菜");
        }

        if (utils.getBooleanValue("is_waite")) {
            //
            waite_btn.setCheck(true);
            waite_view.setText("服务员再次确认点餐模式");
        } else {
            waite_btn.setCheck(false);
            waite_view.setText("顾客自主点餐模式");
        }

        if (utils.getBooleanValue("screen_keep")) {
            screen_btn.setCheck(true);
            screen_view.setText("屏幕常亮模式");
        } else {
            screen_btn.setCheck(false);
            screen_view.setText("系统熄灭策略");
        }
        change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.setBooleanValue("flip",false);
                BaseApplication.application.resetApplicationAll();
            }
        });
        screen_btn.SetOnChangedListener(new OnChangedListener() {

            @Override
            public void OnChanged(boolean CheckState) {
                // TODO Auto-generated method stub
                utils.setBooleanValue("screen_keep", CheckState);
                if (CheckState) {
                    screen_btn.setCheck(true);
                    screen_view.setText("屏幕常亮模式");
                } else {
                    screen_btn.setCheck(false);
                    screen_view.setText("系统熄灭策略");
                }
                handler.sendEmptyMessage(4);
            }
        });
        price_btn.SetOnChangedListener(new OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                // TODO Auto-generated method stub
                utils.setBooleanValue("is_price", CheckState);
                if (CheckState) {
                    price_btn.setCheck(true);
                    price_view.setText("显示总价");
                } else {
                    price_btn.setCheck(false);
                    price_view.setText("隐藏总价");
                }
                handler.sendEmptyMessage(4);
            }
        });
        edit_price_btn.SetOnChangedListener(new OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                // TODO Auto-generated method stub
                utils.setBooleanValue("is_edit_price", CheckState);
                if (CheckState) {
                    edit_price_btn.setCheck(true);
                    edit_price_view.setText("允许临时菜改价");
                } else {
                    edit_price_btn.setCheck(false);
                    edit_price_view.setText("不允许临时菜改价");
                }
//                handler.sendEmptyMessage(4);
            }
        });
        waite_btn.SetOnChangedListener(new OnChangedListener() {

            @Override
            public void OnChanged(boolean CheckState) {
                // TODO Auto-generated method stub
                utils.setBooleanValue("is_waite", CheckState);
                if (CheckState) {
                    waite_view.setText("服务员再次确认点餐模式");
                } else {
                    waite_view.setText("顾客自主点餐模式");
                }
            }
        });
        swich_btn.SetOnChangedListener(new OnChangedListener() {

            @Override
            public void OnChanged(boolean CheckState) {
                // TODO Auto-generated method stub
                utils.setBooleanValue("isflip", CheckState);
                if (CheckState) {
                    swich_view.setText("上下翻转模式");
                } else {
                    swich_view.setText("左右翻转模式");
                }
                Intent intent = new Intent();
                intent.setAction("app.fc.gr");
                context.sendBroadcast(intent);

            }
        });
        advert_btn.SetOnChangedListener(new OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                utils.setBooleanValue("is_advert", CheckState);
                if (CheckState) {
                    advert_view.setText("广告模式");
                } else {
                    advert_view.setText("正常点餐");
                }
            }
        });
        cook_btn.SetOnChangedListener(new OnChangedListener() {

            @Override
            public void OnChanged(boolean CheckState) {
                // TODO Auto-generated method stub
                utils.setBooleanValue("isCook", CheckState);
                if (CheckState) {
                    cook_view.setText("口味必选");
                } else {
                    cook_view.setText("直接点菜");
                }
            }
        });
        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CommonLoginPop commonConfigPop = new CommonLoginPop(context, updateIp);
                commonConfigPop.showSheet();
            }
        });
        wifi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CommonWifiPop wifiPop = new CommonWifiPop(context);
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
        con_video.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (System.currentTimeMillis() - lastClick <= 2000) {
                    return;
                }
                lastClick = System.currentTimeMillis();
                //NewDataToast.makeText(context, "暂不可用");

                CommonSnyVideoPop videoPop = new CommonSnyVideoPop(context,
                        "同步视频资源");
                videoPop.showSheet();

            }
        });
        con_adver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
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
        send_dev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        super.run();
                        try {
                            Mail.send(handler);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            handler.sendEmptyMessage(-1);
                        }
                    }
                }.start();
            }
        });

        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                double_dish_click();
            }
        });
        delete_video.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                double_video_click();
            }
        });
        delete_adver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                double_adver_click();
            }
        });
        update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                CommonApkPop apkPop = new CommonApkPop(context, "在线升级");
                apkPop.showSheet();
            }
        });
        update.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                //关掉服务窗口
                Intent intentService = new Intent();
                intentService.setAction("flip.pad.com.invisible");
                context.sendBroadcast(intentService);
                //跳出
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        });
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                close();
            }
        });
        dlg.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub
                if (remove != null) {
                    remove.remove();
                }
                close();
            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
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
        if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
        {
            exitTime = System.currentTimeMillis();
            NewDataToast.makeText("再按一次清除数据");
        } else {

            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    // 删除数据
                    File file = new File(Common.SD + Common.json);
                    if (file.exists()) {
                        file.delete();
                    }
                    deleteDir(new File(Common.SOURCE));
                    DB.getInstance().clearDish();
                    Glide.get(context).clearDiskCache();
                    handler.sendEmptyMessage(3);
                }
            }.start();
        }

    }

    /**
     * 删除视频
     */
    private synchronized void double_video_click() {
        if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
        {

            exitTime = System.currentTimeMillis();
            NewDataToast.makeText("再按一次清除数据");
        } else {

            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
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
        if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
        {

            exitTime = System.currentTimeMillis();
            NewDataToast.makeText("再按一次清除数据");
        } else {

            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
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
}
