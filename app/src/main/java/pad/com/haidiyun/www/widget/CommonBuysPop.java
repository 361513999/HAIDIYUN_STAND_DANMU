package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.BuysAdapter;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.BeDish;
import pad.com.haidiyun.www.inter.BuyClick;
import pad.com.haidiyun.www.inter.DishChange;
import pad.com.haidiyun.www.inter.LoadBuy;
import pad.com.haidiyun.www.inter.ReasonI;
import pad.com.haidiyun.www.inter.Remove;
import pad.com.haidiyun.www.inter.WaterConfirm;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;

public class CommonBuysPop {
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
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
                    buysAdapter.updata(dishTableBeans, false);
                    handler.sendEmptyMessage(2);
                    break;
                case 0:
                    buysAdapter.updata(billTableBeans, true);
                    handler.sendEmptyMessage(3);
                    break;
                case 2:
                    total.setText("总价【未下单】:" + process());
                    break;
                case 3:
                    total.setText("总价【已下单】:" + processBill());
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
        //计算总价
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
        //计算总价
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
        @Override
        public void add(DishTableBean obj) {
            DB.getInstance().changeNum(obj.getCount() + 1, obj.getI());
            handler.sendEmptyMessage(1);
        }

        @Override
        public void delete(DishTableBean obj) {
            if (obj.getCount() == 1 && obj.getCount() > 0) {
                buyClick.remove(obj);
            } else {
                DB.getInstance().changeNum(obj.getCount() - 1, obj.getI());
                handler.sendEmptyMessage(1);
            }
        }

        @Override
        public void remove(DishTableBean obj) {
            // TODO Auto-generated method stub
            CommonDeletePop deletePop = new CommonDeletePop(context, obj, remove);
            deletePop.showSheet();
        }

        @Override
        public void res(DishTableBean obj) {
            // TODO Auto-generated method stub
            //多口味选择
            CommonResPop resPop = new CommonResPop(context, reasonI, obj, null);
            resPop.showSheet();
        }

        @Override
        public void person() {
            // TODO Auto-generated method stub
            //人数选择
            person = sharedUtils.getIntValue("person");
            //在这里进行真正的下单
            downDish();
            P.c("订单号" + sharedUtils.getStringValue("billId"));
        }

        @Override
        public void price(DishTableBean obj) {

        }

        @Override
        public void person(LoadBuy buy, String optName) {
            // TODO Auto-generated method stub
            person = sharedUtils.getIntValue("person");
            loginBuy.success(optName);
        }

    };

    private void downDish() {
        if (sharedUtils.getBooleanValue("is_waite")) {
            //服务员确认操作

            waterConfirm.confirm(loginBuy);


        } else {
            //顾客自助点餐模式
//			 tipPop = new CommonTipPop(context, sureBuy);
            tipPop = new CommonTipPop(context, loginBuy);
            tipPop.showSheet();
        }
    }

    private int person = -1;
    //选择规格
    public ReasonI reasonI = new ReasonI() {


        @Override
        public void init(final DishTableBean bean) {
            // TODO Auto-generated method stub
            new Thread() {
                public void run() {
                    DB.getInstance().updateDishInit(bean);
                    handler.sendEmptyMessage(1);
                }

                ;
            }.start();

        }

        //下面是在菜品添加界面上加口味
        @Override
        public void init(FouceBean bean) {
            // TODO Auto-generated method stub

        }

        @Override
        public void select(final ArrayList<ReasonBean> beans, final DishTableBean bean) {

            // TODO Auto-generated method stub
            new Thread() {
                public void run() {
                    DB.getInstance().updateDishTable(beans, bean);
                    handler.sendEmptyMessage(1);
                }

                ;
            }.start();

        }

        @Override
        public void insert(ArrayList<ReasonBean> beans, FouceBean bean) {
            // TODO Auto-generated method stub

        }

        @Override
        public void insert(ArrayList<ReasonBean> beans, FouceBean bean, boolean isCanSelect) {

        }

        @Override
        public void select(ArrayList<ReasonBean> beans, DishTableBean bean, HashMap<String, ArrayList<ReasonBean>> tcBeans, HashMap<String, String> tcListStr, int position, int num) {

        }

    };
    //买单成功
    private LoadBuy loadBuy = new LoadBuy() {

        @Override
        public void success(String optName) {
            // TODO Auto-generated method stub
            dishChange.change();
            if (cancelListener != null) {
                cancelListener.onDismiss(dlg);
            }
            close();
        }

        @Override
        public void waiter(String optName) {
            // TODO Auto-generated method stub
        }
    };
    //确认下单接口
    /**
     * 服务员再次确认成功
     */
    private LoadBuy loginBuy = new LoadBuy() {

        @Override
        public void success(String optName) {
            // TODO Auto-generated method stub
            //这里进行登录成功正式下单操作
            if (person != -1) {
                sendPop = new CommonSendPop(context, "正在下单", dishTableBeans, loadBuy, person, optName);
                sendPop.showSheet();
                sendPop = null;
                close();
            } else {
                NewDataToast.makeText("就餐人数错误");
            }
        }

        @Override
        public void waiter(String optName) {
            // TODO Auto-generated method stub
            P.c("服务员人数选择");
            personPop = new CommonPersonPop(context, buyClick, loginBuy, optName);
            personPop.showSheet();
        }
    };
    //移除接口
    private Remove remove = new Remove() {

        @Override
        public void remove() {
            // TODO Auto-generated method stub
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
    private WaterConfirm waterConfirm;

    public CommonBuysPop(Context context, WaterConfirm waterConfirm, DishChange dishChange, OnDismissListener cancelListener) {
        this.context = context;
        this.dishChange = dishChange;
        this.waterConfirm = waterConfirm;
        this.cancelListener = cancelListener;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    public boolean isShow() {
        if (dlg != null) {
            return dlg.isShowing();
        }
        return false;

    }

    private ListView buys;
    private BuysAdapter buysAdapter;
    private IDialog dlg;
    private CommonSendPop sendPop;
    private TextView total, cancle, send, destory;
    private LinearLayout layout0, layout1, layout2;
    private View layout;
    PopupWindow popupWindow;
    private View content;

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        layout = inflater.inflate(
                R.layout.flip_pop_buy, null);
        //content为布局的跟节点
        content = layout.findViewById(R.id.content);
        dlg.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {
                // TODO Auto-generated method stub
                FileUtils.start(Effectstype.Slideright, content, 500);
            }
        });
        final int cFullFillWidth = 900;
        layout.setMinimumWidth(cFullFillWidth);
        buys = (ListView) layout.findViewById(R.id.buys);
        total = (TextView) layout.findViewById(R.id.total);
        cancle = (TextView) layout.findViewById(R.id.cancle);
        destory = (TextView) layout.findViewById(R.id.destory);
        send = (TextView) layout.findViewById(R.id.send);
        dishTableBeans = new ArrayList<DishTableBean>();
        billTableBeans = new ArrayList<DishTableBean>();
        buysAdapter = new BuysAdapter(context, dishTableBeans, buyClick, reasonI);
        buys.setAdapter(buysAdapter);
        final TextView h_dl_tab_no_order = (TextView) layout.findViewById(R.id.h_dl_tab_no_order);
        final TextView h_dl_tab_order = (TextView) layout.findViewById(R.id.h_dl_tab_order);
        //默认选择
        handler.sendEmptyMessage(1);
        h_dl_tab_no_order.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                destory.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
                h_dl_tab_no_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                h_dl_tab_order.setBackgroundResource(R.color.no_color);
                handler.sendEmptyMessage(1);
            }
        });

        h_dl_tab_order.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (loadNet()) {
                    h_dl_tab_no_order.setBackgroundResource(R.color.no_color);
                    h_dl_tab_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                    destory.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.INVISIBLE);
                } else {
                    NewDataToast.makeText("还未就餐");
                }

            }
        });

        destory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //清空菜品信息
                //DB.getInstance()
                if (dishTableBeans.size() != 0) {
                    //此处采用下单成功的返回只将弹出框关闭即可
                    destoryPop = new CommonDestoryPop(context, loadBuy);
                    destoryPop.showSheet();
                } else {
                    NewDataToast.makeText("还没有点菜呢");
                }
            }
        });
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                close();
            }
        });
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (dishTableBeans.size() != 0) {
                    bePop = new CommonBePop(context, beDish);
                    bePop.showSheet();
                } else {

                    NewDataToast.makeText("还没有点菜呢");
                }
            }
        });
