package pad.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.IBackService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.BuysAdapter;
import pad.com.haidiyun.www.adapter.FlipnNextAdapter;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FlipBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.PicPoint;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.flip.FlipView;
import pad.com.haidiyun.www.flip.FlipView.OnFlipListener;
import pad.com.haidiyun.www.flip.FlipView.OnOverFlipListener;
import pad.com.haidiyun.www.flip.OverFlipMode;
import pad.com.haidiyun.www.inter.AreaTouch;
import pad.com.haidiyun.www.inter.BeDish;
import pad.com.haidiyun.www.inter.BuyClick;
import pad.com.haidiyun.www.inter.DishChange;
import pad.com.haidiyun.www.inter.LoadBuy;
import pad.com.haidiyun.www.inter.PicMoveT;
import pad.com.haidiyun.www.inter.ReasonI;
import pad.com.haidiyun.www.inter.Remove;
import pad.com.haidiyun.www.inter.SelectTable;
import pad.com.haidiyun.www.inter.TcT;
import pad.com.haidiyun.www.inter.UD;
import pad.com.haidiyun.www.inter.WaterConfirm;
import pad.com.haidiyun.www.utils.Tip_Utils;
import pad.com.haidiyun.www.widget.CommonBePop;
import pad.com.haidiyun.www.widget.CommonBuysPop;
import pad.com.haidiyun.www.widget.CommonDeletePop;
import pad.com.haidiyun.www.widget.CommonDestoryPop;
import pad.com.haidiyun.www.widget.CommonDisPop;
import pad.com.haidiyun.www.widget.CommonDiscountPop;
import pad.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.com.haidiyun.www.widget.CommonMenuPop;
import pad.com.haidiyun.www.widget.CommonMovePop;
import pad.com.haidiyun.www.widget.CommonPersonPop;
import pad.com.haidiyun.www.widget.CommonPricePop;
import pad.com.haidiyun.www.widget.CommonResPop;
import pad.com.haidiyun.www.widget.CommonSearchPop;
import pad.com.haidiyun.www.widget.CommonSelectSuitPop;
import pad.com.haidiyun.www.widget.CommonSendPop;
import pad.com.haidiyun.www.widget.CommonSuitPop;
import pad.com.haidiyun.www.widget.CommonTablesPop;
import pad.com.haidiyun.www.widget.CommonTipPop;
import pad.com.haidiyun.www.widget.CommonUserStandPop;
import pad.com.haidiyun.www.widget.NewDataToast;
import pad.com.haidiyun.www.widget.RecyclerImageView;
import pad.com.haidiyun.www.widget.UDlayout;
import pad.stand.com.haidiyun.www.inter.BackFirst;



/**
 * @author Administrator
 */
@SuppressLint("ValidFragment")
public class FlipNextActivity extends Fragment {
    private FlipView flip_view;
    private ImageView iv_f_code;
    private UDlayout ud_layout;
    private FlipnNextAdapter travelAdapter;
    private FlipHandler handler;
    private Tip_Utils tipUtil;
    private CommonMovePop move;
    private Activity activity;
    private Handler parentHandler;

    public FlipNextActivity(Activity activity, Handler parentHandler) {
        this.parentHandler = parentHandler;
        this.activity = activity;
    }

    public FlipNextActivity() {

    }

    private void sendTo(String tip, String o) {
        P.c(tip + "发生sevice-----" + o);
        Intent intent = new Intent();
        intent.setAction(Common.SERVICE_ACTION);
        intent.putExtra(tip, o);
        intent.setPackage(BaseApplication.packgeName);
        activity.startService(intent);

    }

    private UD ud = new UD() {

        @Override
        public void change(boolean isOpen, boolean init) {
            // TODO Auto-generated method stub
            if (init) {
                P.c("运行========");
                if (isOpen) {
                    sendTo("open_buy", "1");
                    menu_view.setVisibility(View.VISIBLE);
                } else {//选中未下单
                    sendTo("open_buy", "0");
                    menu_view.setVisibility(View.GONE);
                    {
                        destory.setVisibility(View.VISIBLE);
                        send.setVisibility(View.VISIBLE);
                        send.setText("确定点餐");
                        h_dl_tab_no_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                        h_dl_tab_order.setBackgroundResource(R.color.no_color);
                        billTableBeans.clear();
                        handler.sendEmptyMessage(31);
                    }
                }
            } else {
                P.c("运行======+++");
                if (!isOpen) {
                    P.c("运行======+++22");
                }
            }
        }
    };
    public ReasonI reasonI = new ReasonI() {
        //resmap中的reasonbean
        @Override
        public void select(final ArrayList<ReasonBean> beans, final DishTableBean bean) {
            // TODO Auto-generated method stub
            new Thread() {
                public void run() {//加入到菜篮子更新表
                    DB.getInstance().updateDishTable(beans, bean);
                    handler.sendEmptyMessage(31);//清空
                }

                ;
            }.start();
        }

        @Override//更新套餐列表
        public void select(final ArrayList<ReasonBean> beans, final DishTableBean bean, final HashMap<String, ArrayList<ReasonBean>> tcBeans, final HashMap<String, String> tcListStr, final int position, final int tcActualNum) {
            new Thread() {
                public void run() {//加入到菜篮子更新表
                    DB.getInstance().updateDishTable(beans, bean, tcBeans, tcListStr, position, tcActualNum);
                    handler.sendEmptyMessage(31);//清空
                }

                ;
            }.start();
        }

        @Override
        public void insert(final ArrayList<ReasonBean> beans, final FouceBean bean) {
            // TODO Auto-generated method stub
            // 点击菜品界面的口味
            /**
             * 最后一个是套餐
             */
            new Thread() {
                public void run() {
                    boolean flag = DB.getInstance().addDishToPad(bean, beans,
                            "", "", "", Common.DEFAULT_NUM, handler);
                    if (flag) {
                        handler.sendEmptyMessage(7);
                    }
                }

                ;
            }.start();

        }

        @Override
        public void insert(final ArrayList<ReasonBean> beans, final FouceBean bean, boolean isCanSelect) {
            // TODO Auto-generated method stub
            // 点击菜品界面的口味
            /**
             * 最后一个是套餐
             */
            new Thread() {
                public void run() {
                    boolean flag = DB.getInstance().addDishToPad(bean, beans,
                            "", "", "", Common.DEFAULT_NUM, handler, true);
                    if (flag) {
                        handler.sendEmptyMessage(7);
                    }
                }

                ;
            }.start();
        }

        //下单
        @Override
        public void init(final FouceBean bean) {
            // TODO Auto-generated method stub
            // 点击菜品界面的口味
            new Thread() {
                public void run() {
                    boolean flag = DB.getInstance().addDishToPad(bean, null,
                            "", "", "", Common.DEFAULT_NUM, handler);
                    if (flag) {
                        handler.sendEmptyMessage(7);
                        // changeNum();
                        // dishLs();
                    }
                }

                ;
            }.start();

        }

        @Override
        public void init(final DishTableBean bean) {
            // TODO Auto-generated method stub
            new Thread() {
                public void run() {
                    DB.getInstance().updateDishInit(bean);
                    handler.sendEmptyMessage(31);
                }

                ;
            }.start();
        }
    };
    private SelectTable selectTable = new SelectTable() {

        @Override
        public void select(TableBean bean, String optName) {
            // TODO Auto-generated method stub
            if (bean != null) {
                // 设置名字，保存状态
                sendTo("recy_table", "");
                // 查询一次桌台
                sharedUtils.setStringValue("table_name", bean.getName());
                sharedUtils.setStringValue("table_code", bean.getCode());
                sharedUtils.setStringValue("optName", optName);
                if (menu_item2 != null) {
                    menu_item2.setText("服务:" + sharedUtils.getStringValue("optName"));
                }
                tipView.setText(sharedUtils.getStringValue("table_name"));
            }
        }

        @Override
        public void isLocked() {
            // TODO Auto-generated method stub
            // 这里是不使用的接口覆盖方法
        }
    };

