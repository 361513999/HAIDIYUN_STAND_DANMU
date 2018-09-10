package pad.stand.com.haidiyun.cy.widget.coverflow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.bumptech.glide.Glide;

import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.P;

/**
 * Cover Flow布局类
 * <p>通过重写LayoutManger布局方法{@link #onLayoutChildren(RecyclerView.Recycler, RecyclerView.State)}
 * 对Item进行布局，并对超出屏幕的Item进行回收
 * <p>通过重写LayoutManger中的{@link #scrollHorizontallyBy(int, RecyclerView.Recycler, RecyclerView.State)}
 * 进行水平滚动处理
 *
 * @version V1.0
 */

public class CoverFlowLayoutManger extends RecyclerView.LayoutManager {

    /**
     * 最大存储item信息存储数量，
     * 超过设置数量，则动态计算来获取
     */
    private final int MAX_RECT_COUNT = 1000;

    /**滑动总偏移量*/
    private int mOffsetAll = 0;

    /**Item宽*/
    private int mDecoratedChildWidth = 0;

    /**Item高*/
    private int mDecoratedChildHeight = 0;

    /**Item间隔与item宽的比例*/
    private float mIntervalRatio = 0.5f;

    /**起始ItemX坐标*/
    private int mStartX = 0;

    /**起始Item Y坐标*/
    private int mStartY = 0;

    /**保存所有的Item的上下左右的偏移量信息*/
    private SparseArray<Rect> mAllItemFrames = new SparseArray<>();

    /**记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收*/
    private SparseBooleanArray mHasAttachedItems = new SparseBooleanArray();

    /**RecyclerView的Item回收器*/
    private RecyclerView.Recycler mRecycle;

    /**RecyclerView的状态器*/
    private RecyclerView.State mState;

    /**滚动动画*/
    private ValueAnimator mAnimation;

    /**正显示在中间的Item*/
    private int mSelectPosition = 0;

    /**前一个正显示在中间的Item*/
    private int mLastSelectPosition = 0;

    /**滑动的方向：左*/
    private static int SCROLL_LEFT = 1;

    /**滑动的方向：右*/
    private static int SCROLL_RIGHT = 2;

    /**
     * 选中监听
     */
    private OnSelected mSelectedListener;

    /**是否为平面滚动，Item之间没有叠加，也没有缩放*/
    private boolean mIsFlatFlow = false;

    /**是否启动Item灰度值渐变*/
    private boolean mItemGradualGrey = false;

    /**是否启动Item半透渐变*/
    private boolean mItemGradualAlpha = false;

    public CoverFlowLayoutManger(boolean isFlat, boolean isGreyItem,
                                 boolean isAlphaItem, float cstInterval) {
        mIsFlatFlow = isFlat;
        mItemGradualGrey = isGreyItem;
        mItemGradualAlpha = isAlphaItem;
        if (cstInterval >= 0) {
            mIntervalRatio = cstInterval;
        } else {
            if (mIsFlatFlow) {
                mIntervalRatio = 1.1f;
            }
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {

        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果没有item，直接返回
        //跳过preLayout，preLayout主要用于支持动画
        if (getItemCount() <= 0 || state.isPreLayout()) {
            mOffsetAll = 0;
            return;
        }
        mAllItemFrames.clear();
        mHasAttachedItems.clear();

        //得到子view的宽和高，这边的item的宽高都是一样的，所以只需要进行一次测量
        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);
        //计算测量布局的宽高
        mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
        mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
        mStartX = Math.round((getHorizontalSpace() - mDecoratedChildWidth) * 1.0f / 2);
        mStartY = Math.round((getVerticalSpace() - mDecoratedChildHeight) *1.0f / 2);

        float offset = mStartX;

        /**只存{@link MAX_RECT_COUNT}个item具体位置*/
        for (int i = 0; i < getItemCount() && i < MAX_RECT_COUNT; i++) {
            Rect frame = mAllItemFrames.get(i);
            if (frame == null) {
                frame = new Rect();
            }
            frame.set(Math.round(offset), mStartY, Math.round(offset + mDecoratedChildWidth), mStartY + mDecoratedChildHeight);
            mAllItemFrames.put(i, frame);
            mHasAttachedItems.put(i, false);
            offset = offset + getIntervalDistance(); //原始位置累加，否则越后面误差越大
        }

        detachAndScrapAttachedViews(recycler); //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        if ((mRecycle == null || mState == null) && //在为初始化前调用smoothScrollToPosition 或者 scrollToPosition,只会记录位置
                mSelectPosition != 0) {                 //所以初始化时需要滚动到对应位置
            mOffsetAll = calculateOffsetForPosition(mSelectPosition);
            onSelectedCallBack();
        }
        P.c("这里001");
        layoutItems(recycler, state, SCROLL_RIGHT);

        mRecycle = recycler;
        mState = state;
    }

