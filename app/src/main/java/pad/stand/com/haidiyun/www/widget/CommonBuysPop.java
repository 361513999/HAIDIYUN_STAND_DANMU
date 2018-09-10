package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zc.changeskin.SkinManager;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.BuysAdapter;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.bean.TableBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.BeDish;
import pad.stand.com.haidiyun.www.inter.BuyClick;
import pad.stand.com.haidiyun.www.inter.DishChange;
import pad.stand.com.haidiyun.www.inter.LoadBuy;
import pad.stand.com.haidiyun.www.inter.NumSel;
import pad.stand.com.haidiyun.www.inter.ReasonI;
import pad.stand.com.haidiyun.www.inter.Remove;
import pad.stand.com.haidiyun.www.inter.SelectPerson;
import pad.stand.com.haidiyun.www.inter.SelectTable;
import pad.stand.com.haidiyun.www.inter.Tz;
import pad.stand.com.haidiyun.www.inter.WaterConfirm;
import pad.stand.com.haidiyun.www.ui.CardValActivity;
import pad.stand.com.haidiyun.www.ui.PriceActivity;
import pad.stand.com.haidiyun.www.utils.ButtonUtil;

public class CommonBuysPop {
    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case -66:
                    NewDataToast.makeText(context.getResources().getString(R.string.hasorderyet0));
                    break;
                case -8:
                    P.c("接收到了。。。。。。。");

                    Intent intent = new Intent();
                    intent.setAction(Common.SERVICE_ACTION);
                    intent.putExtra("free_table", "");
                    intent.setPackage(BaseApplication.packgeName);
                    context.startService(intent);

                    close();
                    break;
                case -7:
                    NewDataToast.makeText("超出限额");
                    break;
                case 1:

                    new Thread() {
                        public void run() {
                            dishTableBeans.clear();
                            dishTableBeans = DB.getInstance().getTableBeans();
                            handler.sendEmptyMessage(-1);
                        }

                        ;
                    }.start();
                    break;
                case -1:

                    if (dishTableBeans.size() == 0 && sharedUtils.getStringValue("billId").length() != 0) {
                        h_dl_tab_order.post(new Runnable() {
                            @Override
                            public void run() {
                                h_dl_tab_order.performClick();
                            }
                        });
                        return;
                    }

                    buysAdapter.updata(dishTableBeans, false);
                    handler.sendEmptyMessage(2);
                    break;
                case 0:
                /*Collections.sort(billTableBeans, new Comparator<DishTableBean>() {
                    @Override
					public int compare(DishTableBean lhs, DishTableBean rhs) {
						int y = lhs.getI()-rhs.getI();
						int x = lhs.getCode().compareTo(rhs.getCode());
						if(x==0){
							 return y;
						 }
							return x;
					}
				});*/

			/*	billTableBeans.sort((lhs, rhs) -> {
                    if (lhs.getName().equals(rhs.getName())) {
						return lhs.getAge() - rhs.getAge();
					} else {
						return lhs.getName().compareTo(rhs.getName());
					}
				});*/
                    //显示折扣
                    if (sharedUtils.getBooleanValue("canShowDiscount")) {
                        showDiscount();
                    }
                    buysAdapter.updata(billTableBeans, true);
                    handler.sendEmptyMessage(3);
                    break;
                case 12:
                    total_discount.setVisibility(View.VISIBLE);
                    total_discount.setText("折扣价 【已下单】:" + msg.obj);
                    break;
                case 2:
                    total_discount.setVisibility(View.GONE);
                    total.setText("总价【未下单】:" + process());
                    break;
                case 3:
                    total.setText("总价【已下单】:" + processBill());
                    break;
                case 5:
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                    }
                    NewDataToast.makeTextL((String) msg.obj, 2000);
                    break;
                case 6:
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                    }
                    break;
                case 11:

                    if (loginBuy == null) {
                        buyClick.person();
                    } else {
                        if (sharedUtils.getBooleanValue("is_waite")) {
                            buyClick.person(loginBuy,
                                    getUser(sharedUtils.getStringValue("optName")));
                        } else {
                            buyClick.person(loginBuy, getUser(""));
                        }

                    }

                    // 取消开台，直接点餐
                /*
                 * if (System.currentTimeMillis() - lastClick <= 2000) { return;
				 * } lastClick = System.currentTimeMillis(); // TODO
				 * Auto-generated method stub try{ if(loadSendPop==null){
				 * loadSendPop = new CommonLoadSendPop(context, "正在为您开台");
				 * loadSendPop.showSheet(false); } //二次桌台 //find();
				 * load(sharedUtils.getIntValue("person"));
				 * }catch(NumberFormatException e){
				 * 
				 * }
				 */
                    break;
                case -11:
                    int person = msg.arg1;
//                    table_code
//                            .setText(sharedUtils.getStringValue("table_name"));
                    table_people.setText(String.valueOf(person));
                    // 刷新菜单