    // 进桌台
    private final int ENTER_TABLE_VAL = 1;
    // 进行多价格,多价格不选择做法，做法需要单独再选择
    private final int ENTER_PRICE_VAL = 2;
    private final int ENTER_BUY_VAL = 3;
    private final int ENTER_WAITER_ORDER_VAL = 4;
    private final int GET_DIS = 5;
    private LoadBuy oBuy = null;
    private CommonTablesPop tablesPop;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENTER_TABLE_VAL && resultCode == 1000) {
            //
            down();
            if (data.getIntExtra("result", 0) == 1 && data.hasExtra("optName")) {
                tablesPop = new CommonTablesPop(activity,
                        selectTable, data.getStringExtra("optName"));
                tablesPop.showSheet();
            } else {
                NewDataToast.makeText("校验失败");
            }

        } else if (requestCode == ENTER_PRICE_VAL && resultCode == 1000) {
            // 时价
            if (data.hasExtra("obj")) {
                CommonPricePop pricePop = new CommonPricePop(
                        activity,
                        (FouceBean) data.getSerializableExtra("obj"), handler);
                pricePop.showSheet();
            }
        } else if (requestCode == ENTER_BUY_VAL && resultCode == 1000) {
            // 登录确认操作
            if (oBuy != null) {
                if (data.hasExtra("optName")) {
                    oBuy.success(data.getStringExtra("optName"));
                }

            }
            oBuy = null;
        } else if (requestCode == ENTER_WAITER_ORDER_VAL && resultCode == 1000) {
            if (oBuy != null) {
                P.c("服务员辅助点餐");

                if (data.hasExtra("optName")) {
                    oBuy.waiter(data.getStringExtra("optName"));
                }
            }
            oBuy = null;
        } else if (requestCode == GET_DIS && resultCode == 1000) {
            //
            P.c("获取折扣" + data.getStringExtra("optName"));
            CommonDisPop disPop = new CommonDisPop(activity, handler, data.getStringExtra("optName"));
            disPop.showSheet();
        }
    }

    private WaterConfirm confirm = new WaterConfirm() {
        public void confirm(LoadBuy loginBuy) {
            oBuy = loginBuy;
            Intent intent = new Intent(activity,
                    CardValActivity.class);
            startActivityForResult(intent, ENTER_BUY_VAL);
        }

        @Override
        public void byWaiter(LoadBuy loginBuy) {
            oBuy = loginBuy;
            // TODO Auto-generated method stub
            Intent intent = new Intent(activity,
                    CardValActivity.class);
            startActivityForResult(intent, ENTER_WAITER_ORDER_VAL);
        }

        ;
    };

    private PicMoveT picMoveT = new PicMoveT() {


        @Override
        public void sj(FouceBean obj) {
            // TODO Auto-generated method stub
            // 时价
            Intent intent = new Intent(activity,
                    CardValActivity.class);
            intent.putExtra("obj", obj);
            startActivityForResult(intent, ENTER_PRICE_VAL);
        }


        @Override
        public void rb(FouceBean obj) {
            // TODO Auto-generated method stub
            // 口味选择下单
            CommonResPop resPop = new CommonResPop(activity,
                    reasonI, null, obj);
            resPop.showSheet();
        }

        @Override
        public void tc(FouceBean obj) {//套餐
            if (obj.getType().equals("P")) {
                // 普通套餐
                CommonSuitPop suitPop = new CommonSuitPop(activity,
                        obj, tcT);
                suitPop.showSheet();
            } else if (obj.getType().equals("C")) {
                // 组合套餐
                CommonSelectSuitPop selectSuitPop = new CommonSelectSuitPop(
                        activity, obj, tcT);
                selectSuitPop.showSheet();
            }

        }
    };
    // 套餐下单
    private TcT tcT = new TcT() {
        @Override
        public void insert(final FouceBean foodsBean,
                           final String details, final String detailnames) {
            // 插入未下单表
            new Thread() {
                public void run() {
                    boolean flag = DB.getInstance().addDishToPad(foodsBean,
                            null, "", details, detailnames, Common.DEFAULT_NUM, handler);
                    if (flag) {
                        handler.sendEmptyMessage(7);
                    }
                }

                ;
            }.start();

        }
    };


    /**
     * 点击区域
     */
    private PicPoint fliPoint = new PicPoint();
    private AreaTouch areaTouch = new AreaTouch() {
        RectF downF = new RectF();
        FouceBean bean;

        @Override
        public void click(FouceBean bn, RectF dF) {
            downF = dF;
            if (Common.guKeys.containsKey(bn.getCode())) {
                bean = null;
                NewDataToast.makeTextL("亲，" + bn.getName() + ",菜品已售罄。欢迎下次品尝", 2000);

            } else {
                bean = bn;
            }

        }

        @Override
        public void up(final float x, final float y, boolean mIsFlipping) {
            // TODO Auto-generated method stub
            if (!mIsFlipping) {
                P.c(x + "弹起" + y + "---" + mIsFlipping);
                if (!downF.isEmpty()) {
                    if (downF.contains(x, y)) {
                        //包含
                        //按下和弹起在一个区域里
                /*
                PicPoint point = new PicPoint();
				fliPoint.setPoint(new int[]{(int)x,(int)y});
				fliPoint.setWidth(30);
				fliPoint.setHeight(30);
				setAnim(fliPoint);
				 
				*/
                        //选择口味是否必选或者可先判断sharedUtils.getBooleanValue("isCook")

                        if (bean != null) {
                            //查看是否有多口味选择
                            //----------------------------
                            final ArrayList<ReasonBean> resons = new ArrayList<ReasonBean>();
                            final String remark = "";
                            Collections.sort(resons, new Comparator<ReasonBean>() {
                                @Override
                                public int compare(ReasonBean rb0, ReasonBean rb1) {
                                    String code0 = rb0.getCode();
                                    String code1 = rb1.getCode();
                                    // 可以按reasoncode对象的其他属性排序，只要属性支持compareTo方法
                                    return code0.compareTo(code1);
                                }
                            });
                            if ((bean.isPrice_modify() || bean.isTemp()) && sharedUtils.getBooleanValue("is_edit_price")) {
                                // 临时菜,需要确认价格才能点击
                                picMoveT.sj(bean);
                            } else {
                                if (bean.isRequire_cook()) {
                                    // 显示口味选择
                                    P.c("1111111");
                                    if (sharedUtils.getBooleanValue("isCook")) {
                                        picMoveT.rb(bean);
                                    } else {//组合套餐勾选了做法走这里
                                        new Thread() {
                                            public void run() {
                                                boolean flag = DB.getInstance().addDishToPad(bean,
                                                        resons, remark, "", "", Common.DEFAULT_NUM, handler);

                                                if (flag) {
                                                    Message msg = new Message();
                                                    int[] fo = new int[2];
                                                    fo[0] = (int) x;
                                                    fo[1] = (int) y;
                                                    msg.what = 20;
                                                    msg.obj = fo;
                                                    handler.sendMessage(msg);
                                                }
                                            }

                                            ;
                                        }.start();


                                    }
                                }
                                // 是套餐
                                else if (bean.isSuit()) {
                                    picMoveT.tc(bean);
                                } else {//普通菜品
                                    new Thread() {
                                        public void run() {
                                            boolean flag = DB.getInstance().addDishToPad(bean,
                                                    resons, remark, "", "", Common.DEFAULT_NUM, handler);
                                            if (flag) {
                                                Message msg = new Message();
                                                int[] fo = new int[2];
                                                fo[0] = (int) x;
                                                fo[1] = (int) y;
                                                msg.what = 20;
                                                msg.obj = fo;
                                                handler.sendMessage(msg);
                                            }
                                        }

                                        ;
                                    }.start();

                                    // 不显示口味选择，
                                }
                            }
                            //
                            if (Common.DOING != 1 && tipUtil.getWel() && DB.getInstance().getCount() == 1 && !tipUtil.getBuy()) {
                                //已经有欢迎了
                                tipUtil.setBuy(true);
                                //证明点菜了
                                //不设置监听行为
                                showtips = new ShowTipsBuilder(activity)
                                        .setTarget(buyView).setTitle("查看菜品&下单")
                                        .setDescription("点这里,可以查看当前菜品\n还可以将选择好的美食用平板直接下单")
                                        .setDelay(200)
                                        .setListen(backFirst)
                                        .build();
                                showtips.show(activity, true);
                                showtips = null;
                            }
                     /*
                      //到处飞
					   final ParticleSystem ps = new ParticleSystem(FlipNextActivity.this, 100, R.drawable.smial, 800);	
					  ps.setSpeedRange(0.1f, 0.25f);
					 int []s =  bean.getPoints();
					 if(s.length==4){
						 ps.emit(getCenter(s[0], s[2]),getCenter(s[1], s[3]),1);
					 }
					  CountDownTimer timer = new CountDownTimer(800,10) {
						@Override
						public void onTick(long arg0) {
							// TODO Auto-generated method stub
						}
						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							  ps.stopEmitting();
							  ps.cancel();
						}
					};
					timer.start();*/
//					showDishNum();
//                            handler.sendEmptyMessage(4);
                        }
                    } else {
                        //按下和弹起不在一起区域
                        System.out.println("不是");
                    }
                } else {
                    //按下都没有在区域里
                    System.out.println("飞了");
                }
            }else{
                //在滑动



            }
        }

        @Override
        public void init(RectF df) {
            // TODO Auto-generated method stub
            downF = df;
        }

        @Override
        public void down() {
            // TODO Auto-generated method stub
            //按下操作
            if (!ud_layout.isOpen()) {
                ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
            }
            if (menu_view != null && menu_view.getVisibility() == View.GONE) {
                menu_view.setVisibility(View.VISIBLE);
            }
        }

    };
    /**
     * 菜品改变
     */
    private DishChange dishChange = new DishChange() {

        @Override
        public void change() {//下单成功菜品更新
            // TODO Auto-generated method stub
//			showDishNum();
            handler.sendEmptyMessage(4);
        }
    };
    private BackFirst backFirst = new BackFirst() {

        @Override
        public void goBack(boolean flag) {
            // TODO Auto-generated method stub
//			toBack(flag);
            if (flag) {
                // down();
            } else {
                // pause();
            }
            if (flag && !tipUtil.getBuy()) {
                //这里就是可以正常点餐了，移动到了点击回调里面

            }
        }
    };

    private int getCenter(int i, int j) {
        return (i + j) / 2;
    }

    /**
     * 显示菜品数量
     */
    private void showDishNum() {
        Animation shake = AnimationUtils.loadAnimation(activity,
                R.anim.buy_anim);
        buyView.startAnimation(shake);
        new Thread() {
            public void run() {
                int count = DB.getInstance().getCount();
                Message msg = new Message();
                msg.what = 29;
                msg.arg1 = count;
                handler.sendMessage(msg);
            }

            ;
        }.start();
    }

    private OnDismissListener cancelListener = new OnDismissListener() {


        @Override
        public void onDismiss(DialogInterface arg0) {
            // TODO Auto-generated method stub
//			showDishNum();
            handler.sendEmptyMessage(4);
            down();
            buysPop = null;
        }
    };
    private TextView buyView, tipView,tip_view0;
    private AnimationDrawable animationDrawable;
    private Typeface tf;
    private RelativeLayout menuLayout;
    private LinearLayout menu_view, ll_top;
    private TextView menu_item0, menu_item1, menu_item2;

   /* private int[] getScreens() {
        int screen[] = new int[2];
        // 保存长宽
        screen[0] = content.getMeasuredWidth();
        screen[1] = content.getMeasuredHeight();
        return screen;
    }*/

    /**
     * 创建餐盘
     */
    private CommonBuysPop buysPop;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy方法");
