package per.wsj.lib.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import per.wsj.lib.utils.ThreadPool;


/**
 * resource图片加载控制器
 */

public class ResImageController extends BaseImageController {

    private Context mContext;

    private int drawableResId;

    public ResImageController(Context context, int resId) {
        mContext = context;
        this.drawableResId = resId;
    }

    @Override
    protected void loadImage(final int viewWidth) {
        if (drawableResId == 0) {
            return;
        }
        ThreadPool.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadResImage(mContext, drawableResId, viewWidth);
            }
        });
    }

    /**
     * 加载resource图片
     * @param mContext
     * @param resId
     * @param viewWidth
     */
    private void loadResImage(Context mContext, int resId, int viewWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Resources resources = mContext.getResources();
        BitmapFactory.decodeResource(resources, resId, options);

        int outWidthPx = options.outWidth;
        int outHeightPx = options.outHeight;

        float scale = 1.0f * viewWidth / outWidthPx;
        int scaleWidth = (int) (scale * outWidthPx);
        int scaleHeight = (int) (scale * outHeightPx);

        options.inSampleSize = calculateInSampleSize(outWidthPx, outHeightPx, scaleWidth, scaleHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = BitmapFactory.decodeResource(resources, resId, options);
        targetBitmap = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
        bmp = null;
        callback.onProcessFinished(scaleWidth, scaleHeight);

        isProcessing = false;
    }
}
