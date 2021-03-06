package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.TablesBean;

public class TablesAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<TablesBean> tablesAreas;;

	public TablesAdapter(Context context,
			ArrayList<TablesBean> tablesAreas) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.tablesAreas = tablesAreas;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tablesAreas.size();
	}

	private int mposition = -1;

	public void selectPosition(int mposition) {
		this.mposition = mposition;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return tablesAreas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	private class ViewHolder {
		TextView menu_title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.flip_area_item_layout, null);
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
