package pad.stand.com.haidiyun.www.ui;


import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.Remove;
import pad.stand.com.haidiyun.www.utils.DesUtils;
import pad.stand.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.stand.com.haidiyun.www.widget.NewDataToast;

public class CardValActivity extends CaseActivity {
    private ImageView anim_complete;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private FrameLayout layout0;
    private LinearLayout layout1;
    private ToggleButton tbButton;
    private EditText user, pass;
    private TextView cancle, save;
    private SharedUtils sharedUtils;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {

            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {

            return true;
        }
        return super.dispatchKeyEvent(event);

    }


    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent();
                    intent.putExtra("result", msg.arg1);
                    P.c("校验返回");
                    Intent dataIntent = getIntent();
                    if (dataIntent.hasExtra("obj")) {
                        P.c("附加对象的校验返回");
                        intent.putExtra("obj", getIntent().getSerializableExtra("obj"));
                    }
                    if (dataIntent.hasExtra("num")) {
                        intent.putExtra("num", getIntent().getStringExtra("num"));
                    }
                    if (dataIntent.hasExtra("tag")) {
                        intent.putExtra("tag", getIntent().getStringExtra("tag"));
                    }
                    P.c("服务员" + (String) msg.obj);
                    intent.putExtra("optName", (String) msg.obj);
//                    Intent endIntent = new Intent(Common.createExplicitFromImplicitIntent(CardValActivity.this, intent));
                    setResult(1000, intent);
                    AppManager.getAppManager().finishActivity(CardValActivity.this);

                    if (dataIntent.hasExtra("print")) {
                        loadSendPop = new CommonLoadSendPop(CardValActivity.this, "打印结账单");
                        loadSendPop.showSheet(false);
//                    //开启通知结账服务
                        printBill(sharedUtils.getStringValue("IP"));
                    }
                    break;
                case 0:
                    if (msg.arg1 == 1) {
                        //NewDataToast.makeTextD( "服务员:"+msg.obj+",你好!\n"+"可以在此点餐机上使用",1000);
                    } else if (msg.arg1 == 0) {
                        NewDataToast.makeText("不能在此点餐机上使用");
                    }
                    AppManager.getAppManager().finishActivity(CardValActivity.this);
                    break;
                case 4:
                    NewDataToast.makeText((String) msg.obj);
                    break;
                case 2:
                    NewDataToast.makeText("校验失败");
                    break;
                case 3:
                    String IP = sharedUtils.getStringValue("IP");
                    String[] ms = (String[]) msg.obj;

