package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;

public class MenusAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private ArrayList<MenuBean> menuBeans;
    private Context context;
    private SharedUtils sharedUtils;

    public MenusAdapter(Context context, ArrayList<MenuBean> menuBeans) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.menuBeans = menuBeans;
        this.sharedUtils = new SharedUtils(Common.CONFIG);
    }

    @Override
    public int getCount() {
        // 能设置的最大值
        return menuBeans.size();
    }

    public void updata(ArrayList<MenuBean> menuBeans) {
        this.menuBeans = menuBeans;
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
        TextView title;
        ImageView img;
        ImageView selImg;

    }

    private int mposition = -1;

    public void selectPosition(int mposition) {
        this.mposition = mposition;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_lv_menu_item, null);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.h_item_tv);
            viewHolder.img = (ImageView) convertView
                    .findViewById(R.id.h_item_img);
            viewHolder.selImg = (ImageView) convertView
                    .findViewById(R.id.h_item_tv_jt);

            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
                    + position);
        }

        final MenuBean obj = menuBeans.get(position);
        String s = obj.toString();
//        viewHolder.title.setText(obj.getName());
        if (sharedUtils.getBooleanValue("is_lan")) {
            //变为英文
            viewHolder.title.setText(obj.getName_en());
        } else {
            viewHolder.title.setText(obj.getName());
        }
        viewHolder.img.setBackgroundResource(R.drawable.h_menu_select);

        if (position == mposition) {

            viewHolder.img.setSelected(true);
            viewHolder.title.setSelected(true);
            viewHolder.title.setTextSize(20);
            viewHolder.selImg.setVisibility(View.VISIBLE);

        } else {
            viewHolder.img.setSelected(false);
            viewHolder.title.setSelected(false);
            viewHolder.title.setTextSize(18);
            viewHolder.selImg.setVisibility(View.GONE);
        }
        return convertView;

    }

}
