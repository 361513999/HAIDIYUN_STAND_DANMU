package pad.stand.com.haidiyun.www.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import pad.stand.com.haidiyun.www.R;

/***
 * 棣栭〉閫氱敤鑿滃搧灞曠ず閫傞厤锟?
 *
 * @author tao78
 *
 */
public class SearchAdapter extends BaseAdapter {

    private String[] zm;
    private Context context;
    private boolean isWord;

    public SearchAdapter(Context context, String[] zm, boolean isWord) {
        this.context = context;
        this.zm = zm;
        this.isWord = isWord;
    }

    ViewHolder viewHolder = null;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + position) == null) {
            convertView = LinearLayout.inflate(context, R.layout.search_item, null);
            viewHolder.tip = (TextView) convertView.findViewById(R.id.tip);

        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
                    + position);
        }
        if ("中".equals(zm[position])) {
            viewHolder.tip.setText("中/英");
            viewHolder.tip.setTextColor(context.getResources().getColor(R.color.ck_item));
            viewHolder.tip.setTextSize(18);
        } else {
            viewHolder.tip.setTextSize(35);
            viewHolder.tip.setText(zm[position]);
        }
        if (clickTemp == position) {
            if (!isWord){
                viewHolder.tip.setTextColor(context.getResources().getColor(R.color.je_right));
            }else {
                viewHolder.tip.setTextColor(context.getResources().getColor(R.color.search_color));
            }
        } else {
//            viewHolder.tip.setTextColor(context.getResources().getColor(R.color.ck_item));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tip;
    }

    @Override
    public int getCount() {
        if (!isWord) {
            return 1;
        } else {
            return zm.length;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int arg0) {

        return null;
    }

    private int clickTemp = -1;
      public void setSelection(int position,boolean state){
          clickTemp = position;
          isWord = state;
      }
}