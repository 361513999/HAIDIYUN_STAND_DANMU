package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pad.stand.com.haidiyun.www.R;
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

public class FoodsAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private ArrayList<FoodsBean> foodsBeans;
    private Context context;
    private PicMoveT picMoveT;
    private Handler handler;
    private Handler handler1;
    private SharedUtils sharedUtils;
    private String num = "";
    private String keys[] = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "←"};

    public FoodsAdapter(Context context, ArrayList<FoodsBean> foodsBeans,
                        PicMoveT picMoveT, Handler handler) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.foodsBeans = foodsBeans;
        this.picMoveT = picMoveT;
        this.handler = handler;
        this.sharedUtils = new SharedUtils(Common.CONFIG);
    }

    @Override
    public int getCount() {
        return foodsBeans.size();
    }

    public void updata(ArrayList<FoodsBean> foodsBeans) {
        this.foodsBeans = foodsBeans;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView is_md_price, is_shijia, is_weigh, is_discount, is_suit, tv_limit;
        T_Image img;
        TextView food_gv_item_name, food_gv_item_price, food_gv_item_order,
                food_gv_item_count, food_gv_item_jian, food_gv_item_add;
        LinearLayout food_gv_item_to_order;
    }

	/*private int mposition = -1;

	public void selectPosition(int mposition) {
		this.mposition = mposition;
		notifyDataSetChanged();
	}*/

    /**
     * 点餐上面的小角标
     *
     * @param is
     * @param v
     */
    private void showTip(boolean is, View v) {
        if (is) {
            v.setVisibility(View.VISIBLE);

        } else {
            v.setVisibility(View.GONE);
        }
    }

    public DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(0).showImageOnFail(0)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            if (sharedUtils.getBooleanValue("is_txt")) {
                //文字模式
                convertView = inflater.inflate(R.layout.food_item_txt, null);
            } else {
                convertView = inflater.inflate(R.layout.food_item, null);
            }
            viewHolder.food_gv_item_name = (TextView) convertView
                    .findViewById(R.id.food_gv_item_name);
            viewHolder.food_gv_item_price = (TextView) convertView
                    .findViewById(R.id.food_gv_item_price);
            viewHolder.food_gv_item_order = (TextView) convertView
                    .findViewById(R.id.food_gv_item_order);
            viewHolder.img = (T_Image) convertView
                    .findViewById(R.id.food_gv_item_img);
            viewHolder.food_gv_item_count = (TextView) convertView
                    .findViewById(R.id.food_gv_item_count);
            viewHolder.food_gv_item_to_order = (LinearLayout) convertView
                    .findViewById(R.id.food_gv_item_to_order);
            viewHolder.food_gv_item_jian = (TextView) convertView
                    .findViewById(R.id.food_gv_item_jian);
            viewHolder.food_gv_item_add = (TextView) convertView
                    .findViewById(R.id.food_gv_item_add);
            viewHolder.is_shijia = (TextView) convertView
                    .findViewById(R.id.is_shijia);
            viewHolder.is_md_price = (TextView) convertView
                    .findViewById(R.id.is_md_price);
            viewHolder.is_weigh = (TextView) convertView
                    .findViewById(R.id.is_weigh);
            viewHolder.is_discount = (TextView) convertView
                    .findViewById(R.id.is_discount);
            viewHolder.is_suit = (TextView) convertView
                    .findViewById(R.id.is_suit);
            viewHolder.tv_limit = (TextView) convertView
                    .findViewById(R.id.tv_limit);

            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
                    + position);
        }
        try {

            if (position < foodsBeans.size()) {

                final FoodsBean obj = foodsBeans.get(position);
                String url = "file://" + Common.ZIP + obj.getPath();
//                P.c(obj.getName() + "==" + url);
                if (obj.getPath() != null) {
                    ImageLoader.getInstance().displayImage(url,
                            viewHolder.img,
                            options);
                }
                viewHolder.img.setTag(R.id.image_tag, url);
                if (sharedUtils.getBooleanValue("is_giv") || sharedUtils.getBooleanValue("is_txt")) {
                    viewHolder.food_gv_item_name.setTextSize(22);
                    viewHolder.food_gv_item_price.setTextSize(22);
                } else {
                    viewHolder.food_gv_item_name.setTextSize(18);
                    viewHolder.food_gv_item_price.setTextSize(18);
                }
                if (obj.getCount() == 0) {
                    viewHolder.food_gv_item_to_order.setVisibility(View.GONE);
                    viewHolder.food_gv_item_order.setText(context.getResources().getString(R.string.order));
                    viewHolder.food_gv_item_order.setEnabled(true);
                } else {
                    if (obj.isMore()) {
                        viewHolder.food_gv_item_to_order.setVisibility(View.GONE);
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
                        viewHolder.food_gv_item_to_order.setVisibility(View.VISIBLE);
                        viewHolder.food_gv_item_count.setText(String.valueOf(obj
                                .getCount()));
                    }
                }
                final double limit = obj.getOrderMinLimit();
                if (limit > 0 && !sharedUtils.getBooleanValue("is_txt")) {
                    viewHolder.tv_limit.setVisibility(View.VISIBLE);
                    viewHolder.tv_limit.setText(limit + context.getResources().getString(R.string.min));
                } else {
                    viewHolder.tv_limit.setVisibility(View.GONE);
                }
                viewHolder.food_gv_item_count.setTextSize(60);
                if (Common.guSttxKeys.containsKey(obj.getCode())) {
                    String num = Common.guSttxKeys.get(obj.getCode());
                    P.c(obj.getName()+"这个数据是"+num);
                if (num.equals("0.0")) {
                    viewHolder.food_gv_item_to_order.setVisibility(View.VISIBLE);
                    viewHolder.food_gv_item_add.setVisibility(View.INVISIBLE);
                    viewHolder.food_gv_item_jian.setVisibility(View.INVISIBLE);
                    viewHolder.food_gv_item_count.setTextSize(30);
                    viewHolder.food_gv_item_count.setText(context.getResources().getString(R.string.soldover));
                    viewHolder.food_gv_item_order.setText(context.getResources().getString(R.string.hassoldover));
                    viewHolder.food_gv_item_order.setTextColor(context
                            .getResources().getColor(R.color.grey));
                   viewHolder.food_gv_item_order.setEnabled(false);
                }
                }
//            String s = obj.toString();
                if (sharedUtils.getBooleanValue("is_lan")) {
                    //变为英文
                    viewHolder.food_gv_item_name.setText(obj.getNameEn());
                } else {
                    viewHolder.food_gv_item_name.setText(obj.getName());
                }
                String tableType = sharedUtils.getStringValue("tableType");
                if (obj.isSuit() && obj.getChargeMode().equals("A")) {
                    viewHolder.food_gv_item_price.setText(context.getResources().getString(R.string.nowprice));
                } else {
                    if (tableType.equals("Price")) {
                        viewHolder.food_gv_item_price.setText("¥" + obj.getPrice());
                    } else if (tableType.equals("Price1")) {
                        viewHolder.food_gv_item_price.setText("¥" + obj.getPrice1());
                    } else {
                        viewHolder.food_gv_item_price.setText("¥" + obj.getPrice2());
                    }
                }
                //点击点餐
                viewHolder.food_gv_item_order.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {


                        if (Common.guSttxKeys.containsKey(obj.getCode())) {
                            //先处理是否沽清
//                        NewDataToast.makeText("已沽清");
                            String num = Common.guSttxKeys.get(obj.getCode());
                            if(num.equals("0.0")){
                                NewDataToast.makeTextL("亲，" + obj.getName() + "菜品已售罄，欢迎下次品尝", 2000);
                                Message msg = new Message();
                                msg.what = 113;
                                msg.obj = obj.getCode();
                                handler.sendMessage(msg);
                                return;
                            }else{
                                handler.sendEmptyMessage(116);
                            }



                        } else {
                            handler.sendEmptyMessage(116);
                        }

                /*
                 * boolean flag = DB.getInstance().addDishToPad(obj); if(flag){
				 * P.c("有图片动�?); addX(viewHolder.img); }
				 */
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

                        // 是否估清
//                    if (!Common.guSttxKeys.containsKey(obj.getCode()) && !Common.guSttxKeys.get(obj.getCode()).equals("0.0")) {
                        //未估清,不是称重或者(是称重并且口味必选)
//                    String sss = obj.toString();
                        if (!obj.isWeigh() || (obj.isRequire_cook() && sharedUtils.getBooleanValue("is_reas") && obj.isWeigh())) {
//                        if (obj.isPriceMofidy() || obj.isTemp()) {
//                            P.c("弹出服务员");
//                            // 临时菜,需要确认价格才能点击
//                            //picMoveT.sj(obj);
//                            boolean flag = DB.getInstance().addDishToPad(obj,
//                                    resons, remark, "", "", limit > 0 ? limit + "" : Common.DEFAULT_NUM, handler);
//                            if (flag) {
//                                addX(viewHolder.img);
//                            }
//                            //改为直接下单
//                            //改价,可能这里造成点不了
//                            DishTableBean bean = new DishTableBean();
//                            bean.setName(obj.getName());
//                            bean.setCode(obj.getCode());
//                            bean.setPrice(obj.getPrice());
//                            bean.setTemp(obj.isTemp());
//                            bean.setPrice_modify(obj.isPriceMofidy());
//                            bean.setI(obj.getId());
//                            Intent intent = new Intent(context, PriceActivity.class);
//                            intent.putExtra("obj", bean);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
//                        } else {
                            if (obj.isRequire_cook()) {
                                // 显示口味选择
                                if (sharedUtils.getBooleanValue("is_reas")) {//口味必选
                                    //选择口味
                                    picMoveT.rb(obj);
                                } else {//否则直接加到菜篮子
                                    boolean flag = DB.getInstance().addDishToPad(obj,
                                            resons, remark, "", "", limit > 0 ? limit + "" : Common.DEFAULT_NUM, handler);
                                    if (flag) {
                                        addX(viewHolder.img);
                                    }
                                    //直接点餐
                                }

                            }
                            // 是套餐
                            else if (obj.isSuit()) {
                                picMoveT.tc(obj);
                            } else {
                                // 不显示口味选择，普通菜品
                                P.c("2222222");
                                boolean flag = DB.getInstance().addDishToPad(obj,
                                        resons, remark, "", "", limit > 0 ? limit + "" : Common.DEFAULT_NUM + "", handler);
                                if (flag) {
                                    addX(viewHolder.img);
                                }
                            }
//                        }
                        } else {
                            //称重商品
                            picMoveT.cz(obj, resons, remark, viewHolder.img);
                        }
                    }
                });
                //放大图片
                viewHolder.img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        if (obj.isImage()) {
                            picMoveT.view(obj, position);
                        }
                    }
                });

                viewHolder.food_gv_item_add.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        picMoveT.add(obj);
                    }
                });

                viewHolder.food_gv_item_jian.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        picMoveT.mics(obj);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private PicPoint getPoint(T_Image im) {
        int[] lo = new int[2];
        int[] lo1 = new int[2];
        im.getLocationInWindow(lo);
        im.getLocationOnScreen(lo1);
        PicPoint point = new PicPoint();
        point.setHeight(im.getMeasuredHeight());
        point.setWidth(im.getMeasuredWidth());
        point.setPoint(lo1);
        point.setPic(im.getTag(R.id.image_tag).toString());
        return point;

    }

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

}
