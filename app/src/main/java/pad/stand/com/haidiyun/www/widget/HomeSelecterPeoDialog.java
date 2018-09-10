package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.PeopleSelAdapter;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.inter.SelectPerson;
import pad.stand.com.haidiyun.www.inter.SelectTable;

/**
 * @author 选择人数的的窗口�?
 */
public class HomeSelecterPeoDialog {

    private Window dialogWindow;
    private WindowManager.LayoutParams lp;

    private GridView peopleNumSelGv;
    ImageView close;
    private PeopleSelAdapter peopleSelAdapter;
    private int numberPeople;
    private IDialog dlg;
    boolean isAddPeople;
    private SelectTable selectTable;
    private TextView table_people;
    private SharedUtils sharedUtils;

    public HomeSelecterPeoDialog(SelectTable selectTable, TextView table_people, SharedUtils sharedUtils) {
        this.selectTable = selectTable;
        this.table_people = table_people;
        this.sharedUtils = sharedUtils;
    }

    //
    public void intiDialog(final Context context, final SelectPerson selectPerson) {

        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.h_dl_sel_number, null);
        List<String> data = new ArrayList<String>();
        for (int i = 1; i < 25; i++) {
            data.add(i + "人");
        }
        data.add("+");
        //
        close = (ImageView) layout.findViewById(R.id.h_dl_sel_people_close);
        peopleNumSelGv = (GridView) layout
                .findViewById(R.id.h_dl_set_pleople_count_gv);
        peopleSelAdapter = new PeopleSelAdapter(context, data);
        peopleNumSelGv.setAdapter(peopleSelAdapter);
        // 直接点�?
        peopleNumSelGv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {
                numberPeople = position + 1;
                //保存人数
                if (position== 24){
                    CommonPersonEditPop userPop = new CommonPersonEditPop(context,table_people);
                    userPop.showSheet();
                    close();
                    if (selectPerson!=null){
                        selectPerson.select();
                    }
                }else {
                    P.c("保存人数" + numberPeople);
                    sharedUtils.setIntValue("person", numberPeople);
                    table_people.setText(String.valueOf(sharedUtils.getIntValue("person")));
                    close();
                    if (selectPerson!=null){
                        selectPerson.select();
                    }
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
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

    // 设置弹出窗口的px大小
    public void setDialogBig(int width, int higth) {
        lp.width = width; // �?
        lp.height = higth; // �?
        dialogWindow.setAttributes(lp);
    }

    public void setTextViewAddPeople(boolean isadd) {
        this.isAddPeople = isadd;
    }

    public void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg.dismiss();
            dlg = null;
        }
    }

}