    @Override
    public int scrollHorizontallyBy(int dx0, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        int dx = (int) (dx0*0.9);
        if (mAnimation != null && mAnimation.isRunning()) mAnimation.cancel();
        int travel = dx;
        if (dx + mOffsetAll < 0) {
            travel = -mOffsetAll;
        } else if (dx + mOffsetAll > getMaxOffset()){
            travel = (int) (getMaxOffset() - mOffsetAll);
        }
        mOffsetAll += travel; //累计偏移量

        layoutItems(recycler, state, dx > 0 ? SCROLL_RIGHT : SCROLL_LEFT);
        return travel;
    }

    /**
     * 布局Item
     * <p>注意：1，先清除已经超出屏幕的item
     * <p>     2，再绘制可以显示在屏幕里面的item
     */
    private    void layoutItems(RecyclerView.Recycler recycler,
                             RecyclerView.State state, int scrollDirection) {
        if(state==null){
            return;
        }
        if (state.isPreLayout()) {return;}

        Rect displayFrame = new Rect(mOffsetAll, 0, mOffsetAll + getHorizontalSpace(), getVerticalSpace());

        int position = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            position = getPosition(child);

            Rect rect = getFrame(position);
            if (!Rect.intersects(displayFrame, rect)) {//Item没有在显示区域，就说明需要回收
                removeAndRecycleView(child, recycler); //回收滑出屏幕的View
                mHasAttachedItems.delete(position);
            } else { //Item还在显示区域内，更新滑动后Item的位置
                layoutItem(child, rect); //更新Item位置
                mHasAttachedItems.put(position, true);
            }
        }
        if (position == 0) position = mSelectPosition;

        int min = position - 50 >= 0? position - 50 : 0;
        int max = position + 50 < getItemCount() ? position + 50 : getItemCount();

