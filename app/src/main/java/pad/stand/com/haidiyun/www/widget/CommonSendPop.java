package pad.stand.com.haidiyun.www.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.LoadBuy;

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
    private CommonCodePop userPop;
    private SharedUtils utils;
    //操作员名字
    private String optName;
    private Handler orderFinishHandler = new Handler();
    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 5:

                    for (int i = 0; i < dishTableBeans.size(); i++) {
                        if (Common.guSttxKeys.containsKey(dishTableBeans.get(i).getCode())) {
                            String num = Common.guSttxKeys.get(dishTableBeans.get(i).getCode());
                            if (num.equals("0.0")) {

                                NewDataToast.makeTextL("亲，" + dishTableBeans.get(i).getName() + "菜品已售罄，欢迎下次品尝", 2000);
                                close();
                                return;
                            }
                        }
                    }
                    //下单
                    if (utils.getBooleanValue("is_limit") && utils.getStringValue("billId").length() != 0) {
                        //进行单据限额查询
                        loadNet(utils);
                    } else {
                        if (utils.getBooleanValue("is_prepay")) {
                            sendPre();
                        } else {
                            send();
                        }
                    }
                    break;
                case 2:
                    new Thread() {
                        public void run() {
                            //进行数量计算
                            boolean itp = false;//false证明没有问题，正常通过,出现一个不正常就断开

                            for (int i = 0; i < dishTableBeans.size(); i++) {
                                DishTableBean bean = dishTableBeans.get(i);
                                String key = bean.getCode();
                                if (billMap.containsKey(key)) {
                                    //这个菜已经在单据中存在了,那么进行超额计算
                                    double count = billMap.get(key) + bean.getCount();//订单数量和现在的数据之和
                                    P.c("菜品总量" + count);
                                    int result = DB.getInstance().canAddByBill(count, bean.getCode());
                                    if (result == -1) {
                                        //pass
                                        DB.getInstance().changelt(0, bean.getCode());
                                    } else {
                                        itp = true;
                                        //固定给一个值，
                                        DB.getInstance().changelt(1, bean.getCode());
                                    }
                                }
                            }
                            if (itp) {
                                //有问题不进行下单操作
                                handler.sendEmptyMessage(4);
                            } else {
                                handler.sendEmptyMessage(3);
                            }

                        }

                        ;
                    }.start();
                    break;
                case 3:
                    send();
                    break;
                case 4:
                    NewDataToast.makeTextL("有菜品超出限额", 1500);
                    loadBuy.lt();
                    close();
                    break;
                case 1:
//                    Intent intent = new Intent();
//                    intent.setAction(Common.SERVICE_ACTION);
//                    intent.setAction("pad.stand.com.haidiyun.www.float");
//                    intent.putExtra("open_table", "");
//                    context.startService(intent);
                    Intent intent = new Intent();
                    intent.setPackage(BaseApplication.packgeName);
                    intent.setAction(Common.SERVICE_ACTION);
                    intent.putExtra("open_table", "");
                    context.startService(intent);
                    utils.clear("order_time");
                    DB.getInstance().clear();
                    NewDataToast.makeTextL(context.getResources().getString(R.string.ordersuccess), 2000);
                    loadBuy.success(null);
                    close();
                    break;
                case 100:
                    //弹二维码
                    close();
                    String payUrl = (String) msg.obj;
                    userPop = new CommonCodePop(context, new Handler(), payUrl);
                    userPop.showSheet();
                    //查订单信息
                    orderFinishHandler.post(orderFinishRunnable);
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
                case -4:
                    NewDataToast.makeText((String) msg.obj);
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

    private String order_Time, sn;
    private int person;
    private boolean isCD;
    public CommonSendPop(Context context, String msg, ArrayList<DishTableBean> dishTableBeans, LoadBuy loadBuy, int person, String optName,boolean isCD) {
        this.context = context;
        this.msg = msg;
        this.dishTableBeans = dishTableBeans;
        this.loadBuy = loadBuy;
        this.person = person;
        this.optName = optName;
        this.isCD = isCD;
        utils = new SharedUtils(Common.CONFIG);
        if (utils.getStringValue("order_time").length() == 0) {
            utils.setStringValue("order_time", String.valueOf(System.currentTimeMillis()));
        }
        sn = utils.getStringValue("sn");
        order_Time = utils.getStringValue("order_time");
    }

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.loading, null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText(msg);
        load_img = (ImageView) layout.findViewById(R.id.load_img);
        load_close = (ImageView) layout.findViewById(R.id.load_close);
        if (utils.getStringValue("IP").length() != 0) {
            gu(utils.getStringValue("IP"));
        }
        load_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
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

    /**
     * 沽清
     */
    private void gu(String ip) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "[{\"TabName\":\"NTORestSaleOutMenu\",\"Timestamp\":0}]");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        requestCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_GUQING))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        requestCall.execute(guCallback);
        P.c("提交沽清查询");
    }

    private StringCallback guCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
