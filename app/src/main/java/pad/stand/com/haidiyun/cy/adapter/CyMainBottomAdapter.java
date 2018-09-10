package pad.stand.com.haidiyun.cy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import pad.stand.com.haidiyun.cy.widget.EffectiveShapeView;
import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;

public class CyMainBottomAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private Context context;

    private  ArrayList<FoodsBean> foodsBeans;
    private Handler handler;
    public DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    public CyMainBottomAdapter(Context context, ArrayList<FoodsBean> foodsBeans, Handler handler){
        this.context = context;
        this.foodsBeans = foodsBeans;
        this.handler = handler;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<FoodsBean> foodsBeans){
        this.foodsBeans = foodsBeans;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return foodsBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return foodsBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
   /* private int mposition = 0;

    public void selectPosition(int mposition) {

        this.mposition = mposition;
        notifyDataSetChanged();
    }
    public int nowIndex(){
        return  mposition;
    }*/
    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.drawable.ic_launcher + i) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cy_main_bottom_item, null);
            viewHolder.item0 = (EffectiveShapeView) convertView.findViewById(R.id.item0);
            viewHolder.child  = (LinearLayout) convertView.findViewById(R.id.child);
            viewHolder.bot_bg = (FrameLayout) convertView.findViewById(R.id.bot_bg);
            viewHolder.cy_bot_0 = (TextView) convertView.findViewById(R.id.cy_bot_0);
            viewHolder.cy_bot_1 = (TextView) convertView.findViewById(R.id.cy_bot_1);
            convertView.setTag(R.drawable.ic_launcher + i);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.drawable.ic_launcher
                    + i);
        }
     /*   if(mposition==i){
            viewHolder.bot_bg.setBackgroundResource(R.drawable.shape_bot_org);
        }else{
            viewHolder.bot_bg.setBackgroundColor(context.getResources().getColor(R.color.no_color));
        }*/

        FoodsBean bean = foodsBeans.get(i);
        String url = "file://" + Common.ZIP + bean.getPath();
      /*  ImageLoader.getInstance().displayImage(url,
                viewHolder.item0,
                options);*/

        Glide.with(BaseApplication.application).load(url).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT) .placeholder(R.drawable.default_image)//默认显示图片
                .error(R.drawable.default_image)//图片加载错误显示的图片
                .into(viewHolder.item0);
        viewHolder.cy_bot_0.setText(bean.getName());
        viewHolder.cy_bot_1.setText("￥"+bean.getPrice()+"/"+bean.getUnit());
        viewHolder.bot_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                P.c("点击。。。");
                Message msg = new Message();
                msg.what = 115;
                msg.arg1 = i;
                handler.sendMessage(msg);
            }
        });



        return convertView;
    }
    private class ViewHolder {
        EffectiveShapeView item0;
        LinearLayout child;
        TextView cy_bot_0,cy_bot_1;
        FrameLayout bot_bg;
    }
}
