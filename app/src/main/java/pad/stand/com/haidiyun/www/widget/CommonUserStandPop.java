package pad.stand.com.haidiyun.www.widget;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.MessageBean;
import com.android.settings.OrderManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.Remove;
import pad.stand.com.haidiyun.www.inter.UD;
import pad.stand.com.haidiyun.www.utils.ButtonUtil;
import pad.stand.com.haidiyun.www.widget.dialog.Animations.Effectstype;

import static com.zc.http.okhttp.log.LoggerInterceptor.TAG;

public class CommonUserStandPop {
    private Context context;
    private TDialog dlg;
    private TextView name, card_name, card_yue;
    private EditText card_no, card_pa;
    private SharedUtils sharedUtils;
    private TextView save, cancle;
    private Handler dl;
    private String ip, billId;

    public CommonUserStandPop(Context context, Handler dl) {
        this.context = context;
        this.dl = dl;
        sharedUtils = new SharedUtils(Common.CONFIG);
        ip = sharedUtils.getStringValue("IP");
        billId = sharedUtils.getStringValue("billId");

        Intent intent = new Intent();
        intent.setAction("com.zed.play.orderaidl");
        //从 Android 5.0开始 隐式Intent绑定服务的方式已不能使用,所以这里需要设置Service所在服务端的包名
        intent.setPackage("com.zed.play");
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }

    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case -2:
                    //创建成功
                    P.c("有没有数据。。。。");
                    List<MessageBean> messageBeans = null;
                    try {
                        messageBeans = orderManager.getDemandlist();

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    ArrayList<MessageBean> advertOuts = new ArrayList<MessageBean>();
                    if (messageBeans != null) {

                        for (int i = 0; i < messageBeans.size(); i++) {
                            if (messageBeans.get(i).getType().equals("3")) {
                                advertOuts.add(messageBeans.get(i));
                            }
                        }
                        int len = advertOuts.size();
                        if (len != 0) {
                            if (len == 1) {
                                ImageLoader.getInstance().displayImage("file:///" + advertOuts.get(0).getFilepath(), img0);
                            } else if (len == 2) {
                                ImageLoader.getInstance().displayImage("file:///" + advertOuts.get(0).getFilepath(), img0);
                                ImageLoader.getInstance().displayImage("file:///" + advertOuts.get(1).getFilepath(), img1);
                            }
                        }
                    }
                    break;
                case -1:
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        loadSendPop = null;
                    }
                    NewDataToast.makeText("获取响应超时");
                    break;
                case 0:
                    NewDataToast.makeText((String) msg.obj);
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        loadSendPop = null;
                    }
                    break;
                case 1:
                    NewDataToast.makeText("支付成功");
                    dl.sendEmptyMessage(-8);
                    close();
                    break;
                case 2:
                    stop();
                    if (msg.arg1 == 1) {
                        qr_txt.setText("微信支付");
                        setName("扫码成功后请确认订单支付金额");
                    } else if (msg.arg1 == 2) {
                        qr_txt.setText("支付宝支付");
                        setName("扫码成功后请确认订单支付金额");
                    } else if (msg.arg1 == 3) {
                        qr_txt.setText("请打开微信或支付宝扫码支付");
                        setName("扫码成功后请确认订单支付金额");
                    }
                    phone_layout.setVisibility(View.VISIBLE);
                    user_layout.setVisibility(View.GONE);
                    ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
                    String temp = (String) msg.obj;
                    createImage(temp);
                    //倒计时30s关闭二维码
                    timer = new Timer();
                    timer.schedule(new RequestTimerTask(), 1000, 1000);
                    //开启订单查询
                    billHandler.post(billRunnable);
                    break;
                case 4:
                    calcleBill();
                    NewDataToast.makeText("支付成功");
                    close();
                    dl.sendEmptyMessage(-8);
                    break;
                case 3:
                    stop();
                    NewDataToast.makeText("获取超时请重新操作");
                    break;
                case 66:
