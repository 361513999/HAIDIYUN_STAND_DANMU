package pad.stand.com.haidiyun.www.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.stand.com.haidiyun.www.bean.ImageBDInfo;
import pad.stand.com.haidiyun.www.bean.ImageInfo;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.widget.HackyViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

public class ShowImages extends BaseActivity implements OnPageChangeListener {
    private ViewPager bi_viewpager;
    private LinearLayout content;
    protected ImageView showimg;
    private ArrayList<ImageInfo> imageInfos;
    private final Spring mSpring = SpringSystem.create().createSpring()
            .addListener(new ExampleSpringListener());
    private SamplePagerAdapter pagerAdapter;
    private int screenWidth;
    private int screenHeight;
    private TextView textv;

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(8);
        // setTranslucentStatus(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_images_activity);
        imageInfos = new ArrayList<ImageInfo>();
        WindowManager wm = this.getWindowManager();

        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }

    @Override
    protected void onStart() {

        super.onStart();
        content = (LinearLayout) findViewById(R.id.content);
        bi_viewpager = (HackyViewPager) findViewById(R.id.bi_viewpager);
        textv = (TextView) findViewById(R.id.textv);
        par = (FrameLayout) findViewById(R.id.par);
        bi_viewpager.setOnPageChangeListener(this);
        index = getIntent().getIntExtra("index", 0);
        String txtString = getIntent().getStringExtra("txt");
        if (txtString.length() > 0) {
            textv.setText(txtString);
        } else {
            textv.setVisibility(View.GONE);
        }

        showLine = getIntent().getIntExtra("showLine", 0);
        type = getIntent().getIntExtra("type", 0);
        imageInfos = (ArrayList<ImageInfo>) getIntent().getSerializableExtra(
                "data");
        P.c(index + "==" + type + "==" + imageInfos.size());
        // imageInfo = imageInfos.get(index);
        imageInfo = imageInfos.get(0);
        bdInfo = (ImageBDInfo) getIntent().getSerializableExtra("bdinfo");
        pagerAdapter = new SamplePagerAdapter();
        bi_viewpager.setAdapter(pagerAdapter);
        bi_viewpager.setCurrentItem(0);
        if (type == 2) {
            moveheight = (screenWidth - showLine * dip2px(2)) / showLine;
        }
        getValue();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {

        final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(loadedImage);
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    class SamplePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageInfos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            String path = "file://" + Common.ZIP + imageInfos.get(position).url;
            /*
			 * Glide.with(BaseApplication.application) .load("file://" +
			 * Common.ZIP + imageInfo.url).asBitmap()
			 * .diskCacheStrategy(DiskCacheStrategy.RESULT) .into(photoView);
			 */

            ImageLoader.getInstance().displayImage(path, photoView, null,
                    animateFirstListener);
            // Now just add PhotoView to ViewPager and return it
            photoView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View arg0, float arg1, float arg2) {
                    par.setVisibility(View.GONE);
                    showimg.setVisibility(View.VISIBLE);
                    setShowimage();
                    // AppManager.getAppManager().finishActivity(ShowImages.this);
                }
            });
            container.addView(photoView, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);


            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    // 定义动画

    private float img_w;
    private float img_h;
    protected ImageBDInfo bdInfo;
    protected ImageInfo imageInfo;
    // private float size, size_h;
    private float y_img_h;
    private FrameLayout par;

    protected void getValue() {
        showimg = new ImageView(this);
        showimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        P.c("--" + imageInfo.url);
        // Glide.with(BaseApplication.application)
        // .load("file://" + Common.ZIP + imageInfo.url).asBitmap()
        // .into(showimg);

        ImageLoader.getInstance().displayImage(
                "file://" + Common.ZIP + imageInfo.url,
                showimg, options
        );

        float pa = imageInfo.height / imageInfo.width;
        img_w = bdInfo.width;
        img_h = pa * img_w;
        P.c("pa" + pa + "bdInfo.width" + bdInfo.width + "\nbdInfo.height"
                + bdInfo.height + "\nimg_w" + img_w + "\nimg_h" + img_h);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                (int) img_w, (int) img_h);
        showimg.setLayoutParams(p);

        p.setMargins(bdInfo.x, bdInfo.y, 0, 0);
        content.addView(showimg);

        showimg.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                setShowimage();
            }
        }, 300);

    }

    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(0).showImageOnFail(0)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
    private float tx;
    private float ty;

    protected void setShowimage() {
        if (mSpring.getEndValue() == 0) {
            mSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(
                    170, 5));
            tx = screenWidth / 2 - (bdInfo.x + img_w / 2);
            ty = screenHeight / 2 - (bdInfo.y + img_h / 2);
            MoveView();
            P.c("========1");
            return;
        }
        P.c("========");
        mSpring.setSpringConfig(SpringConfig
                .fromOrigamiTensionAndFriction(1, 5));
        mSpring.setEndValue(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // execute the task
                MoveBackView();
            }
        }, 300);

    }

    protected void EndSoring() {
        par.setVisibility(View.VISIBLE);
        showimg.setVisibility(View.GONE);

    }

    protected void EndMove() {
        AppManager.getAppManager().finishActivity(ShowImages.this);
    }

    private class ExampleSpringListener implements SpringListener {

        @Override
        public void onSpringUpdate(Spring spring) {
            float xa = 0;
            float ya = 0;
            // float hmax = screenHeight/imageInfo.height;//缩放比例
            // 真实缩放宽度
            // 真实长宽比
            float ck = imageInfo.width / imageInfo.height;
            float xk = imageInfo.height / imageInfo.width;
            // if(screenHeight>imageInfo.height){
            xa = (screenHeight * ck) / img_w;
//			if(showimg.getHeight()!=img_h){
//				ya = screenHeight/showimg.getHeight();
//			}else{
            ya = screenHeight / img_h;
//			}
            P.c(img_h + "img_h" + showimg.getHeight());

            // P.c("wg"+wg+"img_h"+img_h+"imageInfo.width"+imageInfo.width+"imageInfo.height"+imageInfo.height+"ck"+ck+"ya"+ya+"xa"+xa);
            // P.c(screenHeight+"(imageInfo.width+(imageInfo.width*hmin))"+(imageInfo.width+(imageInfo.width*hmin)));
            // P.c("(imageInfo.height+(imageInfo.height*hmin))"+(imageInfo.height+(imageInfo.height*hmin)));
            // }else{
            // xa = (imageInfo.width-(imageInfo.width*hmin))/wg;
            // ya = screenHeight/img_h;
            // }
            double CurrentValue = spring.getCurrentValue();
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(
                    CurrentValue, 0, 1, 1, xa);
            float mapy = (float) SpringUtil.mapValueFromRangeToRange(
                    CurrentValue, 0, 1, 1, ya);
            showimg.setScaleX(mappedValue);
            showimg.setScaleY(mapy);
            if (CurrentValue == 1) {

                EndSoring();
            }
        }

        @Override
        public void onSpringAtRest(Spring spring) {

        }

        @Override
        public void onSpringActivate(Spring spring) {

        }

        @Override
        public void onSpringEndStateChange(Spring spring) {

        }
    }

    private void MoveView() {

        ObjectAnimator.ofFloat(content, "alpha", 0.8f).setDuration(100).start();
        content.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(showimg, "translationX", tx)
                        .setDuration(200),
                ObjectAnimator.ofFloat(showimg, "translationY", ty)
                        .setDuration(200),
                ObjectAnimator.ofFloat(content, "alpha", 1).setDuration(100)

        );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mSpring.setEndValue(1);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();

    }

    protected float to_x = 0;
    protected float to_y = 0;

    private void MoveBackView() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(showimg, "translationX", to_x)
                        .setDuration(100),
                ObjectAnimator.ofFloat(showimg, "translationY", to_y)
                        .setDuration(100));
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                showimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                EndMove();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {


    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {


    }

    private int index = 0;
    private int showLine = 0;
    private int type;
    private float moveheight;

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onPageSelected(int arg0) {


        if (showimg == null) {
            return;
        }
        ImageInfo info = imageInfos.get(arg0);
        ImageLoader.getInstance().displayImage(info.url, showimg, options,
                animateFirstListener);
//		Glide.with(BaseApplication.application).load(info.url).asBitmap()
//				.diskCacheStrategy(DiskCacheStrategy.RESULT).into(showimg);


        if (type == 2) {
            int a = index / showLine;
            int b = index % showLine;
            int a1 = index / showLine;
            int b1 = index % showLine;
            to_y = (a1 - a) * moveheight + (a1 - a) * dip2px(2);
            to_x = (b1 - b) * moveheight + (b1 - b) * dip2px(2);
        }

    }
}
