package pad.stand.com.haidiyun.cy.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.cy.adapter.CyMainBottomAdapter;
import pad.stand.com.haidiyun.cy.adapter.CyMainCenterAdapter;
import pad.stand.com.haidiyun.cy.adapter.CyMainTitleAdapter;
import pad.stand.com.haidiyun.cy.widget.HorizontalListView;
import pad.stand.com.haidiyun.cy.widget.coverflow.CoverFlowLayoutManger;
import pad.stand.com.haidiyun.cy.widget.coverflow.RecyclerCoverFlow;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.SearchAdapter;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.bean.PicPoint;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.bean.TableBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.DishChange;
import pad.stand.com.haidiyun.www.inter.LoadBuy;
import pad.stand.com.haidiyun.www.inter.NumSel;
import pad.stand.com.haidiyun.www.inter.PicMoveT;
import pad.stand.com.haidiyun.www.inter.ReasonI;
import pad.stand.com.haidiyun.www.inter.SelectTable;
import pad.stand.com.haidiyun.www.inter.TcT;
import pad.stand.com.haidiyun.www.inter.Tz;
import pad.stand.com.haidiyun.www.inter.TzC;
import pad.stand.com.haidiyun.www.inter.UD;
import pad.stand.com.haidiyun.www.inter.WaterConfirm;
import pad.stand.com.haidiyun.www.service.FloatService;
import pad.stand.com.haidiyun.www.ui.CardValActivity;
import pad.stand.com.haidiyun.www.widget.CommonAdvertPop;
import pad.stand.com.haidiyun.www.widget.CommonBuysPop;
import pad.stand.com.haidiyun.www.widget.CommonDiscountPop;
import pad.stand.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.stand.com.haidiyun.www.widget.CommonNum;
import pad.stand.com.haidiyun.www.widget.CommonResPop;
import pad.stand.com.haidiyun.www.widget.CommonSelectSuitPop;
import pad.stand.com.haidiyun.www.widget.CommonSuitPop;
import pad.stand.com.haidiyun.www.widget.CommonTablesPop;
import pad.stand.com.haidiyun.www.widget.CommonTzPop;
import pad.stand.com.haidiyun.www.widget.HomeSelecterPeoDialog;
import pad.stand.com.haidiyun.www.widget.NewDataToast;
import pad.stand.com.haidiyun.www.widget.RecyclerImageView;
import pad.stand.com.haidiyun.www.widget.SettingMM1Dialog;
import pad.stand.com.haidiyun.www.widget.T_Image;
import pad.stand.com.haidiyun.www.widget.UDlayout;

import static com.zc.http.okhttp.log.LoggerInterceptor.TAG;

@SuppressLint("ValidFragment")
public class CyMainActivity extends Fragment {

    private SharedUtils sharedUtils;
    private ArrayList<FoodsBean> foodsBeans = new ArrayList<>();
    private ArrayList<MenuBean> menuBeans = new ArrayList<>();
    private Map<String,Integer> menuStrs = new HashMap<>();
    private CyMainCenterAdapter cyMainCenterAdapter;
    private CyMainTitleAdapter cyMainTitleAdapter;
    private CyMainBottomAdapter cyMainBottomAdapter;
    private RecyclerCoverFlow coverFlow;
    private HorizontalListView bottomFlow;
    private RecyclerView titleFlow;
    private LinearLayout cover_left_b,cover_right_b,cover_left_s,cover_right_s,content;
    private Button cy_bot_0,cy_bot_1,cy_bot_2,cy_bot_30;
    private FrameLayout cy_bot_3;
    private TextView dish_num;
    private Activity activity;
    private UDlayout uDlayout;
    private LinearLayout view_content,search_layout;
    private ImageView title_l,title_r;
    private boolean isTishi = false;
    private TextView call_waiter;
     public CyMainActivity(Activity activity) {
        this.activity = activity;
         sharedUtils = new SharedUtils(Common.CONFIG);
         cyMainHandler = new CyMainHandler(CyMainActivity.this);
    }
    private static Handler dataHandler = new Handler();
      Runnable runnable = new Runnable() {

        @Override
        public void run() {

            P.c("-----------自动检测更新------------");
            String ip = sharedUtils.getStringValue("IP");

            if (ip.length() != 0) {
                gu(ip);
            }
            dataHandler.postDelayed(this, 1000 * 10);
        }
    };

