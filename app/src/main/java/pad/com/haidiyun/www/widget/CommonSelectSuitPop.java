package pad.com.haidiyun.www.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.SuitItemAdapter;
import pad.com.haidiyun.www.adapter.TcAdapter;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.TcBean;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.TcT;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;

/**
 * 套餐的弹出框
 *
 * @author Administrator
 *
 */
public class CommonSelectSuitPop {
	private Context context;
	private FouceBean foodsBean;
	private ListView suit_item;
	private IDialog dlg;
	 private TcAdapter tcAdapter;
	private TextView name;
	private SuitItemAdapter suitItemAdapter;

	// 必选
	GridView suit_views;
//
	private TextView save, cancle;
	private TcT t;
 	private ArrayList<ArrayList<Boolean>> maps;
	private ArrayList<ArrayList<TcBean>> tcBeansList;
	public CommonSelectSuitPop(Context context, FouceBean foodsBean , TcT t) {
		this.context = context;
		this.foodsBean = foodsBean;
		this.t = t;
	}
	private Handler handler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
				case 3:
					int index = suitItemAdapter.getCurrentPosition();
					maps.set(index,(ArrayList<Boolean>) msg.obj);
					break;
			case 1:
				suitItemAdapter.setData(itemBeans);
				//这里创建对应的map
				if(itemBeans.size()!=0){
					maps = new ArrayList<ArrayList<Boolean>>();
					tcBeansList = new ArrayList<ArrayList<TcBean>>();

					for(int i=0;i<itemBeans.size();i++){
						maps.add(new ArrayList<Boolean>());
						tcBeansList.add(new ArrayList<TcBean>());
					}
					suitItemAdapter.selectPosition(0);
					if(maps!=null){
						selectItem(0);
					}

				}
					break;
			case 0:

				if(tcBeans.size()!=0){
					int idx = suitItemAdapter.getCurrentPosition();
//					tcAdapter.init();
					ArrayList<Boolean> is = maps.get(idx);
					tcAdapter.updata(tcBeans,is);
 					if(is!=null&&is.size()!=0){
						tcAdapter.setBoolean(is);
					}
					isAll();
				}
				rest();
				break;
			default:
				break;
			}
		};
	};

	//组合套餐的最小选择是所有菜品就全选
	private void isAll(){
		TcBean item = getCurrentoBJ();
		if(item.getMin()==tcBeans.size()){
			tcAdapter.setAllSelected();
		}
	}
	private void selectItem(final int inde){
		new Thread(){
			@Override
			public void run() {
				super.run();
				tcBeans.clear();
                tcBeans = 	DB.getInstance().getTc(itemBeans.get(inde).getCode());
				tcBeansList.set(inde, DB.getInstance().getTc(itemBeans.get(inde).getCode()));

				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	private View parent_d;
	private ArrayList<TcBean> itemBeans,tcBeans;
	public void showSheet() {
		dlg = new IDialog(context, R.style.config_pop_style);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.flip_select_suit_pop_view, null);
		final int cFullFillWidth = 500;
		layout.setMinimumWidth(cFullFillWidth);
		parent_d = layout.findViewById(R.id.parent_d);
		suit_item = (ListView) layout.findViewById(R.id.suit_item);
		itemBeans = new ArrayList<TcBean>();
		tcBeans = new ArrayList<TcBean>();
		suitItemAdapter = new SuitItemAdapter(context,itemBeans);
		suit_item.setAdapter(suitItemAdapter);
		suit_item.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				suitItemAdapter.selectPosition(position);
				selectItem(position);
			}
		});
		dlg.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub
				FileUtils.start(Effectstype.Flipv, parent_d);
			}
		});
		name = (TextView) layout.findViewById(R.id.tc_name);
		name.setText(foodsBean.getName());
		// 必选
		suit_views = (GridView) layout.findViewById(R.id.suit_views);
		// 确保是单行显示
		tcAdapter = new TcAdapter(context, tcBeans, false);
		suit_views.setAdapter(tcAdapter);
		//
		save = (TextView) layout.findViewById(R.id.tc_pop_sure);
		cancle = (TextView) layout.findViewById(R.id.tc_pop_cancle);
		new Thread(){
			public void run() {
				    itemBeans.clear();
				     itemBeans = DB.getInstance().getSuitItems(foodsBean.getCode());
					 handler.sendEmptyMessage(1);
			};
		}.start();
		suit_views.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

				TcBean tb = getCurrentoBJ();
				if(tcAdapter.getSelectedNum()<((tb==null)?0:tb.getMax())){
					tcAdapter.selectPosition(position);
					rest();
				}else {
					if(tcAdapter.isSelected(position)){
						tcAdapter.selectPosition(position);
						rest();
						return;
					}
					NewDataToast.makeText("已超出选择");
				}
				Message msg = new Message();
				msg.obj = tcAdapter.getBooleans();
				msg.what = 3;
				handler.sendMessage(msg);


			}
		});
		//
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				close();
			}
		});
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<TcBean> selecTcBeans = new ArrayList<TcBean>();
				try {


					for(int i=0;i<itemBeans.size();i++){
						//
						if(maps.get(i).size()==0){
							NewDataToast.makeText("所选菜品不够");
							return;
						}
						int count = 0;
						for(int j=0;j<maps.get(i).size();j++){
							//菜品
							if(maps.get(i).get(j)){
								count = count+1;

							}
							if(j==maps.get(i).size()-1){
								if(count<itemBeans.get(i).getMin()){
									NewDataToast.makeText("所选菜品不够");
									return;
								}
							}

						}

					}
				}catch (IndexOutOfBoundsException e){
					NewDataToast.makeText("所选菜品不够");
					return;
				}
				// 生成套餐详细
				for (int i = 0; i < maps.size(); i++) {
					for(int j=0;j<maps.get(i).size();j++){
						if(maps.get(i).get(j)){
							//套餐确定
							System.out.println("名字"+tcBeansList.get(i).get(j).getName());
							selecTcBeans.add(tcBeansList.get(i).get(j));
						}
					}
				}
				StringBuilder builder = new StringBuilder();
				StringBuilder builder1 = new StringBuilder();
				for(int i=0;i<selecTcBeans.size();i++){
					if(i!=selecTcBeans.size()-1){
						builder.append(selecTcBeans.get(i).getCode()+",");
						builder1.append(selecTcBeans.get(i).getName()+",");
					}else{
						builder.append(selecTcBeans.get(i).getCode());
						builder1.append(selecTcBeans.get(i).getName());
					}
				}
				t.insert(foodsBean, builder.toString(),builder1.toString());
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
	private TcBean getCurrentoBJ() {
		int index = suitItemAdapter.getCurrentPosition();
		if(index!=-1){
			return  itemBeans.get(index);
		}
		return  null;
	}
	private void rest(){
		int select = tcAdapter.getSelectedNum();
		TcBean tb = getCurrentoBJ();
		if(select!=0){
			save.setText("确定选择("+select+"/"+(tb==null?0:tb.getMax())+")");
		}else{
			save.setText("确定选择");
		}
	}
	public int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	private void close() {
		if (dlg != null && dlg.isShowing()) {
			dlg.cancel();
			dlg = null;
		}
	}
}
