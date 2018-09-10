package pad.stand.com.haidiyun.cy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.settings.MessageBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;

import pad.stand.com.haidiyun.cy.widget.EffectiveShapeView;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;

public class CyMainTitleAdapter extends RecyclerView.Adapter<CyMainTitleAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private Context context;

    private  ArrayList<MenuBean> foodsBeans;
    private Handler handler;
    private  Map<String,String> map0;


    public CyMainTitleAdapter(Context context, ArrayList<MenuBean> foodsBeans, Handler handler){
        this.context = context;
        this.foodsBeans = foodsBeans;
        this.handler = handler;

        inflater = LayoutInflater.from(context);
    }
    public void ref(Map<String,String> map0){
        this.map0 = map0;

        notifyDataSetChanged();
    }
    public void updata(ArrayList<MenuBean> foodsBeans){
        this.foodsBeans = foodsBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.cy_main_title_item,parent,false);

            return new CyMainTitleAdapter.ViewHolder(v);
    }
    private int mposition = -1;
    public void selectPosition(int mposition){
        this.mposition = mposition;
        notifyDataSetChanged();
    }
    public int nowIndex(){
        return  mposition;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        MenuBean bean = foodsBeans.get(i);
        viewHolder.item0.setText(bean.getName());
        int len = bean.getName().length();
        viewHolder.child.setLayoutParams(new LinearLayout.LayoutParams((40*len)+100, LinearLayout.LayoutParams.MATCH_PARENT));
        String key = String.valueOf(i);
        if (i == mposition) {

            viewHolder.item0.setSelected(true);
            viewHolder.item0.setTextSize(22);
            viewHolder.item0.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.item0.setBackgroundResource(R.drawable.cy_title_bg);
            viewHolder.num_show.setVisibility(View.VISIBLE);

            if(map0!=null&&map0.containsKey(key)){
                int size = getItemCount();
                if(i==size-1){
                    //最后一个的时候，数量计算方式
                    int two = Integer.parseInt(String.valueOf(map0.get(String.valueOf(i-1))));

                    viewHolder.num_show.setText(String.valueOf(size-two));
                }else{
                    int one = Integer.parseInt(String.valueOf(map0.get(String.valueOf(i+1))));
                    int two = Integer.parseInt(String.valueOf(map0.get(String.valueOf(i))));

                    viewHolder.num_show.setText(String.valueOf(one-two));
                }

            }
        } else {
            viewHolder.item0.setSelected(false);
            viewHolder.item0.setBackgroundResource(R.drawable.shape_bg_round_nocolor);
            viewHolder.item0.setTextSize(20);
            viewHolder.item0.setTextColor(context.getResources().getColor(R.color.text_cr));
            viewHolder.num_show.setVisibility(View.GONE);
        }
        viewHolder.child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClick!=null){
                    itemClick.clickItem(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodsBeans.size();
    }
    class  ViewHolder extends  RecyclerView.ViewHolder{

        TextView item0;
        LinearLayout child;
        TextView num_show;
        public ViewHolder(View itemView) {
            super(itemView);
            child = (LinearLayout) itemView.findViewById(R.id.child);
            item0 = (TextView) itemView.findViewById(R.id.item0);
            num_show = (TextView) itemView.findViewById(R.id.num_show);
        }
    }
    private CyMainTitleAdapter.onItemClick itemClick;
    public void setOnItemClick(CyMainTitleAdapter.onItemClick click){
        this.itemClick = click;
    }

    /**
     * 增加点击按钮事件
     */
    public interface onItemClick {
        void clickItem(int pos);
    }

}
