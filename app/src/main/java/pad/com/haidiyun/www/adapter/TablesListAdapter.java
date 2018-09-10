package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.inter.SelectTable;

public class TablesListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<TableBean> results;
	private SelectTable selectTable,close;
	private Context context;
	private String optName;
	public TablesListAdapter(Context context, List<TableBean> results, SelectTable selectTable, SelectTable close, String optName) {
		this.context = context;
		 inflater = LayoutInflater.from(context);
		 this.results = results;
		 this.selectTable = selectTable;
		 this.close = close;
		 this.optName = optName;
	}
	@Override
	public int getCount() {
		return results.size();
	}
	 public void updata(List<TableBean> results){
		   this.results = results;
		   notifyDataSetChanged();
	  }
	@Override
	public Object getItem(int arg0) {
		return results.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	 private class ViewHolder {
		 TextView item0;
	 }
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		 if (convertView == null
					|| convertView.getTag(R.drawable.ic_launcher + position) == null) {
				viewHolder = new ViewHolder();
					convertView = inflater.inflate(R.layout.flip_table_item, null);
					 viewHolder.item0  = (TextView) convertView.findViewById(R.id.item0);
					convertView.setTag(R.drawable.ic_launcher + position);
		  }else {
				viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
		 }
		 final TableBean result = results.get(position);
		 viewHolder.item0.setText(result.getName());
		 if(result.isLocked()){
			 viewHolder.item0.setTextColor(context.getResources().getColor(R.color.ban));
		 }else{
			 viewHolder.item0.setTextColor(context.getResources().getColor(R.color.text_cr));
		 }
		if(result.getState()!=null){

			if(result.getState().equals("F")){
				viewHolder.item0.setTextColor(context.getResources().getColor(R.color.text_cr));
				//viewHolder.item0.setBackgroundResource(R.drawable.table_f);
			}else if (result.getState().equals("O")) {
				viewHolder.item0.setTextColor(context.getResources().getColor(R.color.red));
				// viewHolder.item0.setBackgroundResource(R.drawable.table_o);
			}else if (result.getState().equals("R")) {
				viewHolder.item0.setTextColor(context.getResources().getColor(R.color.blue_semi_transparent));
				// viewHolder.item0.setBackgroundResource(R.drawable.table_r);
			}else if (result.getState().equals("P")) {
				viewHolder.item0.setTextColor(context.getResources().getColor(R.color.je_left));
				// viewHolder.item0.setBackgroundResource(R.drawable.table_o);
			}
		}

		 viewHolder.item0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(result.isLocked()){
					//被锁定
					close.isLocked();
				}else {
					close.select(result,null);
					selectTable.select(result,optName);
				}
				
			}
		});
		return convertView;
	}

}
