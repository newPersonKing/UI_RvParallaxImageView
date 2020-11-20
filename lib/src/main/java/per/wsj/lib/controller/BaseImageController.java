package per.wsj.lib.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import per.wsj.lib.ProcessCallback;
import per.wsj.lib.utils.Logger;

/**
 * 图片资源控制器：1,加载本地/res/url.  2,对图片进行缩放操作,生成bitmap
 * 重写loadImage()来实现自己加载的逻辑
 */
public abstract class BaseImageController implements IController {

    protected volatile boolean isProcessing;

    protected ProcessCallback callback;

    protected Bitmap targetBitmap;

    @Override
    public void process(final int viewWidth) {
        if (isProcessing) {
            return;
        }
        isProcessing = true;

        loadImage(viewWidth);
    }

    /**
     * 计算重采样倍数
     *
     * @param sourceWidth
     * @param sourceHeight
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    protected int calculateInSampleSize(int sourceWidth, int sourceHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (sourceWidth > reqWidth || sourceHeight > reqHeight) {
            int halfWidth = sourceWidth >> 2;
            int halfHeight = sourceHeight >> 2;
            while ((halfWidth / inSampleSize > reqWidth)
                    && (halfHeight / inSampleSize > reqHeight)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    public void setProcessCallback(ProcessCallback listener) {
        this.callback = listener;
    }

    @Override
    public Bitmap getTargetBitmap() {
        return targetBitmap;
    }

    /**
     * @param viewWidth
     */
    protected abstract void loadImage(int viewWidth);

    /**
     * 处理drawable
     * @param viewWidth
     * @param resource
     */
    protected void handleDrawable(int viewWidth, Drawable resource) {
        // 1,get the width/height of resource
        int originWidth = resource.getIntrinsicWidth();
        int originHeight = resource.getIntrinsicHeight();
        // 2,calc the scale factor
        float scale = 1.0f * viewWidth / originWidth;
        // 3,calc the target width/height
        int scaleWidth = (int) (scale * originWidth);
        int scaleHeight = (int) (scale * originHeight);
        // 4,drawable to bitmap
        Bitmap bmp = drawable2Bitmap(resource);
        if (bmp == null) {
            return;
        }
        // 5,scaled bitmap
        targetBitmap = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
        bmp = null;

        // 6,callback
        callback.onProcessFinished(scaleWidth, scaleHeight);
        // 7,reset flag
        isProcessing = false;
    }

    /**
     * 处理bitmap
     * @param viewWidth
     * @param bitmap
     */
    protected void handleBitmap(int viewWidth, Bitmap bitmap) {
        // 1,get the width/height of resource
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();
        Logger.LOGE("originWidth:" + originWidth);
        Logger.LOGE("originHeight:" + originHeight);
        // 2,calc the scale factor
        float scale = 1.0f * viewWidth / originWidth;
        // 3,calc the target width/height
        int scaleWidth = (int) (scale * originWidth);
        int scaleHeight = (int) (scale * originHeight);

        // 4,scaled bitmap
        targetBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
        bitmap = null;

        // 5,callback
        callback.onProcessFinished(scaleWidth, scaleHeight);
        // 6,reset flag
        isProcessing = false;
    }

    /**
     * drawable转bitmap
     * @param drawable
     * @return
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
    }
}
