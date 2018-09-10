package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.TablesAdapter;
import pad.stand.com.haidiyun.www.adapter.TablesListAdapter;
import pad.stand.com.haidiyun.www.bean.TableBean;
import pad.stand.com.haidiyun.www.bean.TablesBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.SelectTable;
import pad.stand.com.haidiyun.www.service.FloatService;


public class CommonTablesPop {
    private Context context;
    /**
     * 删除弹出框
     */
    private SharedUtils sharedUtils;

    public CommonTablesPop(Context context, SelectTable selectTable, String optName) {
        this.context = context;
        this.selectTable = selectTable;
        this.optName = optName;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    private String optName;
    private SelectTable selectTable;
    private IDialog dlg;
    private GridView tablesView;
    private ListView tables_menu, tables_sites;
    private TablesAdapter tablesAreaMenuAdapter, tablesSiteMenuAdapter;
    private TextView cancle;
    private TablesListAdapter tablesListAdapter;
    private ArrayList<TableBean> tableBeans;
    private ArrayList<TablesBean> tablesAreas = new ArrayList<TablesBean>();
    private ArrayList<TablesBean> tablesSites = new ArrayList<TablesBean>();
    private SelectTable close = new SelectTable() {
        @Override
        public void select(TableBean bean, String optName, int person) {
            checkState(bean, optName);
        }

        @Override
        public void isLocked() {
            NewDataToast.makeText("此桌台已被锁定");
        }

    };
    private CommonLoadSendPop loadSendPop;
    private String typePre;
    private String typeBack;

    private Handler hnHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                    }
                    //清菜品栏
//                    typeBack = sharedUtils.getStringValue("tableType");
//                    if (!typePre.equals(typeBack)) {
//                        DB.getInstance().clear(new String[]{"dish_table"});
//                    }
                    close();

                    break;
                case -5:
                    if (loadSendPop != null) {
                        loadSendPop.cancle();
                    }
                    NewDataToast.makeText("获取响应超时");
                    break;

