package pad.com.haidiyun.www.widget.slide.SliderTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import pad.stand.com.haidiyun.www.R;


/**
 * a simple slider view, which just show an image. If you want to make your own slider view,
 *
 * just extend BaseSliderView, and implement getView() method.
 */
public class DefaultSliderView extends BaseSliderView{

    public DefaultSliderView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.flip_slide_render_type_default,null);
        ImageView target = (ImageView)v.findViewById(R.id.daimajia_slider_image);
        bindEventAndShow(v, target);
        return v;
    }
}