package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.TBean;
import pad.stand.com.haidiyun.www.bean.TablesBean;

public class TzAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<TBean> tablesAreas;

	public TzAdapter(Context context,
                     ArrayList<TBean> tablesAreas) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.tablesAreas = tablesAreas;
		inflater = LayoutInflater.from(context);
	}
	public void updata(ArrayList<TBean> tablesAreas){
		this.tablesAreas = tablesAreas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return tablesAreas.size();
	}

	private int mposition = -1;

	public void selectPosition(int mposition) {
		this.mposition = mposition;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {

		return tablesAreas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}
	public String getSelectName(){
		if(mposition!=-1){
			return tablesAreas.get(mposition).getName();
		}
		return  null;
	}
	private class ViewHolder {
		TextView menu_title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.tz_layout, null);
			viewHolder.menu_title = (TextView) convertView
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
		viewHolder.menu_title.setText(tablesAreas.get(position).getName());
		return convertView;
	}

}
