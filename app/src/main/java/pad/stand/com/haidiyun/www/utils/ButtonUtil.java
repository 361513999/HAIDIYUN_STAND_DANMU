package pad.stand.com.haidiyun.www.utils;

public class ButtonUtil {
    private static long lastClickTime = System.currentTimeMillis();

    public static synchronized boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 600) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static synchronized void updateLastTime() {
        lastClickTime = System.currentTimeMillis();
    }
}