//                    name.setText("剩余:" + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        close();
//                        name.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Handler billHandler = new Handler();
    private Runnable billRunnable = new Runnable() {
        @Override
        public void run() {
            checkBill();
            billHandler.postDelayed(billRunnable, 5000);
        }
    };
    RequestCall billCall = null;

    private void checkBill() {
        P.c("查询支付状态");
        final JSONObject obj = new JSONObject();
        try {
            obj.put("data", billId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        phoneCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_PAY_STATUS))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(obj.toString()).build();
        phoneCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    P.c("支付结果"+FileUtils.formatJson(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if (jsonObject.getBoolean("Success")) {
                        handler.sendEmptyMessage(4);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    // 生成QR图
    private void createImage(String formatValue) {
        try {
            // 需要引入core包
            int QR_WIDTH = 150;
            int QR_HEIGHT = 150;
            QRCodeWriter writer = new QRCodeWriter();

            if (formatValue == null || "".equals(formatValue)
                    || formatValue.length() < 1) {
                return;
            }

            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(formatValue,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
            // 二维码的code
            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(formatValue,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            Bitmap temp = addLogo(bitmap, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            qr.setImageBitmap(temp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void calcleBill() {
        ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
        if (phoneCall != null) {
            phoneCall.cancel();
            phoneCall = null;
        }
        if (billCall != null) {
            billCall.cancel();
            billCall = null;
        }
        if(timer!=null){
            timer.cancel();
        }

        billHandler.removeCallbacks(billRunnable);

    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }


    /**
     * 滚动监听
     */

    UD ud = new UD() {
        @Override
        public void change(boolean isOpen, boolean init) {
            if (init) {
                P.c("运行========1");
                if (isOpen) {
                    P.c("运行========2");

                } else {
                    P.c("运行========3");
                }
            } else {
                P.c("运行========4");
                if (!isOpen) {
                    P.c("运行========5");
                }
            }
        }
    };
    private ImageView qr, img0, img1;
    private View parent_d;
    private LinearLayout layout0, layout1, user_layout;
    private RelativeLayout phone_layout;
    private UDlayout ud_layout;
    private LinearLayout btn0, btn1, btn2;
    private FrameLayout child;
    private TextView qr_txt, qr_cancle;
    //倒计时器
    private int recLen = 30;
    Timer timer = null;

    private OrderManager orderManager;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            orderManager = OrderManager.Stub.asInterface(service);
            P.c("服务连接成功了吗");
            handler.sendEmptyMessage(-2);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public void showSheet() {
        dlg = new TDialog(context, R.style.config_pop_style);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.show();
        Window layout = dlg.getWindow();
        layout.getDecorView().setPadding(0, 0, 0, 0);
        layout.setGravity(Gravity.CENTER);
        layout.setContentView(R.layout.user_pop_stand_view);
        WindowManager.LayoutParams lp = layout.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        layout.setAttributes(lp);


       /* LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.user_pop_view, null);
*/


        ud_layout = (UDlayout) layout.findViewById(R.id.ud_layout);
        ud_layout.setUD(ud);
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (connection != null) {
                    try {
                        context.unbindService(connection);
                        connection = null;
                        if (lReceiver != null) {
                            context.unregisterReceiver(lReceiver);
                            lReceiver = null;
                        }
                    } catch (Exception e) {

                    }
                }
                calcleBill();
            }
        });
        ud_layout.post(new Runnable() {
            @Override
            public void run() {
                P.c("ud_layout.getMeasuredHeight()" + ud_layout.getMeasuredHeight());
                SCROOL_HEIGHT = -dip2px(ud_layout.getMeasuredHeight());
            }
        });
        qr = (ImageView) layout.findViewById(R.id.qr);
        parent_d = layout.findViewById(R.id.parent_d);
        child = (FrameLayout) layout.findViewById(R.id.child);
        card_no = (EditText) layout.findViewById(R.id.card_no);
        card_pa = (EditText) layout.findViewById(R.id.card_pa);
        layout0 = (LinearLayout) layout.findViewById(R.id.layout0);
        layout1 = (LinearLayout) layout.findViewById(R.id.layout1);
        qr_txt = (TextView) layout.findViewById(R.id.qr_txt);
        card_name = (TextView) layout.findViewById(R.id.card_name);
        card_yue = (TextView) layout.findViewById(R.id.card_yue);
        btn0 = (LinearLayout) layout.findViewById(R.id.btn0);
        btn1 = (LinearLayout) layout.findViewById(R.id.btn1);
        btn2 = (LinearLayout) layout.findViewById(R.id.btn2);
        qr_cancle = (TextView) layout.findViewById(R.id.qr_cancle);
        user_layout = (LinearLayout) layout.findViewById(R.id.user_layout);
        phone_layout = (RelativeLayout) layout.findViewById(R.id.phone_layout);
        img0 = (ImageView) layout.findViewById(R.id.img0);
        img1 = (ImageView) layout.findViewById(R.id.img1);
        layout0.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.GONE);
        dlg.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                FileUtils.start(Effectstype.Flipv, parent_d);
            }
        });

        parent_d.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        name = (TextView) layout.findViewById(R.id.tc_name);
        setName(null);
        save = (TextView) layout.findViewById(R.id.tc_pop_sure);
        cancle = (TextView) layout.findViewById(R.id.tc_pop_cancle);
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				close();
                setName(null);
                ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
            }

        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save.getText().toString().equals("确定")) {
                    if (loadSendPop == null) {
                        loadSendPop = new CommonLoadSendPop(context, "获取会员信息");
                        loadSendPop.setCloseLis(new Remove() {

                            @Override
                            public void remove() {

                                loadSendPop = null;
                                if (infoCall != null) {
                                    infoCall.cancel();
                                }
                            }
                        });
                        loadSendPop.showSheet(false);
                    }
                    getInfo();
                } else if (save.getText().toString().equals("支付")) {
                    //支付
                    if (loadSendPop == null) {
                        loadSendPop = new CommonLoadSendPop(context, "会员卡支付");
                        loadSendPop.setCloseLis(new Remove() {

                            @Override
                            public void remove() {

                                loadSendPop = null;
                                if (payCall != null) {
                                    payCall.cancel();
                                }
                            }
                        });
                        loadSendPop.showSheet(false);
                    }
                    getPay();
                }

            }
        });
        btn0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ButtonUtil.isFastDoubleClick())
                    return;
                if (!sharedUtils.getBooleanValue("is_card")) {
                    NewDataToast.makeText("请查看配置参数进行确认是否启用");
                    return;
                }
                getPhonePay(1);
            }
        });
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ButtonUtil.isFastDoubleClick())
                    return;
                if (!sharedUtils.getBooleanValue("is_card")) {
                    NewDataToast.makeText("请查看配置参数进行确认是否启用");
                    return;
                }
                getPhonePay(2);
            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ButtonUtil.isFastDoubleClick())
                    return;
                if (!sharedUtils.getBooleanValue("is_card")) {
                    NewDataToast.makeText("请查看配置参数进行确认是否启用");
                    return;
                }
                getPhonePay(3);
            }
        });
        qr_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //取消查询
                calcleBill();
            }
        });
        ud.change(true, true);
        IntentFilter filterO = new IntentFilter();
        filterO.addAction("com.zed.play.start");
        context.registerReceiver(lReceiver, filterO);
    }

    BroadcastReceiver lReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            P.c("播放广告");
            if (intent.getAction().equals("com.zed.play.start")) {
                close();
            }
        }
    };

    class RequestTimerTask extends TimerTask {
        public void run() {
            recLen--;
            Message message = new Message();
            message.what = 66;
            handler.sendMessage(message);
        }
    }

    private Remove remove = new Remove() {
        @Override
        public void remove() {
            stop();

        }
    };

    private void stop() {
        if (loadSendPop != null) {
            loadSendPop.cancle();
            loadSendPop = null;
        }
        if (phoneCall != null) {
            phoneCall.cancel();
            phoneCall = null;
        }
    }

    private RequestCall phoneCall;

    private void getPhonePay(final int PAY) {
        String ip = sharedUtils.getStringValue("IP");
        String sn = sharedUtils.getStringValue("sn");
        String table = sharedUtils.getStringValue("table_code");
        String payMethod = "wxPay";
        final JSONObject obj = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BillNo", billId);
            if (PAY == 2) {
                payMethod = "alipay";
            }
            jsonObject.put("PayMethod", payMethod);
            jsonObject.put("UserName", getUser(sharedUtils.getStringValue("optName")));
            jsonObject.put("DeviceSerialNo", sharedUtils.getStringValue("deviceSerialNo"));
            obj.put("data", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        QrCodePay
        phoneCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_PHONE_PAY_NEW))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(obj.toString()).build();
        phoneCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                P.c("获取二维码失败");
                handler.sendEmptyMessage(3);