    private static RequestCall requestCall;
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
            // P.c("沽清结果"+response);
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
                           // handler.sendEmptyMessage(5);
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cy_main_activity, container, false);
        return view;
    }
    private int AnimationDuration = 1000;
    private FrameLayout animation_viewGroup;
    private boolean isClean = false;
    private int number = 0;
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
        dish_num.getLocationInWindow(end_location);
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

        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                number--;
                if (number == 0) {
                    isClean = true;
                    cyMainHandler.sendEmptyMessage(-1);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }

        });
        view.startAnimation(mAnimationSet);

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
    PicMoveT picMoveT = new PicMoveT() {
        @Override
        public void addP(PicPoint point) {
            if (point != null) {
                doAnim(point);
            }
            P.c("这里4");
            changeNum();
            dishLs();
        }
        private boolean check(final FoodsBean obj) {
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
        public void add(final FoodsBean obj) {
            if (check(obj)) {

                new Thread() {
                    public void run() {
                        DB.getInstance().changeNum(obj.getCount() + 1,
                                obj.getCode(), obj.getPrice(), "+", cyMainHandler);
                        P.c("这里1");
                        // ------
                        process();
                    }
                }.start();
            }
        }

        @Override
        public void mics(FoodsBean obj) {

        }

        @Override
        public void sj(FoodsBean obj) {
            Intent intent = new Intent(activity,
                    CardValActivity.class);
            intent.putExtra("obj", obj);
            startActivityForResult(intent, ENTER_PRICE_VAL);
        }

        @Override
        public void rb(FoodsBean obj) {
            // 口味选择下单
            CommonResPop resPop = new CommonResPop(activity, reasonI,
                    null, obj);
            resPop.showSheet();
        }

        @Override
        public void gq() {

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
        }

        @Override
        public void cz(FoodsBean obj, ArrayList<ReasonBean> resons, String remark, T_Image im) {
            CommonNum commonNum = new CommonNum(activity, numSel, obj, obj.getOrderMinLimit(), resons, remark, im);
            commonNum.showSheet();
        }

        @Override
        public void view(FoodsBean obj, int index) {

        }
    };
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
                        resons, remark, "", "", num, cyMainHandler);
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
                                resons, remark, "", "", num, cyMainHandler);
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
                            resons, remark, "", "", num, cyMainHandler);
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
    private ReasonI reasonI = new ReasonI() {

        @Override
        public void select(ArrayList<ReasonBean> beans, DishTableBean bean, String numView) {


        }

        @Override//更新套餐列表
        public void select(final ArrayList<ReasonBean> beans, final DishTableBean bean, final HashMap<String, ArrayList<ReasonBean>> tcBeans, final HashMap<String, String> tcListStr, final int position, final int tcActualNum) {
            new Thread() {
                public void run() {//加入到菜篮子更新表
                    DB.getInstance().updateDishTable(beans, bean, tcBeans, tcListStr, position, tcActualNum);
                    cyMainHandler.sendEmptyMessage(31);//清空
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
                            "", "", "", numView, cyMainHandler);
                    if (flag) {
                        cyMainHandler.sendEmptyMessage(-8);
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
                            "", "", "", numView, cyMainHandler);
                    if (flag) {
                        cyMainHandler.sendEmptyMessage(-8);
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
                            null, "", details, detailNames, num, cyMainHandler);
                    if (flag) {
                        cyMainHandler.sendEmptyMessage(-8);
                    }
                }

                ;
            }.start();

        }
    };

    int SELECT_ = 0;//记录最后一个页的滑动次数
    private HashMap<String,String> titleKeys = new HashMap<>();
    private void processTindex(){

         for(int i=0;i<menuBeans.size();i++){
             for(int k=0;k<foodsBeans.size();k++){
                 if(foodsBeans.get(k).getCyMenuIndex()==i){

                     titleKeys.put(String.valueOf(i),String.valueOf(k));

                     break;
                 }
             }
         }


    }

    private void selectMenu(final int i) {
        SELECT_=  0;
        P.c("执行时间"+ TimeUtil.getTime(System.currentTimeMillis()));
        cyMainTitleAdapter.selectPosition(i);
        index(i);
        //coverFlow.smoothScrollToPosition(titleKeys.get(i));
       new Thread(){
           @Override
           public void run() {
               super.run();
               Message msg = new Message();
               msg.what = 114;
               msg.obj = titleKeys.get(String.valueOf(i));
               cyMainHandler.sendMessage(msg);
           }
       }.start();

        P.c("执行结束"+ TimeUtil.getTime(System.currentTimeMillis()));

    }
    private UD ud = new UD() {
        @Override
        public void change(boolean isOpen, boolean init) {
            P.c(isOpen+"~~~`"+init);
            if(!isOpen&&!init){
                //证明是关闭且运行完成
                search_view.setText("");
                search.clear();
                initMenu();
               /* if (FileUtils.db_exits()) {

                    new Thread() {
                        public void run() {
                            // 固定给一个营业点
                            menuBeans.clear();
                            menuStrs.clear();
                            String site = sharedUtils.getStringValue("sitecode");

                            menuBeans = DB.getInstance().getMenus(1, site);

                            for(int i=0;i<menuBeans.size();i++){
                                menuStrs.put(menuBeans.get(i).getCode(),i);
                            }
                            foodsBeans.clear();
                            foodsBeans = DB.getInstance().getFoods(menuStrs);

                            cyMainHandler.sendEmptyMessage(10);

                        }
                    }.start();
                }*/

            }
        }
    };
    private final int UD_SCROOL_WIDTH = -300;
    private final int UD_SCROOL_HEIGHT = 0;
    private GridView search_;
    private TextView search_view;
    private EditText et_search;
    private ImageView sear_del;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intentService = new Intent();
        intentService.setAction("pad.com.invisible");
        activity.sendBroadcast(intentService);
        animation_viewGroup = createAnimLayout();
        search_ = (GridView) view.findViewById(R.id.search_);
        cy_bot_0 = (Button) view.findViewById(R.id.cy_bot_0);
        cy_bot_1 = (Button) view.findViewById(R.id.cy_bot_1);
        cy_bot_2 = (Button) view.findViewById(R.id.cy_bot_2);
        cy_bot_3 = (FrameLayout) view.findViewById(R.id.cy_bot_3);
        cy_bot_30 = (Button) view.findViewById(R.id.cy_bot_30);
        search_layout = (LinearLayout) view.findViewById(R.id.search_layout);
        view_content = (LinearLayout) view.findViewById(R.id.view_content);
        uDlayout = (UDlayout) view.findViewById(R.id.ud_layout);
        dish_num = (TextView) view.findViewById(R.id.dish_num);
        content = (LinearLayout) view.findViewById(R.id.content);
        coverFlow = (RecyclerCoverFlow) view.findViewById(R.id.coverFlow);
        sear_del = (ImageView) view.findViewById(R.id.sear_del);
        search_view = (TextView) view.findViewById(R.id.search_view);
        et_search = (EditText) view.findViewById(R.id.et_search);
        title_l = (ImageView) view.findViewById(R.id.title_l);
        title_r = (ImageView) view.findViewById(R.id.title_r);
        cyMainCenterAdapter = new CyMainCenterAdapter(activity,foodsBeans,sharedUtils,picMoveT,cyMainHandler);
        coverFlow.setAdapter(cyMainCenterAdapter);
        call_waiter  = (TextView) view.findViewById(R.id.call_waiter);
        bottomFlow = (HorizontalListView) view.findViewById(R.id.bottomFlow);
        titleFlow = (RecyclerView) view.findViewById(R.id.titleFlow);
        cover_left_b = (LinearLayout) view.findViewById(R.id.cover_left_b);
        cover_right_b = (LinearLayout) view.findViewById(R.id.cover_right_b);
        cover_left_s = (LinearLayout) view.findViewById(R.id.cover_left_s);
        cover_right_s = (LinearLayout) view.findViewById(R.id.cover_right_s);
        uDlayout.setUD(ud);


        uDlayout.smoothScrollTo(UD_SCROOL_WIDTH,UD_SCROOL_HEIGHT);
        cy_bot_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                P.c("这里操作界面");
                uDlayout.smoothScrollTo(UD_SCROOL_WIDTH,UD_SCROOL_HEIGHT);
            }
        });
        call_waiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,
                        CardValActivity.class);
                intent.putExtra("login", true);
                startActivityForResult(intent, ENTER_TABLE_VAL);
            }
        });
        coverFlow.setOnSroolListener(new CoverFlowLayoutManger.OnScrool() {
            @Override
            public void scroolStop() {
               // titleFlow.setEnabled(true);
                int i = coverFlow.getCoverFlowLayout().get();
                if(i!=-1){

                    P.c("显示中"+i);
                }
            }

            @Override
            public void scroolStuts() {
                    //titleFlow.setEnabled(false);
            }

            @Override
            public void scroolStary() {
               // titleFlow.setEnabled(false);
            }

            @Override
            public   void scroolIndex(int position) {
                if(foodsBeans.size()!=0){
                    if(!uDlayout.isOpen()){
                        final int NOW_SELECT = coverFlow.getCoverFlowLayout().getCenterPosition();

                       /* if(NOW_SELECT>LAST_SELECT){
                            //增加滚动
                            bottomFlow.scrollTo(NOW_SELECT*SCROOL_WIDTH);
                        }else{
                            //减少滚动
                            bottomFlow.scrollTo(NOW_SELECT*SCROOL_WIDTH);
//                            bottomFlow.scrollTo(bottomFlow.getScrool()-(NOW_SELECT*SCROOL_WIDTH));
                        }*/
                        LAST_SELECT = NOW_SELECT;
                        if(LAST_SELECT>=foodsBeans.size()){
                            LAST_SELECT= 0;
                        }
                        int index = coverFlow.getCoverFlowLayout().getCenterPosition();
                        int to = foodsBeans.get(index).getCyMenuIndex();
                        cyMainTitleAdapter.selectPosition(to);
                        index(to);
//                        cyMainBottomAdapter.selectPosition(coverFlow.getCoverFlowLayout().getCenterPosition());
//                        bottomFlow.smoothScrollToPosition(position);
                      // if(cyMainTitleAdapter.nowIndex()!=to){
                           titleFlow.smoothScrollToPosition(to);
                      // }
                        if(NOW_SELECT!=-1){
                           new Thread(){
                               @Override
                               public void run() {
                                   super.run();
                                   Message msg = new Message();
                                   msg.what = 116;
                                   msg.arg1 = NOW_SELECT;
                                   cyMainHandler.sendMessage(msg);
                               }
                           }.start();
                        }
                    }
                }
            }
        });


