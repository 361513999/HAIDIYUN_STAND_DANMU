package pad.com.haidiyun.www.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.inter.ConnectTimeOut;
import pad.com.haidiyun.www.inter.LoginS;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.TcpClient;


/**
 * 发送下单
 *
 * @author Administrator
 */
public class Common433SendPop {
    private static final String TAG = "Common433SendPop";
    private Context context;
    private TextView load_tv;
    //    private IDialog dlg;
    private String msg;
    private ImageView load_img, load_close;
    private SharedUtils utils;
    private LoginS loginS;
    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    NewDataToast.makeText("已通知");
//                    close();
//                    dlg.dismiss();
                    if (loginS != null) {
                        loginS.success();
                    }

                    break;
                case 0:
                    NewDataToast.makeText((String) msg.obj);
//                    close();
                    break;
                case -1:
                    NewDataToast.makeText("请检查与主机的连接配置");
//                    dlg.dismiss();
//                    close();
                    break;
                case -2:
                    NewDataToast.makeText("操作失败,请检查WIFI环境和主机配置");
//                    close();
                    break;
                case -3:
//				NewDataToast.makeText(context, "再试一次");
//                    cancle();
                    break;
                case -4:
//                    close();
                    break;
                case -5:
                    NewDataToast.makeText("获取响应数据超时");
//                    close();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    public Common433SendPop(Context context, String msg, LoginS loginS) {
        this.context = context;
        this.loginS = loginS;
        this.msg = msg;
        utils = new SharedUtils(Common.CONFIG);
    }


    public void showSheet() {
//        dlg = new IDialog(context, R.style.buy_pop_style);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        FrameLayout layout = (FrameLayout) inflater.inflate(
//                R.layout.loading, null);
//        load_tv = (TextView) layout.findViewById(R.id.load_tv);
//        load_tv.setText(msg);
//        load_img = (ImageView) layout.findViewById(R.id.load_img);
//        load_close = (ImageView) layout.findViewById(R.id.load_close);
        try {
//            String ip = "119.23.142.29";
//            String ip = "adv.haidiyun.top";
            String ip = utils.getStringValue("callingServer");
            int port = 9000;
//            connect(ip, port + "");
            Log.e(TAG, 1 + "");
            addConnect(ip, port);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, 2 + "");
            e.printStackTrace();
            handler.sendEmptyMessage(-1);
        }
//        load_close.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                if (client != null) {
//                    client.disconnect();
//                }
//                handler.sendEmptyMessage(-3);
//            }
//        });
//        // 动画
//        Animation operatingAnim = AnimationUtils.loadAnimation(context,
//                R.anim.anim_load);
//        // 匀速旋转
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);
//        load_img.startAnimation(operatingAnim);
//        dlg.setCanceledOnTouchOutside(false);
//        dlg.setContentView(layout);
//        dlg.show();
//        return dlg;
    }

    private ConnectTimeOut timeOut = new ConnectTimeOut() {
        @Override
        public void recyle() {
            // TODO Auto-generated method stub
            //多次连接超时
            handler.sendEmptyMessage(-2);
        }
    };

//    private void cancle() {
//        if (dlg != null && dlg.isShowing()) {
//            dlg.cancel();
//            dlg = null;
//        }
//    }

//    private void close() {
//        if (dlg != null && dlg.isShowing()) {
//            if (client != null) {
//                client.disconnect();
//            }
//            dlg.cancel();
//            dlg = null;
//        }
//    }


    private void addConnect(String ip, int port) {
        StringBuffer buffer = new StringBuffer();
        new TcpClient() {

            @Override
            public void readTimeOut(SocketTransceiver transceiver) {
                // TODO Auto-generated method stub
                Log.e(TAG, 3 + "");
                String s = "";
//                dlg.dismiss();
            }

            @Override
            public void onReceive(SocketTransceiver transceiver, String buffer) {
                // TODO Auto-generated method stub
                Log.e(TAG, 6 + "");
                String json = buffer.substring(0, buffer.length() - 5);
                Log.e(TAG, "json值:" + json);
                Message msg = new Message();//请求消息错误
                msg.what = 1;
                msg.obj = json;
                handler.sendMessage(msg);
                //----------
//                if (client != null) {
//                    client.disconnect();
//                }

            }

            @Override
            public void onDisconnect(SocketTransceiver transceiver) {
                // TODO Auto-generated method stub
                String s = "";
//                dlg.dismiss();
            }

            @Override
            public void onConnectFailed() {
                // TODO Auto-generated method stub
                String s = "";
//                dlg.dismiss();
            }

            @Override
            public void onConnect(SocketTransceiver transceiver) {
                // TODO Auto-generated method stub
                Log.e(TAG, 4 + "");
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String regCode = utils.getStringValue("regCode");
                String table_name = utils.getStringValue("table_name");
                if ("".equals(table_name)) {
                    table_name = " ";
                }
//                transceiver.sendTxt("[003BREQ=RegCode:9014D37C8518;TableName:测试加水;ReqMsg:加水]");
//                String text = "REQ=RegCode:" + regCode + ";TableName:" + table_name + ";ReqMsg:" + msg + "]";
                String text = "[003BREQ=RegCode:" + regCode + ";TableName:" + table_name + ";ReqMsg:" + msg + "]";
//                String pre = Integer.toHexString(text.length() + 4);
//                if (pre.length() == 1) {
//                    pre = "000" + pre;
//                } else if (pre.length() == 2) {
//                    pre = "00" + pre;
//                } else if (pre.length() == 3) {
//                    pre = "0" + pre;
//                } else if (pre.length() == 4) {
//                }
//                String sendEdit = "[" + pre + text;
                transceiver.sendTxt(text);
                Log.e(TAG, 5 + "");
            }
        }.connect(ip, port, buffer);

    }

}
