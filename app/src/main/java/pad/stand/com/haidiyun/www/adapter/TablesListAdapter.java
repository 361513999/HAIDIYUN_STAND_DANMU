package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.TableBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.inter.SelectTable;

public class TablesListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<TableBean> results;
    private SelectTable close;
    private Context context;
    private String optName;
    private SharedUtils sharedUtils;

    public TablesListAdapter(Context context, List<TableBean> results, SelectTable close, String optName) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.results = results;
        this.close = close;
        this.optName = optName;
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    @Override
    public int getCount() {
        return results.size();
    }

    public void updata(List<TableBean> results) {
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
        TextView item0,show;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.table_item, null);
            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.show = (TextView) convertView.findViewById(R.id.show);
            convertView.setTag(R.drawable.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher + position);
        }
        final TableBean result = results.get(position);

        viewHolder.item0.setText(result.getName() );

        if (result.isLocked()) {
            viewHolder.item0.setTextColor(context.getResources().getColor(R.color.ban));
        } else {
            viewHolder.item0.setTextColor(context.getResources().getColor(R.color.text_cr));
        }

        if (result.getState() != null) {

            if (result.getState().equals("F")) {
                viewHolder.item0.setTextColor(context.getResources().getColor(R.color.tb_k));
//                viewHolder.item0.setBackgroundResource(R.drawable.table_f);
            } else if (result.getState().equals("O")) {
                viewHolder.item0.setTextColor(context.getResources().getColor(R.color.tb_j));
                // viewHolder.item0.setBackgroundResource(R.drawable.table_o);
            } else if (result.getState().equals("P")) {
                viewHolder.item0.setTextColor(context.getResources().getColor(R.color.tb_d));
                // viewHolder.item0.setBackgroundResource(R.drawable.table_o);
            } else if (result.getState().equals("R")) {
                viewHolder.item0.setTextColor(context.getResources().getColor(R.color.blue_semi_transparent));
                // viewHolder.item0.setBackgroundResource(R.drawable.table_r);
            }
            if(result.isTemp()){
                viewHolder.item0.setTextColor(context.getResources().getColor(R.color.red));
            }
        }
        viewHolder.item0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //包房价
//                String pre = sharedUtils.getStringValue("typePre");
//                String typeBack = result.getTypeCode();
//                int num = DB.getInstance().getTableBeans().size();
//                1、已下单有菜品的情况下，不允许进行在包房和普通房之间互换，提示目前桌台类型及菜品价格与原桌台类型价格不一致，无法换台
                //关闭该提示
//                if (!sharedUtils.getStringValue("typePre").equals(typeBack) && DB.getInstance().getTableBeans().size() != 0) {
//                    NewDataToast.makeText(context.getResources().getString(R.string.changeTip));
//                } else {
//                String s = result.toString();
                sharedUtils.setStringValue("tableType", result.getPriceType());
                if (result.isLocked()) {
                    //被锁定
                    close.isLocked();
                } else {
                    close.select(result, optName, 0);
                    //selectTable.select(result,optName);
//                    }
                }
            }
        });
        //桌台信息列表适配器
        return convertView;
    }

}
