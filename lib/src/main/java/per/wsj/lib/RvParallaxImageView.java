package per.wsj.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import per.wsj.lib.controller.IController;
import per.wsj.lib.utils.Logger;


/**
 *
 */

public class RvParallaxImageView extends View {

    /**
     * 图片往上最大的偏移量
     */
    private float maxUpOffscreen;

    /**
     * 图片往上的偏移量
     */
    private float topOffscreen;

    /**
     * view 相对当前window的位置
     */
    private int[] viewLocation = new int[2];

    /**
     * view宽高
     */
    private int viewWidth, viewHeight;

    /**
     * 是否已经测量过
     */
    private boolean isMeasured;

    /**
     * Control是否完成了缩放处理
     */
    private boolean isScaled;

    /**
     * 图片资源控制器：1,加载本地/res/url.  2,对图片进行缩放操作,生成bitmap
     */
    private IController mImageController;

    /**
     * 绑定的recyclerview
     */
    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener rvScrollListener;

    /**
     * 缩放因子
     */
    private float scaleFactor = 1.0f;

    /**
     * recyclerview位置
     */
    private int[] rvLocation = new int[2];

    /**
     * recyclerview高度
     */
    private int rvHeight;

    public RvParallaxImageView(Context context) {
        this(context, null);
    }

    public RvParallaxImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RvParallaxImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setController(IController controller) {
        Logger.LOGE("setController");
        mImageController = controller;
        mImageController.setProcessCallback(new ProcessCallback() {
            @Override
            public void onProcessFinished(int width, int height) {
                Logger.LOGE("正常情况下只会走一次processListener:" + height);
                isScaled = true;
                resetScaleFactor(height);
                getLocationInWindow(viewLocation);
                topOffscreen = -(viewLocation[1] - rvLocation[1]) * scaleFactor;
                bindTopOrBottom();
                if (viewLocation[1] == 0) {// view还未显示出来就已经执行了，因此位置计算异常不用刷新
                    return;
                }
                // 当前是非ui线程
                postInvalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        viewHeight = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);

        isMeasured = true;
        Logger.LOGE("onMeasure");
        mImageController.process(viewWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = mImageController.getTargetBitmap();
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        canvas.drawBitmap(bitmap, 0, topOffscreen, null);
    }

    /**
     * 让图片在顶部和底部时随着rv移动
     */
    private void bindTopOrBottom() {
        if (topOffscreen > 0) {
            topOffscreen = 0;
        }
        if (topOffscreen < -maxUpOffscreen) {
            topOffscreen = -maxUpOffscreen;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Logger.LOGE("onAttachedToWindow");
        if (isMeasured) {
            mImageController.process(viewWidth);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isScaled = false;
    }

    /**
     * 绑定rv
     *
     * @param recyclerView
     */
    public void bindRecyclerView(RecyclerView recyclerView) {
        if (recyclerView == null || recyclerView.equals(this.recyclerView)) {
            return;
        }
        unbindRecyclerView();
        this.recyclerView = recyclerView;
        rvLocation = new int[2];
        rvHeight = recyclerView.getLayoutManager().getHeight();
        recyclerView.getLocationInWindow(rvLocation);
        recyclerView.addOnScrollListener(rvScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topDistance = getTopDistance();
//                LogUtil.LOGE("onScrolled-----topDistance:" + topDistance);
                if (topDistance > 0 && topDistance + viewHeight < rvHeight) {
                    topOffscreen += dy * scaleFactor;
                    bindTopOrBottom();
                    if (isMeasured) {
                        invalidate();
                    }
                } else if (topDistance + viewHeight >= rvHeight) {
                    // view还未显示出来就执行process回调，因此会出现view在底部图片置顶的情况
                    if (topOffscreen == 0) {
                        if (isScaled) {
                            getLocationOnScreen(viewLocation);
                            topOffscreen = -(viewLocation[1] - rvLocation[1]) * scaleFactor;
                            bindTopOrBottom();
                            // 当前是非ui线程
                            invalidate();
                        }
                    }
                }
            }
        });
    }

    /**
     * 取消绑定
     */
    public void unbindRecyclerView() {
        if (recyclerView != null) {
            if (rvScrollListener != null) {
                recyclerView.removeOnScrollListener(rvScrollListener);
            }
            recyclerView = null;
        }
    }

    /**
     * 计算高度的缩放因子
     *
     * @param scaledHeight
     */
    private void resetScaleFactor(int scaledHeight) {
        if (recyclerView != null) {
            maxUpOffscreen = scaledHeight - viewHeight;
            scaleFactor = 1.0f * maxUpOffscreen / (rvHeight - viewHeight);
            Logger.LOGD("resetScaleFactor : " + scaleFactor);
        }
    }

    /**
     * 计算当前view到RecyclerView顶部的距离
     *
     * @return
     */
    private int getTopDistance() {
        getLocationInWindow(viewLocation);
        return viewLocation[1] - rvLocation[1];
    }
}
