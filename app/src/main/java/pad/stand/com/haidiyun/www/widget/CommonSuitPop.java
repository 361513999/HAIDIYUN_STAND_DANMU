package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.TcAdapter;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.TcBean;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.TcT;
import pad.stand.com.haidiyun.www.widget.dialog.Animations.Effectstype;

/**
 * 套餐的弹出框
 *
 * @author Administrator
 */
public class CommonSuitPop {
    private Context context;
    private FoodsBean foodsBean;
    private List<TcBean> tcbean = new ArrayList<TcBean>();
    private List<TcBean> noneedTcbean = new ArrayList<TcBean>();
    private TDialog dlg;
    private String num;
    private TcAdapter muchAdapter;
    private TcAdapter noNeedAdapter;
    //
    private TextView name;
    TcT tcT;
    // 必选
    HorizontalScrollView muchSel;
    GridView muchGV;
    //	LinearLayout muchll;
    // 可选
    HorizontalScrollView optional;
    GridView optionalGV;
    //	LinearLayout noneedll;
    //
    private TextView save, cancle;
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    setView(tcbean, muchGV);
                    setView(noneedTcbean, optionalGV);
                    muchAdapter.updata(tcbean);
                    noNeedAdapter.updata(noneedTcbean);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public CommonSuitPop(Context context, FoodsBean foodsBean, TcT tcT, String num) {
        this.context = context;
        this.foodsBean = foodsBean;
        this.tcT = tcT;
        this.num = num;
    }

    private View parent_d;

    public void showSheet() {
        dlg = new TDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.suit_pop_view, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        parent_d = layout.findViewById(R.id.parent_d);
        dlg.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {
                FileUtils.start(Effectstype.Flipv, parent_d);
            }
        });
        name = (TextView) layout.findViewById(R.id.tc_name);
        name.setText(foodsBean.getName());
        // 必选
        muchSel = (HorizontalScrollView) layout.findViewById(R.id.much_sel_hs);
        muchGV = (GridView) layout.findViewById(R.id.much_sel_gv);
        // 确保是单行显示

        new Thread() {
            public void run() {
                tcbean = DB.getInstance().getTc(foodsBean.getCode(), true);
                // 查套餐下面的可选菜品
                noneedTcbean = DB.getInstance().getTc(foodsBean.getCode(),
                        false);
                handler.sendEmptyMessage(0);
            }

            ;
        }.start();

        muchAdapter = new TcAdapter(context, tcbean, true);
        muchGV.setAdapter(muchAdapter);
        // 可选
        optional = (HorizontalScrollView) layout
                .findViewById(R.id.optional_sel_hs);
        optionalGV = (GridView) layout.findViewById(R.id.optional_sel_gv);
        // 确保是单行显示

        noNeedAdapter = new TcAdapter(context, noneedTcbean, false);
        optionalGV.setAdapter(noNeedAdapter);
        //
        save = (TextView) layout.findViewById(R.id.tc_pop_sure);
        cancle = (TextView) layout.findViewById(R.id.tc_pop_cancle);

        //
        optionalGV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                noNeedAdapter.selectPosition(position);
            }
        });
        //
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                close();
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TcBean> selbean = new ArrayList<TcBean>();
                // 必选的
                if (muchAdapter.getselTc() != null
                        && muchAdapter.getselTc().size() != 0) {
                    selbean.addAll(muchAdapter.getselTc());
                }
                // 可选的
                if (noNeedAdapter.getselTc() != null
                        && noNeedAdapter.getselTc().size() != 0) {
                    selbean.addAll(noNeedAdapter.getselTc());
                }
                // 生成套餐详细
                Double totalPrice = 0.0;
                String details = "";
                String details1 = "";
                for (int i = 0; i < selbean.size(); i++) {
                    totalPrice = totalPrice + selbean.get(i).getPrice();
                    if (i == selbean.size() - 1) {
                        details += selbean.get(i).getCode();
                        details1 += selbean.get(i).getName();
                    } else {
                        details += selbean.get(i).getCode() + ",";
                        details1 += selbean.get(i).getName() + ",";
                    }
                }
                //修改总价为套餐明细价格
                if (foodsBean.getChargeMode().equals("A")) {
                    foodsBean.setPrice(totalPrice);
                }
                tcT.insert(foodsBean, details, details1, "1");
                close();
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setView(List<TcBean> tcbean, GridView layout) {
        LinearLayout.LayoutParams lp_line = new LinearLayout.LayoutParams(dip2px(150
                * tcbean.size() + (tcbean.size() * 20)),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(lp_line);
        layout.setNumColumns(tcbean.size());
        layout.setHorizontalSpacing(20);
    }

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }
}