                default:
                    break;
            }
        }

        ;
    };

    private void checkState(final TableBean bean, final String optName) {
        loadSendPop = new CommonLoadSendPop(context, "检查桌台状态");
        loadSendPop.showSheet(false);
        Intent intent = new Intent(context, FloatService.class);
        intent.setAction(Common.SERVICE_ACTION);
        intent.putExtra("cancel_table", "");
        context.startService(intent);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", bean.getCode());
            String ip = sharedUtils.getStringValue("IP");
            OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_REFREF_TABLE_STATUS))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build().execute(new StringCallback() {
                @Override
                public void onResponse(final String response, int id) {
                    new Thread() {
                        public void run() {
                            try {
//                                006   {"d":"{\"Success\":true,\"Data\":[{\"Code\":\"003\",\"Name\":\"A03\",\"State\":\"F\",\"BillNo\":\"\",\"GstCount\":6,\"UserCode\":\"\",\"Id\":3}],\"Result\":\"\"}"}
//                                001    {"d":"{\"Success\":true,\"Data\":[{\"Code\":\"001\",\"Name\":\"A01\",\"State\":\"O\",\"BillNo\":\"17101400026\",\"GstCount\":3,\"UserCode\":\
// "Admin\",\"Id\":1},{\"Code\":\"001\",\"Name\":\"A01-东\",\"State\":\"O\",\"BillNo\":\"17101400029\",\"GstCount\":6,\"UserCode\":\"Admin\",\"Id\":1}],\"Result\":\"\"}"}
                                //System.out.println(FileUtils.formatJson(response));
                                JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                                String name = optName;
                                if (jsonObject.getBoolean("Success")) {
                                    //换台成功，这时候需要将旧台挂起
                                    if(sharedUtils.getBooleanValue("is_gua")){
                                        if(sharedUtils.getStringValue("table_code").length()!=0){
                                            DB.getInstance().copyTotemp(sharedUtils.getStringValue("table_code"));
                                        }
                                    }


                                    //成功
                                    JSONArray array = jsonObject.getJSONArray("Data");
                                    int len = array.length();
//                                    if (len != 0 && len == 1) {
                                    //待确认
                                    JSONObject obj = array.getJSONObject(0);
                                    String state = obj.getString("State");
                                    if (state.equals("F")) {
                                        //空台
                                        sharedUtils.clear("billId");
                                        sharedUtils.clear("person");
                                    } else if (state.equals("O")) {
                                        //就餐中
                                        sharedUtils.setIntValue("person", obj.getInt("GstCount"));
                                        //这里有变化需要改变
                                        sharedUtils.setStringValue("billId", obj.getString("BillNo"));
                                        name = obj.getString("UserCode");
                                    }
                                    selectTable.select(bean, name, sharedUtils.getIntValue("person"));
                                    hnHandler.sendEmptyMessage(0);
//                                    }
                                } else {
                                    hnHandler.sendEmptyMessage(0);
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
                    handler.sendEmptyMessage(-5);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private volatile int selectMenu = 0;

    private void select(final int index) {
        this.selectMenu = index;
        new Thread() {
            public void run() {
                //reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index));
                handler.sendEmptyMessage(1);
            }

            ;
        }.start();
    }

    private volatile int selectSite = 0;

    private void selectSite(final int index) {
        this.selectSite = index;
        new Thread() {
            public void run() {
                //reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index));
                handler.sendEmptyMessage(0);
            }

            ;
        }.start();
    }

    private TextView set_t;

    public void showSheet() {
        typePre = sharedUtils.getStringValue("tableType");//001
        sharedUtils.setStringValue("typePre", typePre);
        dlg = new IDialog(context, R.style.config_pop_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.setting_tables, null);
        final int cFullFillWidth = 500;
        layout.setMinimumWidth(cFullFillWidth);
        set_t = (TextView) layout.findViewById(R.id.set_t);
        cancle = (TextView) layout.findViewById(R.id.cancle);
        tablesView = (GridView) layout.findViewById(R.id.tables);
        tables_menu = (ListView) layout.findViewById(R.id.tables_menu);
        tablesAreaMenuAdapter = new TablesAdapter(context, tablesAreas);

        tables_sites = (ListView) layout.findViewById(R.id.tables_sites);
        tablesSiteMenuAdapter = new TablesAdapter(context, tablesSites);

        tables_sites.setAdapter(tablesSiteMenuAdapter);
        tables_menu.setAdapter(tablesAreaMenuAdapter);
        tables_menu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //选择
                select(arg2);
            }
        });
        set_t.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handler.sendEmptyMessage(3);
            }
        });
        tables_sites.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                selectSite(arg2);
                Common.SITE_CODE = tablesSites.get(arg2).getCode();
                //sharedUtils.setStringValue("suitName",tablesSites.get(arg2).getName());
            }
        });
        tableBeans = new ArrayList<TableBean>();
        tablesListAdapter = new TablesListAdapter(context, tableBeans, close, optName);
        tablesView.setAdapter(tablesListAdapter);

        new Thread() {
            public void run() {
                selectSite = 0;
                selectMenu = 0;
                //获取桌台菜单,区域
                tablesSites.clear();
                tablesAreas.clear();
                DB.getInstance().getTablesSites(tablesSites);
                if (tablesSites.size() != 0) {
                    handler.sendEmptyMessage(0);
                }

            }

            ;
        }.start();

        cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                close();
            }
        });
        /*	 */
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(layout);
        dlg.show();
    }

    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 4:
                    NewDataToast.makeText("桌台数据不一致");
                    break;
                case 0:

                    Common.SITE_CODE = tablesSites.get(selectSite).getCode();
                    tablesSiteMenuAdapter.selectPosition(selectSite);
                    if (tablesSites.size() != 0) {
                        sharedUtils.setStringValue("suitName", tablesSites.get(selectSite).getName());
                        new Thread() {
                            public void run() {
                                DB.getInstance().getTablesAreas(tablesAreas, tablesSites.get(selectSite).getCode());
                                handler.sendEmptyMessage(1);
                            }

                            ;
                        }.start();

                    }
                    break;
                case 1:
                    tablesAreaMenuAdapter.selectPosition(selectMenu);
                    if (tablesAreas.size() != 0) {
                        new Thread() {
                            public void run() {
                                String area = tablesAreas.get(selectMenu).getCode();
                                tableBeans = DB.getInstance().getTableCodeBeans(area);
                                int len = tableBeans.size();
                                StringBuilder s = new StringBuilder();
                                for (int i = 0; i < len; i++) {
                                    String code = tableBeans.get(i).getCode();
                                    if (i != len - 1) {
                                        s.append(code + ",");
                                    } else {
                                        s.append(code);
                                    }
                                }
                                if(tempsTableBeans!=null){
                                    tempsTableBeans.clear();
                                    tempsTableBeans = null;
                                }
                                tempsTableBeans = new ArrayList<>();
                                tempsTableBeans = (ArrayList<TableBean>) tableBeans.clone();
                                loadAddTable(s.toString());
                            }
                        }.start();
                    }

                    break;
                case 3:
                    getNetState();
                case 2:
                    if (tablesListAdapter != null) {
                        tablesListAdapter.updata(tableBeans);
                        //获取当前餐台状态
                    }
                    break;

                case -4:
                    close();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private void getNetState() {
        JSONObject jsonObject = new JSONObject();
        try {
            StringBuilder sb = new StringBuilder();
            int len = tableBeans.size();
            for (int i = 0; i < len; i++) {
                TableBean tb = tableBeans.get(i);
                if (i != len - 1) {
                    sb.append(tb.getCode() + ",");
                } else {
                    sb.append(tb.getCode());
                }

            }
            jsonObject.put("data", sb.toString());
            String ip = sharedUtils.getStringValue("IP");
            OkHttpUtils.postString()
                    .url(U.VISTER(ip, U.URL_REFREF_TABLE_STATUS))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(jsonObject.toString())
                    .build().execute(tableCallback);
            P.c(TimeUtil.getTime(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private StringCallback tableCallback = new StringCallback() {

        @Override
        public void onResponse(final String response, int id) {

            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                FileUtils.formatJson(response), "桌台状态结果");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "桌台状态结果");
                    }
                }

                ;
            }.start();
            new Thread() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if (jsonObject.getBoolean("Success")) {
                            //成功
                            JSONArray array = jsonObject.getJSONArray("Data");
                            int len = array.length();
                            int jen = tableBeans.size();
                            if (len == jen) {
                                for (int i = 0; i < len; i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    for (int j = 0; j < jen; j++) {

                                        if (obj.getString("Code").equals(tableBeans.get(j).getCode())) {

                                            tableBeans.get(j).setState(obj.getString("State"));
                                        }
                                    }

                                }
                                handler.sendEmptyMessage(2);
                            } else {
                                //handler.sendEmptyMessage(4);
                            }
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
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                "获取桌台状态结果失败", "桌台状态结果");
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                ;
            }.start();
        }
    };

    public void close() {
        if (dlg != null && dlg.isShowing()) {
            dlg.cancel();
            dlg.dismiss();
            dlg = null;
        }
    }
    ArrayList<TableBean> tempsTableBeans ;
    private void loadAddTable(String s) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString().url("http://" + sharedUtils.getStringValue("IP") + "/DataService.svc/GetRestTable")
                .mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            //            {"d":"{\"Success\":true,\"Data\":[{\"Code\":\"109\",\"Name\":\"109\",\"State\":\"F\",\"BillNo\":\"\",\"" +
