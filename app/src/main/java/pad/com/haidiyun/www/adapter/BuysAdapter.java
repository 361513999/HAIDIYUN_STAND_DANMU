package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.SuitItemBean;
import pad.com.haidiyun.www.common.TimeUtil;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.BuyClick;
import pad.com.haidiyun.www.inter.ReasonI;
import pad.com.haidiyun.www.widget.CommonResPop;

public class BuysAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<DishTableBean> dishTableBeans;
    private ArrayList<SuitItemBean> listSuit;
    private Context context;
    private BuyClick buyClick;
    private ReasonI reasonBuy;

    public BuysAdapter(Context context, ArrayList<DishTableBean> dishTableBeans, BuyClick buyClick, ReasonI reasonI) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.buyClick = buyClick;
        this.dishTableBeans = dishTableBeans;
        this.reasonBuy = reasonI;
    }

    @Override
    public int getCount() {
        //能设置的最大值
        return dishTableBeans.size();
    }

    private boolean isNet = false;

    public void updata(ArrayList<DishTableBean> dishTableBeans, boolean isNet) {
        this.isNet = isNet;
        this.dishTableBeans = dishTableBeans;
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
        TextView name;
        TextView price;
        TextView delete;
        TextView add;
        ImageView remove;
        TextView view;
        TextView res;
        TextView tag;
        TextView prg;
        TextView reason_name;
        LinearLayout auto;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.flip_buy_item, null);
            viewHolder.add = (TextView) convertView.findViewById(R.id.add);
            viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.view = (TextView) convertView.findViewById(R.id.view);
            viewHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
            viewHolder.res = (TextView) convertView.findViewById(R.id.res);
            viewHolder.prg = (TextView) convertView.findViewById(R.id.prg);
            viewHolder.tag = (TextView) convertView.findViewById(R.id.tag);
            viewHolder.auto = (LinearLayout) convertView.findViewById(R.id.auto);
            viewHolder.reason_name = (TextView) convertView.findViewById(R.id.reason_name);
            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
        }

        final DishTableBean obj = dishTableBeans.get(position);
//        P.c("菜品:"+obj.toString());
        //判断做法
        if (obj.isMore()) {
            viewHolder.res.setVisibility(View.VISIBLE);
        } else {
            viewHolder.res.setVisibility(View.GONE);
        }
        /*if (obj.isTemp() || obj.isPrice_modify()) {
            viewHolder.prg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.prg.setVisibility(View.GONE);
        }*/
        if (isNet) {//已下单
            viewHolder.add.setVisibility(View.INVISIBLE);
            viewHolder.delete.setVisibility(View.INVISIBLE);
            viewHolder.remove.setVisibility(View.INVISIBLE);
        } else {
            //未下单
            viewHolder.add.setVisibility(View.VISIBLE);
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.remove.setVisibility(View.VISIBLE);
        }
        if (obj.isTemp()) {
            viewHolder.tag.setVisibility(View.VISIBLE);
            viewHolder.tag.setText("时");
        } else {
            viewHolder.tag.setVisibility(View.GONE);
        }
        if (obj.isSuit()) {
            viewHolder.tag.setText("套");
            viewHolder.tag.setVisibility(View.VISIBLE);
            viewHolder.auto.setVisibility(View.VISIBLE);
            //套餐多做法
            String temp = obj.getDetailNames();
            String codeSuit = obj.getSuitMenuDetail();
            String tcCode = obj.getTc_cook_codes();
            String tcName = obj.getTc_cook_names();
            String tcPrice = obj.getTc_cook_prices();
            //-------------------------------------------------套餐列表
            if (temp.length() != 0) {//套餐有做法
                final String tps[] = temp.split(",");
                final String tps1[] = codeSuit.split(",");
                if (tcCode != null) {
                    if (tcCode.contains(",")) {
                        tcCode = tcCode.replace(",", "&");
                        tcName = tcName.replace(",", "&");
                        tcPrice = tcPrice.replace(",", "&");
                    }
                    if (tcCode.contains(";")) {
                        tcCode = tcCode.replace(";", ",");
                        tcName = tcName.replace(";", ",");
                        tcPrice = tcPrice.replace(";", ",");
                    }
                }
//                02502|+8元配罗宋汤;02617|;00306|
                String finalValue[] = null;
                if (tcCode != null) {
                    if (tcCode.contains(",")) {
                        finalValue = tcName.split(",");
                    }
                }
                viewHolder.auto.removeAllViews();
                for (int i = 0; i < tps.length; i++) {//3
                    View layout = inflater.inflate(R.layout.flip_buy_txt, null);
                    TextView childView = (TextView) layout.findViewById(R.id.txt);
                    TextView detail = (TextView) layout.findViewById(R.id.detail);
                    final TextView res = (TextView) layout.findViewById(R.id.res);
                    final int finalI = i;
                    final String clickCode = tps1[finalI];
                    if (DB.getInstance().getCookClassDataCount(clickCode) == 0) {
                        res.setVisibility(View.GONE);
                        detail.setVisibility(View.GONE);
                    } else {
                        res.setVisibility(View.VISIBLE);
                        detail.setVisibility(View.VISIBLE);
                    }
                    res.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonResPop resPop = new CommonResPop(context, reasonBuy, obj, null, true, position, clickCode);
                            resPop.showSheet();
                        }
                    });
                    childView.setText(tps[i]);
//                    02502|+8元配罗宋汤;02617|;00306|
                    if (finalValue != null) {
                        if (tps.length == finalValue.length)
                            if (finalValue[i] != null && !"".equals(finalValue[i])) {
                                if (finalValue[i].contains("|")) {
                                    String[] right = finalValue[i].split("\\|");
                                    if (right.length > 1) {
                                        String value = finalValue[i].split("\\|")[1];
                                        value = value.replace("&", ",");
                                        detail.setText(value);
                                    }
                                }
                            }
                    }
                    viewHolder.auto.addView(layout);
                    //查询所属分类    ???
                    DB.getInstance().findSortClassMenu(clickCode);
                }
            }
        } else {
            viewHolder.tag.setVisibility(View.GONE);
        }
        viewHolder.prg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyClick.price(obj);
            }
        });
        viewHolder.res.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                buyClick.res(obj);
            }
        });
        viewHolder.view.setText(String.valueOf(obj.getCount()));
        viewHolder.name.setText(obj.getName());
        viewHolder.price.setText(obj.getPrice() * obj.getCount() + "元");
        viewHolder.price.setText(TimeUtil.doubleReverse(obj.getPrice() * obj.getCount()) + "元");
        viewHolder.reason_name.setText(getRes(obj));
        viewHolder.add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                buyClick.add(obj);
            }
        });
        viewHolder.remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                buyClick.remove(obj);
            }
        });
        viewHolder.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                buyClick.delete(obj);
            }
        });
        return convertView;
    }

    /**
     * 计算已选做法
     *
     * @param obj
     * @return
     */
    private String getRes(DishTableBean obj) {
        StringBuilder builder = new StringBuilder();
        ArrayList<ReasonBean> rbs = obj.getReasonBeans();
        int len = rbs == null ? 0 : rbs.size();
        for (int i = 0; i < len; i++) {
            ReasonBean rb = rbs.get(i);
            builder.append(rb.getName());
            if (rb.getPrice() != 0) {
                builder.append(rb.getPrice());
            }
            if (i != len - 1) {
                builder.append(",");
            }
        }
        return builder.toString();

    }
}
