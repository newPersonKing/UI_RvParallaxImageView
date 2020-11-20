package per.wsj.lib.utils;

import android.util.Log;

import java.util.Locale;

public class Logger {
    private static boolean DEBUG = true;
    public static final String TAG = "wsjLib";

    public static void LOGI(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, buildMessage(msg));
        }
    }

    public static void LOGD(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, buildMessage(msg));
        }
    }

    public static void LOGE(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, buildMessage(msg));
        }
    }

    public static void LOGI(String msg) {
        if (DEBUG) {
            Log.i(TAG, buildMessage(msg));
        }
    }

    public static void LOGD(String msg) {
        if (DEBUG) {
            Log.d(TAG, buildMessage(msg));
        }
    }

    public static void LOGE(String msg) {
        if (DEBUG) {
            Log.e(TAG, buildMessage(msg));
        }
    }

    /**
     * 构建信息，用于创建自定义的Log信息结构
     * @param msg Log信息
     * @return 整个创建好的信息内容
     */
    private static String buildMessage(String msg) {
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();

        return String.format(Locale.US, "%s -->%s",
                "(" + targetStackTraceElement.getFileName() + ":"
                        + targetStackTraceElement.getLineNumber() + ")", msg);
    }

    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(Logger.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }

}