//				P.c("沽清结果"+response);
            new Thread() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(
                                FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            Common.guSttxKeys.clear();
                            JSONArray array = jsonObject.getJSONArray("Data");
                            /*JSONArray array = json
                                    .getJSONArray("NTORestSaleOutMenu");
                            int len = array.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Common.guKeys.put(obj.getString("MenuCode"), 0);
                            }*/
                            int len = array.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String qty = obj.getString("LeftQty");
                                Common.guSttxKeys.put(obj.getString("MenuCode"), qty);
                            }
                        }
                        handler.sendEmptyMessage(5);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ;
            }.start();


        }

        @Override
        public void onError(Call call, Exception e, int id) {
            handler.sendEmptyMessage(-5);
        }

    };


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
    private RequestCall orderFinishRequestCall;

    //后付费
    private void send() {
        String ip = utils.getStringValue("IP");
        String tableCode = utils.getStringValue("table_code");
        int person = utils.getIntValue("person");
        StringBuilder builder = new StringBuilder();
        int len = dishTableBeans.size();
        StringBuilder dish = new StringBuilder();
        for (int i = 0; i < len; i++) {
            DishTableBean tableBean = dishTableBeans.get(i);
//            String sss = tableBean.toString();
            String cook_codes = tableBean.getCook_codes();
            String codes = tableBean.getTc_cook_codes();
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
                cook_codes = codes;
            } else {
//                00302,00303
//                00302|;00301|;
                StringBuilder finalStr = new StringBuilder();
                String detail = tableBean.getSuitMenuDetail();
                String[] detailStr = detail.split(",");
                for (int j = 0; j < detail.split(",").length; j++) {
                    finalStr.append(detailStr[j] + "|" + ";");
                }
                String cook_codes_final = finalStr.toString();
//                cook_codes = tableBean.getSuitMenuDetail();
                cook_codes = cook_codes_final;
            }
            double price = 0;
            if (utils.getStringValue("tableType").equals("Price1")) {
                price = tableBean.getPrice1();
            } else if (utils.getStringValue("tableType").equals("Price2")) {
                price = tableBean.getPrice2();
            } else {
                price = tableBean.getPrice();
            }
            dish.append("{\"MenuCode\":\"" + tableBean.getCode() + "\",\"MenuName\":\"" + tableBean.getName() + "\",\"MenuEName\":\"" + tableBean.getNameEn() + "\",\"SuitMenuDetail\":\"" + cook_codes + "\",\"Number\":" + tableBean.getCount() + ",\"Price\":" + price + ",\"Cooks\":\"" + tableBean.getCook_codes() + "\",\"Remark\":\"\",\"MenuFlag\":" + tableBean.getFlag()+ ",\"Status\":"+(tableBean.isIsjj()?1:0)+"}");
            if (i != len - 1) {
                dish.append(",");
            }
        }
        //老的接口下单
        String user = utils.getStringValue("user").equals("") ? "Admin" : utils.getStringValue("user");
        if (utils.getBooleanValue("is_waite") && "".equals(utils.getStringValue("user"))) {
            user = utils.getStringValue("optName");
        }
        builder.append("{\"BillNo\":\"" + utils.getStringValue("billId") + "\",\"DeviceSerialNo\":\"" + utils.getStringValue("deviceSerialNo") + "\",\"TableNo\":\"" + tableCode + "\",\"GstCount\":" + person + ",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\"" + user + "\",\"SendToKitchen\":"+isCD+",\"List\":[" + dish.toString() + "]}");
        P.c("后付费下单===" + builder.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", builder.toString());
            requestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_SEND))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            requestCall.execute(sendCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    //先付费
    private void sendPre() {
        String ip = utils.getStringValue("IP");
        String tableCode = utils.getStringValue("table_code");
        int person = utils.getIntValue("person");
        StringBuilder builder = new StringBuilder();
        int len = dishTableBeans.size();
        StringBuilder dish = new StringBuilder();
        for (int i = 0; i < len; i++) {
            DishTableBean tableBean = dishTableBeans.get(i);
            String cook_codes = tableBean.getCook_codes();
            String codes = tableBean.getTc_cook_codes();
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
                cook_codes = codes;
            } else {
//                cook_codes = tableBean.getSuitMenuDetail();
                StringBuilder finalStr = new StringBuilder();
                String detail = tableBean.getSuitMenuDetail();
                String[] detailStr = detail.split(",");
                for (int j = 0; j < detail.split(",").length; j++) {
                    finalStr.append(detailStr[j] + "|" + ";");
                }
                String cook_codes_final = finalStr.toString();
//                cook_codes = tableBean.getSuitMenuDetail();
                cook_codes = cook_codes_final;
            }
            double price = 0;
            if (utils.getStringValue("tableType").equals("Price1")) {
                price = tableBean.getPrice1();
            } else if (utils.getStringValue("tableType").equals("Price2")) {
                price = tableBean.getPrice2();
            } else {
                price = tableBean.getPrice();
            }
            dish.append("{\"MenuCode\":\"" + tableBean.getCode() + "\",\"MenuName\":\"" + tableBean.getName() + "\",\"SuitMenuDetail\":\"" + cook_codes + "\",\"Number\":" + tableBean.getCount() + ",\"Price\":" + price + ",\"Cooks\":\"" + tableBean.getCook_codes() + "\",\"Remark\":\"\",\"MenuFlag\":" + tableBean.getFlag() + ",\"Status\":"+(tableBean.isIsjj()?1:0)+"}");
            if (i != len - 1) {
                dish.append(",");
            }
        }
        String PAD = "PAD";
        String user = utils.getStringValue("user").equals("") ? "Admin" : utils.getStringValue("user");
        if (utils.getBooleanValue("is_waite") && "".equals(utils.getStringValue("user"))) {
            user = utils.getStringValue("optName");
        }
        builder.append("{\"TableNo\":\"" + tableCode + "\",\"BillNo\":\"" + utils.getStringValue("billId") + "\",\"ClientType\":\"" + PAD + "\",\"DeviceSerialNo\":\"" + utils.getStringValue("deviceSerialNo") + "\",\"GstCount\":" + person + ",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\"" + user + "\",\"SendToKitchen\":"+isCD+",\"List\":[" + dish.toString() + "]}");
        P.c("先付费下单===" + builder.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", builder.toString());
            requestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_PRE_SEND))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            requestCall.execute(sendPreCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    private StringCallback sendPreCallback = new StringCallback() {
        @Override
        public void onResponse(final String response, int id) {
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                FileUtils.formatJson(response), "下菜结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "下菜结果");
                    }
                }
            }.start();

            try {
//                {"Success":false,"Data":"当前餐台还有未付款的菜品,请完成付款后再点单","Result":""}
                P.c("先付费下单返回data===" + response);
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    //成功状态
                    JSONObject object = jsonObject.getJSONObject("Data");
                    String payUrl = object.getString("WeixinPayUrl");
                    String memberPayUrl = object.getString("MemberPayUrl");
                    utils.setStringValue("billId", object.getString("BillNo"));
                    Message msg = new Message();
                    msg.what = 100;
                    msg.obj = payUrl + "$" + memberPayUrl;
                    utils.setStringValue("payUrl", payUrl + "$" + memberPayUrl);
                    handler.sendMessage(msg);
                } else {
                    String msgError = jsonObject.getString("Data");
                    if ("当前餐台还有未付款的菜品,请完成付款后再点单".equals(msgError)) {
                        Message msg = new Message();
                        msg.what = 100;
                        msg.obj = utils.getStringValue("payUrl");
                        handler.sendMessage(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            P.c("下单失败------");
            handler.sendEmptyMessage(-2);
        }
    };
    private StringCallback sendCallback = new StringCallback() {
        @Override
        public void onResponse(final String response, int id) {
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                FileUtils.formatJson(response), "下菜结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "下菜结果");
                    }
                }
            }.start();

            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    P.c("下单返回成功");
                    //成功状态
                    String billId = jsonObject.getString("Data");
                    utils.setStringValue("billId", billId);
                    handler.sendEmptyMessage(1);
                } else {
                    P.c("下单返回msg");
                    Message msg = new Message();
                    msg.what = -4;
                    msg.obj = jsonObject.getString("Data");
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                P.c("下单返回异常");
                e.printStackTrace();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            P.c("下单返回失败------");
            handler.sendEmptyMessage(-2);
        }
    };

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            if (requestCall != null) {
                requestCall.cancel();
            }
            dlg.cancel();
            dlg = null;
        }
    }

    private RequestCall billCall;
    private volatile Map<String, Integer> billMap = new HashMap<String, Integer>();

    /**
     * 加载网络中的菜单
     */
    private boolean loadNet(SharedUtils utils) {
        String ip = utils.getStringValue("IP");
        String billId = utils.getStringValue("billId");
        if (billId.length() == 0) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", billId);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        billCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_GET_ORDER))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString())
                .build();
        billCall.execute(netCallback);
        return true;
    }


    private StringCallback netCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            try {
                P.c(FileUtils.formatJson(response));
            } catch (JSONException e1) {

                e1.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    //成功
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    int len = jsonArray.length();
                    if (len != 0) {
                        billMap.clear();
                    }
                    for (int i = 0; i < len; i++) {
                        //已下单菜品
                        JSONObject object = jsonArray.getJSONObject(i);
                        String key = object.getString("MenuCode");
                        if (billMap.containsKey(key)) {
                            billMap.put(key, billMap.get(key) + object.getInt("Number"));
                        } else {
                            billMap.put(key, object.getInt("Number"));
                        }
                    }
                    handler.sendEmptyMessage(2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
        }
    };
    private int tableDelay = 3000;
    private Runnable orderFinishRunnable = new Runnable() {
        public void run() {
            findOrder();
            orderFinishHandler.postDelayed(this, tableDelay);
        }
    };

    /**
     * 查找先付费订单数据
     */
    private void findOrder() {
        JSONObject jsonObject = new JSONObject();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("{\"BillNo\":\"" + utils.getStringValue("billId") + "\",\"DeviceSerialNo\":\"" + sn + "\"}");
            jsonObject.put("data", builder.toString());
            String ip = utils.getStringValue("IP");
            cal();
            orderFinishRequestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_FINISH_STATUS))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            if (orderFinishRequestCall != null) {
                orderFinishRequestCall.execute(orderFinishCallback);
            }

            P.c(TimeUtil.getTime(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //定时查是否扫码支付
    private StringCallback orderFinishCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                FileUtils.formatJson(response), "先付费模式结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "先付费模式结果");
                    }
                }
            }.start();
            new Thread() {
                public void run() {
                    try {
//                        {"d":"{\"Success\":true,\"Data\":{\"PaySuccess\":false},\"Result\":null}"}
//                        {"d":"{\"Success\":true,\"Data\":{\"PaySuccess\":true},\"Result\":null}"}
                        P.c("先付费查支付状态" + response);
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            //成功
                            JSONObject object = jsonObject.getJSONObject("Data");
                            if (object.getBoolean("PaySuccess")) {
                                //支付成功
                                userPop.close();
                                utils.clear("payUrl");
                                orderFinishHandler.removeCallbacks(orderFinishRunnable);
                                handler.sendEmptyMessage(1);
                            } else {
                                //继续定时查扫码支付
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            cal();
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
        }
    };

    private void cal() {
        if (orderFinishRequestCall != null) {
            orderFinishRequestCall.cancel();
            orderFinishRequestCall = null;
        }
//        关闭定时器
    }
}