                    login(IP, ms[0], ms[1]);
                    break;
                case 5:
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                    }
                    NewDataToast.makeTextL((String) msg.obj, 2000);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void change(boolean flag) {
        if (flag) {
            layout1.setVisibility(View.VISIBLE);
            layout0.setVisibility(View.GONE);
        } else {
            layout0.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);

        }
    }

    private String MD5(String pw) {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = pw.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_val_activity);
        sharedUtils = new SharedUtils(Common.CONFIG);
        layout0 = (FrameLayout) findViewById(R.id.layout0);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        tbButton = (ToggleButton) findViewById(R.id.tb);
        cancle = (TextView) findViewById(R.id.cancle);
        if (getIntent().hasExtra("login")) {
            cancle.setText("注销");
        }
        save = (TextView) findViewById(R.id.save);
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String u = user.getText().toString();
                String p = pass.getText().toString();
                if (u.length() != 0) {
                    boolean flag = DB.getInstance().isVal(u, MD5(p));
                    if (flag) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = flag ? 1 : 0;
                        msg.obj = u;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 3;
                        msg.obj = new String[]{u, MD5(p)};
                        handler.sendMessage(msg);
                    }
                } else {
                    NewDataToast.makeText("请先输出工号");
                }
            }
        });
        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {

                    AppManager.getAppManager().finishActivity(CardValActivity.class);
                    if (getIntent().hasExtra("login")) {
                        sharedUtils.clear("user");
                        sharedUtils.clear("userName");
                        sharedUtils.clear("optName");
                        NewDataToast.makeText("注销成功");
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                try {

                    AppManager.getAppManager().finishActivity(PriceActivity.class);
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });
        change(tbButton.isChecked());
        tbButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                change(arg1);
            }
        });
        anim_complete = (ImageView) findViewById(R.id.write_tip);
        TranslateAnimation alphaAnimation2 = new TranslateAnimation(0, 0, -550f, 0);
        alphaAnimation2.setDuration(2000);
        alphaAnimation2.setRepeatCount(Animation.INFINITE);
        alphaAnimation2.setRepeatMode(Animation.REVERSE);
        anim_complete.setAnimation(alphaAnimation2);
        alphaAnimation2.start();
        init();
        read(getIntent(), 0);
    }

    private void init() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注释nfc
        openNFC();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            read(intent, 1);
        }
    }

    private String result = null;
    private String sn;

    private void read(final Intent intent, final int index) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage ndefMessage = null;
            if (rawMsgs != null) {
                if (rawMsgs.length > 0) {
                    ndefMessage = (NdefMessage) rawMsgs[0];
                }
                NdefRecord record = ndefMessage.getRecords()[0];
                result = new String(record.getPayload());
            }
        }
        if (result != null && result.length() != 0) {
            new Thread() {
                @Override
                public void run() {

                    super.run();
                    try {
                        DesUtils des = new DesUtils("haidiyun");//自定义密钥
//						P.c(result);
//						P.c(des.decrypt(result));
                        String json = des.decrypt(result);
                        JSONObject jsonObject = new JSONObject(json);
                        String code = jsonObject.getString("code");
                        String pwd = jsonObject.getString("pwd");
                        boolean flag = DB.getInstance().isVal(code, pwd);
                        if (flag) {
                            Message msg = new Message();
                            msg.what = index;
                            msg.arg1 = flag ? 1 : 0;
                            msg.obj = code;
                            handler.sendMessage(msg);
                        } else {
                            //网络校验
                            Message msg = new Message();
                            msg.what = 3;
                            msg.obj = new String[]{code, pwd};
                            handler.sendMessage(msg);
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    private RequestCall loginCall;
    private CommonLoadSendPop loadSendPop;

    private void login(String ip, final String code, String pwd) {
        if (loginCall != null) {
            loginCall.cancel();
            loginCall = null;
        }
        if (loadSendPop == null) {
            loadSendPop = new CommonLoadSendPop(CardValActivity.this, "校验用户");
        }
        loadSendPop.setCloseLis(new Remove() {

            @Override
            public void remove() {
                if (loginCall != null) {
                    loginCall.cancel();
                    loginCall = null;
                }
            }
        });
        loadSendPop.showSheet(false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "{\"account\":\"" + code + "\",\"password\":\"" + pwd + "\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginCall = OkHttpUtils
                .postString()
                .url(U.VISTER(ip, U.URL_USER_VAL))
                // .addParams("data",
                // "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        loginCall.execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                handler.sendEmptyMessage(2);
            }

            @Override
            public void onResponse(final String response, int id) {

//				Log.v("测试", response);
                if (loadSendPop != null) {
                    loadSendPop.cancle();
                    loadSendPop = null;
                }
                new Thread() {
                    public void run() {
                        //添加到数据库
                        if (loginCall != null) {
                            loginCall.cancel();
                            loginCall = null;
                        }

                        //  {"d":"{\"Success\":false,\"Data\":\"Input string was not in a correct format.\",\"Result\":\"\"}"}
                        try {
                            JSONObject object = new JSONObject(FileUtils.formatJson(response));

                            if (object.getBoolean("Success")) {
                                //成功
                                DB.getInstance().addUser(object.getJSONObject("Data"));
                                Message msg = new Message();
                                msg.what = 1;
                                msg.arg1 = 1;
                                msg.obj = code;
                                handler.sendMessage(msg);
                            } else {
                                Message msg = new Message();
                                msg.what = 4;
                                msg.obj = object.getString("Data");
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


//						DB.getInstance().addUser(array);

                    }

                    ;
                }.start();

            }

        });
    }

    /**
     * 打开NFC设备
     */
    private void openNFC() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    Common.FILTERS, Common.TECHLISTS);
        }
    }


    private RequestCall billCall;

    private void printBill(String ip) {
        String billString = sharedUtils.getStringValue("billId");
        String tableString = sharedUtils.getStringValue("table_code");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",
                    "{\"billNo\":\"" + billString + "\",\"TableNo\":\"" + tableString + "\"}");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        billCall = OkHttpUtils.postString()
                .url(U.VISTER(ip, U.URL_PRINT_BILL))
                // .addParams("data",
                // "[{\"TabName\":\"NTORestArea\",\"Timestamp\":0},{\"TabName\":\"NTORestTable\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuClass\",\"Timestamp\":0},{\"TabName\":\"NTORestMenu\",\"Timestamp\":0},{\"TabName\":\"NTOUsers\",\"Timestamp\":0},{\"TabName\":\"NTORestCook\",\"Timestamp\":0},{\"TabName\":\"NTORestCookCls\",\"Timestamp\":0},{\"TabName\":\"NTORestRemark\",\"Timestamp\":0},{\"TabName\":\"NTORestMenuImage\",\"Timestamp\":0}]")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonObject.toString()).build();
        billCall.execute(billCallback);
    }

    private StringCallback billCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

//            destory.setEnabled(false);
            CountDownTimer timer = new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long arg0) {


                }

                @Override
                public void onFinish() {

//                    destory.setEnabled(true);
                }
            };
            timer.start();
            new Thread() {
                public void run() {


                    //  {"d":"{\"Success\":true,\"Data\":\"株洲成功！\",\"Result\":\"\"}"}

                    try {
                        JSONObject object = new JSONObject(FileUtils.formatJson(response));
                        //
                        if (object.getBoolean("Success")) {
                            Message msg = new Message();
                            msg.what = 5;
//                            msg.obj = object.getString("Data");
                            msg.obj = "通知成功";
                            handler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 5;
//                            msg.obj = object.getString("Data");
                            msg.obj = "通知失败";
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                }

                ;
            }.start();


        }

        @Override
        public void onError(Call call, Exception e, int id) {

            Message msg = new Message();
            msg.what = 5;
            msg.obj = "响应错误";
            handler.sendMessage(msg);
        }

    };
}