//                    initMenu();
                    if (person == 0) {
                        Intent intent1 = new Intent();
                        intent1.setAction(Common.SERVICE_ACTION);
                        intent1.putExtra("free_table", "");
                        context.startService(intent1);
                        selectPeople();
                    } else {
                        Intent intent2 = new Intent();
                        intent2.setAction(Common.SERVICE_ACTION);
                        intent2.putExtra("open_table", "");
                        context.startService(intent2);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 计算已点菜
     *
     * @return
     */
    private double processBill() {
        // 计算总价
        int len = billTableBeans.size();
        double total = 0;
        for (int i = 0; i < len; i++) {
            DishTableBean obj = billTableBeans.get(i);
            total += (obj.getCount() * obj.getPrice());
        }
        return format(total);
    }

    /**
     * 计算未点餐
     *
     * @return
     */
    private double process() {
        // 计算总价
        int len = dishTableBeans.size();
        double total = 0;
        for (int i = 0; i < len; i++) {
            DishTableBean obj = dishTableBeans.get(i);
            total += (obj.getCount() * obj.getPrice());
        }
        return format(total);
    }

    private Context context;
    private BuyClick buyClick = new BuyClick() {
        public boolean check(final DishTableBean obj) {
            if (Common.guSttxKeys.containsKey(obj.getCode())) {
                String num = Common.guSttxKeys.get(obj.getCode());
                if (!num.equals("0.0")) {
                    if (obj.getCount() + 1 > (int) Double.parseDouble(num)) {
                        NewDataToast.makeText("最大剩余" + num + "份");
                        return false;
                    }
                }

            }
            return true;
        }

        @Override
        public void add(DishTableBean obj) {
            if (check(obj)) {
                DB.getInstance().changeNum(obj.getCount() + 1, obj.getI(),
                        obj.getCode(), "+", handler);
                handler.sendEmptyMessage(1);
            }
        }

        @Override
        public void delete(DishTableBean obj) {
            double limit = obj.getOrderMinLimit();
            if (obj.getCount() - 1 <= 0 && obj.getCount() > 0) {
                buyClick.remove(obj);
            } else {
                if (obj.getCount() > limit) {
                    DB.getInstance().changeNum(obj.getCount() - 1, obj.getI(),
                            obj.getCode(), "-", handler);
                    handler.sendEmptyMessage(1);
                } else {
                    NewDataToast.makeText(context.getResources().getString(R.string.minorder) + limit);
                }
            }
        }

        @Override
        public void remove(DishTableBean obj) {

            CommonDeletePop deletePop = new CommonDeletePop(context, obj,
                    remove, loadBuy);
            deletePop.showSheet();
        }

        @Override
        public void tz(DishTableBean obj, int INDEX) {
            if (INDEX == 0) {
                tz.tuicai(obj);
            } else if (INDEX == 1) {
                tz.zengsong(obj);
            }
            close();
        }

        @Override
        public void res(DishTableBean obj) {

            // 多口味选择
            CommonResPop resPop = new CommonResPop(context, reasonI, obj, null);
            resPop.showSheet();
        }

        @Override
        public void person() {

            // 人数选择
            person = sharedUtils.getIntValue("person");
            // 在这里进行真正的下单
            downDish();
            P.c("订单号" + sharedUtils.getStringValue("billId"));
        }

        @Override
        public void person(LoadBuy buy, String optName) {

            person = sharedUtils.getIntValue("person");
            loginBuy.success(optName);
        }

        @Override
        public void num(DishTableBean obj) {
            //改数量
            CommonNum commonNum = new CommonNum(context, numSel, obj, obj.getOrderMinLimit());
            commonNum.showSheet();
        }

        //修改价格
        @Override
        public void price(DishTableBean obj) {
            //改价
            P.c("改价");
            Intent intent = new Intent(context, PriceActivity.class);
            intent.putExtra("obj", obj);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    };
    private NumSel numSel = new NumSel() {
        @Override
        public void change(String o, Object ob) {
            DishTableBean obj = (DishTableBean) ob;
            DB.getInstance().changeNum(Double.parseDouble(o), obj.getI(),
                    obj.getCode(), "-", handler);
            handler.sendEmptyMessage(1);
        }

        @Override
        public void changeWeigh(String o, Object object, ArrayList<ReasonBean> resons, String remark, T_Image im) {

        }

    };

    //加菜操作
    private void downDish() {
        if (sharedUtils.getBooleanValue("is_waite")) {
            // 服务员确认操作


            /*if (sharedUtils.getBooleanValue("is_dish")) {
                if (sharedUtils.getStringValue("optName").length() != 0) {
                    sendNoTip(loginBuy, null);
                }
            } else {
                waterConfirm.confirm(loginBuy);
            }*/
            if (sharedUtils.getBooleanValue("is_dish")) {
                P.c("服务员确认+加菜确认-->再次登录");
//                waterConfirm.confirm(loginBuy);
                String user = sharedUtils.getStringValue("user");
                if(user.length()==0){
                    waterConfirm.confirm(loginBuy);
                }else{
                    send();
                }
            } else {
                if ("".equals(sharedUtils.getStringValue("user"))) {
                    P.c("服务员确认+无需加菜确认-->登录");
                    waterConfirm.confirm(loginBuy);
                } else {
                    P.c("服务员确认+无需加菜确认-->不登录");
                    sendNoTip(loginBuy, null);
                }
            }

        } else {
            // 顾客自助点餐模式
            // tipPop = new CommonTipPop(context, sureBuy);
            P.c("顾客自助点餐");
            sendNoTip(null, handler);

        }
    }

    /**
     * 快速下单
     */
    private void sendNoTip(LoadBuy loadBuy, Handler handler) {
        if (sharedUtils.getBooleanValue("is_tip")) {

            tipPop = new CommonTipPop(context, loadBuy, handler);
            tipPop.showSheet();
        } else {

            NoTip(loadBuy, handler);
        }
    }

    private void NoTip(LoadBuy loadBuy, Handler fHandler) {
        if (fHandler == null) {
            P.c("这是什么意思");
            String optName = sharedUtils.getStringValue("optName");
            loadBuy.success(optName.length() == 0 ? "" : optName);
        } else {
            //真的就关闭
            fHandler.sendEmptyMessage(11);
        }
    }

    private int person = -1;
    // 选择规格
    public ReasonI reasonI = new ReasonI() {
        @Override
        public void select(ArrayList<ReasonBean> beans, DishTableBean bean, String numView) {

            DB.getInstance().updateDishTable(beans, bean, numView,handler);
            handler.sendEmptyMessage(1);
        }

        @Override
        public void init(DishTableBean bean, String numView) {

            DB.getInstance().updateDishInit(bean);
            handler.sendEmptyMessage(1);
        }

        // 下面是在菜品添加界面上加口味
        @Override
        public void insert(ArrayList<ReasonBean> beans, FoodsBean bean, String numView) {


        }

        @Override
        public void select(final ArrayList<ReasonBean> beans, final DishTableBean bean, final HashMap<String, ArrayList<ReasonBean>> tcBeans, final HashMap<String, String> tcListStr, final int position, final int tcActualNum) {
            new Thread() {
                public void run() {//加入到菜篮子更新表
//                    01407|+8元配罗宋汤;00304|;00603|;00105|;02613|;02614|;
                    DB.getInstance().updateDishTable(beans, bean, tcBeans, tcListStr, position, tcActualNum);
                    handler.sendEmptyMessage(1);//清空
                }
            }.start();
        }

        @Override
        public void init(FoodsBean bean, String numView) {


        }
    };
    // 买单成功
    private LoadBuy loadBuy = new LoadBuy() {

        @Override
        public void success(String optName) {
            //下单成功
            dishChange.change();
            if (cancelListener != null) {
                cancelListener.onDismiss(dlg);
            }
            close();
        }

        @Override
        public void waiter(String optName) {

        }

        @Override
        public void lt() {

            // 超出限额
            handler.sendEmptyMessage(1);
        }
    };
    // 确认下单接口
    /**
     * 服务员再次确认成功
     */
    private LoadBuy loginBuy = new LoadBuy() {

        @Override
        public void success(String optName) {

            // 这里进行登录成功正式下单操作
            int person = sharedUtils.getIntValue("person");
            P.c("person" + person);
            if (person > 0) {
                sendPop = new CommonSendPop(context, "正在下单", dishTableBeans,
                        loadBuy, person, getUser(optName),isCD);
                sendPop.showSheet();
                sendPop = null;
                // close();
            } else {
                NewDataToast.makeText("请选择桌台号和人数");
            }
        }

        @Override
        public void waiter(String optName) {
            P.c("服务员人数选择");
            // personPop =new CommonPersonPop(context, buyClick,loginBuy,
            // optName);
            // personPop.showSheet();
            loginBuy.success(sharedUtils.getStringValue("optName"));
            // send();
        }

        @Override
        public void lt() {
            // 超出限额

        }
    };
    // 移除接口
    private Remove remove = new Remove() {

        @Override
        public void remove() {
            handler.sendEmptyMessage(1);
            dishChange.change();
        }
    };
    /**
     * 删除弹出框
     */
    private ArrayList<DishTableBean> dishTableBeans, billTableBeans;
    private DishChange dishChange;
    private OnDismissListener cancelListener;
    private CommonTipPop tipPop;
    private CommonPersonPop personPop;
    private CommonDestoryPop destoryPop;
    private SharedUtils sharedUtils;
    private int[] screen;
    private WaterConfirm waterConfirm;
    private Tz tz;
    private boolean tuiZeng;
    private TextView table_people;

    public CommonBuysPop(Context context, WaterConfirm waterConfirm,
                         DishChange dishChange, OnDismissListener cancelListener,
                         int screen[], SharedUtils sharedUtils, Tz tz, boolean tuiZeng, TextView table_people) {
        this.context = context;
        this.dishChange = dishChange;
        this.waterConfirm = waterConfirm;
        this.cancelListener = cancelListener;
        this.screen = screen;
        this.sharedUtils = sharedUtils;
        this.tz = tz;
        this.tuiZeng = tuiZeng;
        this.table_people = table_people;
    }

    public boolean isShow() {
        if (dlg != null) {
            return dlg.isShowing();
        }
        return false;

    }

    private ListView buys;
    private BuysAdapter buysAdapter;
    private TDialog dlg;
    private CommonSendPop sendPop;
    private TextView total, send,send1, cancle, destory, more,select_, total_discount, tv_total, tv_final,tip_view;
    private LinearLayout layout0, layout1, layout2, ll_discount,ll_discount1;
    private View layout;

    private void process(double bit, LinearLayout layout) {
        int height = (int) ((bit / 725) * screen[1]);
        P.c("高度" + height);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                height);
        layout.setLayoutParams(layoutParams);
    }

    PopupWindow popupWindow;
    private View content;
    private TextView h_dl_tab_order, h_dl_tab_no_order;
    private LinearLayout mlayout;
    private TextView btn_pay;
    ImageView iv_code ;
    private void getUrlCode() {
        Intent intent = new Intent();
        intent.setAction("com.zed.Usb.ResQRcode");
        intent.putExtra("discount", "discount");
        context.sendBroadcast(intent);
    }
    private void isShowQC(boolean show){
        if(show){
            ll_discount1.setVisibility(View.VISIBLE);
            ll_discount.setVisibility(View.GONE);
        }else{
            ll_discount.setVisibility(View.VISIBLE);
            ll_discount1.setVisibility(View.GONE);
        }
    }




    public Dialog showSheet() {
        dlg = new TDialog(context, R.style.buy_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        layout = inflater.inflate(R.layout.pop_buy, null);
        mlayout = (LinearLayout) layout.findViewById(R.id.mlayout);
        // content为布局的跟节点
        content = layout.findViewById(R.id.content);
        dlg.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {
                // FileUtils.start(Effectstype.Slideright, content,500);

                Intent intent = new Intent();
                intent.setAction("pad.com.invisible");
                context.sendBroadcast(intent);
            }
        });
        getUrlCode();
        if (sharedUtils.getBooleanValue("is_cy")) {
            mlayout.setBackgroundResource(R.drawable.cy_buy_pop);

        } else {
            SkinManager.getInstance().register(layout, R.id.content);
        }
        layout0 = (LinearLayout) layout.findViewById(R.id.layout0);
        layout1 = (LinearLayout) layout.findViewById(R.id.layout1);
        layout2 = (LinearLayout) layout.findViewById(R.id.layout2);
        ll_discount = (LinearLayout) layout.findViewById(R.id.ll_discount);
        ll_discount1 = (LinearLayout) layout.findViewById(R.id.ll_discount1);
        tip_view = (TextView) layout.findViewById(R.id.tip_view);
        if(!sharedUtils.getBooleanValue("is_lan")){
            tip_view.setText(sharedUtils.getStringValue("Tip"));
        }else{
            tip_view.setText(sharedUtils.getStringValue("Tip_en"));
        }

        iv_code = (ImageView) layout.findViewById(R.id.iv_code);
        Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("urlDiscount")).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_code);

        isShowQC(true);
        more = (TextView) layout.findViewById(R.id.more);
        select_ = (TextView) layout.findViewById(R.id.select_);
        P.c(screen[0] + "--" + screen[1]);
        process(128, layout2);
        process(534, layout1);
        process(66, layout0);
        buys = (ListView) layout.findViewById(R.id.buys);
        total = (TextView) layout.findViewById(R.id.total);
        tv_total = (TextView) layout.findViewById(R.id.tv_total);

        tv_final = (TextView) layout.findViewById(R.id.tv_final);
        total_discount = (TextView) layout.findViewById(R.id.total_discount);
        if (sharedUtils.getBooleanValue("is_price")) {
            total.setVisibility(View.VISIBLE);
        } else {
            total.setVisibility(View.INVISIBLE);
        }
        cancle = (TextView) layout.findViewById(R.id.cancle);
        destory = (TextView) layout.findViewById(R.id.destory);
        send = (TextView) layout.findViewById(R.id.send);
        send1 = (TextView) layout.findViewById(R.id.send1);
        more.setVisibility(View.GONE);

        dishTableBeans = new ArrayList<DishTableBean>();
        billTableBeans = new ArrayList<DishTableBean>();
        buysAdapter = new BuysAdapter(context, dishTableBeans, buyClick, reasonI);
        buys.setAdapter(buysAdapter);
        h_dl_tab_no_order = (TextView) layout
                .findViewById(R.id.h_dl_tab_no_order);
        h_dl_tab_order = (TextView) layout
                .findViewById(R.id.h_dl_tab_order);
        // 默认选择
        handler.sendEmptyMessage(1);
        if(sharedUtils.getBooleanValue("is_cd")){
            send1.setVisibility(View.VISIBLE);
        }else{
            send1.setVisibility(View.GONE);
        }
        h_dl_tab_no_order.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isShowQC(true);
                destory.setText(R.string.clear_order);
                // send.setVisibility(View.VISIBLE);
                send.setText(R.string.sure_order);
                send.setVisibility(View.VISIBLE);
                if(sharedUtils.getBooleanValue("is_cd")){
                    send1.setVisibility(View.VISIBLE);
                }else{
                    send1.setVisibility(View.GONE);
                }
                select_.setVisibility(View.VISIBLE);
                more.setVisibility(View.GONE);
                h_dl_tab_no_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                h_dl_tab_order.setBackgroundResource(R.color.no_color);
                handler.sendEmptyMessage(1);
            }
        });
        //退赠菜回到已下单
        if (tuiZeng) {
            if (loadNet()) {
                h_dl_tab_no_order.setBackgroundResource(R.color.no_color);
                h_dl_tab_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                destory.setText(context.getResources().getString(R.string.callpay));
                more.setVisibility(View.VISIBLE);
                select_.setVisibility(View.GONE);
                // send.setVisibility(View.INVISIBLE);
                if (sharedUtils.getBooleanValue("is_card")) {

                    send.setText("手机&会员卡支付");
                } else {
                    send.setVisibility(View.INVISIBLE);
                }
            } else {
                NewDataToast.makeText(context.getResources().getString(R.string.noteat));
            }
        }
        h_dl_tab_order.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (loadNet()) {
                    h_dl_tab_no_order.setBackgroundResource(R.color.no_color);
                    h_dl_tab_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                    destory.setText(context.getResources().getString(R.string.callpay));
                    if(sharedUtils.getBooleanValue("is_print")){
                        destory.setVisibility(View.VISIBLE);
                    }else{
                        destory.setVisibility(View.INVISIBLE);
                    }
                    send1.setVisibility(View.GONE);
                    select_.setVisibility(View.GONE);
                    more.setVisibility(View.VISIBLE);
                    // send.setVisibility(View.INVISIBLE);
                    if (sharedUtils.getBooleanValue("is_card")) {

                        send.setText("手机&会员卡支付");
                    } else {
                        send.setVisibility(View.INVISIBLE);
                    }

                } else {
                    NewDataToast.makeText(context.getResources().getString(R.string.noteat));
                }

            }
        });
        btn_pay = (TextView) layout.findViewById(R.id.btn_pay);

        btn_pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 会员卡支付
                CommonUserStandPop userPop = new CommonUserStandPop(context, handler);
                userPop.showSheet();