        if(onScrool!=null){
            onScrool.scroolIndex(position);
        }
        for (int i = min; i < max; i++) {
            Rect rect = getFrame(i);
            if (Rect.intersects(displayFrame, rect) &&
                !mHasAttachedItems.get(i)) { //重新加载可见范围内的Item
                View scrap = null;
                try {
                    scrap = recycler.getViewForPosition(i);
                } catch (Exception e) {
                    e.printStackTrace();
                   //此处引起界面卡死，撤销步骤可以达到好转
                   return;
                }
                measureChildWithMargins(scrap, 0, 0);
                if (scrollDirection == SCROLL_LEFT || mIsFlatFlow) { //向左滚动，新增的Item需要添加在最前面
                    addView(scrap, 0);
                } else { //向右滚动，新增的item要添加在最后面
                    addView(scrap);
                }
                layoutItem(scrap, rect); //将这个Item布局出来
                mHasAttachedItems.put(i, true);
            }
        }
    }

    /**
     * 布局Item位置
     * @param child 要布局的Item
     * @param frame 位置信息
     */
    private void layoutItem(View child, Rect frame) {
        layoutDecorated(child,
                frame.left - mOffsetAll,
                frame.top,
                frame.right - mOffsetAll,
                frame.bottom);
        if (!mIsFlatFlow) { //不是平面普通滚动的情况下才进行缩放
            child.setScaleX(computeScale(frame.left - mOffsetAll)); //缩放
            child.setScaleY(computeScale(frame.left - mOffsetAll)); //缩放
        }

        if (mItemGradualAlpha) {
            child.setAlpha(computeAlpha(frame.left - mOffsetAll));
        }

        if (mItemGradualGrey) {
            greyItem(child, frame);
        }
    }

    /**
     * 动态获取Item的位置信息
     * @param index item位置
     * @return item的Rect信息
     */
    private Rect getFrame(int index) {
        Rect frame = mAllItemFrames.get(index);
        if (frame == null) {
            frame = new Rect();
            float offset = mStartX + getIntervalDistance() * index; //原始位置累加（即累计间隔距离）
            frame.set(Math.round(offset), mStartY, Math.round(offset + mDecoratedChildWidth), mStartY + mDecoratedChildHeight);
        }

        return frame;
    }

    /**
     * 变化Item的灰度值
     * @param child 需要设置灰度值的Item
     * @param frame 位置信息
     */
    private void greyItem(View child, Rect frame) {
        float value = computeGreyScale(frame.left - mOffsetAll);
        ColorMatrix cm = new ColorMatrix(new float[]{
                value, 0, 0, 0, 120*(1-value),
                0, value, 0, 0, 120*(1-value),
                0, 0, value, 0, 120*(1-value),
                0, 0, 0, 1, 250*(1-value),
        });
//            cm.setSaturation(0.9f);

        // Create a paint object with color matrix
        Paint greyPaint = new Paint();
        greyPaint.setColorFilter(new ColorMatrixColorFilter(cm));

        // Create a hardware layer with the grey paint
        child.setLayerType(View.LAYER_TYPE_HARDWARE, greyPaint);
        if (value >= 1) {
            // Remove the hardware layer
            child.setLayerType(View.LAYER_TYPE_NONE, null);
        }

    }
    private boolean sIsScrolling = false;
    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);


        switch (newState){
            case RecyclerView.SCROLL_STATE_IDLE:
                //滚动停止时
               // fixOffsetWhenFinishScroll();
                if (sIsScrolling == true) {
                    Glide.with(BaseApplication.application).resumeRequests();
                }
                sIsScrolling = false;
                //关闭了位置修正，直接调用暂停
                onSelectedCallBack();
                if(onScrool!=null){

                    onScrool.scroolStop();
                }

                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                //拖拽滚动时
                sIsScrolling = true;
                Glide.with(BaseApplication.application).pauseRequests();
                if(onScrool!=null){
                    onScrool.scroolStary();
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                //动画滚动时
                sIsScrolling = true;
                Glide.with(BaseApplication.application).pauseRequests();
                if(onScrool!=null){
                    onScrool.scroolStuts();
                }
                break;
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (position < 0 || position > getItemCount() - 1) return;
        mOffsetAll = calculateOffsetForPosition(position);
        if (mRecycle == null || mState == null) {//如果RecyclerView还没初始化完，先记录下要滚动的位置
            mSelectPosition = position;
        } else {
            P.c("这里003");
            layoutItems(mRecycle, mState, position > mSelectPosition ? SCROLL_RIGHT : SCROLL_LEFT);
            onSelectedCallBack();
        }
    }
    public RecyclerView.State getState(){
        return  mState;
    }
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        int finalOffset = calculateOffsetForPosition(position);

        if (mRecycle == null || mState == null) {//如果RecyclerView还没初始化完，先记录下要滚动的位置
            mSelectPosition = position;
        } else {
            startScroll(mOffsetAll, finalOffset);
        }
    }
    public void sl(int position){
        int finalOffset = calculateOffsetForPosition(position);
        startScroll(mOffsetAll, finalOffset);
    }
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }
    public int  get(){
        P.c(mHasAttachedItems.size()+"~~"+mAllItemFrames.size()+"~~"+mOffsetAll);

       return 0;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
        mRecycle = null;
        mState = null;
        mOffsetAll = 0;
        mSelectPosition = 0;
        mLastSelectPosition = 0;
        mHasAttachedItems.clear();
        mAllItemFrames.clear();
    }

    /**
     * 获取整个布局的水平空间大小
     */
    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    /**
     * 获取整个布局的垂直空间大小
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    /**
     * 获取最大偏移量
     */
    private float getMaxOffset() {
        return (getItemCount() - 1) * getIntervalDistance();
    }

    /**
     * 计算Item缩放系数
     * @param x Item的偏移量
     * @return 缩放系数
     */
    private float computeScale(int x) {
        float scale = 1 - Math.abs(x - mStartX) * 1.0f / Math.abs(mStartX + mDecoratedChildWidth / mIntervalRatio);
        if (scale < 0) scale = 0;
        if (scale > 1) scale = 1;
        return scale;
    }

    /**
     * 计算Item的灰度值
     * @param x Item的偏移量
     * @return 灰度系数
     */
    private float computeGreyScale(int x) {
        float itemMidPos = x + mDecoratedChildWidth / 2; //item中点x坐标
        float itemDx2Mid = Math.abs(itemMidPos - getHorizontalSpace() / 2); //item中点距离控件中点距离
        float value = 1 - itemDx2Mid * 1.0f / (getHorizontalSpace() /2);
        if (value < 0.1) value = 0.1f;
        if (value > 1) value = 1;
        value = (float) Math.pow(value,.8);
        return value;
    }

    /**
     * 计算Item半透值
     * @param x Item的偏移量
     * @return 缩放系数
     */
    private float computeAlpha(int x) {
        float alpha = 1 - Math.abs(x - mStartX) * 1.0f / Math.abs(mStartX + mDecoratedChildWidth / mIntervalRatio);
        if (alpha < 0.3f) alpha = 0.3f;
        if (alpha > 1) alpha = 1.0f;
        return alpha;
    }

    /**
     * 计算Item所在的位置偏移
     * @param position 要计算Item位置
     */
    private int calculateOffsetForPosition(int position) {
        return Math.round(getIntervalDistance() * position);
    }

    /**
     * 修正停止滚动后，Item滚动到中间位置
     */
    private void fixOffsetWhenFinishScroll() {
        int scrollN = (int) (mOffsetAll * 1.0f / getIntervalDistance());
        float moreDx = (mOffsetAll % getIntervalDistance());
        if (moreDx > (getIntervalDistance() * 0.5)) {
            scrollN ++;
        }
        int finalOffset = (int) (scrollN * getIntervalDistance());
        startScroll(mOffsetAll, finalOffset);
        mSelectPosition = Math.round (finalOffset * 1.0f / getIntervalDistance());
    }

    /**
     * 滚动到指定X轴位置
     * @param from X轴方向起始点的偏移量
     * @param to X轴方向终点的偏移量
     */
    private void startScroll(int from, int to) {
        if (mAnimation != null && mAnimation.isRunning()) {
            mAnimation.cancel();
        }
        P.c("滚动动画");

        final int direction = from < to ? SCROLL_RIGHT : SCROLL_LEFT;
        mAnimation = ValueAnimator.ofFloat(from, to);
        mAnimation.setDuration(200);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetAll = Math.round((float) animation.getAnimatedValue());

                layoutItems(mRecycle, mState, direction);

            }
        });
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                onSelectedCallBack();

                if(onScrool!=null){

                    onScrool.scroolStop();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimation.start();
    }

    /**
     * 获取Item间隔
     */
    private float getIntervalDistance() {
        return mDecoratedChildWidth * mIntervalRatio;
    }

    /**
     * 计算当前选中位置，并回调
     */
    private void onSelectedCallBack() {
        mSelectPosition = Math.round (mOffsetAll / getIntervalDistance());
        if (mSelectedListener != null && mSelectPosition != mLastSelectPosition) {
            mSelectedListener.onItemSelected(mSelectPosition);
        }
        mLastSelectPosition = mSelectPosition;
    }

    /**
     * 获取第一个可见的Item位置
     * <p>Note:该Item为绘制在可见区域的第一个Item，有可能被第二个Item遮挡
     */
    public int getFirstVisiblePosition() {
        Rect displayFrame = new Rect(mOffsetAll, 0, mOffsetAll + getHorizontalSpace(), getVerticalSpace());
        int cur = getCenterPosition();
        for (int i = cur - 1; i >= 0; i--) {
            Rect rect = getFrame(i);
            if (!Rect.intersects(displayFrame, rect)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * 获取最后一个可见的Item位置
     * <p>Note:该Item为绘制在可见区域的最后一个Item，有可能被倒数第二个Item遮挡
     */
    public int getLastVisiblePosition() {
        Rect displayFrame = new Rect(mOffsetAll, 0, mOffsetAll + getHorizontalSpace(), getVerticalSpace());
        int cur = getCenterPosition();
        for (int i = cur + 1; i < getItemCount(); i++) {
            Rect rect = getFrame(i);
            if (!Rect.intersects(displayFrame, rect)) {
                return i - 1;
            }
        }
        return cur;
    }

    /**
     * 获取可见范围内最大的显示Item个数
     */
    public int getMaxVisibleCount() {
        int oneSide = (int) ((getHorizontalSpace() - mStartX) / (getIntervalDistance()));
        return oneSide * 2 + 1;
    }

    /**
     * 获取中间位置
     * <p>Note:该方法主要用于{@link RecyclerCoverFlow#getChildDrawingOrder(int, int)}判断中间位置
     * <p>如果需要获取被选中的Item位置，调用{@link #getSelectedPos()}
     */
    public int getCenterPosition() {
        int pos = (int) (mOffsetAll / getIntervalDistance());
        int more = (int) (mOffsetAll % getIntervalDistance());
        if (more > getIntervalDistance() * 0.5f) pos++;
        return pos;
    }

    /**
     * 设置选中监听
     * @param l 监听接口
     */
    public void setOnSelectedListener(OnSelected l) {
        mSelectedListener = l;
    }

    /**
     * 获取被选中Item位置
     */
    public int getSelectedPos() {
        return mSelectPosition;
    }

    /**
     * 选中监听接口
     */
    public interface OnSelected {
        /**
         * 监听选中回调
         * @param position 显示在中间的Item的位置
         */
        void onItemSelected(int position);
    }
    public interface  OnScrool{
        void scroolStop();
        void scroolStuts();
        void scroolStary();
        void scroolIndex(int position);
    }
    private   OnScrool onScrool;
    public synchronized void setOnScrool(OnScrool scrool){
        this.onScrool = scrool;
    }

    public static class Builder {
        boolean isFlat = false;
        boolean isGreyItem = false;
        boolean isAlphaItem = false;
        float cstIntervalRatio = -1f;

        public Builder setFlat(boolean flat) {
            isFlat = flat;
            return this;
        }

        public Builder setGreyItem(boolean greyItem) {
            isGreyItem = greyItem;
            return this;
        }

        public Builder setAlphaItem(boolean alphaItem) {
            isAlphaItem = alphaItem;
            return this;
        }

        public Builder setIntervalRatio(float ratio) {
            cstIntervalRatio = ratio;
            return this;
        }

        public CoverFlowLayoutManger build() {
            return new CoverFlowLayoutManger(isFlat, isGreyItem,
                    isAlphaItem, cstIntervalRatio);
        }
    }
}