//        if (servicereceiver != null) {
//            getActivity().unregisterReceiver(servicereceiver);
//        }
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        for (int i = 0; i < rootView.getChildCount(); i++) {
            if (rootView.getChildAt(i).getTag() != null && rootView.getChildAt(i).getTag().equals(TAG)) {
                rootView.removeViewAt(i);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (connAidl != null) {
            getActivity().unbindService(connAidl);
        }
        getActivity().unregisterReceiver(sdcardStateChanageReceiver);
    }

    String TAG = "complete";

    private void createDishLayout() {

        tf = Typeface.createFromAsset(activity.getAssets(), "font/mb.ttf");
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
       /* for(int i=0;i<rootView.getChildCount();i++){
            if(rootView.getChildAt(i).getTag()!=null&&rootView.getChildAt(i).getTag().equals(TAG)){
                return;
            }
        }*/

        //RelativeLayout animLayout = new RelativeLayout(this);
        menuLayout = (RelativeLayout) RelativeLayout.inflate(activity, R.layout.flip_menu_layout, null);
        menuLayout.setTag(TAG);
        menu_view = (LinearLayout) menuLayout.findViewById(R.id.menu_view);
        tipView = (TextView) menuLayout.findViewById(R.id.tipView);
        buyView = (TextView) menuLayout.findViewById(R.id.buyView);
        buyView.setTypeface(tf);
        buyView.getBackground().setAlpha(200);
        menu_view.getBackground().setAlpha(150);
        String flag = Build.MODEL;
        if (flag.equals("rk312x")) {
            RelativeLayout.LayoutParams paramTest = (RelativeLayout.LayoutParams) menu_view.getLayoutParams();
            paramTest.topMargin = 20;
            menu_view.setLayoutParams(paramTest);
        }
        tipView.setTypeface(tf);
        tipView.setText(sharedUtils.getStringValue("table_name"));
//		animLayout.addView(menuLayout);
//		animLayout.setBackgroundColor(R.color.red);
        //animationDrawable = (AnimationDrawable) buyView.getBackground();
        //animationDrawable.start();  
//		showDishNum();
        handler.sendEmptyMessage(4);
//		Glide.with(BaseApplication.application).load(R.drawable.buy).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(buyView);
        buyView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                /*if(buyView.getText().toString().length()==0){
                    //还没有点餐
					NewDataToast.makeText("请先点餐");
				}else{
					
			  } */
                /*if(buysPop==null){
                      buysPop = new CommonBuysPop(FlipNextActivity.this, confirm, dishChange, cancelListener);
					  buysPop.showSheet();
					}*/
                String view = buyView.getText().toString();
                if (sharedUtils.getStringValue("billId").length() != 0
                        || view.length() != 0) {
                    ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
                } else {
                    NewDataToast.makeText("请先点餐");
                }
            }
        });

        buyView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                return true;
            }
        });
        rootView.addView(menuLayout);
        //设置进行类目和服务操作
        menu_item0 = (TextView) menuLayout.findViewById(R.id.menu_item0);
        menu_item1 = (TextView) menuLayout.findViewById(R.id.menu_item1);
        menu_item2 = (TextView) menuLayout.findViewById(R.id.menu_item2);
        menu_item0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CommonMenuPop menuPop = new CommonMenuPop(activity, handler);
                menuPop.showSheet();
            }
        });
        //
        menu_item1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CommonSearchPop searchPop = new CommonSearchPop(activity, handler);
                searchPop.showSheet();
            }
        });
        //
        if (sharedUtils.isHere("optName") && sharedUtils.getStringValue("table_name").length() != 0) {
            menu_item2.setText("服务:" + sharedUtils.getStringValue("optName"));
        }
        menu_item2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (FileUtils.db_exits()) {

                    Intent intent = new Intent(activity,
                            CardValActivity.class);
                    startActivityForResult(intent, ENTER_TABLE_VAL);
                }
            }
        });
    }

    private ShowTipsView showtips;
    private volatile ArrayList<FlipBean> flipBeans;

    private SharedUtils sharedUtils;
    private FrameLayout animation_viewGroup;
