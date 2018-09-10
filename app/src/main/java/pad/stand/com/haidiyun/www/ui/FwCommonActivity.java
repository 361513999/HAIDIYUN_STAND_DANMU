package pad.stand.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.MoveAdapter;
import pad.stand.com.haidiyun.www.bean.MoveItem;
import pad.stand.com.haidiyun.www.widget.Common433SendPop;


/**
 * Created by Administrator on 2017/9/6/006.
 */
@SuppressLint("ValidFragment")
public class FwCommonActivity extends Fragment {
    private Activity activity;
    private Handler parentHandler;
    public FwCommonActivity(Activity activity,Handler parentHandler){
        this.activity = activity;
        this.parentHandler = parentHandler;
    }
    private ArrayList<MoveItem> moveItems = new ArrayList<MoveItem>();
    {
        add("加水","#EB6483");
        add("打包","#25DB61");
        add("加汤","#DAD966");
        add("催菜","#5AABF6");
        add("加餐具","#F42CE8");
        add("呼叫","#8D44F0");
        add("清洁","#ECBE64");
        add("加位","#30E5D6");
        add("结账","#3809F7");

    }
    private void add(String txt,String color){
        MoveItem i = new MoveItem();
        i.setTxt(txt);
        i.setColor(color);
        moveItems.add(i);

    }
    private Common433SendPop sendPop;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final GridView views = (GridView) view.findViewById(R.id.views);
        final LinearLayout content = (LinearLayout) view.findViewById(R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                MoveAdapter moveAdapter = new MoveAdapter(activity,moveItems,(content.getWidth()-4)/3);
                views.setAdapter(moveAdapter);
                views.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        sendPop = new Common433SendPop(activity,moveItems.get(position).getTxt().toString(),null);
                        sendPop.showSheet();

                    }
                });
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentHandler.sendEmptyMessage(2);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.float_service_foc, container, false);
        return view;

    }
}
