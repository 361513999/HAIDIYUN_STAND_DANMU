package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.TcBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.widget.T_Image;

public class TcAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<TcBean> beans;
    private Context context;
    private boolean isNeed;
    private List<Boolean> selList = new ArrayList<Boolean>();

    public TcAdapter(Context context, List<TcBean> beans, boolean isNeed) {
        this.context = context;
        this.isNeed = isNeed;
        inflater = LayoutInflater.from(context);
        setData(beans);
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    public boolean isSelected(int arg0) {
        return selList.get(arg0);
    }

    public List<Boolean> getBooleans() {
        return selList;
    }

    public void setData(List<TcBean> beans) {
        selList.clear();
        if (beans == null) {
            beans = new ArrayList<TcBean>();
        } else {
            this.beans = beans;
            for (TcBean tcBean : beans) {
                if (isNeed) {
                    selList.add(true);
                } else {
                    selList.add(false);
                }
            }
        }
    }

    public void setData(List<TcBean> beans, ArrayList<Boolean> list) {
        //selList.clear();
        if (beans == null) {
            beans = new ArrayList<TcBean>();
        } else {
            this.beans = beans;
            if (list != null) {
                this.selList = list;

                if (list.size() == 0) {

                    for (TcBean tcBean : beans) {

                        selList.add(false);

                    }
                }
            }


        }
    }


    public void setBoolean(List<Boolean> selList) {
        this.selList = selList;
        notifyDataSetChanged();
    }

    public void updata(List<TcBean> beans) {
        setData(beans);
        notifyDataSetChanged();
    }

    public void updata(List<TcBean> beans, ArrayList<Boolean> list) {
        setData(beans, list);
        notifyDataSetChanged();
    }

    /**
     * 设置全选
     */
    public void setAllSelected() {
        for (int i = 0; i < selList.size(); i++) {
            selList.set(i, true);
        }
        notifyDataSetChanged();
    }

    public void init() {

        selList.clear();
        for (TcBean tcBean : beans) {
            selList.add(false);
        }
    }

    public void selectPosition(int mposition) {
        if (!isNeed) {
            if (selList.get(mposition)) {
                selList.set(mposition, false);
            } else {
                selList.set(mposition, true);
            }
        }
        notifyDataSetChanged();
    }

    public int getSelectedNum() {
        int select = 0;
        for (int i = 0; i < selList.size(); i++) {
            if (selList.get(i)) {
                select += 1;
            }
        }
        return select;
    }

    public List<TcBean> getselTc() {
        List<TcBean> selBeans = new ArrayList<TcBean>();
        for (int i = 0; i < beans.size(); i++) {
            if (selList.get(i)) {
                selBeans.add(beans.get(i));
            }
        }
        return selBeans;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tc_item, null);
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.tc_gv_item_name);

            viewHolder.img = (T_Image) convertView
                    .findViewById(R.id.tc_gv_item_img);
            viewHolder.state = (ImageView) convertView
                    .findViewById(R.id.tc_gv_item_stste);
            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
                    + position);
        }

        if (beans.size() > 0) {
            final TcBean tcBean = beans.get(position);
            String ss = tcBean.toString();
            String url = "file://" + Common.ZIP + tcBean.getPath();
            Glide.with(BaseApplication.application).load(url).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(viewHolder.img);
            viewHolder.img.setTag(R.id.image_tag, url);
            viewHolder.name.setText(tcBean.getName() + "(" + tcBean.getPrice() + ")");
            if (isNeed) {
                viewHolder.state.setVisibility(View.VISIBLE);
            } else {
                if (selList.get(position)) {
                    viewHolder.state.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.state.setVisibility(View.GONE);
                }
            }
        }
        return convertView;

    }

    private class ViewHolder {
        T_Image img;
        ImageView state;
        TextView name;
    }

}
