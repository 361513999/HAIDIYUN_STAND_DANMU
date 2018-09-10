package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.NumAdapter;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.inter.NumSel;

/**
 * Created by Administrator on 2017/6/24/024.
 */

public class CommonNum {
    private IDialog dlg;
    private Context context;
    private NumSel numSel;
    private Object object;
    private String remark;
    private ArrayList<ReasonBean> resons;
    private T_Image im;
    private double num;

    public CommonNum(Context context, NumSel numSel, Object object, double num) {
        this.context = context;
        this.numSel = numSel;
        this.object = object;
        this.num = num;
    }

    private TextView view, sure;
    private GridView keyboard;
    private NumAdapter numAdapter;
    private String keys[] = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "←"};

    public CommonNum(Context context, NumSel numSel, Object object, double num, ArrayList<ReasonBean> resons, String remark, T_Image im) {
        this.context = context;
        this.numSel = numSel;
        this.object = object;
        this.resons = resons;
        this.remark = remark;
        this.im = im;
        this.num = num;
    }

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.menu_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.common_num_select, null);
        view = (TextView) layout.findViewById(R.id.view);
        sure = (TextView) layout.findViewById(R.id.sure);
        keyboard = (GridView) layout.findViewById(R.id.keyboard);
        numAdapter = new NumAdapter(context, keys, handler);
        keyboard.setAdapter(numAdapter);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = view.getText().toString();
                if (temp.equals("")) {
                    return;
                }
                double tempDouble = Double.valueOf(temp);
                if (tempDouble >= num) {
                    if (temp.length() != 0 && Double.parseDouble(temp) != 0) {
                        if (resons != null) {
                            numSel.changeWeigh(view.getText().toString(), object, resons, remark, im);
                        } else {
                            numSel.change(view.getText().toString(), object);
                        }
                        close();
                    }
                } else {
                    NewDataToast.makeText(context.getResources().getString(R.string.minorder) + num);
                    close();
                }

            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private void close() {
        if (dlg != null) {
            dlg.cancel();
            dlg = null;
        }
    }

    private double formatOne(double total) {
        BigDecimal b = new BigDecimal(total);
        // 保留2位小数
        double total_v = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_v;
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    int num = msg.arg1;
                    String key = keys[num];
                    String num_view = view.getText().toString();
                    if (num == keys.length - 1) {
                        //删除按钮
                        P.c("删除");
                        try {
                            String temp = num_view.substring(0, num_view.length() - 1);
                            view.setText(temp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            String temp = num_view + key;

                            if (temp.contains(".") && temp.split("\\.").length == 2 && temp.split("\\.")[1].length() > 1) {
                                return;
                            }
                            Double.parseDouble(temp);
                            view.setText(temp);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    break;
            }
        }
    };
}
