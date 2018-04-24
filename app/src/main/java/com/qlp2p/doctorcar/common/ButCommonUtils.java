package com.qlp2p.doctorcar.common;
//防止按钮重复点击
public class ButCommonUtils {
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 900) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
