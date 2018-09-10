package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.inter.UD;
import pad.stand.com.haidiyun.www.widget.dialog.Animations.Effectstype;

/**
 * 二维码的弹出框
 *
 * @author Administrator
 */
public class CommonCodePop {
    private Context context;
    private IDialog dlg;
    private TextView name, card_name, card_yue;
    private EditText card_no, card_pa;
    private SharedUtils sharedUtils;
    private TextView save, cancle;
    private Handler dl;
    private String ip, billId, payUrl;

    public CommonCodePop(Context context, Handler dl, String payUrl) {
        this.context = context;
        this.dl = dl;
        this.payUrl = payUrl;
        sharedUtils = new SharedUtils(Common.CONFIG);
        ip = sharedUtils.getStringValue("IP");
        billId = sharedUtils.getStringValue("billId");
    }

    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
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
                        setName("扫码支付");
                    } else if (msg.arg1 == 2) {
                        setName("支付宝扫码支付");
                    }
                    phone_layout.setVisibility(View.VISIBLE);
                    user_layout.setVisibility(View.GONE);
//                    ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
                    String[] temp = (String[]) msg.obj;
                    createImage(temp[0], 1);
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
                    NewDataToast.makeText("获取失败请重新操作");
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Handler billHandler = new Handler();
    private Runnable billRunnable = new Runnable() {
        @Override
        public void run() {
            checkBill();
        }
    };
    RequestCall billCall = null;

    private void checkBill() {

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
                billHandler.postDelayed(billRunnable, 5000);
            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if (jsonObject.getBoolean("Success")) {
                        //

                        handler.sendEmptyMessage(4);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                billHandler.postDelayed(billRunnable, 5000);
            }
        });
    }

    // 生成QR图
    private void createImage(String formatValue, int flag) {
        try {
            // 需要引入core包
            int QR_WIDTH = 300;
            int QR_HEIGHT = 300;
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
            if (flag == 1) {
                iv_code.setImageBitmap(temp);
            } else {
                iv_code_member.setImageBitmap(temp);
            }

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
    private ImageView qr, iv_code, iv_code_member;
    private View parent_d;
    private LinearLayout layout0, layout1, user_layout;
    private RelativeLayout phone_layout;
    private UDlayout ud_layout;
    private LinearLayout btn0, btn1, btn2;

    public void showSheet() {
        SCROOL_HEIGHT = -dip2px(300);
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.user_pop_view_code, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        ud_layout = (UDlayout) layout.findViewById(R.id.ud_layout);
        ud_layout.setUD(ud);
        qr = (ImageView) layout.findViewById(R.id.qr);
        parent_d = layout.findViewById(R.id.parent_d);
        card_no = (EditText) layout.findViewById(R.id.card_no);
        card_pa = (EditText) layout.findViewById(R.id.card_pa);
        layout0 = (LinearLayout) layout.findViewById(R.id.layout0);
        layout1 = (LinearLayout) layout.findViewById(R.id.layout1);
        card_name = (TextView) layout.findViewById(R.id.card_name);
        card_yue = (TextView) layout.findViewById(R.id.card_yue);
        btn0 = (LinearLayout) layout.findViewById(R.id.btn0);
        btn1 = (LinearLayout) layout.findViewById(R.id.btn1);
        btn2 = (LinearLayout) layout.findViewById(R.id.btn2);
        iv_code = (ImageView) layout.findViewById(R.id.iv_code);
        iv_code_member = (ImageView) layout.findViewById(R.id.iv_code_member);
        user_layout = (LinearLayout) layout.findViewById(R.id.user_layout);
        phone_layout = (RelativeLayout) layout.findViewById(R.id.phone_layout);
        layout0.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.GONE);
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {
                FileUtils.start(Effectstype.Flipv, parent_d);
            }
        });
        name = (TextView) layout.findViewById(R.id.tc_name);
        save = (TextView) layout.findViewById(R.id.tc_pop_sure);
        cancle = (TextView) layout.findViewById(R.id.tc_pop_cancle);
        setName("扫码支付");
        if (!payUrl.contains("$")) {
            NewDataToast.makeText("请确认餐台账单");
            return;
        }
        String[] url = payUrl.split("\\$");
        String pay1 = url[0];
        String pay2 = url[1];
        createImage(pay1, 1);
        createImage(pay2, 2);
        ud.change(true, true);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
    }

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

    private void setName(String txt) {
        name.setText(txt != null ? txt : "扫码支付");
    }

    private final int SCROOL_WIDTH = 0;
    private int SCROOL_HEIGHT = 0;

    private CommonLoadSendPop loadSendPop;

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
            //关闭定时器
        }

    }
}