//                    "GstCount\":2,\"UserCode\":\"\",\"Id\":9}],\"Result\":\"\"}"}
            @Override
            public void onResponse(final String response, int id) {
                try {

                    JSONObject jsonObject = new JSONObject(
                            FileUtils.formatJson(response));
                    if (jsonObject.getBoolean("Success")) {
                        // 成功
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        int len = jsonArray.length();
                        if (len != 0) {
                            tableBeans.clear();
                        }
                        for (int i = 0; i < len; i++) {
                            // 已下单菜品
                            JSONObject object = jsonArray.getJSONObject(i);
                            String code = object.getString("Code");
                            TableBean beanSql = DB.getInstance().getTableCodeInfo(code);
                            TableBean tableBean = new TableBean();
                            tableBean.setCode(code);
                            tableBean.setName(object.getString("Name"));
                            tableBean.setMax(0);
                            tableBean.setLocked(false);
                            tableBean.setTypeCode(beanSql.getTypeCode());
                            tableBean.setTypeName(beanSql.getTypeName());
                            tableBean.setPriceType(beanSql.getPriceType());
                            try {
                                tableBean.setTemp(tempsTableBeans.get(i).isTemp());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            tableBeans.add(tableBean);

                        }
                        handler.sendEmptyMessage(3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
