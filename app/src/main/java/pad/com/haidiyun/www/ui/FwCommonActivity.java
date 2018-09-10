package pad.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.MoveAdapter;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.MoveItem;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.service.CountService;
import pad.com.haidiyun.www.widget.Common433SendPop;
import pad.com.haidiyun.www.widget.NewDataToast;

/**
 * Created by Administrator on 2017/9/6/006.
 */
@SuppressLint("ValidFragment")
public class FwCommonActivity extends Fragment {
    private Activity activity;
    private Handler parentHandler;
    private static final String TAG = "FwCommonActivity";

    public FwCommonActivity(Activity activity, Handler parentHandler) {
        this.parentHandler = parentHandler;
        this.activity = activity;
    }

    private ArrayList<MoveItem> moveItems;
    private Common433SendPop sendPop;
    GridView views;
    LinearLayout content;
    SharedUtils sharedUtils;
    TextView tv_call;
    private Intent mIntent;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedUtils = new SharedUtils(Common.CONFIG);
        moveItems = new ArrayList<MoveItem>();
        BooleanInfo();
        mIntent = new Intent(getActivity(), CountService.class);
        views = (GridView) view.findViewById(R.id.views);
        tv_call = (TextView) view.findViewById(R.id.tv_call);
        content = (LinearLayout) view.findViewById(R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                MoveAdapter moveAdapter = new MoveAdapter(activity, moveItems, (content.getWidth() - 4) / 3);
                views.setAdapter(moveAdapter);
                views.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (BaseApplication.clickMap.get(position)) {
                            BaseApplication.clickMap.put(position, false);
                            // 启动倒计时的服务
                            mIntent.putExtra("key", position);
                            getActivity().startService(mIntent);
                            sendPop = new Common433SendPop(activity, moveItems.get(position).getTxt().toString(), null);
                            sendPop.showSheet();
                        } else {
                            NewDataToast.makeText("已通知");
                            Log.i(TAG, "倒计时未完成");
                        }
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

    private void BooleanInfo() {
        String info = sharedUtils.getStringValue("info");
        if (info != null && !"".equals(info)) {
            try {
                JSONObject object = new JSONObject(info);
                if (object.getBoolean("isok")) {
                    JSONArray arrayObj = object.getJSONArray("obj");
                    int len = arrayObj.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = arrayObj.getJSONObject(i);
                        MoveItem item = new MoveItem();
                        item.setTxt(obj.getString("type_name"));
                        item.setColor(obj.getString("color"));
                        moveItems.add(item);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flip_float_service_foc, container, false);
        return view;

    }

    // 广播接收者
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case CountService.IN_RUNNING:
//                    tv_call.setText("倒计时中(" + intent.getStringExtra("time") + ")");
                    Log.i(TAG, "发送服务剩余时间:" + intent.getStringExtra("time"));
                    break;
                case CountService.END_RUNNING:
                    //退出界面,马上进来计时器记住,计时完成再进来触发不了这操作
                    break;
            }
        }
    };

    // 注册广播
    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CountService.IN_RUNNING);
        intentFilter.addAction(CountService.END_RUNNING);
        return intentFilter;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mUpdateReceiver, updateIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mUpdateReceiver);
    }
}
