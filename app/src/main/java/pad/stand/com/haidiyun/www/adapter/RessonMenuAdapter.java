package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.ResonMenuBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;

public class RessonMenuAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ResonMenuBean> resonMenuBeans;
    private SharedUtils sharedUtils;

    public RessonMenuAdapter(Context context,
                             ArrayList<ResonMenuBean> resonMenuBeans) {
        this.context = context;
        this.resonMenuBeans = resonMenuBeans;
        inflater = LayoutInflater.from(context);
        this.sharedUtils = new SharedUtils(Common.CONFIG);
    }

    public void updata(ArrayList<ResonMenuBean> resonMenuBeans) {
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
        TextView menu_title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.reason_item_layout, null);
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
//		viewHolder.menu_title.setText(resonMenuBeans.get(position).getName()+(resonMenuBeans.get(position).isMultySelect()?"多":"单")+(resonMenuBeans.get(position).isMustSel()?"(必选)":""));
        viewHolder.menu_title.setText(resonMenuBeans.get(position).getName() + (resonMenuBeans.get(position).isMustSel() ? "(必选)" : ""));
        if (sharedUtils.getBooleanValue("is_lan")) {
            //变为英文
            viewHolder.menu_title.setText(resonMenuBeans.get(position).getNameEn() + (resonMenuBeans.get(position).isMustSel() ? "(must select)" : ""));
        } else {
            viewHolder.menu_title.setText(resonMenuBeans.get(position).getName() + (resonMenuBeans.get(position).isMustSel() ? "(必选)" : ""));
        }
        return convertView;
    }

}
