package pad.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.bean.SuitItemBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;

/**
 * Created by Administrator on 2017/10/31.
 */


public class SuitAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private SharedUtils sharedUtils;
    private ArrayList<SuitItemBean> listSuit;
    SuitItemBean bean;
    private int W = 0;

    public SuitAdapter(Context context, SuitItemBean results, int W) {
        inflater = LayoutInflater.from(context);
        this.bean = results;
        this.W = W;
        sharedUtils = new SharedUtils(Common.SHARED_WIFI);
        initList();
    }

    private void initList() {
        listSuit = new ArrayList<SuitItemBean>();
        String temp1 = bean.getCode();
        String temp = bean.getName();
        String tps[] = temp.split(",");
        String code[] = temp1.split(",");
        for (int i = 0; i < tps.length; i++) {
            SuitItemBean bean = new SuitItemBean();
            bean.setCode(code[i]);
            bean.setName(tps[i]);
            listSuit.add(bean);
        }
    }

    @Override
    public int getCount() {
        return listSuit.size();
    }

    public void updata(ArrayList<SuitItemBean> results) {
        this.listSuit = results;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int arg0) {
        return listSuit.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    private class ViewHolder {
        TextView name;
        TextView reason_name;
        TextView res;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        SuitAdapter.ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.flip_suit_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.reason_name = (TextView) convertView.findViewById(R.id.reason_name);
            viewHolder.res = (TextView) convertView.findViewById(R.id.res);

            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
        }
        SuitItemBean bean = listSuit.get(position);
        viewHolder.name.setText(bean.getName());
        viewHolder.reason_name.setText("");

        return convertView;
    }

}
