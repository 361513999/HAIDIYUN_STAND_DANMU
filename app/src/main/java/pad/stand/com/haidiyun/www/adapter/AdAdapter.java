package pad.stand.com.haidiyun.www.adapter;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.bean.ResonMenuBean;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<MenuBean> resonMenuBeans;
	private Handler handler;
	public AdAdapter(Context context,
			ArrayList<MenuBean> resonMenuBeans,Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.handler = handler;
		this.resonMenuBeans = resonMenuBeans;
		inflater = LayoutInflater.from(context);
	}
	public void updata(ArrayList<MenuBean> resonMenuBeans){
		this.resonMenuBeans = resonMenuBeans;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {

		return resonMenuBeans.size();
	}

	private int mposition = -1;

	public void selectPosition(int mposition) {
		this.mposition = mposition;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {

		return resonMenuBeans.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	private class ViewHolder {
		Button menu_title;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {

		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.ad_item_layout, null);
			viewHolder.menu_title = (Button) convertView
					.findViewById(R.id.menu_title);

			convertView.setTag(R.drawable.ic_launcher + position);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
					+ position);
		}
		if (position == mposition) {
			viewHolder.menu_title.setSelected(true);
		} else {
			viewHolder.menu_title.setSelected(false);
		}
		// -----------
		viewHolder.menu_title.setText(resonMenuBeans.get(position).getName());
		viewHolder.menu_title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				Message msg = new Message();
				msg.what = -9;
				msg.arg1 = position;
				handler.sendMessage(msg);
			}
		});
		return convertView;
	}

}
