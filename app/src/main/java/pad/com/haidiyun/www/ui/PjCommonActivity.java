package pad.com.haidiyun.www.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.FlipBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.widget.NewDataToast;

/**
 * Created by Administrator on 2017/9/6/006.
 */
@SuppressLint("ValidFragment")
public class PjCommonActivity extends Fragment {
    private Activity activity;
    private Handler parentHandler;
    private ImageView icon;
    private volatile ArrayList<FlipBean> flipBeans;
    private RatingBar item0, item1, item2;
    private TextView enter;
    private SharedUtils sharedUtils;

    public PjCommonActivity(Activity activity, Handler parentHandler) {
        this.activity = activity;
        this.parentHandler = parentHandler;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        icon = (ImageView) view.findViewById(R.id.icon);
        item0 = (RatingBar) view.findViewById(R.id.item0);
        item1 = (RatingBar) view.findViewById(R.id.item1);
        item2 = (RatingBar) view.findViewById(R.id.item2);
        enter = (TextView) view.findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        flipBeans = DB.getInstance().getDishsToFlip();
        if (flipBeans != null && flipBeans.size() != 0) {
            Glide.with(BaseApplication.application).load("file://" + Common.SOURCE + flipBeans.get(0).getPath()).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(icon);

        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentHandler.sendEmptyMessage(2);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flip_pj_layout, container, false);
        return view;

    }

    private void send() {
        String billId = sharedUtils.getStringValue("billId");
        if (billId.length() == 0) {
            NewDataToast.makeText("不存在订单");
            return;
        }
        JSONObject object = new JSONObject();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BillNo", billId);
            jsonObject.put("OverView", item0.getRating());
            jsonObject.put("Taste", item1.getRating());
            jsonObject.put("Enviroment", item2.getRating());
            jsonObject.put("Detail", "");
            jsonObject.put("Contact", "");
            jsonObject.put("Phone", "");
            object.put("data", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        P.c(jsonObject.toString());
        String ip = sharedUtils.getStringValue("IP");
        OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_POST_PJ))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(object.toString())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                P.c(e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject1 = new JSONObject(FileUtils.formatJson(response));
                    if (jsonObject1.getBoolean("Success")) {
                        NewDataToast.makeText("评价完成");
                    } else {
                        NewDataToast.makeText(jsonObject1.getString("Data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
