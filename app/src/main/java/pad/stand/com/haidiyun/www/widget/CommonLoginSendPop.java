package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.inter.ConnectTimeOut;
import pad.stand.com.haidiyun.www.inter.LoginS;
import pad.stand.com.haidiyun.www.inter.UpdateIp;

/**
 * 发送下单
 *
 * @author Administrator
 */
public class CommonLoginSendPop {
    private Context context;
    private TextView load_tv;
    private IDialog dlg;
    private String msg;
    private ImageView load_img, load_close;
    private SharedUtils utils;
    private LoginS loginS;
    private UpdateIp updateIp;
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    NewDataToast.makeText("检验成功");
                    //若修改的ip与系统取得ip不一致,不再从系统获取新ip
                    if (!ip.equals(utils.getStringValue("IP"))) {
                        utils.setStringValue("notGetIp", "nope");
                    }
                    utils.setStringValue("IP", ip);
                    close();

                    loginS.success();
                    updateIp.change(ip);
                    break;
                case 0:
                    NewDataToast.makeText("请检查账户名密码");
                    close();
                    break;
                case -1:
                    NewDataToast.makeText("请检查与主机的连接配置");
                    close();
                    break;
                case -2:
                    NewDataToast.makeText("登录失败,请检查WIFI环境和主机配置");
                    close();
                    break;
                case -3:
                    NewDataToast.makeText("取消登录");
                    cancle();
                    break;
                case -5:
                    NewDataToast.makeText("获取响应数据超时");
                    close();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private String ip;

    public CommonLoginSendPop(Context context, String msg, String ip, LoginS loginS, UpdateIp updateIp) {
        this.context = context;
        this.ip = ip;
        this.loginS = loginS;
        this.updateIp = updateIp;
        this.msg = msg;
        utils = new SharedUtils(Common.CONFIG);
    }

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.buy_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.loading,
                null);
        load_tv = (TextView) layout.findViewById(R.id.load_tv);
        load_tv.setText(msg);
        load_img = (ImageView) layout.findViewById(R.id.load_img);
        load_close = (ImageView) layout.findViewById(R.id.load_close);
        // String ip = utils.getStringValue("IP");
        // String port = utils.getStringValue("port");
        load();

        load_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (requestCall != null) {
                    requestCall.cancel();
                }
                handler.sendEmptyMessage(-3);
            }
        });
        // 动画
        Animation operatingAnim = AnimationUtils.loadAnimation(context,
                R.anim.anim_load);
        // 匀速旋转
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        load_img.startAnimation(operatingAnim);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    /**
     * 查找更新数据
     */
    private RequestCall requestCall;

    private void load() {


        requestCall = OkHttpUtils.post()
                .url(U.VISTER(ip, U.URL_INDEX))
//		.url("http://192.168.1.147:8001/DataService.svc/DownloadData")

                // .mediaType(MediaType.parse("application/json; charset=utf-8"))
                // .content("{\"Cmd\":\"DL\",\"Lid\":\""+mac+"\",\"Pid\":\""+mac+"\",\"UsrID\" : \""+user+"\",\"Pwd\" : \""+pass+"\"}")
                //.content("{\"Cmd\":\"CXJCSJZXBB\",\"Lid\":\""+mac+"\",\"Pid\":\""+mac+"\",\"CurVer\":\""+versionTime+"\"}")
                .build();
        requestCall.execute(loginCallBack);
    }

    @SuppressWarnings("unused")
    private ConnectTimeOut timeOut = new ConnectTimeOut() {
        @Override
        public void recyle() {

            // 多次连接超时
            handler.sendEmptyMessage(-2);

        }
    };

    private void cancle() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }

    private void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg = null;
        }
    }

    private StringCallback loginCallBack = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

            handler.sendEmptyMessage(-2);
        }

        @Override
        public void onResponse(String response, int id) {
            P.c(response);
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                if (jsonObject.getBoolean("Success")) {
                    handler.sendEmptyMessage(1);
                } else {
                    handler.sendEmptyMessage(0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        /*	if (status.equals("SUCCESS")) {
                handler.sendEmptyMessage(1);
			} else {
				handler.sendEmptyMessage(0);
			}*/

        }

    };
}
