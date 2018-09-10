package pad.com.haidiyun.www.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.LoadBuy;

/**
 * 发送下单
 *
 * @author Administrator
 */
public class CommonSendPop {
    private Context context;
    private TextView load_tv;
    private IDialog dlg;
    private String msg;
    private ImageView load_img, load_close;
    private ArrayList<DishTableBean> dishTableBeans;
    private LoadBuy loadBuy;
    private SharedUtils utils;
    //操作员名字
    private String optName;
    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DB.getInstance().clear();
                    NewDataToast.makeTextL("恭喜您，您已成功下单!", 1500);
                    loadBuy.success(null);
                    close();
                    break;
                case 0:
//				NewDataToast.makeTextL(context,(String)msg.obj,3000);
                    DB.getInstance().clear();
                    NewDataToast.makeTextL((String) msg.obj, 1500);
                    loadBuy.success(null);
                    close();
                    break;
                case -1:
                    NewDataToast.makeText("请检查与主机的连接配置");
                    close();
                    break;
                case -2:
                    NewDataToast.makeText("下单失败,请检查WIFI环境");
                    close();
                    break;
                case -3:
                    if (requestCall != null) {
                        requestCall.cancel();
                    }
                    NewDataToast.makeText("取消下单");
                    cancle();
                    break;
                case -4:
                    NewDataToast.makeText((String) msg.obj);
                    close();
                    break;
                case -5:
                    NewDataToast.makeText("获取响应数据超时");
                    close();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    /**
     * 连接socket
     */

    private long order_Time;
    private int person;

    public CommonSendPop(Context context, String msg, ArrayList<DishTableBean> dishTableBeans, LoadBuy loadBuy, int person, String optName) {
        this.context = context;
        this.msg = msg;
        this.dishTableBeans = dishTableBeans;
        this.loadBuy = loadBuy;
        this.person = person;
        this.optName = optName;
        utils = new SharedUtils(Common.CONFIG);
        order_Time = System.currentTimeMillis();
    }

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.flip_loading, null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText(msg);
        load_img = (ImageView) layout.findViewById(R.id.load_img);
        load_close = (ImageView) layout.findViewById(R.id.load_close);

        //下单
        send();

        load_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                handler.sendEmptyMessage(-3);
            }
        });
        // 动画
        Animation operatingAnim = AnimationUtils.loadAnimation(context,
                R.anim.anim_load);
        // 匀速旋转
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        load_img.startAnimation(operatingAnim);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private void cancle() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }

    /**
     * 下单
     */
    private RequestCall requestCall;

    private void send() {
        String ip = utils.getStringValue("IP");
        String tableCode = utils.getStringValue("table_code");
        int person = utils.getIntValue("person");
        StringBuilder builder = new StringBuilder();
        int len = dishTableBeans.size();
        StringBuilder dish = new StringBuilder();
        for (int i = 0; i < len; i++) {
            DishTableBean tableBean = dishTableBeans.get(i);
//			tc_cook_codes='02502|0572   ;    02617|    ;    00306|',
//            02502|001,002;0573|001,002
            String cook_codes = tableBean.getCook_codes();
            String codes = tableBean.getTc_cook_codes();
//            02502|0513,0573,0572;   02617|   ;      00306|;
            if (codes != null) {
                StringBuilder s = new StringBuilder("");
                String[] codeAll = codes.split(";");
                for (int j = 0; j < codeAll.length; j++) {
                    String[] codeFinal = codeAll[j].split("\\|");
                    if (codeFinal.length > 1) {
                        s.append(codeFinal[0] + "|");
                        String[] codeDes = codeFinal[1].split(",");
                        for (int k = 0; k < codeDes.length; k++) {
                            if (k != codeDes.length - 1) {
                                s.append(codeDes[k] + ",");
                            } else {
                                s.append(codeDes[k] + ";");
                            }
                        }
                    }
                }
//                cook_codes = s.toString().substring(0, s.toString().length() - 1);
//                cook_codes = s.toString();
                cook_codes = codes;
            } else {
                cook_codes = tableBean.getSuitMenuDetail();
            }
//            01407|0571,0572;02614|0572,0509
            dish.append("{\"MenuCode\":\"" + tableBean.getCode() + "\",\"MenuName\":\"" + tableBean.getName() + "\",\"SuitMenuDetail\":\"" + cook_codes + "\",\"Number\":" + tableBean.getCount() + ",\"Price\":" + tableBean.getPrice() + ",\"Cooks\":\"" + tableBean.getCook_codes() + "\",\"Remark\":\"\",\"MenuFlag\":" + tableBean.getFlag() + "}");
            if (i != len - 1) {
                dish.append(",");
            }
        }

        builder.append("{\"TableNo\":\"" + tableCode + "\",\"GstCount\":" + person+",\"BillNo\":\"" + utils.getStringValue("billId")+"\",\"DeviceSerialNo\":\"" + Common.SER+ "\",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\"" + optName + "\",\"List\":[" + dish.toString() + "]}");
        P.c("下单===" + builder.toString());
        JSONObject jsonObject = new JSONObject();
        //http://192.168.1.147/DataService.svc/DownloadData
        try {
            jsonObject.put("data", builder.toString());
            requestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_SEND))
//			        .addParams("data", "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            requestCall.execute(sendCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    private StringCallback sendCallback = new StringCallback() {
        @Override
        public void onResponse(String response, int id) {
            P.c(response);
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    //成功状态
                    handler.sendEmptyMessage(1);
                } else {
                    Message msg = new Message();
                    msg.what = -4;
                    msg.obj = jsonObject.getString("Data");
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            // TODO Auto-generated method stub
            P.c(e.getMessage());
            handler.sendEmptyMessage(-2);
        }
    };

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }

}
