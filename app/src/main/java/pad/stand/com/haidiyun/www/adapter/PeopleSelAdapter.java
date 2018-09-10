package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;

/***
 * 棣栭〉淇敼浜烘暟
 *
 * @author tao78
 *
 */
public class PeopleSelAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> mList;

    public PeopleSelAdapter(Context context, List<String> list) {
        this.inflater = LayoutInflater.from(context);
        setData(list);
    }

    public void setData(List<String> mList) {
        if (mList != null) {
            this.mList = mList;

        } else {
            this.mList = new ArrayList<String>();
        }
    }

    public void updataAdapter(List<String> mList) {
        this.setData(mList);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.set_people_num_gv_item,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.titel = (TextView) convertView
                    .findViewById(R.id.sel_people_num_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mList != null && mList.size() > 0) {

            viewHolder.titel.setText(mList.get(position));
            if ("+".equals(mList.get(position))) {
                viewHolder.titel.setTextSize(32);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView titel;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}