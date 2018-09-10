package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.LoadBuy;
import pad.stand.com.haidiyun.www.inter.Remove;
import pad.stand.com.haidiyun.www.widget.dialog.Animations.Effectstype;


public class CommonDeletePop {
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    remove.remove();
                    dlg.cancel();
                    dlg = null;
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
    private TextView tip, txt, cancle, sure;
    private Remove remove;
    private DishTableBean oj;
    private TDialog dlg;
    private View parent_d;
    private LoadBuy sureBuy;

    public CommonDeletePop(Context context, DishTableBean oj, Remove remove, LoadBuy sureBuy) {
        this.context = context;
        this.oj = oj;
        this.remove = remove;
        this.sureBuy = sureBuy;
    }

    public Dialog showSheet() {
        dlg = new TDialog(context, R.style.config_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.common_tip, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        parent_d = layout.findViewById(R.id.parent_d);
        tip = (TextView) layout.findViewById(R.id.tip);
        txt = (TextView) layout.findViewById(R.id.txt);
        cancle = (TextView) layout.findViewById(R.id.cancle);
        sure = (TextView) layout.findViewById(R.id.sure);
        tip.setText(context.getResources().getString(R.string.tip));
        try {
            String str = context.getResources().getString(R.string.remove) + "【" + URLDecoder.decode(oj.getName(), "UTF-8") + "】";
            SpannableStringBuilder sbBuilder = new SpannableStringBuilder(str);

            sbBuilder.setSpan
                    (new ForegroundColorSpan(Color.RED), 3, str.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            txt.setText(sbBuilder);
        } catch (UnsupportedEncodingException e1) {

            e1.printStackTrace();
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
            public void onClick(View arg0) {
                DB.getInstance().delete(oj.getI());
                handler.sendEmptyMessage(1);
                //跟新菜品
//                sureBuy.success(null);
            }
        });
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

}
