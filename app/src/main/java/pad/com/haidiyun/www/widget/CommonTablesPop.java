package pad.com.haidiyun.www.widget;

import android.content.Context;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.TablesAdapter;
import pad.com.haidiyun.www.adapter.TablesListAdapter;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.bean.TablesBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.common.TimeUtil;
import pad.com.haidiyun.www.common.U;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.SelectTable;


public class CommonTablesPop {
	 private Context context;
	/**
	 * 删除弹出框
	 */
	public CommonTablesPop(Context context, SelectTable selectTable, String optName) {
		this.context = context;
		this.selectTable = selectTable;
		this.optName = optName;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}
	private String optName;
	private SelectTable selectTable;
	private  IDialog dlg ;
	private GridView tablesView;
	private ListView tables_menu,tables_sites;
	private TablesAdapter tablesAreaMenuAdapter,tablesSiteMenuAdapter;
	private TextView cancle;
	private TablesListAdapter tablesListAdapter;
	private List<TableBean> tableBeans;
	private ArrayList<TablesBean> tablesAreas = new ArrayList<TablesBean>();
	private ArrayList<TablesBean> tablesSites = new ArrayList<TablesBean>();
	private SelectTable close = new SelectTable() {
		@Override
		public void select(TableBean bean,String optName) {
			// TODO Auto-generated method stub
			if (bean != null) {
				close();
			}
		}
		@Override
		public void isLocked() {
			// TODO Auto-generated method stub
			NewDataToast.makeText("此桌台已被锁定");
		}
	};
	private volatile int selectMenu = 0;
	private void select(final int index) {
		this.selectMenu = index;
		new Thread(){
			public void run() {
				//reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index));
				handler.sendEmptyMessage(1);
			};
		}.start();
	}
	private volatile int selectSite = 0;
	private void selectSite(final int index) {
		this.selectSite = index;
		new Thread(){
			public void run() {
				//reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index));
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	private SharedUtils sharedUtils;
	public  void showSheet() {
		 dlg = new IDialog(context, R.style.config_pop_style);
		 if
				 (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.flip_setting_tables, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
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
				// TODO Auto-generated method stub
				//选择
				select(arg2);
			}
		});
		tables_sites.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectSite(arg2);
			}
		});
		tableBeans = new ArrayList<TableBean>();
		tablesListAdapter = new TablesListAdapter(context, tableBeans,selectTable,close,optName);
		tablesView.setAdapter(tablesListAdapter);
		
		new Thread(){
			public void run() {
				selectSite = 0;
				selectMenu = 0;
				//获取桌台菜单,区域
				tablesSites.clear();
				tablesAreas.clear();
				DB.getInstance().getTablesSites(tablesSites);
				if(tablesSites.size()!=0){
					handler.sendEmptyMessage(0);
				}
			};
		}.start();
		
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
	private void getNetState(){
		JSONObject jsonObject = new JSONObject();
		try {
			StringBuilder sb = new StringBuilder();
			int len = tableBeans.size();
			for(int i=0;i<len;i++){
				TableBean tb = tableBeans.get(i);
				if(i!=len-1){
					sb.append(tb.getCode()+",");
				}else{
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private StringCallback tableCallback  =new StringCallback() {

		@Override
		public void onResponse(final String response, int id) {
			new Thread(){
				public void run() {
					try {
						JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));

						if(jsonObject.getBoolean("Success")){
							//成功
							JSONArray array = jsonObject.getJSONArray("Data");
							int len = array.length();
							int jen = tableBeans.size();
							if(len==jen){
								for(int i=0;i<len;i++){
									JSONObject obj = array.getJSONObject(i);
									for(int j=0;j<jen;j++){

										if(obj.getString("Code").equals(tableBeans.get(j).getCode())){

											tableBeans.get(j).setState(obj.getString("State"));
										}
									}

								}
								handler.sendEmptyMessage(2);
							}else {
								//handler.sendEmptyMessage(4);
							}
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
		}

		@Override
		public void onError(Call call, Exception e, int id) {

		}
	};
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
			 
				tablesSiteMenuAdapter.selectPosition(selectSite);
				if(tablesSites.size()!=0){
					sharedUtils.setStringValue("suitName",tablesSites.get(selectSite).getName());
					new Thread(){
						public void run() {
							DB.getInstance().getTablesAreas(tablesAreas,tablesSites.get(selectSite).getCode());
							handler.sendEmptyMessage(1);
						};
					}.start();
				
				}
				break;

			case 1:
				tablesAreaMenuAdapter.selectPosition(selectMenu);
				if(tablesAreas.size()!=0){
					new Thread(){
						public void run() {
							tableBeans = DB.getInstance().getTableCodeBeans(tablesAreas.get(selectMenu).getCode());
							handler.sendEmptyMessage(3);
						};
					}.start();
				}
			
				break;
			case 3:
					getNetState();
			case 2:
				if(tablesListAdapter!=null){
					tablesListAdapter.updata(tableBeans);
				}
				break;
			case -4:
				close();
				break;
			default:
				break;
			}
		};
	};
	public void close(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg.dismiss();
			dlg = null;
		}
	}
}