//		send.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//				if(buyLoginPop==null){
//					buyLoginPop = new CommonBuyLoginPop(context, loginBuy,cancleBuy);
//					buyLoginPop.showSheet();
//				}
//				
//				return true;
//			}
//		});
        dlg.setOnDismissListener(cancelListener);
        handler.sendEmptyMessage(2);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        pause();
        return dlg;
    }

    private CommonBePop bePop;
    private BeDish beDish = new BeDish() {

        @Override
        public void dish() {
            // TODO Auto-generated method stub
            if (sharedUtils.getStringValue("billId").length() != 0) {
                NewDataToast.makeTextL("您已经开台,如是本人请选加菜,不是本人开台请通知服务员", 3000);
            } else {
                if (sharedUtils.getStringValue("table_code").length() == 0) {
                    NewDataToast.makeTextL("请选确认桌台", 2000);
                } else {
                    //开台操作
                    if (sharedUtils.getBooleanValue("is_waite")) {
                        //服务员辅助模式
                        waterConfirm.byWaiter(loginBuy);
                    } else {
                        //自助模式
                        if (sharedUtils.getStringValue("optName").length() == 0) {
                            //暂时不处理
                        } else {
                            personPop = new CommonPersonPop(context, buyClick, null, sharedUtils.getStringValue("optName"));
                            personPop.showSheet();
                        }

                    }

                }
            }
        }

        @Override
        public void add() {
            // TODO Auto-generated method stub

            if (sharedUtils.getStringValue("billId").length() != 0) {
                //是true的话就是就餐中
                person = sharedUtils.getIntValue("person");
                //
                //给个1是默认的
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        requestCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_GET_ORDER))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString())
                .build();
        requestCall.execute(netCallback);
        return true;
    }

    private StringCallback netCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            // TODO Auto-generated method stub
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                P.c("已下单:" + jsonObject.toString());
                if (jsonObject.getBoolean("Success")) {
                    //成功
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    int len = jsonArray.length();
                    if (len != 0) {
                        billTableBeans.clear();
                    }
                    for (int i = 0; i < len; i++) {
                        //已下单菜品
                        JSONObject object = jsonArray.getJSONObject(i);
                        DishTableBean tableBean = new DishTableBean();
                        tableBean.setCode(object.getString("MenuCode"));
                        tableBean.setName(object.getString("MenuName"));
                        tableBean.setCount(object.getInt("Number"));
                        tableBean.setPrice(object.getDouble("Price"));
                        tableBean.setUnit(object.getString("Unit"));
                        tableBean.setCook_names(object.getString("Cooks"));
                        tableBean.setRemark(object.getString("Remark"));
                        billTableBeans.add(tableBean);

                    }
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            // TODO Auto-generated method stub
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
            down();

        }
    }

    private double format(double total) {
        BigDecimal b = new BigDecimal(total);
        //保留2位小数
        double total_v = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_v;
    }
}
