package per.wsj.lib.controller;

import android.graphics.Bitmap;

import per.wsj.lib.ProcessCallback;


public interface IController {
    void process(int viewWidth);

    Bitmap getTargetBitmap();

    void setProcessCallback(ProcessCallback callback);
}
