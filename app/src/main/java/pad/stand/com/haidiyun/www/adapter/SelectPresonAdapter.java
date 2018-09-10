package pad.stand.com.haidiyun.www.adapter;

import pad.stand.com.haidiyun.www.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * 棣栭〉閫氱敤鑿滃搧灞曠ず閫傞厤锟?
 * 
 * @author tao78
 * 
 */
public class SelectPresonAdapter extends BaseAdapter {

	private String[] zm;
	private Context context;
	public SelectPresonAdapter(Context context,String[] zm) {
		this.context =context;
		this.zm = zm;
	}

	ViewHolder viewHolder = null;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolder = new ViewHolder();
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			convertView = LinearLayout.inflate(context, R.layout.select_num_item, null);
			viewHolder.tip = (TextView) convertView.findViewById(R.id.tip);

		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
					+ position);
		}
		viewHolder.tip.setText(zm[position]);
		return convertView;
	}

	private class ViewHolder {
		TextView tip;
	}

	@Override
	public int getCount() {
		return zm.length;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	 

}