package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.BuyClick;
import pad.stand.com.haidiyun.www.inter.ReasonI;
import pad.stand.com.haidiyun.www.widget.CommonResPop;

public class BuysAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<DishTableBean> dishTableBeans;
    private Context context;
    private BuyClick buyClick;
    private ReasonI reasonBuy;
    private SharedUtils sharedUtils;

    public BuysAdapter(Context context, ArrayList<DishTableBean> dishTableBeans, BuyClick buyClick, ReasonI reasonI) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.buyClick = buyClick;
        this.dishTableBeans = dishTableBeans;
        this.reasonBuy = reasonI;
        this.sharedUtils = new SharedUtils(Common.CONFIG);

    }

    @Override
    public int getCount() {
        //能设置的最大值
        return dishTableBeans.size();
    }

    private boolean isNet = false;

    public void updata(ArrayList<DishTableBean> dishTableBeans, boolean isNet) {
        this.isNet = isNet;
        if (isNet) {
            notNet = true;
        }
        this.dishTableBeans = dishTableBeans;
        notifyDataSetChanged();
    }

    private boolean notNet = true;

    public void changeTZ(boolean notNet) {
        this.notNet = notNet;
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
        TextView tui;
        TextView zengsong;
        TextView view;
        TextView res, prg;
        TextView tag;
        TextView reason_name;
        LinearLayout ts;
        LinearLayout auto,jjl;
        ToggleButton jj;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.buy_item, null);
            viewHolder.add = (TextView) convertView.findViewById(R.id.add);
            viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.view = (TextView) convertView.findViewById(R.id.view);
            viewHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
            viewHolder.res = (TextView) convertView.findViewById(R.id.res);
            viewHolder.prg = (TextView) convertView.findViewById(R.id.prg);
            viewHolder.tag = (TextView) convertView.findViewById(R.id.tag);
            viewHolder.reason_name = (TextView) convertView.findViewById(R.id.reason_name);
            viewHolder.tui = (TextView) convertView.findViewById(R.id.tui);
            viewHolder.zengsong = (TextView) convertView.findViewById(R.id.zengsong);
            viewHolder.ts = (LinearLayout) convertView.findViewById(R.id.ts);
            viewHolder.jjl = (LinearLayout) convertView.findViewById(R.id.jjl);
            viewHolder.jj = (ToggleButton) convertView.findViewById(R.id.jj);
            viewHolder.auto = (LinearLayout) convertView.findViewById(R.id.auto);
            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
        }

        final DishTableBean obj = dishTableBeans.get(position);
        if (obj.isMore()) {
            viewHolder.res.setVisibility(View.VISIBLE);
        } else {
            viewHolder.res.setVisibility(View.GONE);
        }
        if ((obj.isTemp() || obj.isPrice_modify()) && sharedUtils.getBooleanValue("is_edit_price")) {
            viewHolder.prg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.prg.setVisibility(View.GONE);
        }

        if (isNet) {
            viewHolder.add.setVisibility(View.INVISIBLE);
            viewHolder.delete.setVisibility(View.INVISIBLE);
            viewHolder.jjl.setVisibility(View.GONE);
            viewHolder.ts.setVisibility(View.VISIBLE);


        } else {
            //未下单
            viewHolder.add.setVisibility(View.VISIBLE);
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.jjl.setVisibility(View.VISIBLE);
            viewHolder.ts.setVisibility(View.GONE);
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
            String temp = obj.getDetailNames();
            String codeSuit = obj.getSuitMenuDetail();
            String tcCode = obj.getTc_cook_codes();
            String tcName = obj.getTc_cook_names();
            String tcPrice = obj.getTc_cook_prices();
            if (temp.length() != 0) {
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
                /*for (int i = 0; i < tps.length; i++) {
                    View layout = inflater.inflate(R.layout.buy_txt, null);
                    TextView childView = (TextView) layout.findViewById(R.id.txt);
                    childView.setText(tps[i]);
                    viewHolder.auto.addView(layout);
                }*/
                //---------------------------------------
                //套餐列表
                for (int i = 0; i < tps.length; i++) {//3
                    View layout = inflater.inflate(R.layout.buy_txt, null);
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
//                            buyClick.res(obj);
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
                }
                //---------------------------------------
            }

        } else {
            viewHolder.tag.setVisibility(View.GONE);
        }
        viewHolder.res.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                buyClick.res(obj);
            }
        });
        viewHolder.prg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyClick.price(obj);
            }
        });
        if (obj.getLt() != 0) {
            viewHolder.view.setTextColor(context.getResources().getColor(R.color.red));
        }

        viewHolder.jj.setChecked(obj.isIsjj());
        viewHolder.jj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                obj.setIsjj(b);
                notifyDataSetChanged();
               new Thread(){
                   @Override
                   public void run() {
                       super.run();
                       DB.getInstance().changeJJ(obj,b);
                   }
               }.start();
            }
        });
        viewHolder.view.setText(String.valueOf(obj.getCount()));
        if (sharedUtils.getBooleanValue("is_lan")) {
            //变为英文
            viewHolder.name.setText(obj.getNameEn());
        } else {
            viewHolder.name.setText(obj.getName());
        }
        if (sharedUtils.getStringValue("tableType").equals("Price1")) {
            viewHolder.price.setText(TimeUtil.doubleReverse(obj.getPrice1()) + context.getResources().getString(R.string.yuan) + "(" + obj.getUnit() + ")");
        } else if (sharedUtils.getStringValue("tableType").equals("Price2")) {
            viewHolder.price.setText(TimeUtil.doubleReverse(obj.getPrice2()) + context.getResources().getString(R.string.yuan) + "(" + obj.getUnit() + ")");
        } else {
            viewHolder.price.setText(TimeUtil.doubleReverse(obj.getPrice()) + context.getResources().getString(R.string.yuan) + "(" + obj.getUnit() + ")");
        }
        viewHolder.reason_name.setText(getRes(obj));
        viewHolder.add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                buyClick.add(obj);
            }
        });
        viewHolder.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示
                if (!isNet) {
                    buyClick.num(obj);
                }
            }
        });
        viewHolder.remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                buyClick.remove(obj);
            }
        });
        if (notNet) {
            viewHolder.tui.setVisibility(View.INVISIBLE);
            viewHolder.zengsong.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tui.setVisibility(View.VISIBLE);
            viewHolder.zengsong.setVisibility(View.VISIBLE);
        }
        viewHolder.tui.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //退菜
                //0是退菜
                buyClick.tz(obj, 0);
            }
        });
        viewHolder.zengsong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyClick.tz(obj, 1);
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
