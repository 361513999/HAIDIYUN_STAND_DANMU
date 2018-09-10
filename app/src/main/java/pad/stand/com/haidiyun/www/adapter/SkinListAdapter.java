package pad.stand.com.haidiyun.www.adapter;

import java.util.ArrayList;
import com.zc.changeskin.SkinManager;
import com.zc.changeskin.callback.ISkinChangingCallback;
import com.zc.changeskin.utils.PrefUtils;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.Skin;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.ui.CardValActivity;
import pad.stand.com.haidiyun.www.ui.OrderActivity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SkinListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private ArrayList<Skin> skins;
	private PrefUtils mPrefUtils;
	private Context context;
	private Handler handler;
	public SkinListAdapter(Context context,ArrayList<Skin> skins, Handler handler) {
		 inflater = LayoutInflater.from(context);
		 this.context = context;
		 this.skins = skins;
		 this.handler = handler;
		 mPrefUtils = new PrefUtils(context);
	}
	@Override
	public int getCount() {
		return skins.size();
	}
	 public void updata(ArrayList<Skin> results){
		   this.skins = results;
		   notifyDataSetChanged();
	  }
	@Override
	public Object getItem(int arg0) {
		return skins.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	 private class ViewHolder {
		 TextView skin_name;
		 TextView skin_select;
		}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		 if (convertView == null
					|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
				viewHolder = new ViewHolder();
					convertView = inflater.inflate(R.layout.skin_item, null);
					 viewHolder.skin_name  = (TextView) convertView.findViewById(R.id.skin_name);
					 viewHolder.skin_select = (TextView) convertView.findViewById(R.id.skin_select);
					convertView.setTag(R.drawable.ic_launcher + position);
		  }else {
				viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		 }
		 final Skin result = skins.get(position);
		 viewHolder.skin_name.setText(result.getName());
			 viewHolder.skin_select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					 if(result.getPkg().length()==0){
						 SkinManager.getInstance().removeAnySkin();
						 P.c("恢复皮肤");
//						 BaseApplication.application.resetApplicationAll();
						 handler.sendEmptyMessage(1);
					 }else{
							SkinManager.getInstance().changeSkin(Common.SOURCE_SKIN+result.getApk(), result.getPkg(), new ISkinChangingCallback() {
								
								@Override
								public void onStart() {
									
								}
								@Override
								public void onError(Exception e) {
									P.c("更换失败"+e.getMessage());
									handler.sendEmptyMessage(0);
								}
								
								@Override
								public void onComplete() {

									P.c("更换完成");
									handler.sendEmptyMessage(1);
//									BaseApplication.application.resetApplicationAll();
								}
							});
					 }
//					 SkinManager.getInstance().notifyChangedListeners();
					 notifyDataSetInvalidated();
				}
				
			});
		 String saveName = mPrefUtils.getPluginPkgName();
		 P.c("----------->"+saveName);
		 if(saveName.equals(result.getPkg())){
			 viewHolder.skin_select.setText("已选");
		 }else{
			 viewHolder.skin_select.setText("选择");
		 }
		return convertView;
	}

	

}