//        coverFlow.addOnScrollListener(new RecyclerViewListener());
        cyMainCenterAdapter.setOnItemClick(new CyMainCenterAdapter.onItemClick() {
            @Override
            public void clickItem(int pos) {
//                NewDataToast.makeText(foodsBeans.get(pos).getName());
                P.c(coverFlow.getSelectedPos()+"~~");
                //coverFlow.scrollToPosition(pos);
//                coverFlow.getCoverFlowLayout().scrollToPosition(pos);
                    move(pos);

            }
        });
        cyMainBottomAdapter = new CyMainBottomAdapter(activity,foodsBeans,cyMainHandler);
        bottomFlow.setAdapter(cyMainBottomAdapter);
     /*   bottomFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                P.c("这里。。。。。。。。。");

                cyMainBottomAdapter.selectPosition(i);
                coverFlow.smoothScrollToPosition(i);
                P.c("这里。。。。。。。。。1");
            }
        });*/

      /*  bottomFlow.setOnScrool(new HorizontalListView.OnScrool() {
            @Override
            public void scroolStop() {
                cyMainBottomAdapter.selectPosition(coverFlow.getSelectedPos());
            }
        });*/


        cyMainTitleAdapter = new CyMainTitleAdapter(activity,menuBeans,cyMainHandler);
        LinearLayoutManager titleManager =  new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false);
        titleFlow.setLayoutManager(titleManager);
        titleFlow.setAdapter(cyMainTitleAdapter);
        cyMainTitleAdapter.setOnItemClick(new CyMainTitleAdapter.onItemClick() {
            @Override
            public void clickItem(int pos) {
                if(uDlayout.isOpen()){
                    uDlayout.smoothScrollTo(UD_SCROOL_WIDTH,UD_SCROOL_HEIGHT);
                }
                selectMenu(pos);
            }
        });


       /* titleFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(uDlayout.isOpen()){
                    uDlayout.smoothScrollTo(UD_SCROOL_WIDTH,UD_SCROOL_HEIGHT);
                }
               selectMenu(i);

            }
        });*/




        cover_left_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move(coverFlow.getSelectedPos()-1);
            }
        });
        cover_right_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move(coverFlow.getSelectedPos()+1);
            }
        });



        cover_left_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                bottomFlow.scrollTo(bottomFlow.getScrool()-SCROOL_WIDTH);


            }
        });

        cover_right_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   P.c((SCROOL_WIDTH+bottomFlow.getScrool())+"移动");

                bottomFlow.scrollTo(SCROOL_WIDTH+bottomFlow.getScrool());


            }
        });



       /* titleFlow.post(new Runnable() {
            @Override
            public void run() {
                initMenu();
            }
        });*/
         /* new Thread(){
            @Override
            public void run() {
                super.run();
                foodsBeans.clear();
                foodsBeans =   DB.getInstance().getFoods("101",10,0);
                getHandler().sendEmptyMessage(1);
            }
        }.start();*/
      /*  cy_bot_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cyMainHandler.sendEmptyMessage(5);
               P.c("选择的是"+ coverFlow.getSelectedPos());

            }
        });*/
        cy_bot_1.setText(String.valueOf(sharedUtils.getIntValue("person")));
        cy_bot_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (sharedUtils.getStringValue("billId").length() != 0) {
                    return;
                }
                // 选择
                selectPeople();
            }
        });
        cy_bot_2
                .setText(sharedUtils.getStringValue("table_name").length() == 0 ? getResources().getString(R.string.select_table)
                        : sharedUtils.getStringValue("table_name"));

        cy_bot_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (FileUtils.db_exits()) {
                    if (sharedUtils.getBooleanValue("is_table") || sharedUtils.getBooleanValue("is_waite") || sharedUtils.getBooleanValue("is_dish")) {
                        //判断必须输密码的情况
                        if (sharedUtils.getBooleanValue("is_ps")) {
                            final SettingMM1Dialog mmDialog = new SettingMM1Dialog(
                                    activity);
                            mmDialog.getSureBtn().setOnClickListener(
                                    new View.OnClickListener() {
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
                        } else {
                            tablesPop = new CommonTablesPop(
                                    activity,
                                    selectTable, "");
                            tablesPop.showSheet();
                        }
                    } else {
                        final SettingMM1Dialog mmDialog = new SettingMM1Dialog(
                                activity);
                        mmDialog.getSureBtn().setOnClickListener(
                                new View.OnClickListener() {
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
            }
        });
        dish_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDish();
            }
        });
        cy_bot_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDish();
            }
        });
        cy_bot_30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDish();
            }
        });
        searchAdapter = new SearchAdapter(activity, zm, isWord);
        search_.setAdapter(searchAdapter);

        search_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        search_bg = (RelativeLayout) view.findViewById(R.id.search_bg);
        search_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                        P.c(uDlayout.isOpen()+"");
                        if(uDlayout.isOpen()){
                            uDlayout.smoothScrollTo(UD_SCROOL_WIDTH,UD_SCROOL_HEIGHT);
                        }
            }
        });
        sear_del.setOnClickListener(new View.OnClickListener() {

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
        dataHandler.post(runnable);
        setDevice();
    }
    private void index(int index){
        if(index==0){
            title_l.setVisibility(View.GONE);
            title_r.setVisibility(View.VISIBLE);
        }else if(index==(menuBeans.size()-1)){
            title_l.setVisibility(View.VISIBLE);
            title_r.setVisibility(View.GONE);
        }else{
            title_l.setVisibility(View.VISIBLE);
            title_r.setVisibility(View.VISIBLE);
        }
    }


    private RelativeLayout search_bg;
    private ArrayList<String> search = new ArrayList<String>();
    private void get(final String search, final boolean word) {
        new Thread() {
            @Override
            public void run() {
                super.run();
               // foodsBeans.clear();

                foodsBeans = DB.getInstance().getSearchFoods(search,
                         word);
                cyMainHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private void showInput(EditText et_search) {
        et_search.setFocusable(true);
        et_search.setFocusableInTouchMode(true);
        et_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_search, 0);
    }
    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),
                    0);
        }
    }
    private boolean isWord = true;
    private SearchAdapter searchAdapter;
    private String []zm = new String[]{"中", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    private void showDish(){
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return ;
        }
        lastClick = System.currentTimeMillis();
        int screen[] = getScreens();
        buysPop = new CommonBuysPop(
                activity, confirm,
                dishChange, cancelListener, screen,
                sharedUtils, tz, false, cy_bot_1);
        buysPop.showSheet();
    }
    private TzC conf = new TzC() {

        @Override
        public void tuicai(DishTableBean obj, String num, String tag) {
            if (tag != null) {
                Intent intent = new Intent(activity,
                        CardValActivity.class);
                intent.putExtra("obj", obj);
                intent.putExtra("num", Integer.parseInt(num));
                intent.putExtra("tag", tag);
                startActivityForResult(intent, ORDER_TUI);
            }

        }

        @Override
        public void zengsong(DishTableBean obj, String num, String tag) {

            Intent intent = new Intent(activity,
                    CardValActivity.class);
            intent.putExtra("obj", obj);
            intent.putExtra("num", Integer.parseInt(num));
            intent.putExtra("tag", tag);
            startActivityForResult(intent, ORDER_ZENG);


        }
    };
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
    private int[] getScreens() {
        int screen[] = new int[2];
        // 保存长宽
        screen[0] = content.getMeasuredWidth();
        screen[1] = content.getMeasuredHeight();
        return screen;
    }
    /**
     * 发送一次点击广播
     */
    private void down() {
        Intent intent = new Intent();
        intent.setAction(Common.TOUCH_DOWN);
        activity.sendBroadcast(intent);
    }

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
    private void process(){
        if (menuBeans.size() != 0) {


            foodsBeans = DB.getInstance().getFoods(menuStrs);

        }
        cyMainHandler.sendEmptyMessage(5);


    }

    int FOOD_COUNT = 0;

    private DialogInterface.OnDismissListener cancelListener = new DialogInterface.OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface arg0) {
            dishLs();
          /*  down();
            P.c("隐藏菜篮子");
            dishLs();
            buysPop = null;*/
        }
    };
    private DishChange dishChange = new DishChange() {

        @Override
        public void change() {

            changeNum();
        }
    };
    private LoadBuy oBuy = null;
    private final int ENTER_TABLE_VAL = 1;
    // 进行多价格,多价格不选择做法，做法需要单独再选择
    private final int ENTER_PRICE_VAL = 2;
    private final int ENTER_BUY_VAL = 3;
    private final int ENTER_WAITER_ORDER_VAL = 4;
    private final int ENTER_TABLE_ = 5;
    private final int ORDER_ZENG = 6;
    private final int ORDER_TUI = 7;
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
    private int LAST_SELECT = 0;
    private CommonBuysPop buysPop;
    private long lastClick;
    private void selectPeople() {
        HomeSelecterPeoDialog numDl = new HomeSelecterPeoDialog(selectTable,
                cy_bot_1, sharedUtils);
        numDl.intiDialog(activity,null);
    }
    private CommonTablesPop tablesPop;
    private SelectTable selectTable = new SelectTable() {

        @Override
        public void select(TableBean bean, String optName, int person) {

            if (bean != null) {
                // 设置名字，保存状态
                Intent intent = new Intent(activity,FloatService.class);
                intent.setAction(Common.SERVICE_ACTION);
                intent.putExtra("recy_table", "");
                activity.startService(intent);
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
                cyMainHandler.sendMessage(msg);

            }
        }

        @Override
        public void isLocked() {

            // 这里是不使用的接口覆盖方法
        }

    };
    private void toALL(int i){

        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = i;
        P.c("点击"+i);
        cyMainHandler.sendMessage(msg);
    }

    private CyMainHandler cyMainHandler;
    private class CyMainHandler extends Handler {
        WeakReference<CyMainActivity> mLeakActivityRef;

        public CyMainHandler(CyMainActivity leakActivity) {
            mLeakActivityRef = new WeakReference<CyMainActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {

                pars(msg);
            }
        }
    }

    private void initMenu() {
        if (FileUtils.db_exits()) {

            new Thread() {
                public void run() {
                    // 固定给一个营业点
                    menuBeans.clear();
                    menuStrs.clear();
                    String site = sharedUtils.getStringValue("sitecode");
                    P.c("营业点" + site);
                    menuBeans = DB.getInstance().getMenus(1, site);

                    for(int i=0;i<menuBeans.size();i++){
                        String key = menuBeans.get(i).getCode();
                        menuStrs.put(key,i);
                    }

                    cyMainHandler.sendEmptyMessage(0);
                }
            }.start();
        }
    }


    public void pars(final Message msg) {
        P.c("接收msg"+msg.what);
    switch (msg.what){
        case 66:
            CommonDiscountPop pop  = new CommonDiscountPop(activity);
            pop.showSheet();
            break;
        case 117:

            break;
        case 116:
            int NOW_SELECT = msg.arg1;

                bottomFlow.scrollTo(NOW_SELECT*SCROOL_WIDTH);


            break;
        case 115:
            final int bIndex = msg.arg1;
            //确定底部
            P.c("这里、。、。、。、。、。、。");
           // cyMainBottomAdapter.selectPosition(bIndex);
            if(bIndex!=-1){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message msg = new Message();
                        msg.what = 114;
                        msg.obj = String.valueOf(bIndex);
                        cyMainHandler.sendMessage(msg);
                    }
                }.start();
            }

            //coverFlow.getCoverFlowLayout().scrollToPosition(bIndex);
