package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.TzAdapter;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.bean.TBean;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.NumSel;
import pad.stand.com.haidiyun.www.inter.TzC;
import pad.stand.com.haidiyun.www.widget.dialog.Animations.Effectstype;


public class CommonTzPop {
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    view.setText("" + msg.obj);
                    break;
                case 1:
                    dlg.cancel();
                    dlg = null;
                    break;
                case 0:
                    adapter.updata(tBeen);
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Context context;
    /**
     * 删除弹出框
     */
    private TextView set_t, cancle, sure;
    private TzAdapter adapter;
    private GridView tables;
    private DishTableBean oj;
    private TDialog dlg;
    private View parent_d;
    private String title;
    private boolean index;
    private TextView delete, view, add;
    private DishTableBean obj;
    private TzC conf;

    public CommonTzPop(Context context, String title, boolean index, DishTableBean obj, TzC conf) {
        this.context = context;
        this.title = title;
        this.index = index;
        this.obj = obj;
        this.conf = conf;

    }

    private ArrayList<TBean> tBeen = new ArrayList<TBean>();

    public Dialog showSheet() {
        dlg = new TDialog(context, R.style.config_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.tz_pop, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        parent_d = layout.findViewById(R.id.parent_d);
        set_t = (TextView) layout.findViewById(R.id.set_t);
        tables = (GridView) layout.findViewById(R.id.tables);
        cancle = (TextView) layout.findViewById(R.id.cancle);
        sure = (TextView) layout.findViewById(R.id.sure);
        delete = (TextView) layout.findViewById(R.id.delete);
        view = (TextView) layout.findViewById(R.id.view);
        add = (TextView) layout.findViewById(R.id.add);

        set_t.setText(title);
        adapter = new TzAdapter(context, tBeen);
        tables.setAdapter(adapter);
        tables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selectPosition(position);
            }
        });
        if (index) {
            //退菜
            tables.setVisibility(View.VISIBLE);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    tBeen.clear();
                    tBeen = DB.getInstance().getTbeans();
                    handler.sendEmptyMessage(0);
                }
            }.start();


        } else {
            //赠菜
//			tables.setVisibility(View.GONE);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    tBeen.clear();
                    tBeen = DB.getInstance().getTbeans();
                    handler.sendEmptyMessage(0);
                }
            }.start();

        }
        dlg.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                FileUtils.start(Effectstype.Flipv, parent_d);
            }
        });
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                dlg.cancel();
            }
        });
        sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double tempDouble = Double.valueOf(view.getText().toString());

                if (tempDouble > obj.getCount()) {
                    NewDataToast.makeText("数量至多为" + obj.getCount());
                    return;
                }
                if (index) {

                    conf.tuicai(obj, view.getText().toString(), adapter.getSelectName());
                } else {

                    conf.zengsong(obj, view.getText().toString(), adapter.getSelectName());


                }
            }
        });
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String num = view.getText().toString();
                try {
                    int max = Integer.parseInt(num);
                    if (max == 1) {
                        //已达最小
                    } else {
                        view.setText(String.valueOf(max - 1));
                    }
                } catch (NumberFormatException e) {

                }
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = view.getText().toString();
                try {
                    int max = Integer.parseInt(num);
                    if (max == obj.getCount()) {
                        //已达最大值
                    } else {
                        view.setText(String.valueOf(max + 1));
                    }
                } catch (NumberFormatException e) {

                }
            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//				buyClick.num(obj);
                CommonNum commonNum = new CommonNum(context, numSel, obj, obj.getOrderMinLimit());
                commonNum.showSheet();
            }
        });
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    public void close() {
        if (dlg != null) {
            dlg.cancel();
            dlg = null;
        }
    }

    private NumSel numSel = new NumSel() {
        @Override
        public void change(String o, Object ob) {
            Message msg = new Message();
            msg.what = 2;
            msg.obj = o;
            handler.sendMessage(msg);
        }

        @Override
        public void changeWeigh(String o, Object object, ArrayList<ReasonBean> resons, String remark, T_Image im) {

        }

    };
}
