package pad.stand.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.IBackService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.FoodsAdapter;
import pad.stand.com.haidiyun.www.adapter.MenusAdapter;
import pad.stand.com.haidiyun.www.adapter.SearchAdapter;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.ImageBDInfo;
import pad.stand.com.haidiyun.www.bean.ImageInfo;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.bean.PicPoint;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.bean.TableBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.DishChange;
import pad.stand.com.haidiyun.www.inter.LoadBuy;
import pad.stand.com.haidiyun.www.inter.NumSel;
import pad.stand.com.haidiyun.www.inter.PicMoveT;
import pad.stand.com.haidiyun.www.inter.ReasonI;
import pad.stand.com.haidiyun.www.inter.SelectTable;
import pad.stand.com.haidiyun.www.inter.SetI;
import pad.stand.com.haidiyun.www.inter.TcT;
import pad.stand.com.haidiyun.www.inter.Tz;
import pad.stand.com.haidiyun.www.inter.TzC;
import pad.stand.com.haidiyun.www.inter.WaterConfirm;
import pad.stand.com.haidiyun.www.widget.AutoWrapLinearLayout;
import pad.stand.com.haidiyun.www.widget.CommonAdvertPop;
import pad.stand.com.haidiyun.www.widget.CommonBuysPop;
import pad.stand.com.haidiyun.www.widget.CommonDiscountPop;
import pad.stand.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.stand.com.haidiyun.www.widget.CommonNum;
import pad.stand.com.haidiyun.www.widget.CommonResPop;
import pad.stand.com.haidiyun.www.widget.CommonSelectSuitPop;
import pad.stand.com.haidiyun.www.widget.CommonSnyDataPop;
import pad.stand.com.haidiyun.www.widget.CommonSuitPop;
import pad.stand.com.haidiyun.www.widget.CommonTablesPop;
import pad.stand.com.haidiyun.www.widget.CommonTzPop;
import pad.stand.com.haidiyun.www.widget.HomeSelecterPeoDialog;
import pad.stand.com.haidiyun.www.widget.LazyGridView;
import pad.stand.com.haidiyun.www.widget.LazyGridView.OnScrollBottomListener;
import pad.stand.com.haidiyun.www.widget.MarqueeText;
import pad.stand.com.haidiyun.www.widget.NewDataToast;
import pad.stand.com.haidiyun.www.widget.RecyclerImageView;
import pad.stand.com.haidiyun.www.widget.SettingMM1Dialog;
import pad.stand.com.haidiyun.www.widget.T_Image;

import static com.zc.http.okhttp.log.LoggerInterceptor.TAG;

@SuppressLint("ValidFragment")
public class OrderActivity extends Fragment {
    private ImageView buyView;
    private AutoWrapLinearLayout add_view;
    private ImageView add_dl;
    private FrameLayout fl;
    private ListView home_lv_menu;
    private MenusAdapter menusAdapter;
    private FoodsAdapter foodAdapter;
    private RelativeLayout search_bg;
    private LazyGridView f_h_com_gv;
    private GridView search_;
    private TextView home_tv_food_count;
    private FrameLayout animation_viewGroup;
    private LinearLayout content, search_layout, ll_all;
    private RelativeLayout title, ll_person;
    private static SharedUtils sharedUtils;
    private SearchAdapter searchAdapter;
    private TextView search_view, call_advert, call_waiter, call_table;
    private ImageView sear_del, iv_language;
    private EditText et_search;
    private MarqueeText tv_tip;
    private static OrderHandler handler;
    private boolean isTishi = false;
    /**
     * 滑动监听
     *
     * @author Administrator
     */
    private Activity activity;
    private Handler parentHandler;
//    private ServiceReceiver servicereceiver;

    public OrderActivity(Activity activity, Handler parentHandler) {
        this.activity = activity;
        this.parentHandler = parentHandler;
    }
    private int CLICK_COL = 0;
    class buy extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            P.c("buy===="+action);
                    if (action.equals("buy.free")){
                        CLICK_COL = R.drawable.home_btn_yid_press;
                        buyView.setImageResource(CLICK_COL);
                    }else   if (action.equals("buy.doing")){
                        CLICK_COL = R.drawable.home_btn_yid_en;
                        buyView.setImageResource(CLICK_COL);
                    }else   if (action.equals("buy.end")){

                        CLICK_COL = R.drawable.home_btn_yid_fr;
                        buyView.setImageResource(CLICK_COL);
                    }else if(action.equals("buy.un")){
                        CLICK_COL = R.drawable.home_btn_yid_normal;
                        buyView.setImageResource(CLICK_COL);
                    }
        }
    }

    private buy b;
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intentService = new Intent();
        intentService.setAction("pad.com.visible");
        activity.sendBroadcast(intentService);
        handler = new OrderHandler(OrderActivity.this);
        b = new buy();
        IntentFilter filter = new IntentFilter();
        filter.addAction("buy.free");
        filter.addAction("buy.doing");
        filter.addAction("buy.end");
        filter.addAction("buy.un");
        getContext().registerReceiver(b,filter);
               init(view);


        // 如果不存在二级选项那么重新点选
        initMenu();
        dataHandler.post(runnable);
        down();
        P.c("执行这里///////////////////");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.oreder_layout, container, false);
        return view;
    }

    private CommonLoadSendPop loadSendPop;
    private int LOAD_NUMBER = 10; // 每次获取多少条数据
    private int LOAD_NUMBER_ALL = 200; // 每次获取多少条数据
    private volatile int MAX_PAGE = 0; // 总共有多少页
    private boolean loadfinish = true; // 指示数据是否加载完成
    private OnScrollBottomListener bottomListener = new OnScrollBottomListener() {
        @Override
        public void onScrollBottom() {
            P.c("============================================================");
            int lastItemid = f_h_com_gv.getLastVisiblePosition(); // 获取当前屏幕最后Item的ID
            P.c("最后ID" + lastItemid);
            /*
             * P.c("onScroll(firstVisibleItem=" + firstVisibleItem +
			 * ",visibleItemCount=" + visibleItemCount + ",totalItemCount=" +
			 * totalItemCount +"lastItemid"+lastItemid + ")");
			 */
            int totalItemCount = foodAdapter.getCount();
            int cp = totalItemCount % LOAD_NUMBER == 0 ? totalItemCount
                    / LOAD_NUMBER : totalItemCount / LOAD_NUMBER + 1;
            // P.c("currentpage"+cp+"  MAX_PAGE"+MAX_PAGE+"  totalItemCount"+totalItemCount+"   lastItemid"+lastItemid);
            if ((lastItemid + 1) == totalItemCount) { // 达到数据的最后一条记录
                if (totalItemCount > 0) {
                    // 当前页
                    int currentpage = totalItemCount % LOAD_NUMBER == 0 ? totalItemCount
                            / LOAD_NUMBER
                            : totalItemCount / LOAD_NUMBER + 1;
                    final int nextpage = currentpage + 1; // 下一页
                    P.c(MAX_PAGE + "==" + currentpage);
                    if (nextpage <= MAX_PAGE && loadfinish) {
                        loadfinish = false;
                        // 开一个线程加载数据
                        // P.c("下一页"+nextpage);
                        /*
                         * if(loadSendPop==null){ loadSendPop = new
						 * CommonLoadSendPop(OrderActivity.this, "加载数据");
						 * loadSendPop.showSheet(true); }
						 */
                        // 取消加载框
                        new Thread() {
                            public void run() {
                                //
                                P.c("加载--");
                                if (search_.getVisibility() == View.GONE) {
                                    if (!selectChildTag.equals("")) {

                                        try {
                                            String menuCode = childMenuBeans
                                                    .get(Integer
                                                            .parseInt(selectChildTag))
                                                    .getCode();
                                            P.c("加载小类分页" + menuCode);
                                            DB.getInstance()
                                                    .getChildFoods(
                                                            foodsBeans,
                                                            menuCode,
                                                            LOAD_NUMBER,
                                                            ((nextpage - 1) * LOAD_NUMBER));
                                        } catch (Exception e) {
                                            P.c("二级分类有误");
                                        }

                                    } else {
                                        P.c("加载大类分页");
                                        DB.getInstance().getFoods(
                                                foodsBeans,
                                                menuBeans.get(selectMenu)
                                                        .getCode(),
                                                LOAD_NUMBER,
                                                ((nextpage - 1) * LOAD_NUMBER));
                                    }

                                } else {
                                    P.c("搜索");
                                    String rs = search.toString()
                                            .replace("[", "%")
                                            .replaceAll("]", "%")
                                            .replaceAll(",", "%")
                                            .replaceAll(" ", "");

                                    DB.getInstance().getSearchFoods(foodsBeans,
                                            rs, LOAD_NUMBER,
                                            ((nextpage - 1) * LOAD_NUMBER));
                                }
                                // P.c("从第"+(((nextpage-1)*LOAD_NUMBER))+"加载");
                                handler.sendEmptyMessage(4);
                            }

                            ;
                        }.start();

                    }
                }
            }

        }
    };


    /**
     * 发送一次点击广播
     */
    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        activity.sendBroadcast(intent);
    }

    /**
     * 进行一次暂停
     */
    private void pause() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_PAUSE);
        activity.sendBroadcast(intent);
    }

    private static Handler dataHandler = new Handler();

    private void initMenu() {
        if (FileUtils.db_exits()) {
            changeNum();
            new Thread() {
                public void run() {
                    // 固定给一个营业点
                    menuBeans.clear();
                    String site = sharedUtils.getStringValue("sitecode");
                    menuBeans = DB.getInstance().getMenus(1, site);
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }
    }


    /**
     * 点菜界面动画
     *
     * @return
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

    // 动画数量
    private int number = 0;
    // 清理视图
    private boolean isClean = false;
    //
    private int AnimationDuration = 1000;
    private static RequestCall requestCall;

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
                picPoint.getWidth(), picPoint.getHeight() - 50);
        lp.leftMargin = x;
        lp.topMargin = y;
        // view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);
        vg.addView(view);
        return view;
    }

    private void doAnim(PicPoint point) {
        if (!isClean) {
            setAnim(point);
        } else {
            try {
                animation_viewGroup.removeAllViews();
                animation_viewGroup.removeAllViewsInLayout();
                animation_viewGroup.destroyDrawingCache();
                animation_viewGroup.clearAnimation();
                isClean = false;
                setAnim(point);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isClean = true;
            }
        }
    }

    private DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(0).showImageOnFail(0)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();

    private void setAnim(PicPoint picPoint) {

		/*Glide.with(BaseApplication.application).load(picPoint.getPic())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)
				.into(iview);*/
        final RecyclerImageView iview = new RecyclerImageView(activity);
        ImageLoader.getInstance().displayImage(picPoint.getPic(),
                iview,
                options);

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
        home_tv_food_count.getLocationInWindow(end_location);
        int endX = end_location[0] - start_location[0];
        int endY = end_location[1] - start_location[1];
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

                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                number--;
                if (number == 0) {
                    isClean = true;
                    handler.sendEmptyMessage(-1);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }

        });
        view.startAnimation(mAnimationSet);

    }

    /**
     * 沽清
     */
    private static void gu(String ip) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "[{\"TabName\":\"NTORestSaleOutMenu\",\"Timestamp\":0}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestCall = OkHttpUtils
                .postString()