//            coverFlow.smoothScrollToPosition(bIndex);
            //cyMainBottomAdapter.selectPosition(bIndex);
            P.c("这里、。、。、。、。、。、。1");
            break;
        case 114:
            int tIndex = Integer.parseInt((String) msg.obj);
            coverFlow.scrollToPosition(tIndex);
            break;
        case 112:
            picMoveT.gq();
            break;
        case 11:
            NewDataToast.makeText("获取数据异常");
            break;
        case 12:
            NewDataToast.makeText((String) msg.obj);
            break;
        case -8:
            NewDataToast.makeTextD("已添加到点餐栏", 500);
            changeNum();
            dishLs();
            break;
        case -7:
            NewDataToast.makeText("超出限额");
            break;
        case -6:
            NewDataToast.makeText(getResources().getString(R.string.hasorderyet));
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
            P.c(menuBeans.size()+"长度");
            int len = menuBeans.size();
            cyMainTitleAdapter.updata(menuBeans);
            if(len!=0){
                cyMainTitleAdapter.selectPosition(0);
                index(0);
                toALL(0);
            }

            break;
        case 2:
            final int index = msg.arg1;
            if(index!=-1){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();


                        foodsBeans.clear();
                        titleKeys.clear();
                      /*  String menuCode = menuBeans.get(index).getCode();
                        int count = DB.getInstance().getCounts(menuCode);
*/

//                        foodsBeans =   DB.getInstance().getFoods(menuBeans.get(index).getCode(),10,0);

                        foodsBeans = DB.getInstance().getFoods(menuStrs);

                        processTindex();
                        cyMainHandler.sendEmptyMessage(1);
                    }
                }.start();
            }

            break;
        case 4:


            //
            FOOD_COUNT = foodsBeans.size();
            P.c("加载数据。。。。"+FOOD_COUNT);
            cyMainCenterAdapter.updata(foodsBeans);

            changeNum();
            break;
        case 5:
            P.c("刷新界面"+FOOD_COUNT);
            FOOD_COUNT = foodsBeans.size();
            cyMainCenterAdapter.updata(foodsBeans);

            changeNum();
            break;


        case 1:

            FOOD_COUNT = foodsBeans.size();
            cyMainCenterAdapter.updata(foodsBeans);
            cyMainTitleAdapter.ref(titleKeys);
            changeNum();
            if(foodsBeans.size()!=0){
//                coverFlow.scrollToPosition(0);
                //防止直接跳转引起下标越界问题
                try {
                    coverFlow.scrollToPosition(LAST_SELECT);
                } catch (Exception e) {
                    e.printStackTrace();
                    LAST_SELECT = 0;
                    coverFlow.scrollToPosition(LAST_SELECT);
                }
                cyMainBottomAdapter.updata(foodsBeans);

               // smoothMoveToPosition(0);
            }

         break;
        case -11:
            int person = msg.arg1;
            cy_bot_2
                    .setText(sharedUtils.getStringValue("table_name"));
            cy_bot_1.setText(String.valueOf(person));
            // 刷新菜单
            initMenu();
            if (person == 0) {
                Intent intent = new Intent(activity,FloatService.class);
                intent.setAction(Common.SERVICE_ACTION);
                intent.putExtra("free_table", "");
                activity.startService(intent);
                selectPeople();
            } else {
                Intent intent = new Intent(activity,FloatService.class);
                intent.setAction(Common.SERVICE_ACTION);
                intent.putExtra("open_table", "");
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
            P.c("已点菜单数量值:" + ct);
            if(ct!=0){
                dish_num.setVisibility(View.VISIBLE);
                dish_num.setText(String.valueOf(ct));
            }else{
                dish_num.setVisibility(View.INVISIBLE);
            }

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
        }
    }
    //可滚动范围
    int SCROOL_WIDTH = 180;


    /**
     * 对滑动的控制
     * @param n
     */
    private void smoothMoveToPosition(int n) {
        try {
            View from = coverFlow.getCoverFlowLayout().findViewByPosition(n);
            View to = coverFlow.getCoverFlowLayout().findViewByPosition(coverFlow.getCoverFlowLayout().getCenterPosition());

            int dir = from.getLeft()-to.getLeft();
            P.c("偏移量"+dir);
            coverFlow.smoothScrollBy(dir,0);
        } catch (Exception e) {
            e.printStackTrace();
            P.c("异常处理");
        }
    }
    private boolean isShow = true;
    @Override
    public void onResume() {
        super.onResume();
        changeNum();
        if (sharedUtils.getBooleanValue("canDiscount") && isShow) {

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    int ct = DB.getInstance().getCount();
                    P.c("允许打折"+ct);
                    if(sharedUtils.getStringValue("billId").length()==0&&ct==0){
                        cyMainHandler.sendEmptyMessage(66);
                    }
                }
            }.start();




        } else {
            isShow = true;
        }

    }

    /**
     * 改变菜品总数量
     */
    private void changeNum() {
        new Thread() {
            @Override
            public void run() {
                P.c("修改菜品数量");
                super.run();
                Message msg = new Message();
                msg.what = -10;
                msg.arg1 = DB.getInstance().getCount();
                cyMainHandler.sendMessage(msg);
            }
        }.start();

    }
    private int mIndex = 0;
    private void move(int n){
        if (n<0 || n>=cyMainCenterAdapter.getItemCount() ){
            NewDataToast.makeText("超出范围了");
            return;
        }
        mIndex = n;
        coverFlow.stopScroll();
        smoothMoveToPosition(n);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENTER_TABLE_VAL && resultCode == 1000) {
            //
            down();
            if (data.getIntExtra("result", 0) == 1 && data.hasExtra("optName")) {
                /*tablesPop = new CommonTablesPop(activity,
                        selectTable, data.getStringExtra("optName"));
                tablesPop.showSheet();*/
                String user = data.getStringExtra("optName");
                if (user.equals(sharedUtils.getStringValue("user"))) {
                    NewDataToast.makeText(getResources().getString(R.string.loginclearsuccess));
                    sharedUtils.clear("user");
                    //call_waiter.setText("服务员");
                    sharedUtils.clear("user");
                    sharedUtils.clear("userName");
                    call_waiter.setText("服务员");
                } else {
                    NewDataToast.makeText(getResources().getString(R.string.loginsuccess));
                    //call_waiter.setText("服务员:" + user);
                    String name = DB.getInstance().getName(user);
                    sharedUtils.setStringValue("user", user);
                    P.c("服务员登录名:" + user);
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
            int num = data.getIntExtra("num", 0);
            tui(optName, num, obj, tag);

        } else if (requestCode == ORDER_ZENG && resultCode == 1000) {
            //赠送
            if (loadSendPop == null) {
                loadSendPop = new CommonLoadSendPop(activity, "正在赠菜");
                loadSendPop.showSheet(false);
            }
            String optName = data.getStringExtra("optName");
            DishTableBean obj = (DishTableBean) data.getSerializableExtra("obj");
            int num = data.getIntExtra("num", 0);
            zeng(optName, num, obj);
        }
    }
    private RequestCall tableCall;
    private CommonLoadSendPop loadSendPop;
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
    private void cancelTable() {
        if (loadSendPop != null) {
            loadSendPop.cancle();
            loadSendPop = null;
        }
        if (tableCall != null) {
            tableCall.cancel();
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
    private CommonTzPop tzPop;



    private RequestCall tuiRequestCall;

    private void tui(String optName, int num, DishTableBean obj, String reason) {
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
            tuiRequestCall.execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    P.c("退菜。。" + e.getLocalizedMessage());
                    cyMainHandler.sendEmptyMessage(11);
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
                                    sharedUtils, tz, true, cy_bot_1);
                            buysPop.showSheet();
                        } else {
                            Message msg = new Message();
                            msg.obj = object.getString("Data");
                            msg.what = 12;
                            cyMainHandler.sendMessage(msg);
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

    private void zeng(String optName, int num, final DishTableBean obj) {
        String ip = sharedUtils.getStringValue("IP");
        String order = sharedUtils.getStringValue("billId");
        if (order.length() == 0) {
            NewDataToast.makeText("订单异常");
            return;
        }
        String post = "{\"BillNo\":\"" + order + "\",\"SerialNo\":\"" + obj.getI() + "\",\"Number\":" + num + ",\"UserName\":\"" + optName + "\"}";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", post);
            zengRequestCall = OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_ORDER_ZENG))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build();
            zengRequestCall.execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    cyMainHandler.sendEmptyMessage(11);
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
                            cyMainHandler.sendMessage(msg);


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


}
