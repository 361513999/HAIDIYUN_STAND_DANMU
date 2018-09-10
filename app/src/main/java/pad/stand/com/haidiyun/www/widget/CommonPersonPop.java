package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import com.zc.http.okhttp.request.RequestCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.SelectPresonAdapter;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.U;
import pad.stand.com.haidiyun.www.inter.BuyClick;
import pad.stand.com.haidiyun.www.inter.LoadBuy;

public class CommonPersonPop {
	 private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private BuyClick buyClick;
	private SharedUtils sharedUtils;
	private   TextView search_txt;
	private LoadBuy loginBuy;
	private String optName;
	public CommonPersonPop(Context context,BuyClick buyClick,LoadBuy loginBuy,String optName ) {
		this.context = context;
		this.buyClick = buyClick;
		this.loginBuy = loginBuy;
		this.optName = optName;
		sharedUtils = new SharedUtils(Common.CONFIG);
	}
	private long lastClick;
	private String zm[] = new String[]{"1","2","3","4","5","6","7","8","9"};
	public  Dialog showSheet() {
		
	 
		  dlg = new IDialog(context, R.style.menu_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.select_num, null);
		SelectPresonAdapter searchAdapter = new SelectPresonAdapter(context, zm);
		GridView select_num_view= (GridView) layout.findViewById(R.id.select_num_view);
		TextView cancle = (TextView) layout.findViewById(R.id.cancle);
		TextView send = (TextView) layout.findViewById(R.id.send);
		search_txt = (TextView) layout.findViewById(R.id.search_txt);
		TextView zero = (TextView) layout.findViewById(R.id.zero);
		ImageView sear_del = (ImageView) layout.findViewById(R.id.sear_del);
		select_num_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				 try{
					int num =  Integer.parseInt(search_txt.getText().toString()
						+ zm[arg2]);
					if(num<=100){
						 search_txt.setText(String.valueOf(num));
					}
				 }catch(NumberFormatException e){
					 NewDataToast.makeText( "输入错误");
				 }
				
				
			}
		});
		zero.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				 
				 try{
						int num =  Integer.parseInt(search_txt.getText().toString()
							+  "0");
						if(num<=100){
							 search_txt.setText(String.valueOf(num));
						}
					 }catch(NumberFormatException e){
						 NewDataToast.makeText( "输入错误");
					 }
			}
		});
		sear_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				/**
				 * 删除数据
				 */
				if(search_txt.getText().toString().length() != 0){
					String temp = search_txt.getText().toString();
					search_txt.setText(temp.subSequence(0, temp.length() - 1));
				}else{
					 
				}
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				close();
			}
		});
		
		//防止快速点击
		send.setOnClickListener(new OnClickListener() {
			
		

			@Override
			public void onClick(View arg0) {
				//防止快速点击
				if (System.currentTimeMillis() - lastClick <= 2000) {
					return;
				}
				lastClick = System.currentTimeMillis();

				try{
					if(loadSendPop==null){
						loadSendPop = new CommonLoadSendPop(context, "正在为您开台");
						loadSendPop.showSheet(false);
					}
					//二次桌台
					//find();
					load(Integer.parseInt(search_txt.getText().toString())); 
				}catch(NumberFormatException e){
					
				}
				
			}
		});
		select_num_view.setAdapter(searchAdapter);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	
	private void find(){
		//{'CMD':'TB','CONTENT':{'MhtTableArea':[{"TableAreaId":1},…]
			if(sharedUtils.getStringValue("table_name").length()!=0&&sharedUtils.getStringValue("IP").length()!=0){
			/*	 
				{
					  "Cmd":"CXWJZD",
					  "Lid":"001",
					  "Pid":"1C-6F-65-7D-24-61",
					  "Lan":1,
					  "BillName":"101台",
					}*/
				String mac = FileUtils.getDeviceId();
				String ip = sharedUtils.getStringValue("IP");
				OkHttpUtils .postString()
		        .url(U.VISTER(ip, U.URL))
		        .mediaType(MediaType.parse("application/json; charset=gb2312"))
		        .content("{\"Cmd\":\"CXWJZD\",\"Lid\":\""+mac+"\",\"Pid\":\""+mac+"\",\"Lan\":1,\"BillName\":\""+sharedUtils.getStringValue("table_name")+"\"}")
		        .build()
		        .execute(tableCallback);
			}else{
				//什么都没有那么就把桌台状态清掉 
				
			}
	}
	private StringCallback tableCallback  =new StringCallback() {
		
		@Override
		public void onResponse(String response, int id) {

			 
			 
			try {
				JSONObject jsonObject = new JSONObject(response);
				int status = jsonObject.getInt("Code");
				switch (status) {
				case 1:
					JSONArray json = jsonObject.getJSONArray("Bills");
					int len = json.length();
					if(len==1){
						JSONObject obj = json.getJSONObject(0);
						NewDataToast.makeTextL( "【"+obj.getString("BillName")+"】已开台,请联系服务员或几秒后重新下单", 3000);
					}else{
						load(Integer.parseInt(search_txt.getText().toString())); 
					}
					break;
				case 0:
					//异常
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onError(Call call, Exception e, int id) {

			NewDataToast.makeText( "连接失败");
		}
	};
	private CommonLoadSendPop loadSendPop;
	private RequestCall requestCall;
	/**
	 * 开台
	 */
	private void load(int num){
		String ip = sharedUtils.getStringValue("IP");
		String tableCode = sharedUtils.getStringValue("table_code");
		String post = "{\"DeviceSerialNo\":\"" + sharedUtils.getStringValue("deviceSerialNo") + "\",\"TableNo\":\""+tableCode+"\",\"GstCount\":"+num+",\"GstSrc\":\"\",\"Waiter\":\"\",\"Remark\":\"\",\"User\":\""+optName+"\",\"ClientType\":\"Android\",\"ClientMac\":\""+FileUtils.getDeviceId()+"\"}";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("data", post);
			requestCall = OkHttpUtils .postString()
			        .url(U.VISTER(ip, U.URL_TABLE_OPEN))
			        .mediaType(MediaType.parse("application/json; charset=utf-8"))
			        .content(jsonObject.toString())
			        .build();
					P.c(post.toString());
			        requestCall.execute(personCallback);
		} catch (JSONException e) {

			e.printStackTrace();
			NewDataToast.makeText( "发送失败");
		}
		
		
	}
	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				NewDataToast.makeTextL( (String)msg.obj,2000);
				break;

			default:
				break;
			}
		};
	};
	private StringCallback personCallback = new StringCallback() {
		
		@Override
		public void onResponse(String response, int id) {

			P.c(response);
			//
			try {
				JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
				if(jsonObject.getBoolean("Success")){
				
					String billId = jsonObject.getString("Data");
					sharedUtils.setStringValue("billId", billId);
					P.c("成功开台"+billId);
					sharedUtils.setIntValue("person", Integer.parseInt(search_txt.getText().toString()));
					//-----通知service更改
					Intent intent = new Intent();
					intent.setAction(Common.SERVICE_ACTION);
					intent.putExtra("open_table", "");
					context.startService(intent);
					
					if(loginBuy==null){
						buyClick.person();
					}else{
						buyClick.person(loginBuy,optName);
					}
					
				}else{
					
					Message msg = new Message();
					msg.what = 0;
					msg.obj = jsonObject.getString("Data");
					handler.sendMessage(msg);
				}
			
			} catch (JSONException e) {

				e.printStackTrace();
				NewDataToast.makeTextL( "数据异常",2000);
			}
			close();
		}
		@Override
		public void onError(Call call, Exception e, int id) {

			NewDataToast.makeText( "连接失败");
			close();
		}
	};
	private void close(){
		if(loadSendPop!=null){
			loadSendPop.cancle();
			loadSendPop = null;
		}
		if(dlg!=null){
			dlg.cancel();
			dlg = null;
		}
	}
}
