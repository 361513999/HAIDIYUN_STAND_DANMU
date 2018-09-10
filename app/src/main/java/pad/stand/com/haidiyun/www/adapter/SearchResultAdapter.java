package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import pad.stand.com.haidiyun.www.bean.FouceBean;
public class SearchResultAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<FouceBean> searchDishs;
	private Context context;

	public SearchResultAdapter(Context context,ArrayList<FouceBean> searchDishs) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.searchDishs = searchDishs;
	}

	@Override
	public int getCount() {
		// 能设置的最大值
		return searchDishs.size();
	}

	public void updata(ArrayList<FouceBean> searchDishs) {
		this.searchDishs = searchDishs;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView name;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		 

		return convertView;

	}

}
