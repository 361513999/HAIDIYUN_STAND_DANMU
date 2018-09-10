package pad.stand.com.haidiyun.cy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pad.stand.com.haidiyun.cy.widget.EffectiveShapeView;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.PicPoint;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.PicMoveT;
import pad.stand.com.haidiyun.www.widget.NewDataToast;
import pad.stand.com.haidiyun.www.widget.T_Image;

public class CyMainCenterAdapter extends RecyclerView.Adapter<CyMainCenterAdapter.ViewHolder> {
    private Context context;
    public DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private  ArrayList<FoodsBean> foodsBeans;
    private SharedUtils sharedUtils;
    private PicMoveT picMoveT;
    private Handler handler;
    public CyMainCenterAdapter(Context context, ArrayList<FoodsBean> foodsBeans, SharedUtils sharedUtils, PicMoveT picMoveT, Handler handler){
        this.context = context;
        this.foodsBeans = foodsBeans;
        this.sharedUtils = sharedUtils;
        this.picMoveT = picMoveT;
        this.handler = handler;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cy_main_center_item,parent,false);

        return new ViewHolder(v);
    }

    public void updata(ArrayList<FoodsBean> foodsBeans){
        this.foodsBeans = foodsBeans;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final FoodsBean obj = foodsBeans.get(position);
        String url = "file://" + Common.ZIP + obj.getPath();
        // P.c(obj.getName()+"=="+url);
		Glide.with(BaseApplication.application).load(url).asBitmap()
				.diskCacheStrategy(DiskCacheStrategy.RESULT) .placeholder(R.drawable.default_image)//默认显示图片
                .error(R.drawable.default_image)//图片加载错误显示的图片
                .into(viewHolder.img);
      /* ImageLoader.getInstance().displayImage(url,
                viewHolder.img,
                options);*/
        viewHolder.img.setTag(R.id.image_tag, url);
        viewHolder.item1.setText(obj.getName());
        viewHolder.item4.setText("/"+obj.getUnit());
        viewHolder.food_gv_item_price.setText("￥"+obj.getPrice()+" / "+obj.getUnit());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClick!=null){
                        itemClick.clickItem(position);
                    }
                }
            });
        final double limit = obj.getOrderMinLimit();
        if (obj.getCount() == 0) {
            viewHolder.food_gv_item_order.setText(context.getResources().getString(R.string.order));
            viewHolder.food_gv_item_order.setEnabled(true);
        } else {
            if (obj.isMore()) {
                viewHolder.food_gv_item_order.setText(context.getResources().getString(R.string.continueorder));
                viewHolder.food_gv_item_order.setEnabled(true);
                viewHolder.food_gv_item_order.setTextColor(context
                        .getResources().getColor(R.color.pik));
                // more(viewHolder, obj);
            } else {
                viewHolder.food_gv_item_order.setText(context.getResources().getString(R.string.has_order));
                viewHolder.food_gv_item_order.setEnabled(false);
                viewHolder.food_gv_item_order.setTextColor(context
                        .getResources().getColor(R.color.grey));
            }
        }
        if (obj.isSuit() && obj.getChargeMode().equals("A")) {
            viewHolder.food_gv_item_price.setText(context.getResources().getString(R.string.nowprice));
        } else {
            if (sharedUtils.getStringValue("tableType").equals("Price1")) {
                viewHolder.food_gv_item_price.setText("¥" + obj.getPrice1());
            } else if (sharedUtils.getStringValue("tableType").equals("Price2")) {
                viewHolder.food_gv_item_price.setText("¥" + obj.getPrice2());
            } else {
                viewHolder.food_gv_item_price.setText("¥" + obj.getPrice());
            }
        }
        //点击点餐
        viewHolder.food_gv_item_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                P.c("00");
                if (Common.guSttxKeys.containsKey(obj.getCode())) {
                    //先处理是否沽清
                    NewDataToast.makeTextL("亲，" + obj.getName() + "菜品已售罄，欢迎下次品尝", 2000);
//                    NewDataToast.makeText("已沽清");
                    Message msg = new Message();
                    msg.what = 113;
                    msg.obj = obj.getCode();
                    handler.sendMessage(msg);

                    return;
                }
                P.c("01");
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


                if (!obj.isWeigh() || (obj.isRequire_cook() && sharedUtils.getBooleanValue("is_reas") && obj.isWeigh())) {
//
                    if (obj.isRequire_cook()) {
                        P.c("02");
                        // 显示口味选择

                        if (sharedUtils.getBooleanValue("is_reas")) {//口味必选
                            //选择口味
                            P.c("03");
                            picMoveT.rb(obj);
                        } else {//否则直接加到菜篮子
                            P.c("04");
                            boolean flag = DB.getInstance().addDishToPad(obj,
                                    resons, remark, "", "", limit > 0 ? limit + "" : Common.DEFAULT_NUM, handler);
                            if (flag) {
                                addX(viewHolder.img);
                                //新界面图片效果有问题,用以下代替adapetr
                               // picMoveT.addP(null);
                            }
                            //直接点餐
                        }

                    }
                    // 是套餐
                    else if (obj.isSuit()) {
                        P.c("05");
                        picMoveT.tc(obj);
                    } else {
                        P.c("06");
                        // 不显示口味选择，普通菜品
                        P.c("2222222");
                        boolean flag = DB.getInstance().addDishToPad(obj,
                                resons, remark, "", "", limit > 0 ? limit + "" : Common.DEFAULT_NUM + "", handler);
                        if (flag) {
                            addX(viewHolder.img);
                        //    picMoveT.addP(null);
                        }
                    }
//                        }
                } else {
                    P.c("07");
                    //称重商品
                    picMoveT.cz(obj, resons, remark, viewHolder.img);
                }

//                    } else {
//                        picMoveT.gq();
//                    }

            }
        });


    }
    public void addX(T_Image im) {
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
    @Override
    public int getItemCount() {
        return foodsBeans.size();
    }
    class  ViewHolder extends  RecyclerView.ViewHolder{
        EffectiveShapeView img;
        TextView item1,food_gv_item_price,food_gv_item_order,item4;
        LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            item1 = (TextView) itemView.findViewById(R.id.item1);
            food_gv_item_price = (TextView) itemView.findViewById(R.id.item2);
            food_gv_item_order = (TextView) itemView.findViewById(R.id.item3);
            item4 = (TextView) itemView.findViewById(R.id.item4);
            img = (EffectiveShapeView) itemView.findViewById(R.id.item0);
        }
    }
    private onItemClick itemClick;

    /**
     * 增加点击按钮
     *
     * @param click
     */
    public void setOnItemClick(onItemClick click){
        this.itemClick = click;
    }
    public interface onItemClick {
        void clickItem(int pos);
    }
}