//                close();
            }
        });

        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buysAdapter.changeTZ(false);
            }
        });
        select_.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is = false;
                for(int i=0;i<dishTableBeans.size();i++){
                    if(i==0){
                        is = dishTableBeans.get(i).isIsjj();
                    }
                    dishTableBeans.get(i).setIsjj(!is);
                }
                buysAdapter.updata(dishTableBeans,false);
                DB.getInstance().changeJJ(!is);
            }
        });
        destory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // 清空菜品信息
                // DB.getInstance()

                String btnTag = destory.getText().toString();
                if (btnTag.equals("清空已点") || btnTag.equals("clear order")) {
                    if (dishTableBeans.size() != 0) {
                        // 此处采用下单成功的返回只将弹出框关闭即可
                        destoryPop = new CommonDestoryPop(context, loadBuy);
                        destoryPop.showSheet();
                    } else {
                        NewDataToast.makeText(context.getResources().getString(R.string.notorder));
                    }
                } else if (btnTag.equals("通知结账") || btnTag.equals("call pay")) {
                    /*loadSendPop = new CommonLoadSendPop(context, "打印结账单");
                    loadSendPop.showSheet(false);
                    //开启通知结账服务
                    printBill(sharedUtils.getStringValue("IP"));*/

                    if(sharedUtils.getStringValue("user").length()==0){
                        Intent intent = new Intent(context,
                                CardValActivity.class);
                        intent.putExtra("print", true);
                        context.startActivity(intent);
                    }else{
                        printBill(sharedUtils.getStringValue("IP"));
                    }

                }
            }
        });
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setAction("pad.com.visible");
                context.sendBroadcast(intent);
                close();
            }
        });
        send1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isCD = false;
                dop();
            }
        });
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                isCD = true;
              dop();
            }
        });
        dlg.setOnDismissListener(cancelListener);
        handler.sendEmptyMessage(2);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                try {
                    if (sdcardStateChanageReceiver != null) {
                        context.unregisterReceiver(sdcardStateChanageReceiver);
                    }
                } catch (Exception e) {

                }
            }
        });
        Window dialogWindow = dlg.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 20; // 新位置X坐标
        lp.y = 40; // 新位置Y坐标
        lp.width = screen[0]; // 宽度
        lp.height = screen[1]; // 高度
        lp.alpha = 0.9f; // 透明度
        dialogWindow.setAttributes(lp);
        dlg.show();
        pause();
        sdcardStateChanageReceiver = new SdcardStateChanageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("pad.haidiyun.www.change_price");
        context.registerReceiver(sdcardStateChanageReceiver, filter);

        return dlg;
    }
    private volatile  boolean isCD = false;
    private void dop(){

        // 这里进行点餐限制操作
        // is_limit
        /*
         * if(sharedUtils.getBooleanValue("is_limit")){ //开启了点餐数量限制
         *
         * }
         */
        //多次点击只执行一次
        if (ButtonUtil.isFastDoubleClick())
            return;
        String user = sharedUtils.getStringValue("user");
        P.c("下单服务员名:" + user);
        String btnTag = send.getText().toString();
        if ((btnTag.equals("sure order") || btnTag.equals("确定点餐")) && sharedUtils.getStringValue("ParmValue").equals("True") && "".equals(user)) {
            NewDataToast.makeText("服务员未登录");
            return;
        }
        for (int i = 0; i < dishTableBeans.size(); i++) {
            P.c("菜品:" + dishTableBeans.get(i).toString());
            if ((dishTableBeans.get(i).isPrice_modify() || dishTableBeans.get(i).isTemp()) && !dishTableBeans.get(i).isEditPrice() && sharedUtils.getBooleanValue("is_edit_price")) {
                NewDataToast.makeText(dishTableBeans.get(i).getName() + "为临时菜需改价");
                return;
            }
        }
        for (int i = 0; i < dishTableBeans.size(); i++) {
            if (dishTableBeans.get(i).isMst()) {
                NewDataToast.makeText(dishTableBeans.get(i).getName() + "存在必选做法");
                return;
            }
        }


        if (btnTag.equals("sure order") || btnTag.equals("确定点餐")) {
            person = sharedUtils.getIntValue("person");
            if (dishTableBeans.size() != 0) {
                if (person > 0) {
                    if (sharedUtils.getStringValue("billId").length() != 0) {
                        // NewDataToast.makeTextL(
                        // "您已经开台,如是本人请选加菜,不是本人开台请通知服务员",3000);
                        // 是true的话就是就餐中
                        person = sharedUtils.getIntValue("person");
                        //
                        // 给个1是默认的
                        // downDish();
                        bePop = new CommonBePop(context, beDish);
                        bePop.showSheet();
                    } else {
                        selectPeople();

                        //////////
                                /*if (sharedUtils.getStringValue("table_code")
                                        .length() == 0) {
                                    NewDataToast.makeTextL(R.string.pleaseselecttable + "", 2000);
                                } else {
                                    // 开台操作
                                    if (sharedUtils.getBooleanValue("is_waite")) {
                                        // 服务员辅助模式
                                        waterConfirm.byWaiter(loginBuy);
                                    } else {
                                        // 自助模式
                                        *//*
                         * if(sharedUtils.getStringValue("optName"
                         * ).length()==0){ //暂时不处理 }else{
                         *
                         * }
                         *//*
                                        // personPop =new
                                        // CommonPersonPop(context,
                                        // buyClick,null,sharedUtils.getStringValue("optName"));
                                        // personPop.showSheet();
                                        send();
                                    }

                                }*/
                    }

                    // 取消加菜和开台
                    // bePop = new CommonBePop(context, beDish);
                    // bePop.showSheet();

                } else {
//                            NewDataToast.makeText(context.getResources().getString(R.string.selecttableperson));
                    if (sharedUtils.getStringValue("billId").length() != 0) {
                        return;
                    }
                    // 选择
                    if (sharedUtils.getStringValue("table_code").length() == 0) {
                        NewDataToast.makeText(context.getResources().getString(R.string.selecttablecode));
                    } else {
                        selectPeople();
                    }
                }
            } else {
                NewDataToast.makeText(context.getResources().getString(R.string.notorder));
            }
        } else if (btnTag.equals("手机&会员卡支付")) {
            // 会员卡支付
            CommonUserStandPop userPop = new CommonUserStandPop(context, handler);
            userPop.showSheet();

        }
    }



    //开台操作
    private SelectPerson selectPerson = new SelectPerson() {
        @Override
        public void select() {
            if (sharedUtils.getStringValue("table_code")
                    .length() == 0) {
                NewDataToast.makeTextL(R.string.pleaseselecttable + "", 2000);
            } else {
                // 开台操作
                P.c("开台-->加菜确认-->再次登录");
                if (sharedUtils.getBooleanValue("is_dish")) {
                    // 服务员辅助模式
                    waterConfirm.byWaiter(loginBuy);
                } else {
                    if (sharedUtils.getBooleanValue("is_waite")) {
                        P.c("开台-->服务员确认+加菜确认-->登录");
                        String user = sharedUtils.getStringValue("user");
                        if(user.length()==0){
                            waterConfirm.confirm(loginBuy);
                        }else{
                            send();
                        }

                        //waterConfirm.confirm(loginBuy);
                    } else {
                        P.c("开台-->服务员确认+无需加菜确认-->不登录");
                        send();
                    }

                }

            }
        }
    };
    private SdcardStateChanageReceiver sdcardStateChanageReceiver;

    class SdcardStateChanageReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(1);
        }
    }

    private RequestCall billCall, discountCall;

    private void printBill(String ip) {
        String billString = sharedUtils.getStringValue("billId");
        String tableString = sharedUtils.getStringValue("table_code");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "{\"billNo\":\"" + billString + "\",\"TableNo\":\"" + tableString + "\"}");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        billCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_PRINT_BILL))
                // .addParams("data",
                // "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        billCall.execute(billCallback);
    }

    private StringCallback billCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

            destory.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long arg0) {


                }

                @Override
                public void onFinish() {

                    destory.setEnabled(true);
                }
            };
            timer.start();
            new Thread() {
                public void run() {


                    //  {"d":"{\"Success\":true,\"Data\":\"株洲成功！\",\"Result\":\"\"}"}

                    try {
                        JSONObject object = new JSONObject(FileUtils.formatJson(response));
                        //
                        if (object.getBoolean("Success")) {
                            Message msg = new Message();
                            msg.what = 5;
//                            msg.obj = object.getString("Data");
                            msg.obj = "通知成功";
                            handler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 5;
//                            msg.obj = object.getString("Data");
                            msg.obj = "通知失败";
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                }

                ;
            }.start();


        }

        @Override
        public void onError(Call call, Exception e, int id) {

            Message msg = new Message();
            msg.what = 5;
            msg.obj = "响应错误";
            handler.sendMessage(msg);
        }

    };
    private long lastClick;
    private CommonLoadSendPop loadSendPop;

    private void send() {
        P.c("进行到这来");
//		tipPop = new CommonTipPop(context, null, handler);
//		tipPop.showSheet();
        sendNoTip(null, handler);
    }

    //
    /*
     * private void load(int num){ String ip = sharedUtils.getStringValue("IP");
	 * String tableCode = sharedUtils.getStringValue("table_code"); String
	 * optName = getUser(""); String post =
	 * "{\"TableNo\":\""+tableCode+"\",\"GstCount\":"
	 * +num+",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\""
	 * +optName
	 * +"\",\"ClientType\":\"Android\",\"ClientMac\":\""+FileUtils.getDeviceId
	 * ()+"\"}"; JSONObject jsonObject = new JSONObject(); try {
	 * jsonObject.put("data", post); requestCall = OkHttpUtils .postString()
	 * .url(U.VISTER(ip, U.URL_TABLE_OPEN))
	 * .mediaType(MediaType.parse("application/json; charset=utf-8"))
	 * .content(jsonObject.toString()) .build(); P.c(post.toString());
	 * requestCall.execute(personCallback); } catch (JSONException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); NewDataToast.makeText(
	 * "发送失败"); } } private StringCallback personCallback = new StringCallback()
	 * {
	 * 
	 * @Override public void onResponse(String response, int id) { // TODO
	 * Auto-generated method stub P.c(response); // try { JSONObject jsonObject
	 * = new JSONObject(FileUtils.formatJson(response));
	 * if(jsonObject.getBoolean("Success")){
	 * 
	 * String billId = jsonObject.getString("Data");
	 * sharedUtils.setStringValue("billId", billId); P.c("成功开台"+billId);
	 * //-----通知service更改 Intent intent = new Intent();
	 * intent.setAction(Common.SERVICE_ACTION); intent.putExtra("open_table",
	 * ""); context.startService(intent); handler.sendEmptyMessage(6);
	 * if(loginBuy==null){ buyClick.person(); }else{ buyClick.person(loginBuy,
	 * getUser(sharedUtils.getStringValue("optName"))); }
	 * 
	 * }else{ Message msg = new Message(); msg.what = 5; msg.obj =
	 * jsonObject.getString("Data"); handler.sendMessage(msg); }
	 * 
	 * } catch (JSONException e) {
	 * e.printStackTrace(); NewDataToast.makeTextL( "数据异常",2000); } close(); }
	 * 
	 * @Override public void onError(Call call, Exception e, int id) { // TODO
	 * Auto-generated method stub NewDataToast.makeText( "连接失败"); close(); } };
	 */

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

    private CommonBePop bePop;
    private BeDish beDish = new BeDish() {

        @Override
        public void dish() {

            if (sharedUtils.getStringValue("billId").length() != 0) {
                NewDataToast.makeTextL("您已经开台,如是本人请选加菜,不是本人开台请通知服务员", 3000);
            } else {
                if (sharedUtils.getStringValue("table_code").length() == 0) {
                    NewDataToast.makeTextL("请选确认桌台", 2000);
                } else {
                    // 开台操作
                    if (sharedUtils.getBooleanValue("is_waite")) {
                        // 服务员辅助模式

                        if (sharedUtils.getBooleanValue("is_dish")) {
                            if (sharedUtils.getStringValue("optName").length() != 0) {
                                send();
                            }
                        } else {
                            waterConfirm.byWaiter(loginBuy);
                        }

                    } else {
                        // 自助模式
                        if (sharedUtils.getStringValue("optName").length() == 0) {
                            // 暂时不处理
                        } else {
                            // personPop =new CommonPersonPop(context,
                            // buyClick,null,sharedUtils.getStringValue("optName"));
                            // personPop.showSheet();
                            send();
                        }

                    }

                }
            }
        }

        @Override
        public void add() {
            //加菜操作
            P.c("加菜操作-->bedish.add()");
            if (sharedUtils.getStringValue("billId").length() != 0) {
                // 是true的话就是就餐中
                person = sharedUtils.getIntValue("person");
                //
                // 给个1是默认的
                downDish();
            } else {
                NewDataToast.makeTextL("还没有开台,不能加菜", 3000);
            }
        }
    };
    private RequestCall requestCall;

    /**
     * 加载网络中的菜单
     */
    private boolean loadNet() {
        String ip = sharedUtils.getStringValue("IP");
        String billId = sharedUtils.getStringValue("billId");
        if (billId.length() == 0) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", billId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_GET_ORDER))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        requestCall.execute(netCallback);
        return true;
    }

    private StringCallback netCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(FileUtils.formatJson(response),
                                "UI已下单数据");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "UI已下单结果");
                    }
                }
            }.start();
            try {
                P.c("下单结果"+FileUtils.formatJson(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(
                        FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    // 成功
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    int len = jsonArray.length();
                    if (len != 0) {
                        billTableBeans.clear();
                    }
                    for (int i = 0; i < len; i++) {
                        // 已下单菜品
                        JSONObject object = jsonArray.getJSONObject(i);
                        DishTableBean tableBean = new DishTableBean();
                        tableBean.setI(object.getInt("SerialNo"));
                        tableBean.setCode(object.getString("MenuCode"));
                        tableBean.setNameEn(object.getString("MenuEName"));
                        tableBean.setName(object.getString("MenuName"));
                        tableBean.setCount(object.getDouble("Number"));
                        if (sharedUtils.getStringValue("tableType").equals("Price1")) {
                            tableBean.setPrice1(object.getDouble("Price"));
                        } else if (sharedUtils.getStringValue("tableType").equals("Price2")) {
                            tableBean.setPrice2(object.getDouble("Price"));
                        } else {
                            tableBean.setPrice(object.getDouble("Price"));
                        }
                        tableBean.setPrice(object.getDouble("Price"));
                        tableBean.setUnit(object.getString("Unit"));
                        tableBean.setCook_names(object.getString("Cooks"));
                        tableBean.setRemark(object.getString("Remark"));
//						tableBean.setCanTz();
                        billTableBeans.add(tableBean);

                    }
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
        }
    };

    private void pause() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_PAUSE);
        context.sendBroadcast(intent);
    }

    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        context.sendBroadcast(intent);
    }

    public void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
            context.unregisterReceiver(sdcardStateChanageReceiver);
            SkinManager.getInstance().unregister(layout);
            down();

        }
    }

    private double format(double total) {
        BigDecimal b = new BigDecimal(total);
        // 保留2位小数
        double total_v = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_v;
    }

    private SelectTable selectTable = new SelectTable() {

        @Override
        public void select(TableBean bean, String optName, int person) {

            if (bean != null) {
                // 设置名字，保存状态
                Intent intent = new Intent();
                intent.setAction(Common.SERVICE_ACTION);
                intent.putExtra("recy_table", "");
                context.startService(intent);
                // 查询一次桌台
                P.c("营业" + Common.SITE_CODE);

                if (Common.SITE_CODE.length() != 0) {
                    sharedUtils.setStringValue("sitecode", Common.SITE_CODE);
                }
                sharedUtils.setStringValue("table_name", bean.getName());
                sharedUtils.setStringValue("table_code", bean.getCode());
                sharedUtils.setStringValue("optName", optName);
                Message msg = new Message();
                msg.what = -11;
                msg.arg1 = person;
                handler.sendMessage(msg);

            }
        }

        @Override
        public void isLocked() {

            // 这里是不使用的接口覆盖方法
        }

    };

    private void selectPeople() {
        HomeSelecterPeoDialog numDl = new HomeSelecterPeoDialog(selectTable,
                table_people, sharedUtils);
        numDl.intiDialog(context, selectPerson);
    }

    private void showDiscount() {
        String ip = sharedUtils.getStringValue("IP");
        String billId = sharedUtils.getStringValue("billId");
        if (billId.length() == 0) {
            return;
        }
        JSONObject dataObject = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();
            String param = "{\"BillNo\":\"" + billId + "\"}";
            jsonObject.put("BillNo", billId);
            dataObject.put("data", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        discountCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_GET_DISCOUNT))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(dataObject.toString()).build();
        discountCall.execute(discountCallback);
    }

    private StringCallback discountCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(FileUtils.formatJson(response),
                                "折扣数据");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "折扣结果");
                    }
                }
            }.start();
            try {
                JSONObject jsonObject = new JSONObject(
                        FileUtils.formatJson(response));
                P.c("折扣数据:" + jsonObject.toString());
                if (jsonObject.getBoolean("Success")) {
                    // 成功
                    JSONObject object = jsonObject.getJSONObject("Data");
                    JSONObject feeInfo = object.getJSONObject("FeeInfo");
                    double amount = feeInfo.getDouble("Amount");
                    double discount = feeInfo.getDouble("Discount");
                    double total = feeInfo.getDouble("Total");
//                    Message message = new Message();
//                    message.what = 12;
//                    message.obj = amount + "";
//                    handler.sendMessage(message);
                    isShowQC(false);

                    tv_total.setText("总价: " + total + "元");

                    tv_final.setText("会员价: " + amount + "元");
                }
            } catch (JSONException e) {
                P.c("扫码打折解析错误");
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            P.c("扫码打折获取超时");
            e.printStackTrace();
        }

    };
}