//    private ServiceReceiver servicereceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView方法");
        View view = inflater.inflate(R.layout.flip_next_layout, container, false);
        return view;
    }

//    class ServiceReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if ("com.customs.broadcast".equals(intent.getAction())) {
//                String table_name = sharedUtils.getStringValue("table_name");
//                String broadcastIntent = "com.hdy.sendcode";
//                Intent intent1 = new Intent(broadcastIntent);
//                intent1.putExtra("code", table_name);         //向广播接收器传递数据
////                Log.e(TAG, "点餐发送桌台号:" + table_code + "桌台name:" + table_name);
//                getActivity().sendBroadcast(intent1);
//            }
//        }
//
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated方法");
        initLayout(view);
        String ip = sharedUtils.getStringValue("IP");
        if (ip.length() != 0) {
            gu(ip);
        }
        handler.sendEmptyMessage(3);
        if (sharedUtils.getBooleanValue("canDiscount") && sharedUtils.getBooleanValue("canDiscountShow")) {
            P.c("允许打折");
          //  handler.sendEmptyMessage(66);
        }
    }

    private TextView tv_total, tv_final;
    private LinearLayout ll_discount,ll_discount1;
    private TextView btn_pay,tip_view;

    private void isShowQC(boolean show){
        if(show){
            ll_discount1.setVisibility(View.VISIBLE);
            ll_discount.setVisibility(View.GONE);
        }else{
            ll_discount.setVisibility(View.VISIBLE);
            ll_discount1.setVisibility(View.GONE);
        }
    }
    private void initLayout(View view) {

        // TODO Auto-generated method stub
        handler = new FlipHandler(this);

        down();
        animation_viewGroup = createAnimLayout();
        sharedUtils = new SharedUtils(Common.CONFIG);
        tipUtil = new Tip_Utils();
        tip_view0 = (TextView) view.findViewById(R.id.tip_view0);
        iv_f_code = (ImageView) view.findViewById(R.id.iv_f_code);
        flip_view = (FlipView) view.findViewById(R.id.flip_view);
        ud_layout = (UDlayout) view.findViewById(R.id.ud_layout);
        ll_discount1 = (LinearLayout) view.findViewById(R.id.ll_discount1);
        ll_discount = (LinearLayout) view.findViewById(R.id.ll_discount);
        isShowQC(true);
        Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("urlDiscount")).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(iv_f_code);
        if(!sharedUtils.getBooleanValue("is_lan")){
            tip_view0.setText(sharedUtils.getStringValue("Tip"));
        }else{
            tip_view0.setText(sharedUtils.getStringValue("Tip_en"));
        }
        tip_view = (TextView) view.findViewById(R.id.tip_view);
        btn_pay = (TextView) view.findViewById(R.id.btn_pay);
        tip_view.setText(sharedUtils.getStringValue("Tip"));
        btn_pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 会员卡支付
                CommonUserStandPop userPop = new CommonUserStandPop(getActivity(), handler);
                userPop.showSheet();
            }
        });

        tv_total = (TextView) view.findViewById(R.id.tv_total);

        tv_final = (TextView) view.findViewById(R.id.tv_final);
        ud_layout.setUD(ud);
        //翻转模式判断
       /* if (sharedUtils.getBooleanValue("isflip")) {
            flip_view.setIsFlippingVertically(true);
        } else {
            flip_view.setIsFlippingVertically(false);
        }*/
        flip_view.setOverFlipMode(OverFlipMode.RUBBER_BAND);
        flip_view.setCancleListener(areaTouch);
        flip_view.peakNext(true);


        flip_view.setOnFlipListener(new OnFlipListener() {

            @Override
            public void onFlippedToPage(FlipView v, int position, long id) {
                int INDEX = position % flipBeans.size();
                if (INDEX==0){
                    Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("urlDiscount")).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(iv_f_code);
                    iv_f_code.setVisibility(View.VISIBLE);
                    tip_view0.setVisibility(View.VISIBLE);
                }else{
                    iv_f_code.setVisibility(View.GONE);
                    tip_view0.setVisibility(View.GONE);
                }
                Common.LAST_PAGE = position;
                // TODO Auto-generated method stub
                NewDataToast.Text(activity, (INDEX + 1) + "/" + flipBeans.size(), 100).show();
                if (DB.getInstance().getCount() == 0 && !tipUtil.getWel() && Common.DOING != 1) {
                    //如果是没有数据的时候,也不是第一次
                    //----------------------
                    //如果是可以点击
                    P.c("OnFlipListener" + position);
                    ArrayList<FouceBean> fouces = flipBeans.get(INDEX).getFouceBeans();
                    if (fouces.size() != 0) {
                        tipUtil.setWel(true);
                        showtips = new ShowTipsBuilder(activity)
                                .setTarget(flip_view).setTitle("点选美食")
                                .setDescription("从这里开始选择你喜欢的美食,学会了吗?")
                                .setDelay(200)
                                .setListen(backFirst)
                                .build();
                        showtips.showBy_XY(activity, fouces.get(0).getPoints());
                        showtips = null;

                    }

                }
                //当前位置
            }
        });
        flip_view.setOnOverFlipListener(new OnOverFlipListener() {

            @Override
            public void onOverFlip(FlipView v, OverFlipMode mode,
                                   boolean overFlippingPrevious, float overFlipDistance,
                                   float flipDistancePerPage) {
                // TODO Auto-generated method stub
                P.c("OnOverFlipListener");
            }
        });

        createDishLayout();
        flipBeans = new ArrayList<FlipBean>();



		/*handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				flip_view.smoothFlipTo(flip_view.getCurrentPage()+1);
				handler.postDelayed(this, 3000);
			}
		}, 3000);*/
        //处理引导业务
        if (!tipUtil.getWel() && DB.getInstance().getCount() == 0 && !tipUtil.getBuy() && !tipUtil.getFirst() && Common.DOING != 1) {
            tipUtil.setFirst(true);
            if (sharedUtils.getBooleanValue("isflip")) {
                showtips = new ShowTipsBuilder(activity)
                        .setTarget(buyView).setTitle("上下翻看菜品")
                        .setDescription("手指上下滑动即可翻看本店精品菜品")
                        .setDelay(200)
                        .setImageSource(R.drawable.ud)
                        .setListen(backFirst)
                        .build();
            } else {
                showtips = new ShowTipsBuilder(activity)
                        .setTarget(buyView).setTitle("左右翻看菜品")
                        .setDescription("手指左右滑动即可翻看本店精品菜品")
                        .setDelay(200)
                        .setImageSource(R.drawable.lr)
                        .setListen(backFirst)
                        .build();
            }
            int points[] = new int[]{0, 0, 1, 1};
            showtips.showBy_XY(activity, points);
        }
        // 初始化菜篮
        buys = (ListView) view.findViewById(R.id.buys);
        total = (TextView) view.findViewById(R.id.total);
        if (sharedUtils.getBooleanValue("is_price")) {
            total.setVisibility(View.VISIBLE);
        } else {
            total.setVisibility(View.INVISIBLE);
        }
        cancle = (TextView) view.findViewById(R.id.cancle);
        destory = (TextView) view.findViewById(R.id.destory);
        send = (TextView) view.findViewById(R.id.send);
        h_dl_tab_no_order = (TextView) view.findViewById(R.id.h_dl_tab_no_order);
        h_dl_tab_order = (TextView) view.findViewById(R.id.h_dl_tab_order);
        ll_top = (LinearLayout) view.findViewById(R.id.ll_top);
        String flag = sharedUtils.getStringValue("isOldVersion");
        if (flag.equals("rk312x")) {
            //平移一点
            RelativeLayout.LayoutParams paramTest = (RelativeLayout.LayoutParams) ll_top.getLayoutParams();
            paramTest.leftMargin = 90;
            paramTest.rightMargin = 0;
            ll_top.setLayoutParams(paramTest);
        } else {
            RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) ll_top.getLayoutParams();
            rp.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        dishTableBeans = new ArrayList<DishTableBean>();
        billTableBeans = new ArrayList<DishTableBean>();
        buysAdapter = new BuysAdapter(activity, dishTableBeans,
                buyClick, reasonI);
        buys.setAdapter(buysAdapter);
        destory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //清空菜品信息
                //DB.getInstance()
                if (dishTableBeans.size() != 0) {
                    //此处采用下单成功的返回只将弹出框关闭即可
                    destoryPop = new CommonDestoryPop(activity, loadBuy);
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
                ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
            }
        });
        //确定点餐
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (send.getText().toString().equals("确定点餐")) {
                    if (dishTableBeans.size() != 0) {
                        int size = dishTableBeans.size();
                        int plus = 0;
                        //套餐的判断
                        for (int i = 0; i < size; i++) {
                            if (!dishTableBeans.get(i).isCanSendMustFlag()) {//false
                                plus = plus + 1;
                            }
                        }
                        if (plus > 0) {
                            NewDataToast.makeText("请选择必选的做法");
                        } else {
                            bePop = new CommonBePop(activity, beDish);
                            bePop.showSheet();
                        }
                    } else {
                        NewDataToast.makeText("还没有点菜呢");
                    }
                } else if (send.getText().toString().equals("获取折扣")) {
                    Intent intent = new Intent(activity,
                            CardValActivity.class);
                    startActivityForResult(intent, GET_DIS);
                }
                // TODO Auto-generated method stub

            }
        });
        h_dl_tab_no_order.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                isShowQC(true);
                billTableBeans.clear();
                destory.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
                send.setText("确定点餐");
                h_dl_tab_no_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                h_dl_tab_order.setBackgroundResource(R.color.no_color);
                handler.sendEmptyMessage(31);
            }
        });

        h_dl_tab_order.setOnClickListener(new OnClickListener() {//选中已下单菜品

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (loadNet()) {
                    h_dl_tab_no_order.setBackgroundResource(R.color.no_color);
                    h_dl_tab_order.setBackgroundResource(R.drawable.h_dl_s_sel);
                    destory.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                    send.setText("获取折扣");
                } else {
//                    billTableBeans.clear();
//                    ll_discount.setVisibility(View.GONE);
//                    handler.sendEmptyMessage(34);
                    NewDataToast.makeText("还未就餐");
                }

            }
        });

        ud.change(true, true);

    }


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
//                        String bean = tableBean.toString();
                    }
                    handler.sendEmptyMessage(34);
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
                        confirm.byWaiter(loginBuy);
                    } else {
                        //自助模式
                        if (sharedUtils.getStringValue("optName").length() == 0) {
                            //暂时不处理
                        } else {
                            personPop = new CommonPersonPop(activity, buyClick, null, sharedUtils.getStringValue("optName"));
                            personPop.showSheet();
                        }
                    }
                }
            }
        }

        @Override
        public void add() {//加菜
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
    // 移除接口
    private Remove remove = new Remove() {

        @Override
        public void remove() {
            // TODO Auto-generated method stub
            handler.sendEmptyMessage(31);
            dishChange.change();
        }
    };
    private BuyClick buyClick = new BuyClick() {
        @Override
        public void add(final DishTableBean obj) {
            new Thread() {
                public void run() {
                    DB.getInstance().changeNum(obj.getCount() + 1, obj.getI());
                    handler.sendEmptyMessage(31);
                }

                ;
            }.start();

        }

        @Override
        public void delete(final DishTableBean obj) {
            if (obj.getCount() == 1 && obj.getCount() > 0) {
                buyClick.remove(obj);
            } else {
                new Thread() {
                    public void run() {
                        DB.getInstance().changeNum(obj.getCount() - 1,
                                obj.getI());
                        handler.sendEmptyMessage(31);
                    }

                    ;
                }.start();

            }
        }

        @Override
        public void remove(DishTableBean obj) {
            // TODO Auto-generated method stub
            CommonDeletePop deletePop = new CommonDeletePop(
                    activity, obj, remove);
            deletePop.showSheet();
        }

        @Override
        public void res(DishTableBean obj) {
            // TODO Auto-generated method stub
            // 多口味选择
            CommonResPop resPop = new CommonResPop(activity, reasonI, obj, null);
            resPop.showSheet();
        }

        @Override
        public void person() {
            // TODO Auto-generated method stub
            // 人数选择
            person = sharedUtils.getIntValue("person");
            // 在这里进行真正的下单
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.e(TAG, "hidden true方法");
            Intent intentService = new Intent();
            intentService.setAction("flip.pad.com.invisible");
            getActivity().sendBroadcast(intentService);
        } else {
            Log.e(TAG, "hidden false方法");
            Intent intentService = new Intent();
            intentService.setAction("flip.pad.com.visible");
            activity.sendBroadcast(intentService);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume方法");
        Intent intentService = new Intent();
        intentService.setAction("flip.pad.com.visible");
        activity.sendBroadcast(intentService);
        getUrlCode();
        getAidlArg();
        //设置设备号
        setDevice();
        sdcardStateChanageReceiver = new SdcardStateChanageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("pad.haidiyun.www.change_price");
        getActivity().registerReceiver(sdcardStateChanageReceiver, filter);
    }

    private SdcardStateChanageReceiver sdcardStateChanageReceiver;

    class SdcardStateChanageReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("refreshData")) {
                P.c("刷新已点菜单");
                ll_discount.setVisibility(View.GONE);
                billTableBeans.clear();
                handler.sendEmptyMessage(31);
            }
        }
    }

    private void getUrlCode() {
        Intent intent = new Intent();
        intent.setAction("com.zed.Usb.ResQRcode");
        intent.putExtra("discount", "discount");
        getActivity().sendBroadcast(intent);
    }

    private void setDevice() {
        String snCode = sharedUtils.getStringValue("sn");
        String ipCode = sharedUtils.getStringValue("IP");
        if (!"".equals(snCode)) {
            sharedUtils.setStringValue("deviceSerialNo", snCode);
        } else {
            sharedUtils.setStringValue("deviceSerialNo", ipCode.replace(".", "_"));
        }
    }

    private void getAidlArg() {
        String isOldVersion = Build.MODEL;//rk3368-P9   rk312x
        sharedUtils.setStringValue("isOldVersion", isOldVersion);
        Intent mServiceIntent = new Intent("sys.update.time");
        Intent endIntent = new Intent(pad.stand.com.haidiyun.www.common.Common.createExplicitFromImplicitIntent(activity, mServiceIntent));
        getActivity().bindService(endIntent, connAidl, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection connAidl = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBackService backService = IBackService.Stub.asInterface(service);
            try {
                String regCode = backService.getdeviceInfoString("ServiceInfo", 1);
                String sn = backService.getdeviceInfoString("ServiceInfo", 2);
                String callingServer = backService.getdeviceInfoString("ServiceInfo", 19);
                String localServer = backService.getdeviceInfoString("ServiceInfo", 6);
                P.c("regcode值:" + regCode + "  localServer值:" + localServer);
                if (!"".equals(regCode)) {
                    sharedUtils.setStringValue("regCode", regCode);
                    sharedUtils.setStringValue("sn", sn);
                    sharedUtils.setStringValue("callingServer", callingServer);
                    //这里存储aidl获取的ip替代原来手动配置
                    if (sharedUtils.getStringValue("IP").equals(localServer)) {
                        return;
                    } else {
//                        sharedUtils.setStringValue("IP", localServer);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("", "断开连接");
        }
    };
    private CommonPersonPop personPop;
    private CommonSendPop sendPop;
    private CommonTipPop tipPop;
    private CommonDestoryPop destoryPop;
    private ListView buys;
    private BuysAdapter buysAdapter;
    private Dialog dlg;
    private ArrayList<DishTableBean> dishTableBeans, billTableBeans;
    private TextView total, cancle, send, destory, h_dl_tab_no_order, h_dl_tab_order;
    private CommonBePop bePop;
    private int person = -1;

    //加菜
    private void downDish() {
        if (sharedUtils.getBooleanValue("is_waite")) {
            // 服务员确认操作

            confirm.confirm(loginBuy);

        } else {
            // 顾客自助点餐模式
            // tipPop = new CommonTipPop(context, sureBuy);
            tipPop = new CommonTipPop(activity, loginBuy);
            tipPop.showSheet();
        }
    }

    // 买单成功
    private LoadBuy loadBuy = new LoadBuy() {
        @Override
        public void success(String optName) {//成功下单或者加菜
            // TODO Auto-generated method stub
            dishChange.change();
            if (cancelListener != null) {
                cancelListener.onDismiss(dlg);
            }
            ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
        }

        @Override
        public void waiter(String optName) {
            // TODO Auto-generated method stub
        }
    };
    private LoadBuy loginBuy = new LoadBuy() {

        @Override
        public void success(String optName) {
            // TODO Auto-generated method stub
            // 这里进行登录成功正式下单操作
            if (person != -1) {
                sendPop = new CommonSendPop(activity, "正在下单",
                        dishTableBeans, loadBuy, person, optName);
                sendPop.showSheet();
                sendPop = null;
                person = -1;
                //ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
            } else {
                NewDataToast.makeText("就餐人数错误");
            }
        }

        @Override
        public void waiter(String optName) {
            // TODO Auto-generated method stub
            P.c("服务员人数选择");
            personPop = new CommonPersonPop(activity, buyClick,
                    loginBuy, optName);
            personPop.showSheet();
        }
    };


    private CountDownTimer backTimer;
    /**
     * 返回到首页
     */
    /*private void toBack(boolean start){
        if(backTimer!=null){
			backTimer.cancel();
		}else{
			backTimer = new CountDownTimer(30000,100) {
			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(2);
			 	}
			};
		}
		if(start){
			backTimer.start();
		}

	}*/

    /**
     * 发送一次点击广播
     */
    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        activity.sendBroadcast(intent);
    }


    private void loadData() {
        //加载数据
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                flipBeans = DB.getInstance().getDishsToFlip();
                handler.sendEmptyMessage(1);
            }
        }.start();

    }


    public void changeFlip() {
        if (flip_view.isFlippingVertically()) {
            flip_view.setIsFlippingVertically(false);
        } else {
            flip_view.setIsFlippingVertically(true);
        }
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

    /**
     * 计算已下单
     *
     * @return
     */
    private double process(boolean noSend) {
        // 计算总价
        int len = billTableBeans.size();
        double total = 0;
        for (int i = 0; i < len; i++) {
            DishTableBean obj = billTableBeans.get(i);
            total += (obj.getCount() * obj.getPrice());
        }
        return format(total);
    }

    private double format(double total) {
        BigDecimal b = new BigDecimal(total);
        // 保留2位小数
        double total_v = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_v;
    }

    private final int SCROOL_WIDTH = -1000;
    private final int SCROOL_HEIGHT = 0;
    CommonLoadSendPop loadSendPop = null;

    private class FlipHandler extends Handler {
        WeakReference<FlipNextActivity> mLeakActivityRef;

        public FlipHandler(FlipNextActivity leakActivity) {
            mLeakActivityRef = new WeakReference<FlipNextActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {

                switch (msg.what) {
                    case 67:
                        String ip = sharedUtils.getStringValue("IP");
                        Map<String, String> map = (Map<String, String>) msg.obj;
                        String name = map.get("name");
                        String dis = map.get("dis");

                        JSONObject object = new JSONObject();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            object.put("BillNo", sharedUtils.getStringValue("billId"));
                            object.put("DiscountRule", dis);
                            object.put("UserName", name);
                            object.put("DeviceSerialNo", Common.SER);

                            jsonObject.put("data", object.toString());
                            P.c(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadSendPop = new CommonLoadSendPop(activity, "进行打折操作");
                        loadSendPop.showSheet(true);
                        OkHttpUtils.postString()
                                .url(U.VISTER(ip, U.URL_POST_DIS))
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .content(jsonObject.toString())
                                .build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                if (loadSendPop != null) {
                                    loadSendPop.cancle();
                                    loadSendPop = null;
                                }
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                P.c(response);
                                if (loadSendPop != null) {
                                    loadSendPop.cancle();
                                    loadSendPop = null;
                                }

                                try {
                                    JSONObject jsonObject = new JSONObject(
                                            FileUtils.formatJson(response));
                                    P.c("折扣数据:" + jsonObject.toString());
                                    if (jsonObject.getBoolean("Success")) {
                                        NewDataToast.makeText("打折成功");
                                        loadNet();
                                    } else {
                                        NewDataToast.makeText(jsonObject.getString("Data"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                        break;
                    case 66:
                        CommonDiscountPop pop = new CommonDiscountPop(activity);
                        pop.showSheet();
                        break;
                    case 29:
                        int count = msg.arg1;
                        if (count != 0) {
                            buyView.setText(String.valueOf(count));
                        } else {
                            buyView.setText("");
                        }
                        tipView.setText(sharedUtils.getStringValue("table_name"));
                        break;
                    case 31:
                        new Thread() {
                            public void run() {
                                dishTableBeans.clear();
                                dishTableBeans = DB.getInstance().getTableBeans();
                                handler.sendEmptyMessage(32);
                            }
                        }.start();
                        break;
                    case 32:
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
                        handler.sendEmptyMessage(33);
                        break;
                    case 33:
                        total.setText("总价【未下单】:" + process());
//                        total.setVisibility(View.VISIBLE);
                        break;
                    case 34:
                        P.c("已下单列表");
                        if (sharedUtils.getBooleanValue("canDiscount") && sharedUtils.getStringValue("billId").length() != 0) {
                            showDiscount();
                        } else {
                            ll_discount.setVisibility(View.GONE);
                        }
                        buysAdapter.updata(billTableBeans, true);
                        handler.sendEmptyMessage(35);
                        break;
                    case 35:
                        total.setText("总价【已下单】:" + process(false));
//                        total.setVisibility(View.VISIBLE);
                        break;
                    case 20:
                        int[] fo = (int[]) msg.obj;
                        fliPoint.setPoint(fo);
                        fliPoint.setWidth(30);
                        fliPoint.setHeight(30);
                        setAnim(fliPoint);
                        break;
                    case 7:
                        NewDataToast.makeTextD("已添加到点餐栏", 500);
                    case 4:
                        showDishNum();
                        break;
                    case 3:
                        //解析数据
                        new Thread() {
                            public void run() {
                                loadData();
                            }

                            ;
                        }.start();
                        break;
                    case 2:
                        int index = msg.arg1;
                        flip_view.flipTo(index);//回到第一页
                        break;
                    case 0:
                        // 用来清除动画后留下的垃圾
                        try {
                            animation_viewGroup.removeAllViews();
                            animation_viewGroup.removeAllViewsInLayout();
                            animation_viewGroup.destroyDrawingCache();
                            animation_viewGroup.clearAnimation();
                        } catch (Exception e) {
                        }
                        isClean = false;
                        break;
                    case 1:
                        if (flipBeans.size() != 0) {
                            flip_view.setT(new FlipView.T() {


                                @Override
                                public void move() {
                                    int INDEX = flip_view.getCurrentPage() % flipBeans.size();
                                    P.c("第几个~~"+INDEX);

                                    if(INDEX==0){
                                        iv_f_code.setVisibility(View.GONE);
                                        tip_view0.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void down() {
                                    int INDEX = flip_view.getCurrentPage() % flipBeans.size();
                                    P.c("第几个~~"+INDEX);
                                    if(INDEX==0){
                                        Glide.with(BaseApplication.application).load(sharedUtils.getStringValue("urlDiscount")).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_f_code);
                                        iv_f_code.setVisibility(View.VISIBLE);
                                        tip_view0.setVisibility(View.VISIBLE);
                                    }else{
                                        iv_f_code.setVisibility(View.GONE);
                                        tip_view0.setVisibility(View.GONE);
                                    }
                                }


                            });


                            if (travelAdapter == null) {
                                travelAdapter = new FlipnNextAdapter(activity, flipBeans,
                                        areaTouch);
                                flip_view.setAdapter(travelAdapter);
                                if (flipBeans != null && flipBeans.size() != 0) {
                                    NewDataToast.Text(activity, 1 + "/" + flipBeans.size(), 100).show();
                                }

                            } else {
                                travelAdapter.updata(flipBeans);
                            }
                            String s = buyView.getText().toString();
                            int ct = 0;
                            try {
                                ct =   Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            if(sharedUtils.getStringValue("billId").length()==0&&ct==0){
                                Common.LAST_PAGE = 0;
                            }
                            flip_view.flipTo(Common.LAST_PAGE);
                        } else {

                        }
                        break;
                    case -2:
                        NewDataToast.Text(activity, "请检查数据完整性", -1);
                        break;
                    default:
                        break;
                }


            }
        }
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

                    tv_total.setText("总价:        " + total + "元");

                    tv_final.setText("会员价:    " + amount + "元");
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

    // 动画数量
    private int number = 0;
    // 清理视图
    private boolean isClean = false;
    /**
     * 创建视图动画
     */
    private int AnimationDuration = 800;

    /**
     * @param vg       动画运行的层 这里是frameLayout
     * @param view     要运行动画的View
     * @param location 动画的起始位置
     * @return
     * @deprecated 将要执行动画的view 添加到动画层
     */

    private View addViewToAnimLayout(ViewGroup vg, View view, PicPoint picPoint) {
        int location[] = picPoint.getPoint();

        int x = location[0];
        int y = location[1];

			/*
             * FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
			 * dip2px(this,picPoint.getWidth()),dip2px(this,picPoint.getHeight()));
			 */
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                picPoint.getWidth(), picPoint.getHeight());
        lp.leftMargin = x;
        lp.topMargin = y;
        // view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);
        vg.addView(view);
        return view;
    }

    private void setAnim(PicPoint picPoint) {
        final RecyclerImageView iview = new RecyclerImageView(activity);

        Glide.with(BaseApplication.application).load(R.drawable.smial)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iview);

        final View view = addViewToAnimLayout(animation_viewGroup, iview,
                picPoint);
        int start_location[] = picPoint.getPoint();
        Animation mScaleAnimation = new ScaleAnimation(1.5f, 0.0f, 1.5f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF,
                0.1f);
        mScaleAnimation.setDuration(AnimationDuration);
        mScaleAnimation.setFillAfter(true);

        // view.setAlpha(0.6f);

        int[] end_location = new int[2];
        buyView.getLocationInWindow(end_location);
        int endX = end_location[0] - start_location[0];
        int endY = end_location[1] - start_location[1];

        // -start_location[1]
        Animation mTranslateAnimation = new TranslateAnimation(0, endX, 0, endY);
        Animation mRotateAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setDuration(AnimationDuration);
        mTranslateAnimation.setDuration(AnimationDuration);
        AnimationSet mAnimationSet = new AnimationSet(true);

        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(mRotateAnimation);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mTranslateAnimation);

        mAnimationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

                number--;
                if (number == 0) {
                    isClean = true;
                    handler.sendEmptyMessage(0);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

        });
        view.startAnimation(mAnimationSet);
        handler.sendEmptyMessage(4);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 创建动画层
     */
    private FrameLayout createAnimLayout() {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    /**
     * 沽清
     */
    private RequestCall guCall, discountCall;

    private void gu(String ip) {
        P.c("查询沽清");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "[{\"TabName\":\"GetSaleOutMenus\",\"Timestamp\":0}]");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        guCall = OkHttpUtils.post()
                .url(U.VISTER(ip, U.URL_GU))
                // .addParams("data",
                // "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                //.mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build();
        guCall.execute(guCallback);
        P.c("提交沽清查询");
    }

    private StringCallback guCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            // TODO Auto-generated method stub
            P.c(response);
            try {
                JSONObject jsonObject = new JSONObject(
                        FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    Common.guKeys.clear();
                    JSONArray json = jsonObject.getJSONArray("Data");

                    int len = json.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = json.getJSONObject(i);
                        Common.guKeys.put(obj.getString("MenuCode"), obj.getInt("LeftQty"));
                    }
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

}