//                .url(U.VISTER(ip, U.URL_DOWNLOAD_DATA))
                .url(U.VISTER(ip, U.URL_GUQING))
                // .addParams("data",
                // "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        requestCall.execute(guCallback);
        P.c("提交沽清查询");
    }

    private static StringCallback guCallback = new StringCallback() {
        @Override
        public void onResponse(final String response, int id) {
             P.c("沽清结果"+response);
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(FileUtils.formatJson(response),
                                "UI沽清结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "UI沽清结果");
                    }
                }
            }.start();
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
                            handler.sendEmptyMessage(5);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }
                }

                ;
            }.start();

        }

        @Override
        public void onError(Call call, Exception e, int id) {

            FileUtils.writeLog("访问异常", "UI沽清结果");
        }
    };

    int FOOD_COUNT = 0;


    static Runnable runnable = new Runnable() {

        @Override
        public void run() {
            table_people.setText(String.valueOf(sharedUtils
                    .getIntValue("person")));
            P.c("-----------order--自动检测更新------------");
            String ip = sharedUtils.getStringValue("IP");

            if (ip.length() != 0) {
                gu(ip);
            }
            dataHandler.postDelayed(this, 1000 * 10);
        }
    };


    // 进桌台
    private final int ENTER_TABLE_VAL = 1;
    // 进行多价格,多价格不选择做法，做法需要单独再选择
    private final int ENTER_PRICE_VAL = 2;
    private final int ENTER_BUY_VAL = 3;
    private final int ENTER_WAITER_ORDER_VAL = 4;
    private final int ENTER_TABLE_ = 5;
    private final int ORDER_ZENG = 6;
    private final int ORDER_TUI = 7;
    private final int ORDER_PRINT = 8;

    // private AdAdapter adAdapter;
    private Bitmap showRl_bitmap;
    private ArrayList<Button> btnsList = new ArrayList<Button>();
    private long lastClick, waiteClick;
    private    CommonDiscountPop pop;

    private class OrderHandler extends Handler {
        WeakReference<OrderActivity> mLeakActivityRef;

        public OrderHandler(OrderActivity leakActivity) {
            mLeakActivityRef = new WeakReference<OrderActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {

                switch (msg.what) {
                    case 66:
                       /* if (!OrderActivity.this.isHidden()) {
                            pop = new CommonDiscountPop(activity);
                            pop.showSheet();
                        }*/
                    if(pop==null){

                        pop = new CommonDiscountPop(activity);
                        pop.dis(new SetI() {
                            @Override
                            public void click() {

                            }
                        });
                        pop.showSheet();
                    }


                        break;
                    case 116:
                        search.clear();
                        search_view.setText("");
                        break;
                    case 113:
                        //处理沽清
                        String regCode = sharedUtils.getStringValue("regCode");
                        String table = sharedUtils.getStringValue("table_code");
                        String dishCode = (String) msg.obj;
                        Map<String, String> pa0 = new HashMap<String, String>();
                        // pa0.put("method","InsertSoldout");
                        pa0.put("tableNo", table);
                        pa0.put("siteCode", regCode);
                        pa0.put("menuCode", dishCode);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("tableNo", table);
                            jsonObject.put("siteCode", regCode);
                            jsonObject.put("menuCode", dishCode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //////////////////////
                        pa0.put("tableNo", table);
                        pa0.put("siteCode", regCode);
                        pa0.put("menuCode", dishCode);
                        String param = "{\"tableNo\":\"" + table + "\",\"siteCode\":\"" + regCode + "\",\"menuCode\":\"" + dishCode + "\"}";
                        OkHttpUtils.post().url("http://www.haidiyun.top/ntoreportONline/ajax/Ajax_LoginAction?method=InsertSoldout").addParams("obj", param).build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                P.c("报错");
                                try {
                                    P.c(e.getMessage());
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                P.c("沽清提交" + response);
                            }
                        });


                        break;
                    case 112:
                        picMoveT.gq();
                        break;
                    case -11:
                        int person = msg.arg1;
                        table_code
                                .setText(sharedUtils.getStringValue("table_name"));
                        table_people.setText(String.valueOf(person));
                        // 刷新菜单
                        initMenu();
                        if (person == 0) {
                            Intent intent = new Intent();
                            intent.setAction(Common.SERVICE_ACTION);
                            intent.putExtra("free_table", "");
                            intent.setPackage(BaseApplication.packgeName);
                            activity.startService(intent);
                            //人数在下单时候选择
//                            selectPeople();
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(Common.SERVICE_ACTION);
                            intent.putExtra("open_table", "");
                            intent.setPackage(BaseApplication.packgeName);
                            activity.startService(intent);
                        }
                        break;
                    case -10:
                        int ct = msg.arg1;
                        if (sharedUtils.getBooleanValue("is_advert")) {
                            if (ct == 0) {
                                isTishi = true;
                            }
                            if (ct == 1 && isTishi) {
                                //如果是1或者提示为空那么就产生
                                isTishi = false;
                                CommonAdvertPop advertPop = new CommonAdvertPop(activity);
                                advertPop.showSheet();

                            }
                        }
                        home_tv_food_count.setText(String.valueOf(ct));
                        break;
                    case -9:
                        try {
                            int arg2 = msg.arg1;
                            // adAdapter.selectPosition(arg2);
                            loadChildDatas(arg2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case -8:
                        NewDataToast.makeTextD("已添加到点餐栏", 500);
                        changeNum();
                        dishLs();
                        break;
                    case -66:
                        NewDataToast.makeText(getResources().getString(R.string.hasorderyet0));
                        break;
                    case -6:
                        NewDataToast.makeText(getResources().getString(R.string.hasorderyet));
                        break;
                    case -7:
                        NewDataToast.makeText("超出限额");
                        break;
                    case 2:
                        showRl_bitmap = ((BitmapDrawable) (buyView.getDrawable()))
                                .getBitmap();
                        buyView.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View arg0, MotionEvent arg1) {


                                if (MotionEvent.ACTION_DOWN == arg1.getAction()) {
                                    if (showRl_bitmap.getPixel((int) (arg1.getX()),
                                            ((int) arg1.getY())) == 0) {
                                        return false;// 透明区域返回true
                                    } else {
                                        if (System.currentTimeMillis() - lastClick <= 1000) {
                                            return true;
                                        }
                                        lastClick = System.currentTimeMillis();
                                        buyView.setImageResource(R.drawable.home_btn_yid_pre);
                                        int screen[] = getScreens();
                                        // 保存长宽
                                        buysPop = new CommonBuysPop(
                                                activity, confirm,
                                                dishChange, cancelListener, screen,
                                                sharedUtils, tz, false, table_people);
                                        buysPop.showSheet();

                                        return true;// 透明区域返回true
                                    }
                                } else if (MotionEvent.ACTION_MOVE == arg1
                                        .getAction()) {
                                    return false;// 透明区域返回true
                                } else if (MotionEvent.ACTION_UP == arg1
                                        .getAction()) {
                                    if(CLICK_COL!=0){
                                        buyView.setImageResource(CLICK_COL);
                                    }else{
                                        buyView.setImageResource(R.drawable.home_btn_yid_normal);
                                    }

                                }

                                return false;
                            }
                        });
                        break;
                    case -1:
                        try {
                            animation_viewGroup.removeAllViews();
                            animation_viewGroup.removeAllViewsInLayout();
                            animation_viewGroup.destroyDrawingCache();
                            animation_viewGroup.clearAnimation();

                        } catch (Exception e) {
                        }
                        isClean = false;
                        break;
                    case 0:
                        // 暂时默认给个0
                        menusAdapter.updata(menuBeans);
                        select(0);
                        break;
                    case 1:
                        f_h_com_gv.smoothScrollToPositionFromTop(0, 0);
                        // 更新菜品
                        FOOD_COUNT = foodsBeans.size();
                        // int screen0[] = getScreens();
                        // 移到绘制二级
                        // foodAdapter.updata(foodsBeans);
                        // 更新二级分类
                        if (childMenuBeans == null) {
                            ref();
                            return;
                        }
                        if (FOOD_COUNT == 0) {
                            home_lv_menu.setEnabled(true);
                        }

                        if (childMenuBeans.size() == 0) {
                            add_view.setVisibility(View.GONE);
                            add_dl.setVisibility(View.GONE);
                        } else {

                            if (childMenuBeans.size() == 1) {
                                add_view.setVisibility(View.GONE);
                                add_dl.setVisibility(View.GONE);
                            } else {
                                add_view.setVisibility(View.VISIBLE);
                            }
                            handler.sendEmptyMessage(3);
                            // adAdapter.updata(childMenuBeans);
                            // setListViewHeightBasedOnChildren(add_view);
                            // adAdapter.notifyDataSetChanged();
                            // add_view.post(new Runnable() {
                            // @Override
                            // public void run() {
                            //
                            // //
                            // P.c(add_view.getMeasuredHeight()+"--"+add_view.getHeight());
                            // FrameLayout.LayoutParams params = new
                            // FrameLayout.LayoutParams(
                            // FrameLayout.LayoutParams.MATCH_PARENT, add_view
                            // .getMeasuredHeight()+30);
                            // add_dl.setLayoutParams(params);
                            // add_dl.setVisibility(View.VISIBLE);
                            // ref();
                            // }
                            // });
                            // changeNum();
                            // 释放home_lv_menu 点击
                            // home_lv_menu.setEnabled(true);
                            // add_view.post(new Runnable() {
                            // @Override
                            // public void run() {
                            //
                            // handler.sendEmptyMessage(3);
                            // }
                            // });
                        }
                        break;
                    case 4:
                        if (loadSendPop != null) {
                            loadSendPop.cancle();
                            loadSendPop = null;
                        }
                        loadfinish = true; // 加载完成
                        //
                        FOOD_COUNT = foodsBeans.size();
                        foodAdapter.updata(foodsBeans);
                        changeNum();
                        break;

                    case 7:
                        f_h_com_gv.smoothScrollToPositionFromTop(0, 0);
                    case 5:
                        // 只更新菜品栏
                        // int screen1[] = getScreens();
                        FOOD_COUNT = foodsBeans.size();
                        foodAdapter.updata(foodsBeans);
                        changeNum();
                        break;
                    case 6:
                        dishLs();
                        break;
                    case 3:
                        if (add_view.getChildCount() != 0) {
                            ref();
                            return;
                        }
                        final int len = childMenuBeans.size();
                        // 进行二级分类操作
                        // final Button[] btns = new Button[len];
                        btnsList.clear();
                        // len
                        for (int i = 0; i < len; i++) {
                            final Button btn = new Button(activity);
                            LayoutParams pa = new LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT);
                            pa.setMargins(2, 4, 2, 4);
                            btn.setLayoutParams(pa);


                            if (sharedUtils.getBooleanValue("is_lan")) {
                                //变为英文
                                btn.setText(childMenuBeans.get(i).getName_en());
                            } else {
                                btn.setText(childMenuBeans.get(i).getName());
                            }

                            btn.setPadding(12, 5, 12, 5);
                            btn.setTag(Integer.valueOf(i));
                            btn.setTextColor(R.color.ban);
                            btn.setTextSize(16);
                            btn.setBackgroundResource(R.drawable.shape_common_cwhite);
                            // btns[i] = btn;
                            btnsList.add(btn);
                            // add_view.addView(btns[i]);
                            add_view.addView(btnsList.get(i));
                        }
                        // loadChildDatasInit(len);
                        // 对子类控件进行事件编写
                        for (int i = 0; i < len; i++) {
                            final Button btn = btnsList.get(i);
                            btn.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    selectChildTag = btn.getTag().toString();
                                    //
                                    loadChildDatas(len);
                                }
                            });
                        }
                        add_view.post(new Runnable() {
                            @Override
                            public void run() {

                                // P.c(add_view.getMeasuredHeight()+"--"+add_view.getHeight());
                                if (childMenuBeans.size() > 1) {
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                            FrameLayout.LayoutParams.MATCH_PARENT,
                                            add_view.getMeasuredHeight());
                                    add_dl.setLayoutParams(params);
                                    add_dl.setVisibility(View.VISIBLE);
                                }
                                ref();
                            }
                        });
                        changeNum();
                        // 释放home_lv_menu 点击
                        menusAdapter.selectPosition(selectMenu);
                        home_lv_menu.setEnabled(true);
                        break;
                    case 8:
                        try {
                            ImageBDInfo bdInfo = new ImageBDInfo();
                            int index = msg.arg1;
                            int count = f_h_com_gv.getChildCount();
                            int pik = f_h_com_gv.getFirstVisiblePosition();
                            // 倍数
                            P.c(index + "--" + count + "点击" + index % showLine
                                    % count + "firstVisiblePosition");
                            T_Image c = (T_Image) ((FrameLayout) ((LinearLayout) ((LinearLayout) f_h_com_gv
                                    .getChildAt((index - pik) % count))
                                    .getChildAt(0)).getChildAt(0)).getChildAt(0);
                            int xy[] = getLocation(c);
                            bdInfo.width = c.getWidth();
                            bdInfo.height = c.getHeight();
                            bdInfo.x = xy[0];
                            bdInfo.y = xy[1];
                            Intent intent = new Intent(activity,
                                    ShowImages.class);
                            intent.putExtra("data", (Serializable) imageInfos);
                            intent.putExtra("bdinfo", bdInfo);
                            intent.putExtra("showLine", showLine);
                            intent.putExtra("type", 2);
                            intent.putExtra("index", (index - pik) % count);
                            intent.putExtra("txt", (String) msg.obj);
                            startActivity(intent);
                            bdInfo = null;
                        } catch (Exception e) {
                            P.c(f_h_com_gv.getChildCount() + "错误" + msg.arg1);
                        }

                        break;
                    case 11:
                        NewDataToast.makeText("获取数据异常");
                        break;
                    case 12:
                        NewDataToast.makeText((String) msg.obj);
                        break;
                    case 100:
                        NewDataToast.makeText(getResources().getString(R.string.languagetip));
                        break;

                    default:
                        break;
                }

            }
        }
    }

    // View宽，高
    public int[] getLocation(View v) {
        int[] loc = new int[4];
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        loc[0] = location[0];
        loc[1] = location[1];
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);

        loc[2] = v.getMeasuredWidth();
        loc[3] = v.getMeasuredHeight();

        // base = computeWH();
        return loc;
    }

    public void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }

    private void ref() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                foodsBeans.clear();
                String menuCode = menuBeans.get(selectMenu).getCode();
                //菜类大类
                DB.getInstance().getFoods(foodsBeans, menuCode, LOAD_NUMBER, 0);
                handler.sendEmptyMessage(5);
            }
        }.start();
    }

    /*
     * private void loadChildDatas(final int select){ new Thread() { public void
     * run() { try {
     *
     * foodsBeans.clear(); String menuCode = childMenuBeans .get(select)
     * .getCode(); int count = DB.getInstance() .getChildCounts(menuCode);
     * MAX_PAGE = count % LOAD_NUMBER != 0 ? (count / LOAD_NUMBER) + 1 : count /
     * LOAD_NUMBER; P.c(MAX_PAGE+"二级菜单"+count); foodsBeans = DB.getInstance()
     * .getChildFoods( menuCode, LOAD_NUMBER, 0); handler.sendEmptyMessage(7);
     *
     * } catch (Exception e) { } }; }.start(); }
     */
    private void loadChildDatas(int len) {
        if (!selectChildTag.equals("")) {
            for (int j = 0; j < len; j++) {
                ((Button) add_view.getChildAt(j)).setTextColor(0x65000000);
                if (String.valueOf(j).equals(selectChildTag)) {
                    ((Button) add_view.getChildAt(j))
                            .setTextColor(getResources().getColor(R.color.red));
                }
            }
            // 分页展示，加载二级菜单数据
            new Thread() {
                public void run() {
                    try {

                        foodsBeans.clear();
                        String menuCode = childMenuBeans.get(
                                Integer.parseInt(selectChildTag)).getCode();
                        int count = DB.getInstance().getChildCounts(menuCode);

                        MAX_PAGE = count % LOAD_NUMBER != 0 ? (count / LOAD_NUMBER) + 1
                                : count / LOAD_NUMBER;
                        P.c(MAX_PAGE + "二级菜单" + count);
                        //菜类小类
                        foodsBeans = DB.getInstance().getChildFoods(menuCode,
                                LOAD_NUMBER_ALL, 0);
//                        process();
                        handler.sendEmptyMessage(7);

                    } catch (Exception e) {
                    }
                }
            }.start();
            // 全部展示二级菜单数据模式
        }
    }

    private volatile String selectChildTag = "";

    private int[] getScreens() {
        int screen[] = new int[2];
        // 保存长宽
        screen[0] = ll_all.getMeasuredWidth();
        screen[1] = ll_all.getMeasuredHeight();
        return screen;
    }

    private DishChange dishChange = new DishChange() {

        @Override
        public void change() {

            changeNum();
        }
    };
    private OnDismissListener cancelListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface arg0) {

            down();
            P.c("隐藏菜篮子");
            dishLs();
            buysPop = null;
        }
    };
    private CommonTzPop tzPop;
    private Tz tz = new Tz() {
        @Override
        public void tuicai(DishTableBean obj) {
            tzPop = new CommonTzPop(activity, "退菜", true, obj, conf);
            tzPop.showSheet();

        }

        @Override
        public void zengsong(DishTableBean obj) {
            tzPop = new CommonTzPop(activity, "赠菜", false, obj, conf);
            tzPop.showSheet();
        }
    };
    private TzC conf = new TzC() {

        @Override
        public void tuicai(DishTableBean obj, String num, String tag) {
//            if (tag != null) {


            if(sharedUtils.getStringValue("user").length()==0){
                Intent intent = new Intent(activity,
                        CardValActivity.class);
                intent.putExtra("obj", obj);
                intent.putExtra("num", num);
                intent.putExtra("tag", tag);
                startActivityForResult(intent, ORDER_TUI);
            }else{


                if (loadSendPop == null) {
                    loadSendPop = new CommonLoadSendPop(activity, "正在退菜");
                    loadSendPop.showSheet(false);
                }

                String optName = sharedUtils.getStringValue("user");

                tui(optName, num, obj, tag);

            }
//            }

        }

        @Override
        public void zengsong(DishTableBean obj, String num, String tag) {


            if(sharedUtils.getStringValue("user").length()==0){
                Intent intent = new Intent(activity,
                        CardValActivity.class);
                intent.putExtra("obj", obj);
//            intent.putExtra("num", Integer.parseInt(num));
                intent.putExtra("num", num);
                intent.putExtra("tag", tag);
                startActivityForResult(intent, ORDER_ZENG);
            }else{
                if (loadSendPop == null) {
                    loadSendPop = new CommonLoadSendPop(activity, "正在赠菜");
                    loadSendPop.showSheet(false);
                }
                String optName = sharedUtils.getStringValue("user");
                P.c("这里执行");
                zeng(optName, num, obj, tag);
            }



        }
    };
    private CommonBuysPop buysPop;
    private ArrayList<MenuBean> menuBeans;
    private ArrayList<FoodsBean> foodsBeans;
    private ArrayList<MenuBean> childMenuBeans;
    // private Typeface tf;
    private TextView table_code;
    private static TextView table_people, table_tip;

    /**
     * 选择一个主类
     *
     * @param index
     */
    private int selectMenu;

    private void select(final int index) {
//        home_lv_menu.setEnabled(false);
        home_lv_menu.setEnabled(true);
        this.selectMenu = index;
        selectChildTag = "";// 还原二级菜单选项
        // adAdapter.selectPosition(-1);
        if (sharedUtils.getBooleanValue("is_giv")) {
            showLine = 3;
        } else {
            showLine = 4;
        }
        if (sharedUtils.getBooleanValue("is_txt")) {
            //文字模式
            showLine = 2;
        }
        f_h_com_gv.setNumColumns(showLine);
        add_view.removeAllViews();
        add_view.removeAllViewsInLayout();
        add_view.destroyDrawingCache();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        add_dl.setLayoutParams(params);
        add_dl.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                try {
                    foodsBeans.clear();
                    String menuCode = menuBeans.get(index).getCode();
                    int count = DB.getInstance().getCounts(menuCode);
                    MAX_PAGE = count % LOAD_NUMBER != 0 ? (count / LOAD_NUMBER) + 1
                            : count / LOAD_NUMBER;
                    P.c(MAX_PAGE + "类别code" + menuCode + "数量" + count + "获取了"
                            + foodsBeans.size());
                    childMenuBeans = DB.getInstance().getChildMenuBeans(
                            menuCode);
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    /**
     * 查询菜品
     *
     * @param search
     */
    private void get(final String search, final boolean word) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                foodsBeans.clear();
                int count = DB.getInstance().getSearchCounts(search, word);
                MAX_PAGE = count % LOAD_NUMBER != 0 ? (count / LOAD_NUMBER) + 1
                        : count / LOAD_NUMBER;
                foodsBeans = DB.getInstance().getSearchFoods(search,
                        LOAD_NUMBER, 0, word);
                handler.sendEmptyMessage(7);
            }
        }.start();
    }

    private CommonTablesPop tablesPop;
    private String[] zm;
    private ArrayList<String> search = new ArrayList<String>();
    int showLine = 0;
    private boolean isWord = true;
    private boolean state = true;
    LoadBuy loadBuy = new LoadBuy() {
        @Override
        public void success(String optName) {

        }

        @Override
        public void waiter(String optName) {

        }

        @Override
        public void lt() {

        }
    };



    private void init(View view) {
        zm = new String[]{"中", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        sharedUtils = new SharedUtils(Common.CONFIG);

        // tf = Typeface.createFromAsset(getAssets(), "font/mb.ttf");
        table_code = (TextView) view.findViewById(R.id.table_code);
        ll_all = (LinearLayout) view.findViewById(R.id.ll_all);
        table_people = (TextView) view.findViewById(R.id.table_people);
        table_tip = (TextView) view.findViewById(R.id.table_tip);
        table_tip = (TextView) view.findViewById(R.id.table_tip);

        search_bg = (RelativeLayout) view.findViewById(R.id.search_bg);
        call_waiter = (TextView) view.findViewById(R.id.call_waiter);
        call_advert = (TextView) view.findViewById(R.id.call_advert);
        call_table = (TextView) view.findViewById(R.id.call_table);

        // table_code.setTypeface(tf);
        // call_waiter.setTypeface(tf);
        content = (LinearLayout) view.findViewById(R.id.content);
//        ll_person = (RelativeLayout) view.findViewById(R.id.ll_person);
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        title = (RelativeLayout) view.findViewById(R.id.title);
        home_tv_food_count = (TextView) view.findViewById(R.id.home_tv_food_count);
        add_view = (AutoWrapLinearLayout) view.findViewById(R.id.add_view);
        add_dl = (ImageView) view.findViewById(R.id.add_dl);
        fl = (FrameLayout) view.findViewById(R.id.fl);
        buyView = (ImageView) view.findViewById(R.id.home_rl_showMenu);
        home_lv_menu = (ListView) view.findViewById(R.id.home_lv_menu);
        f_h_com_gv = (LazyGridView) view.findViewById(R.id.f_h_com_gv);
        f_h_com_gv.setOnScrollBottomListener(bottomListener);
        f_h_com_gv.setPadding(4, 4, 4, 4);
        search_view = (TextView) view.findViewById(R.id.search_view);
        et_search = (EditText) view.findViewById(R.id.et_search);
        tv_tip = (MarqueeText) view.findViewById(R.id.tv_tip);
        search_ = (GridView) view.findViewById(R.id.search_);
        sear_del = (ImageView) view.findViewById(R.id.sear_del);
        iv_language = (ImageView) view.findViewById(R.id.iv_language);
        menuBeans = new ArrayList<MenuBean>();
        foodsBeans = new ArrayList<FoodsBean>();
        childMenuBeans = new ArrayList<MenuBean>();
        foodAdapter = new FoodsAdapter(activity, foodsBeans,
                picMoveT, handler);
        f_h_com_gv.setAdapter(foodAdapter);
        searchAdapter = new SearchAdapter(activity, zm, isWord);
        search_.setAdapter(searchAdapter);

        menusAdapter = new MenusAdapter(activity, menuBeans);
        home_lv_menu.setAdapter(menusAdapter);




    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }

    private void showInput(EditText et_search) {
        et_search.setFocusable(true);
        et_search.setFocusableInTouchMode(true);
        et_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_search, 0);
    }

    private void selectPeople() {
        HomeSelecterPeoDialog numDl = new HomeSelecterPeoDialog(selectTable,
                table_people, sharedUtils);
        numDl.intiDialog(activity, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isShow = false;
        if (requestCode == ENTER_TABLE_VAL && resultCode == 1000) {
            down();
            if (data.getIntExtra("result", 0) == 1 && data.hasExtra("optName")) {
                String user = data.getStringExtra("optName");
                if (user.equals(sharedUtils.getStringValue("user"))) {
                    NewDataToast.makeText(getResources().getString(R.string.loginclearsuccess));
                    sharedUtils.clear("user");
                    sharedUtils.clear("userName");
                    call_waiter.setText("服务员");
                } else {
                    NewDataToast.makeText(getResources().getString(R.string.loginsuccess));
                    String name = DB.getInstance().getName(user);
                    call_waiter.setText("服务员:" + name);
                    P.c("服务员登录名:" + name + "-->服务员号:" + user);
                    sharedUtils.setStringValue("user", user);
                    sharedUtils.setStringValue("userName", name);
                }
            } else {
                NewDataToast.makeText(getResources().getString(R.string.wrongcheck));
            }

        } else if (requestCode == ENTER_PRICE_VAL && resultCode == 1000) {
            // 时价
            if (data.hasExtra("obj")) {
            /*	CommonPricePop pricePop = new CommonPricePop(
                        OrderActivity.this,
						(FoodsBean) data.getSerializableExtra("obj"), handler);
				pricePop.showSheet();*/
            }
        } else if (requestCode == ENTER_BUY_VAL && resultCode == 1000) {
            // 登录确认操作
            if (oBuy != null) {
                if (data.hasExtra("optName")) {
                    oBuy.success(data.getStringExtra("optName"));
                    sharedUtils.setStringValue("optName", data.getStringExtra("optName"));
                }

            }
            oBuy = null;
        } else if (requestCode == ENTER_WAITER_ORDER_VAL && resultCode == 1000) {
            if (oBuy != null) {
                P.c("服务员辅助点餐");
                if (data.hasExtra("optName")) {
                    String optName = data.getStringExtra("optName");
                    sharedUtils.setStringValue("optName", optName);
                    oBuy.waiter(optName);
                }
            }
            oBuy = null;
        } else if (requestCode == ENTER_TABLE_ && resultCode == 1000) {
            //单独开台
            if (loadSendPop == null) {
                loadSendPop = new CommonLoadSendPop(activity, "正在为您开台");
                loadSendPop.showSheet(false);
            }
            if (data.hasExtra("optName")) {

                String optName = data.getStringExtra("optName");
                P.c("选桌台user:" + optName);
                sharedUtils.setStringValue("optName", optName);
                load(optName);
            }
        } else if (requestCode == ORDER_TUI && resultCode == 1000) {
            //退菜
            if (loadSendPop == null) {
                loadSendPop = new CommonLoadSendPop(activity, "正在退菜");
                loadSendPop.showSheet(false);
            }

            String optName = data.getStringExtra("optName");
            String tag = data.getStringExtra("tag");
            DishTableBean obj = (DishTableBean) data.getSerializableExtra("obj");
            String num = data.getStringExtra("num");
            tui(optName, num, obj, tag);

        } else if (requestCode == ORDER_ZENG && resultCode == 1000) {
            //赠送
            if (loadSendPop == null) {
                loadSendPop = new CommonLoadSendPop(activity, "正在赠菜");
                loadSendPop.showSheet(false);
            }
            String optName = data.getStringExtra("optName");
            String tag = data.getStringExtra("tag");
            DishTableBean obj = (DishTableBean) data.getSerializableExtra("obj");
            String num = data.getStringExtra("num");
            P.c("这里执行00");
            zeng(optName, num, obj, tag);
        }
    }

    private RequestCall tuiRequestCall;

    private void tui(String optName, String num, DishTableBean obj, String reason) {
        String ip = sharedUtils.getStringValue("IP");
        String order = sharedUtils.getStringValue("billId");
        if (order.length() == 0) {
            NewDataToast.makeText("订单异常");
            return;
        }
        String post = "{\"BillNo\":\"" + order + "\",\"SerialNo\":\"" + obj.getI() + "\",\"Number\":" + num + ",\"Reason\":\"" + reason + "\",\"UserName\":\"" + optName + "\"}";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", post);
            tuiRequestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_TUI))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            P.c("退菜:" + jsonObject.toString());
            tuiRequestCall.execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    P.c("退菜。。" + e.getLocalizedMessage());
                    handler.sendEmptyMessage(11);
                    if (tzPop != null) {
                        tzPop.close();
                        tzPop = null;
                    }
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        ;
                        loadSendPop = null;
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    if (tuiRequestCall != null) {
                        tuiRequestCall.cancel();
                    }
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        loadSendPop = null;
                    }
                    if (tzPop != null) {
                        tzPop.close();
                        tzPop = null;
                    }
                    try {
                        JSONObject object = new JSONObject(FileUtils.formatJson(response));
                        if (object.getBoolean("Success")) {
                            NewDataToast.makeText(getResources().getString(R.string.backsuccess));
                            //回到已点单页面
                            int screen[] = getScreens();
                            CommonBuysPop buysPop = new CommonBuysPop(
                                    activity, confirm,
                                    dishChange, cancelListener, screen,
                                    sharedUtils, tz, true, table_people);
                            buysPop.showSheet();
                        } else {
                            Message msg = new Message();
                            msg.obj = object.getString("Data");
                            msg.what = 12;
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    //赠送
    private RequestCall zengRequestCall;

    private void zeng(String optName, String num, final DishTableBean obj, final String reason) {
        String ip = sharedUtils.getStringValue("IP");
        String order = sharedUtils.getStringValue("billId");
        if (order.length() == 0) {
            NewDataToast.makeText("订单异常");
            return;
        }
//        String post = "{\"BillNo\":\"" + order + "\",\"SerialNo\":\"" + obj.getI() + "\",\"Number\":" + num + ",\"UserName\":\"" + optName + "\"}";
        String post = "{\"BillNo\":\"" + order + "\",\"SerialNo\":\"" + obj.getI() + "\",\"Number\":" + num + ",\"Reason\":\"" + reason + "\",\"UserName\":\"" + optName + "\"}";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", post);
            zengRequestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_ZENG))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            P.c("赠菜:" + jsonObject.toString());
            zengRequestCall.execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    handler.sendEmptyMessage(11);
                    if (tzPop != null) {
                        tzPop.close();
                        tzPop = null;
                    }
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        ;
                        loadSendPop = null;
                    }
                }

                @Override
                public void onResponse(String response, int id) {
                    P.c("赠菜结果"+response);
                    if (zengRequestCall != null) {
                        zengRequestCall.cancel();
                    }
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                        ;
                        loadSendPop = null;
                    }
                    if (tzPop != null) {
                        tzPop.close();
                        tzPop = null;
                    }
                    try {
                        JSONObject object = new JSONObject(FileUtils.formatJson(response));
                        if (object.getBoolean("Success")) {
                            NewDataToast.makeText(getResources().getString(R.string.giftsuccess));

                        } else {
                            Message msg = new Message();
                            msg.obj = object.getString("Data");
                            msg.what = 12;
                            handler.sendMessage(msg);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }


    private RequestCall tableCall;

    private void load(String optName) {
        String ip = sharedUtils.getStringValue("IP");
        String tableCode = sharedUtils.getStringValue("table_code");
        int num = sharedUtils.getIntValue("person");
        String sn = sharedUtils.getStringValue("sn");
        String post = "{\"DeviceSerialNo\":\"" + sharedUtils.getStringValue("deviceSerialNo") + "\",\"TableNo\":\"" + tableCode + "\",\"GstCount\":" + num + ",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\"" + optName + "\",\"ClientType\":\"Android\",\"ClientMac\":\"" + FileUtils.getDeviceId() + "\"}";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", post);
            tableCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_TABLE_OPEN))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            P.c(post.toString());
            tableCall.execute(personCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            NewDataToast.makeText("发送失败");
        }
    }

    private StringCallback personCallback = new StringCallback() {

        @Override
        public void onResponse(String response, int id) {
            cancelTable();
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
//                {"d":"{\"Success\":true,\"Data\":\"17110400021\",\"Result\":\"\"}"}
                if (jsonObject.getBoolean("Success")) {

                    String billId = jsonObject.getString("Data");
                    sharedUtils.setStringValue("billId", billId);
                    P.c("成功开台" + billId);
                    //-----通知service更改
                    Intent intent = new Intent();
                    intent.setAction(Common.SERVICE_ACTION);
                    intent.putExtra("open_table", "");
                    activity.startService(intent);
                    // 选择
                    selectPeople();
                } else {
                    NewDataToast.makeText(jsonObject.getString("Data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                NewDataToast.makeTextL("数据异常", 2000);
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            NewDataToast.makeText("连接失败");

            cancelTable();
        }
    };

    private void cancelTable() {
        if (loadSendPop != null) {
            loadSendPop.cancle();
            loadSendPop = null;
        }
        if (tableCall != null) {
            tableCall.cancel();
        }
    }

    /**
     * 服务员确认操作
     */
    private LoadBuy oBuy = null;
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

            Intent intent = new Intent(activity,
                    CardValActivity.class);
            startActivityForResult(intent, ENTER_WAITER_ORDER_VAL);
        }

        ;
    };

    private SelectTable selectTable = new SelectTable() {

        @Override
        public void select(final TableBean bean, String optName, int person) {

            if (bean != null) {
                // 设置名字，保存状态
                Intent intent = new Intent();
                intent.setAction(Common.SERVICE_ACTION);
                intent.setPackage(BaseApplication.packgeName);
                intent.putExtra("recy_table", "");
                activity.startService(intent);
                // 查询一次桌台
                P.c("营业" + Common.SITE_CODE);

                if (Common.SITE_CODE.length() != 0) {
                    sharedUtils.setStringValue("sitecode", Common.SITE_CODE);
                }
                sharedUtils.setStringValue("table_name", bean.getName());
                sharedUtils.setStringValue("table_code", bean.getCode());
                P.c("选桌台order_user:" + optName);
                sharedUtils.setStringValue("optName", optName);
                if(sharedUtils.getBooleanValue("is_gua")){
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            DB.getInstance().resetToDish(bean.getCode());
                        }
                    }.start();
                }

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

    /**
     * 菜品列表
     */
    private void dishLs() {

        new Thread() {
            public void run() {
                process();
            }

            ;
        }.start();
    }

    private ReasonI reasonI = new ReasonI() {

        @Override
        public void select(ArrayList<ReasonBean> beans, DishTableBean bean, String numView) {


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
        public void insert(final ArrayList<ReasonBean> beans,
                           final FoodsBean bean, final String numView) {

            // 点击菜品界面的口味
            /**
             * 最后一个是套餐
             */
            new Thread() {
                public void run() {
                    boolean flag = DB.getInstance().addDishToPad(bean, beans,
                            "", "", "", numView, handler);
                    if (flag) {
                        handler.sendEmptyMessage(-8);
                    }
                }

                ;
            }.start();

        }

        @Override
        public void init(final FoodsBean bean, final String numView) {

            // 点击菜品界面的口味
            new Thread() {
                public void run() {

                    boolean flag = DB.getInstance().addDishToPad(bean, null,
                            "", "", "", numView, handler);
                    if (flag) {
                        handler.sendEmptyMessage(-8);
                    }
                }

                ;

            }.start();

        }

        @Override
        public void init(DishTableBean bean, String numView) {


        }
    };
    // 套餐下单
    private TcT tcT = new TcT() {
        @Override
        public void insert(final FoodsBean foodsBean,
                           final String details, final String detailNames, final String num) {
            // 插入未下单表
            new Thread() {
                public void run() {
                    boolean flag = DB.getInstance().addDishToPad(foodsBean,
                            null, "", details, detailNames, num, handler);
                    if (flag) {
                        handler.sendEmptyMessage(-8);
                    }
                }

                ;
            }.start();

        }
    };

    /**
     * 和二级菜单相关
     *
     * @return
     */
    private boolean initChild() {
        if (selectChildTag.equals("")) {
            return false;
        }
        return true;
    }

    private void process() {
        try {
            if (initChild()) {

                foodsBeans = DB.getInstance().getChildFoods(
                        childMenuBeans.get(Integer.parseInt(selectChildTag))
                                .getCode(), FOOD_COUNT, 0);
                // ------
                // P.c("刷新"+foodAdapter.getCount()+"条数据");
            } else if (search_.getVisibility() == View.VISIBLE) {
                // 是搜索状态
                String rs = search.toString().replace("[", "%")
                        .replaceAll("]", "%").replaceAll(",", "%")
                        .replaceAll(" ", "");

                foodsBeans = DB.getInstance().getSearchFoods(rs, FOOD_COUNT, 0, false);
            } else {
                P.c("selectMenu" + selectMenu);
                if (menuBeans.size() != 0) {
                    foodsBeans = DB.getInstance().getFoods(
                            menuBeans.get(selectMenu).getCode(), FOOD_COUNT, 0);
                }
            }
            handler.sendEmptyMessage(5);
        } catch (IndexOutOfBoundsException e) {

        }

    }

    ArrayList<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
    private PicMoveT picMoveT = new PicMoveT() {

        @Override
        public void view(final FoodsBean obj, final int index) {

            // 显示多图 大图
            new Thread() {
                @Override
                public void run() {

                    super.run();
                    imageInfos.clear();
                    DB.getInstance().getImageInfos(imageInfos, obj.getCode());
                    Message msg = new Message();
                    msg.what = 8;
                    msg.arg1 = index;
                    msg.obj = obj.getDescription();
                    handler.sendMessage(msg);
                }
            }.start();

        }

        @Override
        public void add(final FoodsBean obj) {
            if (check(obj)) {

                new Thread() {
                    public void run() {
                        DB.getInstance().changeNum(obj.getCount() + 1,
                                obj.getCode(), obj.getPrice(), "+", handler);
                        P.c("这里1");
                        // ------
                        process();
                    }
                }.start();
            }
        }

        public boolean check(final FoodsBean obj) {
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
        public void mics(final FoodsBean obj) {
            double limit = obj.getOrderMinLimit();
            //最少数量做限制
            if (obj.getCount() - 1 <= 0 && obj.getCount() > 0) {
                // 删除菜篮子
                new Thread() {
                    public void run() {
                        DB.getInstance().delete(obj.getCode(), obj.getPrice());
                        // ------
                        process();
                    }
                }.start();

            } else {
                double ddd = obj.getCount();
                double sss = limit;//3
                if (obj.getCount() > limit) {
                    new Thread() {
                        public void run() {
                            DB.getInstance().changeNum(obj.getCount() - 1,
                                    obj.getCode(), obj.getPrice(), "-", handler);
                            // ------
                            //
                            process();
                        }

                        ;
                    }.start();
                } else {
                    NewDataToast.makeText(getResources().getString(R.string.minorder) + limit);
                }
            }
        }

        @Override
        public void addP(PicPoint point) {

            if (point != null) {
                doAnim(point);
            }
            P.c("这里4");
            changeNum();
            dishLs();
        }

        @Override
        public void sj(FoodsBean obj) {

            // 时价
            Intent intent = new Intent(activity,
                    CardValActivity.class);
            intent.putExtra("obj", obj);
            startActivityForResult(intent, ENTER_PRICE_VAL);
        }

        @Override
        public void gq() {

            NewDataToast.makeText("已沽清");
        }

        @Override
        public void rb(FoodsBean obj) {
            // 口味选择下单
            CommonResPop resPop = new CommonResPop(activity, reasonI,
                    null, obj);
            resPop.showSheet();
        }

        @Override
        public void tc(FoodsBean obj) {
            if (obj.getType().equals("P")) {
                // 普通套餐
                CommonSuitPop suitPop = new CommonSuitPop(activity,
                        obj, tcT, "1");
                Log.e(TAG, "单菜品-----------------------菜品:" + obj.toString());
                suitPop.showSheet();
            } else if (obj.getType().equals("C")) {
                // 组合套餐
                CommonSelectSuitPop selectSuitPop = new CommonSelectSuitPop(
                        activity, obj, tcT, "1");
                Log.e(TAG, "单菜品-----------------------菜品:" + obj.toString());
                selectSuitPop.showSheet();
            }
            // 查套餐下面的必选菜品
        }

        @Override
        public void tc(FoodsBean obj, String num) {
            if (obj.getType().equals("P")) {
                // 普通套餐
                CommonSuitPop suitPop = new CommonSuitPop(activity,
                        obj, tcT, num);
                suitPop.showSheet();
            } else if (obj.getType().equals("C")) {
                // 组合套餐
                CommonSelectSuitPop selectSuitPop = new CommonSelectSuitPop(
                        activity, obj, tcT, num);
                selectSuitPop.showSheet();
            }
            // 查套餐下面的必选菜品
        }

        @Override
        public void cz(FoodsBean obj, ArrayList<ReasonBean> resons, String remark, T_Image im) {
            CommonNum commonNum = new CommonNum(activity, numSel, obj, obj.getOrderMinLimit(), resons, remark, im);
            commonNum.showSheet();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if(buysPop!=null){
            buysPop.close();
            buysPop = null;
        }
    }

    private NumSel numSel = new NumSel() {
        @Override
        public void change(String o, Object ob) {
            Double s = Double.parseDouble(o);
        }

        @Override
        public void changeWeigh(String o, Object object, ArrayList<ReasonBean> resons, String remark, T_Image im) {
            FoodsBean obj = (FoodsBean) object;
            Double s = Double.parseDouble(o);
            String num = s + "";
//            boolean flag = DB.getInstance().addDishToPad(obj,
//                    resons, remark, "", "", num, handler);
//            if (flag) {
//                addX(im);
//            }


            if (obj.isPriceMofidy()) {
                // 临时菜,需要确认价格才能点击
                //picMoveT.sj(obj);
                boolean flag1 = DB.getInstance().addDishToPad(obj,
                        resons, remark, "", "", num, handler);
                if (flag1) {
                    addX(im);
                }
                //改为直接下单
            } else {

                if (obj.isRequire_cook()) {
                    // 显示口味选择
                    P.c("1111111");
                    if (sharedUtils.getBooleanValue("is_reas")) {//口味必选
                        //选择口味
                        picMoveT.rb(obj);
                    } else {//否则直接加到菜篮子
                        boolean flag = DB.getInstance().addDishToPad(obj,
                                resons, remark, "", "", num, handler);
                        if (flag) {
                            addX(im);
                        }
                        //直接点餐
                    }

                }
                // 是套餐
                else if (obj.isSuit()) {
                    picMoveT.tc(obj, num);
                } else {
                    // 不显示口味选择，
                    P.c("2222222");
                    boolean flag = DB.getInstance().addDishToPad(obj,
                            resons, remark, "", "", num, handler);
                    if (flag) {
                        addX(im);
                    }
                }
            }
        }
    };

    private void addX(T_Image im) {
        int[] lo = new int[2];
        int[] lo1 = new int[2];
        im.getLocationInWindow(lo);
        im.getLocationOnScreen(lo1);
        PicPoint point = new PicPoint();
        point.setHeight(im.getMeasuredHeight());
        point.setWidth(im.getMeasuredWidth());
        point.setPoint(lo1);
        point.setPic(im.getTag(R.id.image_tag).toString());
        picMoveT.addP(point);
        point = null;
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 改变菜品总数量
     */
    private void changeNum() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                msg.what = -10;
                msg.arg1 = DB.getInstance().getCount();
                handler.sendMessage(msg);
            }
        }.start();

    }

    private CommonSnyDataPop dataPop;
    private boolean isShow = true;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        P.c("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(animation_viewGroup==null){
            animation_viewGroup = createAnimLayout();
        }
        if (sharedUtils.getStringValue("isOldVersion").equals("rk312x")) {
            RelativeLayout.LayoutParams paramTest = (RelativeLayout.LayoutParams) table_tip.getLayoutParams();
            paramTest.leftMargin = 0;
            paramTest.rightMargin = 500;
            table_tip.setLayoutParams(paramTest);
        }

        if (sharedUtils.getBooleanValue("is_giv")) {
            showLine = 3;
        } else {
            showLine = 4;
        }
        if (sharedUtils.getBooleanValue("is_txt")) {
            //文字模式
            showLine = 2;
        }
        f_h_com_gv.setNumColumns(showLine);
        /*if (sharedUtils.getBooleanValue("is_call")) {
            call_waiter.setVisibility(View.GONE);
        } else {
            call_waiter.setVisibility(View.VISIBLE);
        }*/
        if (sharedUtils.getBooleanValue("is_table")) {
            call_table.setVisibility(View.VISIBLE);
        } else {
            call_table.setVisibility(View.GONE);
        }
        if (sharedUtils.getBooleanValue("is_advert")) {
            call_advert.setVisibility(View.VISIBLE);
            call_advert.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Common.ACTION_ADVERT);
                        intent.putExtra("playaction", "gift");//主动点击
                        startActivity(intent);
                    } catch (Exception e) {
                        NewDataToast.makeText("没有检测到");
                    }

                }
            });
        } else {
            call_advert.setVisibility(View.GONE);
        }



        if (sharedUtils.getBooleanValue("is_lan")) {
            tv_tip.setTextSize(22);
            tv_tip.setText(sharedUtils.getStringValue("tip_en"));
        } else {
            tv_tip.setText(sharedUtils.getStringValue("tip"));
        }
        et_search.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {//修改回车键功能
//                    search_view.setVisibility(View.GONE);
//                    et_search.setVisibility(View.VISIBLE);
                    String rs = et_search.getText().toString().replace("[", "%")
                            .replaceAll("]", "%").replaceAll(",", "%")
                            .replaceAll(" ", "");
                    if (rs.length() != 0) {
                        //
                        P.c("查询");
                        get(rs, true);
                    }
                    hideInput();
                }

                return false;
            }
        });
        //监听搜索编辑框
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                Log.e("输入过程中执行该方法", "文字变化");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听
                Log.e("输入前确认执行该方法", "开始输入");

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                Log.e("输入结束执行该方法", "输入结束");
                String rs = et_search.getText().toString().replace("[", "%")
                        .replaceAll("]", "%").replaceAll(",", "%")
                        .replaceAll(" ", "");
                if (rs.length() != 0) {
                    //
                    P.c("查询");
                    get(rs, true);
                }

            }
        });

        if (sharedUtils.getBooleanValue("is_lan")) {
            iv_language.setImageDrawable(getResources().getDrawable(R.drawable.en));
        } else {
            iv_language.setImageDrawable(getResources().getDrawable(R.drawable.zh));
        }
        iv_language.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                CommonDestoryPop destoryPop = new CommonDestoryPop(getActivity(), loadBuy);
