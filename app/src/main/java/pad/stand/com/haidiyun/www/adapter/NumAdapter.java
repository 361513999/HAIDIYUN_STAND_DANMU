package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.TBean;

public class NumAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private String []keys;
	private Handler handler;
	public NumAdapter(Context context,
					  String []keys, Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.keys = keys;
		inflater = LayoutInflater.from(context);
		this.handler = handler;
	}
	public void updata(String []keys){
		this.keys = keys;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return keys.length;
	}

	private int mposition = -1;

	public void selectPosition(int mposition) {
		this.mposition = mposition;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {

		return keys[arg0];
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	private class ViewHolder {
		TextView key_item;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {

		ViewHolder viewHolder;
		if (convertView == null
				|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.num_item, null);
			viewHolder.key_item = (TextView) convertView.findViewById(R.id.key_item);
			convertView.setTag(R.drawable.ic_launcher + position);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
					+ position);
		}
		viewHolder.key_item.setText(keys[position]);
		viewHolder.key_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = position;
				handler.sendMessage(msg);
			}
		});
		return convertView;
	}

}