//                ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
            }

            @Override
            public void onResponse(String response, int id) {
                P.c("支付:" + response);
                try {
                    JSONObject object = new JSONObject(FileUtils.formatJson(response));
                    if (object.getBoolean("Success")) {
                        JSONObject obj = object.getJSONObject("Data");
                        String codeUrl = obj.getString("WeixinPayUrl");
                        if (PAY == 3) {
                            codeUrl = obj.getString("MemberPayUrl");
                        } else if (PAY == 2) {
                            codeUrl = obj.getString("AliPayUrl");
                        }
                        Message msg = new Message();
                        msg.what = 2;
                        msg.arg1 = PAY;
                        msg.obj = codeUrl;
                        handler.sendMessage(msg);
                    } else {
                        NewDataToast.makeText("请确认餐台账单");
                        stop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void setName(String txt) {
        name.setText(txt != null ? txt : "请选择支付方式");
    }

    private final int SCROOL_WIDTH = 0;
    private int SCROOL_HEIGHT = 0;
    private RequestCall payCall;

    private void getPay() {
        JSONObject obj = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MemberId", card_name.getTag().toString());
            jsonObject.put("Password", card_pa.getText().toString());
            jsonObject.put("BillNo", billId);
            obj.put("data", jsonObject.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }
        //MemberCheckOut
        payCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_PAY_USERINFO))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(obj.toString()).build();
        payCall.execute(payCallback);
    }

    private StringCallback payCallback = new StringCallback() {

        @Override
        public void onError(Call call, Exception e, int id) {
            //关闭
            Log.e(TAG, "onError: " + e.toString());
        }

        @Override
        public void onResponse(String response, int id) {

            if (loadSendPop != null) {
                loadSendPop.cancle();
                loadSendPop = null;
            }
            try {
                JSONObject object = new JSONObject(FileUtils.formatJson(response));
                if (object.getBoolean("Success")) {
                    handler.sendEmptyMessage(1);
                } else {
                    Message msg = new Message();
                    msg.what = 0;
                    String temp = card_yue.getText().toString();
                    if (temp.substring(temp.indexOf(":") + 1).equals("0")) {
                        msg.obj = "客官,您好!\n您的账单已通知收银\n请稍后,收银马上过来";
                    } else {
                        msg.obj = object.getString("Data");
                    }

                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

    };

    private RequestCall infoCall;

    private void getInfo() {
        String ip = sharedUtils.getStringValue("IP");
        JSONObject jsonObject = new JSONObject();
        String cardNo = card_no.getText().toString();
        String cardPass = card_pa.getText().toString();
        try {
            jsonObject.put("Code", cardNo);
            jsonObject.put("Password", cardPass);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        infoCall = OkHttpUtils.get()
                .url(U.VISTER(ip, U.URL_GET_USERINFO)).addParams("data", jsonObject.toString()).build();
//				.mediaType(MediaType.parse("application/json; charset=utf-8"))
//				.content(jsonObject.toString()).build();
        infoCall.execute(infoCallback);
    }

    private StringCallback infoCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            try {
                JSONObject object = new JSONObject(FileUtils.formatJson(response));
                if (object.getBoolean("Success")) {
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        loadSendPop = null;
                    }
                    layout0.setVisibility(View.GONE);
                    layout1.setVisibility(View.VISIBLE);
                    save.setText("支付");
                    JSONObject obj = object.getJSONObject("Data");
                    int yue = obj.getInt("FnumGiveSurplus") + obj.getInt("FnumPrincipalSurplus");
                    String name = obj.getString("FvchrMemberName");
                    card_name.setText("会员名:" + name);
//                    card_name.setTag(obj.getString("UniqueId"));
                    card_name.setTag(obj.getString("Id"));
                    card_yue.setText("余额:" + yue);
                } else {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = object.getString("Data");
                    handler.sendMessage(msg);

                }
            } catch (JSONException e) {

                e.printStackTrace();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {

            handler.sendEmptyMessage(-1);
        }
    };

    private CommonLoadSendPop loadSendPop;

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private String getUser(String optName) {
        String user = null;
        if (optName.length() == 0) {
            // 没有操作员信息
            user = DB.getInstance().getStand(MD5(Common.COMMON_PASS));
        } else {
            user = optName;
        }
        return user;
    }

    private String MD5(String pw) {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = pw.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