//                destoryPop.showSheet();
                if (sharedUtils.getBooleanValue("is_lan")) {//当前是英文
                    iv_language.setImageDrawable(getResources().getDrawable(R.drawable.zh));
                    sharedUtils.setBooleanValue("is_lan", false);
                    handler.sendEmptyMessage(100);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            BaseApplication.application.resetApplicationAll();
                        }
                    }, 1500);
                } else {
                    iv_language.setImageDrawable(getResources().getDrawable(R.drawable.en));
                    sharedUtils.setBooleanValue("is_lan", true);
                    handler.sendEmptyMessage(100);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            BaseApplication.application.resetApplicationAll();
                        }
                    }, 1500);
                }
            }
        });
        table_people.setText(String.valueOf(sharedUtils.getIntValue("person")));
        // sharedUtils.reg("person", table_people);
        call_waiter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*if (System.currentTimeMillis() - waiteClick <= 1000) {
                    return;
                }
                waiteClick = System.currentTimeMillis();
                parentHandler.sendEmptyMessage(3);
                // 发一个时间更改的通知*/

                if (FileUtils.db_exits()) {

                    Intent intent = new Intent(activity,
                            CardValActivity.class);
                    intent.putExtra("login", true);
                    startActivityForResult(intent, ENTER_TABLE_VAL);
                }
            }
        });
        search_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (home_lv_menu.isShown()) {
                    sear_del.setBackgroundResource(R.drawable.news_item_bg);
                    search_.setVisibility(View.VISIBLE);
                    home_lv_menu.setVisibility(View.GONE);
                    add_view.setVisibility(View.GONE);
                    add_dl.setVisibility(View.GONE);
                    if (childMenuBeans != null) {
                        childMenuBeans.clear();
                    }
                } else {
                    sear_del.setBackgroundResource(0);
                    search_.setVisibility(View.GONE);
                    search_view.setText("");
                    search.clear();
                    home_lv_menu.setVisibility(View.VISIBLE);
                    select(selectMenu);
                }
            }
        });
        search_.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if ("中".equals(zm[arg2])) {
                    if (isWord) {
                        isWord = false;
                        search_view.setVisibility(View.GONE);
                        sear_del.setVisibility(View.GONE);
                        et_search.setVisibility(View.VISIBLE);
                        showInput(et_search);
                        searchAdapter.setSelection(arg2, isWord);
                        searchAdapter.notifyDataSetChanged();
                    } else {
                        isWord = true;
                        search_view.setVisibility(View.VISIBLE);
                        sear_del.setVisibility(View.VISIBLE);
                        et_search.setVisibility(View.GONE);
                        hideInput();
                        searchAdapter.setSelection(arg2, isWord);
                        searchAdapter.notifyDataSetChanged();
                    }
                    //直接搜索
                } else {
                    isWord = true;
                    search_view.setVisibility(View.VISIBLE);
                    sear_del.setVisibility(View.VISIBLE);
                    et_search.setVisibility(View.GONE);
                    hideInput();
                    search_view
                            .setText(search_view.getText().toString() + zm[arg2]);
                    search.add(zm[arg2]);
                    //
                    String rs = search.toString().replace("[", "%")
                            .replaceAll("]", "%").replaceAll(",", "%")
                            .replaceAll(" ", "");
                    if (rs.length() != 0) {
                        //
                        P.c("查询");
                        get(rs, false);
                    }
                }

            }
        });
        sear_del.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                /**
                 * 删除数据
                 */
                if (search_view.getText().toString().length() != 0) {
                    String temp = search_view.getText().toString();
                    search_view.setText(temp.subSequence(0, temp.length() - 1));
                    search.remove(search.size() - 1);
                    String rs = search.toString().replace("[", "%")
                            .replaceAll("]", "%").replaceAll(",", "%")
                            .replaceAll(" ", "");
                    P.c("回退" + rs);
                    if (search.size() != 0) {
                        // 查询
                        P.c("查询");
                        get(rs, false);
                    } else {
                        search.clear();
                        P.c("清除0");
                        get(rs, false);
                    }
                } else {
                    search.clear();
                    P.c("清除1");
                    NewDataToast.makeText(getResources().getString(R.string.wrongclick));
                }
            }
        });

        home_lv_menu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //菜类大类
                childMenuBeans.clear();
                select(arg2);
                /*
                 * if (android.os.Build.VERSION.SDK_INT >= 8) {
                 * home_lv_menu.smoothScrollToPosition(arg2); } else {
                 * home_lv_menu.setSelection(arg2); }
                 */
            }
        });

        // adAdapter = new AdAdapter(OrderActivity.this, childMenuBeans,
        // handler);
        // add_view.setAdapter(adAdapter);


        buyView.post(new Runnable() {

            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        });
        table_code
                .setText(sharedUtils.getStringValue("table_name").length() == 0 ? getResources().getString(R.string.select_table)
                        : sharedUtils.getStringValue("table_name"));
        /*
         * table_code.setOnClickListener(new OnClickListener() {
         *
         * @Override public void onClick(View arg0) {
         * method stub if (FileUtils.db_exits()) { pause(); Intent intent = new
         * Intent(OrderActivity.this, CardValActivity.class);
         * startActivityForResult(intent, ENTER_TABLE_VAL); }
         *
         * } });
         */
        //
        table_people.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (sharedUtils.getStringValue("billId").length() != 0) {
                    return;
                }
                // 选择
                selectPeople();
            }
        });
        table_tip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (sharedUtils.getStringValue("billId").length() != 0) {
                    return;
                }
                // 选择
                selectPeople();
            }
        });
        call_table.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(activity,
                        CardValActivity.class);
                startActivityForResult(intent, ENTER_TABLE_);
            }
        });
        table_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (FileUtils.db_exits()) {
                    if (sharedUtils.getBooleanValue("is_table") || sharedUtils.getBooleanValue("is_waite") || sharedUtils.getBooleanValue("is_dish")) {
                        //判断必须输密码的情况
                        if (sharedUtils.getBooleanValue("is_ps")) {
                            selectTableName();
                        } else {
                            tablesPop = new CommonTablesPop(
                                    activity,
                                    selectTable, "");
                            tablesPop.showSheet();
                        }
                    } else {
                        selectTableName();
                    }
                }
            }
        });

        P.c("执行这里。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
      //  if (sharedUtils.getBooleanValue("canDiscount") && isShow) {



//        } else {
//            isShow = true;
//        }
        //获取二维码广播
        getUrlCode();
        getAidlArg();
        //设置设备号
        setDevice();
        int ct = DB.getInstance().getCount();
        P.c("允许打折"+ct);
        if(sharedUtils.getStringValue("billId").length()==0&&ct==0){
            IS_DO = 1;
            handler.sendEmptyMessage(66);
        }
    }
    private int IS_DO = 0;
    private void updateData() {
        int versionOld = sharedUtils.getIntValue("versionOld");
        int version = BaseApplication.application.getVersionCode();
        P.c("version:" + version + "---versionOld:" + versionOld);
        if (version > versionOld) {
            if (System.currentTimeMillis() - lastClick <= 2000) {
                return;
            }
            lastClick = System.currentTimeMillis();
            if (dataPop == null) {
                dataPop = new CommonSnyDataPop(getActivity(), "同步菜品资源");
                dataPop.showSheet();
                dataPop = null;
            }
        }
    }

    private void setDevice() {
        String snCode = sharedUtils.getStringValue("sn");
        String ipCode = sharedUtils.getStringValue("IP");
        call_waiter.setText(getResources().getString(R.string.call_service) + sharedUtils.getStringValue("userName"));
        if (!"".equals(snCode)) {
            sharedUtils.setStringValue("deviceSerialNo", snCode);
        } else {
            sharedUtils.setStringValue("deviceSerialNo", ipCode.replace(".", "_"));
        }
    }

    private void getUrlCode() {
        Intent intent = new Intent();
        intent.setAction("com.zed.Usb.ResQRcode");
        intent.putExtra("discount", "discount");
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (connAidl != null) {
            getActivity().unbindService(connAidl);
        }
        if(pop!=null){
            pop.close();
            pop = null;
        }
    }

    private void getAidlArg() {
        String isOldVersion = Build.MODEL;//rk3368-P9   rk312x
        sharedUtils.setStringValue("isOldVersion", isOldVersion);
        Intent mServiceIntent = new Intent("sys.update.time");
        Intent endIntent = new Intent(Common.createExplicitFromImplicitIntent(activity, mServiceIntent));
        getActivity().bindService(endIntent, connAidl, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection connAidl = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBackService backService = IBackService.Stub.asInterface(service);
            try {
                String regCode = backService.getdeviceInfoString("ServiceInfo", 1);
                P.c("注册码:" + regCode);
                String sn = backService.getdeviceInfoString("ServiceInfo", 2);
                String callingServer = backService.getdeviceInfoString("ServiceInfo", 19);
                String localServer = backService.getdeviceInfoString("ServiceInfo", 6);
                String notGetIp = sharedUtils.getStringValue("notGetIp");
                P.c("localServer值:" + localServer);
                if (!"".equals(regCode)) {
                    sharedUtils.setStringValue("regCode", regCode);
                    sharedUtils.setStringValue("sn", sn);
                    sharedUtils.setStringValue("callingServer", callingServer);
                    updateData();
                    //这里存储aidl获取的ip替代原来手动配置
                    if (sharedUtils.getStringValue("IP").equals(localServer) || notGetIp.equals("nope")) {
                        return;
                    } else {
                        sharedUtils.setStringValue("IP", localServer);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
       /* if (pop != null) {
            pop.close();
        }*/
       if(b!=null){
           getContext().unregisterReceiver(b);
       }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        P.c("order--onDestroyView--");
        dataHandler.removeCallbacks(runnable);
    }

    private void selectTableName() {
        final SettingMM1Dialog mmDialog = new SettingMM1Dialog(
                activity);
        mmDialog.getSureBtn().setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (mmDialog.getPassword().equals(
                                Common.COMMON_PASS)) {
                            mmDialog.dismiss();
                            tablesPop = new CommonTablesPop(
                                    activity,
                                    selectTable, "");
                            tablesPop.showSheet();
                            //
                            // showSetTableDialog();
                        } else {
                            NewDataToast.makeText(getResources().getString(R.string.wrongnum));
                        }
                    }
                });
        mmDialog.show();
    }
}
